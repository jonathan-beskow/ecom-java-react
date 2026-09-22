package com.ecommerce.sb_ecom.exception;

import com.ecommerce.sb_ecom.payload.APIResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {


    @Test
    void resourceNotFound_returns404() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResourceNotFoundException ex =
                new ResourceNotFoundException("Category", "cateoryId", 9L);

        ResponseEntity<APIResponse> response = handler.myResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void apiException_returns400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        APIException ex = new APIException("Some trouble has occurred");

        ResponseEntity<APIResponse> response = handler.myAPIException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void methodArgumentInvalid_returns400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        FieldError fieldError = new FieldError("objectName", "email", "must not be blank");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.getAllErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.methodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("must not be blank", response.getBody().get("email"));
    }


}