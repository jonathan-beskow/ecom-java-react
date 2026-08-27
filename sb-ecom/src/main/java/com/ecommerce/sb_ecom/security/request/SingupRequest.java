package com.ecommerce.sb_ecom.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SingupRequest {

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;
    @Email
    private String email;
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;


}
