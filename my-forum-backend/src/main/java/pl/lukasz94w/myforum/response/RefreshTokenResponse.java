package pl.lukasz94w.myforum.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RefreshTokenResponse {
    @NonNull
    private String accessToken;
    @NonNull
    private String refreshToken;
    private final String tokenType = "Bearer";
}
