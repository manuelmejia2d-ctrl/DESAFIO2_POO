package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
/** Tipo de paciente. Coincide con la columna tipo_paciente. */
public enum TipoPaciente {
 ESTUDIANTE("Estudiante"),
 DOCENTE("Docente"),
 ADMINISTRATIVO("Administrativo"),
 VISITANTE("Visitante");
 private final String texto;
 TipoPaciente(String texto) {
 this.texto = texto;
 }
 public String getTexto() {
 return texto;
 }
 public static TipoPaciente desdeTexto(String texto) {
 for (TipoPaciente t : values()) {
 if (t.texto.equalsIgnoreCase(texto)) {
 return t;
 }
 }
 throw new DatosInvalidosException("Tipo de paciente no válido: "
+ texto);
 }
 @Override
 public String toString() {
 return texto;
 }
}

