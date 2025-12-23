package com.proyectogradle.bakend.DTO;

/**
 * DTO para listar las tareas del sector
 *
 */
public class TareaSimpleDTO {
    private Long id;
    private String titulo;

    public TareaSimpleDTO(Long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
}

