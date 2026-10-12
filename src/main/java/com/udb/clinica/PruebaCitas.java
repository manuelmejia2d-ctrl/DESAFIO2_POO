package com.udb.clinica;

import com.udb.clinica.dao.CitaDAO;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.EstadoCita;
import com.udb.clinica.servicio.CitaServicio;
import com.udb.clinica.util.DatosInvalidosException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
/** Prueba de consola de las reglas de citas. Deja la base con las mismas
11 citas. */
public class PruebaCitas {
 private interface Accion {
 void ejecutar() throws SQLException;
 }
 /** Ejecuta algo que DEBE ser rechazado e imprime el motivo. */
 private static void esperarRechazo(String nombre, Accion accion)
throws SQLException {
 try {
 accion.ejecutar();
 System.out.println(nombre + " -> ERROR: debió ser rechazado");
 } catch (DatosInvalidosException e) {
 System.out.println(nombre + " -> rechazado: " +
e.getMessage());
 }
 }
 public static void main(String[] args) {
 CitaServicio servicio = new CitaServicio();
 CitaDAO dao = new CitaDAO();
 try {
 int inicial = dao.listar().size();
 System.out.println("Citas al inicio: " + inicial + "(esperado 11)");
 LocalDate dia = LocalDate.of(2026, 10, 15);
 // 1) Cita válida
 Cita a = new Cita(1, 1, dia, LocalTime.of(8, 0), "Prueba A",
EstadoCita.PROGRAMADA);
 servicio.programar(a);
 System.out.println("1) Cita 08:00 programada con id " +
a.getIdCita() + " -> correcto");
 // 2) Se cruza (08:15 con la de 08:00)
 esperarRechazo("2) Cita 08:15 mismo doctor", () ->
 servicio.programar(new Cita(2, 1, dia,
LocalTime.of(8, 15), "Cruce", EstadoCita.PROGRAMADA)));
 // 3) Justo 30 minutos después: permitido
 Cita b = new Cita(2, 1, dia, LocalTime.of(8, 30), "Prueba B",
EstadoCita.PROGRAMADA);
 servicio.programar(b);
 System.out.println("3) Cita 08:30 mismo doctor -> correcto (id " + b.getIdCita() + ")");
 // 4) Cancelar libera el horario
 servicio.cancelar(a.getIdCita());
 Cita c = new Cita(3, 1, dia, LocalTime.of(8, 0), "Prueba C",
EstadoCita.PROGRAMADA);
 servicio.programar(c);
 System.out.println("4) Tras cancelar la de 08:00, se pudo programar otra a las 08:00 -> correcto");
 // 5) Paciente inactivo (id 6)
 esperarRechazo("5) Paciente inactivo", () ->
 servicio.programar(new Cita(6, 1, dia,
LocalTime.of(9, 0), "Inactivo", EstadoCita.PROGRAMADA)));
 // 6) Doctor inactivo (id 5)
 esperarRechazo("6) Doctor inactivo", () ->
 servicio.programar(new Cita(1, 5, dia,
LocalTime.of(9, 0), "Inactivo", EstadoCita.PROGRAMADA)));
 // 7) Paciente que no existe
 esperarRechazo("7) Paciente inexistente", () ->
 servicio.programar(new Cita(999, 1, dia,
LocalTime.of(9, 0), "No existe", EstadoCita.PROGRAMADA)));
 // 8) Modificar una cita que ya está cancelada
 esperarRechazo("8) Marcar atendida una cita cancelada", () ->
 servicio.marcarAtendida(a.getIdCita()));
 // 9) Marcar atendida una Programada
 servicio.marcarAtendida(b.getIdCita());
 System.out.println("9) Cita B marcada como " +
dao.buscarPorId(b.getIdCita()).getEstado());
 // Limpieza
 dao.eliminar(a.getIdCita());
 dao.eliminar(b.getIdCita());
 dao.eliminar(c.getIdCita());
 System.out.println("Citas al final: " + dao.listar().size() +
" (esperado " + inicial + ")");
 } catch (SQLException e) {
 System.out.println("Error de base de datos: " +
e.getMessage());
 }
 }
}