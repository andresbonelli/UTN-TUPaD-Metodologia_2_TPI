package com.utn.tpi.refactorizado.entity;

import com.utn.tpi.refactorizado.entity.enums.AreaAcademica;
import com.utn.tpi.refactorizado.exception.ValidacionException;

import java.util.Objects;

/**
 * Entidad de dominio que representa al responsable asignado a la gestion de solicitudes.
 */
public class Responsable {
    private final String id;
    private final String nombre;
    private final AreaAcademica area;
    private final String email;

    public Responsable(String id, String nombre, AreaAcademica area, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacionException("El identificador del responsable es obligatorio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre del responsable es obligatorio.");
        }
        this.id = id.trim();
        this.nombre = nombre.trim();
        this.area = (area != null) ? area : AreaAcademica.SECRETARIA_ACADEMICA;
        this.email = (email != null && !email.trim().isEmpty()) ? email.trim() : id + "@utn.edu.ar";
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public AreaAcademica getArea() {
        return area;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Responsable that = (Responsable) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
