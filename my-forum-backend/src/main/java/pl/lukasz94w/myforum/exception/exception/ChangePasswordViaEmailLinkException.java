package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.reason.ChangePasswordViaEmailLinkExceptionReason;

@Getter
@RequiredArgsConstructor
public class ChangePasswordViaEmailLinkException extends RuntimeException {

    private final ChangePasswordViaEmailLinkExceptionReason changePasswordViaEmailLinkExceptionReason;
}
