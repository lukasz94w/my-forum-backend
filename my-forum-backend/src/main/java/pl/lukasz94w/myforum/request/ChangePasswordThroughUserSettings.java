package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class ChangePasswordThroughUserSettings {

    @NotBlank
    @Size(min = 5, max = 30)
    private String currentPassword;

    @NotBlank
    @Size(min = 5, max = 30)
    private String newPassword;
}