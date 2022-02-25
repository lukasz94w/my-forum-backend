package pl.lukasz94w.myforum.exception.reason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ForumItemNotFoundExceptionReason {
    TOPIC_DOESNT_EXIST(ExceptionMessage.TOPIC_DOESNT_EXIST_MESSAGE),
    CATEGORY_DOESNT_EXIST(ExceptionMessage.CATEGORY_DOESNT_EXIST_MESSAGE),
    POST_DOESNT_EXIST(ExceptionMessage.POST_DOESNT_EXIST_MESSAGE),
    USER_DOESNT_EXIST(ExceptionMessage.USER_DOESNT_EXIST_MESSAGE);

    private final String exceptionMessage;

    private static class ExceptionMessage {
        private static final String TOPIC_DOESNT_EXIST_MESSAGE = "There is no topic with such number";
        private static final String CATEGORY_DOESNT_EXIST_MESSAGE = "Such category doesn't exist";
        private static final String POST_DOESNT_EXIST_MESSAGE = "Post with this number doesn't exist";
        private static final String USER_DOESNT_EXIST_MESSAGE = "User not found";
    }
}
