package com.mabotalb.book_network_api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @Email(message = "Email isn't formatted")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;
}
