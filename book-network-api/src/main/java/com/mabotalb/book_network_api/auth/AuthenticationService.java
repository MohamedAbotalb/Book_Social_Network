package com.mabotalb.book_network_api.auth;

import com.mabotalb.book_network_api.email.EmailService;
import com.mabotalb.book_network_api.email.EmailTemplateName;
import com.mabotalb.book_network_api.exception.ExpiredTokenException;
import com.mabotalb.book_network_api.exception.InvalidTokenException;
import com.mabotalb.book_network_api.exception.NotEqualPasswordsException;
import com.mabotalb.book_network_api.exception.UserAlreadyExistsException;
import com.mabotalb.book_network_api.role.RoleRepository;
import com.mabotalb.book_network_api.security.JwtService;
import com.mabotalb.book_network_api.user.Token;
import com.mabotalb.book_network_api.user.TokenRepository;
import com.mabotalb.book_network_api.user.User;
import com.mabotalb.book_network_api.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${application.mailing.frontend.reset-url}")
    private String resetUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = this.roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role User not found"));

        // Check if the user is already exist
        var existingUser = this.userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var token = generateAndSaveToken(user);
        // Send email with validation link
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                token,
                "Account activation"
        );
    }

    private String generateAndSaveToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    public LoginResponse login(LoginRequest request) {
        var auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return LoginResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = this.tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        // Check if the token is expired
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            this.sendValidationEmail(savedToken.getUser());
            throw new ExpiredTokenException("Activation token has expired, A new token has been sent to the same email");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        User user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("There's no registered user with this email"));

        this.sendResetPasswordEmail(user);
    }

    private void sendResetPasswordEmail(User user) throws MessagingException {
        var resetCode = generateAndSaveToken(user);
        // Send email with reset code
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.FORGOT_PASSWORD,
                resetUrl,
                resetCode,
                "Password reset"
        );
    }

    public void resetPassword(String token, ResetPasswordRequest request) throws MessagingException {
        Token savedToken = this.tokenRepository.findByToken(token)
               .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        // Check if the token is expired
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            this.sendResetPasswordEmail(savedToken.getUser());
            throw new ExpiredTokenException("Reset token has expired!, A new token has been sent to the same email");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
               .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if the new password matches the confirmation password
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new NotEqualPasswordsException("Passwords do not match");
        }

        // Update the user's password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
