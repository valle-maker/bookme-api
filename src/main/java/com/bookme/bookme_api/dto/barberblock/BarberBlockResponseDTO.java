package com.bookme.bookme_api.dto.barberblock;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class BarberBlockResponseDTO {
    private Long id;
    private Long barberId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String reason;
    private LocalDateTime createdAt;
}
