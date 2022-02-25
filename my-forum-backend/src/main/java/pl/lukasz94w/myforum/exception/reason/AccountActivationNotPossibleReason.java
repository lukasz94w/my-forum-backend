package pl.lukasz94w.myforum.exception.reason;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountActivationNotPossibleReason {
    TOKEN_NOT_FOUND(ExceptionMessage.TOKEN_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    ACCOUNT_ALREADY_ACTIVATED(ExceptionMessage.ACCOUNT_WAS_ALREADY_ACTIVATED_MESSAGE, HttpStatus.CONFLICT),
    TOKEN_EXPIRED(ExceptionMessage.TOKEN_EXPIRED_MESSAGE, HttpStatus.GONE);

    private final String exceptionMessage;
    private final HttpStatus httpStatus;

    private static class ExceptionMessage {
        private static final String TOKEN_NOT_FOUND_MESSAGE = "Link is incorrect. No account associated with it was found";
        private static final String ACCOUNT_WAS_ALREADY_ACTIVATED_MESSAGE = "Account has already been activated";
        private static final String TOKEN_EXPIRED_MESSAGE = "Used link is out of date. Ask for new one";
    }
}
