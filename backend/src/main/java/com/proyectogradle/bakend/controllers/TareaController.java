package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.MensajeRespuesta;
import com.proyectogradle.bakend.DTO.TareaRequest;
import com.proyectogradle.bakend.entities.Sector;
import com.proyectogradle.bakend.entities.Tarea;
import com.proyectogradle.bakend.entities.Usuario;
import com.proyectogradle.bakend.repository.TareaRepository;
import com.proyectogradle.bakend.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tarea")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TareaController {

    private final TareaRepository tareaRepository;
    private final UserRepository userRepository;

    @Autowired
    public TareaController(TareaRepository tareaRepository, UserRepository userRepository) {
        this.tareaRepository = tareaRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene todas las tareas.
     *
     * GET /api/tareas
     */
    @GetMapping
    public ResponseEntity<List<Tarea>> getAll() {
        return ResponseEntity.ok(tareaRepository.findAll());
    }

    /**
     * Obtiene una tarea por ID.
     *
     * GET /api/tareas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(id);
        if (tareaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeRespuesta("Error: Tarea no encontrada"));
        }
        return ResponseEntity.ok(tareaOpt.get());
    }

    /**
     * Crea una nueva tarea.
     *
     * POST /api/tareas
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TareaRequest request, Authentication auth) {
        // 1. Validación de entrada
        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            return ResponseEntity.badRequest().body(new MensajeRespuesta("Error: titulo es requerido"));
        }

        // 2. Manejo de Ubicación PostGIS (Longitud, Latitud)
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point ubicacion = geometryFactory.createPoint(new Coordinate(request.getLongitud(), request.getLatitud()));

        // 3. Mapeo de TareaRequest a Entidad Tarea
        Tarea tarea = new Tarea();
        tarea.setTitulo(request.getTitulo());
        tarea.setDescripcion(request.getDescripcion());
        tarea.setFechaVencimiento(request.getFechaVencimiento());
        tarea.setCompletada(request.getCompletada() != null ? request.getCompletada() : false);
        tarea.setUbicacion(ubicacion);

        // 4. SETEAR USUARIO DESDE EL TOKEN (Seguridad)
        String username = auth.getName();
        Usuario u = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en DB"));
        tarea.setUsuario(u);

        // 5. Setear Sector si viene en el request
        if (request.getIdSector() != null) {
            Sector s = new Sector();
            s.setId(request.getIdSector());
            tarea.setSector(s);
        }

        // 6. Persistir
        tareaRepository.save(tarea);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensajeRespuesta("Tarea creada exitosamente para el usuario: " + username));
    }

    /**
     * Actualiza una tarea existente.
     *
     * PUT /api/tareas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TareaRequest request, Authentication auth) {
        System.out.println("--- DEBUG UPDATE ---");
        System.out.println("ID Tarea: " + id);
        System.out.println("Titulo: " + request.getTitulo());
        System.out.println("Completada (Request): " + request.getCompletada()); // <--- ESTE ES EL CLAVE
        System.out.println("--------------------");
        // 1. Buscar la tarea existente
        Tarea tareaExistente = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // 2. SEGURIDAD: Verificar que el usuario del token es el dueño de la tarea
        String username = auth.getName();
        if (!tareaExistente.getUsuario().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MensajeRespuesta("Error: No tienes permiso para editar esta tarea"));
        }

        // 3. Actualizar campos básicos
        tareaExistente.setTitulo(request.getTitulo());
        tareaExistente.setDescripcion(request.getDescripcion());
        tareaExistente.setFechaVencimiento(request.getFechaVencimiento());
        tareaExistente.setCompletada(request.getCompletada());

        // 4. Actualizar Sector
        if (request.getIdSector() != null) {
            Sector s = new Sector();
            s.setId(request.getIdSector());
            tareaExistente.setSector(s);
        }

        // 5. Actualizar Ubicación PostGIS
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point nuevaUbicacion = geometryFactory.createPoint(new Coordinate(request.getLongitud(), request.getLatitud()));
        tareaExistente.setUbicacion(nuevaUbicacion);

        // 6. Guardar cambios (el método save del repository servirá si hace un UPDATE en SQL)
        tareaRepository.update(tareaExistente);

        return ResponseEntity.ok(new MensajeRespuesta("Tarea actualizada correctamente"));
    }

    /**
     * Elimina una tarea por ID.
     *
     * DELETE /api/tareas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = tareaRepository.deleteById(id);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeRespuesta("Error: Tarea no encontrada"));
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener las tareas pertenecientes al usuario autenticado.
     * El ID del usuario se obtiene del Token JWT (Authentication), no de la URL.
     * * GET /api/tareas/mis-tareas
     */
    @GetMapping("/mis-tareas")
    public ResponseEntity<List<Tarea>> getMisTareas(Authentication authentication) {
        // Extrae el nombre de usuario, directamente del Token JWT validado
        String username = authentication.getName();

        // El servicio se encarga de buscar el ID de ese usuario y filtrar sus tareas
        List<Tarea> tareas = tareaRepository.findByUsername(username);

        // Retornamos 200 OK con la lista de tareas (estará vacía si el usuario no tiene)
        return ResponseEntity.ok(tareas);
    }

}
