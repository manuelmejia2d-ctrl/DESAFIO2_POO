-- =====================================================
-- datos_prueba.sql
-- Datos ficticios para probar el sistema.
-- Ejecutar DESPUÉS de schema.sql.
-- =====================================================
USE clinica_udb;

-- Pacientes (dos inactivos para probar esa regla)
INSERT INTO pacientes (codigo, nombre_completo, edad, telefono, correo, tipo_paciente, estado) VALUES
('PAC-001', 'Ana María López Hernández',  20, '7000-0001', 'ana.lopez@ejemplo.com',      'Estudiante',     'Activo'),
('PAC-002', 'Carlos Eduardo Ramírez',     22, '7000-0002', 'carlos.ramirez@ejemplo.com', 'Estudiante',     'Activo'),
('PAC-003', 'Sofía Martínez Rivas',       41, '7000-0003', 'sofia.martinez@ejemplo.com', 'Docente',        'Activo'),
('PAC-004', 'José Roberto Castillo',      35, '7000-0004', 'jose.castillo@ejemplo.com',  'Administrativo', 'Activo'),
('PAC-005', 'Daniela Beatriz Flores',     19, '7000-0005', 'daniela.flores@ejemplo.com', 'Estudiante',     'Activo'),
('PAC-006', 'Miguel Ángel Portillo',      52, '7000-0006', 'miguel.portillo@ejemplo.com','Docente',        'Inactivo'),
('PAC-007', 'Laura Patricia Guzmán',      29, '7000-0007', 'laura.guzman@ejemplo.com',   'Visitante',      'Activo'),
('PAC-008', 'Kevin Alexander Mejía',      21, '7000-0008', 'kevin.mejia@ejemplo.com',    'Estudiante',     'Inactivo');

-- Doctores (uno inactivo para probar esa regla)
INSERT INTO doctores (codigo, nombre_completo, especialidad, telefono, correo, estado) VALUES
('DOC-001', 'Dr. Ricardo Alvarado',  'Medicina General', '7100-0001', 'ricardo.alvarado@ejemplo.com', 'Activo'),
('DOC-002', 'Dra. Patricia Núñez',   'Psicología',       '7100-0002', 'patricia.nunez@ejemplo.com',    'Activo'),
('DOC-003', 'Dra. Gabriela Serrano', 'Nutrición',        '7100-0003', 'gabriela.serrano@ejemplo.com',  'Activo'),
('DOC-004', 'Dr. Fernando Zelaya',   'Fisioterapia',     '7100-0004', 'fernando.zelaya@ejemplo.com',   'Activo'),
('DOC-005', 'Dr. Héctor Menjívar',   'Medicina General', '7100-0005', 'hector.menjivar@ejemplo.com',   'Inactivo');

-- Citas (columnas: paciente, doctor, fecha, hora, motivo, estado)
-- Las citas 9 y 10 tienen el mismo doctor, fecha y hora: la 9 está
-- Cancelada, así que no ocupa el horario y la 10 sí es válida.
INSERT INTO citas (id_paciente, id_doctor, fecha, hora, motivo, estado) VALUES
(1, 1, '2026-10-12', '08:00:00', 'Dolor de cabeza frecuente', 'Programada'),
(2, 1, '2026-10-12', '09:00:00', 'Chequeo general',           'Programada'),
(3, 2, '2026-10-13', '10:00:00', 'Ansiedad por exámenes',     'Programada'),
(4, 3, '2026-10-14', '14:00:00', 'Plan de alimentación',      'Programada'),
(5, 4, '2026-10-14', '15:00:00', 'Dolor lumbar',              'Programada'),
(7, 1, '2026-10-05', '08:00:00', 'Gripe y fiebre',            'Atendida'),
(1, 2, '2026-10-06', '11:00:00', 'Manejo del estrés',         'Atendida'),
(2, 3, '2026-10-07', '09:30:00', 'Control de peso',           'Atendida'),
(3, 1, '2026-10-12', '10:00:00', 'Revisión de presión',       'Cancelada'),
(4, 1, '2026-10-12', '10:00:00', 'Dolor de garganta',         'Programada'),
(5, 2, '2026-10-08', '16:00:00', 'Problemas de sueño',        'Cancelada');