package com.bookme.bookme_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookme.bookme_api.dto.barber.BarberRequestDTO;
import com.bookme.bookme_api.dto.barber.BarberResponseDTO;
import com.bookme.bookme_api.service.BarberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v1/barbers")
@RequiredArgsConstructor
public class BarberController {
    private final BarberService barberService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<BarberResponseDTO> findById(@PathVariable Long id){
        BarberResponseDTO response = barberService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarberResponseDTO> create(@Valid @RequestBody BarberRequestDTO dto){
        BarberResponseDTO response = barberService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<Page<BarberResponseDTO>> getAllActive(Pageable pageable){
        Page<BarberResponseDTO> response = barberService.getAllActive(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarberResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BarberRequestDTO dto){
        BarberResponseDTO response = barberService.update(id, dto);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id){
        barberService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
}
