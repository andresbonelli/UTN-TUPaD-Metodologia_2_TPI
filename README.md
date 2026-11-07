# Sistema de Gestión de Solicitudes Académicas
### Trabajo Práctico Integrador - Metodología de Sistemas II

---

## 1. Descripción del Proyecto

**Demostración de refactoring de calidad de software**: diagnóstico de deuda técnica, detección y eliminación de code smells, aplicación estricta de principios **SOLID**, incorporación de patrones de diseño (**State Pattern**, **Repository Pattern**) y ejecución de un **refactoring seguro** respaldado por pruebas unitarias.

El sistema modela el flujo de solicitudes académicas universitarias transitando por los estados `PENDIENTE`, `EN_REVISION`, `APROBADA` y `RECHAZADA`.

- `com.utn.tpi.base`: Versión inicial monolítica con problemas de arquitectura.
- `com.utn.tpi.refactorizado`: Versión refactorizada aplicando buenas prácticas.

---

## 2. Tecnologías Utilizadas

- Java 21
- JUnit 5 (Jupiter Engine)

---

## 3. Funcionalidades Principales

1. **Registrar Solicitud:** Alta de trámites académicos con validación estricta de datos (email institucional, legajo, tipo y prioridad).
2. **Asignación de Responsables:** Asignación de docentes o coordinadores por área académica competente.
3. **Control del Ciclo de Vida (Patrón State):**
   - Transición `PENDIENTE -> EN_REVISION` (requiere responsable asignado).
   - Transición `EN_REVISION -> APROBADA` (con registro de resolución).
   - Transición a `RECHAZADA` (con justificación obligatoria).
   - Bloqueo de transiciones ilegales con excepciones de dominio tipadas.
4. **Búsqueda y Filtrado:** Consultas multicriterio por tipo, estado y legajo utilizando objetos de transferencia (`FiltroSolicitudDTO`).
5. **Reportes Estadísticos y Analíticos:** Cálculo de métricas agregadas desacoplado de la capa de presentación mediante `ReporteSolicitudService`.

---

## 4. Estructura de Directorios

```text
gestion-solicitudes-academicas/
├── docs/
│   └── checklist_code_review.md     # Checklist técnico
├── pom.xml                          # Configuración de Maven y dependencias
├── README.md                        # Documentación general
└── src/
    ├── main/java/com/utn/tpi/
    │   ├── base/                    # Versión 1: Código legado
    │   │   ├── Estudiante.java
    │   │   ├── Responsable.java
    │   │   ├── Solicitud.java
    │   │   └── SolicitudManager.java
    │   └── refactorizado/           # Versión 2: Código limpio y SOLID
    │       ├── dominio/             # Entidades Enums
    │       ├── dto/                 # Parameter Objects (DTOs)
    │       ├── estado/              # Patrón State
    │       ├── excepcion/           # Excepciones de dominio
    │       ├── repositorio/         # Patrón Repository (DIP)
    │       └── servicio/            # Servicios de aplicación (SRP)
    └── test/java/com/utn/tpi/
        ├── base/
        │   └── SolicitudManagerTest.java   # Tests de la versión inicial
        └── refactorizado/
            └── SolicitudServiceTest.java   # Tests de la versión refactorizada
```

---

## 5. Compilación y Ejecución de Pruebas


```bash
mvn clean test

mvn clean package
```

---

Visualizar los tags en Git:
```bash
git tag -n
```
