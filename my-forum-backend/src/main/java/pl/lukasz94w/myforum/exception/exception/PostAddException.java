package pl.lukasz94w.myforum.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.exception.enums.PostAddExceptionEnum;

@Getter
@RequiredArgsConstructor
public class PostAddException extends RuntimeException {

    private final PostAddExceptionEnum postAddExceptionEnum;
}
