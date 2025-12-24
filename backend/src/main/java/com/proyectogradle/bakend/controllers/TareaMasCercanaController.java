package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.TareaMasCercanaDTO;
import com.proyectogradle.bakend.entities.Usuario;
import com.proyectogradle.bakend.repository.TareaMasCercanaRepository;
import com.proyectogradle.bakend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarea")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TareaMasCercanaController {

    private final TareaMasCercanaRepository tareaMasCercanaRepository;
    private final UserRepository userRepository;


    public TareaMasCercanaController(
            TareaMasCercanaRepository tareaMasCercanaRepository,
            UserRepository userRepository
    ) {
        this.tareaMasCercanaRepository = tareaMasCercanaRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene la tarea pendiente más cercana a la ubicación del usuario.
     * El usuario se obtiene desde el TOKEN JWT (Authentication)
     * GET /api/tarea/mascercana
     */
    @GetMapping("/mascercana")
    public ResponseEntity<TareaMasCercanaDTO> obtenerTareaMasCercana(
            Authentication authentication
    ) {
        String username = authentication.getName();

        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        TareaMasCercanaDTO resultado =
                tareaMasCercanaRepository.obtenerTareaMasCercana(usuario.getId());

        return ResponseEntity.ok(resultado);
    }
}

