package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.DTO.SectorTopDTO;
import com.proyectogradle.bakend.DTO.TareasPorUsuarioSectorDTO;
import com.proyectogradle.bakend.entities.Sector;
import com.proyectogradle.bakend.entities.Tarea;
import com.proyectogradle.bakend.entities.Usuario;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class TareaRepository {

    // JdbcTemplate es el componente de Spring para interactuar con la base de datos usando SQL.
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TareaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TareasPorUsuarioSectorDTO> reporteRowMapper =
            (rs, rowNum) -> new TareasPorUsuarioSectorDTO(
                    rs.getString("username"),
                    rs.getString("sector"),
                    (int) rs.getLong("tareas_realizadas")
            );



    private final RowMapper<SectorTopDTO> sectorTopRowMapper = (rs, rowNum) ->
            new SectorTopDTO(
                    rs.getLong("sector_id"),
                    rs.getString("sector"),
                    rs.getInt("tareas_completadas_5km")
            );



    // RowMapper para convertir de ResultSet a Tarea.
    // Este objeto mapea los nombres de las columnas de la tabla 'tarea' a los
    // atributos correspondientes de la clase Tarea.
    // Usa ST_AsText para que la base de datos entregue el punto como String
    String sql = "SELECT id, titulo, descripcion, fecha_vencimiento, completada, id_usuario, id_sector, ST_AsText(ubicacion) as ubicacion_wkt, creada_en FROM tarea ORDER BY id";
    private final RowMapper<Tarea> tareaRowMapper = new RowMapper<Tarea>() {
        @Override
        public Tarea mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tarea tarea = new Tarea();
            tarea.setId(rs.getLong("id"));
            tarea.setTitulo(rs.getString("titulo"));
            tarea.setDescripcion(rs.getString("descripcion"));

            Date fv = rs.getDate("fecha_vencimiento");
            if (fv != null) {
                tarea.setFechaVencimiento(fv.toLocalDate());
            }

            boolean completada = rs.getBoolean("completada");
            tarea.setCompletada(completada);

            // FK: id_usuario e id_sector (mapeamos solo el ID para no hacer joins).
            long idUsuario = rs.getLong("id_usuario");
            if (!rs.wasNull()) {
                Usuario u = new Usuario();
                u.setId((int) idUsuario);
                u.setUsername(rs.getString("username"));
                tarea.setUsuario(u);
            }

            long idSector = rs.getLong("id_sector");
            if (!rs.wasNull()) {
                Sector s = new Sector();
                s.setId(idSector);
                tarea.setSector(s);
            }

            String wkt = rs.getString("ubicacion_wkt");
            if (wkt != null) {
                try {
                    // WKTReader es de la librería JTS
                    tarea.setUbicacion((Point) new WKTReader().read(wkt));
                } catch (Exception e) {
                    tarea.setUbicacion(null);
                }
            }

            Timestamp ts = rs.getTimestamp("creada_en");
            if (ts != null) {
                tarea.setCreadaEn(ts.toLocalDateTime());
            }

            return tarea;
        }
    };

    /**
     * Busca una tarea por su ID.
     *
     * @param id ID de la tarea a buscar.
     * @return Optional<Tarea> con la tarea encontrada o vacío si no existe.
     */
    public Optional<Tarea> findById(Long id) {
        String sql = "SELECT t.id, t.titulo, t.descripcion, t.fecha_vencimiento, t.completada, " +
                "t.id_usuario, u.username, t.id_sector, ST_AsText(t.ubicacion) as ubicacion_wkt, t.creada_en " +
                "FROM tarea t " +
                "INNER JOIN usuario u ON t.id_usuario = u.id " +
                "WHERE t.id = ?";
        try {
            Tarea tarea = jdbcTemplate.queryForObject(sql, tareaRowMapper, id);
            return Optional.ofNullable(tarea);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Lista todas las tareas.
     *
     * @return Lista con todas las tareas de la tabla.
     */
    public List<Tarea> findAll() {
        String sql = "SELECT id, titulo, descripcion, fecha_vencimiento, completada, " +
                "id_usuario, id_sector, ST_AsText(ubicacion) as ubicacion_wkt, creada_en " +
                "FROM tarea ORDER BY id";

        return jdbcTemplate.query(sql, tareaRowMapper);
    }

    /**
     * Guarda una nueva tarea en la base de datos.
     *
     * Importante: 'creada_en' lo setea la BD por defecto (NOW()), por eso no lo insertamos.
     *
     * @param tarea El objeto Tarea con los datos a guardar.
     */
    public void save(Tarea tarea) {
        String sql = "INSERT INTO tarea (titulo, descripcion, fecha_vencimiento, completada, id_usuario, id_sector, ubicacion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography)";

        Point p = tarea.getUbicacion();

        Integer idUsuario = (tarea.getUsuario() != null) ? tarea.getUsuario().getId() : null;
        Long idSector = (tarea.getSector() != null) ? tarea.getSector().getId() : null;

        jdbcTemplate.update(sql,
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getFechaVencimiento(),
                Boolean.TRUE.equals(tarea.getCompletada()),
                idUsuario,
                idSector,
                p.getX(), // Longitud
                p.getY()  // Latitud
        );
    }

    /**
     * Actualiza una tarea existente.
     *
     * @param tarea Tarea con ID y campos a actualizar.
     * @return true si se actualizó 1 fila, false si no existía el ID.
     */
    public boolean update(Tarea tarea) {
        // 1. Quitamos id_usuario del SET para que el dueño sea inmutable
        String sql = "UPDATE tarea SET " +
                "titulo = ?, descripcion = ?, fecha_vencimiento = ?, completada = ?, id_sector = ?, " +
                "ubicacion = ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography " +
                "WHERE id = ?";

        // 2. Manejo seguro del Point para evitar errores si no hay coordenadas
        Point p = tarea.getUbicacion();
        double lon = (p != null) ? p.getX() : 0.0;
        double lat = (p != null) ? p.getY() : 0.0;

        // 3. Obtención segura del ID del sector
        Long idSector = (tarea.getSector() != null) ? tarea.getSector().getId() : null;

        int rows = jdbcTemplate.update(sql,
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getFechaVencimiento(),
                Boolean.TRUE.equals(tarea.getCompletada()),
                idSector, // id_usuario eliminado de aquí
                lon,      // p.getX() seguro
                lat,      // p.getY() seguro
                tarea.getId()
        );

        return rows > 0;
    }

    /**
     * Elimina una tarea por su ID.
     *
     * @param id ID de la tarea a eliminar.
     * @return true si se eliminó 1 fila, false si no existía el ID.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM tarea WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }
    /**
     * Busca todas las tareas que pertenecen a un usuario específico.
     */
    public List<Tarea> findByUsuarioId(Integer idUsuario) {
        String sql = "SELECT id, titulo, descripcion, fecha_vencimiento, completada, " +
                "id_usuario, id_sector, ST_AsText(ubicacion) as ubicacion_wkt, creada_en " +
                "FROM tarea WHERE id_usuario = ? ORDER BY fecha_vencimiento ASC";

        // Pasamos el idUsuario como parámetro para el "?" del SQL
        return jdbcTemplate.query(sql, tareaRowMapper, idUsuario);
    }

    /**
     * Busca tareas filtrando por el username del usuario.
     * Usa una subconsulta para obtener el ID a partir del nombre.
     */
    public List<Tarea> findByUsername(String username) {
        String sql = "SELECT t.id, t.titulo, t.descripcion, t.fecha_vencimiento, t.completada, " +
                "t.id_usuario, " +
                "u.username, " +
                "t.id_sector, ST_AsText(t.ubicacion) as ubicacion_wkt, t.creada_en " +
                "FROM tarea t " +
                "JOIN usuario u ON t.id_usuario = u.id " +
                "WHERE u.username = ? " +
                "ORDER BY t.fecha_vencimiento ASC";

        return jdbcTemplate.query(sql, tareaRowMapper, username);
    }

    /**
     * @return Busca, cuenta y agrupa tareas tanto por usuario como por el sector en que se realizó(solo tareas completadas)
     */

    public List<TareasPorUsuarioSectorDTO> tareasRealizadasPorUsuarioYSector() {

        String sql =
                "SELECT u.username AS username, " +
                        "       s.nombre  AS sector, " +
                        "       COUNT(*)  AS tareas_realizadas " +
                        "FROM tarea t " +
                        "JOIN usuario u ON u.id = t.id_usuario " +
                        "JOIN sector s ON s.id = t.id_sector " +
                        "WHERE t.completada = TRUE " +
                        "GROUP BY u.username, s.nombre " +
                        "ORDER BY u.username, s.nombre";


        return jdbcTemplate.query(sql, reporteRowMapper);
    }






    public SectorTopDTO sectorConMasTareasCompletadasDentro5km(long userId) {

        String sql =
                "SELECT s.id AS sector_id, s.nombre AS sector, COUNT(*) AS tareas_completadas_5km " +
                        "FROM tarea t " +
                        "JOIN sector s ON s.id = t.id_sector " +
                        "WHERE t.completada = TRUE " +
                        "  AND ST_DWithin(t.ubicacion, (SELECT u.ubicacion FROM usuario u WHERE u.id = ?), 5000) " +
                        "GROUP BY s.id, s.nombre " +
                        "ORDER BY tareas_completadas_5km DESC " +
                        "LIMIT 1";

        List<SectorTopDTO> res = jdbcTemplate.query(sql, sectorTopRowMapper, userId);
        return res.isEmpty() ? null : res.get(0);
    }



}




