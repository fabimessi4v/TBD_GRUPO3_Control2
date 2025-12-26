package com.proyectogradle.bakend.DTO;

public record TareasPorUsuarioSectorDTO(
        String username,
        String sector,
        int tareasRealizadas
) {}

