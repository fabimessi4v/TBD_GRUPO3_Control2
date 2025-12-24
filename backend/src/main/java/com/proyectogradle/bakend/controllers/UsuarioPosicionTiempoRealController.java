package com.proyectogradle.bakend.controllers;

import com.proyectogradle.bakend.DTO.MensajeRespuesta;
import com.proyectogradle.bakend.DTO.PosicionTiempoRealRequest;
import com.proyectogradle.bakend.repository.UserRepository;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UsuarioPosicionTiempoRealController {

    private final UserRepository userRepository;

    // GeometryFactory para construir objetos Point (JTS) con SRID 4326.
    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    public UsuarioPosicionTiempoRealController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Inserta o actualiza la posición en tiempo real del usuario autenticado.
     * Endpoint: PUT /api/usuario/posicion-tiempo-real
     */
    @PutMapping("/posicion-tiempo-real")
    public ResponseEntity<?> upsertPosicionTiempoReal(
            @RequestBody PosicionTiempoRealRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeRespuesta("Unauthorized: Full authentication is required to access this resource"));
        }

        // IMPORTANTE: PostGIS/JTS usa (longitud=X, latitud=Y)
        Point posicion = GEOMETRY_FACTORY.createPoint(
                new org.locationtech.jts.geom.Coordinate(request.getLongitud(), request.getLatitud())
        );
        posicion.setSRID(4326);

        try {
            userRepository.upsertPosicionTiempoRealUsuarioActual(posicion);
            return ResponseEntity.ok(new MensajeRespuesta("Posición en tiempo real actualizada correctamente"));
        } catch (IllegalStateException ex) {
            // Ej: usuario autenticado pero no existe en BD
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeRespuesta(ex.getMessage()));
        }
    }
}
