package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.model.Product;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationTest {

    @Mock
    private Root<Product> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @SuppressWarnings("unchecked")
    private Specification<Product> invokeGetProductSpecification(String keyword, String category) throws Exception {
        Method method = ProductServiceImpl.class.getDeclaredMethod("getProductSpecification", String.class, String.class);
        method.setAccessible(true);
        return (Specification<Product>) method.invoke(null, keyword, category);
    }

    @Test
    void shouldReturnConjunctionOnlyWhenBothAreNull() throws Exception {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Product> spec = invokeGetProductSpecification(null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
        verify(criteriaBuilder).conjunction();
        verifyNoMoreInteractions(criteriaBuilder); // Garante que nenhum 'like' foi chamado
    }

    @Test
    void shouldReturnConjunctionOnlyWhenBothAreEmpty() throws Exception {
        Predicate conjunction = mock(Predicate.class);
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Product> spec = invokeGetProductSpecification("", "");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(conjunction, result);
        verify(criteriaBuilder).conjunction();
        verifyNoMoreInteractions(criteriaBuilder);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterByKeywordOnly() throws Exception {
        Predicate conjunction = mock(Predicate.class);
        Predicate keywordPredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        // CORREÇÃO: Usar Path<String> ao invés de Path<Object>
        Path<String> namePath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);

        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        // O mockito consegue inferir o tipo no get() se forçarmos o retorno
        when(root.<String>get("productName")).thenReturn(namePath);

        // Agora passa direto, sem precisar de (Expression<String>)
        when(criteriaBuilder.lower(namePath)).thenReturn(lowerExpr);
        when(criteriaBuilder.like(lowerExpr, "%iphone%")).thenReturn(keywordPredicate);

        when(criteriaBuilder.and(conjunction, keywordPredicate)).thenReturn(finalPredicate);

        Specification<Product> spec = invokeGetProductSpecification("iPhone", null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        assertEquals(finalPredicate, result);
        verify(criteriaBuilder).like(lowerExpr, "%iphone%");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterByCategoryOnly() throws Exception {
        Predicate conjunction = mock(Predicate.class);
        Predicate categoryPredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        Path<Object> categoryPath = mock(Path.class);
        // CORREÇÃO: Usar Path<String> aqui também
        Path<String> categoryNamePath = mock(Path.class);

        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        when(root.get("category")).thenReturn(categoryPath);
        when(categoryPath.<String>get("categoryName")).thenReturn(categoryNamePath);

        // Agora passa direto, sem precisar do cast!
        when(criteriaBuilder.like(categoryNamePath, "Electronics")).thenReturn(categoryPredicate);

        when(criteriaBuilder.and(conjunction, categoryPredicate)).thenReturn(finalPredicate);

        Specification<Product> spec = invokeGetProductSpecification(null, "Electronics");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        assertEquals(finalPredicate, result);
        verify(criteriaBuilder).like(categoryNamePath, "Electronics");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterByBothKeywordAndCategory() throws Exception {
        Predicate conjunction = mock(Predicate.class);
        Predicate keywordPredicate = mock(Predicate.class);
        Predicate categoryPredicate = mock(Predicate.class);
        Predicate and1 = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        // CORREÇÕES: Utilizar Path<String> para os campos de texto
        Path<String> namePath = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);

        Path<Object> categoryPath = mock(Path.class);
        Path<String> categoryNamePath = mock(Path.class);

        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        // Setup para Keyword
        when(root.<String>get("productName")).thenReturn(namePath);
        when(criteriaBuilder.lower(namePath)).thenReturn(lowerExpr); // Sem cast
        when(criteriaBuilder.like(lowerExpr, "%tv%")).thenReturn(keywordPredicate);
        when(criteriaBuilder.and(conjunction, keywordPredicate)).thenReturn(and1);

        // Setup para Category
        when(root.get("category")).thenReturn(categoryPath);
        when(categoryPath.<String>get("categoryName")).thenReturn(categoryNamePath);
        when(criteriaBuilder.like(categoryNamePath, "Electronics")).thenReturn(categoryPredicate); // Sem cast
        when(criteriaBuilder.and(and1, categoryPredicate)).thenReturn(finalPredicate);

        Specification<Product> spec = invokeGetProductSpecification("TV", "Electronics");
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        assertEquals(finalPredicate, result);

        verify(criteriaBuilder).like(lowerExpr, "%tv%");
        verify(criteriaBuilder).like(categoryNamePath, "Electronics");
    }
}