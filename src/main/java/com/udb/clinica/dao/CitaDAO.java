package com.udb.clinica.dao;

import com.udb.clinica.modelo.Cita;
import com.udb.clinica.modelo.EstadoCita;
import com.udb.clinica.util.ConexionBD;
import com.udb.clinica.util.DatosInvalidosException;
import com.udb.clinica.util.RegistroErrores;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
/**
* Acceso a datos de la tabla citas.
* Las consultas de lectura hacen JOIN con pacientes y doctores para
traer los nombres.
*/
public class CitaDAO {
private static final String SELECT_BASE =
"SELECT c.id_cita, c.id_paciente, c.id_doctor, c.fecha, c.hora, c.motivo, c.estado, " + 
"p.nombre_completo AS paciente, d.nombre_completo AS doctor " +
"FROM citas c " +
"JOIN pacientes p ON c.id_paciente = p.id_paciente " +
"JOIN doctores d ON c.id_doctor = d.id_doctor";
 /** Inserta una cita nueva y le asigna el id generado. */
 public void insertar(Cita c) throws SQLException {
 String sql = "INSERT INTO citas (id_paciente, id_doctor, fecha, hora, motivo, estado) "
 + "VALUES (?, ?, ?, ?, ?, ?)";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql,
Statement.RETURN_GENERATED_KEYS)) {
 ps.setInt(1, c.getIdPaciente());
 ps.setInt(2, c.getIdDoctor());
 ps.setDate(3, Date.valueOf(c.getFecha()));
 ps.setTime(4, Time.valueOf(c.getHora()));
 ps.setString(5, c.getMotivo());
 ps.setString(6, c.getEstado().getTexto());
 ps.executeUpdate();
 try (ResultSet claves = ps.getGeneratedKeys()) {
 if (claves.next()) {
 c.setIdCita(claves.getInt(1));
 }
 }
 } catch (SQLIntegrityConstraintViolationException e) {
 // Error 1452: el paciente o el doctor no existe (llave foránea)
 throw new DatosInvalidosException("El paciente o el doctor indicado no existe.");
 } catch (SQLException e) {
 RegistroErrores.registrar("CitaDAO.insertar", e);
 throw e;
 }
 }
 /** Todas las citas, ordenadas por fecha y hora. */
 public List<Cita> listar() throws SQLException {
 return consultar(SELECT_BASE + " ORDER BY c.fecha, c.hora",
null);
 }
 /** Busca por número de cita, nombre del paciente, nombre del doctor,
fecha (AAAA-MM-DD) o estado. */
 public List<Cita> buscar(String texto) throws SQLException {
 String sql = SELECT_BASE
 + " WHERE CAST(c.id_cita AS CHAR) LIKE ? OR p.nombre_completo LIKE ?" + " OR d.nombre_completo LIKE ? OR CAST(c.fecha AS CHAR) LIKE ? OR c.estado LIKE ?"
 + " ORDER BY c.fecha, c.hora";
 String patron = "%" + texto.trim() + "%";
 return consultar(sql, new String[]{patron, patron, patron,
patron, patron});
 }
 /** Busca una cita por id. Devuelve null si no existe. */
 public Cita buscarPorId(int id) throws SQLException {
 String sql = SELECT_BASE + " WHERE c.id_cita = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setInt(1, id);
 try (ResultSet rs = ps.executeQuery()) {
 return rs.next() ? mapear(rs) : null;
 }
 } catch (SQLException e) {
 RegistroErrores.registrar("CitaDAO.buscarPorId", e);
 throw e;
 }
 }
 /** Cambia el estado de una cita (Programada, Atendida o Cancelada).
*/
 public boolean cambiarEstado(int id, EstadoCita estado) throws
SQLException {
 String sql = "UPDATE citas SET estado = ? WHERE id_cita = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setString(1, estado.getTexto());
 ps.setInt(2, id);
 return ps.executeUpdate() > 0;
 } catch (SQLException e) {
 RegistroErrores.registrar("CitaDAO.cambiarEstado", e);
 throw e;
 }
 }
 /** Elimina una cita. Se usa sobre todo para limpiar las pruebas. */
 public boolean eliminar(int id) throws SQLException {
 String sql = "DELETE FROM citas WHERE id_cita = ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setInt(1, id);
 return ps.executeUpdate() > 0;
 } catch (SQLException e) {
 RegistroErrores.registrar("CitaDAO.eliminar", e);
 throw e;
 }
 }
 /**
 * Indica si el doctor ya tiene una cita PROGRAMADA que se cruza con
la hora indicada.
 * Dos citas se cruzan si empiezan con menos de 30 minutos de
diferencia.
 * Las citas Canceladas o Atendidas no cuentan, así que una cancelada
libera el horario.
 * idCitaExcluir sirve al reprogramar (para no compararla consigo
misma); si es nueva, usar 0.
 */
 public boolean existeSolapamiento(int idDoctor, LocalDate fecha,
LocalTime hora, int idCitaExcluir)
 throws SQLException {
 String sql = "SELECT COUNT(*) FROM citas WHERE id_doctor = ? AND fecha = ? "
 + "AND estado = 'Programada' AND id_cita <> ? "
 + "AND ABS(TIME_TO_SEC(hora) - TIME_TO_SEC(?)) < ?";
 try (Connection con = ConexionBD.obtenerConexion();
 PreparedStatement ps = con.prepareStatement(sql)) {
 ps.setInt(1, idDoctor);
 ps.setDate(2, Date.valueOf(fecha));
 ps.setInt(3, idCitaExcluir);
 ps.setTime(4, Time.valueOf(hora));
 ps.setInt(5, Cita.DURACION_MINUTOS * 60);
 try (ResultSet rs = ps.executeQuery()) {
 rs.next();
 return rs.getInt(1) > 0;
 }
 } catch (SQLException e) {
 RegistroErrores.registrar("CitaDAO.existeSolapamiento", e);
 throw e;
 }
 }
 // ----- Apoyo -----
 private List<Cita> consultar(String sql, String[] parametros) throws
SQLException {
 List<Cita> lista = new ArrayList<>();
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
 RegistroErrores.registrar("CitaDAO.consultar", e);
 throw e;
 }
 return lista;
 }
 private Cita mapear(ResultSet rs) throws SQLException {
 Cita c = new Cita(
 rs.getInt("id_paciente"),
 rs.getInt("id_doctor"),
 rs.getDate("fecha").toLocalDate(),
 rs.getTime("hora").toLocalTime(),
 rs.getString("motivo"),
 EstadoCita.desdeTexto(rs.getString("estado")));
 c.setIdCita(rs.getInt("id_cita"));
 c.setNombrePaciente(rs.getString("paciente"));
 c.setNombreDoctor(rs.getString("doctor"));
 return c;
 }
}

