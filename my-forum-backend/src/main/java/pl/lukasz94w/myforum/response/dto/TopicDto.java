package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {
    private Long id;
    private String title;
    private String user;
    private LocalDateTime dateTime;
}
