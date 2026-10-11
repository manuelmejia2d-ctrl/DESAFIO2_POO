package com.udb.clinica;

import com.udb.clinica.util.ConexionBD;
import com.udb.clinica.util.RegistroErrores;
import com.udb.clinica.vista.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.Connection;
import java.sql.SQLException;
/**
* Clase principal. Comprueba la conexión con MySQL y abre la ventana.
*/
public class Main {
 public static void main(String[] args) {
 // Si MySQL no responde, avisamos con un mensaje claro y no abrimos la ventana
 try (Connection con = ConexionBD.obtenerConexion()) {
 // conexión correcta: se cierra sola al salir del try
 } catch (SQLException e) {
 JOptionPane.showMessageDialog(null,
 "No se pudo conectar a MySQL.\n"
 + "Revise que el servicio esté encendido y que database.properties tenga sus datos.\n\n"
 + e.getMessage(),
 "Error de conexión", JOptionPane.ERROR_MESSAGE);
 return;
 }
 SwingUtilities.invokeLater(() -> {
 try {

UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 } catch (Exception e) {
 RegistroErrores.registrar("Main.lookAndFeel", e);
 }
 new VentanaPrincipal().setVisible(true);
 });
 }
}
 