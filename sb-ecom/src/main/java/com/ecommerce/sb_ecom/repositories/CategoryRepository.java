package com.ecommerce.sb_ecom.repositories;

import com.ecommerce.sb_ecom.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(@NotBlank @Size(min = 4, message = "Category must contain at least 4 characters") String categoryName);
}
