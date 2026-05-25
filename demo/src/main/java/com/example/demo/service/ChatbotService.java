package com.example.demo.service;

import com.example.demo.entities.Servicio;
import com.example.demo.entities.TipoHabitacion;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.TipoHabitacionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Adaptador del chatbot contra Gemini (Google AI). Construye un system prompt
 * con la info real del hotel (tipos de habitacion, servicios y precios desde la
 * BD) para que el bot pueda responder con datos correctos y no inventar.
 */
@Service
public class ChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotService.class);

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String modelo;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public String responder(List<Map<String, String>> messages) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Hola! El chatbot no esta configurado todavia. Por favor contacta a recepcion al telefono del hotel.";
        }

        try {
            String systemPrompt = construirSystemPrompt();

            Map<String, Object> body = new HashMap<>();
            body.put("system_instruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));

            List<Map<String, Object>> contents = new ArrayList<>();
            for (Map<String, String> m : messages) {
                String rol = "user".equalsIgnoreCase(m.get("role")) ? "user" : "model";
                String texto = m.getOrDefault("content", "");
                contents.add(Map.of(
                    "role", rol,
                    "parts", List.of(Map.of("text", texto))
                ));
            }
            body.put("contents", contents);

            body.put("generationConfig", Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 400
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

            String url = String.format(GEMINI_URL, modelo, apiKey);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ResponseEntity<Map<String, Object>> resp =
                (ResponseEntity) restTemplate.postForEntity(url, req, Map.class);

            return extraerTexto(resp.getBody());
        } catch (Exception e) {
            log.error("Error llamando a Gemini", e);
            return "Disculpa, tuve un problema procesando tu mensaje. Intenta de nuevo o contacta a recepcion.";
        }
    }

    private String construirSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres el asistente virtual de Hotel Praia, un hotel premium frente al mar. ");
        sb.append("Tu objetivo es ayudar a los visitantes a conocer el hotel y guiarlos hacia una reserva. ");
        sb.append("Responde en español, de forma breve (1-3 parrafos cortos), amable y profesional. ");
        sb.append("Si el huesped quiere reservar, invitalo a usar la pagina de reservas del sitio.\n\n");

        sb.append("=== TIPOS DE HABITACION DISPONIBLES ===\n");
        for (TipoHabitacion t : tipoHabitacionRepository.findAll()) {
            sb.append("- ").append(t.getNombre())
              .append(" (capacidad ").append(t.getCapacidad()).append(" personas)")
              .append(": $").append(t.getPrecio()).append(" por noche. ")
              .append(t.getDescripcion() == null ? "" : t.getDescripcion());
            if (t.getAmenities() != null && !t.getAmenities().isBlank()) {
                sb.append(" Amenities: ").append(t.getAmenities()).append(".");
            }
            sb.append("\n");
        }

        sb.append("\n=== SERVICIOS DEL HOTEL ===\n");
        for (Servicio s : servicioRepository.findAll()) {
            sb.append("- ").append(s.getNombre())
              .append(" ($").append(s.getPrecio()).append(" ");
            if (s.getPrecioTipo() != null) sb.append(s.getPrecioTipo().getDisplayName().toLowerCase());
            sb.append(")");
            if (s.getHorario() != null && !s.getHorario().isBlank()) {
                sb.append(", horario: ").append(s.getHorario());
            }
            if (s.getDescripcion() != null && !s.getDescripcion().isBlank()) {
                sb.append(". ").append(s.getDescripcion());
            }
            sb.append("\n");
        }

        sb.append("\n=== REGLAS ===\n");
        sb.append("- No inventes habitaciones ni servicios que no esten en las listas de arriba.\n");
        sb.append("- Si el huesped pregunta por algo que no ofrecemos, dilo amablemente.\n");
        sb.append("- Si pregunta por precios usa los exactos del listado.\n");
        sb.append("- Si quiere reservar, sugierele ir a la seccion 'Reservar' del sitio.\n");
        sb.append("- No respondas temas que no tengan que ver con el hotel.\n");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String extraerTexto(Map<String, Object> respuesta) {
        if (respuesta == null) return "Sin respuesta del modelo.";
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) respuesta.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            log.warn("Gemini respondio sin candidates: {}", respuesta);
            return "No pude generar una respuesta. Intenta reformular la pregunta.";
        }
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        if (content == null) return "Sin contenido en la respuesta.";
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) return "Sin parts.";
        Object text = parts.get(0).get("text");
        return text == null ? "" : text.toString();
    }
}
