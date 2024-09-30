package com.mabotalb.book_network_api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Implement the registration method
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user by providing the necessary details")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad request, validation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        this.authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    // Implement the Login method
    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Login a user by providing valid credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Bad request, validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        return ResponseEntity.ok(this.authenticationService.login(request));
    }

    // Implement the activation account method
    @GetMapping("/activate-account")
    @Operation(summary = "Activate the user account", description = "Activate a user account by providing the activation token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account successfully activated"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void activate(
            @Pattern(regexp = "^\\d{6}$", message = "Token should be 6 digits numbers only") @RequestParam String token
    ) throws MessagingException {
        this.authenticationService.activateAccount(token);
    }

    // Implement the forgot password method
    @PostMapping("/forgot-password")
    @Operation(summary = "Send a reset password email", description = "Send an email to reset the password by providing the registered email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset password email sent"),
            @ApiResponse(responseCode = "400", description = "Bad request, validation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request
    ) throws MessagingException {
        this.authenticationService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    // Implement the reset password method
    @PatchMapping("/reset-password")
    @Operation(summary = "Reset the user password", description = "Reset a user password by providing the reset token and new password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid token or new password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> resetPassword(
            @Pattern(regexp = "^\\d{6}$", message = "Token should be 6 digits numbers only") @RequestParam String token,
            @RequestBody @Valid ResetPasswordRequest request
    ) throws MessagingException {
        this.authenticationService.resetPassword(token, request);
        return ResponseEntity.ok().build();
    }
}
