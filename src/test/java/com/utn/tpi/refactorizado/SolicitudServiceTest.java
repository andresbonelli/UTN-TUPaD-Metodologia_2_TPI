package com.utn.tpi.refactorizado;

import com.utn.tpi.refactorizado.entity.*;
import com.utn.tpi.refactorizado.dto.CrearSolicitudDTO;
import com.utn.tpi.refactorizado.dto.FiltroSolicitudDTO;
import com.utn.tpi.refactorizado.dto.ReporteDTO;
import com.utn.tpi.refactorizado.entity.enums.AreaAcademica;
import com.utn.tpi.refactorizado.entity.enums.PrioridadSolicitud;
import com.utn.tpi.refactorizado.entity.enums.TipoSolicitud;
import com.utn.tpi.refactorizado.exception.TransicionEstadoInvalidaException;
import com.utn.tpi.refactorizado.exception.ValidacionException;
import com.utn.tpi.refactorizado.repository.InMemSolicitudRepository;
import com.utn.tpi.refactorizado.repository.SolicitudRepository;
import com.utn.tpi.refactorizado.service.NotificadorService;
import com.utn.tpi.refactorizado.service.ReporteSolicitudService;
import com.utn.tpi.refactorizado.service.SolicitudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests del Sistema Refactorizado")
class SolicitudServiceTest {

    private SolicitudRepository repository;
    private SolicitudService service;
    private ReporteSolicitudService reporteService;
    private List<String> notificacionesRecibidas;

    @BeforeEach
    void setUp() {
        repository = new InMemSolicitudRepository();
        notificacionesRecibidas = new ArrayList<>();

        // implementacion liviana para verificar notificaciones
        NotificadorService notificadorMock = new NotificadorService() {
            @Override
            public void notificarRegistro(Solicitud solicitud) {
                notificacionesRecibidas.add("REGISTRO:" + solicitud.getId());
            }

            @Override
            public void notificarCambioEstado(Solicitud solicitud, String mensaje) {
                notificacionesRecibidas.add("ESTADO:" + solicitud.getId() + ":" + solicitud.getNombreEstado());
            }
        };

        service = new SolicitudService(repository, notificadorMock);
        reporteService = new ReporteSolicitudService(repository);
    }

    @Test
    @DisplayName("Debe registrar solicitud exitosamente utilizando Parameter Object (DTO)")
    void testRegistrarSolicitudExitosa() {
        CrearSolicitudDTO dto = new CrearSolicitudDTO(
                "SOL-101",
                "LEG-9988",
                "Andres Bonelli",
                "andres.bonelli@alumnos.utn.edu.ar",
                "TUPaD",
                TipoSolicitud.HOMOLOGACION,
                "Homologacion de Algoritmos",
                PrioridadSolicitud.ALTA
        );

        Solicitud creada = service.registrarSolicitud(dto);

        assertNotNull(creada);
        assertEquals("SOL-101", creada.getId());
        assertEquals("PENDIENTE", creada.getNombreEstado());
        assertEquals(AreaAcademica.SECRETARIA_ACADEMICA, creada.getAreaResponsable());
        assertTrue(repository.existePorId("SOL-101"));
        assertEquals(1, notificacionesRecibidas.size());
        assertTrue(notificacionesRecibidas.getFirst().contains("REGISTRO:SOL-101"));
    }

    @Test
    @DisplayName("Debe lanzar ValidacionException ante datos invalidos")
    void testValidacionDatosEstudianteInvalido() {
        // Email invalido
        CrearSolicitudDTO dtoEmailMalo = new CrearSolicitudDTO(
                "SOL-102", "LEG-1", "Carlos", "email_invalido",
                "TUPaD", TipoSolicitud.CERTIFICADO, "Desc", PrioridadSolicitud.MEDIA
        );
        assertThrows(ValidacionException.class, () -> service.registrarSolicitud(dtoEmailMalo));

        // ID duplicado
        CrearSolicitudDTO dtoValido = new CrearSolicitudDTO(
                "SOL-103", "LEG-2", "Maria", "maria@utn.edu.ar",
                "TUPaD", TipoSolicitud.CERTIFICADO, "Desc", PrioridadSolicitud.MEDIA
        );
        service.registrarSolicitud(dtoValido);

        assertThrows(ValidacionException.class, () -> service.registrarSolicitud(dtoValido));
    }

    @Test
    @DisplayName("Patron State: Flujo completo valido PENDIENTE -> EN_REVISION -> APROBADA")
    void testFlujoValidoStatePattern() {
        CrearSolicitudDTO dto = new CrearSolicitudDTO(
                "SOL-201", "LEG-10", "Estudiante Test", "test@utn.edu.ar",
                "TUPaD", TipoSolicitud.PRORROGA, "Prorroga entrega final", PrioridadSolicitud.ALTA
        );
        service.registrarSolicitud(dto);

        Responsable docente = new Responsable("DOC-01", "Prof. Lopez", AreaAcademica.COORDINACION_CARRERA, "lopez@utn.edu.ar");

        // Intentar pasar a revision sin responsable lanza excepcion
        assertThrows(TransicionEstadoInvalidaException.class, () -> service.avanzarARevision("SOL-201", null));

        // Pasar a revision con responsable
        service.avanzarARevision("SOL-201", docente);
        Solicitud enRev = service.buscarPorId("SOL-201");
        assertEquals("EN_REVISION", enRev.getNombreEstado());
        assertEquals("DOC-01", enRev.getResponsable().getId());

        // Aprobar
        service.aprobarSolicitud("SOL-201", "Aprobado plazo extraordinario de 7 dias");
        Solicitud aprobada = service.buscarPorId("SOL-201");
        assertEquals("APROBADA", aprobada.getNombreEstado());
        assertEquals("Aprobado plazo extraordinario de 7 dias", aprobada.getResolucion());
    }

    @Test
    @DisplayName("Patron State: Prohibe aprobar directamente desde PENDIENTE (Transicion Invalida)")
    void testProhibirAprobacionDirecta() {
        CrearSolicitudDTO dto = new CrearSolicitudDTO(
                "SOL-301", "LEG-11", "Estudiante 2", "est2@utn.edu.ar",
                "TUPaD", TipoSolicitud.HOMOLOGACION, "Desc", PrioridadSolicitud.BAJA
        );
        service.registrarSolicitud(dto);

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> service.aprobarSolicitud("SOL-301", "Resolucion apresurada"));
    }

    @Test
    @DisplayName("Patron State: Prohibe rechazar una solicitud que ya fue aprobada")
    void testProhibirRechazoSobreAprobada() {
        CrearSolicitudDTO dto = new CrearSolicitudDTO(
                "SOL-401", "LEG-12", "Estudiante 3", "est3@utn.edu.ar",
                "TUPaD", TipoSolicitud.CERTIFICADO, "Desc", PrioridadSolicitud.MEDIA
        );
        service.registrarSolicitud(dto);

        Responsable resp = new Responsable("RES-1", "Admin", AreaAcademica.ALUMNOS, "admin@utn.edu.ar");
        service.avanzarARevision("SOL-401", resp);
        service.aprobarSolicitud("SOL-401", "Listo");

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> service.rechazarSolicitud("SOL-401", "Rechazo tardio"));
    }

    @Test
    @DisplayName("Filtrado seguro con FiltroSolicitudDTO")
    void testFiltrarSolicitudes() {
        service.registrarSolicitud(new CrearSolicitudDTO("S1", "L1", "A", "a@utn.edu.ar", "TUPaD", TipoSolicitud.HOMOLOGACION, "D1", PrioridadSolicitud.ALTA));
        service.registrarSolicitud(new CrearSolicitudDTO("S2", "L2", "B", "b@utn.edu.ar", "TUPaD", TipoSolicitud.CERTIFICADO, "D2", PrioridadSolicitud.MEDIA));
        service.registrarSolicitud(new CrearSolicitudDTO("S3", "L1", "A", "a@utn.edu.ar", "TUPaD", TipoSolicitud.HOMOLOGACION, "D3", PrioridadSolicitud.BAJA));

        FiltroSolicitudDTO filtroTipo = new FiltroSolicitudDTO(TipoSolicitud.HOMOLOGACION, null, null, null);
        List<Solicitud> homologaciones = service.filtrarSolicitudes(filtroTipo);
        assertEquals(2, homologaciones.size());

        FiltroSolicitudDTO filtroEstudiante = new FiltroSolicitudDTO(null, null, "L1", null);
        List<Solicitud> solicitudesL1 = service.filtrarSolicitudes(filtroEstudiante);
        assertEquals(2, solicitudesL1.size());
    }

    @Test
    @DisplayName("SRP: ReporteSolicitudService calcula metricas analiticas correctamente")
    void testReporteEstadistico() {
        service.registrarSolicitud(new CrearSolicitudDTO("S1", "L1", "A", "a@utn.edu.ar", "TUPaD", TipoSolicitud.HOMOLOGACION, "D1", PrioridadSolicitud.ALTA));
        service.registrarSolicitud(new CrearSolicitudDTO("S2", "L2", "B", "b@utn.edu.ar", "TUPaD", TipoSolicitud.CERTIFICADO, "D2", PrioridadSolicitud.ALTA));

        ReporteDTO metricas = reporteService.calcularMetricas();
        assertEquals(2, metricas.totalSolicitudes());
        assertEquals(2, metricas.pendientes());
        assertEquals(2, metricas.prioridadAlta());

        String reporteTexto = reporteService.generarReporteFormateado();
        assertTrue(reporteTexto.contains("Total Solicitudes: 2"));
        assertTrue(reporteTexto.contains("Pendientes: 2"));
    }
}
