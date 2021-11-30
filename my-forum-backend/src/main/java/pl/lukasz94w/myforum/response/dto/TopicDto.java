package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TopicDto {
    private Long id;
    private String title;
    private String user;
    private LocalDateTime dateTime;
}
