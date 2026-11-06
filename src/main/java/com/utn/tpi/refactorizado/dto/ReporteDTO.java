package com.utn.tpi.refactorizado.dto;

import java.util.Map;

/**
 * Contiene la informacion analitica del reporte,
 * desacoplando el modelo de datos de su representacion visual (SRP).
 */
public record ReporteDTO(
        int totalSolicitudes,
        int pendientes,
        int enRevision,
        int aprobadas,
        int rechazadas,
        int prioridadAlta,
        Map<String, Long> porTipo
) {
}
