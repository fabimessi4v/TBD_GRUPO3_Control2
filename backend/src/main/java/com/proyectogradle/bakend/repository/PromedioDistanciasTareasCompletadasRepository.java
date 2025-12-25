package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.PromedioDistanciasTareasCompletadasDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PromedioDistanciasTareasCompletadasRepository {

    // JdbcTemplate es el componente de Spring para interactuar con la base de datos usando SQL.
    private final JdbcTemplate jdbcTemplate;

    public PromedioDistanciasTareasCompletadasRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Calcula el promedio de distancia (en metros) entre la ubicación del usuario autenticado y
     * la ubicación de sus tareas completadas.
     *
     * Usa el username desde la sesión actual (Spring Security), evitando hardcodear un usuario.
     *
     * @return DTO con username y promedioDistanciaMetros (puede ser null si no hay tareas).
     */
    public PromedioDistanciasTareasCompletadasDTO obtenerPromedioDistanciaTareasCompletadasUsuarioActual() {
        String username = getCurrentUsername();

        String sql =
                "SELECT " +
                        "  u.username, " +
                        "  AVG(ST_Distance(t.ubicacion, u.ubicacion)) AS promedio_distancia_metros " +
                        "FROM tarea t " +
                        "JOIN usuario u ON t.id_usuario = u.id " +
                        "WHERE t.completada = TRUE " +
                        "  AND u.username = ? " +
                        "GROUP BY u.username";

        // query(...) evita tener que manejar excepciones; si no hay filas, devolvemos promedio null.
        return jdbcTemplate.query(sql,
                        (rs, rowNum) -> new PromedioDistanciasTareasCompletadasDTO(
                                rs.getString("username"),
                                (Double) rs.getObject("promedio_distancia_metros")
                        ),
                        username)
                .stream()
                .findFirst()
                .orElse(new PromedioDistanciasTareasCompletadasDTO(username, null));
    }

    /**
     * Extrae el username del contexto de seguridad (sesión actual).
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad.");
        }
        return auth.getName();
    }
}
