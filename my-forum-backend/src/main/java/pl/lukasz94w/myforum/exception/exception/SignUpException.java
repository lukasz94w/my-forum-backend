package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.enums.SignUpExceptionEnum;

@Getter
@RequiredArgsConstructor
public class SignUpException extends RuntimeException {

    private final SignUpExceptionEnum signUpExceptionEnum;
}
