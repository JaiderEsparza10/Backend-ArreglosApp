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
        String sql = "INSERT INTO FAVORITOS (user_id, arreglo_id, cantidad) VALUES (?, ?, ?)";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, favorito.getUserId());
            ps.setInt(2, favorito.getArregloId());
            ps.setInt(3, favorito.getCantidad() > 0 ? favorito.getCantidad() : 1);

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
    String sql = "SELECT f.*, a.arreglo_nombre, a.arreglo_precio_base, a.arreglo_imagen_url, a.arreglo_descripcion " +
                 "FROM FAVORITOS f " +
                 "INNER JOIN ARREGLOS a ON f.arreglo_id = a.arreglo_id " +
                 "WHERE f.user_id = ? ORDER BY f.fecha_agregado DESC";
                 
    List<Favorito> favoritos = new ArrayList<>();

    try (Connection con = ConectionDB.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Favorito favorito = new Favorito();
                favorito.setFavoritoId(rs.getInt("favorito_id"));
                favorito.setArregloId(rs.getInt("arreglo_id"));
                
                // Mapeo de datos del JOIN:
                // Usamos setCategoria o setNombreCategoria para el título
                favorito.setNombreCategoria(rs.getString("arreglo_nombre")); 
                
                // IMPORTANTE: Aquí llenamos la descripción que antes estaba null
                // Si tu modelo Favorito tiene setCategoria, úsalo para la descripción 
                // o el campo que estés imprimiendo en esa línea del JSP
                favorito.setCategoria(rs.getString("arreglo_descripcion")); 
                
                favorito.setPrecio(rs.getDouble("arreglo_precio_base"));
                favorito.setImagenUrl(rs.getString("arreglo_imagen_url"));
                favorito.setCantidad(rs.getInt("cantidad"));

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

    // Verificar si existe por ID de arreglo (Se mantiene igual)
    public boolean existeFavoritoPorArreglo(int userId, int arregloId) throws Exception {
        String sql = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ? AND arreglo_id = ?";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, arregloId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar favorito: " + e.getMessage());
        }
        return false;
    }
}