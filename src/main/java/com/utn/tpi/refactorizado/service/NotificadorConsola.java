package com.utn.tpi.refactorizado.service;

import com.utn.tpi.refactorizado.entity.Solicitud;

public class NotificadorConsola implements NotificadorService {

    @Override
    public void notificarRegistro(Solicitud solicitud) {
        System.out.println("[NOTIFICACION-CONSOLA] Solicitud registrada: " + solicitud.getId()
                + " para " + solicitud.getEstudiante().getNombre()
                + " (" + solicitud.getEstudiante().getEmail() + ")");
    }

    @Override
    public void notificarCambioEstado(Solicitud solicitud, String mensaje) {
        System.out.println("[NOTIFICACION-CONSOLA] Solicitud: " + solicitud.getId()
                + " -> Nuevo Estado: " + solicitud.getNombreEstado()
                + " | Detalle: " + mensaje);
    }
}
