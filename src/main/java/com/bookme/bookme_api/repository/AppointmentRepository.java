package com.bookme.bookme_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookme.bookme_api.entity.AppointmentEntity;
import com.bookme.bookme_api.enums.Status;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    Page<AppointmentEntity> findByBarberIdAndStartDateTimeBetween(
        Long barberId,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable);

    boolean existsByBarberIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
        Long barberId,
        Status status,
        LocalDateTime newEnd,
        LocalDateTime newStart);

    Page<AppointmentEntity> findByClientEmail(String email, Pageable pageable);

    Page<AppointmentEntity> findByBarberUserEmail(String email, Pageable pageable);

    /** Usado por el scheduler para marcar citas vencidas como COMPLETED. */
    List<AppointmentEntity> findByStatusAndEndDateTimeBefore(Status status, LocalDateTime threshold);
}
