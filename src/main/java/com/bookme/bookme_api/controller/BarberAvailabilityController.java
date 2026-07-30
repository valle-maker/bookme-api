package com.bookme.bookme_api.controller;

import com.bookme.bookme_api.dto.barberblock.BarberAvailabilityResponseDTO;
import com.bookme.bookme_api.service.BarberAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/barbers")
@RequiredArgsConstructor
public class BarberAvailabilityController {

    private final BarberAvailabilityService availabilityService;

    /**
     * Devuelve horario laboral + slots ocupados (citas + bloqueos) para un barbero en una fecha.
     * Accesible por cualquier usuario autenticado (para el formulario de agendamiento).
     */
    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<BarberAvailabilityResponseDTO> getAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getAvailability(id, date));
    }
}
