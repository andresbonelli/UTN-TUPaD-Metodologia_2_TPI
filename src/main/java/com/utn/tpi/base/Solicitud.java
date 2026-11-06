package com.utn.tpi.base;

/**
 * Modelo anemico de datos de la Solicitud (Version Base).
 * Presenta violaciones de encapsulamiento y obsesion por primitivos.
 */
public class Solicitud {
    public String id;
    public String idEstudiante;
    public String tipo; // "HOMOLOGACION", "CERTIFICADO", "PRORROGA", etc.
    public String descripcion;
    public String prioridad; // "ALTA", "MEDIA", "BAJA"
    public String estado; // "PENDIENTE", "EN_REVISION", "APROBADA", "RECHAZADA"
    public String fechaCreacion;
    public String idResponsable;
    public String areaResponsable;
    public String motivoRechazo;

    public Solicitud() {
    }

    public Solicitud(String id, String idEstudiante, String tipo, String descripcion,
                     String prioridad, String estado, String fechaCreacion,
                     String idResponsable, String areaResponsable) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.idResponsable = idResponsable;
        this.areaResponsable = areaResponsable;
    }
}
