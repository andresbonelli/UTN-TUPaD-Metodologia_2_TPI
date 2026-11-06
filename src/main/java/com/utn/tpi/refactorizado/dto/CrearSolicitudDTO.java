package com.utn.tpi.refactorizado.dto;

import com.utn.tpi.refactorizado.entity.enums.PrioridadSolicitud;
import com.utn.tpi.refactorizado.entity.enums.TipoSolicitud;

/**
 * Reemplaza la lista extensa de parametros primitivos sueltos por un objeto de transferencia cohesivo.
 */
public record CrearSolicitudDTO(
        String id,
        String legajoEstudiante,
        String nombreEstudiante,
        String emailEstudiante,
        String carrera,
        TipoSolicitud tipo,
        String descripcion,
        PrioridadSolicitud prioridad
) {
}
