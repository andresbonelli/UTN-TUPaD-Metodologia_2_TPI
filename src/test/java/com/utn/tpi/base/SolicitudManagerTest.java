package com.utn.tpi.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests del Sistema Base (Validacion de Comportamiento Inicial)")
class SolicitudManagerTest {

    private SolicitudManager manager;

    @BeforeEach
    void setUp() {
        SolicitudManager.reiniciarBD();
        manager = new SolicitudManager();
    }

    @Test
    @DisplayName("Debe registrar exitosamente una solicitud valida")
    void testRegistrarSolicitudValida() {
        boolean ok = manager.registrarSolicitud(
                "SOL-001", "LEG-12345", "Juan Perez", "juan.perez@alumnos.utn.edu.ar",
                "TUPaD", "HOMOLOGACION", "Homologar Programacion I", "ALTA", null
        );

        assertTrue(ok);
        assertEquals(1, SolicitudManager.solicitudesDB.size());
        Solicitud creada = SolicitudManager.solicitudesDB.getFirst();
        assertEquals("SOL-001", creada.id);
        assertEquals("PENDIENTE", creada.estado);
        assertEquals("SECRETARIA_ACADEMICA", creada.areaResponsable);
    }

    @Test
    @DisplayName("Debe rechazar el registro con datos invalidos")
    void testRechazarRegistroInvalido() {
        boolean emailInvalido = manager.registrarSolicitud(
                "SOL-002", "LEG-12345", "Juan Perez", "email_invalido",
                "TUPaD", "HOMOLOGACION", "Homologar Programacion I", "ALTA", null
        );
        assertFalse(emailInvalido);

        boolean tipoInvalido = manager.registrarSolicitud(
                "SOL-003", "LEG-12345", "Juan Perez", "juan@utn.edu.ar",
                "TUPaD", "TIPO_INEXISTENTE", "Test", "ALTA", null
        );
        assertFalse(tipoInvalido);
    }

    @Test
    @DisplayName("Debe asignar un responsable a la solicitud")
    void testAsignarResponsable() {
        manager.registrarSolicitud(
                "SOL-001", "LEG-12345", "Juan Perez", "juan@utn.edu.ar",
                "TUPaD", "HOMOLOGACION", "Homologar", "ALTA", null
        );

        boolean asignado = manager.asignarResponsable("SOL-001", "PROF-99", "Prof. Gomez", "SECRETARIA_ACADEMICA");
        assertTrue(asignado);

        Solicitud s = SolicitudManager.solicitudesDB.getFirst();
        assertEquals("PROF-99", s.idResponsable);
        assertEquals("SECRETARIA_ACADEMICA", s.areaResponsable);
    }

    @Test
    @DisplayName("Debe respetar el flujo de transicion de estados: PENDIENTE -> EN_REVISION -> APROBADA")
    void testFlujoValidoEstados() {
        manager.registrarSolicitud(
                "SOL-001", "LEG-12345", "Juan Perez", "juan@utn.edu.ar",
                "TUPaD", "HOMOLOGACION", "Homologar", "ALTA", null
        );

        // No debe permitir pasar a EN_REVISION sin responsable
        boolean revisionSinResp = manager.cambiarEstado("SOL-001", "EN_REVISION", null);
        assertFalse(revisionSinResp);

        // Asignar responsable y reintentar
        manager.asignarResponsable("SOL-001", "PROF-99", "Prof. Gomez", "SECRETARIA_ACADEMICA");
        boolean revisionConResp = manager.cambiarEstado("SOL-001", "EN_REVISION", null);
        assertTrue(revisionConResp);

        // Pasar a APROBADA
        boolean aprobada = manager.cambiarEstado("SOL-001", "APROBADA", "Cumple correlativas");
        assertTrue(aprobada);

        Solicitud s = SolicitudManager.solicitudesDB.getFirst();
        assertEquals("APROBADA", s.estado);
    }

    @Test
    @DisplayName("No debe permitir transiciones invalidas como aprobar desde PENDIENTE")
    void testTransicionInvalidaDirecta() {
        manager.registrarSolicitud(
                "SOL-001", "LEG-12345", "Juan Perez", "juan@utn.edu.ar",
                "TUPaD", "HOMOLOGACION", "Homologar", "ALTA", null
        );

        boolean directoAprobada = manager.cambiarEstado("SOL-001", "APROBADA", "Salteo revision");
        assertFalse(directoAprobada);
        assertEquals("PENDIENTE", SolicitudManager.solicitudesDB.getFirst().estado);
    }

    @Test
    @DisplayName("Debe filtrar solicitudes por tipo y estado")
    void testBuscarSolicitudes() {
        manager.registrarSolicitud("SOL-1", "LEG-1", "A", "a@utn.edu.ar", "TUPaD", "HOMOLOGACION", "D1", "ALTA", null);
        manager.registrarSolicitud("SOL-2", "LEG-2", "B", "b@utn.edu.ar", "TUPaD", "CERTIFICADO", "D2", "MEDIA", null);
        manager.registrarSolicitud("SOL-3", "LEG-1", "A", "a@utn.edu.ar", "TUPaD", "HOMOLOGACION", "D3", "BAJA", null);

        List<Solicitud> homologaciones = manager.buscarSolicitudes("HOMOLOGACION", null, null);
        assertEquals(2, homologaciones.size());

        List<Solicitud> estudiante1 = manager.buscarSolicitudes(null, null, "LEG-1");
        assertEquals(2, estudiante1.size());
    }

    @Test
    @DisplayName("Debe generar el reporte general consolidado")
    void testGenerarReporteGeneral() {
        manager.registrarSolicitud("SOL-1", "LEG-1", "A", "a@utn.edu.ar", "TUPaD", "HOMOLOGACION", "D1", "ALTA", null);
        manager.registrarSolicitud("SOL-2", "LEG-2", "B", "b@utn.edu.ar", "TUPaD", "CERTIFICADO", "D2", "ALTA", null);

        String reporte = manager.generarReporteGeneral();
        assertNotNull(reporte);
        assertTrue(reporte.contains("Total Solicitudes: 2"));
        assertTrue(reporte.contains("Pendientes: 2"));
        assertTrue(reporte.contains("Prioridad Alta: 2"));
    }
}
