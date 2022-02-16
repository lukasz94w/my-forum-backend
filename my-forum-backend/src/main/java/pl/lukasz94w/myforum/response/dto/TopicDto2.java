package pl.lukasz94w.myforum.response.dto;

import lombok.Getter;
import pl.lukasz94w.myforum.response.dto.TopicDto;

import java.time.LocalDateTime;

@Getter
public class TopicDto2 extends TopicDto {
    private final String category;

    public TopicDto2(Long id, String title, String user, LocalDateTime dateTime, String category) {
        super(id, title, user, dateTime);
        this.category = category;
    }
}
