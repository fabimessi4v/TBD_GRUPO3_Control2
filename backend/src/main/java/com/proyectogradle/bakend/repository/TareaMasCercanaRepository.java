package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.TareaMasCercanaDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TareaMasCercanaRepository {

    private final JdbcTemplate jdbcTemplate;

    public TareaMasCercanaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     *   Obtiene la tarea pendiente más cercana a la ubicación del usuario.
     *   @param idUsuario ID del usuario autenticado
     *   @return TareaMasCercanaDTO
     *
     *  */
    public TareaMasCercanaDTO obtenerTareaMasCercana(Integer idUsuario) {

        String sql = """
            SELECT
              t.id,
              t.titulo,
              ST_Distance(
                t.ubicacion,
                u.ubicacion
              ) AS distancia
            FROM tarea t
            JOIN usuario u ON u.id = ?
            WHERE t.id_usuario = ?
              AND t.completada = FALSE
            ORDER BY distancia ASC
            LIMIT 1
        """;

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{ idUsuario, idUsuario },
                (rs, rowNum) -> new TareaMasCercanaDTO(
                        rs.getLong("id"),
                        rs.getString("titulo"),
                        rs.getDouble("distancia")
                )
        );
    }
}

