package dao;

import config.ConectionDB;
import model.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    // Obtener pedidos activos de un usuario con su cita
    public List<Map<String, Object>> obtenerPedidosActivos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, " +
                     "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas " +
                     "FROM PEDIDOS p " +
                     "LEFT JOIN CITAS c ON c.pedido_id = p.pedido_id " +
                     "WHERE p.usuario_id = ? " +
                     "AND p.pedido_estado IN ('pendiente','confirmado','en_proceso') " +
                     "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId);
    }

    // Obtener historial de pedidos de un usuario
    public List<Map<String, Object>> obtenerHistorialPedidos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, " +
                     "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas " +
                     "FROM PEDIDOS p " +
                     "LEFT JOIN CITAS c ON c.pedido_id = p.pedido_id " +
                     "WHERE p.usuario_id = ? " +
                     "AND p.pedido_estado IN ('terminado','cancelado') " +
                     "ORDER BY p.pedido_fecha_creacion DESC";
        return ejecutarConsulta(sql, userId);
    }

    private List<Map<String, Object>> ejecutarConsulta(String sql, int userId) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("pedidoId",           rs.getInt("pedido_id"));
                    fila.put("pedidoEstado",        rs.getString("pedido_estado"));
                    fila.put("pedidoTotal",         rs.getBigDecimal("pedido_total"));
                    fila.put("citaId",              rs.getInt("cita_id"));
                    fila.put("citaEstado",          rs.getString("cita_estado"));
                    fila.put("citaNotas",           rs.getString("cita_notas"));

                    if (rs.getTimestamp("pedido_fecha_creacion") != null) {
                        fila.put("pedidoFecha", rs.getTimestamp("pedido_fecha_creacion").toLocalDateTime());
                    }
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        fila.put("citaFechaHora", rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener pedidos: " + e.getMessage());
        }
        return lista;
    }

    // Cancelar un pedido
    public boolean cancelarPedido(int pedidoId, int userId) throws Exception {
        String sql = "UPDATE PEDIDOS SET pedido_estado = 'cancelado', " +
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
}