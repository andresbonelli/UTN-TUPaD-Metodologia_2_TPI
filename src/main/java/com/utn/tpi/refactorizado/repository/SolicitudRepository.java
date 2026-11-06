package com.utn.tpi.refactorizado.repository;

import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.dto.FiltroSolicitudDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia que permite cumplir con principio de inversion de dependencias (DIP)
 * segregacion de interfaces (ISP).
 */
public interface SolicitudRepository {
    void guardar(Solicitud solicitud);

    Optional<Solicitud> buscarPorId(String id);

    List<Solicitud> buscarTodas();

    List<Solicitud> filtrar(FiltroSolicitudDTO filtro);

    boolean existePorId(String id);

    void limpiar();
}
