package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.Min;

@Getter
public class PostStatus {

    @Min(value = 0)
    private Long postId;

    private boolean moderated;
}
