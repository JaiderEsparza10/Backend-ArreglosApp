/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: ImagenPersonalizacionDAO.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.0
 * @description: Gestiona el repositorio visual de las personalizaciones.
 *               Permite asociar múltiples archivos de referencia a un único
 *               requerimiento técnico, facilitando la interpretación del diseño.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la galería de imágenes de personalización.
 * Soporta la carga masiva de referencias y la gestión de la imagen de portada.
 */
public class ImagenPersonalizacionDAO {

    /**
     * Vincula una nueva URL de imagen a un proceso de personalización activo.
     * 
     * @param personalizacionId ID del requerimiento padre.
     * @param imagenUrl         Ruta o enlace al archivo (local o Cloud).
     * @param esPrincipal       Indicador de jerarquía visual (Portada).
     * @return true si el registro fue insertado correctamente.
     * @throws Exception Error de base de datos.
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
     * Recupera el carrusel completo de imágenes para una personalización.
     * Prioriza la imagen principal en el orden de salida.
     * 
     * @param personalizacionId ID de la solicitud.
     * @return Lista de cadenas (URLs) de imágenes.
     * @throws Exception Error SQL.
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
     * Localiza la imagen designada como portada del requerimiento.
     * 
     * @param personalizacionId Criterio de búsqueda.
     * @return URL de la imagen principal o null si no existe.
     * @throws Exception Error de conexión.
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
     * Alterna la jerarquía de imágenes para establecer una nueva portada.
     * Limpia el estado 'principal' de otras imágenes del mismo padre antes de asignar la nueva.
     * 
     * @param imagenId ID de la imagen que será la nueva principal.
     * @return true si se realizó el cambio.
     * @throws Exception Fallo en la integridad o lógica SQL.
     */
    public boolean establecerPrincipal(int imagenId) throws Exception {
        // Fase 1: Resetear jerarquía para asegurar exclusividad de la imagen principal
        String sql = "UPDATE IMAGENES_PERSONALIZACION SET es_principal = FALSE WHERE personalizacion_id = (SELECT personalizacion_id FROM IMAGENES_PERSONALIZACION WHERE imagen_id = ?)";
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, imagenId);
            ps.executeUpdate();
            
            // Fase 2: Promover la imagen seleccionada como principal
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
     * Purga un registro de imagen del sistema.
     * 
     * @param imagenId Identificador de la imagen a borrar.
     * @return true si la operación JDBC reportó filas afectadas.
     * @throws Exception Error SQL.
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
