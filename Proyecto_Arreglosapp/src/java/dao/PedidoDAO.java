package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase proporciona métodos para consultar y gestionar el estado de los
 * pedidos.
 */
public class PedidoDAO {

    /**
     * Recupera la lista de pedidos activos (pendientes, confirmados o en proceso)
     * de un usuario.
     */
    public List<Map<String, Object>> obtenerPedidosActivos(int userId) throws Exception {
        String sql = "SELECT p.*, c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo " +
                "FROM pedidos p " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('pendiente','confirmado','en_proceso') " +
                "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId);
    }

    /**
     * Obtiene el historial de pedidos finalizados o cancelados de un usuario.
     */
    public List<Map<String, Object>> obtenerHistorialPedidos(int userId) throws Exception {
        String sql = "SELECT p.*, c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo " +
                "FROM pedidos p " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('terminado','cancelado') " +
                "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId);
    }

    /**
     * Filtra los pedidos de un usuario por un estado específico.
     */
    public List<Map<String, Object>> obtenerPedidosPorEstado(int userId, String estado) throws Exception {
        String sql = "SELECT p.*, c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo " +
                "FROM pedidos p " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado = ? " +
                "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId, estado);
    }

    /**
     * Cancela un pedido si se encuentra en estado pendiente o confirmado.
     */
    public boolean cancelarPedido(int pedidoId, int userId) throws Exception {
        String sql = "UPDATE pedidos SET pedido_estado = 'cancelado', " +
                "pedido_fecha_actualizacion = CURRENT_TIMESTAMP " +
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

    /**
     * Método genérico para ejecutar consultas y mapear resultados a una lista de mapas.
     */
    private List<Map<String, Object>> ejecutarConsulta(String sql, Object... params) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new LinkedHashMap<>();
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("pedidoEstado", rs.getString("pedido_estado"));
                    
                    // Columnas de base (siempre deben existir)
                    try { p.put("pedidoTotal", rs.getBigDecimal("pedido_total")); } catch (SQLException e) { p.put("pedidoTotal", 0.0); }
                    
                    // Columnas opcionales (pueden faltar en versiones anteriores del esquema)
                    p.put("pedidoMontoAbonado", obtenerColumnaOpcionalBigDecimal(rs, "pedido_monto_abonado", 0.0));
                    p.put("pedidoPagoEstado", obtenerColumnaOpcionalString(rs, "pedido_pago_estado", "pendiente"));
                    p.put("pedidoEntregaEstado", obtenerColumnaOpcionalString(rs, "pedido_entrega_estado", "pendiente"));
                    
                    // Manejo de fechas de pedido
                    if (rs.getTimestamp("pedido_fecha_creacion") != null) {
                        p.put("pedidoFecha", rs.getTimestamp("pedido_fecha_creacion").toLocalDateTime());
                    }

                    // Datos de la cita (pueden ser nulos por el LEFT JOIN)
                    p.put("citaId", rs.getInt("cita_id"));
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        p.put("citaFechaHora", rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    p.put("citaEstado", rs.getString("cita_estado"));
                    p.put("citaNotas", rs.getString("cita_notas"));
                    p.put("citaMotivo", rs.getString("cita_motivo"));
                    
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al ejecutar consulta de pedidos: " + e.getMessage());
        }
        return lista;
    }

    private String obtenerColumnaOpcionalString(ResultSet rs, String columna, String valorDefecto) {
        try {
            return rs.getString(columna);
        } catch (SQLException e) {
            return valorDefecto;
        }
    }

    private Object obtenerColumnaOpcionalBigDecimal(ResultSet rs, String columna, double valorDefecto) {
        try {
            return rs.getBigDecimal(columna);
        } catch (SQLException e) {
            return valorDefecto;
        }
    }
}