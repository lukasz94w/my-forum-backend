package pl.lukasz94w.myforum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
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

    @NonNull
    @NotBlank
    @Size(min = 5, max = 500)
    private String content;

    private LocalDateTime dateTimeOfTopic = LocalDateTime.now();

    @ManyToOne (fetch = FetchType.LAZY)
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
