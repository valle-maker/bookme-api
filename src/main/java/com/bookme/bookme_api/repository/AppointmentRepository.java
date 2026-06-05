package com.bookme.bookme_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.AppointmentEntity;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>{
    List<AppointmentEntity> findByBarberIdAndStartDateTimeBetween(Long barberId, LocalDateTime start, LocalDateTime end);

}
