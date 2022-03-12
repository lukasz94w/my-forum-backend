package pl.lukasz94w.myforum.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public final class SignUpRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String username;

    @NotBlank
    @Size(min = 4, max = 20)
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 30)
    private String password;
}
