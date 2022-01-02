package pl.lukasz94w.myforum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public final class PasswordToken {

    private static final int EXPIRATION_TIME_IN_DAY = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @NonNull
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    @NonNull
    private String token;

    private final LocalDateTime expiryDate = LocalDateTime.now().plusDays(EXPIRATION_TIME_IN_DAY);
}
