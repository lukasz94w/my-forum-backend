package pl.lukasz94w.myforum.security.token;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${pl.lukasz94w.jwtSecret}")
    private String jwtSecret;

    @Value("${pl.lukasz94w.jwtAccessTokenExpirationTimeInMs}")
    private int jwtExpirationMs;

    // TODO w sumie mozna to w sekundach przesylac i wtedy nie trzeba bedzie konwertowac tego we froncie!
    @Value("${pl.lukasz94w.jwtRefreshTokenExpirationTimeInMs}")
    private int jwtRefreshTokenExpirationTimeInMs;

    public String generateJwtAccessToken(UserDetailsImpl userPrincipal) {
        return generateTokenFromUserName(userPrincipal.getUsername());
    }

    public String generateTokenFromUserName(String userName) {

        //poza tym tu nie bedzie raczej z username tylko z calego usera
        //wtedy trzeba bedzie dawac
        //                 .map(user -> {
        //                    String token = jwtUtils.generateTokenFromUserDetails(user!!!!)
        //                })
        //TODO a i tutaj enabled bede w jakims scope przesylac!
        Claims claims = Jwts.claims();
        claims.setSubject(userName);
//        claims.put("scope", userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshTokenExpirationTimeInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public int getExpirationTimeInSeconds() {
        return jwtRefreshTokenExpirationTimeInMs / 1000;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
