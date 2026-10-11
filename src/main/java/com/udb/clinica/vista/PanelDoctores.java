package com.udb.clinica.vista;

import com.udb.clinica.dao.DoctorDAO;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.Especialidad;
import com.udb.clinica.modelo.EstadoPersona;
import com.udb.clinica.util.DatosInvalidosException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla para administrar doctores: formulario arriba, búsqueda y tabla abajo.
 * Solo muestra y recoge datos; el acceso a MySQL lo hace DoctorDAO
 * y las validaciones las hace la clase Doctor.
 */
public class PanelDoctores extends JPanel {

    private final DoctorDAO dao = new DoctorDAO();
    // Lista que corresponde fila por fila con la tabla
    private List<Doctor> doctores = new ArrayList<>();

    private final JTextField txtCodigo = new JTextField(12);
    private final JTextField txtNombre = new JTextField(20);
    private final JComboBox<Especialidad> cmbEspecialidad = new JComboBox<>(Especialidad.values());
    private final JTextField txtTelefono = new JTextField(12);
    private final JTextField txtCorreo = new JTextField(20);
    private final JComboBox<EstadoPersona> cmbEstado = new JComboBox<>(EstadoPersona.values());
    private final JTextField txtBuscar = new JTextField(20);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Código", "Nombre", "Especialidad", "Teléfono", "Correo", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false; // la tabla es solo de lectura
        }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelDoctores() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearZonaTabla(), BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFormularioDesdeSeleccion();
            }
        });
        cargarTodos();
    }

    // ---------- Construcción de la pantalla ----------

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 6));
        form.add(new JLabel("Código:"));           form.add(txtCodigo);
        form.add(new JLabel("Nombre completo:"));  form.add(txtNombre);
        form.add(new JLabel("Especialidad:"));     form.add(cmbEspecialidad);
        form.add(new JLabel("Teléfono:"));         form.add(txtTelefono);
        form.add(new JLabel("Correo:"));           form.add(txtCorreo);
        form.add(new JLabel("Estado:"));           form.add(cmbEstado);

        JButton btnGuardar = new JButton("Guardar nuevo");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEstado = new JButton("Cambiar estado");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnGuardar.addActionListener(e -> guardarNuevo());
        btnActualizar.addActionListener(e -> actualizar());
        btnEstado.addActionListener(e -> cambiarEstado());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEstado);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel contenedor = new JPanel(new BorderLayout(0, 6));
        contenedor.setBorder(BorderFactory.createTitledBorder("Datos del doctor"));
        contenedor.add(form, BorderLayout.CENTER);
        contenedor.add(botones, BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearZonaTabla() {
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todos");
        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTodos();
        });

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Buscar (código, nombre o especialidad):"));
        barra.add(txtBuscar);
        barra.add(btnBuscar);
        barra.add(btnTodos);

        JPanel zona = new JPanel(new BorderLayout(0, 4));
        zona.add(barra, BorderLayout.NORTH);
        zona.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return zona;
    }

    // ---------- Acciones de los botones ----------

    private void guardarNuevo() {
        try {
            Doctor d = leerFormulario();
            dao.insertar(d);
            JOptionPane.showMessageDialog(this, "Doctor registrado correctamente.");
            limpiar();
            cargarTodos();
        } catch (DatosInvalidosException | SQLException ex) {
            mostrarError(ex);
        }
    }

    private void actualizar() {
        Doctor seleccionado = doctorSeleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            Doctor d = leerFormulario();
            d.setId(seleccionado.getId());
            dao.actualizar(d);
            JOptionPane.showMessageDialog(this, "Datos actualizados.");
            cargarTodos();
        } catch (DatosInvalidosException | SQLException ex) {
            mostrarError(ex);
        }
    }

    private void cambiarEstado() {
        Doctor seleccionado = doctorSeleccionado();
        if (seleccionado == null) {
            return;
        }
        EstadoPersona nuevo = seleccionado.estaActivo() ? EstadoPersona.INACTIVO : EstadoPersona.ACTIVO;
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Cambiar el estado de " + seleccionado.getNombreCompleto() + " a " + nuevo + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.cambiarEstado(seleccionado.getId(), nuevo);
            cargarTodos();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        Doctor seleccionado = doctorSeleccionado();
        if (seleccionado == null) {
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + seleccionado.getNombreCompleto() + "? Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.eliminar(seleccionado.getId());
            limpiar();
            cargarTodos();
        } catch (DatosInvalidosException | SQLException ex) {
            mostrarError(ex);
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarTodos();
            return;
        }
        try {
            doctores = dao.buscar(texto);
            llenarTabla();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    // ---------- Apoyo ----------

    private void cargarTodos() {
        try {
            doctores = dao.listar();
            llenarTabla();
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void llenarTabla() {
        modelo.setRowCount(0);
        for (Doctor d : doctores) {
            modelo.addRow(new Object[]{d.getCodigo(), d.getNombreCompleto(), d.getEspecialidad(),
                    d.getTelefono(), d.getCorreo(), d.getEstado()});
        }
    }

    /** Lee el formulario y crea un Doctor. Si un dato es inválido, el modelo lanza la excepción. */
    private Doctor leerFormulario() {
        return new Doctor(txtCodigo.getText(), txtNombre.getText(),
                (Especialidad) cmbEspecialidad.getSelectedItem(), txtTelefono.getText(),
                txtCorreo.getText(), (EstadoPersona) cmbEstado.getSelectedItem());
    }

    private Doctor doctorSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un doctor de la tabla.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return doctores.get(fila);
    }

    private void cargarFormularioDesdeSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Doctor d = doctores.get(fila);
        txtCodigo.setText(d.getCodigo());
        txtNombre.setText(d.getNombreCompleto());
        cmbEspecialidad.setSelectedItem(d.getEspecialidad());
        txtTelefono.setText(d.getTelefono());
        txtCorreo.setText(d.getCorreo());
        cmbEstado.setSelectedItem(d.getEstado());
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbEspecialidad.setSelectedIndex(0);
        txtTelefono.setText("");
        txtCorreo.setText("");
        cmbEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }

    /** Dato inválido: aviso con el motivo. Error de BD: mensaje general (el detalle está en el log). */
    private void mostrarError(Exception ex) {
        if (ex instanceof DatosInvalidosException) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos no válidos",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo completar la operación con la base de datos.\n"
                            + "Revise que MySQL esté encendido y consulte logs/errores.log.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}