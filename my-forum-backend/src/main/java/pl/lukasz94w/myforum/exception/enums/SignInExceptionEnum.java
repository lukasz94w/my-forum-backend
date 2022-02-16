package pl.lukasz94w.myforum.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SignInExceptionEnum {
    ACCOUNT_NOT_ACTIVATED(ExceptionMessage.ACCOUNT_NOT_ACTIVATED_MESSAGE, HttpStatus.TOO_EARLY),
    USER_IS_BANNED(ExceptionMessage.USER_IS_BANNED_MESSAGE, HttpStatus.LOCKED),
    BAD_CREDENTIALS(ExceptionMessage.BAD_CREDENTIALS_MESSAGE, HttpStatus.FORBIDDEN);

    private final String exceptionMessage;
    private final HttpStatus httpStatus;

    private static class ExceptionMessage {
        private static final String ACCOUNT_NOT_ACTIVATED_MESSAGE = "Account is not confirmed. Please check email for activation link";
        private static final String USER_IS_BANNED_MESSAGE = "You have been banned. From now you will not be able to create new topics, add posts or changing your personal data. Check profile settings for more info";
        private static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials, login or password";
    }
}
