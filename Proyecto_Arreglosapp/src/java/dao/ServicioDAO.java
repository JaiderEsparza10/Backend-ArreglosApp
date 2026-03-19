package dao;

import config.ConectionDB;
import model.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Servicios - Gestión del catálogo base
 * Jerarquía: Servicios → Personalizaciones → Arreglos
 * 
 * @author Arquitecto de Software - DBA
 */
public class ServicioDAO {

    /**
     * Obtiene todos los servicios activos
     * @return Lista de servicios disponibles
     */
    public List<Servicio> obtenerTodosServicios() throws Exception {
        String sql = "SELECT servicio_id, servicio_nombre, servicio_descripcion, " +
                "servicio_precio_base, servicio_tiempo_estimado, servicio_activo, " +
                "fecha_creacion FROM SERVICIOS WHERE servicio_activo = 1 " +
                "ORDER BY servicio_nombre ASC";
        
        List<Servicio> lista = new ArrayList<>();

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio();
                s.setServicioId(rs.getInt("servicio_id"));
                s.setServicioNombre(rs.getString("servicio_nombre"));
                s.setServicioDescripcion(rs.getString("servicio_descripcion"));
                s.setServicioPrecioBase(rs.getDouble("servicio_precio_base"));
                s.setServicioTiempoEstimado(rs.getInt("servicio_tiempo_estimado"));
                s.setServicioActivo(rs.getBoolean("servicio_activo"));
                
                s.setImagenUrl(null);
                
                // Formatear fecha
                Timestamp fecha = rs.getTimestamp("fecha_creacion");
                s.setFechaCreacion(fecha != null ? fecha.toString() : "");
                
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener servicios: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un servicio específico por ID
     * @param servicioId ID del servicio
     * @return Objeto Servicio o null si no existe
     */
    public Servicio obtenerServicioPorId(int servicioId) throws Exception {
        String sql = "SELECT s.servicio_id, s.servicio_nombre, s.servicio_descripcion, " +
                "s.servicio_precio_base, s.servicio_tiempo_estimado, s.servicio_activo, " +
                "s.fecha_creacion " +
                "FROM SERVICIOS s " +
                "WHERE s.servicio_id = ?";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, servicioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Servicio s = new Servicio();
                    s.setServicioId(rs.getInt("servicio_id"));
                    s.setServicioNombre(rs.getString("servicio_nombre"));
                    s.setServicioDescripcion(rs.getString("servicio_descripcion"));
                    s.setServicioPrecioBase(rs.getDouble("servicio_precio_base"));
                    s.setServicioTiempoEstimado(rs.getInt("servicio_tiempo_estimado"));
                    s.setServicioActivo(rs.getBoolean("servicio_activo"));
                    
                    s.setImagenUrl(null);
                    
                    Timestamp fecha = rs.getTimestamp("fecha_creacion");
                    s.setFechaCreacion(fecha != null ? fecha.toString() : "");
                    
                    return s;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener servicio: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica si un nombre de servicio ya existe
     * @param nombre Nombre del servicio a verificar
     * @return true si ya existe, false si está disponible
     */
    public boolean existeNombreServicio(String nombre) throws Exception {
        String sql = "SELECT COUNT(*) FROM SERVICIOS WHERE servicio_nombre = ? AND servicio_activo = 1";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar nombre de servicio: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si un nombre de servicio ya existe (excluyendo un ID específico)
     * @param nombre Nombre del servicio a verificar
     * @param excluirId ID del servicio a excluir de la verificación
     * @return true si ya existe, false si está disponible
     */
    public boolean existeNombreServicioExcluyendo(String nombre, int excluirId) throws Exception {
        String sql = "SELECT COUNT(*) FROM SERVICIOS WHERE servicio_nombre = ? AND servicio_id != ? AND servicio_activo = 1";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre.trim());
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar nombre de servicio: " + e.getMessage());
        }
        return false;
    }

    /**
     * Crea un nuevo servicio en el catálogo base
     * @param servicio Objeto Servicio con todos los datos
     * @return true si se creó exitosamente
     */
    public boolean crearServicio(Servicio servicio) throws Exception {
        String sql = "INSERT INTO SERVICIOS (servicio_nombre, servicio_descripcion, " +
                "servicio_precio_base, servicio_tiempo_estimado, servicio_activo) " +
                "VALUES (?, ?, ?, ?, 1)";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getServicioNombre());
            ps.setString(2, servicio.getServicioDescripcion());
            ps.setDouble(3, servicio.getServicioPrecioBase());
            ps.setInt(4, servicio.getServicioTiempoEstimado());

            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al crear servicio: " + e.getMessage());
        }
    }

    /**
     * Actualiza un servicio existente
     * @param servicio Objeto Servicio con datos actualizados
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarServicio(Servicio servicio) throws Exception {
        String sql = "UPDATE SERVICIOS SET servicio_nombre = ?, servicio_descripcion = ?, " +
                "servicio_precio_base = ?, servicio_tiempo_estimado = ?, " +
                "servicio_activo = COALESCE(?, servicio_activo) " +
                "WHERE servicio_id = ?";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getServicioNombre());
            ps.setString(2, servicio.getServicioDescripcion());
            ps.setDouble(3, servicio.getServicioPrecioBase());
            ps.setInt(4, servicio.getServicioTiempoEstimado());
            ps.setObject(5, null, java.sql.Types.BOOLEAN); // Usar null para que COALESCE mantenga el valor actual
            ps.setInt(6, servicio.getServicioId());

            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar servicio: " + e.getMessage());
        }
    }

    /**
     * Realiza una desactivación lógica del servicio (Meta 3 - Cascada)
     * Gestiona dependencias en FAVORITOS y PERSONALIZACIONES para evitar errores de FK.
     */
    public boolean desactivarServicio(int servicioId) throws Exception {
        String sqlCheck = "SELECT COUNT(*) FROM DETALLE_PEDIDO dp " +
                         "JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                         "JOIN PERSONALIZACIONES p ON a.personalizacion_id = p.personalizacion_id " +
                         "WHERE p.servicio_id = ?";
        
        String sqlFavoritos = "DELETE FROM FAVORITOS WHERE servicio_id = ?";
        String sqlPerso = "UPDATE PERSONALIZACIONES SET estado = 'cancelado' WHERE servicio_id = ? AND estado = 'pendiente'";
        String sqlServicio = "UPDATE SERVICIOS SET servicio_activo = 0 WHERE servicio_id = ?";
        
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            
            // 0. Verificar si existen pedidos asociados (Nueva Restricción)
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, servicioId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("No se puede eliminar el servicio porque ya tiene pedidos o citas registradas por clientes.");
                    }
                }
            }

            con.setAutoCommit(false); // Iniciar transacción

            // 1. Eliminar de favoritos (Cascada lógica/física)
            try (PreparedStatement psF = con.prepareStatement(sqlFavoritos)) {
                psF.setInt(1, servicioId);
                psF.executeUpdate();
            }

            // 2. Gestionar personalizaciones pendientes
            try (PreparedStatement psP = con.prepareStatement(sqlPerso)) {
                psP.setInt(1, servicioId);
                psP.executeUpdate();
            }

            // 3. Desactivar el servicio principal
            try (PreparedStatement psS = con.prepareStatement(sqlServicio)) {
                psS.setInt(1, servicioId);
                int result = psS.executeUpdate();
                
                con.commit(); // Confirmar cambios
                return result > 0;
            }

        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) {}
            throw new Exception("Error al desactivar servicio en cascada: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) {}
            }
        }
    }
    
    /**
     * Reactiva un servicio previamente desactivado
     * @param servicioId ID del servicio a reactivar
     * @return true si se reactivó exitosamente
     */
    public boolean reactivarServicio(int servicioId) throws Exception {
        String sql = "UPDATE SERVICIOS SET servicio_activo = 1 WHERE servicio_id = ?";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, servicioId);
            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al reactivar servicio: " + e.getMessage());
        }
    }

    // ================================================================
    // Métodos de compatibilidad para código existente
    // ================================================================

    /**
     * Método de compatibilidad - obtenerServicios()
     * @deprecated Usar obtenerTodosServicios()
     */
    @Deprecated
    public List<Servicio> obtenerServicios() throws Exception {
        return obtenerTodosServicios();
    }

    /**
     * Método de compatibilidad - obtenerPorId()
     * @deprecated Usar obtenerServicioPorId()
     */
    @Deprecated
    public Servicio obtenerPorId(int servicioId) throws Exception {
        return obtenerServicioPorId(servicioId);
    }
}