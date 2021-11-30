package pl.lukasz94w.myforum.model;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;

import javax.persistence.*;
import java.util.Locale;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NonNull
    private EnumeratedCategory enumeratedCategory;

    @Override
    public String toString() {
        return enumeratedCategory.toString().toLowerCase(Locale.ROOT);
    }
}
