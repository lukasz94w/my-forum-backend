package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter //TODO finalnie dac setter tylko do czasu aktualizacji...
@RequiredArgsConstructor
@Table(name = "topics")
public final class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @NonNull
    @JoinColumn(name = "user_id")
    private User user;

    //with this addNew post doesnt work (fetch = FetchType.LAZY)
    @OneToOne
    @NonNull
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime dateTime = LocalDateTime.now();

    private LocalDateTime timeOfActualization = dateTime;
}
