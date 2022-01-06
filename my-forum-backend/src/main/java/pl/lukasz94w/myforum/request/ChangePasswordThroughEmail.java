package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ChangePasswordThroughEmail {

    @NotBlank
    @Size(min = 5, max = 30)
    private String newPassword;

    @NotBlank
    @Size(min = 30, max = 30)
    private String receivedToken;
}
