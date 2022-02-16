package pl.lukasz94w.myforum.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostAddExceptionEnum {
    TOPIC_DOESNT_EXIST(ExceptionMessage.TOPIC_DOESNT_EXIST_MESSAGE, HttpStatus.GONE),
    TOPIC_WAS_CLOSED(ExceptionMessage.TOPIC_WAS_CLOSED_MESSAGE, HttpStatus.LOCKED);

    private final String exceptionMessage;
    private final HttpStatus httpStatus;

    private static class ExceptionMessage {
        private static final String TOPIC_DOESNT_EXIST_MESSAGE = "Such topic was deleted or doesn't exist";
        private static final String TOPIC_WAS_CLOSED_MESSAGE = "Topic was closed. You can't write in it anymore";
    }
}
