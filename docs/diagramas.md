# Diagramas

Los diagramas de casos de uso y el modelo entidad-relación están en el
documento PDF. Aquí se incluyen el diagrama de clases, la arquitectura por
capas y el diagrama de actividades de "Programar cita".

## 1. Diagrama de clases

```mermaid
classDiagram
    class Persona {
        <<abstract>>
        -int id
        -String codigo
        -String nombreCompleto
        -String telefono
        -String correo
        -EstadoPersona estado
        +getResumen() String
        +estaActivo() boolean
    }
    class Paciente {
        -int edad
        -TipoPaciente tipoPaciente
        +getResumen() String
    }
    class Doctor {
        -Especialidad especialidad
        +getResumen() String
    }
    class Cita {
        -int idCita
        -int idPaciente
        -int idDoctor
        -LocalDate fecha
        -LocalTime hora
        -String motivo
        -EstadoCita estado
        +getHoraFin() LocalTime
    }
    class EstadoPersona {
        <<enumeration>>
        ACTIVO
        INACTIVO
    }
    class TipoPaciente {
        <<enumeration>>
        ESTUDIANTE
        DOCENTE
        ADMINISTRATIVO
        VISITANTE
    }
    class Especialidad {
        <<enumeration>>
        MEDICINA_GENERAL
        PSICOLOGIA
        NUTRICION
        FISIOTERAPIA
    }
    class EstadoCita {
        <<enumeration>>
        PROGRAMADA
        ATENDIDA
        CANCELADA
    }

    Persona <|-- Paciente
    Persona <|-- Doctor
    Persona --> EstadoPersona
    Paciente --> TipoPaciente
    Doctor --> Especialidad
    Cita --> EstadoCita
    Cita ..> Paciente : referencia por idPaciente
    Cita ..> Doctor : referencia por idDoctor
```

**Explicación.** `Persona` es una clase abstracta porque en la clínica nunca
existe una persona sola: siempre es un paciente o un doctor. Reúne los datos
que ambos comparten. `Paciente` agrega la edad y el tipo; `Doctor` agrega la
especialidad. `Cita` no guarda los objetos completos, solo los id del paciente
y del doctor, igual que las llaves foráneas de la tabla `citas`.

## 2. Clases de acceso a datos, reglas y utilidades

```mermaid
classDiagram
    class PanelCitas
    class CitaServicio {
        +programar(Cita)
        +marcarAtendida(int)
        +cancelar(int)
    }
    class CitaDAO {
        +insertar(Cita)
        +listar()
        +buscar(String)
        +cambiarEstado(int, EstadoCita)
        +existeSolapamiento(int, LocalDate, LocalTime, int) boolean
    }
    class PacienteDAO
    class DoctorDAO
    class ReporteDAO
    class ConexionBD {
        +obtenerConexion() Connection
    }
    class Validador
    class RegistroErrores
    class DatosInvalidosException

    PanelCitas --> CitaServicio
    CitaServicio --> CitaDAO
    CitaServicio --> PacienteDAO
    CitaServicio --> DoctorDAO
    CitaDAO --> ConexionBD
    PacienteDAO --> ConexionBD
    DoctorDAO --> ConexionBD
    ReporteDAO --> ConexionBD
    ConexionBD --> RegistroErrores
    CitaServicio ..> DatosInvalidosException
    Validador ..> DatosInvalidosException
```

**Explicación.** Las pantallas no escriben SQL. Para las citas pasan por
`CitaServicio`, que aplica las reglas y después llama a los DAO. Los DAO
obtienen la conexión de `ConexionBD`.

## 3. Arquitectura por capas

```mermaid
flowchart LR
    A[vista: paneles Swing] --> B[servicio: reglas de negocio]
    B --> C[dao: consultas SQL]
    A --> C
    C --> D[util: ConexionBD JDBC]
    D --> E[(MySQL clinica_udb)]
```

Los paneles de pacientes, doctores y reportes llaman directamente al DAO
porque no tienen reglas que aplicar. Las citas pasan por la capa de servicio.

## 4. Diagrama de actividades: programar una cita

```mermaid
flowchart TD
    A([Inicio]) --> B[El usuario elige paciente y doctor]
    B --> C[Escribe fecha, hora y motivo]
    C --> D{¿Formato de fecha, hora y motivo válidos?}
    D -- No --> X1[Mostrar aviso de datos no válidos]
    D -- Sí --> E{¿El paciente existe y está activo?}
    E -- No --> X2[Mostrar aviso de paciente no disponible]
    E -- Sí --> F{¿El doctor existe y está activo?}
    F -- No --> X3[Mostrar aviso de doctor no disponible]
    F -- Sí --> G{¿El doctor tiene otra cita Programada que se cruce?}
    G -- Sí --> X4[Mostrar aviso de horario ocupado]
    G -- No --> H[Guardar la cita como Programada en MySQL]
    H --> I[Mostrar mensaje de éxito y actualizar la tabla]
    I --> Z([Fin])
    X1 --> Z
    X2 --> Z
    X3 --> Z
    X4 --> Z
```

**Explicación.** Antes de guardar se comprueban en orden: los datos escritos,
el paciente, el doctor y el horario. Si alguna comprobación falla, se muestra
un aviso y no se guarda nada. Las citas Canceladas o Atendidas no cuentan para
el cruce, por eso una cita cancelada libera el horario.