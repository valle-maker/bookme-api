package com.bookme.bookme_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookme.bookme_api.dto.appointment.AppointmentRequestDTO;
import com.bookme.bookme_api.entity.AppointmentEntity;
import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.entity.BarberServiceEntity;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Role;
import com.bookme.bookme_api.enums.Status;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.mapper.AppointmentMapper;
import com.bookme.bookme_api.repository.AppointmentRepository;
import com.bookme.bookme_api.repository.BarberRepository;
import com.bookme.bookme_api.repository.BarberServiceRepository;
import com.bookme.bookme_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BarberRepository barberRepository;
    @Mock
    private BarberServiceRepository barberServiceRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private UserEntity client;
    private BarberEntity barber;
    private BarberServiceEntity service;
    private AppointmentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        client = new UserEntity();
        client.setId(1L);
        client.setName("Carlos");
        client.setEmail("carlos@mail.com");
        client.setRole(Role.CLIENT);
        client.setActive(true);

        barber = new BarberEntity();
        barber.setId(1L);
        barber.setUser(client);
        barber.setWorkStartTime(LocalTime.of(9, 0));
        barber.setWorkEndTime(LocalTime.of(18, 0));
        barber.setActive(true);

        service = new BarberServiceEntity();
        service.setId(1L);
        service.setName("Haircut");
        service.setDurationMinutes(30);
        service.setPrice(new BigDecimal("25000"));
        service.setActive(true);

        requestDTO = new AppointmentRequestDTO();
        requestDTO.setClientId(1L);
        requestDTO.setBarberId(1L);
        requestDTO.setServiceId(1L);
        requestDTO.setAppointmentDate(LocalDate.of(2026, 7, 15));
        requestDTO.setStartTime(LocalTime.of(10, 0));
    }

    // ===== TEST 1: COMPLETE EXAMPLE =====

    @Test
    void create_ShouldThrowException_WhenBarberHasConflict() {
        // Arrange — set up the scenario
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(barberServiceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(appointmentRepository
            .existsByBarberIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(1L), eq(Status.SCHEDULED), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(true);

        // Act & Assert — call the method and check it throws
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class,
            () -> appointmentService.create(requestDTO)
        );

        assertEquals("Barber already has an appointment during this time slot",
            exception.getMessage());

        // Verify the appointment was never saved
        verify(appointmentRepository, never()).save(any());
    }

    // ===== YOUR TURN — fill these in following the same pattern =====

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        // Arrange — what should findById return for a missing user?
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // Act & Assert — what exception should be thrown?
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
             () -> appointmentService.create(requestDTO));
        
        assertEquals("User not found",
        exception.getMessage());
        // Verify — should save() be called?
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenBarberNotActive() {
        // Arrange — user exists, barber exists but is inactive
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));

        barber.setActive(false);

        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        
        // Act & Assert
        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
            ()-> appointmentService.create(requestDTO));
        
        assertEquals("Barber is not active",
            exception.getMessage());
        
        verify(appointmentRepository, never()).save(any()); 
    }

    @Test
    void create_ShouldThrowException_WhenOutsideWorkHours() {
        // Arrange — set the appointment time to 7:00 AM (before barber's 9:00 AM start)
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(barberServiceRepository.findById(1L)).thenReturn(Optional.of(service));
        requestDTO.setStartTime(LocalTime.of(07, 0));
        
        // Act & Assert
        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
            ()-> appointmentService.create(requestDTO));
        
        assertEquals("Appointment is outside barber's working hours",
            exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancel_ShouldThrowException_WhenLessThanOneHourBefore() {
        // Arrange — create an appointment starting very soon
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setId(1L);
        appointment.setStatus(Status.SCHEDULED);
        appointment.setStartDateTime(
            LocalDateTime.now().plusMinutes(30)
        );
        when(appointmentRepository.findById(1L))
            .thenReturn(Optional.of(appointment));
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class, 
            ()-> appointmentService.cancel(1L));
        // Act & Assert
        assertEquals("Cannot cancel less than 1 hour before the appointment",
        exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancel_ShouldThrowException_WhenAlreadyCancelled() {
        // Arrange — create an appointment with Status.CANCELLED
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setId(1L);
        appointment.setStatus(Status.CANCELLED);
        when(appointmentRepository.findById(1L))
            .thenReturn(Optional.of(appointment));
        // Act & Assert
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class, 
            ()-> appointmentService.cancel(1L));
        assertEquals("Appointment is already cancelled",
        exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }
}