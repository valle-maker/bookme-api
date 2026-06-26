package com.bookme.bookme_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.auth.AuthResponseDTO;
import com.bookme.bookme_api.dto.auth.LoginRequestDTO;
import com.bookme.bookme_api.dto.auth.RegisterRequestDTO;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.exception.DuplicateResourceException;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.repository.UserRepository;
import com.bookme.bookme_api.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
}