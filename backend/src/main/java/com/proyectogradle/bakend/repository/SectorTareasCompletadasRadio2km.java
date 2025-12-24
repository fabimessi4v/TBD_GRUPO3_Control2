package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.SectorTotalCompletadasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SectorTareasCompletadasRadio2km {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtiene los sectores ordenados por cantidad de tareas completadas dentro de un radio de 2km
     * desde la ubicación del usuario actualmente autenticado (sesión actual).
     *
     * Requiere que exista un registro en la tabla usuario con el username autenticado y con ubicacion.
     *
     * @return Lista con (sector, totalCompletadas).
     */
    public List<SectorTotalCompletadasDTO> findSectoresConTareasCompletadasEnRadio2kmParaUsuarioActual() {
        String username = getCurrentUsername();

        String sql =
                "SELECT " +
                        "  s.nombre AS sector, " +
                        "  COUNT(t.id) AS total_completadas " +
                        "FROM sector s " +
                        "JOIN tarea t ON t.id_sector = s.id " +
                        "JOIN usuario u ON u.username = ? " +
                        "WHERE t.completada = TRUE " +
                        "  AND ST_DWithin(t.ubicacion, u.ubicacion, 2000) " +
                        "GROUP BY s.id, s.nombre " +
                        "ORDER BY total_completadas DESC";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SectorTotalCompletadasDTO(
                        rs.getString("sector"),
                        rs.getLong("total_completadas")
                ),
                username
        );
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
