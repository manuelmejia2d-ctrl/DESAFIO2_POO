package com.udb.clinica;

import com.udb.clinica.dao.DoctorDAO;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.Especialidad;
import com.udb.clinica.modelo.EstadoPersona;
import com.udb.clinica.util.DatosInvalidosException;

import java.sql.SQLException;
import java.util.List;

/** Prueba de consola de DoctorDAO. Deja la base con los mismos 5 doctores. */
public class PruebaDoctores {

    public static void main(String[] args) {
        DoctorDAO dao = new DoctorDAO();
        try {
            System.out.println("1) Doctores al inicio: " + dao.listar().size() + " (esperado 5)");

            Doctor nuevo = new Doctor("DOC-900", "Dr. Prueba DAO", Especialidad.PSICOLOGIA,
                    "7100-0900", "prueba.dao@ejemplo.com", EstadoPersona.ACTIVO);
            dao.insertar(nuevo);
            System.out.println("2) Insertado con id " + nuevo.getId());

            List<Doctor> encontrados = dao.buscar("DOC-900");
            System.out.println("3) Búsqueda 'DOC-900': " + encontrados.size() + " (esperado 1)");

            nuevo.setTelefono("7100-0999");
            System.out.println("4) Actualizado: " + dao.actualizar(nuevo)
                    + " | teléfono guardado: " + dao.buscarPorId(nuevo.getId()).getTelefono());

            dao.cambiarEstado(nuevo.getId(), EstadoPersona.INACTIVO);
            System.out.println("5) Estado ahora: " + dao.buscarPorId(nuevo.getId()).getEstado());

            try {
                dao.insertar(new Doctor("DOC-900", "Otro Doctor", Especialidad.NUTRICION,
                        "7100-0888", "otro.doctor@ejemplo.com", EstadoPersona.ACTIVO));
                System.out.println("6) ERROR: debió rechazar el código repetido");
            } catch (DatosInvalidosException e) {
                System.out.println("6) Duplicado rechazado: " + e.getMessage());
            }

            System.out.println("7) Eliminado DOC-900: " + dao.eliminar(nuevo.getId()));

            try {
                dao.eliminar(1); // el doctor 1 tiene citas en los datos de prueba
                System.out.println("8) ERROR: debió impedir borrar un doctor con citas");
            } catch (DatosInvalidosException e) {
                System.out.println("8) Eliminación rechazada: " + e.getMessage());
            }

            System.out.println("9) Doctores al final: " + dao.listar().size() + " (esperado 5)");
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}