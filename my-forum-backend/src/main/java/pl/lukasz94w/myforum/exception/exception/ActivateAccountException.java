package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.enums.ActivateAccountExceptionEnum;

@Getter
@RequiredArgsConstructor
public class ActivateAccountException extends RuntimeException {

    private final ActivateAccountExceptionEnum activateAccountExceptionEnum;
}
