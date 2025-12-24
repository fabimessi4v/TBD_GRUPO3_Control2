package com.proyectogradle.bakend.DTO;

public class TareaMasCercanaDTO {
    private Long id;
    private String titulo;
    private Double distancia;

    public TareaMasCercanaDTO(Long id, String titulo, Double distancia) {
        this.id = id;
        this.titulo = titulo;
        this.distancia = distancia;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Double getDistancia() {
        return distancia;
    }
}
