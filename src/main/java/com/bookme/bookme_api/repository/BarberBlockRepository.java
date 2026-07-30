package com.bookme.bookme_api.repository;

import com.bookme.bookme_api.entity.BarberBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BarberBlockRepository extends JpaRepository<BarberBlockEntity, Long> {

    List<BarberBlockEntity> findByBarberIdAndStartDateTimeGreaterThanEqualOrderByStartDateTime(
            Long barberId, LocalDateTime from);

    List<BarberBlockEntity> findByBarberIdAndStartDateTimeBetween(
            Long barberId, LocalDateTime start, LocalDateTime end);

    boolean existsByBarberIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long barberId, LocalDateTime end, LocalDateTime start);
}
