package pl.lukasz94w.myforum.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public final class JwtResponse {

    //token data
    private String accessToken;
    private final String type = "Bearer";
    private String refreshToken;
    private int expirationTime;

    //user data
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private List<String> roles;
}
