package com.ecommerce.sb_ecom.repositories;

import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Busca por categoria (objeto)
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    // Busca por keyword no nome do produto (LIKE)
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}