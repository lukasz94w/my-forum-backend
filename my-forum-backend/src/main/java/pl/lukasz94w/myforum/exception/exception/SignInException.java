package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import pl.lukasz94w.myforum.exception.reason.SignInNotPossibleReason;

import java.util.Map;

@Getter
public class SignInException extends RuntimeException {

    private final SignInNotPossibleReason signInNotPossibleReason;
    private Map<String, Object> bannedUserData;

    public SignInException(SignInNotPossibleReason signInNotPossibleReason) {
        this.signInNotPossibleReason = signInNotPossibleReason;
    }

    public SignInException(SignInNotPossibleReason signInNotPossibleReason, Map<String, Object> bannedUserData) {
        this.signInNotPossibleReason = signInNotPossibleReason;
        this.bannedUserData = bannedUserData;
    }
}
