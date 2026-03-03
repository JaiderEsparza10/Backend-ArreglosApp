package dao;

import config.ConectionDB;
import model.Personalizacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalizacionDAO {

    // Crear una personalización
    public boolean crearPersonalizacion(Personalizacion personalizacion) throws Exception {
        String sql = "INSERT INTO PERSONALIZACIONES (user_id, categoria, descripcion, material_tela, imagen_referencia, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, personalizacion.getUserId());
            ps.setString(2, personalizacion.getCategoria());
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

    // Obtener personalizaciones de un usuario
    public List<Personalizacion> obtenerPersonalizacionesPorUsuario(int userId) throws Exception {
        String sql = "SELECT * FROM PERSONALIZACIONES WHERE user_id = ? ORDER BY fecha_creacion DESC";
        List<Personalizacion> personalizaciones = new ArrayList<>();

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Personalizacion personalizacion = new Personalizacion();
                    personalizacion.setPersonalizacionId(rs.getInt("personalizacion_id"));
                    personalizacion.setUserId(rs.getInt("user_id"));
                    personalizacion.setCategoria(rs.getString("categoria"));
                    personalizacion.setDescripcion(rs.getString("descripcion"));
                    personalizacion.setMaterialTela(rs.getString("material_tela"));
                    personalizacion.setImagenReferencia(rs.getString("imagen_referencia"));
                    personalizacion.setEstado(rs.getString("estado"));

                    if (rs.getTimestamp("fecha_creacion") != null) {
                        personalizacion.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                    }
                    if (rs.getTimestamp("fecha_actualizacion") != null) {
                        personalizacion.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime());
                    }

                    personalizaciones.add(personalizacion);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener personalizaciones: " + e.getMessage());
        }

        return personalizaciones;
    }

    // ✅ NUEVO — Obtener una personalización por ID y userId
    public Personalizacion obtenerPorId(int personalizacionId, int userId) throws Exception {
        String sql = "SELECT * FROM PERSONALIZACIONES WHERE personalizacion_id = ? AND user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personalizacionId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Personalizacion p = new Personalizacion();
                    p.setPersonalizacionId(rs.getInt("personalizacion_id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setCategoria(rs.getString("categoria"));
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

    // ✅ NUEVO — Actualizar una personalización existente
    public boolean actualizarPersonalizacion(Personalizacion p) throws Exception {
        String sql = "UPDATE PERSONALIZACIONES SET " +
                "categoria = ?, " +
                "descripcion = ?, " +
                "material_tela = ?, " +
                "imagen_referencia = COALESCE(?, imagen_referencia), " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE personalizacion_id = ? AND user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getCategoria());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getMaterialTela());
            ps.setString(4, p.getImagenReferencia()); // null = mantener imagen anterior
            ps.setInt(5, p.getPersonalizacionId());
            ps.setInt(6, p.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new Exception("Error al actualizar personalización: " + e.getMessage());
        }
    }

    // Actualizar estado de una personalización
    public boolean actualizarEstado(int personalizacionId, String nuevoEstado) throws Exception {
        String sql = "UPDATE PERSONALIZACIONES SET estado = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE personalizacion_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, personalizacionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new Exception("Error al actualizar estado: " + e.getMessage());
        }
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

    // Contar personalizaciones de un usuario
    public int contarPersonalizacionesPorUsuario(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM PERSONALIZACIONES WHERE user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al contar personalizaciones: " + e.getMessage());
        }

        return 0;
    }
}