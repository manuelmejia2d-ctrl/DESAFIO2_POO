package com.udb.clinica.vista;

import com.udb.clinica.dao.PacienteDAO;
import com.udb.clinica.modelo.EstadoPersona;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.TipoPaciente;
import com.udb.clinica.util.DatosInvalidosException;
import com.udb.clinica.util.Validador;
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
* Pantalla para administrar pacientes: formulario arriba, búsqueda y
tabla abajo.
* Esta clase solo se encarga de mostrar y recoger datos; el acceso a
MySQL
* lo hace PacienteDAO y las validaciones las hace el modelo.
*/
public class PanelPacientes extends JPanel {
 private final PacienteDAO dao = new PacienteDAO();
 // Lista que corresponde fila por fila con la tabla
 private List<Paciente> pacientes = new ArrayList<>();
 private final JTextField txtCodigo = new JTextField(12);
 private final JTextField txtNombre = new JTextField(20);
 private final JTextField txtEdad = new JTextField(5);
 private final JTextField txtTelefono = new JTextField(12);
 private final JTextField txtCorreo = new JTextField(20);
 private final JComboBox<TipoPaciente> cmbTipo = new
JComboBox<>(TipoPaciente.values());
 private final JComboBox<EstadoPersona> cmbEstado = new
JComboBox<>(EstadoPersona.values());
 private final JTextField txtBuscar = new JTextField(20);
 private final DefaultTableModel modelo = new DefaultTableModel(
 new String[]{"Código", "Nombre", "Edad", "Teléfono",
"Correo", "Tipo", "Estado"}, 0) {
 @Override
 public boolean isCellEditable(int fila, int columna) {
 return false; // la tabla es solo de lectura
 }
 };
 private final JTable tabla = new JTable(modelo);
 public PanelPacientes() {
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
 JPanel form = new JPanel(new GridLayout(4, 4, 8, 6));
 form.add(new JLabel("Código:")); form.add(txtCodigo);
 form.add(new JLabel("Nombre completo:")); form.add(txtNombre);
 form.add(new JLabel("Edad:")); form.add(txtEdad);
 form.add(new JLabel("Teléfono:")); form.add(txtTelefono);
 form.add(new JLabel("Correo:")); form.add(txtCorreo);
 form.add(new JLabel("Tipo de paciente:")); form.add(cmbTipo);
 form.add(new JLabel("Estado:")); form.add(cmbEstado);
 form.add(new JLabel("")); form.add(new JLabel(""));
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
 contenedor.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));
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
 barra.add(new JLabel("Buscar (código, nombre, correo o tipo):"));
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
 Paciente p = leerFormulario();
 dao.insertar(p);
 JOptionPane.showMessageDialog(this, "Paciente registrado correctamente.");
 limpiar();
 cargarTodos();
 } catch (DatosInvalidosException | SQLException ex) {
 mostrarError(ex);
 }
 }
 private void actualizar() {
 Paciente seleccionado = pacienteSeleccionado();
 if (seleccionado == null) {
 return;
 }
 try {
 Paciente p = leerFormulario();
 p.setId(seleccionado.getId());
 dao.actualizar(p);
 JOptionPane.showMessageDialog(this, "Datos actualizados.");
 cargarTodos();
 } catch (DatosInvalidosException | SQLException ex) {
 mostrarError(ex);
 }
 }
 private void cambiarEstado() {
 Paciente seleccionado = pacienteSeleccionado();
 if (seleccionado == null) {
 return;
 }
 EstadoPersona nuevo = seleccionado.estaActivo() ?
EstadoPersona.INACTIVO : EstadoPersona.ACTIVO;
 int resp = JOptionPane.showConfirmDialog(this,
 "¿Cambiar el estado de " +
seleccionado.getNombreCompleto() + " a " + nuevo + "?",
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
 Paciente seleccionado = pacienteSeleccionado();
 if (seleccionado == null) {
 return;
 }
 int resp = JOptionPane.showConfirmDialog(this,
 "¿Eliminar a " + seleccionado.getNombreCompleto() + "? Esta acción no se puede deshacer.",
 "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
JOptionPane.WARNING_MESSAGE);
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
 pacientes = dao.buscar(texto);
 llenarTabla();
 } catch (SQLException ex) {
 mostrarError(ex);
 }
 }
 // ---------- Apoyo ----------
 private void cargarTodos() {
 try {
 pacientes = dao.listar();
 llenarTabla();
 } catch (SQLException ex) {
 mostrarError(ex);
 }
 }
 private void llenarTabla() {
 modelo.setRowCount(0);
 for (Paciente p : pacientes) {
 modelo.addRow(new Object[]{p.getCodigo(),
p.getNombreCompleto(), p.getEdad(),
 p.getTelefono(), p.getCorreo(), p.getTipoPaciente(),
p.getEstado()});
 }
 }
 /** Lee el formulario y crea un Paciente. Si un dato es inválido, el
modelo lanza la excepción. */
 private Paciente leerFormulario() {
 int edad = Validador.enteroDesdeTexto(txtEdad.getText(), "edad");
 return new Paciente(txtCodigo.getText(), txtNombre.getText(),
edad, txtTelefono.getText(),
 txtCorreo.getText(), (TipoPaciente)
cmbTipo.getSelectedItem(),
 (EstadoPersona) cmbEstado.getSelectedItem());
 }
 private Paciente pacienteSeleccionado() {
 int fila = tabla.getSelectedRow();
 if (fila < 0) {
 JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.",
 "Aviso", JOptionPane.INFORMATION_MESSAGE);
 return null;
 }
 return pacientes.get(fila);
 }
 private void cargarFormularioDesdeSeleccion() {
 int fila = tabla.getSelectedRow();
 if (fila < 0) {
 return;
 }
 Paciente p = pacientes.get(fila);
 txtCodigo.setText(p.getCodigo());
 txtNombre.setText(p.getNombreCompleto());
 txtEdad.setText(String.valueOf(p.getEdad()));
 txtTelefono.setText(p.getTelefono());
 txtCorreo.setText(p.getCorreo());
 cmbTipo.setSelectedItem(p.getTipoPaciente());
 cmbEstado.setSelectedItem(p.getEstado());
 }
 private void limpiar() {
 txtCodigo.setText("");
 txtNombre.setText("");
 txtEdad.setText("");
 txtTelefono.setText("");
 txtCorreo.setText("");
 cmbTipo.setSelectedIndex(0);
 cmbEstado.setSelectedIndex(0);
 tabla.clearSelection();
 }
 /** Dato inválido: aviso amarillo con el motivo. Error de BD: mensaje
general (el detalle está en el log). */
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

