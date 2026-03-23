/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Administrar la galería de imágenes asociadas a las solicitudes de personalización.
 */
package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Proporciona soporte para múltiples imágenes por cada trabajo a medida, incluyendo la imagen principal.
 */
public class ImagenPersonalizacionDAO {

    /**
     * Agrega una nueva imagen a una personalización
     */
    public boolean agregarImagen(int personalizacionId, String imagenUrl, boolean esPrincipal) throws Exception {
        String sql = "INSERT INTO IMAGENES_PERSONALIZACION (personalizacion_id, imagen_url, es_principal) VALUES (?, ?, ?)";
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personalizacionId);
            ps.setString(2, imagenUrl);
            ps.setBoolean(3, esPrincipal);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new Exception("Error al agregar imagen: " + e.getMessage());
        }
    }

    /**
     * Obtiene todas las imágenes de una personalización
     */
    public List<String> obtenerImagenes(int personalizacionId) throws Exception {
        String sql = "SELECT imagen_url FROM IMAGENES_PERSONALIZACION WHERE personalizacion_id = ? ORDER BY es_principal DESC, fecha_subida ASC";
        List<String> imagenes = new ArrayList<>();
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personalizacionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imagenes.add(rs.getString("imagen_url"));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener imágenes: " + e.getMessage());
        }
        
        return imagenes;
    }

    /**
     * Obtiene la imagen principal de una personalización
     */
    public String obtenerImagenPrincipal(int personalizacionId) throws Exception {
        String sql = "SELECT imagen_url FROM IMAGENES_PERSONALIZACION WHERE personalizacion_id = ? AND es_principal = TRUE LIMIT 1";
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personalizacionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("imagen_url");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener imagen principal: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Establece una imagen como principal (desmarca las demás)
     */
    public boolean establecerPrincipal(int imagenId) throws Exception {
        String sql = "UPDATE IMAGENES_PERSONALIZACION SET es_principal = FALSE WHERE personalizacion_id = (SELECT personalizacion_id FROM IMAGENES_PERSONALIZACION WHERE imagen_id = ?)";
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, imagenId);
            
            // Establecer la nueva como principal
            String sqlPrincipal = "UPDATE IMAGENES_PERSONALIZACION SET es_principal = TRUE WHERE imagen_id = ?";
            try (PreparedStatement ps2 = con.prepareStatement(sqlPrincipal)) {
                ps2.setInt(1, imagenId);
                return ps2.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            throw new Exception("Error al establecer imagen principal: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen específica
     */
    public boolean eliminarImagen(int imagenId) throws Exception {
        String sql = "DELETE FROM IMAGENES_PERSONALIZACION WHERE imagen_id = ?";
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, imagenId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new Exception("Error al eliminar imagen: " + e.getMessage());
        }
    }
}
