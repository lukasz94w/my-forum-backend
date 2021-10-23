package pl.lukasz94w.myforum.postRequest;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class NewTopicContent {

    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank
    @Size(min = 5, max = 500)
    private String content;

}
