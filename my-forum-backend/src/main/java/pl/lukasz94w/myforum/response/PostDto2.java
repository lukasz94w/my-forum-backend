package pl.lukasz94w.myforum.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto2 {
    private final Long topicId;
    private final String topicTitle;
    private final String topicCategory;
    private final LocalDateTime dateTime;
    private final String content;
    private final int number;
}
