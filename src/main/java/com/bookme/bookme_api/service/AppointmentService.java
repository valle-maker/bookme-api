package com.bookme.bookme_api.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.dto.appointment.AppointmentRequestDTO;
import com.bookme.bookme_api.dto.appointment.AppointmentResponseDTO;
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
    public AppointmentResponseDTO create(AppointmentRequestDTO dto){
        UserEntity userEntity = userRepository.findById(dto.getClientId()).
            orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(!userEntity.isActive()){
                throw new InvalidOperationException("User is not active");
            }
        
        BarberEntity barberEntity = barberRepository.findById(dto.getBarberId()).
            orElseThrow(()-> new ResourceNotFoundException("Barber not found"));
        if(!barberEntity.isActive()){
        throw new InvalidOperationException("Barber is not active");
            }
        
        

        BarberServiceEntity barberServiceEntity = barberServiceRepository.findById(dto.getServiceId()).
            orElseThrow(()-> new ResourceNotFoundException("Service not found"));

        
        if(!barberServiceEntity.isActive()){
        throw new InvalidOperationException("Service is not active");
            }
        
        LocalDateTime startTime = dto.getAppointmentDate().atTime(dto.getStartTime());
        LocalDateTime endTime = startTime.plusMinutes(barberServiceEntity.getDurationMinutes());
        
        LocalTime appointmentStart = startTime.toLocalTime(); 
        LocalTime appointmentEnd = endTime.toLocalTime();

        if(appointmentStart.isBefore(barberEntity.getWorkStartTime()) || appointmentEnd.isAfter(barberEntity.getWorkEndTime())){
            throw new InvalidOperationException("Appointment is outside barber's working hours");
        }

        if(appointmentRepository.existsByBarberIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            barberEntity.getId(),
            Status.SCHEDULED,
            endTime,
            startTime)){
                throw new InvalidOperationException("Barber already has an appointment during this time slot");
            }

        AppointmentEntity entity = appointmentMapper.toEntity(dto);
        
        entity.setClient(userEntity);
        entity.setBarber(barberEntity);
        entity.setService(barberServiceEntity);
        entity.setStatus(Status.SCHEDULED);
        entity.setStartDateTime(startTime);
        entity.setEndDateTime(endTime);

        AppointmentEntity created = appointmentRepository.save(entity);
        return appointmentMapper.toResponseDTO(created);

    }
    
    public AppointmentResponseDTO getById(Long id){
        AppointmentEntity entity = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        return appointmentMapper.toResponseDTO(entity);
    }


    public Page<AppointmentResponseDTO> findByBarberIdAndStartDateTimeBetween(
        Long barberId,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable) {

        validateDateRange(start, end);

        barberRepository.findById(barberId)
        .orElseThrow(() ->
            new ResourceNotFoundException("Barber not found"));

        return appointmentRepository
            .findByBarberIdAndStartDateTimeBetween(
             barberId,
                 start,
                end,
                pageable)
            .map(appointmentMapper::toResponseDTO);
        }

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new InvalidOperationException("Start and end date-times cannot be null");
        }
        if (start.isAfter(end)) {
            throw new InvalidOperationException("Start date-time cannot be after end date-time");
        }
    }

    @Transactional
    public void cancel(Long id){
        AppointmentEntity entity = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    
        if(entity.getStatus() == Status.CANCELLED){
            throw new InvalidOperationException("Appointment is already cancelled");
        }
    
        if(entity.getStatus() != Status.SCHEDULED){
            throw new InvalidOperationException("Only scheduled appointments can be cancelled");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentStart = entity.getStartDateTime();
        
        if(appointmentStart.isBefore(now.plusHours(1))){
            throw new InvalidOperationException("Cannot cancel less than 1 hour before the appointment");
        }
    
        entity.setStatus(Status.CANCELLED);
        appointmentRepository.save(entity);
        
    }

}
