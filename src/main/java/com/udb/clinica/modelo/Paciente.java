package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
import com.udb.clinica.util.Validador;
/**
* Paciente de la clínica. Hereda de Persona y agrega edad y tipo.
* Corresponde a la tabla "pacientes".
*/
public class Paciente extends Persona {
 private int edad;
 private TipoPaciente tipoPaciente;
 public Paciente(String codigo, String nombreCompleto, int edad,
String telefono,
 String correo, TipoPaciente tipoPaciente,
EstadoPersona estado) {
 super(codigo, nombreCompleto, telefono, correo, estado);
 setEdad(edad);
 setTipoPaciente(tipoPaciente);
 }
 @Override
 public String getResumen() {
 return getCodigo() + " - " + getNombreCompleto() + " (" +
tipoPaciente + ")";
 }
 public int getEdad() {
 return edad;
 }
 public void setEdad(int edad) {
 this.edad = Validador.edad(edad);
 }
 public TipoPaciente getTipoPaciente() {
 return tipoPaciente;
 }
 public void setTipoPaciente(TipoPaciente tipoPaciente) {
 if (tipoPaciente == null) {
 throw new DatosInvalidosException("El tipo de paciente es obligatorio.");
 }
 this.tipoPaciente = tipoPaciente;
 }
}

