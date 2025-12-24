package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.PromedioUbicacionRegistroDTO;
import com.proyectogradle.bakend.entities.Usuario;
import com.proyectogradle.bakend.repository.PromedioUbicacionRegistroRepository;
import com.proyectogradle.bakend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promedioubicacion")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class PromedioUbicacionRegistroController {
    private final PromedioUbicacionRegistroRepository promedioUbicacionRegistroRepository;
    private final UserRepository userRepository;

    public PromedioUbicacionRegistroController(PromedioUbicacionRegistroRepository promedioUbicacionRegistroRepository, UserRepository userRepository) {
        this.promedioUbicacionRegistroRepository = promedioUbicacionRegistroRepository;
        this.userRepository = userRepository;
    }


    /**
     * Obtiene lel promedio de la ubicación de las tareas con la ubicación de registro del usuario.
     * GET /api/promedioubicacion
     */
    @GetMapping("/tarea/registro")
    public ResponseEntity<PromedioUbicacionRegistroDTO> obtenerPromedioUbicacionRegistro(
            Authentication authentication
    ) {
        String username = authentication.getName();

        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        PromedioUbicacionRegistroDTO resultado =
                promedioUbicacionRegistroRepository.obtenerPromedioTareasRegistro(usuario.getId());

        return ResponseEntity.ok(resultado);
    }
}
