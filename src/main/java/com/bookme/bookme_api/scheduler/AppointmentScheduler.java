package com.bookme.bookme_api.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bookme.bookme_api.entity.AppointmentEntity;
import com.bookme.bookme_api.enums.Status;
import com.bookme.bookme_api.repository.AppointmentRepository;
import com.bookme.bookme_api.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "CO"));
    private static final DateTimeFormatter TIME_FMT =
        DateTimeFormatter.ofPattern("h:mm a");

    // ─── Auto-completar citas vencidas (cada minuto) ──────────────────────────

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void completeExpiredAppointments() {
        List<AppointmentEntity> expired = appointmentRepository
            .findByStatusAndEndDateTimeBefore(Status.SCHEDULED, LocalDateTime.now());

        if (expired.isEmpty()) return;

        expired.forEach(a -> a.setStatus(Status.COMPLETED));
        appointmentRepository.saveAll(expired);
        log.info("[Scheduler] {} cita(s) marcada(s) como COMPLETED", expired.size());
    }

    // ─── Recordatorio 2 horas antes (cada 30 minutos) ────────────────────────

    /**
     * Busca citas SCHEDULED que empiecen entre 1h45m y 2h15m desde ahora
     * y envía un recordatorio por email.
     *
     * La ventana de 30 minutos coincide con la frecuencia del job, garantizando
     * que cada cita recibe exactamente un recordatorio.
     */
    @Scheduled(fixedRate = 1_800_000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusMinutes(105); // 1h45m
        LocalDateTime windowEnd   = now.plusMinutes(135); // 2h15m

        List<AppointmentEntity> upcoming = appointmentRepository
            .findByStatusAndStartDateTimeBetween(Status.SCHEDULED, windowStart, windowEnd);

        if (upcoming.isEmpty()) return;

        for (AppointmentEntity a : upcoming) {
            try {
                emailService.sendAppointmentReminder(
                    a.getClient().getEmail(),
                    a.getClient().getName(),
                    a.getBarber().getUser().getName(),
                    a.getService().getName(),
                    a.getStartDateTime().format(DATE_FMT),
                    a.getStartDateTime().format(TIME_FMT),
                    a.getEndDateTime().format(TIME_FMT)
                );
            } catch (Exception e) {
                log.error("[Scheduler] Error enviando recordatorio para cita {}: {}", a.getId(), e.getMessage());
            }
        }

        log.info("[Scheduler] {} recordatorio(s) enviado(s)", upcoming.size());
    }
}
