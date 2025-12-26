package com.proyectogradle.bakend.entities;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(unique = true, nullable = false, length = 100)
        private String username;

        @Column(name = "password_hash", nullable = false)
        private String passwordHash;

        // Hibernate Spatial necesita saber explícitamente que es una geografía de PostGIS
        @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
        private Point ubicacion;

        @Column(name = "creado_en", updatable = false, insertable = false,
                columnDefinition = "TIMESTAMP DEFAULT NOW()")
        private LocalDateTime creadoEn;

        @Column(name = "posicion_tiempo_real")
        private Point posicion_tiempo_real;

        public Point getPosicion_tiempo_real() {
                return posicion_tiempo_real;
        }

        public void setPosicion_tiempo_real(Point posicion_tiempo_real) {
                this.posicion_tiempo_real = posicion_tiempo_real;
        }
// Getters y Setters

        public Integer getId() {
                return id;
        }
        public void setId(Integer id) {
                this.id = id;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getPasswordHash() {
                return passwordHash;
        }

        public void setPasswordHash(String passwordHash) {
                this.passwordHash = passwordHash;
        }

        public Point getUbicacion() {
                return ubicacion;
        }

        public void setUbicacion(Point ubicacion) {
                this.ubicacion = ubicacion;
        }

        public LocalDateTime getCreadoEn() {
                return creadoEn;
        }

        public void setCreadoEn(LocalDateTime creadoEn) {
                this.creadoEn = creadoEn;
        }
}
