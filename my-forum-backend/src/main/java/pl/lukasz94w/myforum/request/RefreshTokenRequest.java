package pl.lukasz94w.myforum.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
