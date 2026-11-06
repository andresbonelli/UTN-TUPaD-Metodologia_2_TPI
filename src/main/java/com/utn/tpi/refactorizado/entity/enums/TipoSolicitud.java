package com.utn.tpi.refactorizado.entity.enums;

public enum TipoSolicitud {
    HOMOLOGACION(AreaAcademica.SECRETARIA_ACADEMICA),
    CERTIFICADO(AreaAcademica.ALUMNOS),
    PRORROGA(AreaAcademica.COORDINACION_CARRERA);

    private final AreaAcademica areaSugerida;

    TipoSolicitud(AreaAcademica areaSugerida) {
        this.areaSugerida = areaSugerida;
    }

    public AreaAcademica getAreaSugerida() {
        return areaSugerida;
    }

    public static TipoSolicitud desdeTexto(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo.");
        }
        return TipoSolicitud.valueOf(texto.trim().toUpperCase());
    }
}
