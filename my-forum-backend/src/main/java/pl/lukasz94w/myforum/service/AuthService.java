package pl.lukasz94w.myforum.service;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.exception.reason.AccountActivationNotPossibleReason;
import pl.lukasz94w.myforum.exception.reason.ChangePasswordViaEmailLinkExceptionReason;
import pl.lukasz94w.myforum.exception.exception.*;
import pl.lukasz94w.myforum.exception.reason.SignInNotPossibleReason;
import pl.lukasz94w.myforum.exception.reason.SignUpExceptionReason;
import pl.lukasz94w.myforum.model.ActivateToken;
import pl.lukasz94w.myforum.model.PasswordToken;
import pl.lukasz94w.myforum.model.Role;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;
import pl.lukasz94w.myforum.repository.ActivateTokenRepository;
import pl.lukasz94w.myforum.repository.PasswordTokenRepository;
import pl.lukasz94w.myforum.repository.RoleRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.*;
import pl.lukasz94w.myforum.response.message.SuccessResponse;
import pl.lukasz94w.myforum.security.token.JwtUtils;
import pl.lukasz94w.myforum.service.util.MailServiceUtil;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Value("${pl.lukasz94w.serverAddress}")
    private String serverUrl;

    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final PasswordTokenRepository passwordTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final ActivateTokenRepository activateTokenRepository;
    private final MailServiceUtil mailServiceUtil;

    public SuccessResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByName(signUpRequest.getUsername())) {
            throw new SignUpException(SignUpExceptionReason.USERNAME_IS_TAKEN);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new SignUpException(SignUpExceptionReason.EMAIL_IS_TAKEN);
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
        String confirmLink = mailServiceUtil.constructConfirmLink(token, serverUrl);
        try {
            mailService.sendActivateAccountEmail(signUpRequest.getEmail(), confirmLink); // it can also be done using @Async or by publishing event
        } catch (MessagingException | UnsupportedEncodingException | MailSendException exception) {
            logger.error(exception.getMessage());
            throw new SignUpException(SignUpExceptionReason.SENDING_MAIL_FAILED);
        }

        activateTokenRepository.save(new ActivateToken(user, token));
        return new SuccessResponse("Registration successful! Check email for confirmation link");
    }

    public Map<String, String> signIn(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        } catch (DisabledException exception) {
            throw new SignInException(SignInNotPossibleReason.ACCOUNT_NOT_ACTIVATED);
        } catch (LockedException exception) {
            Map<String, Object> bannedUserData = new HashMap<>();
            bannedUserData.put("userName", signInRequest.getUsername());
            bannedUserData.put("dateOfBan", userRepository.findByName(signInRequest.getUsername()).getBan().getDateAndTimeOfBan().atZone(ZoneId.systemDefault()).toEpochSecond());
            throw new SignInException(SignInNotPossibleReason.USER_IS_BANNED, bannedUserData);
        } catch (BadCredentialsException exception) {
            throw new SignInException(SignInNotPossibleReason.BAD_CREDENTIALS);
        }

        User signedInUser = userRepository.findByName(signInRequest.getUsername());

        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("accessToken", jwtUtils.generateJwtAccessToken(signedInUser.getName(), signedInUser.isAdmin()));
        tokens.put("refreshToken", jwtUtils.generateJwtRefreshToken(signedInUser.getName()));

        return tokens;
    }

    public Map<String, String> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (jwtUtils.validateJwtToken(refreshToken) && jwtUtils.getUserNameFromJwtToken(refreshToken) != null) {
            String userNameFromToken = jwtUtils.getUserNameFromJwtToken(refreshToken);
            String newAccessToken = jwtUtils.generateJwtAccessToken(userNameFromToken, null);
            return Collections.singletonMap("accessToken", newAccessToken);
        }

        throw new RefreshTokenException("Can't send new access token. Probably refresh token is expired");
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String userEmail = resetPasswordRequest.getEmail();
        try {
            User userFoundedByEmail = userRepository.findByEmail(resetPasswordRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + resetPasswordRequest.getEmail()));

            String token = RandomString.make(30);
            String resetPasswordLink = mailServiceUtil.constructResetPasswordLink(token, serverUrl);

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
            // so app logs this event for information purposes
            logger.error(exception.getMessage());
        }
    }

    public SuccessResponse changePassword(ChangePasswordViaEmailLink changePasswordViaEmailLink) {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(changePasswordViaEmailLink.getReceivedToken());

        if (passwordToken == null) {
            throw new ChangePasswordViaEmailLinkException(ChangePasswordViaEmailLinkExceptionReason.TOKEN_NOT_FOUND);
        }

        if (passwordToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ChangePasswordViaEmailLinkException(ChangePasswordViaEmailLinkExceptionReason.TOKEN_EXPIRED);
        }

        User user = userRepository.findByName(passwordToken.getUser().getName());
        user.setPassword(passwordEncoder.encode(changePasswordViaEmailLink.getNewPassword()));
        userRepository.save(user);
        passwordTokenRepository.delete(passwordToken);

        return new SuccessResponse("Password changed successfully");
    }

    public SuccessResponse activateAccount(String activationToken) {
        ActivateToken activateToken = activateTokenRepository.findByToken(activationToken);

        if (activateToken == null) {
            throw new ActivateAccountException(AccountActivationNotPossibleReason.TOKEN_NOT_FOUND);
        }

        User user = activateToken.getUser();
        if (user.isActivated()) {
            throw new ActivateAccountException(AccountActivationNotPossibleReason.ACCOUNT_ALREADY_ACTIVATED);
        }

        if (activateToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ActivateAccountException(AccountActivationNotPossibleReason.TOKEN_EXPIRED);
        }

        user.setActivated(true);
        userRepository.save(user);

        return new SuccessResponse("Account successfully activated");
    }

    public SuccessResponse resendActivationToken(String expiredActivationToken) {
        ActivateToken activateToken = activateTokenRepository.findByToken(expiredActivationToken);

        String newToken = RandomString.make(30);
        activateToken.setNewToken(newToken);
        activateToken.setNewExpirationDateOfToken();
        String confirmLink = mailServiceUtil.constructConfirmLink(newToken, serverUrl);
        try {
            mailService.sendActivateAccountEmail(activateToken.getUser().getEmail(), confirmLink);
        } catch (MessagingException | UnsupportedEncodingException | MailSendException exception) {
            logger.error(exception.getMessage());
            throw new ResendActivationTokenException("Failed to send email. Try again later");
        }

        activateTokenRepository.save(activateToken);
        return new SuccessResponse("Email with new confirmation link was sent. Check your email inbox for further instructions");
    }
}
