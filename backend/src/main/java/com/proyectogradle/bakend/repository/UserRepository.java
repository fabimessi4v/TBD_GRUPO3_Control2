package com.proyectogradle.bakend.repository;

import com.proyectogradle.bakend.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import org.locationtech.jts.geom.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Repositorio para la entidad Usuario.
 *
 * Esta clase maneja la interacción con la base de datos para operaciones relacionadas
 * con los usuarios, utilizando JDBC a través de JdbcTemplate.
 *
 * Cada método realiza una consulta SQL específica y mapea los resultados a la entidad Usuario.
 */
@Repository
public class UserRepository {
    // JdbcTemplate es el componente de Spring para interactuar con la base de datos usando SQL.
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper para convertir de ResultSet a Usuario
    // Este objeto mapea los nombres de las columnas de la tabla 'usuarios' a los
    // atributos correspondientes de la clase Usuario.
    private final RowMapper<Usuario> usuarioRowMapper = new RowMapper<Usuario>() {
        @Override
        public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Usuario usuario = new Usuario();
            usuario.setId((int) rs.getLong("id"));
            usuario.setUsername(rs.getString("username"));
            usuario.setPasswordHash(rs.getString("password_hash"));

            Object geom = rs.getObject("ubicacion");
            if (geom instanceof Point) {
                usuario.setUbicacion((Point) geom);
            }


            Timestamp ts = rs.getTimestamp("creado_en");
            if (ts != null) {
                usuario.setCreadoEn(ts.toLocalDateTime());
            }
            return usuario;
        }
    };

    /**
     * Busca un usuario por su email.
     *
     * @param username nombre de usuario del usuario a buscar.
     * @return Optional<Usuario> con el usuario encontrado o vacío si no existe.
     */
    public Optional<Usuario> findByUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE username = ?";
        try {
            Usuario usuario = jdbcTemplate.queryForObject(sql, usuarioRowMapper, username);
            return Optional.ofNullable(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto Usuario con los datos a guardar.
     */
    public void save(Usuario usuario) {
        String sql = "INSERT INTO usuario (username, password_hash, ubicacion) " +
                "VALUES (?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography)";

        Point p = usuario.getUbicacion();

        jdbcTemplate.update(sql,
                usuario.getUsername(),
                usuario.getPasswordHash(),
                p.getX(), // Longitud (Eje X)
                p.getY()  // Latitud (Eje Y)
        );
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario a buscar.
     * @return Optional<Usuario> con el usuario encontrado o vacío si no existe.
     */
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try {
            Usuario usuario = jdbcTemplate.queryForObject(sql, usuarioRowMapper, id);
            return Optional.ofNullable(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Comprueba si existe un usuario con el username dado.
     *
     * @param username nombre de usuario a comprobar.
     * @return true si existe al menos un registro con ese username, false en caso contrario.
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(1) FROM usuarios WHERE username = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return count != null && count > 0;
        } catch (Exception e) {
            // En caso de error (p. ej. problema de conexión o SQL) se retorna false.
            return false;
        }
    }

    /**
     * Inserta o actualiza la posición en tiempo real del usuario autenticado (sesión actual).
     *
     * - Obtiene el username desde el SecurityContext (JWT/Spring Security).
     * - En este proyecto, `usuario.password_hash` es NOT NULL, por lo que NO podemos crear un
     *   "usuario mínimo" sólo con username.
     * - Por eso este método asume que el usuario ya existe (registrado) y sólo actualiza
     *   `posicion_tiempo_real`.
     *
     * @param posicion Punto (longitud=X, latitud=Y) con SRID 4326.
     */
    public void upsertPosicionTiempoRealUsuarioActual(Point posicion) {
        String username = getCurrentUsername();

        int updated = updatePosicionTiempoReal(username, posicion);
        if (updated == 0) {
            throw new IllegalStateException("No existe el usuario '" + username + "' en la tabla usuario. Primero debes registrarlo.");
        }
    }

    /**
     * Actualiza la posición en tiempo real de un usuario existente.
     *
     * @param username nombre de usuario.
     * @param posicion Punto (longitud=X, latitud=Y) con SRID 4326.
     * @return cantidad de filas actualizadas.
     */
    public int updatePosicionTiempoReal(String username, Point posicion) {
        String sql =
                "UPDATE usuario SET posicion_tiempo_real = " +
                "ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography " +
                "WHERE username = ?";

        return jdbcTemplate.update(
                sql,
                posicion.getX(), // Longitud
                posicion.getY(),  // Latitud
                username
        );
    }

    /**
     * Inserta o actualiza la posición en tiempo real del usuario.
     *
     * Esta sobrecarga acepta username explícito (útil para administración o tests).
     *
     * IMPORTANTE: por compatibilidad, este método ahora hace UPDATE sobre usuario existente.
     *
     * @param username nombre de usuario.
     * @param posicion Punto (longitud=X, latitud=Y) con SRID 4326.
     */
    public void upsertPosicionTiempoReal(String username, Point posicion) {
        int updated = updatePosicionTiempoReal(username, posicion);
        if (updated == 0) {
            throw new IllegalStateException("No existe el usuario '" + username + "' en la tabla usuario. Primero debes registrarlo.");
        }
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

