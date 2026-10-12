# Pruebas de la base de datos

Pruebas ejecutadas en MySQL Workbench sobre MySQL 8.0.46.

## Orden real de ejecución

`schema.sql` se ejecutó varias veces mientras se ajustaba el proceso. Cada
ejecución borra y recrea las tablas, por lo que deja los datos en cero.
Después se ejecutó `datos_prueba.sql` y se realizaron los conteos, el JOIN
y las pruebas de errores. Al final se volvió a ejecutar `schema.sql` para
capturar su salida y `SHOW TABLES`; luego se recargó `datos_prueba.sql`
una sola vez y se repitieron los conteos.

## Pruebas de la base de datos

| N.º | Prueba | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| 1 | Ejecutar `schema.sql` | Se crean 3 tablas | `SHOW TABLES` devolvió citas, doctores y pacientes | Correcto |
| 2 | Ejecutar `datos_prueba.sql` | 8 pacientes, 5 doctores, 11 citas | Los `COUNT(*)` dieron 8, 5 y 11 | Correcto |
| 3 | JOIN de citas con pacientes y doctores | 11 filas con nombres | Salieron 11 filas con nombres | Correcto |
| 4 | Insertar paciente con edad 150 | Error por CHECK | Error 3819, `ck_pacientes_edad` violada | Correcto |
| 5 | Insertar paciente con código repetido `PAC-001` | Error por UNIQUE | Error 1062, entrada duplicada | Correcto |
| 6 | Insertar cita con paciente inexistente (id 999) | Error por llave foránea | Error 1452 | Correcto |

## Evidencias (carpeta `docs/capturas/`)

- `01_tablas_creadas.png`
- `02_conteos.png`
- `03_join_citas.png`
- `04_error_edad.png`
- `05_error_duplicado.png`
- `06_error_llave_foranea.png`
- `07_schema_datos_output.png`

## Observaciones

- Ejecutar `schema.sql` borra tablas y datos (`DROP TABLE`), por eso siempre
  debe seguirse de `datos_prueba.sql`.
- Al ejecutar `datos_prueba.sql` dos veces seguidas, MySQL rechazó la segunda
  carga con el error 1062 por el código duplicado `PAC-001`.
- Un JOIN y varios conteos se ejecutaron en un momento con las tablas vacías
  y devolvieron 0 filas, porque `schema.sql` se había vuelto a ejecutar
  después de cargar los datos.

# 2. Pruebas del programa (interfaz Swing)

### Pacientes

| N.º | Prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| 1 | Abrir la pestaña | 8 pacientes | Correcto, coincide con lo esperado |
| 2 | Registrar paciente nuevo | Mensaje de éxito y aparece en la tabla | Correcto, coincide con lo esperado |
| 3 | Registrar con código repetido | Mensaje de código o correo repetido | Correcto, coincide con lo esperado |
| 4 | Nombre vacío | Mensaje de campo obligatorio | Correcto, coincide con lo esperado |
| 5 | Edad con texto | Mensaje de número entero | Correcto, coincide con lo esperado |
| 6 | Edad 150 | Mensaje de edad entre 0 y 120 | Correcto, coincide con lo esperado |
| 7 | Correo con formato inválido | Mensaje de formato de correo | Correcto, coincide con lo esperado |
| 8 | Actualizar un paciente | Cambio visible en la tabla | Correcto, coincide con lo esperado |
| 9 | Cambiar estado | Pasa de Activo a Inactivo | Correcto, coincide con lo esperado |
| 10 | Buscar por tipo | Solo muestra ese tipo | Correcto, coincide con lo esperado |
| 11 | Eliminar paciente con citas | Mensaje de que no se puede eliminar | Correcto, coincide con lo esperado |
| 12 | Eliminar paciente sin citas | Desaparece de la tabla | Correcto, coincide con lo esperado |

### Doctores

| N.º | Prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| 13 | Abrir la pestaña | 5 doctores | Correcto, coincide con lo esperado |
| 14 | Registrar doctor nuevo | Mensaje de éxito | Correcto, coincide con lo esperado |
| 15 | Código repetido | Mensaje de código o correo repetido | Correcto, coincide con lo esperado |
| 16 | Buscar por especialidad | Solo esa especialidad | Correcto, coincide con lo esperado |
| 17 | Cambiar estado | Pasa a Inactivo | Correcto, coincide con lo esperado |
| 18 | Eliminar doctor con citas | Mensaje de que no se puede eliminar | Correcto, coincide con lo esperado |
| 19 | Eliminar doctor sin citas | Desaparece de la tabla | Correcto, coincide con lo esperado |

### Citas

| N.º | Prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| 20 | Abrir la pestaña | 11 citas con nombres | Correcto, coincide con lo esperado |
| 21 | Programar cita válida | Mensaje de éxito | Correcto, coincide con lo esperado |
| 22 | Cita 15 min después, mismo doctor y fecha | Rechazada por cruce de horario | Correcto, coincide con lo esperado |
| 23 | Cita 30 min después, mismo doctor y fecha | Se programa | Correcto, coincide con lo esperado |
| 24 | Paciente inactivo | Rechazada | Correcto, coincide con lo esperado |
| 25 | Doctor inactivo | Rechazada | Correcto, coincide con lo esperado |
| 26 | Fecha con formato incorrecto | Mensaje de formato | Correcto, coincide con lo esperado |
| 27 | Hora con formato incorrecto | Mensaje de formato | Correcto, coincide con lo esperado |
| 28 | Motivo vacío | Mensaje de campo obligatorio | Correcto, coincide con lo esperado |
| 29 | Cancelar una cita programada | Pasa a Cancelada | Correcto, coincide con lo esperado |
| 30 | Programar en el horario de la cancelada | Se permite | Correcto, coincide con lo esperado |
| 31 | Marcar atendida una cancelada | Rechazada | Correcto, coincide con lo esperado |
| 32 | Marcar atendida una programada | Pasa a Atendida | Correcto, coincide con lo esperado |
| 33 | Buscar por estado, nombre y fecha | Filtra cada vez | Correcto, coincide con lo esperado |

### Reportes

| N.º | Prueba | Cómo se comprobó | Resultado obtenido |
|---|---|---|---|
| 34 | Programadas, atendidas y canceladas | Coincide con la pestaña Citas | Correcto, coincide con lo esperado |
| 35 | Cantidad por estado | La suma es igual al total de citas | Correcto, coincide con lo esperado |
| 36 | Citas por doctor | Doctores sin citas salen con 0 | Correcto, coincide con lo esperado |
| 37 | Citas por fecha | Una fila por fecha | Correcto, coincide con lo esperado |
| 38 | Pacientes registrados | Total = activos + inactivos | Correcto, coincide con lo esperado |

## 3. Pruebas de consola

| Programa | Resultado esperado | Resultado obtenido |
|---|---|---|
| `PruebaPacientes` | Puntos 1 a 9 correctos; 8 pacientes al inicio y al final | Correcto, coincide con lo esperado |
| `PruebaDoctores` | Puntos 1 a 9 correctos; 5 doctores al inicio y al final | Correcto, coincide con lo esperado |
| `PruebaCitas` | Pruebas 1, 3, 4 y 9 correctas; 2, 5, 6, 7 y 8 rechazadas | Correcto, coincide con lo esperado |

## 4. Evidencias

Capturas de pantalla en `docs/capturas/`: resultados de la base de datos en
Workbench, pestañas del programa, mensajes de validación, cita rechazada por
cruce de horario y reportes.

## 5. Nota sobre el registro de las pruebas

Las pruebas del programa las realizaron los integrantes del equipo sobre la
base recién cargada. No se anotó el texto exacto de cada mensaje: se verificó
que cada resultado coincidiera con lo esperado. Las capturas de
`docs/capturas/` muestran ejemplos de las pruebas principales.