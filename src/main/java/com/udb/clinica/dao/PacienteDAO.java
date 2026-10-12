package com.udb.clinica.dao;


import com.udb.clinica.modelo.EstadoPersona;
import com.udb.clinica.modelo.Paciente;
import com.udb.clinica.modelo.TipoPaciente;
import com.udb.clinica.util.ConexionBD;
import com.udb.clinica.util.DatosInvalidosException;
import com.udb.clinica.util.RegistroErrores;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
* Acceso a datos de la tabla pacientes.
* Todas las consultas usan PreparedStatement (con ?) para que los datos
* viajen separados de la sentencia SQL y no haya inyección SQL.
*/
public class PacienteDAO {
 private static final String SELECT_BASE =
 "SELECT id_paciente, codigo, nombre_completo, edad, telefono, correo, "
 + "tipo_paciente, estado FROM pacientes";
 /** Inserta un paciente nuevo y le asigna el id que generó MySQL. */
 public void insertar(Paciente p) throws SQLException {
 String sql = "INSERT INTO pacientes (codigo, nombre_completo, edad, telefono, correo, "
 + "tipo_paciente, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql,
Statement.RETURN_GENERATED_KEYS)) {
 ps.setString(1, p.getCodigo());
 ps.setString(2, p.getNombreCompleto());
 ps.setInt(3, p.getEdad());
 ps.setString(4, p.getTelefono());
 ps.setString(5, p.getCorreo());
 ps.setString(6, p.getTipoPaciente().getTexto());
 ps.setString(7, p.getEstado().getTexto());
 ps.executeUpdate();
 try (ResultSet claves = ps.getGeneratedKeys()) {
 if (claves.next()) {
 p.setId(claves.getInt(1));
 }
 }
 } catch (SQLIntegrityConstraintViolationException e) {
 // Error 1062: se violó el UNIQUE de codigo o correo
 throw new DatosInvalidosException("Ya existe un paciente con ese código o correo.");
 } catch (SQLException e) {
 RegistroErrores.registrar("PacienteDAO.insertar", e);
 throw e;
 }
 }
 /** Todos los pacientes, ordenados por código. */
 public List<Paciente> listar() throws SQLException {
 return consultar(SELECT_BASE + " ORDER BY codigo", null);
 }
 /** Busca por parte del código, nombre, correo o tipo de paciente. */
 public List<Paciente> buscar(String texto) throws SQLException {
 String sql = SELECT_BASE  + " WHERE codigo LIKE ? OR nombre_completo LIKE ? OR correo LIKE ? OR tipo_paciente LIKE ?"
 + " ORDER BY codigo";
 String patron = "%" + texto.trim() + "%";
 return consultar(sql, new String[]{patron, patron, patron,
patron});
 }
 /** Busca por id. Devuelve null si no existe. */
 public Paciente buscarPorId(int id) throws SQLException {
 String sql = SELECT_BASE + " WHERE id_paciente = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setInt(1, id);
 try (ResultSet rs = ps.executeQuery()) {
 return rs.next() ? mapear(rs) : null;
 }
 } catch (SQLException e) {
 RegistroErrores.registrar("PacienteDAO.buscarPorId", e);
 throw e;
 }
 }
 /** Actualiza todos los datos de un paciente. true si se modificó una
fila. */
 public boolean actualizar(Paciente p) throws SQLException {
 String sql = "UPDATE pacientes SET codigo = ?, nombre_completo = ?, edad = ?, telefono = ?, "
 + "correo = ?, tipo_paciente = ?, estado = ? WHERE id_paciente = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setString(1, p.getCodigo());
 ps.setString(2, p.getNombreCompleto());
 ps.setInt(3, p.getEdad());
 ps.setString(4, p.getTelefono());
 ps.setString(5, p.getCorreo());
 ps.setString(6, p.getTipoPaciente().getTexto());
 ps.setString(7, p.getEstado().getTexto());
 ps.setInt(8, p.getId());
 return ps.executeUpdate() > 0;
 } catch (SQLIntegrityConstraintViolationException e) {
 throw new DatosInvalidosException("Ya existe otro paciente con ese código o correo.");
 } catch (SQLException e) {
 RegistroErrores.registrar("PacienteDAO.actualizar", e);
 throw e;
 }
 }
 /** Cambia solo el estado (Activo o Inactivo). */
 public boolean cambiarEstado(int id, EstadoPersona estado) throws
SQLException {
 String sql = "UPDATE pacientes SET estado = ? WHERE id_paciente = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setString(1, estado.getTexto());
 ps.setInt(2, id);
 return ps.executeUpdate() > 0;
 } catch (SQLException e) {
 RegistroErrores.registrar("PacienteDAO.cambiarEstado", e);
 throw e;
 }
 }
 /** Elimina un paciente. MySQL lo impide si tiene citas (ON DELETE
RESTRICT). */
 public boolean eliminar(int id) throws SQLException {
 String sql = "DELETE FROM pacientes WHERE id_paciente = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setInt(1, id);
 return ps.executeUpdate() > 0;
 } catch (SQLIntegrityConstraintViolationException e) {
 // Error 1451: hay citas que dependen de este paciente
 throw new DatosInvalidosException(
 "No se puede eliminar: el paciente tiene citas. Cámbielo a Inactivo.");
 } catch (SQLException e) {
 RegistroErrores.registrar("PacienteDAO.eliminar", e);
 throw e;
 }
 }
 // ----- Apoyo -----
 /** Ejecuta un SELECT con parámetros de texto opcionales y arma la
lista. */
 private List<Paciente> consultar(String sql, String[] parametros)
throws SQLException {
 List<Paciente> lista = new ArrayList<>();
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 if (parametros != null) {
 for (int i = 0; i < parametros.length; i++) {
 ps.setString(i + 1, parametros[i]);
 }
 }
 try (ResultSet rs = ps.executeQuery()) {
 while (rs.next()) {
 lista.add(mapear(rs));
 }
 }
 } catch (SQLException e) {
 RegistroErrores.registrar("PacienteDAO.consultar", e);
 throw e;
 }
 return lista;
 }
 /** Convierte una fila del ResultSet en un objeto Paciente. */
 private Paciente mapear(ResultSet rs) throws SQLException {
 Paciente p = new Paciente(
 rs.getString("codigo"),
 rs.getString("nombre_completo"),
 rs.getInt("edad"),
 rs.getString("telefono"),
 rs.getString("correo"),
 TipoPaciente.desdeTexto(rs.getString("tipo_paciente")),
 EstadoPersona.desdeTexto(rs.getString("estado")));
 p.setId(rs.getInt("id_paciente"));
 return p;
 }
}
