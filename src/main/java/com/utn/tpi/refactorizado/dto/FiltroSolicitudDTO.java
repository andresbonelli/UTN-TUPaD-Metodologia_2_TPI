package com.utn.tpi.refactorizado.dto;

import com.utn.tpi.refactorizado.entity.enums.TipoSolicitud;

public record FiltroSolicitudDTO(
        TipoSolicitud tipo,
        String estado,
        String legajoEstudiante,
        String idResponsable
) {
    public static FiltroSolicitudDTO vacio() {
        return new FiltroSolicitudDTO(null, null, null, null);
    }
}
