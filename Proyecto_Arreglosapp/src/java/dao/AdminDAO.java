package dao;

import config.ConectionDB;
import model.Usuario;
import java.sql.*;
import java.util.*;

public class AdminDAO {

    // ─── DASHBOARD ────────────────────────────────────────────────

    public int contarPedidosActivos() throws Exception {
        String sql = "SELECT COUNT(*) FROM PEDIDOS WHERE pedido_estado IN ('pendiente','confirmado','en_proceso')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new Exception("Error al contar pedidos: " + e.getMessage());
        }
        return 0;
    }

    public int contarCitasHoy() throws Exception {
        String sql = "SELECT COUNT(*) FROM CITAS WHERE DATE(cita_fecha_hora) = CURDATE() AND cita_estado IN ('programada','confirmada')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new Exception("Error al contar citas: " + e.getMessage());
        }
        return 0;
    }

    public List<Map<String, Object>> obtenerPedidosRecientes() throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_fecha_hora " +
                "FROM PEDIDOS p " +
                "JOIN USUARIOS u ON p.usuario_id = u.user_id " +
                "LEFT JOIN CITAS c ON c.pedido_id = p.pedido_id " +
                "ORDER BY p.pedido_fecha DESC LIMIT 10";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> pedido = new LinkedHashMap<>();
                pedido.put("pedidoId", rs.getInt("pedido_id"));
                pedido.put("estado", rs.getString("pedido_estado"));
                pedido.put("fecha", rs.getTimestamp("pedido_fecha"));
                pedido.put("cliente", rs.getString("user_nombre"));
                pedido.put("email", rs.getString("user_email"));
                pedido.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                lista.add(pedido);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener pedidos: " + e.getMessage());
        }
        return lista;
    }

    // ─── USUARIOS ─────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerUsuarios(String busqueda) throws Exception {
        String sql = "SELECT u.user_id, u.user_nombre, u.user_email, u.rol_id, " +
                "t.telefono_numero " +
                "FROM USUARIOS u " +
                "LEFT JOIN TELEFONOS t ON t.user_id = u.user_id AND t.telefono_es_principal = true " +
                "WHERE u.rol_id = 2 ";

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql += "AND (u.user_nombre LIKE ? OR u.user_email LIKE ?) ";
        }
        sql += "ORDER BY u.user_id DESC";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                String like = "%" + busqueda.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> u = new LinkedHashMap<>();
                    u.put("userId", rs.getInt("user_id"));
                    u.put("nombre", rs.getString("user_nombre"));
                    u.put("email", rs.getString("user_email"));
                    u.put("telefono", rs.getString("telefono_numero"));
                    u.put("rolId", rs.getInt("rol_id"));
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarUsuario(int userId) throws Exception {
        String sql = "DELETE FROM USUARIOS WHERE user_id = ? AND rol_id = 2";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // ─── SERVICIOS ────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerServicios() throws Exception {
        String sql = "SELECT servicio_id, servicio_nombre, servicio_descripcion, servicio_precio, servicio_imagen " +
                "FROM SERVICIOS ORDER BY servicio_id ASC";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> s = new LinkedHashMap<>();
                s.put("servicioId", rs.getInt("servicio_id"));
                s.put("nombre", rs.getString("servicio_nombre"));
                s.put("descripcion", rs.getString("servicio_descripcion"));
                s.put("precio", rs.getDouble("servicio_precio"));
                s.put("imagen", rs.getString("servicio_imagen"));
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener servicios: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarServicio(int servicioId) throws Exception {
        String sql = "DELETE FROM SERVICIOS WHERE servicio_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al eliminar servicio: " + e.getMessage());
        }
    }

    public boolean actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws Exception {
        String sql = "UPDATE PEDIDOS SET pedido_estado = ? WHERE pedido_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, pedidoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar estado: " + e.getMessage());
        }
    }

    public Map<String, Object> obtenerDetallePedido(int pedidoId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, " +
                "u.user_nombre, u.user_email, u.user_ubicacion_direccion, " +
                "c.cita_fecha_hora, c.cita_notas, c.cita_estado, " +
                "s.servicio_nombre, s.servicio_precio " +
                "FROM PEDIDOS p " +
                "JOIN USUARIOS u ON p.usuario_id = u.user_id " +
                "LEFT JOIN CITAS c ON c.pedido_id = p.pedido_id " +
                "LEFT JOIN PERSONALIZACIONES per ON per.personalizacion_id = p.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON s.servicio_id = per.servicio_id " +
                "WHERE p.pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> detalle = new LinkedHashMap<>();
                    detalle.put("pedidoId", rs.getInt("pedido_id"));
                    detalle.put("estado", rs.getString("pedido_estado"));
                    detalle.put("fecha", rs.getTimestamp("pedido_fecha"));
                    detalle.put("cliente", rs.getString("user_nombre"));
                    detalle.put("email", rs.getString("user_email"));
                    detalle.put("direccion", rs.getString("user_ubicacion_direccion"));
                    detalle.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                    detalle.put("citaNotas", rs.getString("cita_notas"));
                    detalle.put("citaEstado", rs.getString("cita_estado"));
                    detalle.put("servicio", rs.getString("servicio_nombre"));
                    detalle.put("precio", rs.getDouble("servicio_precio"));
                    return detalle;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener detalle: " + e.getMessage());
        }
        return null;
    }
}