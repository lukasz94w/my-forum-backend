package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
public final class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    @Email
    private String email;

    @NonNull
    private String password;

    @NonNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne
    @JoinColumn(name = "profilepic_id")
    private ProfilePic profilePic;

    private LocalDateTime registered = LocalDateTime.now();

    private boolean activated = false;

    @OneToOne
    @JoinColumn(name = "ban_id")
    private Ban ban;

    public boolean isAdmin() {
        return roles
                .stream()
                .anyMatch(role -> role.getEnumeratedRole().toString().equals("ROLE_ADMIN"));
    }

    public boolean isBanned() {
        if (ban == null) {
            return false;
        } else {
            return ban.getDateAndTimeOfBan().isAfter(LocalDateTime.now(ZoneId.systemDefault()));
        }
    }
}