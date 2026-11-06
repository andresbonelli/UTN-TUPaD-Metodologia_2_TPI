package com.utn.tpi.base;

public class Responsable {
    public String id;
    public String nombre;
    public String area; // "SECRETARIA_ACADEMICA", "COORDINACION_CARRERA", "DOCENTES"
    public String email;

    public Responsable() {
    }

    public Responsable(String id, String nombre, String area, String email) {
        this.id = id;
        this.nombre = nombre;
        this.area = area;
        this.email = email;
    }
}
