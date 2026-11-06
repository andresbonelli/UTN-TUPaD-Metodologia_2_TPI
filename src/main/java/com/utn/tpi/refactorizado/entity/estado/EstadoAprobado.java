package com.utn.tpi.refactorizado.entity.estado;

import com.utn.tpi.refactorizado.entity.Responsable;
import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.exception.TransicionEstadoInvalidaException;

public class EstadoAprobado implements EstadoSolicitud {

    private final String resolucion;

    public EstadoAprobado(String resolucion) {
        this.resolucion = (resolucion != null) ? resolucion : "Solicitud aprobada satisfactoriamente.";
    }

    public String getResolucion() {
        return resolucion;
    }

    @Override
    public String getNombre() {
        return "APROBADA";
    }

    @Override
    public void avanzarRevision(Solicitud solicitud, Responsable responsable) {
        throw new TransicionEstadoInvalidaException("No se puede pasar a revision una solicitud que ya fue aprobada.");
    }

    @Override
    public void aprobar(Solicitud solicitud, String resolucion) {
        throw new TransicionEstadoInvalidaException("La solicitud ya fue aprobada previamente.");
    }

    @Override
    public void rechazar(Solicitud solicitud, String motivo) {
        throw new TransicionEstadoInvalidaException("No se puede rechazar una solicitud que ya fue aprobada.");
    }
}
