package com.udb.clinica.vista;

import com.udb.clinica.dao.ReporteDAO;
import com.udb.clinica.dao.ReporteDAO.Reporte;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;

/**
 * Pantalla de reportes: se elige un reporte de la lista y se muestra en una tabla.
 */
public class PanelReportes extends JPanel {

    private final ReporteDAO dao = new ReporteDAO();

    private static final String[] OPCIONES = {
            "Citas programadas",
            "Citas atendidas",
            "Citas canceladas",
            "Cantidad de citas por estado",
            "Citas por doctor",
            "Citas por fecha",
            "Pacientes registrados"
    };

    private final JComboBox<String> cmbReporte = new JComboBox<>(OPCIONES);
    private final JLabel lblResumen = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelReportes() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnGenerar = new JButton("Generar reporte");
        btnGenerar.addActionListener(e -> generar());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBorder(BorderFactory.createTitledBorder("Reporte"));
        barra.add(new JLabel("Seleccione:"));
        barra.add(cmbReporte);
        barra.add(btnGenerar);

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);
    }

    private void generar() {
        try {
            Reporte r;
            switch (cmbReporte.getSelectedIndex()) {
                case 0:  r = dao.citasDeEstado("Programada"); break;
                case 1:  r = dao.citasDeEstado("Atendida"); break;
                case 2:  r = dao.citasDeEstado("Cancelada"); break;
                case 3:  r = dao.citasPorEstado(); break;
                case 4:  r = dao.citasPorDoctor(); break;
                case 5:  r = dao.citasPorFecha(); break;
                default: r = dao.totalPacientes(); break;
            }
            modelo.setColumnIdentifiers(r.columnas);
            modelo.setRowCount(0);
            for (Object[] fila : r.filas) {
                modelo.addRow(fila);
            }
            lblResumen.setText("Filas mostradas: " + r.filas.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el reporte.\n"
                            + "Revise que MySQL esté encendido y consulte logs/errores.log.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}