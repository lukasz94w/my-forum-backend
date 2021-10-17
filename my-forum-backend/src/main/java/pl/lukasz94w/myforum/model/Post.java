package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @NonNull
    @Size(min = 5, max = 500)
    private String content;

    @ManyToOne
    @NonNull
    @NotNull
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @NonNull
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;
}
