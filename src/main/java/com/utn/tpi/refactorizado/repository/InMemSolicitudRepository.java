package com.utn.tpi.refactorizado.repository;

import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.dto.FiltroSolicitudDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementacion en memoria del repositorio de solicitudes.
 */
public class InMemSolicitudRepository implements SolicitudRepository {

    private final Map<String, Solicitud> almacenamiento = new ConcurrentHashMap<>();

    @Override
    public void guardar(Solicitud solicitud) {
        almacenamiento.put(solicitud.getId(), solicitud);
    }

    @Override
    public Optional<Solicitud> buscarPorId(String id) {
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public List<Solicitud> buscarTodas() {
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public List<Solicitud> filtrar(FiltroSolicitudDTO filtro) {
        if (filtro == null) {
            return buscarTodas();
        }

        return almacenamiento.values().stream()
                .filter(s -> filtro.tipo() == null || s.getTipo() == filtro.tipo())
                .filter(s -> filtro.estado() == null || s.getNombreEstado().equalsIgnoreCase(filtro.estado()))
                .filter(s -> filtro.legajoEstudiante() == null || s.getEstudiante().getLegajo().equals(filtro.legajoEstudiante()))
                .filter(s -> filtro.idResponsable() == null || (s.getResponsable() != null && s.getResponsable().getId().equals(filtro.idResponsable())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorId(String id) {
        return almacenamiento.containsKey(id);
    }

    @Override
    public void limpiar() {
        almacenamiento.clear();
    }
}
