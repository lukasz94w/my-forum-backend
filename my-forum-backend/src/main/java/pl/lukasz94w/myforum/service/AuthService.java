package pl.lukasz94w.myforum.service;

import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.PasswordToken;
import pl.lukasz94w.myforum.model.Role;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;
import pl.lukasz94w.myforum.repository.PasswordTokenRepository;
import pl.lukasz94w.myforum.repository.RoleRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.ChangePasswordThroughEmail;
import pl.lukasz94w.myforum.request.LoginRequest;
import pl.lukasz94w.myforum.request.SendResetEmailRequest;
import pl.lukasz94w.myforum.request.SignupRequest;
import pl.lukasz94w.myforum.response.JwtResponse;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.security.token.JwtUtils;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;
import pl.lukasz94w.myforum.service.util.MailUtil;

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

    @Value("${pl.lukasz94w.serverAddress}")
    private String serverUrl;

    public AuthService(AuthenticationManager authenticationManager, RoleRepository roleRepository,
                       UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                       PasswordTokenRepository passwordTokenRepository, MailService mailService,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.passwordTokenRepository = passwordTokenRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<MessageResponse> signUp(SignupRequest signUpRequest) {
        if (userRepository.existsByName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setName(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<JwtResponse> signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        int expirationTimeInSeconds = jwtUtils.getExpirationTimeInSeconds();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                expirationTimeInSeconds,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public void sendEmailWithResetToken(SendResetEmailRequest sendResetEmailRequest) {
        String userEmail = sendResetEmailRequest.getEmail();
        try {
            User userFoundedByEmail = userRepository.findByEmail(sendResetEmailRequest.getEmail()).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with email: " + sendResetEmailRequest.getEmail()));
            String token = RandomString.make(30);
            String resetPasswordLink = MailUtil.constructResetPasswordLink(token, serverUrl);
            this.passwordTokenRepository.save(new PasswordToken(userFoundedByEmail, token));
            this.mailService.sendMail(userEmail, resetPasswordLink);
        } catch (UsernameNotFoundException exception) {
            //application doesn't return result if the user with
            //such email exist, so we log this event for information purposes
            logger.error(exception.getMessage());
        } catch (MessagingException | UnsupportedEncodingException exception) {
            logger.error(exception.getMessage());
        }
    }

    public ResponseEntity<MessageResponse> changePasswordThroughEmail(ChangePasswordThroughEmail changePasswordThroughEmail) {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(changePasswordThroughEmail.getReceivedToken());
        if (passwordToken == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
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
}
