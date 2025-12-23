package com.proyectogradle.bakend.DTO;

public class SectorCantidadDTO {
    private String sector;
    private Long cantidadTareas;

    public SectorCantidadDTO(String sector, Long cantidadTareas) {
        this.sector = sector;
        this.cantidadTareas = cantidadTareas;
    }

    public String getSector() {
        return sector;
    }

    public Long getCantidadTareas() {
        return cantidadTareas;
    }
}
