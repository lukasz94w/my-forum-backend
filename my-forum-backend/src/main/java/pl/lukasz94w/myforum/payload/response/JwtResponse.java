package pl.lukasz94w.myforum.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public final class JwtResponse {

    //token data
    private String token;
    private final String type = "Bearer";
    private int expirationTime;

    //user data
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
