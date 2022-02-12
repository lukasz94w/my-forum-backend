package pl.lukasz94w.myforum.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicDto3 {
    private final String title;
    private final boolean closed;
}
