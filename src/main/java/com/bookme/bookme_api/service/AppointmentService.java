package com.bookme.bookme_api.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.appointment.AppointmentRequestDTO;
import com.bookme.bookme_api.dto.appointment.AppointmentResponseDTO;
import com.bookme.bookme_api.dto.appointment.WalkInRequestDTO;
import com.bookme.bookme_api.entity.AppointmentEntity;
import com.bookme.bookme_api.entity.BarberEntity;
import com.bookme.bookme_api.entity.BarberServiceEntity;
import com.bookme.bookme_api.entity.UserEntity;
import com.bookme.bookme_api.enums.Status;
import com.bookme.bookme_api.exception.InvalidOperationException;
import com.bookme.bookme_api.exception.ResourceNotFoundException;
import com.bookme.bookme_api.mapper.AppointmentMapper;
import com.bookme.bookme_api.repository.AppointmentRepository;
import com.bookme.bookme_api.repository.BarberRepository;
import com.bookme.bookme_api.repository.BarberServiceRepository;
import com.bookme.bookme_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final BarberServiceRepository barberServiceRepository;

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        UserEntity userEntity = userRepository.findById(dto.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!userEntity.isActive()) {
            throw new InvalidOperationException("User is not active");
        }

        BarberEntity barberEntity = barberRepository.findById(dto.getBarberId())
            .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        if (!barberEntity.isActive()) {
            throw new InvalidOperationException("Barber is not active");
        }

        BarberServiceEntity barberServiceEntity = barberServiceRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!barberServiceEntity.isActive()) {
            throw new InvalidOperationException("Service is not active");
        }

        LocalDateTime startTime = dto.getAppointmentDate().atTime(dto.getStartTime());
        LocalDateTime endTime = startTime.plusMinutes(barberServiceEntity.getDurationMinutes());

        validateWithinWorkingHours(barberEntity, startTime, endTime);
        validateNoOverlap(barberEntity.getId(), startTime, endTime);

        AppointmentEntity entity = appointmentMapper.toEntity(dto);
        entity.setClient(userEntity);
        entity.setBarber(barberEntity);
        entity.setService(barberServiceEntity);
        entity.setStatus(Status.SCHEDULED);
        entity.setStartDateTime(startTime);
        entity.setEndDateTime(endTime);

        return appointmentMapper.toResponseDTO(appointmentRepository.save(entity));
    }

    public AppointmentResponseDTO getById(Long id) {
        AppointmentEntity entity = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        return appointmentMapper.toResponseDTO(entity);
    }

    public Page<AppointmentResponseDTO> findByBarberIdAndStartDateTimeBetween(
        Long barberId, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        validateDateRange(start, end);
        barberRepository.findById(barberId)
            .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));

        return appointmentRepository
            .findByBarberIdAndStartDateTimeBetween(barberId, start, end, pageable)
            .map(appointmentMapper::toResponseDTO);
    }

    @Transactional
    public void cancel(Long id) {
        AppointmentEntity entity = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        validateCancellable(entity);
        entity.setStatus(Status.CANCELLED);
        appointmentRepository.save(entity);
    }

    @Transactional
    public void cancelMyAppointment(Long id, String email) {
        AppointmentEntity entity = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!entity.getClient().getEmail().equals(email)) {
            throw new InvalidOperationException("You can only cancel your own appointments");
        }

        validateCancellable(entity);
        entity.setStatus(Status.CANCELLED);
        appointmentRepository.save(entity);
    }

    @Transactional
    public void markNoShow(Long id) {
        AppointmentEntity entity = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (entity.getStatus() != Status.SCHEDULED) {
            throw new InvalidOperationException("Only scheduled appointments can be marked as no-show");
        }
        entity.setStatus(Status.NO_SHOW);
        appointmentRepository.save(entity);
    }

    public Page<AppointmentResponseDTO> getByClientEmail(String email, Pageable pageable) {
        return appointmentRepository.findByClientEmail(email, pageable)
            .map(appointmentMapper::toResponseDTO);
    }

    public Page<AppointmentResponseDTO> getByBarberEmail(String email, Pageable pageable) {
        return appointmentRepository.findByBarberUserEmail(email, pageable)
            .map(appointmentMapper::toResponseDTO);
    }

    public Page<AppointmentResponseDTO> getAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
            .map(appointmentMapper::toResponseDTO);
    }

    @Transactional
    public AppointmentResponseDTO createMyAppointment(AppointmentRequestDTO dto, String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!userEntity.isActive()) {
            throw new InvalidOperationException("User is not active");
        }

        BarberEntity barberEntity = barberRepository.findById(dto.getBarberId())
            .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        if (!barberEntity.isActive()) {
            throw new InvalidOperationException("Barber is not active");
        }

        BarberServiceEntity barberServiceEntity = barberServiceRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!barberServiceEntity.isActive()) {
            throw new InvalidOperationException("Service is not active");
        }

        LocalDateTime startTime = dto.getAppointmentDate().atTime(dto.getStartTime());
        LocalDateTime endTime = startTime.plusMinutes(barberServiceEntity.getDurationMinutes());

        validateWithinWorkingHours(barberEntity, startTime, endTime);
        validateNoOverlap(barberEntity.getId(), startTime, endTime);

        AppointmentEntity entity = appointmentMapper.toEntity(dto);
        entity.setClient(userEntity);
        entity.setBarber(barberEntity);
        entity.setService(barberServiceEntity);
        entity.setStatus(Status.SCHEDULED);
        entity.setStartDateTime(startTime);
        entity.setEndDateTime(endTime);

        return appointmentMapper.toResponseDTO(appointmentRepository.save(entity));
    }

    /**
     * Crea una cita presencial (walk-in) iniciada por el barbero autenticado.
     * El barberId se resuelve desde el JWT — el cliente elige quien atiende.
     */
    @Transactional
    public AppointmentResponseDTO createWalkIn(WalkInRequestDTO dto, String barberEmail) {
        BarberEntity barberEntity = barberRepository.findByUserEmail(barberEmail)
            .orElseThrow(() -> new ResourceNotFoundException(
                "No se encontro un perfil de barbero para este usuario"));
        if (!barberEntity.isActive()) {
            throw new InvalidOperationException("Barber is not active");
        }

        UserEntity clientEntity = userRepository.findById(dto.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        if (!clientEntity.isActive()) {
            throw new InvalidOperationException("Client is not active");
        }

        BarberServiceEntity barberServiceEntity = barberServiceRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!barberServiceEntity.isActive()) {
            throw new InvalidOperationException("Service is not active");
        }

        LocalDateTime startTime = dto.getAppointmentDate().atTime(dto.getStartTime());
        LocalDateTime endTime = startTime.plusMinutes(barberServiceEntity.getDurationMinutes());

        validateWithinWorkingHours(barberEntity, startTime, endTime);
        validateNoOverlap(barberEntity.getId(), startTime, endTime);

        AppointmentEntity entity = new AppointmentEntity();
        entity.setClient(clientEntity);
        entity.setBarber(barberEntity);
        entity.setService(barberServiceEntity);
        entity.setStatus(Status.SCHEDULED);
        entity.setStartDateTime(startTime);
        entity.setEndDateTime(endTime);
        entity.setNotes(dto.getNotes());

        return appointmentMapper.toResponseDTO(appointmentRepository.save(entity));
    }

    // ─── Helpers privados ────────────────────────────────────────────────────

    private void validateCancellable(AppointmentEntity entity) {
        if (entity.getStatus() == Status.CANCELLED) {
            throw new InvalidOperationException("Appointment is already cancelled");
        }
        if (entity.getStatus() != Status.SCHEDULED) {
            throw new InvalidOperationException("Only scheduled appointments can be cancelled");
        }
        if (entity.getStartDateTime().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidOperationException("Cannot cancel less than 1 hour before the appointment");
        }
    }

    private void validateWithinWorkingHours(BarberEntity barber,
                                             LocalDateTime start, LocalDateTime end) {
        LocalTime aptStart = start.toLocalTime();
        LocalTime aptEnd = end.toLocalTime();
        if (aptStart.isBefore(barber.getWorkStartTime()) || aptEnd.isAfter(barber.getWorkEndTime())) {
            throw new InvalidOperationException("Appointment is outside barber's working hours");
        }
    }

    private void validateNoOverlap(Long barberId, LocalDateTime start, LocalDateTime end) {
        if (appointmentRepository.existsByBarberIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            barberId, Status.SCHEDULED, end, start)) {
            throw new InvalidOperationException("Barber already has an appointment during this time slot");
        }
    }

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new InvalidOperationException("Start and end date-times cannot be null");
        }
        if (start.isAfter(end)) {
            throw new InvalidOperationException("Start date-time cannot be after end date-time");
        }
    }
}
