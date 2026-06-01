package com.bookme.bookme_api.dto.barberService;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarberServiceResponseDTO {

    private Long id;

    private String name;

    private String description;

    private int durationMinutes;

    private BigDecimal price;

    private boolean active;
}