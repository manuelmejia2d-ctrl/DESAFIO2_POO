package com.udb.clinica.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
* Guarda los errores técnicos en el archivo logs/errores.log.
* Solo se escribe la fecha, el lugar del error y el mensaje.
* Nunca se escriben contraseñas.
*/
public final class RegistroErrores {
 private static final Path ARCHIVO = Paths.get("logs", "errores.log");
 private static final DateTimeFormatter FORMATO =
 DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
 private RegistroErrores() {
 }
 public static void registrar(String origen, Throwable error) {
 String mensaje = (error.getMessage() == null) ? "" :
error.getMessage().replace("\n", " ");
 String linea = LocalDateTime.now().format(FORMATO) + " | " +
origen + " | "
 + error.getClass().getSimpleName() + " | " + mensaje;
 try {
 Files.createDirectories(ARCHIVO.getParent());
 Files.writeString(ARCHIVO, linea + System.lineSeparator(),
StandardCharsets.UTF_8,
 StandardOpenOption.CREATE,
StandardOpenOption.APPEND);
 } catch (IOException e) {
 // Si falla el log, avisamos por consola para no ocultar el problema
 
 System.err.println("No se pudo escribir en el log: " +
e.getMessage());
 }
 }
}

