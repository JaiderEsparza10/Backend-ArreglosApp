package dao;

import config.ConectionDB;
import model.Personalizacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalizacionDAO {

    /**
     * Registra una nueva personalización de arreglo en la base de datos.
     * 
     * @param personalizacion El objeto con los detalles de la personalización.
     * @return true si la inserción fue exitosa, false en caso contrario.
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
     * Obtiene personalizaciones de un usuario con nombres de servicios.
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
                    p.setServicio(rs.getString("servicio_nombre")); // Nombre del servicio
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setMaterialTela(rs.getString("material_tela"));
                    p.setImagenReferencia(rs.getString("imagen_referencia"));
                    p.setEstado(rs.getString("estado"));

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
     * Actualiza una personalización existente.
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

    // Obtener una personalización por ID y userId
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
                    p.setServicio(rs.getString("servicio_nombre")); // Nombre del servicio
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setMaterialTela(rs.getString("material_tela"));
                    p.setImagenReferencia(rs.getString("imagen_referencia"));
                    p.setEstado(rs.getString("estado"));

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

    // Eliminar una personalización
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