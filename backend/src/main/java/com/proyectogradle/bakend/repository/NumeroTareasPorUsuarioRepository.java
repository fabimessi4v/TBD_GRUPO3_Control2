package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.SectorCantidadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NumeroTareasPorUsuarioRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtiene la cantidad de tareas que ha realizado el usuario por sector (pendientes).
     *
     * @param id ID del usuario.
     * @return Cantidad de tareas.
     */
    public List<SectorCantidadDTO> obtenerListaTareas(Integer id) {

        String sql = """
                    SELECT 
                        s.nombre AS sector,
                        COUNT(t.id) AS cantidad_tareas
                    FROM tarea t
                    JOIN sector s ON t.id_sector = s.id
                    WHERE t.id_usuario = ?
                      AND t.completada = FALSE
                    GROUP BY s.nombre
                """;

        return jdbcTemplate.query(
                sql,
                new Object[]{id},
                (rs, rowNum) -> new SectorCantidadDTO(
                        rs.getString("sector"),
                        rs.getLong("cantidad_tareas")
                )
        );
    }

}
