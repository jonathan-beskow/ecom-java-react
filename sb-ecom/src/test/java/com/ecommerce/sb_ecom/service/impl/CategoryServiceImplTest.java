package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    Category category;
    Category saved;
    Category category2;
    Category saved2;
    CategoryDTO input;
    CategoryDTO output;
    CategoryDTO input2;
    CategoryDTO output2;


    @BeforeEach
    void init() {

        category = new Category();
        category.setCategoryName("Books");

        category2 = new Category();
        category2.setCategoryName("Crimes");

        saved = new Category();
        saved.setCategoryId(1L);
        saved.setCategoryName("Books");

        saved2 = new Category();
        saved2.setCategoryId(2L);
        saved2.setCategoryName("Crimes");

        input = new CategoryDTO(null, "Books");
        output = new CategoryDTO(1L, "Books");

        input2 = new CategoryDTO(null, "Crimes");
        output2 = new CategoryDTO(2L, "Crimes");
    }

    @Test
    void createCategory_whenNameDoesNotExist_savesAndReturnsDto() {

        when(modelMapper.map(input, Category.class)).thenReturn(category);
        when(categoryRepository.findByCategoryName("Books")).thenReturn(null);
        when(categoryRepository.save(category)).thenReturn(saved);
        when(modelMapper.map(saved, CategoryDTO.class)).thenReturn(output);

        CategoryDTO result = categoryService.createCategory(input);

        assertEquals(1L, result.getCategoryId());
        assertEquals("Books", result.getCategoryName());
        verify(categoryRepository).save(category);
    }

    @Test
    void createCategory_whenNameAlreadyExists_throwsApiException() {
        CategoryDTO input = new CategoryDTO(null, "Books");
        Category category = new Category();
        category.setCategoryName("Books");

        when(modelMapper.map(input, Category.class)).thenReturn(category);
        when(categoryRepository.findByCategoryName("Books")).thenReturn(new Category());

        assertThrows(APIException.class, () -> categoryService.createCategory(input));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void shouldReturnAllCategoriesWithSuccess() {

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);
        categoryList.add(category2);

        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "categoryId";
        String sortOrder = "asc";

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());

        Page<Category> categoryPage = new PageImpl<>(
                categoryList,
                pageDetails,
                categoryList.size()
        );

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(categoryPage);
        when(modelMapper.map(category, CategoryDTO.class))
                .thenReturn(output);
        when(modelMapper.map(category2, CategoryDTO.class))
                .thenReturn(output2);

        CategoryResponse response = categoryService.getAllCategories(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(pageNumber, response.getPageNumber());
        assertEquals(pageSize, response.getPageSize());
        assertEquals(2, response.getTotalElements());
        assertTrue(response.isLastPage());
        assertEquals("Books", response.getContent().get(0).getCategoryName());
        assertEquals("Crimes", response.getContent().get(1).getCategoryName());
    }

    @Test
    void shouldReturnAllCategoriesWithDescendingOrder() {

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category2);
        categoryList.add(category);


        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "categoryId";
        String sortOrder = "desc";

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

        Page<Category> categoryPage = new PageImpl<>(
                categoryList,
                pageDetails,
                categoryList.size()
        );

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(categoryPage);
        when(modelMapper.map(category, CategoryDTO.class))
                .thenReturn(output);
        when(modelMapper.map(category2, CategoryDTO.class))
                .thenReturn(output2);

        CategoryResponse response = categoryService.getAllCategories(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(pageNumber, response.getPageNumber());
        assertEquals(pageSize, response.getPageSize());
        assertEquals(2, response.getTotalElements());
        assertTrue(response.isLastPage());
        assertEquals("Crimes", response.getContent().get(0).getCategoryName());
        assertEquals("Books", response.getContent().get(1).getCategoryName());
    }

    @Test
    void shouldThrowApiExceptionWithNoCategoryReturned() {

        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "categoryId";
        String sortOrder = "asc";
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());

        List<Category> categories = new ArrayList<>();
        Page<Category> categoryPage = new PageImpl<>(
                categories,
                pageDetails,
                categories.size()
        );

        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(categoryPage);

        //Captura a exceção
        APIException exception = assertThrows(APIException.class,
                () -> categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));

        assertEquals("No category created till now", exception.getMessage());

    }

    @Test
    void shouldDeleteACategory() {

        Long categoryId = 1L;

        //preparando para recuperar uma categoria
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(saved));
        //prepara pra deletar
        doNothing()
                .when(categoryRepository).delete(saved);

        //prepara para mapear
        when(modelMapper.map(saved, CategoryDTO.class))
                .thenReturn(output);

        CategoryDTO result = categoryService.deleteCategory(categoryId);
        assertNotNull(result);
        assertEquals(output.getCategoryId(), result.getCategoryId());
        assertEquals(output.getCategoryName(), result.getCategoryName());

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(saved);
        verify(categoryRepository).delete(any(Category.class));
    }

    @Test
    void shouldThrowExceptionWhenCategoryIdIsNotFoundWhenDeleteIsCalled() {

        Long categoryId = 6L;

        //preparando para recuperar uma categoria
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(categoryId));

        assertNotNull(ex);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));
        verify(modelMapper, never()).map(any(Category.class), eq(CategoryDTO.class));
    }

    @Test
    void shouldUpdateACategorySuccessfully() {
        
    }



}
