package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.SectorTotalCompletadasDTO;
import com.proyectogradle.bakend.repository.SectorTareasCompletadasRadio2km;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para consultar sectores con tareas completadas dentro de un radio de 2km
 * desde la ubicación del usuario autenticado.
 */
@RestController
@RequestMapping("/api/sectores")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SectorTareasCompletadasRadio2kmController {

    @Autowired
    private SectorTareasCompletadasRadio2km sectorTareasCompletadasRadio2kmRepository;

    /**
     * Retorna los sectores ordenados por cantidad de tareas completadas dentro de un radio de 2km
     * desde la ubicación del usuario autenticado.
     *
     * IMPORTANTE: este endpoint requiere autenticación (Bearer Token JWT)
     *
     * @return Lista de sectores con total de tareas completadas.
     */
    @GetMapping("/tareas-completadas/radio-2km")
    public ResponseEntity<List<SectorTotalCompletadasDTO>> getSectoresTareasCompletadasRadio2km() {
        List<SectorTotalCompletadasDTO> result =
                sectorTareasCompletadasRadio2kmRepository.findSectoresConTareasCompletadasEnRadio2kmParaUsuarioActual();
        return ResponseEntity.ok(result);
    }
}

