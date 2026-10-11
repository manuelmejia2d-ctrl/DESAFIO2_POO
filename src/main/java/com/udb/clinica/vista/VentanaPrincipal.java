package com.udb.clinica.vista;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
/**
* Ventana principal: una pestaña por cada módulo del sistema.
*/
public class VentanaPrincipal extends JFrame {
 public VentanaPrincipal() {
 super("Clínica Universitaria Vida UDB - Gestión de citas");
 setDefaultCloseOperation(EXIT_ON_CLOSE);
 setSize(1000, 650);
 setLocationRelativeTo(null); // centrada en la pantalla
 JTabbedPane pestanas = new JTabbedPane();
 pestanas.addTab("Pacientes", new PanelPacientes());

 // pestanas.addTab("Doctores", new PanelDoctores());
 // pestanas.addTab("Reportes", new PanelReportes());
 add(pestanas);
 }
}
