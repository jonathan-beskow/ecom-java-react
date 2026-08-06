package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.model.Category;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CategoryServiceImpl implements CategoryService {

    private Long nextId = 1L;
    private List<Category> categories = new ArrayList<>();

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories
                .stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst().orElseThrow( () -> new ResponseStatusException(NOT_FOUND));
        categories.remove(category);
        return "Category with categoryId: " + categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        Category cat = categories
                .stream()
                .filter(c -> c.getCategoryId().equals(id))
                .findFirst().orElseThrow( () -> new ResponseStatusException(NOT_FOUND));

        cat.setCategoryName(category.getCategoryName());

        return cat;
    }
}
