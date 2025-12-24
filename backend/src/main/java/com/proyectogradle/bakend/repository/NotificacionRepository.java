package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificacionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public List<NotificacionDTO> obtenerTareasFechaVencimiento(Integer idUsuario) {

        String sql = """
            SELECT
             t.titulo,
             t.fecha_vencimiento
            FROM tarea t
            WHERE t.completada = FALSE
            AND t.id_usuario = ?
            AND t.fecha_vencimiento BETWEEN CURRENT_DATE
            AND CURRENT_DATE + INTERVAL '2 days';
        """;

        return jdbcTemplate.query(
                sql,
                new Object[]{ idUsuario },
                (rs, rowNum) ->
                        new NotificacionDTO(
                                rs.getString("titulo"),
                                rs.getDate("fecha_vencimiento").toLocalDate()
                        )
        );
    }
}

