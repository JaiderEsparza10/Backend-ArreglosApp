package dao;

import config.ConectionDB;
import model.Favorito;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    /**
     * Registra un nuevo servicio en la lista de favoritos.
     * Ahora solo guarda IDs, cumpliendo con la normalización.
     */
    public boolean agregarFavorito(Favorito favorito) throws Exception {
        // SQL simplificado: solo columnas existentes en la tabla normalizada
        String sql = "INSERT INTO FAVORITOS (user_id, servicio_id) VALUES (?, ?)";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, favorito.getUserId());
            ps.setInt(2, favorito.getServicioId());
            
            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;
            
        } catch (SQLException e) {
            throw new Exception("Error al agregar favorito: " + e.getMessage());
        }
    }

    /**
     * Obtiene los favoritos usando INNER JOIN para traer los detalles del arreglo.
     * Esto soluciona el problema de las columnas eliminadas.
     */
    public List<Favorito> obtenerFavoritosPorUsuario(int userId) throws Exception {
    // 1. Agregamos a.arreglo_descripcion a la consulta SELECT
    String sql = "SELECT f.*, s.servicio_nombre, s.servicio_precio_base, '' as servicio_imagen_url, s.servicio_descripcion " +
                 "FROM FAVORITOS f " +
                 "LEFT JOIN SERVICIOS s ON f.servicio_id = s.servicio_id " +
                 "WHERE f.user_id = ? ORDER BY f.fecha_agregado DESC";
                 
    List<Favorito> favoritos = new ArrayList<>();

    try (Connection con = ConectionDB.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Favorito favorito = new Favorito();
                favorito.setFavoritoId(rs.getInt("favorito_id"));
                favorito.setServicioId(rs.getInt("servicio_id"));
                
                // Mapeo de datos del JOIN:
                // Usamos setServicio o setNombreServicio para el título
                favorito.setNombreServicio(rs.getString("servicio_nombre")); 
                
                // IMPORTANTE: Aquí llenamos la descripción que antes estaba null
                // Si tu modelo Favorito tiene setServicio, úsalo para la descripción 
                // o el campo que estés imprimiendo en esa línea del JSP
                favorito.setServicio(rs.getString("servicio_descripcion")); 
                
                favorito.setPrecio(rs.getDouble("servicio_precio_base"));
                favorito.setImagenUrl(rs.getString("servicio_imagen_url"));

                if (rs.getTimestamp("fecha_agregado") != null) {
                    favorito.setFechaAgregado(rs.getTimestamp("fecha_agregado").toLocalDateTime());
                }

                favoritos.add(favorito);
            }
        }
    } catch (SQLException e) {
        throw new Exception("Error al obtener favoritos: " + e.getMessage());
    }
    return favoritos;
}
    // Eliminar un favorito (Se mantiene igual)
    public boolean eliminarFavorito(int favoritoId, int userId) throws Exception {
        String sql = "DELETE FROM FAVORITOS WHERE favorito_id = ? AND user_id = ?";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, favoritoId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al eliminar favorito: " + e.getMessage());
        }
    }

    // Verificar si existe por ID de servicio
    public boolean existeFavoritoPorServicio(int userId, int servicioId) throws Exception {
        String sql = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ? AND servicio_id = ?";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, servicioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar favorito: " + e.getMessage());
        }
        return false;
    }
}