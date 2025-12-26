package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.TareaSimpleDTO;
import com.proyectogradle.bakend.DTO.TareasPendientesSectorDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TareasPendientesSectorRepository {
    private final JdbcTemplate jdbcTemplate;

    public TareasPendientesSectorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * contar tareas por sector
     *
     * @param sector y tarea
     * @return List<TareasPendientesSectorDTO>
     */
    public List<TareasPendientesSectorDTO> contarPendientesPorSector() {

        String sql = """
            SELECT
              s.id,
              s.nombre,
              COUNT(*) AS pendientes
            FROM tarea t
            JOIN sector s ON s.id = t.id_sector
            WHERE t.completada = false
            GROUP BY s.id, s.nombre
            ORDER BY pendientes DESC
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new TareasPendientesSectorDTO(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getInt("pendientes")
                )
        );
    }

    /**
     * listar tareas por sector
     *
     * @param sector y tarea
     * @return List<TareaSimpleDTO>
     */
    public List<TareaSimpleDTO> listarPendientesPorSector(Long idSector) {

        String sql = """
            SELECT t.id, t.titulo
            FROM tarea t
            WHERE t.completada = false
              AND t.id_sector = ?
            ORDER BY t.id DESC
          """;

        return jdbcTemplate.query(
                sql,
                new Object[]{idSector},
                (rs, rowNum) -> new TareaSimpleDTO(
                        rs.getLong("id"),
                        rs.getString("titulo")
                )
        );
    }


}
