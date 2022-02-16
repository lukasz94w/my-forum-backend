package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ResetPasswordRequest {

    @NotBlank
    @Size(min = 4, max = 50)
    @Email
    private String email;
}
