package com.utn.tpi.refactorizado.entity;

import com.utn.tpi.refactorizado.exception.ValidacionException;

import java.util.Objects;

/**
 * Entidad de dominio con validaciones de invariantes y encapsulamiento.
 */
public class Estudiante {
    private final String legajo;
    private final String nombre;
    private final String email;
    private final String carrera;

    public Estudiante(String legajo, String nombre, String email, String carrera) {
        if (legajo == null || legajo.trim().isEmpty()) {
            throw new ValidacionException("El legajo del estudiante es obligatorio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre del estudiante es obligatorio.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new ValidacionException("El email del estudiante tiene un formato invalido: " + email);
        }
        this.legajo = legajo.trim();
        this.nombre = nombre.trim();
        this.email = email.trim();
        this.carrera = (carrera != null) ? carrera.trim() : "TUPaD";
    }

    public String getLegajo() {
        return legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getCarrera() {
        return carrera;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estudiante that = (Estudiante) o;
        return Objects.equals(legajo, that.legajo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legajo);
    }
}
