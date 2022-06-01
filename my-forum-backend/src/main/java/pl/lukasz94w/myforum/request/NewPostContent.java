package pl.lukasz94w.myforum.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class NewPostContent {

    @NotBlank
    @Size(min = 5, max = 1000)
    private String content;

    private Long topicId;
}
