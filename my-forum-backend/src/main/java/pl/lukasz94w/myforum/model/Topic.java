package pl.lukasz94w.myforum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @ManyToOne
    @NotNull
    @NonNull
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @NotNull
    @NonNull
    @JoinColumn(name = "category_id")
    private Category category;
}
