package com.udb.clinica.vista;

import com.udb.clinica.dao.CitaDAO;
import com.udb.clinica.dao.DoctorDAO;
import com.udb.clinica.dao.PacienteDAO;
import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.EstadoCita;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.Persona;
import com.udb.clinica.servicio.CitaServicio;
import com.udb.clinica.util.DatosInvalidosException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
/**
* Pantalla para programar, consultar, buscar y cambiar el estado de las
citas.
* Las reglas (solapamiento, inactivos) las aplica CitaServicio.
*/
public class PanelCitas extends JPanel {
 private final CitaDAO citaDAO = new CitaDAO();
 private final PacienteDAO pacienteDAO = new PacienteDAO();
 private final DoctorDAO doctorDAO = new DoctorDAO();
 private final CitaServicio servicio = new CitaServicio();
 private List<Cita> citas = new ArrayList<>();
 private final JComboBox<Paciente> cmbPaciente = new JComboBox<>();
 private final JComboBox<Doctor> cmbDoctor = new JComboBox<>();
 private final JTextField txtFecha = new JTextField(10);
 private final JTextField txtHora = new JTextField(6);
 private final JTextField txtMotivo = new JTextField(25);
 private final JTextField txtBuscar = new JTextField(20);
 private final DefaultTableModel modelo = new DefaultTableModel(
 new String[]{"N.º cita", "Paciente", "Doctor", "Fecha",
"Hora", "Motivo", "Estado"}, 0) {
 @Override
 public boolean isCellEditable(int fila, int columna) {
 return false;
 }
 };
 private final JTable tabla = new JTable(modelo);
 /** Muestra el resumen de la persona y avisa si está inactiva. */
 private static class RenderPersona extends DefaultListCellRenderer {
 @Override
 public Component getListCellRendererComponent(JList<?> lista,
Object valor, int indice,
 boolean
seleccionado, boolean foco) {
 super.getListCellRendererComponent(lista, valor, indice,
seleccionado, foco);
 if (valor instanceof Persona) {
 Persona p = (Persona) valor;
 setText(p.getResumen() + (p.estaActivo() ? "" : " [Inactivo]"));
 }
 return this;
 }
 }
 public PanelCitas() {
 setLayout(new BorderLayout(8, 8));
 setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 cmbPaciente.setRenderer(new RenderPersona());
 cmbDoctor.setRenderer(new RenderPersona());
 tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 add(crearFormulario(), BorderLayout.NORTH);
 add(crearZonaTabla(), BorderLayout.CENTER);
 cargarListas();
 cargarTodas();
 }
 // ---------- Construcción de la pantalla ----------
 private JPanel crearFormulario() {
 JPanel form = new JPanel(new GridLayout(3, 4, 8, 6));
 form.add(new JLabel("Paciente:"));
form.add(cmbPaciente);
 form.add(new JLabel("Doctor:")); form.add(cmbDoctor);
 form.add(new JLabel("Fecha (AAAA-MM-DD):")); form.add(txtFecha);
 form.add(new JLabel("Hora (HH:mm):")); form.add(txtHora);
 form.add(new JLabel("Motivo de consulta:")); form.add(txtMotivo);
 form.add(new JLabel("")); form.add(new
JLabel(""));
 JButton btnProgramar = new JButton("Programar cita");
 JButton btnAtendida = new JButton("Marcar atendida");
 JButton btnCancelar = new JButton("Cancelar cita");
 JButton btnLimpiar = new JButton("Limpiar");
 JButton btnListas = new JButton("Actualizar listas");
 btnProgramar.addActionListener(e -> programar());
 btnAtendida.addActionListener(e -> cambiarEstado(true));
 btnCancelar.addActionListener(e -> cambiarEstado(false));
 btnLimpiar.addActionListener(e -> limpiar());
 btnListas.addActionListener(e -> cargarListas());
 JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
 botones.add(btnProgramar);
 botones.add(btnAtendida);
 botones.add(btnCancelar);
 botones.add(btnLimpiar);
 botones.add(btnListas);
 JPanel contenedor = new JPanel(new BorderLayout(0, 6));
 contenedor.setBorder(BorderFactory.createTitledBorder("Datos de la cita"));
 contenedor.add(form, BorderLayout.CENTER);
 contenedor.add(botones, BorderLayout.SOUTH);
 return contenedor;
 }
 private JPanel crearZonaTabla() {
 JButton btnBuscar = new JButton("Buscar");
 JButton btnTodas = new JButton("Mostrar todas");
 btnBuscar.addActionListener(e -> buscar());
 btnTodas.addActionListener(e -> {
 txtBuscar.setText("");
 cargarTodas();
 });
 JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
 barra.add(new JLabel("Buscar (n.º, paciente, doctor, fecha o estado):"));
 barra.add(txtBuscar);
 barra.add(btnBuscar);
 barra.add(btnTodas);
 JPanel zona = new JPanel(new BorderLayout(0, 4));
 zona.add(barra, BorderLayout.NORTH);
 zona.add(new JScrollPane(tabla), BorderLayout.CENTER);
 return zona;
 }
 // ---------- Acciones ----------
 private void programar() {
 try {
 Paciente paciente = (Paciente) cmbPaciente.getSelectedItem();
 Doctor doctor = (Doctor) cmbDoctor.getSelectedItem();
 if (paciente == null) {
 throw new DatosInvalidosException("Debe seleccionar un paciente.");
 }
 if (doctor == null) {
 throw new DatosInvalidosException("Debe seleccionar un doctor.");
 }
 Cita cita = new Cita(paciente.getId(), doctor.getId(),
leerFecha(), leerHora(),
 txtMotivo.getText(), EstadoCita.PROGRAMADA);
 servicio.programar(cita);
 JOptionPane.showMessageDialog(this, "Cita programada correctamente (n.º " + cita.getIdCita() + ").");
 limpiar();
 cargarTodas();
 } catch (DatosInvalidosException | SQLException ex) {
 mostrarError(ex);
 }
 }
 /** atendida = true marca como Atendida; false cancela. */
 private void cambiarEstado(boolean atendida) {
 int fila = tabla.getSelectedRow();
 if (fila < 0) {
 JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla.",
 "Aviso", JOptionPane.INFORMATION_MESSAGE);
 return;
 }
 Cita cita = citas.get(fila);
 String accion = atendida ? "marcar como atendida" : "cancelar";
 int resp = JOptionPane.showConfirmDialog(this,
 "¿Desea " + accion + " la cita n.º " + cita.getIdCita() +
"?",
 "Confirmar", JOptionPane.YES_NO_OPTION);
 if (resp != JOptionPane.YES_OPTION) {
 return;
 }
 try {
 if (atendida) {
 servicio.marcarAtendida(cita.getIdCita());
 } else {
 servicio.cancelar(cita.getIdCita());
 }
 cargarTodas();
 } catch (DatosInvalidosException | SQLException ex) {
 mostrarError(ex);
 }
 }
 private void buscar() {
 String texto = txtBuscar.getText().trim();
 if (texto.isEmpty()) {
 cargarTodas();
 return;
 }
 try {
 citas = citaDAO.buscar(texto);
 llenarTabla();
 } catch (SQLException ex) {
 mostrarError(ex);
 }
 }
 // ---------- Apoyo ----------
 /** Llena las listas desplegables. Se puede repetir si se agregaron
pacientes o doctores. */
 private void cargarListas() {
 try {
 cmbPaciente.removeAllItems();
 for (Paciente p : pacienteDAO.listar()) {
 cmbPaciente.addItem(p);
 }
 cmbDoctor.removeAllItems();
 for (Doctor d : doctorDAO.listar()) {
 cmbDoctor.addItem(d);
 }
 } catch (SQLException ex) {
 mostrarError(ex);
 }
 }
 private void cargarTodas() {
 try {
 citas = citaDAO.listar();
 llenarTabla();
 } catch (SQLException ex) {
 mostrarError(ex);
 }
 }
 private void llenarTabla() {
 modelo.setRowCount(0);
 for (Cita c : citas) {
 modelo.addRow(new Object[]{c.getIdCita(),
c.getNombrePaciente(), c.getNombreDoctor(),
 c.getFecha(), c.getHora(), c.getMotivo(),
c.getEstado()});
 }
 }
 private LocalDate leerFecha() {
 try {
 return LocalDate.parse(txtFecha.getText().trim());
 } catch (DateTimeParseException e) {
 throw new DatosInvalidosException("La fecha debe tener el formato AAAA-MM-DD, por ejemplo 2026-10-15.");
 }
 }
 private LocalTime leerHora() {
 try {
 return LocalTime.parse(txtHora.getText().trim());
 } catch (DateTimeParseException e) {
 throw new DatosInvalidosException("La hora debe tener el formato HH:mm, por ejemplo 09:30.");
 }
 }
 private void limpiar() {
 txtFecha.setText("");
 txtHora.setText("");
 txtMotivo.setText("");
 tabla.clearSelection();
 }
 private void mostrarError(Exception ex) {
 if (ex instanceof DatosInvalidosException) {
 JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo realizar la acción",
 JOptionPane.WARNING_MESSAGE);
 } else {
 JOptionPane.showMessageDialog(this,
 "No se pudo completar la operación con la base de datos.\n"
 + "Revise que MySQL esté encendido y consultelogs/errores.log.",
 "Error", JOptionPane.ERROR_MESSAGE);
 }

}

}

