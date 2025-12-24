package com.proyectogradle.bakend.DTO;

/**
 * DTO que representa el promedio de distancia (en metros) entre la ubicación del usuario
 * y las ubicaciones de sus tareas completadas.
 */
public class PromedioDistanciasTareasCompletadasDTO {

    private String username;
    private Double promedioDistanciaMetros;

    public PromedioDistanciasTareasCompletadasDTO() {
    }

    public PromedioDistanciasTareasCompletadasDTO(String username, Double promedioDistanciaMetros) {
        this.username = username;
        this.promedioDistanciaMetros = promedioDistanciaMetros;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getPromedioDistanciaMetros() {
        return promedioDistanciaMetros;
    }

    public void setPromedioDistanciaMetros(Double promedioDistanciaMetros) {
        this.promedioDistanciaMetros = promedioDistanciaMetros;
    }
}

