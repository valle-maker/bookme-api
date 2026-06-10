package com.bookme.bookme_api.dto.barber;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarberResponseDTO {

    private Long id;

    private String barberName;

    private String specialities;

    private LocalTime workStartTime;

    private LocalTime workEndTime;

    private boolean active;
}