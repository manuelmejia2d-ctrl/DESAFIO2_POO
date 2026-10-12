package com.udb.clinica;
import com.udb.clinica.dao.PacienteDAO;
import com.udb.clinica.modelo.EstadoPersona;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.TipoPaciente;
import com.udb.clinica.util.DatosInvalidosException;
import java.sql.SQLException;
/** Prueba de consola de PacienteDAO. Deja la base con los mismos 8
pacientes. */
public class PruebaPacientes {
 public static void main(String[] args) {
 PacienteDAO dao = new PacienteDAO();
 try {
 System.out.println("1) Pacientes al inicio: " +
dao.listar().size() + " (esperado 8)");
 Paciente nuevo = new Paciente("PAC-900", "Paciente Prueba DAO", 25, "7000-0900",
 "prueba.dao.pac@ejemplo.com", TipoPaciente.VISITANTE,
EstadoPersona.ACTIVO);
 dao.insertar(nuevo);
 System.out.println("2) Insertado con id " + nuevo.getId());
 System.out.println("3) Búsqueda 'PAC-900': " +
dao.buscar("PAC-900").size() + " (esperado 1)");
 nuevo.setEdad(26);
 System.out.println("4) Actualizado: " + dao.actualizar(nuevo)
 + " | edad guardada: " +
dao.buscarPorId(nuevo.getId()).getEdad());
 dao.cambiarEstado(nuevo.getId(), EstadoPersona.INACTIVO);
 System.out.println("5) Estado ahora: " +
dao.buscarPorId(nuevo.getId()).getEstado());
 try {
 dao.insertar(new Paciente("PAC-900", "Otro Paciente", 30,
"7000-0901",
 "otro.pac@ejemplo.com", TipoPaciente.DOCENTE,
EstadoPersona.ACTIVO));
 System.out.println("6) ERROR: debió rechazar el código repetido");
 } catch (DatosInvalidosException e) {
 System.out.println("6) Duplicado rechazado: " +
e.getMessage());
 }
 System.out.println("7) Eliminado PAC-900: " +
dao.eliminar(nuevo.getId()));
 try {
 dao.eliminar(1); // el paciente 1 tiene citas en los datos de prueba
 System.out.println("8) ERROR: debió impedir borrar un paciente con citas");
 } catch (DatosInvalidosException e) {
 System.out.println("8) Eliminación rechazada: " +
e.getMessage());
 }
 System.out.println("9) Pacientes al final: " +
dao.listar().size() + " (esperado 8)");
 } catch (SQLException e) {
 System.out.println("Error de base de datos: " +
e.getMessage());
 }
 }
}

