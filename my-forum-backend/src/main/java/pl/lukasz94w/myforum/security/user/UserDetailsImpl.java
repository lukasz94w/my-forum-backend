package pl.lukasz94w.myforum.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.lukasz94w.myforum.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    @JsonIgnore
    private final String password;
    private final boolean enabled;
    private final boolean isAdmin;
    private final boolean isBanned;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String password, boolean enabled,
                           Collection<? extends GrantedAuthority> authorities, boolean isAdmin, boolean isBanned) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getEnumeratedRole().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.isActivated(),
                authorities,
                user.isAdmin(),
                user.isBanned());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBanned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
