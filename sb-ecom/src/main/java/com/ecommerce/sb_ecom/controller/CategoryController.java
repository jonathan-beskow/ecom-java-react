package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.config.AppConstants;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping("/echo")
    public ResponseEntity<String> echoMessage(@RequestParam(name = "message") String message) {
        return new ResponseEntity<>("Echoed message: " + message, OK);
    }


    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder

    ) {
        CategoryResponse categories = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categories, OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, CREATED);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable("categoryId") Long id) {
        CategoryDTO newCat = categoryService.updateCategory(categoryDTO, id);
        return new ResponseEntity<>(newCat, OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

}
