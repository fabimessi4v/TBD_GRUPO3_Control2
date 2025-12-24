package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.SectorCantidadDTO;
import com.proyectogradle.bakend.entities.Usuario;
import com.proyectogradle.bakend.repository.NumeroTareasPorUsuarioRepository;
import com.proyectogradle.bakend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/numerotareas")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NumeroTareasPorUsuarioController {

    private final NumeroTareasPorUsuarioRepository numeroTareasPorUsuarioRepository;
    private final UserRepository userRepository;

    public NumeroTareasPorUsuarioController(
            NumeroTareasPorUsuarioRepository numeroTareasPorUsuarioRepository,
            UserRepository userRepository
    )
    {
        this.numeroTareasPorUsuarioRepository = numeroTareasPorUsuarioRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene la cantidad de tareas del usuario autenticado por sector.
     * El usuario se obtiene desde el TOKEN JWT (Authentication),
     * GET /api/numerotareas/cantidadtareas
     */
    @GetMapping("/cantidadtareas")
    public ResponseEntity<List<SectorCantidadDTO>> obtenerCantidadTareas(Authentication authentication) {
        // Username desde el token (JWT validado por Spring Security)
        String username = authentication.getName();

        // Obtener el usuario real desde la BD
        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado en la base de datos"
                ));

        // Ejecutar consulta usando el ID
        List<SectorCantidadDTO> resultado =
                numeroTareasPorUsuarioRepository.obtenerListaTareas(usuario.getId());

        // Retornar respuesta
        return ResponseEntity.ok(resultado);
    }
}
