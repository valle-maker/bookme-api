package com.bookme.bookme_api.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookme.bookme_api.dto.user.UserResponseDTO;
import com.bookme.bookme_api.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControl {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id){
        UserResponseDTO dto = userService.getById(id);
        return ResponseEntity.ok(dto);
    }
    
    
}
