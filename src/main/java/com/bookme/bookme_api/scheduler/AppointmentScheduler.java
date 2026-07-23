package com.bookme.bookme_api.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.entity.AppointmentEntity;
import com.bookme.bookme_api.enums.Status;
import com.bookme.bookme_api.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Job que marca como COMPLETED las citas cuyo endDateTime ya paso
 * y siguen en estado SCHEDULED.
 *
 * Se ejecuta cada minuto. Si no hay citas vencidas, no hace nada.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void completeExpiredAppointments() {
        List<AppointmentEntity> expired = appointmentRepository
            .findByStatusAndEndDateTimeBefore(Status.SCHEDULED, LocalDateTime.now());

        if (expired.isEmpty()) {
            return;
        }

        expired.forEach(appointment -> appointment.setStatus(Status.COMPLETED));
        appointmentRepository.saveAll(expired);

        log.info("[Scheduler] {} cita(s) marcada(s) como COMPLETED", expired.size());
    }
}
