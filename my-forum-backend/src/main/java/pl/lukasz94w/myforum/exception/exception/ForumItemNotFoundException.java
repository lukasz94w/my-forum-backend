package pl.lukasz94w.myforum.exception.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;

@Getter
@AllArgsConstructor
public class ForumItemNotFoundException extends RuntimeException {

    private final ForumItemNotFoundExceptionReason forumItemNotFoundExceptionReason;
}
