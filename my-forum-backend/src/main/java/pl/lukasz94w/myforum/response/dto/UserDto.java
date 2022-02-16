package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserDto {
    private final String name;
    private final String email;
    private final byte[] profilePic;
    private final LocalDateTime registered;
    private final boolean isAdmin;
    private final boolean isBanned;
}
