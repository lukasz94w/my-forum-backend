package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.Min;

@Getter
public final class TopicStatus {

    @Min(value = 1)
    private Long topicId;

    private boolean closed;
}
