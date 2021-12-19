package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String content;
    private String user;
    private LocalDateTime dateTime;
    private byte[] profilePic;
    private int number;
}
