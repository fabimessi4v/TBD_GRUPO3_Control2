package com.proyectogradle.bakend.DTO;

public class SectorTopDTO {
    private long sectorId;
    private String sector;
    private int tareasCompletadas5km;

    public SectorTopDTO(long sectorId, String sector, int tareasCompletadas5km) {
        this.sectorId = sectorId;
        this.sector = sector;
        this.tareasCompletadas5km = tareasCompletadas5km;
    }

    public long getSectorId() { return sectorId; }
    public String getSector() { return sector; }
    public int getTareasCompletadas5km() { return tareasCompletadas5km; }
}
