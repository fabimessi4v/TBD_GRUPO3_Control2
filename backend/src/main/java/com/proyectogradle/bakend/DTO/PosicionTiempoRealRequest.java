package com.proyectogradle.bakend.DTO;

/**
 * DTO de request para actualizar la posición en tiempo real del usuario.
 *
 * IMPORTANTE: el orden para PostGIS/JTS es (longitud, latitud).
 */
public class PosicionTiempoRealRequest {

    private double latitud;
    private double longitud;

    public PosicionTiempoRealRequest() {
    }

    public PosicionTiempoRealRequest(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
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

