package com.example.demo.service;

import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.front.url:http://localhost:4200}")
    private String frontUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia el correo de verificacion con un link al front.
     * Si SMTP no esta configurado (MAIL_USERNAME vacio) solo loguea el link
     * y NO falla el registro (modo "dev sin SMTP").
     */
    public void enviarCorreoVerificacion(String destinatario, String nombre, String token) {
        String linkVerificacion = frontUrl + "/verificar?token=" + token;

        // Modo dev sin SMTP: solo log
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("=================================================================");
            log.warn("SMTP NO CONFIGURADO. Link de verificacion (copia y pega):");
            log.warn(linkVerificacion);
            log.warn("=================================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setFrom(fromEmail);
            helper.setSubject("Hotel Praia - Verifica tu cuenta");
            helper.setText(buildHtml(nombre, linkVerificacion), true);

            mailSender.send(message);
            log.info("Correo de verificacion enviado a {}", destinatario);
        } catch (Exception e) {
            // Capturamos Exception (no solo MessagingException) porque Spring envuelve
            // los errores SMTP en RuntimeExceptions (MailAuthenticationException, etc).
            // Si los dejamos propagar, /registro y /recuperar revientan con 500.
            log.error("Error enviando correo a {}: {}", destinatario, e.getMessage());
            log.warn("Link de verificacion (fallback): {}", linkVerificacion);
        }
    }

    private String buildHtml(String nombre, String link) {
        return ""
            + "<div style=\"font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#f5efe3;padding:20px;\">"
            + "  <div style=\"background:#08121a;padding:32px 28px;border-radius:12px;color:#f5efe3;\">"
            + "    <h1 style=\"color:#d6b36a;margin:0 0 16px;font-family:Georgia,serif;\">Hotel Praia</h1>"
            + "    <h2 style=\"color:#fff;margin:0 0 20px;\">Hola " + (nombre == null ? "" : nombre) + ",</h2>"
            + "    <p style=\"color:#cbd5e1;line-height:1.7;\">Gracias por registrarte en Hotel Praia. "
            + "    Para activar tu cuenta y poder reservar, haz click en el siguiente botón:</p>"
            + "    <p style=\"text-align:center;margin:32px 0;\">"
            + "      <a href=\"" + link + "\" "
            + "         style=\"background:linear-gradient(135deg,#d6b36a,#b99243);color:#08121a;"
            + "                padding:14px 32px;border-radius:999px;text-decoration:none;"
            + "                font-weight:600;display:inline-block;\">Verificar mi cuenta</a>"
            + "    </p>"
            + "    <p style=\"color:#94a3b8;font-size:13px;line-height:1.6;\">Si el botón no funciona, copia y pega este link en tu navegador:<br>"
            + "       <a href=\"" + link + "\" style=\"color:#d6b36a;word-break:break-all;\">" + link + "</a></p>"
            + "    <hr style=\"border:none;border-top:1px solid #1e293b;margin:28px 0;\">"
            + "    <p style=\"color:#64748b;font-size:12px;\">Si tú no creaste esta cuenta, puedes ignorar este correo.</p>"
            + "  </div>"
            + "</div>";
    }

    /**
     * Envia el correo con link para restablecer la contraseña.
     * El token expira en 1 hora (el back lo valida en /restablecer).
     */
    public void enviarCorreoRecuperacion(String destinatario, String nombre, String token) {
        String linkRecuperacion = frontUrl + "/restablecer-password?token=" + token;

        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("=================================================================");
            log.warn("SMTP NO CONFIGURADO. Link de recuperacion (copia y pega):");
            log.warn(linkRecuperacion);
            log.warn("=================================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setFrom(fromEmail);
            helper.setSubject("Hotel Praia - Recupera tu contraseña");
            helper.setText(buildHtmlRecuperacion(nombre, linkRecuperacion), true);
            mailSender.send(message);
            log.info("Correo de recuperacion enviado a {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando correo de recuperacion a {}: {}", destinatario, e.getMessage());
            log.warn("Link de recuperacion (fallback): {}", linkRecuperacion);
        }
    }

    private String buildHtmlRecuperacion(String nombre, String link) {
        return ""
            + "<div style=\"font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#f5efe3;padding:20px;\">"
            + "  <div style=\"background:#08121a;padding:32px 28px;border-radius:12px;color:#f5efe3;\">"
            + "    <h1 style=\"color:#d6b36a;margin:0 0 16px;font-family:Georgia,serif;\">Hotel Praia</h1>"
            + "    <h2 style=\"color:#fff;margin:0 0 20px;\">Hola " + (nombre == null ? "" : nombre) + ",</h2>"
            + "    <p style=\"color:#cbd5e1;line-height:1.7;\">Recibimos una solicitud para restablecer la contraseña "
            + "    de tu cuenta. Haz click en el siguiente botón para elegir una nueva:</p>"
            + "    <p style=\"text-align:center;margin:32px 0;\">"
            + "      <a href=\"" + link + "\" "
            + "         style=\"background:linear-gradient(135deg,#d6b36a,#b99243);color:#08121a;"
            + "                padding:14px 32px;border-radius:999px;text-decoration:none;"
            + "                font-weight:600;display:inline-block;\">Restablecer contraseña</a>"
            + "    </p>"
            + "    <p style=\"color:#94a3b8;font-size:13px;line-height:1.6;\">Si el botón no funciona, copia y pega este link en tu navegador:<br>"
            + "       <a href=\"" + link + "\" style=\"color:#d6b36a;word-break:break-all;\">" + link + "</a></p>"
            + "    <p style=\"color:#fbbf24;font-size:13px;margin-top:18px;\">⏱ Este enlace expira en 1 hora.</p>"
            + "    <hr style=\"border:none;border-top:1px solid #1e293b;margin:28px 0;\">"
            + "    <p style=\"color:#64748b;font-size:12px;\">Si tú no solicitaste este cambio, puedes ignorar este correo. "
            + "    Tu contraseña actual seguira funcionando.</p>"
            + "  </div>"
            + "</div>";
    }
}
