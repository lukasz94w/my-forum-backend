package pl.lukasz94w.myforum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.*;
import pl.lukasz94w.myforum.response.message.SuccessResponse;
import pl.lukasz94w.myforum.service.AuthService;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<SuccessResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    public ResponseEntity<Map<String, String>> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return new ResponseEntity<>(authService.signIn(signInRequest), HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new ResponseEntity<>(authService.refreshToken(refreshTokenRequest), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<SuccessResponse> changePassword(@Valid @RequestBody ChangePasswordViaEmailLink changePasswordViaEmailLink) {
        return new ResponseEntity<>(authService.changePassword(changePasswordViaEmailLink), HttpStatus.CREATED);
    }

    @GetMapping("/activateAccount")
    public ResponseEntity<SuccessResponse> activateAccount(@RequestParam @Size(min = 30, max = 30) String activationToken) {
        return new ResponseEntity<>(authService.activateAccount(activationToken), HttpStatus.CREATED);
    }

    @GetMapping("/resendActivationToken")
    public ResponseEntity<SuccessResponse> resendActivationToken(@RequestParam @Size(min = 30, max = 30) String oldExpiredToken) {
        return new ResponseEntity<>(authService.resendActivationToken(oldExpiredToken), HttpStatus.OK);
    }
}
