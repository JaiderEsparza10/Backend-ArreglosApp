package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    public List<Map<String, Object>> obtenerPedidosActivos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo " +
                "FROM pedidos p " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('pendiente','confirmado','en_proceso') " +
                "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId);
    }

    public List<Map<String, Object>> obtenerHistorialPedidos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo " +
                "FROM pedidos p " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('terminado','cancelado') " +
                "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId);
    }

    public List<Map<String, Object>> obtenerPedidosPorEstado(int userId, String estado) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo " +
                "FROM pedidos p " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado = ? " +
                "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId, estado);
    }

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
                    p.put("pedidoTotal", rs.getDouble("pedido_total"));
                    if (rs.getTimestamp("pedido_fecha_creacion") != null) {
                        p.put("pedidoFecha", rs.getTimestamp("pedido_fecha_creacion").toLocalDateTime());
                    }
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
}