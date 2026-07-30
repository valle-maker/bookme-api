package com.bookme.bookme_api.service;

import com.bookme.bookme_api.dto.barberblock.BarberAvailabilityResponseDTO;
import com.bookme.bookme_api.entity.AppointmentEntity;
import com.bookme.bookme_api.entity.BarberBlockEntity;
import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.enums.Status;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.repository.AppointmentRepository;
import com.bookme.bookme_api.repository.BarberBlockRepository;
import com.bookme.bookme_api.repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberAvailabilityService {

    private final BarberRepository barberRepository;
    private final AppointmentRepository appointmentRepository;
    private final BarberBlockRepository blockRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public BarberAvailabilityResponseDTO getAvailability(Long barberId, LocalDate date) {
        BarberEntity barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(23, 59, 59);

        // Citas programadas del día
        List<AppointmentEntity> appointments = appointmentRepository
                .findByStatusAndStartDateTimeBetween(Status.SCHEDULED, dayStart, dayEnd);

        // Filtrar solo las del barbero en cuestión
        List<BarberAvailabilityResponseDTO.OccupiedSlot> slots = new ArrayList<>();

        for (AppointmentEntity apt : appointments) {
            if (apt.getBarber().getId().equals(barberId)) {
                slots.add(BarberAvailabilityResponseDTO.OccupiedSlot.builder()
                        .startDateTime(apt.getStartDateTime().format(FMT))
                        .endDateTime(apt.getEndDateTime().format(FMT))
                        .type("APPOINTMENT")
                        .label(apt.getService().getName())
                        .build());
            }
        }

        // Bloques del barbero del día
        List<BarberBlockEntity> blocks = blockRepository
                .findByBarberIdAndStartDateTimeBetween(barberId, dayStart, dayEnd);

        for (BarberBlockEntity block : blocks) {
            slots.add(BarberAvailabilityResponseDTO.OccupiedSlot.builder()
                    .startDateTime(block.getStartDateTime().format(FMT))
                    .endDateTime(block.getEndDateTime().format(FMT))
                    .type("BLOCK")
                    .label(block.getReason() != null ? block.getReason() : "No disponible")
                    .build());
        }

        slots.sort(Comparator.comparing(BarberAvailabilityResponseDTO.OccupiedSlot::getStartDateTime));

        return BarberAvailabilityResponseDTO.builder()
                .barberId(barberId)
                .barberName(barber.getUser().getName())
                .workStartTime(barber.getWorkStartTime())
                .workEndTime(barber.getWorkEndTime())
                .occupiedSlots(slots)
                .build();
    }
}
