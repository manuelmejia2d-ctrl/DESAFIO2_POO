# DESAFIO2_POO - Sistema de gestión de citas, Clínica Universitaria Vida UDB

**Universidad Don Bosco**
**Materia:** Programación Orientada a Objetos
**Proyecto:** Desafío 2
**Fecha de entrega:** 11 de octubre de 2026

## Integrantes

| N.º | Nombre completo | Carnet |
|---|---|---|
| 1 | Katherine Julissa Arévalo Sierra | AS242488 |
| 2 | Manuel de Jesús Mejia Rivera     | MR260122 |
| 3 | José javier Aguilar Amaya        | AA261221 |
| 4 | Marlon Josué Sosa Barillas       | SB263028 |

## Descripción

Aplicación de escritorio hecha en Java (Swing) para administrar pacientes,
doctores y citas médicas de la Clínica Universitaria Vida UDB. Los datos se
guardan en MySQL y se accede a ellos mediante JDBC.

## Qué hace el sistema

- **Pacientes:** registrar, consultar, buscar, actualizar, cambiar estado
  (Activo/Inactivo) y eliminar.
- **Doctores:** lo mismo que pacientes, con especialidad.
- **Citas:** programar una cita eligiendo paciente y doctor, buscar por
  número, paciente, doctor, fecha o estado, y marcarla como Atendida o
  Cancelada.
- **Reglas de las citas:**
  - El paciente y el doctor deben existir y estar activos.
  - Un doctor no puede tener dos citas programadas que se crucen.
    Cada cita dura 30 minutos.
  - Una cita cancelada libera el horario.
- **Reportes:** citas programadas, atendidas y canceladas, cantidad por
  estado, citas por doctor, citas por fecha y total de pacientes.

## Tecnologías

- Java 17
- Maven 3.9
- MySQL 8.0
- JDBC (mysql-connector-j 8.4.0)
- Java Swing
- Git y GitHub

## Requisitos previos

1. JDK 17 instalado (`java -version` debe mostrar 17).
2. Maven instalado (`mvn -version`).
3. MySQL Server 8.x encendido. Se recomienda MySQL Workbench.

## Instalación y configuración

### 1. Clonar el repositorio

```powershell
git clone https://github.com/manuelmejia2d-ctrl/DESAFIO2_POO.git
cd DESAFIO2_POO
```

### 2. Crear la base de datos

En MySQL Workbench, conectado a su servidor local, abra y ejecute **en este
orden** (cada uno una sola vez):

1. `database/schema.sql` - crea la base `clinica_udb` y las tablas.
2. `database/datos_prueba.sql` - carga datos ficticios de prueba
   (8 pacientes, 5 doctores y 11 citas).

Atención: `schema.sql` borra las tablas antes de crearlas. Si lo vuelve a
ejecutar, pierde los datos y debe volver a cargar `datos_prueba.sql`.

### 3. Configurar la conexión

Copie el archivo `src/main/resources/database.properties.example` con el nombre
`src/main/resources/database.properties` y escriba su propia contraseña:

```properties
db.url=jdbc:mysql://localhost:3306/clinica_udb?useSSL=false&serverTimezone=America/El_Salvador&allowPublicKeyRetrieval=true
db.user=root
db.password=SU_CONTRASENA_DE_MYSQL
```

Si su usuario de MySQL no es `root`, cámbielo en `db.user`.

El archivo `database.properties` está en el `.gitignore`, por lo que no se
sube al repositorio. Nunca escriba contraseñas en el código.

## Ejecución

Desde la carpeta del proyecto:

```powershell
mvn compile exec:java
```

Se abre la ventana principal con cuatro pestañas: Pacientes, Doctores, Citas
y Reportes. Si MySQL no está encendido o la contraseña es incorrecta, el
programa muestra un mensaje de error de conexión.

Nota: en la consola de Windows las tildes pueden verse mal. Es solo la consola,
no afecta al programa.

## Estructura del proyecto

```text
DESAFIO2_POO/
├── src/main/java/com/udb/clinica/
│   ├── modelo/     Persona, Paciente, Doctor, Cita y enumeraciones
│   ├── dao/        Acceso a MySQL (PreparedStatement)
│   ├── servicio/   Reglas de negocio de las citas
│   ├── util/       Conexión JDBC, validaciones, log y excepción propia
│   ├── vista/      Ventanas y paneles Swing
│   └── Main.java
├── src/main/resources/
│   └── database.properties.example
├── database/       schema.sql y datos_prueba.sql
├── docs/           Diagramas, pruebas y capturas
├── pom.xml
└── README.md
```

## Conceptos aplicados

- **Herencia:** `Paciente` y `Doctor` extienden la clase abstracta `Persona`,
  que reúne lo que comparten (código, nombre, teléfono, correo y estado).
- **Encapsulamiento:** atributos privados; los setters validan los datos.
- **Excepciones:** `DatosInvalidosException` para datos incorrectos y manejo
  de `SQLException` en los DAO.
- **Registro de errores:** los errores técnicos se guardan en `logs/errores.log`
  (sin contraseñas). Esa carpeta no se sube al repositorio.

## Documentación

- `docs/diagramas.md` - diagramas de clases, de capas y de actividades.
- `docs/pruebas.md` - pruebas realizadas y sus resultados.