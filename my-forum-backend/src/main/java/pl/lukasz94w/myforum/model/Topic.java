package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@NoArgsConstructor
@Getter
@Setter //TODO finalnie dac setter tylko do czasu aktualizacji...
@RequiredArgsConstructor
@Table(name = "topics")
public final class Topic {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    private LocalDateTime dateTime = LocalDateTime.now();

    private LocalDateTime timeOfActualization = dateTime;

    @ManyToOne (fetch = FetchType.EAGER)
    @NotNull
    @NonNull
    @JoinColumn(name = "user_id")
    private User user;

    //with this addNew post doesnt work (fetch = FetchType.LAZY)
    @OneToOne
    @NotNull
    @NonNull
    @JoinColumn(name = "category_id")
    private Category category;
}
