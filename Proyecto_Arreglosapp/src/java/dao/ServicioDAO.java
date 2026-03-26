/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: ServicioDAO.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.2
 * @description: Gestiona el catálogo maestro de servicios de Arreglos App.
 *               Implementa reglas de integridad para altas, bajas y cambios,
 *               incluyendo la gestión de estados lógicos.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package dao;

import config.ConectionDB;
import model.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para el inventario de servicios.
 * Proporciona el soporte transaccional para el mantenimiento del catálogo básico.
 */
public class ServicioDAO {

    /**
     * Recupera la oferta comercial completa visible para los clientes.
     * Solo incluye registros con bandera de activación positiva.
     * 
     * @return Lista de modelos de servicio ordenados alfabéticamente.
     * @throws Exception Error de conexión o mapeo JDBC.
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
                
                // No se requiere imagen URL en el catálogo base (uso futuro o placeholder)
                s.setImagenUrl(null);
                
                // Normalización de la marca de tiempo para el objeto de negocio
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
     * Localiza una ficha técnica por su identificador primario.
     * 
     * @param servicioId ID de búsqueda.
     * @return Instancia de Servicio o null si el ID es inválido.
     * @throws Exception Error JDBC.
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
     * Valida la unicidad nominal para evitar duplicidad funcional en el catálogo.
     * 
     * @param nombre Etiqueta a verificar.
     * @return true si el nombre ya está registrado y activo.
     * @throws Exception Error SQL.
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
     * Valida la unicidad nominal ignorando el registro actual (útil para edición).
     * 
     * @param nombre    Nuevo nombre propuesto.
     * @param excluirId ID del registro en edición.
     * @return true si el nombre colisiona con otro servicio activo.
     * @throws Exception Error JDBC.
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
     * Inserta una nueva categoría de servicio en el repositorio.
     * Por defecto se marca con visibilidad activa (1).
     * 
     * @param servicio Modelo con datos comerciales básicos.
     * @return true si la inserción fue persistida.
     * @throws Exception Error de base de datos.
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
     * Sincroniza los cambios en la definición del servicio.
     * Mantiene la integridad del estado activo mediante COALESCE.
     * 
     * @param servicio Instancia con los nuevos valores comerciales.
     * @return true si la fila fue impactada en el motor SQL.
     * @throws Exception Error JDBC.
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
            ps.setObject(5, null, java.sql.Types.BOOLEAN); // Preservación del estado actual
            ps.setInt(6, servicio.getServicioId());

            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar servicio: " + e.getMessage());
        }
    }

    /**
     * Ejecuta una baja lógica del servicio bajo protocolo estricto de integridad.
     * Fases:
     * 1. Check: Impide la baja si existen pedidos/citas vinculados.
     * 2. Cascada: Purga de la tabla FAVORITOS.
     * 3. Gestión: Cancela personalizaciones en fase de borrador (pendiente).
     * 4. Update: Cambia el bit de visibilidad del servicio.
     * 
     * @param servicioId ID del servicio a retirar.
     * @return true si el protocolo transaccional finalizó con éxito.
     * @throws Exception Si se viola una restricción de integridad comercial.
     */
    public boolean desactivarServicio(int servicioId) throws Exception {
        // Validación de seguridad física antes de iniciar la transacción
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
            
            // 0. Bloqueo preventivo si hay órdenes de compra activas o históricas
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, servicioId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("No se puede eliminar el servicio porque ya tiene pedidos o citas registradas por clientes.");
                    }
                }
            }
 
            con.setAutoCommit(false); // Aislamiento transaccional

            // 1. Limpieza de preferencias de usuario
            try (PreparedStatement psF = con.prepareStatement(sqlFavoritos)) {
                psF.setInt(1, servicioId);
                psF.executeUpdate();
            }

            // 2. Invalidación de borradores técnicos huérfanos
            try (PreparedStatement psP = con.prepareStatement(sqlPerso)) {
                psP.setInt(1, servicioId);
                psP.executeUpdate();
            }

            // 3. Finalización: Invisibilidad en el catálogo
            try (PreparedStatement psS = con.prepareStatement(sqlServicio)) {
                psS.setInt(1, servicioId);
                int result = psS.executeUpdate();
                
                con.commit(); // Persistencia atómica de todos los pasos
                return result > 0;
            }

        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) {}
            throw new Exception("Error al desactivar servicio en cascada: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Restaura la visibilidad de un servicio en la plataforma pública.
     * 
     * @param servicioId Identificador del registro a habilitar.
     * @return true si la operación JDBC reportó el cambio.
     * @throws Exception Error de conexión.
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
    // CAPA DE RETROCOMPATIBILIDAD (Bridge Pattern)
    // ================================================================

    /**
     * Redirecciona a la nueva implementación del listado maestro.
     * @deprecated Migrar llamadas a {@link #obtenerTodosServicios()}
     */
    @Deprecated
    public List<Servicio> obtenerServicios() throws Exception {
        return obtenerTodosServicios();
    }

    /**
     * Redirecciona a la nueva implementación de búsqueda puntual.
     * @deprecated Migrar llamadas a {@link #obtenerServicioPorId(int)}
     */
    @Deprecated
    public Servicio obtenerPorId(int servicioId) throws Exception {
        return obtenerServicioPorId(servicioId);
    }
}