package com.ecommerce.sb_ecom.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class APIException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }

}
