package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class LastActivityInPageableTopicDto {
    private String userName;
    private byte[] userProfilePic;
    private LocalDateTime timeOfLastActivity;
    private int postNumber;
}
