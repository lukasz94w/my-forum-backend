package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import pl.lukasz94w.myforum.exception.reason.SignInExceptionReason;

import java.util.Map;

@Getter
public class SignInException extends RuntimeException {

    private final SignInExceptionReason signInExceptionReason;
    private Map<String, Object> bannedUserData;

    public SignInException(SignInExceptionReason signInExceptionReason) {
        this.signInExceptionReason = signInExceptionReason;
    }

    public SignInException(SignInExceptionReason signInExceptionReason, Map<String, Object> bannedUserData) {
        this.signInExceptionReason = signInExceptionReason;
        this.bannedUserData = bannedUserData;
    }
}
