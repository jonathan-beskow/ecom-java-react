package com.ecommerce.sb_ecom.security.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestTest {


    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenFieldsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Should not have validation errors");
    }

    @Test
    void shouldFailValidationWhenUsernameIsBlank() {
        LoginRequest request = new LoginRequest();
        request.setUsername(""); // Vazio
        request.setPassword("123456");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Deve haver exatamente 1 erro
        assertEquals(1, violations.size());

        // O erro deve ser no campo username
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("username", violation.getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenPasswordIsNull() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword(null); // Nulo

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("password", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenBothFieldsAreBlank() {
        LoginRequest request = new LoginRequest();
        request.setUsername("   "); // Apenas espaços
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Ambos os campos devem falhar
        assertEquals(2, violations.size());
    }

    @Test
    void shouldSetAndGetPropertiesCorrectly() {
        // Teste simples para cobrir Getters e Setters (importante se você usa ferramentas como o SonarQube)
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");

        assertEquals("user", request.getUsername());
        assertEquals("pass", request.getPassword());
    }

}