package com.utn.tpi.refactorizado.entity.estado;

import com.utn.tpi.refactorizado.entity.Responsable;
import com.utn.tpi.refactorizado.entity.Solicitud;

/**
 * Interfaz que define el contrato del patron State.
 * Permite que la solicitud altere su comportamiento cuando cambia su estado interno,
 * eliminando condicionales extensos y cumpliendo con proncipio abierto/cerrado (OCP).
 */
public interface EstadoSolicitud {
    String getNombre();

    void avanzarRevision(Solicitud solicitud, Responsable responsable);

    void aprobar(Solicitud solicitud, String resolucion);

    void rechazar(Solicitud solicitud, String motivo);
}
