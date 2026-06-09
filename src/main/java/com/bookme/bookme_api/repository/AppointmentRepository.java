package com.bookme.bookme_api.repository;

import java.time.LocalDateTime;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.AppointmentEntity;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>{
    Page<AppointmentEntity> findByBarberIdAndStartDateTimeBetween(
        Long barberId,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable);
}
