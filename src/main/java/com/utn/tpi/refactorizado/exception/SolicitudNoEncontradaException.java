package com.utn.tpi.refactorizado.exception;

public class SolicitudNoEncontradaException extends SolicitudException {
    public SolicitudNoEncontradaException(String id) {
        super("No se encontro la solicitud con identificador: " + id);
    }
}
