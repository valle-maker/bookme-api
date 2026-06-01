package com.bookme.bookme_api.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.AppointmentEntity;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>{
    Optional<AppointmentEntity> findByBarberIdAndStartTimeBetween(Long barberId, LocalDate start, LocalDate end );

}
