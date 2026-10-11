package com.udb.clinica.dao;

import com.udb.clinica.util.ConexionBD;
import com.udb.clinica.util.RegistroErrores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas de los reportes básicos. Solo lee datos, no modifica nada.
 */
public class ReporteDAO {

    /** Resultado genérico de un reporte: títulos de columnas y filas. */
    public static class Reporte {
        public final String[] columnas;
        public final List<Object[]> filas;

        public Reporte(String[] columnas, List<Object[]> filas) {
            this.columnas = columnas;
            this.filas = filas;
        }
    }

    /** Cuántas citas hay en cada estado. */
    public Reporte citasPorEstado() throws SQLException {
        return ejecutar("ReporteDAO.citasPorEstado",
                "SELECT estado AS 'Estado', COUNT(*) AS 'Cantidad' "
                        + "FROM citas GROUP BY estado ORDER BY estado", null);
    }

    /** Cuántas citas tiene cada doctor (incluye doctores sin citas). */
    public Reporte citasPorDoctor() throws SQLException {
        return ejecutar("ReporteDAO.citasPorDoctor",
                "SELECT d.codigo AS 'Código', d.nombre_completo AS 'Doctor', "
                        + "d.especialidad AS 'Especialidad', COUNT(c.id_cita) AS 'Citas' "
                        + "FROM doctores d LEFT JOIN citas c ON c.id_doctor = d.id_doctor "
                        + "GROUP BY d.id_doctor, d.codigo, d.nombre_completo, d.especialidad "
                        + "ORDER BY d.codigo", null);
    }

    /** Cuántas citas hay en cada fecha. */
    public Reporte citasPorFecha() throws SQLException {
        return ejecutar("ReporteDAO.citasPorFecha",
                "SELECT fecha AS 'Fecha', COUNT(*) AS 'Citas' "
                        + "FROM citas GROUP BY fecha ORDER BY fecha", null);
    }

    /** Lista de citas de un estado: "Programada", "Atendida" o "Cancelada". */
    public Reporte citasDeEstado(String estado) throws SQLException {
        return ejecutar("ReporteDAO.citasDeEstado",
                "SELECT c.id_cita AS 'N.º cita', p.nombre_completo AS 'Paciente', "
                        + "d.nombre_completo AS 'Doctor', c.fecha AS 'Fecha', c.hora AS 'Hora', "
                        + "c.motivo AS 'Motivo' "
                        + "FROM citas c "
                        + "JOIN pacientes p ON c.id_paciente = p.id_paciente "
                        + "JOIN doctores d ON c.id_doctor = d.id_doctor "
                        + "WHERE c.estado = ? ORDER BY c.fecha, c.hora",
                new String[]{estado});
    }

    /** Total de pacientes registrados, activos e inactivos. */
    public Reporte totalPacientes() throws SQLException {
        return ejecutar("ReporteDAO.totalPacientes",
                "SELECT COUNT(*) AS 'Total registrados', "
                        + "SUM(estado = 'Activo') AS 'Activos', "
                        + "SUM(estado = 'Inactivo') AS 'Inactivos' FROM pacientes", null);
    }

    /** Ejecuta un SELECT y arma el Reporte leyendo los nombres de columna del propio resultado. */
    private Reporte ejecutar(String origen, String sql, String[] parametros) throws SQLException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametros != null) {
                for (int i = 0; i < parametros.length; i++) {
                    ps.setString(i + 1, parametros[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int n = meta.getColumnCount();
                String[] columnas = new String[n];
                for (int i = 0; i < n; i++) {
                    columnas[i] = meta.getColumnLabel(i + 1);
                }
                List<Object[]> filas = new ArrayList<>();
                while (rs.next()) {
                    Object[] fila = new Object[n];
                    for (int i = 0; i < n; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    filas.add(fila);
                }
                return new Reporte(columnas, filas);
            }
        } catch (SQLException e) {
            RegistroErrores.registrar(origen, e);
            throw e;
        }
    }
}