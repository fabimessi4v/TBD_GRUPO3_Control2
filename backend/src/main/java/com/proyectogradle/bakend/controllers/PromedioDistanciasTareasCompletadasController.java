package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.PromedioDistanciasTareasCompletadasDTO;
import com.proyectogradle.bakend.repository.PromedioDistanciasTareasCompletadasRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promedios")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PromedioDistanciasTareasCompletadasController {

    private final PromedioDistanciasTareasCompletadasRepository promedioDistanciasTareasCompletadasRepository;

    public PromedioDistanciasTareasCompletadasController(
            PromedioDistanciasTareasCompletadasRepository promedioDistanciasTareasCompletadasRepository
    ) {
        this.promedioDistanciasTareasCompletadasRepository = promedioDistanciasTareasCompletadasRepository;
    }

    /**
     * Obtiene el promedio de distancia (metros) entre la ubicación del usuario autenticado
     * y la ubicación de sus tareas completadas.
     * Endpoint: GET /api/promedios/distancia-tareas-completadas
     */
    @GetMapping("/distancia-tareas-completadas")
    public ResponseEntity<PromedioDistanciasTareasCompletadasDTO> getPromedioDistanciaTareasCompletadas() {
        PromedioDistanciasTareasCompletadasDTO dto =
                promedioDistanciasTareasCompletadasRepository.obtenerPromedioDistanciaTareasCompletadasUsuarioActual();
        return ResponseEntity.ok(dto);
    }
}

