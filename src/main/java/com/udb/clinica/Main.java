 package com.udb.clinica;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.Especialidad;
import com.udb.clinica.modelo.EstadoCita;
import com.udb.clinica.modelo.EstadoPersona;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.TipoPaciente;
import com.udb.clinica.util.ConexionBD;
import com.udb.clinica.util.DatosInvalidosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
/**
* Clase principal. Por ahora solo hace pruebas de consola
* (modelo, validaciones y conexión). Después abrirá la ventana Swing.
*/
public class Main {
 public static void main(String[] args) {
 // Prueba 1: crear objetos del modelo
 System.out.println("=== Prueba 1: clases del modelo ===");
 try {
 Paciente paciente = new Paciente("PAC-100", "Prueba Local",
21, "7000-0100",
 "prueba.local@ejemplo.com", TipoPaciente.ESTUDIANTE,
EstadoPersona.ACTIVO);
 Doctor doctor = new Doctor("DOC-100", "Dr. Prueba Local",
Especialidad.NUTRICION,
 "7100-0100", "doctor.local@ejemplo.com",
EstadoPersona.ACTIVO);
 Cita cita = new Cita(1, 1, LocalDate.of(2026, 10, 12),
LocalTime.of(8, 0),
 "Chequeo de prueba", EstadoCita.PROGRAMADA);
 System.out.println(paciente.getResumen());
 System.out.println(doctor.getResumen());
 System.out.println("Cita: " + cita.getFecha() + " de " +
cita.getHora()
 + " a " + cita.getHoraFin());
 } catch (DatosInvalidosException e) {
 System.out.println("Datos inválidos: " + e.getMessage());
 }
 // Prueba 2: una validación que debe fallar
 System.out.println("\n=== Prueba 2: validación (debe rechazar la edad) ===");
 try {
 new Paciente("PAC-101", "Edad Incorrecta", 150, "7000-0101",
 "edad@ejemplo.com", TipoPaciente.DOCENTE,
EstadoPersona.ACTIVO);
 System.out.println("ERROR: debió rechazar la edad 150.");
 } catch (DatosInvalidosException e) {
 System.out.println("Correcto, se rechazó: " +
e.getMessage());
 }
 // Prueba 3: conexión a MySQL
 System.out.println("\n=== Prueba 3: conexión a MySQL ===");
 try (Connection con = ConexionBD.obtenerConexion()) {
 System.out.println("Conexión exitosa.");
 System.out.println("Pacientes: " + contar(con, "SELECT COUNT(*) FROM pacientes"));
 System.out.println("Doctores: " + contar(con, "SELECT COUNT(*) FROM doctores"));
 System.out.println("Citas: " + contar(con, "SELECT COUNT(*) FROM citas"));
 } catch (SQLException e) {
 System.out.println("No se pudo conectar a MySQL: " +
e.getMessage());
 System.out.println("Revise el archivo logs/errores.log");
 }
 }
 /** Ejecuta un SELECT COUNT(*) y devuelve el número. */
 private static int contar(Connection con, String sql) throws
SQLException {
 try (PreparedStatement ps = con.prepareStatement(sql);
 ResultSet rs = ps.executeQuery()) {
 rs.next();
 return rs.getInt(1);
 }
 }
}