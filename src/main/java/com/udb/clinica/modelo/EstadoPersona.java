package com.udb.clinica.modelo;

import com.udb.clinica.util.DatosInvalidosException;
/** Estado de un paciente o doctor. Coincide con
ENUM('Activo','Inactivo'). */
public enum EstadoPersona {
 ACTIVO("Activo"),
 INACTIVO("Inactivo");
 private final String texto;
 EstadoPersona(String texto) {
 this.texto = texto;
 }
 /** Texto tal como se guarda en MySQL. */
 public String getTexto() {
 return texto;
 }
 /** Convierte el texto de la base de datos al enum. */
 public static EstadoPersona desdeTexto(String texto) {
 for (EstadoPersona e : values()) {
 if (e.texto.equalsIgnoreCase(texto)) {
 return e;
 }
 }
 throw new DatosInvalidosException("Estado no válido: " + texto);
 }
 @Override
 public String toString() {
 return texto;
 }
}
