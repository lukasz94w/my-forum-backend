package pl.lukasz94w.myforum.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
    private byte[] profilePic;
    private LocalDateTime registered;
}
