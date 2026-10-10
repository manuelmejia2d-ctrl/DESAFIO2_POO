-- =====================================================
-- schema.sql
-- Base de datos de la Clinica Universitaria Vida UDB
-- Se puede ejecutar varias veces: borra y recrea las tablas.
-- =====================================================

-- 1. Crear la base de datos (si no existe) con soporte de tildes y ñ
CREATE DATABASE IF NOT EXISTS clinica_udb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. Indicar que vamos a trabajar dentro de esa base de datos
USE clinica_udb;

-- 3. Borrar tablas anteriores (primero citas, porque depende de las otras dos)
DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS doctores;
DROP TABLE IF EXISTS pacientes;

-- =====================================================
-- Tabla pacientes
-- =====================================================
CREATE TABLE pacientes (
    id_paciente     INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(15)  NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    edad            INT          NOT NULL,
    telefono        VARCHAR(15)  NOT NULL,
    correo          VARCHAR(100) NOT NULL,
    tipo_paciente   ENUM('Estudiante','Docente','Administrativo','Visitante') NOT NULL,
    estado          ENUM('Activo','Inactivo') NOT NULL DEFAULT 'Activo',

    CONSTRAINT uq_pacientes_codigo UNIQUE (codigo),
    CONSTRAINT uq_pacientes_correo UNIQUE (correo),
    CONSTRAINT ck_pacientes_edad   CHECK (edad BETWEEN 0 AND 120),
    CONSTRAINT ck_pacientes_codigo CHECK (TRIM(codigo) <> ''),
    CONSTRAINT ck_pacientes_nombre CHECK (TRIM(nombre_completo) <> ''),
    CONSTRAINT ck_pacientes_tel    CHECK (TRIM(telefono) <> '')
) ENGINE=InnoDB;

-- =====================================================
-- Tabla doctores
-- =====================================================
CREATE TABLE doctores (
    id_doctor       INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(15)  NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    especialidad    ENUM('Medicina General','Psicología','Nutrición','Fisioterapia') NOT NULL,
    telefono        VARCHAR(15)  NOT NULL,
    correo          VARCHAR(100) NOT NULL,
    estado          ENUM('Activo','Inactivo') NOT NULL DEFAULT 'Activo',

    CONSTRAINT uq_doctores_codigo UNIQUE (codigo),
    CONSTRAINT uq_doctores_correo UNIQUE (correo),
    CONSTRAINT ck_doctores_codigo CHECK (TRIM(codigo) <> ''),
    CONSTRAINT ck_doctores_nombre CHECK (TRIM(nombre_completo) <> ''),
    CONSTRAINT ck_doctores_tel    CHECK (TRIM(telefono) <> '')
) ENGINE=InnoDB;

-- =====================================================
-- Tabla citas
-- =====================================================
CREATE TABLE citas (
    id_cita     INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT          NOT NULL,
    id_doctor   INT          NOT NULL,
    fecha       DATE         NOT NULL,
    hora        TIME         NOT NULL,
    motivo      VARCHAR(200) NOT NULL,
    estado      ENUM('Programada','Atendida','Cancelada') NOT NULL DEFAULT 'Programada',

    CONSTRAINT fk_citas_paciente FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_citas_doctor FOREIGN KEY (id_doctor)
        REFERENCES doctores (id_doctor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_citas_motivo CHECK (TRIM(motivo) <> ''),

    -- Índice para buscar rápido las citas de un doctor en una fecha
    INDEX idx_citas_doctor_fecha (id_doctor, fecha, hora)
) ENGINE=InnoDB;