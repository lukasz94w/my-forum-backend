package pl.lukasz94w.myforum.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {
    private final Long id;
    private final String content;
    private final String user;
    private final LocalDateTime dateTime;
    private final byte[] profilePic;
    private final int number;
    private final boolean moderated;
}
