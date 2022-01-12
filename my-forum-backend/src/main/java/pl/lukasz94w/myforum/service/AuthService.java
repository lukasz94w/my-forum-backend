package pl.lukasz94w.myforum.service;

import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.exception.RefreshTokenException;
import pl.lukasz94w.myforum.model.*;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;
import pl.lukasz94w.myforum.repository.ActivateTokenRepository;
import pl.lukasz94w.myforum.repository.PasswordTokenRepository;
import pl.lukasz94w.myforum.repository.RoleRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.*;
import pl.lukasz94w.myforum.response.JwtResponse;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.response.RefreshTokenResponse;
import pl.lukasz94w.myforum.security.token.JwtUtils;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;
import pl.lukasz94w.myforum.service.util.MailServiceUtil;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final PasswordTokenRepository passwordTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final ActivateTokenRepository activateTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${pl.lukasz94w.serverAddress}")
    private String serverUrl;

    public AuthService(AuthenticationManager authenticationManager, RoleRepository roleRepository,
                       UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                       PasswordTokenRepository passwordTokenRepository, MailService mailService,
                       PasswordEncoder passwordEncoder, ActivateTokenRepository activateTokenRepository,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.passwordTokenRepository = passwordTokenRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.activateTokenRepository = activateTokenRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseEntity<MessageResponse> signUp(SignupRequest signUpRequest) {
        if (userRepository.existsByName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already taken"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use"));
        }

        User user = new User();
        user.setName(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER);
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);

        String token = RandomString.make(30);
        String confirmLink = MailServiceUtil.constructConfirmLink(token, serverUrl);
        try {
            mailService.sendActivateAccountEmail(signUpRequest.getEmail(), confirmLink); // it can also be done using @Async or by publishing event
            activateTokenRepository.save(new ActivateToken(user, token));
        } catch (MessagingException | UnsupportedEncodingException | MailSendException exception) {
            logger.error(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new MessageResponse("Cannot send verification email. Try again later"));
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully, check email for verification link"));
    }

    public ResponseEntity<?> signIn(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (DisabledException exception) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        } catch (BadCredentialsException exception) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtAccessToken = jwtUtils.generateJwtAccessToken(userDetails);
        int expirationTimeInSeconds = jwtUtils.getExpirationTimeInSeconds();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken jwtRefreshToken = refreshTokenService.createJwtRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(
                jwtAccessToken,
                jwtRefreshToken.getToken(),
                expirationTimeInSeconds,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.isEnabled(),
                roles));
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        logger.warn("Access token expired, asking for new one");
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtUtils.generateTokenFromUserName(user.getName());
                    return ResponseEntity
                            .ok(new RefreshTokenResponse(newAccessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));
    }

    public void sendEmailWithResetToken(SendResetEmailRequest sendResetEmailRequest) {
        String userEmail = sendResetEmailRequest.getEmail();
        try {
            User userFoundedByEmail = userRepository.findByEmail(sendResetEmailRequest.getEmail()).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with email: " + sendResetEmailRequest.getEmail()));

            String token = RandomString.make(30);
            String resetPasswordLink = MailServiceUtil.constructResetPasswordLink(token, serverUrl);

            PasswordToken passwordToken = passwordTokenRepository.findByUser(userFoundedByEmail);
            if (passwordToken == null) {
                passwordTokenRepository.save(new PasswordToken(userFoundedByEmail, token));
            } else {
                // replace old token (probably expired) with the new one
                passwordToken.setNewToken(token);
                passwordToken.setNewExpirationDateOfToken();
                passwordTokenRepository.save(passwordToken);
            }

            mailService.sendResetPasswordEmail(userEmail, resetPasswordLink);
        } catch (UsernameNotFoundException | MessagingException | UnsupportedEncodingException exception) {
            // application doesn't return result if the user with
            // such email exist or if the email was successfully sent,
            // so we log this event for information purposes
            logger.error(exception.getMessage());
        }
    }

    public ResponseEntity<MessageResponse> changePasswordThroughEmail(ChangePasswordThroughEmail changePasswordThroughEmail) {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(changePasswordThroughEmail.getReceivedToken());
        if (passwordToken == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Token not found"));
        }
        if (passwordToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(new MessageResponse("Token is expired"));
        }

        User user = userRepository.findByName(passwordToken.getUser().getName());
        user.setPassword(passwordEncoder.encode(changePasswordThroughEmail.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Password changed successfully"));
    }

    public ResponseEntity<Void> activateAccount(String activationToken) {
        ActivateToken activateToken = activateTokenRepository.findByToken(activationToken);

        if (activateToken == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = activateToken.getUser();
        if (user.isEnabled()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (activateToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(HttpStatus.GONE);
        }

        user.setEnabled(true);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> resendActivationToken(String oldExpiredToken) {
        ActivateToken activateToken = activateTokenRepository.findByToken(oldExpiredToken);

        if (activateToken == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String newToken = RandomString.make(30);
        activateToken.setNewToken(newToken);
        activateToken.setNewExpirationDateOfToken();
        String confirmLink = MailServiceUtil.constructConfirmLink(newToken, serverUrl);
        try {
            mailService.sendActivateAccountEmail(activateToken.getUser().getEmail(), confirmLink);
            activateTokenRepository.save(activateToken);
        } catch (MessagingException | UnsupportedEncodingException | MailSendException exception) {
            logger.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
