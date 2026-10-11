package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
/**
* Doctor de la clínica. Hereda de Persona y agrega la especialidad.
* Corresponde a la tabla "doctores".
*/
public class Doctor extends Persona {
 private Especialidad especialidad;
 public Doctor(String codigo, String nombreCompleto, Especialidad
especialidad,
 String telefono, String correo, EstadoPersona estado) {
 super(codigo, nombreCompleto, telefono, correo, estado);
 setEspecialidad(especialidad);
 }
 @Override
 public String getResumen() {
 return getCodigo() + " - " + getNombreCompleto() + " (" +
especialidad + ")";
 }
 public Especialidad getEspecialidad() {
 return especialidad;
 }
 public void setEspecialidad(Especialidad especialidad) {
 if (especialidad == null) {
 throw new DatosInvalidosException("La especialidad es obligatoria.");
 }
 this.especialidad = especialidad;
 }
}

