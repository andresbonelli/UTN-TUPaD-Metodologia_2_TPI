package com.utn.tpi.base;

public class Estudiante {
    public String id;
    public String legajo;
    public String nombre;
    public String email;
    public String carrera;

    public Estudiante() {
    }

    public Estudiante(String id, String legajo, String nombre, String email, String carrera) {
        this.id = id;
        this.legajo = legajo;
        this.nombre = nombre;
        this.email = email;
        this.carrera = carrera;
    }
}
