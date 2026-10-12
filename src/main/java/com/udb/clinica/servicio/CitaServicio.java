package com.udb.clinica.servicio;

import com.udb.clinica.dao.CitaDAO;
import com.udb.clinica.dao.DoctorDAO;
import com.udb.clinica.dao.PacienteDAO;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.EstadoCita;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.util.DatosInvalidosException;
import java.sql.SQLException;
/**
* Reglas de negocio de las citas. La pantalla llama a esta clase y no al
DAO
* directamente, así las reglas están en un solo lugar.
*/
public class CitaServicio {
 private final CitaDAO citaDAO = new CitaDAO();
 private final PacienteDAO pacienteDAO = new PacienteDAO();
 private final DoctorDAO doctorDAO = new DoctorDAO();
 /**
 * Programa una cita nueva si cumple todas las reglas:
 * 1) el paciente existe y está activo, 2) el doctor existe y está
activo,
 * 3) el doctor no tiene otra cita programada que se cruce.
 */
 public void programar(Cita cita) throws SQLException {
 Paciente paciente =
pacienteDAO.buscarPorId(cita.getIdPaciente());
 if (paciente == null) {
 throw new DatosInvalidosException("El paciente seleccionado no existe.");
 }
 if (!paciente.estaActivo()) {
 throw new DatosInvalidosException("El paciente está inactivo y no puede recibir citas.");
 }
 Doctor doctor = doctorDAO.buscarPorId(cita.getIdDoctor());
 if (doctor == null) {
 throw new DatosInvalidosException("El doctor seleccionado no existe.");
 }
 if (!doctor.estaActivo()) {
 throw new DatosInvalidosException("El doctor está inactivo y no puede recibir citas.");
 }
 if (citaDAO.existeSolapamiento(cita.getIdDoctor(),
cita.getFecha(), cita.getHora(), 0)) {
 throw new DatosInvalidosException("El doctor ya tiene una cita programada que se cruza con "
 + "ese horario (cada cita dura " +
Cita.DURACION_MINUTOS + " minutos).");
 }
 cita.setEstado(EstadoCita.PROGRAMADA);
 citaDAO.insertar(cita);
 }
 /** Marca una cita Programada como Atendida. */
 public void marcarAtendida(int idCita) throws SQLException {
 cambiarDesdeProgramada(idCita, EstadoCita.ATENDIDA);
 }
 /** Cancela una cita Programada. Al quedar Cancelada, su horario deja
de estar ocupado. */
 public void cancelar(int idCita) throws SQLException {
 cambiarDesdeProgramada(idCita, EstadoCita.CANCELADA);
 }
 /** Solo se puede pasar a Atendida o Cancelada una cita que esté
Programada. */
 private void cambiarDesdeProgramada(int idCita, EstadoCita
nuevoEstado) throws SQLException {
 Cita cita = citaDAO.buscarPorId(idCita);
 if (cita == null) {
 throw new DatosInvalidosException("La cita no existe.");
 }
 if (cita.getEstado() != EstadoCita.PROGRAMADA) {
 throw new DatosInvalidosException("Solo se puede modificar una cita Programada. "
 + "Esta cita está " + cita.getEstado() + ".");
 }
 citaDAO.cambiarEstado(idCita, nuevoEstado);
 }
}