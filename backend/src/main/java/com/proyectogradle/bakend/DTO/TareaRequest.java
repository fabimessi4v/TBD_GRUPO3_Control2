package com.proyectogradle.bakend.DTO;

import java.time.LocalDate;

/**
 * DTO para crear/actualizar tareas.
 *
 * Este objeto representa el payload que llega desde el frontend para:
 * - crear una tarea (POST)
 * - actualizar una tarea (PUT)
 *
 * Incluye latitud/longitud para persistir el Point en PostGIS.
 */
public class TareaRequest {
    private String titulo;
    private String descripcion;
    private LocalDate fechaVencimiento;
    private Boolean completada;

    // Relaciones (solo IDs)
    private Integer idUsuario;
    private Long idSector;

    // Ubicación (PostGIS geography(Point,4326))
    private double latitud;
    private double longitud;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getCompletada() {
        return completada;
    }

    public void setCompletada(Boolean completada) {
        this.completada = completada;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdSector() {
        return idSector;
    }

    public void setIdSector(Long idSector) {
        this.idSector = idSector;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}

