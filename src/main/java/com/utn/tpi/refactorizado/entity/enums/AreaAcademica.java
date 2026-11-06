package com.utn.tpi.refactorizado.entity.enums;

public enum AreaAcademica {
    SECRETARIA_ACADEMICA("Secretaria Academica"),
    ALUMNOS("Departamento de Alumnos"),
    COORDINACION_CARRERA("Coordinacion de Carrera"),
    DOCENTES("Cuerpo Docente");

    private final String descripcion;

    AreaAcademica(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
