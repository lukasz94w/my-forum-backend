package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.reason.AccountActivationNotPossibleReason;

@Getter
@RequiredArgsConstructor
public class ActivateAccountException extends RuntimeException {

    private final AccountActivationNotPossibleReason accountActivationNotPossibleReason;
}
