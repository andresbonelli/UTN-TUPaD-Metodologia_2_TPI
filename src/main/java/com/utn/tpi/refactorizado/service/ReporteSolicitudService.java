package com.utn.tpi.refactorizado.service;

import com.utn.tpi.refactorizado.entity.enums.PrioridadSolicitud;
import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.dto.ReporteDTO;
import com.utn.tpi.refactorizado.repository.SolicitudRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio con Responsabilidad Unica (SRP) para analisis estadistico y reportes.
 */
public class ReporteSolicitudService {

    private final SolicitudRepository repository;

    public ReporteSolicitudService(SolicitudRepository repository) {
        this.repository = repository;
    }

    public ReporteDTO calcularMetricas() {
        List<Solicitud> todas = repository.buscarTodas();

        int pendientes = (int) todas.stream().filter(s -> "PENDIENTE".equals(s.getNombreEstado())).count();
        int enRevision = (int) todas.stream().filter(s -> "EN_REVISION".equals(s.getNombreEstado())).count();
        int aprobadas = (int) todas.stream().filter(s -> "APROBADA".equals(s.getNombreEstado())).count();
        int rechazadas = (int) todas.stream().filter(s -> "RECHAZADA".equals(s.getNombreEstado())).count();
        int prioridadAlta = (int) todas.stream().filter(s -> s.getPrioridad() == PrioridadSolicitud.ALTA).count();

        Map<String, Long> porTipo = todas.stream()
                .collect(Collectors.groupingBy(s -> s.getTipo().name(), Collectors.counting()));

        return new ReporteDTO(
                todas.size(),
                pendientes,
                enRevision,
                aprobadas,
                rechazadas,
                prioridadAlta,
                porTipo
        );
    }

    public String generarReporteFormateado() {
        ReporteDTO dto = calcularMetricas();

        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE GENERAL DE SOLICITUDES ACADEMICAS ===\n");
        sb.append("Total Solicitudes: ").append(dto.totalSolicitudes()).append("\n");
        sb.append("Pendientes: ").append(dto.pendientes()).append("\n");
        sb.append("En Revision: ").append(dto.enRevision()).append("\n");
        sb.append("Aprobadas: ").append(dto.aprobadas()).append("\n");
        sb.append("Rechazadas: ").append(dto.rechazadas()).append("\n");
        sb.append("Prioridad Alta: ").append(dto.prioridadAlta()).append("\n");
        sb.append("Distribucion por Tipo: ").append(dto.porTipo()).append("\n");
        sb.append("================================================");

        return sb.toString();
    }
}
