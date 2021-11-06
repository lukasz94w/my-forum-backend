package pl.lukasz94w.myforum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

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

    private LocalDateTime dateTimeOfPost = LocalDateTime.now();

    @ManyToOne (fetch = FetchType.EAGER)
    @NonNull
    @NotNull
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @NonNull
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", dateTimeOfPost=" + dateTimeOfPost +
                ", topic=" + topic +
                ", user=" + user +
                '}';
    }
}
