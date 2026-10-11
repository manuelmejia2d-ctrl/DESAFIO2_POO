package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
/** Estado de una cita. Coincide con
ENUM('Programada','Atendida','Cancelada'). */
public enum EstadoCita {
 PROGRAMADA("Programada"),
 ATENDIDA("Atendida"),
 CANCELADA("Cancelada");
 private final String texto;
 EstadoCita(String texto) {
 this.texto = texto;
 }
 public String getTexto() {
 return texto;
 }
 public static EstadoCita desdeTexto(String texto) {
 for (EstadoCita e : values()) {
 if (e.texto.equalsIgnoreCase(texto)) {
 return e;
 }
 }
 throw new DatosInvalidosException("Estado de cita no válido: " +
texto);
 }
 @Override
 public String toString() {
 return texto;
 }
}
