package com.utn.tpi.refactorizado.entity;

import com.utn.tpi.refactorizado.entity.enums.AreaAcademica;
import com.utn.tpi.refactorizado.entity.enums.PrioridadSolicitud;
import com.utn.tpi.refactorizado.entity.enums.TipoSolicitud;
import com.utn.tpi.refactorizado.entity.estado.EstadoPendiente;
import com.utn.tpi.refactorizado.entity.estado.EstadoSolicitud;
import com.utn.tpi.refactorizado.exception.ValidacionException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Aplica encapsulamiento y el patron State para gestionar su ciclo de vida.
 */
public class Solicitud {
    private final String id;
    private final Estudiante estudiante;
    private final TipoSolicitud tipo;
    private final String descripcion;
    private final PrioridadSolicitud prioridad;
    private final LocalDate fechaCreacion;
    private AreaAcademica areaResponsable;
    private Responsable responsable;
    private EstadoSolicitud estado;
    private String resolucion;
    private String motivoRechazo;

    public Solicitud(String id, Estudiante estudiante, TipoSolicitud tipo,
                     String descripcion, PrioridadSolicitud prioridad) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacionException("El identificador de la solicitud no puede estar vacio.");
        }
        if (estudiante == null) {
            throw new ValidacionException("El estudiante solicitante no puede ser nulo.");
        }
        if (tipo == null) {
            throw new ValidacionException("El tipo de solicitud es obligatorio.");
        }
        if (prioridad == null) {
            throw new ValidacionException("La prioridad de la solicitud es obligatoria.");
        }
        this.id = id.trim();
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
        this.prioridad = prioridad;
        this.fechaCreacion = LocalDate.now();
        this.areaResponsable = tipo.getAreaSugerida();
        this.estado = new EstadoPendiente(); // Estado inicial GoF State
    }

    // Delegacion de transiciones al patron State
    public void avanzarRevision(Responsable nuevoResponsable) {
        this.estado.avanzarRevision(this, nuevoResponsable);
    }

    public void aprobar(String detalleResolucion) {
        this.estado.aprobar(this, detalleResolucion);
    }

    public void rechazar(String motivo) {
        this.estado.rechazar(this, motivo);
        this.motivoRechazo = motivo;
    }

    public void asignarResponsable(Responsable responsable) {
        if (responsable == null) {
            throw new ValidacionException("El responsable asignado no puede ser nulo.");
        }
        this.responsable = responsable;
        this.areaResponsable = responsable.getArea();
    }

    // Metodos internos accesibles por el patron State
    public void setEstadoInterno(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public PrioridadSolicitud getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public AreaAcademica getAreaResponsable() {
        return areaResponsable;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getNombreEstado() {
        return estado.getNombre();
    }

    public String getResolucion() {
        return resolucion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solicitud solicitud = (Solicitud) o;
        return Objects.equals(id, solicitud.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
