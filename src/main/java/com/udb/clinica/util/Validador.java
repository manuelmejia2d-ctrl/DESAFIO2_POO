package com.udb.clinica.util;

/**
* Métodos estáticos para validar datos. Así no repetimos el mismo
* código de validación en cada clase del modelo.
*/
public final class Validador {
 // Constructor privado: esta clase solo se usa con métodos estáticos
 private Validador() {
 }
 /** Verifica que el texto no sea nulo ni vacío y que no exceda el
máximo. */
 public static String textoObligatorio(String valor, String campo, int
maximo) {
 if (valor == null || valor.trim().isEmpty()) {
 throw new DatosInvalidosException("El campo " + campo + " es obligatorio.");
 }
 String limpio = valor.trim();
 if (limpio.length() > maximo) {
 throw new DatosInvalidosException("El campo " + campo
 + " no puede tener más de " + maximo + "caracteres.");
 }
 return limpio;
 }
 /** Teléfono: solo números, guion, signo + y espacios (8 a 15
caracteres). */
 public static String telefono(String valor) {
 String limpio = textoObligatorio(valor, "teléfono", 15);
 if (!limpio.matches("[0-9+\\- ]{8,15}")) {
 throw new DatosInvalidosException(
 "El teléfono solo puede tener números y guiones (mínimo 8 caracteres).");
 }
 return limpio;
 }
 /** Correo con formato básico: algo@dominio.ext */
public static String correo(String valor) {
    String limpio = textoObligatorio(valor, "correo", 100);
    if (!limpio.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        throw new DatosInvalidosException("El correo electrónico no tiene un formato válido.");
    }
    return limpio;
}
 /** Edad entre 0 y 120 (igual que el CHECK de la tabla pacientes). */
 public static int edad(int valor) {
 if (valor < 0 || valor > 120) {
 throw new DatosInvalidosException("La edad debe estar entre 0 y 120.");
 }
 return valor;
 }
 /** Convierte un texto a número entero. Sirve para campos de
formularios. */
 public static int enteroDesdeTexto(String texto, String campo) {
 if (texto == null || texto.trim().isEmpty()) {
 throw new DatosInvalidosException("El campo " + campo + " es obligatorio.");
 }
 try {
 return Integer.parseInt(texto.trim());
 } catch (NumberFormatException e) {
 throw new DatosInvalidosException("El campo " + campo + "debe ser un número entero.");
 }
 }
}

