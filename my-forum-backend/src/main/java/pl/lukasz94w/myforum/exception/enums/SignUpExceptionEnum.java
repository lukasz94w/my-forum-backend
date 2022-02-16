package pl.lukasz94w.myforum.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SignUpExceptionEnum {
    USERNAME_IS_TAKEN(ExceptionMessage.USERNAME_IS_TAKEN_MESSAGE, HttpStatus.BAD_REQUEST),
    EMAIL_IS_TAKEN(ExceptionMessage.EMAIL_IS_TAKEN_MESSAGE, HttpStatus.BAD_REQUEST),
    SENDING_MAIL_FAILED(ExceptionMessage.SENDING_MAIL_FAILED_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);

    private final String exceptionMessage;
    private final HttpStatus httpStatus;

    private static class ExceptionMessage {
        private static final String USERNAME_IS_TAKEN_MESSAGE = "username is already taken";
        private static final String EMAIL_IS_TAKEN_MESSAGE = "email is already in use";
        private static final String SENDING_MAIL_FAILED_MESSAGE = "cannot send verification email. Try again later";
    }
}
