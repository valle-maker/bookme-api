package com.bookme.bookme_api.dto.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para que un BARBER registre una cita presencial (walk-in).
 * barberId NO se incluye — el backend lo resuelve desde el JWT.
 */
@Getter
@Setter
public class WalkInRequestDTO {

    @NotNull
    private Long clientId;

    @NotNull
    private Long serviceId;

    @NotNull
    @FutureOrPresent
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime startTime;

    @Size(max = 500)
    private String notes;
}
