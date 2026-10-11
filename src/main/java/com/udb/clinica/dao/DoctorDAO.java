package com.udb.clinica.dao;

import com.udb.clinica.modelo.Doctor;
import com.udb.clinica.modelo.Especialidad;
import com.udb.clinica.modelo.EstadoPersona;
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
 * Acceso a datos de la tabla doctores.
 * Todas las consultas usan PreparedStatement (con ?) para evitar
 * inyección SQL y para que los datos se manden separados de la sentencia.
 */
public class DoctorDAO {

    private static final String SELECT_BASE =
            "SELECT id_doctor, codigo, nombre_completo, especialidad, telefono, correo, estado "
            + "FROM doctores";

    /** Inserta un doctor nuevo y le asigna el id que generó MySQL. */
    public void insertar(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctores (codigo, nombre_completo, especialidad, telefono, correo, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getCodigo());
            ps.setString(2, d.getNombreCompleto());
            ps.setString(3, d.getEspecialidad().getTexto());
            ps.setString(4, d.getTelefono());
            ps.setString(5, d.getCorreo());
            ps.setString(6, d.getEstado().getTexto());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    d.setId(claves.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Error 1062: el UNIQUE de codigo o correo se violó
            throw new DatosInvalidosException("Ya existe un doctor con ese código o correo.");
        } catch (SQLException e) {
            RegistroErrores.registrar("DoctorDAO.insertar", e);
            throw e;
        }
    }

    /** Devuelve todos los doctores ordenados por código. */
    public List<Doctor> listar() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY codigo", null);
    }

    /** Busca por parte del código, del nombre o de la especialidad. */
    public List<Doctor> buscar(String texto) throws SQLException {
        String sql = SELECT_BASE
                + " WHERE codigo LIKE ? OR nombre_completo LIKE ? OR especialidad LIKE ?"
                + " ORDER BY codigo";
        String patron = "%" + texto.trim() + "%";
        return consultar(sql, new String[]{patron, patron, patron});
    }

    /** Busca un doctor por su id. Devuelve null si no existe. */
    public Doctor buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE id_doctor = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            RegistroErrores.registrar("DoctorDAO.buscarPorId", e);
            throw e;
        }
    }

    /** Actualiza los datos de un doctor. Devuelve true si se modificó una fila. */
    public boolean actualizar(Doctor d) throws SQLException {
        String sql = "UPDATE doctores SET codigo = ?, nombre_completo = ?, especialidad = ?, "
                + "telefono = ?, correo = ?, estado = ? WHERE id_doctor = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getCodigo());
            ps.setString(2, d.getNombreCompleto());
            ps.setString(3, d.getEspecialidad().getTexto());
            ps.setString(4, d.getTelefono());
            ps.setString(5, d.getCorreo());
            ps.setString(6, d.getEstado().getTexto());
            ps.setInt(7, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DatosInvalidosException("Ya existe otro doctor con ese código o correo.");
        } catch (SQLException e) {
            RegistroErrores.registrar("DoctorDAO.actualizar", e);
            throw e;
        }
    }

    /** Cambia solo el estado (Activo o Inactivo). */
    public boolean cambiarEstado(int id, EstadoPersona estado) throws SQLException {
        String sql = "UPDATE doctores SET estado = ? WHERE id_doctor = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.getTexto());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            RegistroErrores.registrar("DoctorDAO.cambiarEstado", e);
            throw e;
        }
    }

    /** Elimina un doctor. MySQL lo impide si tiene citas (ON DELETE RESTRICT). */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM doctores WHERE id_doctor = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Error 1451: hay citas que dependen de este doctor
            throw new DatosInvalidosException(
                    "No se puede eliminar: el doctor tiene citas. Cámbielo a Inactivo.");
        } catch (SQLException e) {
            RegistroErrores.registrar("DoctorDAO.eliminar", e);
            throw e;
        }
    }

    // ----- Métodos privados de apoyo -----

    /** Ejecuta un SELECT con parámetros de texto opcionales y arma la lista. */
    private List<Doctor> consultar(String sql, String[] parametros) throws SQLException {
        List<Doctor> lista = new ArrayList<>();
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
            RegistroErrores.registrar("DoctorDAO.consultar", e);
            throw e;
        }
        return lista;
    }

    /** Convierte una fila del ResultSet en un objeto Doctor. */
    private Doctor mapear(ResultSet rs) throws SQLException {
        Doctor d = new Doctor(
                rs.getString("codigo"),
                rs.getString("nombre_completo"),
                Especialidad.desdeTexto(rs.getString("especialidad")),
                rs.getString("telefono"),
                rs.getString("correo"),
                EstadoPersona.desdeTexto(rs.getString("estado")));
        d.setId(rs.getInt("id_doctor"));
        return d;
    }
}