package com.bookme.bookme_api.dto.user;

import java.time.LocalDateTime;

import com.bookme.bookme_api.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;

    private String name;

    private String lastName;

    private String email;

    private String phone;

    private Role role;

    private LocalDateTime createdAt;

    private boolean active;
    
}