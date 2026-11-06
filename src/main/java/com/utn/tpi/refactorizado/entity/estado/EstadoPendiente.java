package com.utn.tpi.refactorizado.entity.estado;

import com.utn.tpi.refactorizado.entity.Responsable;
import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.exception.TransicionEstadoInvalidaException;
import com.utn.tpi.refactorizado.exception.ValidacionException;

public class EstadoPendiente implements EstadoSolicitud {

    @Override
    public String getNombre() {
        return "PENDIENTE";
    }

    @Override
    public void avanzarRevision(Solicitud solicitud, Responsable responsable) {
        if (responsable == null && solicitud.getResponsable() == null) {
            throw new TransicionEstadoInvalidaException("Debe tener un responsable asignado para pasar a EN_REVISION.");
        }
        if (responsable != null) {
            solicitud.asignarResponsable(responsable);
        }
        solicitud.setEstadoInterno(new EstadoEnRevision());
    }

    @Override
    public void aprobar(Solicitud solicitud, String resolucion) {
        throw new TransicionEstadoInvalidaException("Solo se puede aprobar una solicitud en estado EN_REVISION.");
    }

    @Override
    public void rechazar(Solicitud solicitud, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ValidacionException("El rechazo exige un motivo justificado.");
        }
        solicitud.setEstadoInterno(new EstadoRechazado(motivo));
    }
}
