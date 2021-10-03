package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NonNull
    @Size(min = 5, max = 50)
    private String title;

    @NotNull
    @NonNull
    @Size(min = 5, max = 250)
    private String content;
}
