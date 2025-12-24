package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.PromedioUbicacionRegistroDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PromedioUbicacionRegistroRepository {
    private final JdbcTemplate jdbcTemplate;

    public PromedioUbicacionRegistroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Obtiene el promedio de tareas de la ubicación de registro del usuario.
     *
     * @param idUsuario ID del usuario autenticado
     * @return promedio de la ubicación con las tareas
     */
    public PromedioUbicacionRegistroDTO obtenerPromedioTareasRegistro(Integer idUsuario) {

        String sql = """
                SELECT
                    AVG(
                        ST_Distance(
                            t.ubicacion,
                            u.ubicacion
                        )
                    ) AS promedio_distancia_metros
                FROM tarea t
                JOIN usuario u ON t.id_usuario = u.id
                WHERE t.completada = TRUE
                  AND u.id = ?;
                """;

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{idUsuario},
                (rs, rowNum) ->
                        new PromedioUbicacionRegistroDTO(
                                rs.getDouble("promedio_distancia_metros")
                        )
        );
    }
}
