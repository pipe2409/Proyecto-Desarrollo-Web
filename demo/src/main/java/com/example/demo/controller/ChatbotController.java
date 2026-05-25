package com.example.demo.controller;

import com.example.demo.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoint publico (sin login) para el chatbot que aparece en la landing.
 * El front envia el historial completo del chat y devolvemos la siguiente
 * respuesta del asistente.
 */
@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/mensaje")
    public ResponseEntity<Map<String, String>> responder(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages =
            (List<Map<String, String>>) body.getOrDefault("messages", List.of());
        if (messages.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("err", "messages requerido"));
        }
        String reply = chatbotService.responder(messages);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}
