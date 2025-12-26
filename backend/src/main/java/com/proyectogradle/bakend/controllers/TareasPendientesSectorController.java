package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.TareaSimpleDTO;
import com.proyectogradle.bakend.DTO.TareasPendientesSectorDTO;
import com.proyectogradle.bakend.repository.TareasPendientesSectorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarea")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // ajusta al puerto real
public class TareasPendientesSectorController {

    private final TareasPendientesSectorRepository tareaRepository;

    public TareasPendientesSectorController(TareasPendientesSectorRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @GetMapping("/pendientes-por-sector")
    public List<TareasPendientesSectorDTO> pendientesPorSector() {
        return tareaRepository.contarPendientesPorSector();
    }

    @GetMapping("/pendientes-por-sector/{idSector}")
    public List<TareaSimpleDTO> pendientesPorSectorDetalle(@PathVariable Long idSector) {
        return tareaRepository.listarPendientesPorSector(idSector);
    }
}

