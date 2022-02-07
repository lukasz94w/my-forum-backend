package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public final class WebServiceMessageRequest {

    private boolean statusOfBan;

    @NotBlank
    @Size(min = 4, max = 30)
    private String userName;
}
