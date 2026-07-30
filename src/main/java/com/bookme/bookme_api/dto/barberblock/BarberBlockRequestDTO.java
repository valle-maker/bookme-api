package com.bookme.bookme_api.dto.barberblock;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BarberBlockRequestDTO {

    @NotNull(message = "La fecha y hora de inicio son requeridas")
    private LocalDateTime startDateTime;

    @NotNull(message = "La fecha y hora de fin son requeridas")
    private LocalDateTime endDateTime;

    private String reason;
}
