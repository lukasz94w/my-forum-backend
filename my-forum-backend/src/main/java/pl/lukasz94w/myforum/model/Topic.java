package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@RequiredArgsConstructor
@Table(name = "topics")
public final class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @NonNull
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @NonNull
    @JoinColumn(name = "user_id")
    private User user;

    // with this addNew post doesn't work (fetch = FetchType.LAZY)
    @OneToOne
    @NonNull
    @JoinColumn(name = "category_id")
    private Category category;

    private final LocalDateTime dateTime = LocalDateTime.now();

    @Setter
    private LocalDateTime timeOfActualization = dateTime;

    @Setter
    private boolean closed = false;
}
