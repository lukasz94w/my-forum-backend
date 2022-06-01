package pl.lukasz94w.myforum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lukasz94w.myforum.exception.exception.ActivateAccountException;
import pl.lukasz94w.myforum.exception.exception.SignUpException;
import pl.lukasz94w.myforum.exception.reason.AccountActivationExceptionReason;
import pl.lukasz94w.myforum.exception.reason.SignUpExceptionReason;
import pl.lukasz94w.myforum.model.ActivateToken;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.ActivateTokenRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.SignUpRequest;
import pl.lukasz94w.myforum.response.message.SuccessResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ActivateTokenRepository activateTokenRepository;
    @InjectMocks
    AuthService authService;

    @Test
    void shouldThrowExceptionWhenTryingToSignUpUsingExistingName() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest("alreadyExistingUserName", "someEmail@gmail.com", "somePassword");
        given(userRepository.existsByName("alreadyExistingUserName")).willThrow(new SignUpException(SignUpExceptionReason.USERNAME_IS_TAKEN));

        // when, then
        SignUpException exception = assertThrows(SignUpException.class, () -> authService.signUp(signUpRequest));

        assertEquals("username is already taken", exception.getSignUpExceptionReason().getExceptionMessage());
        then(userRepository)
                .should(never())
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToSignUpUsingExistingEmail() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest("user1", "alreadyExistingEmail@gmail.com", "somePassword");
        given(userRepository.existsByEmail("alreadyExistingEmail@gmail.com")).willThrow(new SignUpException(SignUpExceptionReason.EMAIL_IS_TAKEN));

        // when, then
        SignUpException exception = assertThrows(SignUpException.class, () -> authService.signUp(signUpRequest));

        assertEquals("email is already in use", exception.getSignUpExceptionReason().getExceptionMessage());
        then(userRepository)
                .should(never())
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToActivateAccountWithNotExistingToken() {
        // given
        String tokenWhichDoesNotExistInDb = "3258t3ng2o3g3902j99g032";
        given(activateTokenRepository.findByToken(tokenWhichDoesNotExistInDb)).willThrow(new ActivateAccountException(AccountActivationExceptionReason.TOKEN_NOT_FOUND));

        // when, then
        ActivateAccountException exception = assertThrows(ActivateAccountException.class, () -> {
            authService.activateAccount(tokenWhichDoesNotExistInDb);
        });

        assertEquals("Link is incorrect. No account associated with it was found", exception.getAccountActivationExceptionReason().getExceptionMessage());
        then(userRepository)
                .should(never())
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToActivateAccountWhichWasAlreadyActivated() {
        // given
        String tokenBelongingToActivatedAccount = "3258t3ng2o3g3902j99g032";
        User user = new User();
        user.setActivated(true);
        given(activateTokenRepository.findByToken(tokenBelongingToActivatedAccount)).willReturn(new ActivateToken(user, tokenBelongingToActivatedAccount));

        // when, then
        ActivateAccountException exception = assertThrows(ActivateAccountException.class, () -> {
            authService.activateAccount(tokenBelongingToActivatedAccount);
        });

        assertEquals("Account has already been activated", exception.getAccountActivationExceptionReason().getExceptionMessage());
        then(userRepository)
                .should(never())
                .save(any(User.class));
    }

    @Test
    void shouldReturnSuccessResponseAfterUsingValidActivationToken() {
        // given
        String validToken = "3258t3ng2o3g3902j99g032";
        User userWhichIsActivatingHisAccount = new User();
        given(activateTokenRepository.findByToken(validToken)).willReturn(new ActivateToken(userWhichIsActivatingHisAccount, validToken));

        // when
        SuccessResponse successResponse = authService.activateAccount(validToken);

        // then
        then(userRepository)
                .should(atLeastOnce())
                .save(userWhichIsActivatingHisAccount);
        assertEquals("Account successfully activated", successResponse.getMessage());
    }
}