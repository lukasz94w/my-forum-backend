package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TopicDto {
    private final Long id;
    private final String title;
    private final String user;
    private final LocalDateTime dateTime;
}
