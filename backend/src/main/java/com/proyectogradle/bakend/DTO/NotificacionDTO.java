package com.proyectogradle.bakend.DTO;

import java.time.LocalDate;

public class NotificacionDTO {
    private String titulo;
    private LocalDate fechaVencimiento;

    public NotificacionDTO(String titulo, LocalDate fechaVencimiento) {
        this.titulo = titulo;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
}
