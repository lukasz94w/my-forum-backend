package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByEnumeratedCategory(EnumeratedCategory enumeratedCategory);

    List<Category> findAll();
}
