package com.udb.clinica.modelo;

import com.udb.clinica.util.Validador;
/**
* Clase padre de Paciente y Doctor.
* Ambos comparten: código, nombre, teléfono, correo y estado.
* Es abstracta porque en la clínica nunca existe una "persona" sola:
* siempre es un paciente o un doctor.
*
* Encapsulamiento: los atributos son privados y solo se cambian
* con los setters, que validan el dato antes de guardarlo.
*/
public abstract class Persona {
 private int id; // id_paciente o id_doctor, según la clase hija
 private String codigo;
 private String nombreCompleto;
 private String telefono;
 private String correo;
 private EstadoPersona estado;
 public Persona(String codigo, String nombreCompleto, String telefono,
 String correo, EstadoPersona estado) {
 setCodigo(codigo);
 setNombreCompleto(nombreCompleto);
 setTelefono(telefono);
 setCorreo(correo);
 setEstado(estado);
 }
 // Método abstracto: cada clase hija lo implementa a su manera
 public abstract String getResumen();
 /** Indica si la persona puede recibir o dar atención. */
 public boolean estaActivo() {
 return estado == EstadoPersona.ACTIVO;
 }
 // ----- Getters y setters -----
 public int getId() {
 return id;
 }
 public void setId(int id) {
 this.id = id;
 }
 public String getCodigo() {
 return codigo;
 }
 public void setCodigo(String codigo) {
 this.codigo = Validador.textoObligatorio(codigo, "código", 15);
 }
 public String getNombreCompleto() {
 return nombreCompleto;
 }
 public void setNombreCompleto(String nombreCompleto) {
 this.nombreCompleto = Validador.textoObligatorio(nombreCompleto,
"nombre completo", 100);
 }
 public String getTelefono() {
 return telefono;
 }
 public void setTelefono(String telefono) {
 this.telefono = Validador.telefono(telefono);
 }
 public String getCorreo() {
 return correo;
 }
 public void setCorreo(String correo) {
 this.correo = Validador.correo(correo);
 }
 public EstadoPersona getEstado() {
 return estado;
 }
 public void setEstado(EstadoPersona estado) {
 if (estado == null) {
 throw new com.udb.clinica.util.DatosInvalidosException("El estado es obligatorio.");
 }
 this.estado = estado;
 }
}

