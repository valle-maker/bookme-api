package com.bookme.bookme_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookme.bookme_api.dto.appointment.AppointmentRequestDTO;
import com.bookme.bookme_api.dto.appointment.AppointmentResponseDTO;
import com.bookme.bookme_api.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import java.time.LocalDateTime;

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
import org.springframework.web.bind.annotation.RequestParam;






@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'BARBER')")
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id){
        AppointmentResponseDTO response = appointmentService.getById(id);
        return ResponseEntity.ok(response);

    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AppointmentResponseDTO> create(@Valid @RequestBody AppointmentRequestDTO dto){
        AppointmentResponseDTO response = appointmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/barber/{barberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER')")
    public ResponseEntity<Page<AppointmentResponseDTO>> findByBarberAndDateRange(
            @PathVariable Long barberId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            Pageable pageable) {

            Page<AppointmentResponseDTO> response =
            appointmentService.findByBarberIdAndStartDateTimeBetween(
                barberId,
                start,
                end,
                pageable);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id){
        appointmentService.cancel(id);
        return ResponseEntity.noContent().build();
    } 

    
        @GetMapping("/my")
        @PreAuthorize("hasRole('CLIENT')")
        public ResponseEntity<Page<AppointmentResponseDTO>> getMyAppointments(
            Authentication authentication,
            Pageable pageable) {
        String email = authentication.getName(); // = sub del JWT = email
        return ResponseEntity.ok(appointmentService.getByClientEmail(email, pageable));
    }

    @GetMapping("/my-schedule")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<Page<AppointmentResponseDTO>> getMySchedule(
            Authentication authentication,
            Pageable pageable) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.getByBarberEmail(email, pageable));
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAll(pageable));
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> cancelMyAppointment(
        @PathVariable Long id,
        Authentication authentication
        ) {
        appointmentService.cancelMyAppointment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/my")
@PreAuthorize("hasRole('CLIENT')")
public ResponseEntity<AppointmentResponseDTO> createMyAppointment(
        @Valid @RequestBody AppointmentRequestDTO dto,
        Authentication authentication) {

    AppointmentResponseDTO response =
            appointmentService.createMyAppointment(dto, authentication.getName());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
    
}


    

