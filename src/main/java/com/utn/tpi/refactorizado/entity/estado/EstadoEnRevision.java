package com.utn.tpi.refactorizado.entity.estado;

import com.utn.tpi.refactorizado.entity.Responsable;
import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.exception.TransicionEstadoInvalidaException;
import com.utn.tpi.refactorizado.exception.ValidacionException;

public class EstadoEnRevision implements EstadoSolicitud {

    @Override
    public String getNombre() {
        return "EN_REVISION";
    }

    @Override
    public void avanzarRevision(Solicitud solicitud, Responsable responsable) {
        throw new TransicionEstadoInvalidaException("La solicitud ya se encuentra en estado EN_REVISION.");
    }

    @Override
    public void aprobar(Solicitud solicitud, String resolucion) {
        solicitud.setResolucion(resolucion);
        solicitud.setEstadoInterno(new EstadoAprobado(resolucion));
    }

    @Override
    public void rechazar(Solicitud solicitud, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ValidacionException("El rechazo exige un motivo justificado.");
        }
        solicitud.setEstadoInterno(new EstadoRechazado(motivo));
    }
}
