package com.bookme.bookme_api.dto.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentRequestDTO {

    @NotNull
    private Long clientId;

    @NotNull
    private Long barberId;

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