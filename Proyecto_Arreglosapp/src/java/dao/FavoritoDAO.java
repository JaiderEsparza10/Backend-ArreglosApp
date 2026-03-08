package dao;

import config.ConectionDB;
import model.Favorito;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase maneja la persistencia de los servicios favoritos o seleccionados
 * por los usuarios.
 */
public class FavoritoDAO {

    /**
     * Registra un nuevo servicio en la lista de favoritos de un usuario.
     * 
     * @param favorito El objeto que contiene los datos del favorito a registrar.
     * @return true si se agregó correctamente, false en caso contrario.
     */
    public boolean agregarFavorito(Favorito favorito) throws Exception {
        String sql = "INSERT INTO FAVORITOS (user_id, arreglo_id, categoria, nombre_categoria, precio, imagen_url) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, favorito.getUserId());
            ps.setInt(2, favorito.getArregloId());
            ps.setString(3, favorito.getCategoria());
            ps.setString(4, favorito.getNombreCategoria());
            ps.setDouble(5, favorito.getPrecio());
            ps.setString(6, favorito.getImagenUrl());

            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al agregar favorito: " + e.getMessage());
        }
    }

    // Obtener favoritos de un usuario
    public List<Favorito> obtenerFavoritosPorUsuario(int userId) throws Exception {
        String sql = "SELECT * FROM FAVORITOS WHERE user_id = ? ORDER BY fecha_agregado DESC";
        List<Favorito> favoritos = new ArrayList<>();

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Favorito favorito = new Favorito();
                    favorito.setFavoritoId(rs.getInt("favorito_id"));
                    favorito.setUserId(rs.getInt("user_id"));
                    favorito.setArregloId(rs.getInt("arreglo_id"));
                    favorito.setCategoria(rs.getString("categoria"));
                    favorito.setNombreCategoria(rs.getString("nombre_categoria"));
                    favorito.setPrecio(rs.getDouble("precio"));
                    favorito.setImagenUrl(rs.getString("imagen_url"));

                    // Convertir timestamp a LocalDateTime si es necesario
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

    // Eliminar un favorito
    public boolean eliminarFavorito(int favoritoId, int userId) throws Exception {
        String sql = "DELETE FROM FAVORITOS WHERE favorito_id = ? AND user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, favoritoId);
            ps.setInt(2, userId);

            int filasEliminadas = ps.executeUpdate();
            return filasEliminadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al eliminar favorito: " + e.getMessage());
        }
    }

    // Verificar si un favorito ya existe por arreglo
    public boolean existeFavoritoPorArreglo(int userId, int arregloId) throws Exception {
        String sql = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ? AND arreglo_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, arregloId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar favorito: " + e.getMessage());
        }

        return false;
    }

    // Verificar si un favorito ya existe (método antiguo para compatibilidad)
    public boolean existeFavorito(int userId, String categoria) throws Exception {
        String sql = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ? AND categoria = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, categoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar favorito: " + e.getMessage());
        }

        return false;
    }

    // Contar favoritos de un usuario
    public int contarFavoritosPorUsuario(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al contar favoritos: " + e.getMessage());
        }

        return 0;
    }
}
