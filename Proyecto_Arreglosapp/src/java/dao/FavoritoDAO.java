/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: FavoritoDAO.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Gestiona la persistencia de los servicios marcados como favoritos.
 *               Permite a los usuarios preseleccionar servicios de interés para
 *               consultas rápidas en su perfil personal.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package dao;

import config.ConectionDB;
import model.Favorito;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la gestión de elementos favoritos.
 * Implementa la vinculación relacional entre Usuarios y Servicios del catálogo.
 */
public class FavoritoDAO {

    /**
     * Registra un vínculo de interés entre un usuario y un servicio base.
     * Implementa la normalización de datos guardando solo las llaves foráneas.
     * 
     * @param favorito Instancia del modelo con los IDs de usuario y servicio.
     * @return true si la inserción fue exitosa.
     * @throws Exception Error de integridad o fallo JDBC.
     */
    public boolean agregarFavorito(Favorito favorito) throws Exception {
        // SQL optimizado para la tabla normalizada FAVORITOS
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
     * Recupera la colección de favoritos hidratada con la metadata del servicio.
     * Emplea un LEFT JOIN para asegurar que el registro se muestre aunque la 
     * metadata del servicio haya sufrido cambios.
     * 
     * @param userId Identificador del propietario de los favoritos.
     * @return Lista de favoritos con nombre, precio y descripción del servicio vinculados.
     * @throws Exception Error en la resolución del JOIN o conexión.
     */
    public List<Favorito> obtenerFavoritosPorUsuario(int userId) throws Exception {
        // Extracción consolidada de datos para evitar consultas repetitivas (N+1 problem)
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
                    
                    // Hidratación del nombre desde la tabla SERVICIOS
                    favorito.setNombreServicio(rs.getString("servicio_nombre")); 
                    
                    // Recuperación de la descripción técnica
                    favorito.setServicio(rs.getString("servicio_descripcion")); 
                    
                    favorito.setPrecio(rs.getDouble("servicio_precio_base"));
                    favorito.setImagenUrl(rs.getString("servicio_imagen_url"));

                    // Conversión de temporalidad JDBC a LocalDateTime
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
    /**
     * Elimina la marca de favorito basada en el ID único y propiedad del usuario.
     * 
     * @param favoritoId Identificador del registro.
     * @param userId     Propietario para validar autorización de borrado.
     * @return true si se eliminó el registro.
     * @throws Exception Error SQL.
     */
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

    /**
     * Valida la preexistencia de un servicio en la lista de deseos del usuario.
     * Útil para alternar estados visuales (corazón lleno/vacío) en el UI.
     * 
     * @param userId     Propietario de la búsqueda.
     * @param servicioId ID del servicio a verificar.
     * @return true si ya existe el registro.
     * @throws Exception Error de base de datos.
     */
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