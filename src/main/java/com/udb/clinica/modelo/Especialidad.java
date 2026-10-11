package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
/** Especialidad del doctor. Coincide con la columna especialidad. */
public enum Especialidad {

 MEDICINA_GENERAL("Medicina General"),
 PSICOLOGIA("Psicología"),
 NUTRICION("Nutrición"),
 FISIOTERAPIA("Fisioterapia");
 private final String texto;
 Especialidad(String texto) {
 this.texto = texto;
 }
 public String getTexto() {
 return texto;
 }
 public static Especialidad desdeTexto(String texto) {
 for (Especialidad e : values()) {
 if (e.texto.equalsIgnoreCase(texto)) {
 return e;
 }
 }
 throw new DatosInvalidosException("Especialidad no válida: " +
texto);
 }
 @Override
 public String toString() {
 return texto;
 }
}

