package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class NewPostContent {

    @NotBlank
    @Size(min = 5, max = 1000)
    private String content;

    @NotNull
    private Long topicId;
}
