package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.security.jwt.JwtUtils;
import com.ecommerce.sb_ecom.security.services.UserDetailsServiceImpl;
import com.ecommerce.sb_ecom.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CategoryService categoryService;
    @MockitoBean
    JwtUtils jwtUtils;
    @MockitoBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturnAllCategories() throws Exception {
        CategoryResponse response = new CategoryResponse();
        response.setContent(List.of(new CategoryDTO(1L, "Books")));

        when(categoryService.getAllCategories(any(), any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryName").value("Books"));

    }

}