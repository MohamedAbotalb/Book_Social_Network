package com.mabotalb.book_network_api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@Builder
public class LoginRequest {

    @Email(message = "Email isn't formatted")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;
}
