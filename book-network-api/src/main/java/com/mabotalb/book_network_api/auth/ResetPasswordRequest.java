package com.mabotalb.book_network_api.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ResetPasswordRequest {

    @NotNull(message = "New password is required")
    @NotEmpty(message = "New password is required")
    @Size(min = 8, message = "New password should be at least 8 characters")
    private String newPassword;

    @NotNull(message = "Confirmation password is required")
    @NotEmpty(message = "Confirmation password is required")
//    @Size(min = 8, message = "Confirmation password should be at least 8 characters")
    private String confirmationPassword;
}
