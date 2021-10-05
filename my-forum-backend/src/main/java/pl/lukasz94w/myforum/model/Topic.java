package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NonNull
    @Size(min = 5, max = 500)
    private String title;

    @NotNull
    @NonNull
    @Size(min = 5, max = 500)
    private String content;
}
