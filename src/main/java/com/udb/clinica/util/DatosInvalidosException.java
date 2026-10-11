package com.udb.clinica.util;

    /**
* Excepción propia del sistema. Se lanza cuando un dato no cumple
* las reglas (campo vacío, edad fuera de rango, correo mal escrito...).
* Extiende RuntimeException para no obligar a poner try-catch en cada
setter.
*/
public class DatosInvalidosException extends RuntimeException {
 public DatosInvalidosException(String mensaje) {
 super(mensaje);
 }
}


