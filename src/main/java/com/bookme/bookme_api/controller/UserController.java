package com.bookme.bookme_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookme.bookme_api.dto.profile.ChangePasswordRequestDTO;
import com.bookme.bookme_api.dto.profile.ProfileUpdateRequestDTO;
import com.bookme.bookme_api.dto.user.UserRequestDTO;
import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')") 
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id){
        UserResponseDTO dto = userService.getById(id);
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto){
        UserResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }   
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAll(
        Pageable pageable) {

        Page<UserResponseDTO> users = userService.getAll(pageable);

        return ResponseEntity.ok(users);
    
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.update(id, dto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> delete(@PathVariable Long id){

        userService.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<UserResponseDTO> getMe(Authentication authentication) {
        UserResponseDTO response = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<UserResponseDTO> updateMe(
        @Valid @RequestBody ProfileUpdateRequestDTO dto,
        Authentication authentication) {
        UserResponseDTO response = userService.updateMe(dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequestDTO dto,
        Authentication authentication) {
        userService.changePassword(dto, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
