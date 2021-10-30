package pl.lukasz94w.myforum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.lukasz94w.myforum.model.Topic;

@Getter
@Setter
@AllArgsConstructor
public class PostDto {
    private Long id;

    private String content;

    //is it needed?
    private Topic topic;
}
