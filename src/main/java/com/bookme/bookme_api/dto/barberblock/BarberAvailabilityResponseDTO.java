package com.bookme.bookme_api.dto.barberblock;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter @Builder
public class BarberAvailabilityResponseDTO {

    private Long barberId;
    private String barberName;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private List<OccupiedSlot> occupiedSlots;

    @Getter @Builder
    public static class OccupiedSlot {
        private String startDateTime;
        private String endDateTime;
        private String type;   // "APPOINTMENT" | "BLOCK"
        private String label;  // service name or block reason
    }
}
