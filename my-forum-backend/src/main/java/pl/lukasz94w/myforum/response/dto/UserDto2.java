package pl.lukasz94w.myforum.response.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserDto2 extends UserDto {

    private final LocalDate dateOfBan;
    private final String reasonOfBan;

    public UserDto2(String name, String email, byte[] profilePic,
                    LocalDateTime registered, boolean isAdmin, boolean isBanned,
                    LocalDate dateOfBan, String reasonOfBan) {
        super(name, email, profilePic, registered, isAdmin, isBanned);
        this.dateOfBan = dateOfBan;
        this.reasonOfBan = reasonOfBan;
    }
}
