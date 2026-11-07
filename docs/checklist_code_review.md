# Checklist de Code Review

---

## 1. Criterios de Evaluación

| Categoría | Criterio de Inspección | Estado Sistema Base | Estado Sistema Refactorizado | Observaciones Técnicas |
| :--- | :--- | :---: | :---: | :--- |
| **Arquitectura & SOLID** | **SRP (Single Responsibility)**: Cada clase tiene una sola razón de cambio. | ❌ Rechazado | ✅ Aprobado | En la versión base `SolicitudManager` acumulaba persistencia, reglas, reportes y formateo I/O. En la refactorizada se dividió en `Solicitud`, `SolicitudService`, `ReporteSolicitudService` y `InMemSolicitudRepository`. |
| **Arquitectura & SOLID** | **OCP (Open-Closed)**: El sistema es extensible sin modificar código existente. | ❌ Rechazado | ✅ Aprobado | El switch procedural de estados violaba OCP. Se resolvió con el patrón GoF **State** (`EstadoSolicitud`), permitiendo añadir nuevos estados sin modificar clases previas. |
| **Arquitectura & SOLID** | **LSP (Liskov Substitution)**: Las subclases/estados respetan los contratos sin corromper el modelo. | ❌ Rechazado | ✅ Aprobado | Todas las implementaciones de `EstadoSolicitud` cumplen la interfaz y disparan `TransicionEstadoInvalidaException` ante flujos ilegales. |
| **Arquitectura & SOLID** | **ISP (Interface Segregation)**: Interfaces cohesivas y específicas. | ⚠️ No aplica | ✅ Aprobado | Se crearon contratos atómicos: `SolicitudRepository`, `NotificadorService` y `EstadoSolicitud`. |
| **Arquitectura & SOLID** | **DIP (Dependency Inversion)**: Los módulos de alto nivel dependen de abstracciones. | ❌ Rechazado | ✅ Aprobado | `SolicitudService` depende de `SolicitudRepository` y `NotificadorService`, inyectadas por constructor. |
| **Clean Code** | **Nombres Significativos**: Intención clara sin abreviaturas crípticas. | ⚠️ Parcial | ✅ Aprobado | Nomenclatura expresiva de métodos de dominio (`avanzarRevision`, `aprobar`, `rechazar`). |
| **Clean Code** | **Long Methods & Nesting**: Métodos atómicos de bajo anidamiento. | ❌ Rechazado | ✅ Aprobado | Métodos reducidos a menos de 15 líneas mediante *Extract Method*. |
| **Clean Code** | **Primitive Obsession & Data Clumps**: Uso de tipos de dominio y DTOs. | ❌ Rechazado | ✅ Aprobado | Reemplazados por `CrearSolicitudDTO`, `FiltroSolicitudDTO` y enums con comportamiento (`TipoSolicitud`, `PrioridadSolicitud`, `AreaAcademica`). |
| **Manejo de Errores** | **Excepciones de Dominio Tipadas**: No tragar excepciones ni usar strings de error. | ❌ Rechazado | ✅ Aprobado | Se erradicó el retorno de `false` o `"ERROR:"`. Se crearon `SolicitudNoEncontradaException`, `TransicionEstadoInvalidaException` y `ValidacionException`. |
| **Testing & Seguridad** | **Refactoring Seguro**: Suite de pruebas unitarias automatizadas. | ⚠️ Sin tests previos | ✅ Aprobado | 14 pruebas unitarias automatizadas con JUnit 5 validando tanto el comportamiento esperado de negocio como la no regresión. |

---

## 2. Registro de Hallazgos y Correcciones

### #1: Violación crítica de Encapsulamiento
- **Ubicación:** `com.utn.tpi.base.Solicitud`
- **Problema:** Campos públicos mutables sin validación de invariantes.
- **Acción Correctiva:** Encapsulamiento con modificadores `private final`, validación en constructor de `Estudiante` y `Solicitud`, y delegación de transiciones al patrón State.

### #2: Dependencia a Estado Global Mutable
- **Ubicación:** `SolicitudManager.solicitudesDB` (lista estática en memoria)
- **Problema:** Generaba acoplamiento global, impidiendo ejecución concurrente de tests e introduciendo efectos colaterales persistentes.
- **Acción Correctiva:** Implementación del patrón Repository (`SolicitudRepository` e `InMemSolicitudRepository`) con almacenamiento `ConcurrentHashMap` encapsulado.

### #3: Efectos Colaterales en Consola ( I/O Coupling)
- **Ubicación:** Llamadas directas a `System.out.println` dentro de la lógica del gestor.
- **Problema:** Imposibilitaba la reutilización del módulo en APIs web, interfaces gráficas o pipelines batch.
- **Acción Correctiva:** Abstracción mediante `NotificadorService` con inyección desacoplada.

---

## 3. Criterios de Aceptación Técnica
1. **Comportamiento externo:** Las 4 operaciones troncales conservan su semántica funcional.
2. **Regresiones:** Verificado mediante ejecución de pruebas JUnit 5.
3. **Mantenibilidad:** Complejidad ciclomática promedio reducida a métodos lineales y desacoplados.
