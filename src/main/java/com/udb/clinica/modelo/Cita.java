package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
import com.udb.clinica.util.Validador;
import java.time.LocalDate;
import java.time.LocalTime;
/**
* Cita médica. Une un paciente con un doctor en una fecha y hora.
* Corresponde a la tabla "citas".
* Guarda los ids (llaves foráneas), no los objetos completos.
*/
public class Cita {
 // Duración fija propuesta para cada cita (se usará en la regla de solapamiento)
 public static final int DURACION_MINUTOS = 30;
 private int idCita;
 private int idPaciente;
 private int idDoctor;
 private LocalDate fecha;
 private LocalTime hora;
 private String motivo;
 private EstadoCita estado;
 // Solo para mostrar en pantallas (vienen de un JOIN, no se guardan)
 private String nombrePaciente;
 private String nombreDoctor;
 public Cita(int idPaciente, int idDoctor, LocalDate fecha, LocalTime
hora,
 String motivo, EstadoCita estado) {
 setIdPaciente(idPaciente);
 setIdDoctor(idDoctor);
 setFecha(fecha);
 setHora(hora);
 setMotivo(motivo);
 setEstado(estado);
 }
 /** Hora en que termina la cita. */
 public LocalTime getHoraFin() {
 return hora.plusMinutes(DURACION_MINUTOS);
 }
 // ----- Getters y setters -----
 public int getIdCita() {
 return idCita;
 }
 public void setIdCita(int idCita) {
 this.idCita = idCita;
 }
 public int getIdPaciente() {
 return idPaciente;
 }
 public void setIdPaciente(int idPaciente) {
 if (idPaciente <= 0) {
 throw new DatosInvalidosException("Debe seleccionar un paciente.");
 }
 this.idPaciente = idPaciente;
 }
 public int getIdDoctor() {
 return idDoctor;
 }
 public void setIdDoctor(int idDoctor) {
 if (idDoctor <= 0) {
 throw new DatosInvalidosException("Debe seleccionar un doctor.");
 }
 this.idDoctor = idDoctor;
 }
 public LocalDate getFecha() {
 return fecha;
 }
 public void setFecha(LocalDate fecha) {
 if (fecha == null) {
 throw new DatosInvalidosException("La fecha es obligatoria.");
 }
 this.fecha = fecha;
 }
 public LocalTime getHora() {
 return hora;
 }
 public void setHora(LocalTime hora) {
 if (hora == null) {
 throw new DatosInvalidosException("La hora es obligatoria.");
 }
 this.hora = hora;
 }
 public String getMotivo() {
 return motivo;
 }
 public void setMotivo(String motivo) {
 this.motivo = Validador.textoObligatorio(motivo, "motivo de consulta", 200);
 }
 public EstadoCita getEstado() {
 return estado;
 }
 public void setEstado(EstadoCita estado) {
 if (estado == null) {
 throw new DatosInvalidosException("El estado de la cita es obligatorio.");
 }
 this.estado = estado;
 }
 public String getNombrePaciente() {
 return nombrePaciente;
 }
 public void setNombrePaciente(String nombrePaciente) {
 this.nombrePaciente = nombrePaciente;
 }
 public String getNombreDoctor() {
 return nombreDoctor;
 }
 public void setNombreDoctor(String nombreDoctor) {
 this.nombreDoctor = nombreDoctor;
 }
}
