package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize) {

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new APIException("No category created till now");
        }

        List<CategoryDTO> categoryDTOS = categories.stream().map(
                category -> {
                    return modelMapper.map(category, CategoryDTO.class);
                }
        ).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryFromDb != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        var savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(savedCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long id) {
        var savedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", id));
        Category category = modelMapper.map(savedCategory, Category.class);
        category.setCategoryId(id);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
