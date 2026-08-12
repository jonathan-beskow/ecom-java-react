package com.ecommerce.sb_ecom.exception;

import com.ecommerce.sb_ecom.payload.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            response.put(fieldName, message);
        });
        return new ResponseEntity<Map<String, String>>(
                response, BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException ex) {
        String message = ex.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException ex) {
        String message = ex.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, BAD_REQUEST);
    }

}
