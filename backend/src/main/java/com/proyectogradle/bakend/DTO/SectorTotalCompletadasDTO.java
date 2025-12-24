package com.proyectogradle.bakend.DTO;

/**
 * DTO que representa el total de tareas completadas por sector.
 */
public class SectorTotalCompletadasDTO {

    private String sector;
    private long totalCompletadas;

    public SectorTotalCompletadasDTO() {
    }

    public SectorTotalCompletadasDTO(String sector, long totalCompletadas) {
        this.sector = sector;
        this.totalCompletadas = totalCompletadas;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public long getTotalCompletadas() {
        return totalCompletadas;
    }

    public void setTotalCompletadas(long totalCompletadas) {
        this.totalCompletadas = totalCompletadas;
    }
}

