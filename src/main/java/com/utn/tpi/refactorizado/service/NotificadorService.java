package com.utn.tpi.refactorizado.service;

import com.utn.tpi.refactorizado.entity.Solicitud;

/**
 * Interface para desacoplar el envio de notificaciones.
 */
public interface NotificadorService {
    void notificarRegistro(Solicitud solicitud);
    void notificarCambioEstado(Solicitud solicitud, String mensaje);
}
