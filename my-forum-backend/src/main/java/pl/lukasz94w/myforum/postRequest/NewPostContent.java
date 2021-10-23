package pl.lukasz94w.myforum.postRequest;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class NewPostContent {

    @NotBlank
    @Size(min = 5, max = 500)
    private String content;

    @NotNull
    private Long topicId;
}
