package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.reason.SignUpExceptionReason;

@Getter
@RequiredArgsConstructor
public class SignUpException extends RuntimeException {

    private final SignUpExceptionReason signUpExceptionReason;
}
