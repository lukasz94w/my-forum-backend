package pl.lukasz94w.myforum.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class LastActivityInCategoryDto {
    private String topicName;
    private Long topicId;
    private String userName;
    private byte[] userProfilePic;
    private LocalDateTime timeOfLastActivity;
    private int postNumber;
}
