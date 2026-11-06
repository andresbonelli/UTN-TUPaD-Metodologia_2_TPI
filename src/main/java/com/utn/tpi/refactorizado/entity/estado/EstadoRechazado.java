package com.utn.tpi.refactorizado.entity.estado;

import com.utn.tpi.refactorizado.entity.Responsable;
import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.exception.TransicionEstadoInvalidaException;

public class EstadoRechazado implements EstadoSolicitud {

    private final String motivo;

    public EstadoRechazado(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String getNombre() {
        return "RECHAZADA";
    }

    @Override
    public void avanzarRevision(Solicitud solicitud, Responsable responsable) {
        throw new TransicionEstadoInvalidaException("No se puede pasar a revision una solicitud que fue rechazada.");
    }

    @Override
    public void aprobar(Solicitud solicitud, String resolucion) {
        throw new TransicionEstadoInvalidaException("No se puede aprobar una solicitud que fue rechazada.");
    }

    @Override
    public void rechazar(Solicitud solicitud, String motivo) {
        throw new TransicionEstadoInvalidaException("La solicitud ya se encuentra rechazada.");
    }
}
