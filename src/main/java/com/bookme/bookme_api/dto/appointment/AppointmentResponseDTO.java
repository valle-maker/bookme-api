package com.bookme.bookme_api.dto.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.bookme.bookme_api.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentResponseDTO {

    private Long id;

    private Long clientId;
    private String clientName;

    private Long barberId;
    private String barberName;

    private Long serviceId;
    private String serviceName;

    private LocalDate appointmentDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Status status;

    private String notes;

    private LocalDateTime createdAt;
}