package pl.lukasz94w.myforum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopicDto {
    private Long id;

    private String title;

    private String content;

    private String username;
}
