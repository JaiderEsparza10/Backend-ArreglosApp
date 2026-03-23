/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Centralizar la gestión de las órdenes de servicio y su ciclo de vida.
 */
package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase maneja la recuperación de pedidos activos, el historial y las cancelaciones.
 */
public class PedidoDAO {

    public List<Map<String, Object>> obtenerPedidosActivos(int userId) throws Exception {
        System.out.println("=== DEBUG: PedidoDAO.obtenerPedidosActivos() llamado para userId=" + userId + " ===");
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('pendiente','confirmado','en_proceso') " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId);
    }

    public List<Map<String, Object>> obtenerHistorialPedidos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('terminado','cancelado') " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId);
    }

    public List<Map<String, Object>> obtenerPedidosPorEstado(int userId, String estado) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado = ? " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId, estado);
    }

    public List<Map<String, Object>> buscarPedidosPorNombreServicio(int userId, String nombreServicio) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND s.servicio_nombre LIKE ? " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId, "%" + nombreServicio + "%");
    }

    public boolean cancelarPedido(int pedidoId, int userId) throws Exception {
        String sql = "UPDATE PEDIDOS SET pedido_estado = 'cancelado', " +
                "pedido_fecha = CURRENT_TIMESTAMP " +
                "WHERE pedido_id = ? AND usuario_id = ? " +
                "AND pedido_estado IN ('pendiente','confirmado')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al cancelar pedido: " + e.getMessage());
        }
    }

    // Método para Dashboard/Admin - Lista todos los pedidos con datos completos
    public List<Map<String, Object>> listarTodos() throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, p.usuario_id, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM pedidos p " +
                "LEFT JOIN detalle_pedido dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN arreglos a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN personalizaciones per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN servicios s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsultaAdmin(sql);
    }

    // Método para Dashboard/Admin - Lista pedidos por estado
    public List<Map<String, Object>> listarPorEstado(String estado) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, p.usuario_id, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM pedidos p " +
                "LEFT JOIN detalle_pedido dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN arreglos a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN personalizaciones per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN servicios s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.pedido_estado = ? " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsultaAdmin(sql, estado);
    }

    private List<Map<String, Object>> ejecutarConsulta(String sql, Object... params) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        System.out.println("=== DEBUG EJECUTAR CONSULTA ===");
        System.out.println("SQL: " + sql);
        System.out.println("Parámetros: " + java.util.Arrays.toString(params));
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    System.out.println("=== ENCONTRADA FILA #" + rowCount + " ===");
                    Map<String, Object> p = new LinkedHashMap<>();
                    // Datos del pedido
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("pedidoEstado", rs.getString("pedido_estado"));
                    p.put("pedidoTotal", rs.getDouble("pedido_total"));
                    if (rs.getTimestamp("pedido_fecha") != null) {
                        p.put("pedidoFecha", rs.getTimestamp("pedido_fecha").toLocalDateTime());
                    }
                    
                    // Datos del servicio
                    p.put("servicioId", rs.getInt("servicio_id"));
                    p.put("servicioNombre", rs.getString("servicio_nombre"));
                    p.put("servicioDescripcion", rs.getString("servicio_descripcion"));
                    p.put("servicioPrecioBase", rs.getDouble("servicio_precio_base"));
                    
                    // Datos del arreglo
                    p.put("arregloId", rs.getInt("arreglo_id"));
                    p.put("arregloNombre", rs.getString("arreglo_nombre"));
                    p.put("arregloDescripcion", rs.getString("arreglo_descripcion"));
                    p.put("arregloImagenUrl", rs.getString("arreglo_imagen_url"));
                    
                    // Datos de la personalización
                    p.put("personalizacionDescripcion", rs.getString("personalizacion_descripcion"));
                    p.put("materialTela", rs.getString("material_tela"));
                    
                    // Datos de la cita
                    p.put("citaId", rs.getInt("cita_id"));
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        p.put("citaFechaHora", rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    p.put("citaEstado", rs.getString("cita_estado"));
                    p.put("citaNotas", rs.getString("cita_notas"));
                    p.put("citaMotivo", rs.getString("cita_motivo"));
                    p.put("citaDireccion", rs.getString("cita_direccion_entrega"));
                    
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("=== ERROR SQL ===");
            System.out.println("Error: " + e.getMessage());
            throw new Exception("Error al ejecutar consulta de pedidos: " + e.getMessage());
        }
        System.out.println("=== TAMAÑO LISTA FINAL: " + lista.size() + " ===");
        return lista;
    }

    private List<Map<String, Object>> ejecutarConsultaAdmin(String sql, Object... params) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new LinkedHashMap<>();
                    // Datos del pedido
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("pedidoEstado", rs.getString("pedido_estado"));
                    p.put("pedidoTotal", rs.getDouble("pedido_total"));
                    p.put("usuarioId", rs.getInt("usuario_id"));
                    if (rs.getTimestamp("pedido_fecha") != null) {
                        p.put("pedidoFecha", rs.getTimestamp("pedido_fecha").toLocalDateTime());
                    }
                    
                    // Datos del servicio
                    p.put("servicioId", rs.getInt("servicio_id"));
                    p.put("servicioNombre", rs.getString("servicio_nombre"));
                    p.put("servicioDescripcion", rs.getString("servicio_descripcion"));
                    p.put("servicioPrecioBase", rs.getDouble("servicio_precio_base"));
                    
                    // Datos del arreglo
                    p.put("arregloId", rs.getInt("arreglo_id"));
                    p.put("arregloNombre", rs.getString("arreglo_nombre"));
                    p.put("arregloDescripcion", rs.getString("arreglo_descripcion"));
                    p.put("arregloImagenUrl", rs.getString("arreglo_imagen_url"));
                    
                    // Datos de la personalización
                    p.put("personalizacionDescripcion", rs.getString("personalizacion_descripcion"));
                    p.put("materialTela", rs.getString("material_tela"));
                    
                    // Datos del usuario (para admin)
                    p.put("userNombre", rs.getString("user_nombre"));
                    p.put("userEmail", rs.getString("user_email"));
                    
                    // Datos de la cita
                    p.put("citaId", rs.getInt("cita_id"));
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        p.put("citaFechaHora", rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    p.put("citaEstado", rs.getString("cita_estado"));
                    p.put("citaNotas", rs.getString("cita_notas"));
                    p.put("citaMotivo", rs.getString("cita_motivo"));
                    p.put("citaDireccion", rs.getString("cita_direccion_entrega"));
                    
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al ejecutar consulta de pedidos admin: " + e.getMessage());
        }
        return lista;
    }
}