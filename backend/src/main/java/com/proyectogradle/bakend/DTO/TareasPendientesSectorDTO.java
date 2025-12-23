package com.proyectogradle.bakend.DTO;

/**
 * DTO para la lista de sectores y el numero de tareas
 *
 */
public class TareasPendientesSectorDTO {
    private Long id;
    private String nombre;
    private int pendientes;

    public TareasPendientesSectorDTO(Long id, String nombre, int pendientes) {
        this.id = id;
        this.nombre = nombre;
        this.pendientes = pendientes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPendientes() {
        return pendientes;
    }

    public void setPendientes(int pendientes) {
        this.pendientes = pendientes;
    }
}
