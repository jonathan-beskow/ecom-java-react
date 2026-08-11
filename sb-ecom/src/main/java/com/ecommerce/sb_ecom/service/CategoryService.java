package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO category, Long id);
}
