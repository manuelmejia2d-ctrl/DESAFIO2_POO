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

