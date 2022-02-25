package pl.lukasz94w.myforum.exception.reason;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChangePasswordViaEmailLinkExceptionReason {
    TOKEN_NOT_FOUND(ExceptionMessage.TOKEN_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(ExceptionMessage.TOKEN_EXPIRED_MESSAGE, HttpStatus.GONE);

    private final String exceptionMessage;
    private final HttpStatus httpStatus;

    private static class ExceptionMessage {
        private static final String TOKEN_NOT_FOUND_MESSAGE = "Token not found";
        private static final String TOKEN_EXPIRED_MESSAGE =  "Token is expired. Ask for new reset link";
    }
}
