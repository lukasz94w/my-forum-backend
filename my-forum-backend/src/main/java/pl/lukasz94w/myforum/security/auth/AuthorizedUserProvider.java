package pl.lukasz94w.myforum.security.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;

@Component
public class AuthorizedUserProvider {

    public String getAuthorizedUserName() {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetailsImpl.getUsername();
    }
}
