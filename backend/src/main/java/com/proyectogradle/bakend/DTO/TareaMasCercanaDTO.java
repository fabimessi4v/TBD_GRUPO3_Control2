package com.proyectogradle.bakend.DTO;

public class TareaMasCercanaDTO {
    private Integer id;
    private String titulo;
    private Double distancia;

    public TareaMasCercanaDTO(Integer id, String titulo, Double distancia) {
        this.id = id;
        this.titulo = titulo;
        this.distancia = distancia;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Double getDistancia() {
        return distancia;
    }
}
