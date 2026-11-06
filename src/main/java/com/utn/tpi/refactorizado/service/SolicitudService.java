package com.utn.tpi.refactorizado.service;

import com.utn.tpi.refactorizado.entity.Estudiante;
import com.utn.tpi.refactorizado.entity.Responsable;
import com.utn.tpi.refactorizado.entity.Solicitud;
import com.utn.tpi.refactorizado.dto.CrearSolicitudDTO;
import com.utn.tpi.refactorizado.dto.FiltroSolicitudDTO;
import com.utn.tpi.refactorizado.exception.SolicitudNoEncontradaException;
import com.utn.tpi.refactorizado.exception.ValidacionException;
import com.utn.tpi.refactorizado.repository.SolicitudRepository;

import java.util.List;

/**
 * Servicio de Aplicacion con Responsabilidad Unica:
 * Orquesta los casos de uso del dominio de Solicitudes Academicas.
 * Aplica Inversion de Dependencias (DIP) inyectando abstracciones.
 */
public class SolicitudService {

    private final SolicitudRepository repository;
    private final NotificadorService notificador;

    public SolicitudService(SolicitudRepository repository, NotificadorService notificador) {
        this.repository = repository;
        this.notificador = notificador;
    }

    public Solicitud registrarSolicitud(CrearSolicitudDTO dto) {
        validarParametrosCreacion(dto);

        if (repository.existePorId(dto.id())) {
            throw new ValidacionException("Ya existe una solicitud con el identificador: " + dto.id());
        }

        Estudiante estudiante = new Estudiante(
                dto.legajoEstudiante(),
                dto.nombreEstudiante(),
                dto.emailEstudiante(),
                dto.carrera()
        );

        Solicitud nueva = new Solicitud(
                dto.id(),
                estudiante,
                dto.tipo(),
                dto.descripcion(),
                dto.prioridad()
        );

        repository.guardar(nueva);

        if (notificador != null) {
            notificador.notificarRegistro(nueva);
        }

        return nueva;
    }

    public void asignarResponsable(String idSolicitud, Responsable responsable) {
        Solicitud solicitud = obtenerSolicitudOError(idSolicitud);
        solicitud.asignarResponsable(responsable);
        repository.guardar(solicitud);
    }

    public void avanzarARevision(String idSolicitud, Responsable responsable) {
        Solicitud solicitud = obtenerSolicitudOError(idSolicitud);
        solicitud.avanzarRevision(responsable);
        repository.guardar(solicitud);

        if (notificador != null) {
            notificador.notificarCambioEstado(solicitud, "Paso a estado EN_REVISION");
        }
    }

    public void aprobarSolicitud(String idSolicitud, String resolucion) {
        Solicitud solicitud = obtenerSolicitudOError(idSolicitud);
        solicitud.aprobar(resolucion);
        repository.guardar(solicitud);

        if (notificador != null) {
            notificador.notificarCambioEstado(solicitud, "Aprobada: " + resolucion);
        }
    }

    public void rechazarSolicitud(String idSolicitud, String motivo) {
        Solicitud solicitud = obtenerSolicitudOError(idSolicitud);
        solicitud.rechazar(motivo);
        repository.guardar(solicitud);

        if (notificador != null) {
            notificador.notificarCambioEstado(solicitud, "Rechazada. Motivo: " + motivo);
        }
    }

    public Solicitud buscarPorId(String id) {
        return obtenerSolicitudOError(id);
    }

    public List<Solicitud> filtrarSolicitudes(FiltroSolicitudDTO filtro) {
        return repository.filtrar(filtro);
    }

    private Solicitud obtenerSolicitudOError(String id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new SolicitudNoEncontradaException(id));
    }

    private void validarParametrosCreacion(CrearSolicitudDTO dto) {
        if (dto == null) {
            throw new ValidacionException("El DTO de creacion no puede ser nulo.");
        }
        if (dto.id() == null || dto.id().trim().isEmpty()) {
            throw new ValidacionException("El identificador de la solicitud es obligatorio.");
        }
        if (dto.tipo() == null) {
            throw new ValidacionException("El tipo de solicitud es obligatorio.");
        }
        if (dto.prioridad() == null) {
            throw new ValidacionException("La prioridad de la solicitud es obligatoria.");
        }
    }
}
