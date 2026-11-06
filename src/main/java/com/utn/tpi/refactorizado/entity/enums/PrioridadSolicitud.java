package com.utn.tpi.refactorizado.entity.enums;

public enum PrioridadSolicitud {
    ALTA,
    MEDIA,
    BAJA;

    public static PrioridadSolicitud desdeTexto(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula.");
        }
        return PrioridadSolicitud.valueOf(texto.trim().toUpperCase());
    }
}
