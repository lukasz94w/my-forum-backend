package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.enums.ChangePasswordViaEmailLinkExceptionEnum;

@Getter
@RequiredArgsConstructor
public class ChangePasswordViaEmailLinkException extends RuntimeException {

    private final ChangePasswordViaEmailLinkExceptionEnum changePasswordViaEmailLinkExceptionEnum;
}
