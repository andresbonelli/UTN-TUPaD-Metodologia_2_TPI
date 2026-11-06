package com.utn.tpi.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que concentra todas las responsabilidades del sistema base:
 * persistencia en memoria, reglas de negocio, validaciones, control de flujo,
 * formateo de reportes y salida por consola.
 * Ejemplifica los code smells y violaciones a principios SOLID a ser diagnosticados.
 */
public class SolicitudManager {

    // Smell: Persistencia estatica global (acoplamiento fuerte)
    public static List<Solicitud> solicitudesDB = new ArrayList<>();
    public static List<Estudiante> estudiantesDB = new ArrayList<>();
    public static List<Responsable> responsablesDB = new ArrayList<>();

    public static void reiniciarBD() {
        solicitudesDB.clear();
        estudiantesDB.clear();
        responsablesDB.clear();
    }

    /**
     * Smell: Long Method, Long Parameter List, Primitive Obsession, Feature Envy, Magic Strings.
     */
    public boolean registrarSolicitud(String id, String legajoEstudiante, String nombreEstudiante,
                                      String emailEstudiante, String carrera, String tipo,
                                      String descripcion, String prioridad, String idResponsable) {
        // Validacion duplicada de campos vacios
        if (id == null || id.trim().equals("")) {
            System.out.println("ERROR: El ID de la solicitud no puede estar vacio.");
            return false;
        }
        if (legajoEstudiante == null || legajoEstudiante.trim().equals("")) {
            System.out.println("ERROR: El legajo del estudiante no puede estar vacio.");
            return false;
        }
        if (emailEstudiante == null || !emailEstudiante.contains("@") || !emailEstudiante.contains(".")) {
            System.out.println("ERROR: Email de estudiante invalido.");
            return false;
        }
        if (tipo == null || (!tipo.equals("HOMOLOGACION") && !tipo.equals("CERTIFICADO") && !tipo.equals("PRORROGA"))) {
            System.out.println("ERROR: Tipo de solicitud no permitido: " + tipo);
            return false;
        }
        if (prioridad == null || (!prioridad.equals("ALTA") && !prioridad.equals("MEDIA") && !prioridad.equals("BAJA"))) {
            System.out.println("ERROR: Prioridad invalida: " + prioridad);
            return false;
        }

        for (Solicitud s : solicitudesDB) {
            if (s.id.equals(id)) {
                System.out.println("ERROR: Ya existe una solicitud con el ID: " + id);
                return false;
            }
        }

        Estudiante estEncontrado = null;
        for (Estudiante e : estudiantesDB) {
            if (e.legajo.equals(legajoEstudiante)) {
                estEncontrado = e;
                break;
            }
        }
        if (estEncontrado == null) {
            estEncontrado = new Estudiante(legajoEstudiante, legajoEstudiante, nombreEstudiante, emailEstudiante, carrera);
            estudiantesDB.add(estEncontrado);
        }

        // Asignacion de area inicial segun tipo (Smell: Logica repartida)
        String areaAsignada = "";
        if (tipo.equals("HOMOLOGACION")) {
            areaAsignada = "SECRETARIA_ACADEMICA";
        } else if (tipo.equals("CERTIFICADO")) {
            areaAsignada = "ALUMNOS";
        } else if (tipo.equals("PRORROGA")) {
            areaAsignada = "COORDINACION_CARRERA";
        }

        Solicitud nueva = new Solicitud();
        nueva.id = id;
        nueva.idEstudiante = estEncontrado.legajo;
        nueva.tipo = tipo;
        nueva.descripcion = descripcion;
        nueva.prioridad = prioridad;
        nueva.estado = "PENDIENTE"; // Magic String
        nueva.fechaCreacion = "2026-11-06"; // Valor Hardcodeado
        nueva.idResponsable = idResponsable;
        nueva.areaResponsable = areaAsignada;

        solicitudesDB.add(nueva);
        System.out.println("LOG: Solicitud registrada con exito: " + id + " para " + estEncontrado.nombre);
        return true;
    }

    /**
     * Smell: Cascadas de if-else para logica de estados. Violacion directa de OCP.
     */
    public boolean cambiarEstado(String idSolicitud, String nuevoEstado, String motivoOComentario) {
        Solicitud objetivo = null;
        for (Solicitud s : solicitudesDB) {
            if (s.id.equals(idSolicitud)) {
                objetivo = s;
                break;
            }
        }

        if (objetivo == null) {
            System.out.println("ERROR: Solicitud no encontrada con ID: " + idSolicitud);
            return false;
        }

        // Logica procedural de control de transicion de estados
        if (nuevoEstado.equals("EN_REVISION")) {
            if (!objetivo.estado.equals("PENDIENTE")) {
                System.out.println("ERROR: Solo se puede pasar a EN_REVISION desde PENDIENTE. Estado actual: " + objetivo.estado);
                return false;
            }
            if (objetivo.idResponsable == null || objetivo.idResponsable.trim().isEmpty()) {
                System.out.println("ERROR: Debe tener un responsable asignado para pasar a EN_REVISION.");
                return false;
            }
            objetivo.estado = "EN_REVISION";
            System.out.println("LOG: Solicitud " + idSolicitud + " paso a EN_REVISION.");
            return true;
        } else if (nuevoEstado.equals("APROBADA")) {
            if (!objetivo.estado.equals("EN_REVISION")) {
                System.out.println("ERROR: Solo se puede aprobar una solicitud en estado EN_REVISION.");
                return false;
            }
            objetivo.estado = "APROBADA";
            System.out.println("LOG: Solicitud " + idSolicitud + " fue APROBADA exitosamente.");
            return true;
        } else if (nuevoEstado.equals("RECHAZADA")) {
            if (objetivo.estado.equals("APROBADA")) {
                System.out.println("ERROR: No se puede rechazar una solicitud que ya fue aprobada.");
                return false;
            }
            if (motivoOComentario == null || motivoOComentario.trim().isEmpty()) {
                System.out.println("ERROR: El rechazo exige un motivo justificado.");
                return false;
            }
            objetivo.estado = "RECHAZADA";
            objetivo.motivoRechazo = motivoOComentario;
            System.out.println("LOG: Solicitud " + idSolicitud + " RECHAZADA. Motivo: " + motivoOComentario);
            return true;
        } else {
            System.out.println("ERROR: Estado desconocido o no contemplado: " + nuevoEstado);
            return false;
        }
    }

    /**
     * Smell: Asignacion directa con baja cohesion y acoplamiento a prints.
     */
    public boolean asignarResponsable(String idSolicitud, String idResponsable, String nombreResponsable, String area) {
        Solicitud objetivo = null;
        for (Solicitud s : solicitudesDB) {
            if (s.id.equals(idSolicitud)) {
                objetivo = s;
                break;
            }
        }
        if (objetivo == null) {
            System.out.println("ERROR: Solicitud inexistente.");
            return false;
        }

        Responsable resp = null;
        for (Responsable r : responsablesDB) {
            if (r.id.equals(idResponsable)) {
                resp = r;
                break;
            }
        }
        if (resp == null) {
            resp = new Responsable(idResponsable, nombreResponsable, area, idResponsable + "@utn.edu.ar");
            responsablesDB.add(resp);
        }

        objetivo.idResponsable = resp.id;
        objetivo.areaResponsable = resp.area;
        System.out.println("LOG: Responsable " + resp.nombre + " asignado a solicitud " + idSolicitud);
        return true;
    }

    /**
     * Smell: Filtrado con multiples parametros opcionales nulos, logica dispersa.
     */
    public List<Solicitud> buscarSolicitudes(String tipo, String estado, String legajoEstudiante) {
        List<Solicitud> filtradas = new ArrayList<>();
        for (Solicitud s : solicitudesDB) {
            boolean coincide = true;
            if (tipo != null && !tipo.isEmpty() && !s.tipo.equalsIgnoreCase(tipo)) {
                coincide = false;
            }
            if (estado != null && !estado.isEmpty() && !s.estado.equalsIgnoreCase(estado)) {
                coincide = false;
            }
            if (legajoEstudiante != null && !legajoEstudiante.isEmpty() && !s.idEstudiante.equals(legajoEstudiante)) {
                coincide = false;
            }
            if (coincide) {
                filtradas.add(s);
            }
        }
        return filtradas;
    }

    /**
     * Smell: Long Method que mezcla calculo de estadisticas y presentacion (violacion SRP).
     */
    public String generarReporteGeneral() {
        int pendientes = 0;
        int enRevision = 0;
        int aprobadas = 0;
        int rechazadas = 0;
        int prioridadAlta = 0;

        for (Solicitud s : solicitudesDB) {
            if ("PENDIENTE".equals(s.estado)) pendientes++;
            else if ("EN_REVISION".equals(s.estado)) enRevision++;
            else if ("APROBADA".equals(s.estado)) aprobadas++;
            else if ("RECHAZADA".equals(s.estado)) rechazadas++;

            if ("ALTA".equalsIgnoreCase(s.prioridad)) prioridadAlta++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE GENERAL DE SOLICITUDES ACADEMICAS ===\n");
        sb.append("Total Solicitudes: ").append(solicitudesDB.size()).append("\n");
        sb.append("Pendientes: ").append(pendientes).append("\n");
        sb.append("En Revision: ").append(enRevision).append("\n");
        sb.append("Aprobadas: ").append(aprobadas).append("\n");
        sb.append("Rechazadas: ").append(rechazadas).append("\n");
        sb.append("Prioridad Alta: ").append(prioridadAlta).append("\n");
        sb.append("================================================");

        System.out.println(sb.toString());
        return sb.toString();
    }
}
