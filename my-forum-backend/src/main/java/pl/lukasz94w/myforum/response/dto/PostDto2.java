package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto2 {
    private Long topicId;
    private String topicTitle;
    private String topicCategory;
    private LocalDateTime dateTime;
    private String content;
}
