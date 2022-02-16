package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import pl.lukasz94w.myforum.exception.enums.SignInExceptionEnum;

import java.util.Map;

@Getter
public class SignInException extends RuntimeException {

    private final SignInExceptionEnum signInExceptionEnum;
    private Map<String, Object> bannedUserData;

    public SignInException(SignInExceptionEnum signInExceptionEnum) {
        this.signInExceptionEnum = signInExceptionEnum;
    }

    public SignInException(SignInExceptionEnum signInExceptionEnum, Map<String, Object> bannedUserData) {
        this.signInExceptionEnum = signInExceptionEnum;
        this.bannedUserData = bannedUserData;
    }
}
