package com.bookme.bookme_api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.bookme.bookme_api.dto.barberService.BarberServiceRequestDTO;
import com.bookme.bookme_api.dto.barberService.BarberServiceResponseDTO;
import com.bookme.bookme_api.service.ServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor

public class BarberServiceController {

    private final ServiceService barberServiceService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<BarberServiceResponseDTO> findById(@PathVariable Long id){
        BarberServiceResponseDTO response = barberServiceService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarberServiceResponseDTO> create(@Valid @RequestBody BarberServiceRequestDTO dto){
        BarberServiceResponseDTO response = barberServiceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<Page<BarberServiceResponseDTO>> getAll(
        Pageable pageable) {

        Page<BarberServiceResponseDTO> response = barberServiceService.getAllActive(pageable);

        return ResponseEntity.ok(response);
    
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarberServiceResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BarberServiceRequestDTO dto ){
        BarberServiceResponseDTO response = barberServiceService.update(id, dto);
        return ResponseEntity.ok(response); 
    } 

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id){
        barberServiceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
    


}
