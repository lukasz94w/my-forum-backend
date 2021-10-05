package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NonNull
    @Size(min = 5, max = 500)
    private String content;

    @ManyToOne
    @NonNull
    @NotNull
    private Topic topic;
}
