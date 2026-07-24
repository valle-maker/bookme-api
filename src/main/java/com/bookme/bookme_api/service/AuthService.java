package com.bookme.bookme_api.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.auth.AuthResponseDTO;
import com.bookme.bookme_api.dto.auth.ForgotPasswordRequestDTO;
import com.bookme.bookme_api.dto.auth.LoginRequestDTO;
import com.bookme.bookme_api.dto.auth.RegisterRequestDTO;
import com.bookme.bookme_api.dto.auth.ResetPasswordRequestDTO;
import com.bookme.bookme_api.entity.PasswordResetTokenEntity;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.repository.PasswordResetTokenRepository;
import com.bookme.bookme_api.repository.UserRepository;
import com.bookme.bookme_api.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.CLIENT)
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidOperationException("Invalid email or password"));

        if (!user.isActive()) {
            throw new InvalidOperationException("Account is deactivated");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidOperationException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    // ─── Recuperar contraseña ────────────────────────────────────────────────

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        // Si el email no existe, respondemos igual para no revelar si está registrado
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();

            PasswordResetTokenEntity resetToken = PasswordResetTokenEntity.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

            resetTokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetTokenEntity resetToken = resetTokenRepository.findByToken(dto.getToken())
            .orElseThrow(() -> new InvalidOperationException("Token inválido o expirado"));

        if (resetToken.isUsed()) {
            throw new InvalidOperationException("Este enlace ya fue utilizado");
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("El enlace ha expirado. Solicita uno nuevo");
        }

        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }
}
