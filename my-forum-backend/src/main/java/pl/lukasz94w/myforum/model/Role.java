package pl.lukasz94w.myforum.model;

import lombok.*;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Table (name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NonNull
    private EnumeratedRole enumeratedRole;
}
