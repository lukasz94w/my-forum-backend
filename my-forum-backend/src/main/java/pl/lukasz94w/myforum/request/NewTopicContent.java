package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class NewTopicContent {

    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank
    @Size(min = 5, max = 1000)
    private String content;

    private String category;
}
