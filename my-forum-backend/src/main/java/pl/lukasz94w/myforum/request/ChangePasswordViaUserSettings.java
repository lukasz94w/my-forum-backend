package pl.lukasz94w.myforum.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class ChangePasswordViaUserSettings {

    @NotBlank
    @Size(min = 5, max = 30)
    private String currentPassword;

    @NotBlank
    @Size(min = 5, max = 30)
    private String newPassword;
}
