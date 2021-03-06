package pl.lukasz94w.myforum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public class ActivateToken {

    @Transient
    private final int EXPIRATION_TIME_IN_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NonNull
    @JoinColumn(name = "user_id")
    private User user;

    @NonNull
    private String token;

    private LocalDateTime expiryDate = LocalDateTime.now().plusHours(EXPIRATION_TIME_IN_HOURS);

    public void setNewExpirationDateOfToken() {
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_TIME_IN_HOURS);
    }

    public void setNewToken(String newToken) {
        this.token = newToken;
    }
}
