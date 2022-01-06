package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.ChangePasswordThroughEmail;
import pl.lukasz94w.myforum.request.LoginRequest;
import pl.lukasz94w.myforum.request.SendResetEmailRequest;
import pl.lukasz94w.myforum.request.SignupRequest;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.service.AuthService;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.signIn(loginRequest);
    }

    @PostMapping("/sendEmailWithResetToken")
    public void sendEmailWithResetToken(@Valid @RequestBody SendResetEmailRequest sendResetEmailRequest) {
        authService.sendEmailWithResetToken(sendResetEmailRequest);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePasswordThroughEmail(@Valid @RequestBody ChangePasswordThroughEmail changePasswordThroughEmail) {
        return authService.changePasswordThroughEmail(changePasswordThroughEmail);
    }

    @GetMapping("/activateAccount")
    public ResponseEntity<?> activateAccount(@RequestParam @Size(min = 30, max = 30) String activationToken) {
        return authService.activateAccount(activationToken);
    }

    @GetMapping("/resendActivationToken")
    public ResponseEntity<?> resendActivationToken(@RequestParam @Size(min = 30, max = 30) String oldExpiredToken) {
        return authService.resendActivationToken(oldExpiredToken);
    }

}
