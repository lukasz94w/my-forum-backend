package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findByEnumeratedCategory(EnumeratedCategory enumeratedCategory) {
        return categoryRepository.findByEnumeratedCategory(enumeratedCategory);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
