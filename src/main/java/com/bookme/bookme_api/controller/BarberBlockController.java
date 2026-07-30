package com.bookme.bookme_api.controller;

import com.bookme.bookme_api.dto.barberblock.BarberBlockRequestDTO;
import com.bookme.bookme_api.dto.barberblock.BarberBlockResponseDTO;
import com.bookme.bookme_api.service.BarberBlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/barbers/me/blocks")
@RequiredArgsConstructor
public class BarberBlockController {

    private final BarberBlockService barberBlockService;

    @PostMapping
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<BarberBlockResponseDTO> create(
            @Valid @RequestBody BarberBlockRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(barberBlockService.createBlock(userDetails.getUsername(), dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<List<BarberBlockResponseDTO>> getMyBlocks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(barberBlockService.getMyBlocks(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        barberBlockService.deleteBlock(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
