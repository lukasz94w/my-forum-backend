package pl.lukasz94w.myforum.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public final class SignupRequest {

    @NotBlank
    @Size(min = 4, max = 30)
    private String username;

    @NotBlank
    @Size(min = 4, max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 30)
    private String password;

    private Set<String> role;
}
