package com.udb.clinica.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
/**
* Se encarga de abrir la conexión con MySQL.
* Lee la URL, el usuario y la contraseña del archivo
database.properties,
* que NO se sube a GitHub, para no dejar contraseñas en el código.
*/
public final class ConexionBD {
 private static final String ARCHIVO_CONFIG = "database.properties";
 private ConexionBD() {
 }
 /** Abre y devuelve una conexión nueva. Quien la use debe cerrarla
(try-with-resources). */
 public static Connection obtenerConexion() throws SQLException {
 Properties config = cargarConfiguracion();
 String url = config.getProperty("db.url");
 String usuario = config.getProperty("db.user");
 String clave = config.getProperty("db.password");
 if (url == null || usuario == null || clave == null) {
 throw new SQLException("Faltan datos en database.properties (db.url, db.user, db.password).");
 }
 try {
 return DriverManager.getConnection(url, usuario, clave);
 } catch (SQLException e) {
 RegistroErrores.registrar("ConexionBD.obtenerConexion", e);
 throw e;
 }
 }
 /** Lee el archivo de configuración desde src/main/resources. */
 private static Properties cargarConfiguracion() throws SQLException {
 Properties config = new Properties();
 try (InputStream entrada =

ConexionBD.class.getClassLoader().getResourceAsStream(ARCHIVO_CONFIG)) {
 if (entrada == null) {
 throw new SQLException("No se encontró database.properties. "
 + "Copie database.properties.example y escriba su contraseña.");
 }
 config.load(entrada);
 } catch (IOException e) {
 RegistroErrores.registrar("ConexionBD.cargarConfiguracion",
e);
 throw new SQLException("No se pudo leer database.properties.", e);
 }
 return config;
 }
}
