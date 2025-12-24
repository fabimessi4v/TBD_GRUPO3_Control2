package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.NotificacionDTO;
import com.proyectogradle.bakend.entities.Usuario;
import com.proyectogradle.bakend.repository.NotificacionRepository;
import com.proyectogradle.bakend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final UserRepository userRepository;

    public NotificacionController(
            NotificacionRepository notificacionRepository,
            UserRepository userRepository
    ) {
        this.notificacionRepository = notificacionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene notificaciones lógicas de tareas próximas a vencer
     * GET /api/notificaciones/fechavencimiento
     */
    @GetMapping("/fechavencimiento")
    public ResponseEntity<List<NotificacionDTO>> obtenerTareasFechaVencimiento(
            Authentication authentication
    ) {
        String username = authentication.getName();

        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<NotificacionDTO> notificaciones =
                notificacionRepository.obtenerTareasFechaVencimiento(usuario.getId());

        return ResponseEntity.ok(notificaciones);
    }
}