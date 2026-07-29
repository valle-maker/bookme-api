package com.bookme.bookme_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${resend.api-key:}")
    private String apiKey;

    private static final String FROM     = "BookMe <onboarding@resend.dev>";
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final HttpClient   httpClient   = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ─── Templates ────────────────────────────────────────────────────────────

    @Async
    public void sendAppointmentConfirmation(
            String clientEmail, String clientName,
            String barberName, String serviceName,
            String date, String startTime, String endTime) {

        send(clientEmail,
            "Cita confirmada — " + serviceName,
            buildHtml("✅ ¡Tu cita está confirmada!",
                "Hola <strong>" + clientName + "</strong>, tu reserva quedó agendada:",
                barberName, serviceName, date, startTime, endTime, "#22c55e"));
    }

    @Async
    public void sendAppointmentCancellation(
            String clientEmail, String clientName,
            String barberName, String serviceName,
            String date, String startTime, String endTime) {

        send(clientEmail,
            "Cita cancelada — " + serviceName,
            buildHtml("❌ Tu cita fue cancelada",
                "Hola <strong>" + clientName + "</strong>, tu reserva ha sido cancelada:",
                barberName, serviceName, date, startTime, endTime, "#ef4444"));
    }

    @Async
    public void sendNoShowNotification(
            String clientEmail, String clientName,
            String barberName, String serviceName,
            String date, String startTime, String endTime) {

        send(clientEmail,
            "Cita marcada como no asistida — " + serviceName,
            buildHtml("⚠️ No se registró tu asistencia",
                "Hola <strong>" + clientName + "</strong>, tu cita fue marcada como no asistida:",
                barberName, serviceName, date, startTime, endTime, "#f97316"));
    }

    @Async
    public void sendAppointmentReminder(
            String clientEmail, String clientName,
            String barberName, String serviceName,
            String date, String startTime, String endTime) {

        send(clientEmail,
            "⏰ Tu cita es en 2 horas — " + serviceName,
            buildHtml("⏰ Recuerda tu cita de hoy",
                "Hola <strong>" + clientName + "</strong>, tu cita empieza en aproximadamente <strong>2 horas</strong>:",
                barberName, serviceName, date, startTime, endTime, "#6366f1"));
    }

    @Async
    public void sendPasswordResetEmail(String clientEmail, String clientName, String resetLink) {
        String body = """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#f4f4f5;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f5;padding:32px 16px;">
                <tr><td align="center">
                  <table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;max-width:560px;">
                    <tr><td style="background:#111827;padding:28px 32px;">
                      <h1 style="margin:0;color:#ffffff;font-size:22px;font-weight:700;">🔐 Recuperar contraseña</h1>
                    </td></tr>
                    <tr><td style="padding:28px 32px;">
                      <p style="margin:0 0 16px;color:#374151;font-size:15px;">
                        Hola <strong>%s</strong>, recibimos una solicitud para restablecer tu contraseña.
                      </p>
                      <p style="margin:0 0 24px;color:#374151;font-size:15px;">
                        El enlace es válido por <strong>1 hora</strong>.
                      </p>
                      <table cellpadding="0" cellspacing="0">
                        <tr>
                          <td style="border-radius:8px;background:#111827;">
                            <a href="%s"
                               style="display:inline-block;padding:14px 28px;color:#ffffff;font-size:15px;font-weight:600;text-decoration:none;border-radius:8px;">
                              Restablecer contraseña
                            </a>
                          </td>
                        </tr>
                      </table>
                      <p style="margin:24px 0 0;color:#6b7280;font-size:13px;">
                        Si no solicitaste este cambio, ignora este mensaje.
                      </p>
                    </td></tr>
                    <tr><td style="padding:16px 32px 28px;border-top:1px solid #f3f4f6;">
                      <p style="margin:0;color:#9ca3af;font-size:12px;text-align:center;">
                        Bookme · Sistema de reservas para barberías
                      </p>
                    </td></tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(clientName, resetLink);
        send(clientEmail, "Recuperar contraseña — Bookme", body);
    }

    // ─── Send (privado, síncrono — ya corre en hilo @Async del caller) ────────

    private void send(String to, String subject, String html) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[Email] RESEND_API_KEY no configurado — omitido: {}", subject);
            return;
        }
        try {
            Map<String, Object> payload = Map.of(
                "from",    FROM,
                "to",      List.of(to),
                "subject", subject,
                "html",    html
            );
            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RESEND_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("[Email] Enviado a {} — status: {} body: {}", to, response.statusCode(), response.body());
        } catch (Exception e) {
            log.error("[Email] Error al enviar a {}: {}", to, e.getMessage());
        }
    }

    // ─── HTML builder ─────────────────────────────────────────────────────────

    private String buildHtml(String title, String intro,
                             String barberName, String serviceName,
                             String date, String startTime, String endTime,
                             String accentColor) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#f4f4f5;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f5;padding:32px 16px;">
                <tr><td align="center">
                  <table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;max-width:560px;">
                    <tr><td style="background:%s;padding:28px 32px;">
                      <h1 style="margin:0;color:#ffffff;font-size:22px;font-weight:700;">%s</h1>
                    </td></tr>
                    <tr><td style="padding:28px 32px;">
                      <p style="margin:0 0 24px;color:#374151;font-size:15px;">%s</p>
                      <table width="100%%" cellpadding="0" cellspacing="0"
                             style="background:#f9fafb;border-radius:8px;border:1px solid #e5e7eb;">
                        <tr><td style="padding:12px 16px;border-bottom:1px solid #e5e7eb;">
                          <span style="color:#6b7280;font-size:12px;text-transform:uppercase;">Barbero</span><br>
                          <strong style="color:#111827;font-size:15px;">%s</strong>
                        </td></tr>
                        <tr><td style="padding:12px 16px;border-bottom:1px solid #e5e7eb;">
                          <span style="color:#6b7280;font-size:12px;text-transform:uppercase;">Servicio</span><br>
                          <strong style="color:#111827;font-size:15px;">%s</strong>
                        </td></tr>
                        <tr><td style="padding:12px 16px;border-bottom:1px solid #e5e7eb;">
                          <span style="color:#6b7280;font-size:12px;text-transform:uppercase;">Fecha</span><br>
                          <strong style="color:#111827;font-size:15px;">%s</strong>
                        </td></tr>
                        <tr><td style="padding:12px 16px;">
                          <span style="color:#6b7280;font-size:12px;text-transform:uppercase;">Horario</span><br>
                          <strong style="color:#111827;font-size:15px;">%s – %s</strong>
                        </td></tr>
                      </table>
                    </td></tr>
                    <tr><td style="padding:16px 32px 28px;border-top:1px solid #f3f4f6;">
                      <p style="margin:0;color:#9ca3af;font-size:12px;text-align:center;">
                        Bookme · Sistema de reservas para barberías
                      </p>
                    </td></tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(accentColor, title, intro, barberName, serviceName, date, startTime, endTime);
    }
}
