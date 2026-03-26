/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: PersonalizacionDAO.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Motor de persistencia para requerimientos de diseño a medida.
 *               Gestiona la captura de especificaciones técnicas y materiales
 *               antes de su materialización en una orden comercial.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package dao;

import config.ConectionDB;
import model.Personalizacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para el subsistema de personalización.
 * Permite la gestión del borrador técnico de la prenda o arreglo solicitado.
 */
public class PersonalizacionDAO {

    /**
     * Registra un nuevo folio de personalización.
     * Captura la intención inicial del cliente con sus parámetros estéticos.
     * 
     * @param personalizacion Modelo con descripción, material y referencia visual.
     * @return true si el registro fue exitoso.
     * @throws Exception Error de base de datos o fallo en recuperación de ID.
     */
    public boolean crearPersonalizacion(Personalizacion personalizacion) throws Exception {
        String sql = "INSERT INTO PERSONALIZACIONES (user_id, servicio_id, descripcion, material_tela, imagen_referencia, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, personalizacion.getUserId());
            ps.setInt(2, personalizacion.getServicioId());
            ps.setString(3, personalizacion.getDescripcion());
            ps.setString(4, personalizacion.getMaterialTela());
            ps.setString(5, personalizacion.getImagenReferencia());
            ps.setString(6, personalizacion.getEstado());

            int filasInsertadas = ps.executeUpdate();

            // Recuperación de la llave primaria generada por el motor SQL
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    personalizacion.setPersonalizacionId(rs.getInt(1));
                }
            }
            return filasInsertadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al crear personalización: " + e.getMessage());
        }
    }

    /**
     * Recupera el listado de borradores técnicos creados por el usuario.
     * Realiza un JOIN con SERVICIOS para mostrar el nombre amigable de la categoría.
     * 
     * @param userId Propietario de los registros.
     * @return Lista de modelos hidratados y ordenados cronológicamente.
     * @throws Exception Error JDBC.
     */
    public List<Personalizacion> obtenerPersonalizacionesPorUsuario(int userId) throws Exception {
        String sql = "SELECT p.*, s.servicio_nombre " +
                     "FROM PERSONALIZACIONES p " +
                     "LEFT JOIN SERVICIOS s ON p.servicio_id = s.servicio_id " +
                     "WHERE p.user_id = ? ORDER BY p.fecha_creacion DESC";
        List<Personalizacion> personalizaciones = new ArrayList<>();

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Personalizacion p = new Personalizacion();
                    p.setPersonalizacionId(rs.getInt("personalizacion_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setServicioId(rs.getInt("servicio_id"));
                    p.setServicio(rs.getString("servicio_nombre")); // Hidratación del nombre del servicio base
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setMaterialTela(rs.getString("material_tela"));
                    p.setImagenReferencia(rs.getString("imagen_referencia"));
                    p.setEstado(rs.getString("estado"));

                    // Conversión de tipos temporales SQL -> Java Time
                    if (rs.getTimestamp("fecha_creacion") != null) {
                        p.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    }
                    personalizaciones.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener personalizaciones: " + e.getMessage());
        }
        return personalizaciones;
    }

    /**
     * Modifica los parámetros técnicos de una solicitud existente.
     * Emplea COALESCE para preservar la imagen previa si no se adjunta una nueva.
     * 
     * @param p Objeto con las actualizaciones.
     * @return true si la fila fue actualizada (valida propiedad del usuario).
     * @throws Exception Error SQL.
     */
    public boolean actualizarPersonalizacion(Personalizacion p) throws Exception {
        String sql = "UPDATE PERSONALIZACIONES SET " +
                     "servicio_id = ?, " + 
                     "descripcion = ?, " +
                     "material_tela = ?, " +
                     "imagen_referencia = COALESCE(?, imagen_referencia), " +
                     "fecha_actualizacion = CURRENT_TIMESTAMP " +
                     "WHERE personalizacion_id = ? AND user_id = ?";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getServicioId());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getMaterialTela());
            ps.setString(4, p.getImagenReferencia());
            ps.setInt(5, p.getPersonalizacionId());
            ps.setInt(6, p.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar personalización: " + e.getMessage());
        }
    }

    /**
     * Localiza un requerimiento específico validando la identidad del solicitante.
     * 
     * @param personalizacionId ID único del registro.
     * @param userId             ID del propietario (Seguridad).
     * @return Instancia del modelo o null si no se encuentra o no pertenece al usuario.
     * @throws Exception Error JDBC.
     */
    public Personalizacion obtenerPorId(int personalizacionId, int userId) throws Exception {
        String sql = "SELECT p.*, s.servicio_nombre " +
                     "FROM PERSONALIZACIONES p " +
                     "LEFT JOIN SERVICIOS s ON p.servicio_id = s.servicio_id " +
                     "WHERE p.personalizacion_id = ? AND p.user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personalizacionId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Personalizacion p = new Personalizacion();
                    p.setPersonalizacionId(rs.getInt("personalizacion_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setServicioId(rs.getInt("servicio_id"));
                    p.setServicio(rs.getString("servicio_nombre")); // Mapeo de identidad del servicio
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setMaterialTela(rs.getString("material_tela"));
                    p.setImagenReferencia(rs.getString("imagen_referencia"));
                    p.setEstado(rs.getString("estado"));

                    // Recuperación de marcas de tiempo del sistema
                    if (rs.getTimestamp("fecha_creacion") != null) {
                        p.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    }
                    if (rs.getTimestamp("fecha_actualizacion") != null) {
                        p.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime());
                    }

                    return p;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener personalización: " + e.getMessage());
        }

        return null;
    }

    /**
     * Purga un requerimiento técnico del sistema.
     * Solo permite la eliminación si el usuario es el propietario legítimo.
     * 
     * @param personalizacionId ID a eliminar.
     * @param userId             ID del usuario para comprobación de seguridad.
     * @return true si el borrado fue efectivo.
     * @throws Exception Error de integridad o JDBC.
     */
    public boolean eliminarPersonalizacion(int personalizacionId, int userId) throws Exception {
        String sql = "DELETE FROM PERSONALIZACIONES WHERE personalizacion_id = ? AND user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personalizacionId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new Exception("Error al eliminar personalización: " + e.getMessage());
        }
    }
}