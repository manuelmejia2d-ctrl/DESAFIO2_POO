package com.udb.clinica;

import com.udb.clinica.vista.PanelDoctores;
import com.udb.clinica.vista.PanelReportes;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/** Ventana temporal solo para probar PanelDoctores y PanelReportes. */
public class ProbarMisPaneles {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Prueba - Doctores y Reportes");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(1000, 600);
            ventana.setLocationRelativeTo(null);
            JTabbedPane pestanas = new JTabbedPane();
            pestanas.addTab("Doctores", new PanelDoctores());
            pestanas.addTab("Reportes", new PanelReportes());
            ventana.add(pestanas);
            ventana.setVisible(true);
        });
    }
}