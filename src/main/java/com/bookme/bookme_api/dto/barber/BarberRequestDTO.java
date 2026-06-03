package com.bookme.bookme_api.dto.barber;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BarberRequestDTO {
    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 100)
    private String specialities;

    @NotNull
    private LocalTime workStartTime;

    @NotNull
    private LocalTime workEndTime;




}
