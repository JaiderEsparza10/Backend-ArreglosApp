package dao;

import config.ConectionDB;
import model.Usuario;
import java.sql.*;
import java.util.*;

public class AdminDAO {
    // ─── DASHBOARD ────────────────────────────────────────────────

    public int contarPedidosActivos() throws Exception {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE pedido_estado IN ('pendiente','confirmado','en_proceso')";
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
        String sql = "SELECT COUNT(*) FROM citas WHERE DATE(cita_fecha_hora) = CURDATE() AND cita_estado IN ('programada','confirmada')";
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
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_fecha_hora, c.cita_estado " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON c.pedido_id = p.pedido_id " +
                "ORDER BY p.pedido_fecha_creacion DESC LIMIT 10";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> pedido = new LinkedHashMap<>();
                pedido.put("pedidoId", rs.getInt("pedido_id"));
                pedido.put("estado", rs.getString("pedido_estado"));
                pedido.put("citaEstado", rs.getString("cita_estado"));
                pedido.put("fecha", rs.getTimestamp("pedido_fecha_creacion"));
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

    public List<Map<String, Object>> obtenerTodasLasCitas() throws Exception {
        String sql = "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, " +
                "u.user_nombre, p.pedido_id " +
                "FROM citas c " +
                "JOIN pedidos p ON p.pedido_id = c.pedido_id " +
                "JOIN usuarios u ON u.user_id = p.usuario_id " +
                "ORDER BY c.cita_fecha_hora DESC";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> cita = new LinkedHashMap<>();
                cita.put("citaId", rs.getInt("cita_id"));
                cita.put("pedidoId", rs.getInt("pedido_id"));
                cita.put("fechaHora", rs.getTimestamp("cita_fecha_hora"));
                cita.put("estado", rs.getString("cita_estado"));
                cita.put("notas", rs.getString("cita_notas"));
                cita.put("cliente", rs.getString("user_nombre"));
                lista.add(cita);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas: " + e.getMessage());
        }
        return lista;
    }

    public boolean cambiarEstadoCita(int citaId, String nuevoEstado) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);

            // 1. Actualizar estado de la cita
            String sqlCita = "UPDATE citas SET cita_estado = ? WHERE cita_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCita)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, citaId);
                ps.executeUpdate();
            }

            // 2. Sincronizar estado del pedido según estado de cita
            String nuevoPedidoEstado = null;
            if (nuevoEstado.equals("confirmada")) {
                nuevoPedidoEstado = "confirmado";
            } else if (nuevoEstado.equals("completada")) {
                nuevoPedidoEstado = "terminado";
            } else if (nuevoEstado.equals("cancelada")) {
                nuevoPedidoEstado = "cancelado";
            }

            if (nuevoPedidoEstado != null) {
                String sqlPedido = "UPDATE pedidos SET pedido_estado = ?, " +
                        "pedido_fecha_actualizacion = CURRENT_TIMESTAMP " +
                        "WHERE pedido_id = (SELECT pedido_id FROM citas WHERE cita_id = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setString(1, nuevoPedidoEstado);
                    ps.setInt(2, citaId);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null)
                con.rollback();
            throw new Exception("Error al cambiar estado cita: " + e.getMessage());
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public int contarTodasLasCitas() throws Exception {
        String sql = "SELECT COUNT(*) FROM citas";
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

    // ─── USUARIOS ─────────────────────────────────────────────────

    public List<Map<String, Object>> obtenerUsuarios(String busqueda) throws Exception {
        String sql = "SELECT u.user_id, u.user_nombre, u.user_email, u.rol_id, " +
                "t.telefono_numero " +
                "FROM usuarios u " +
                "LEFT JOIN telefonos t ON t.user_id = u.user_id AND t.telefono_es_principal = true " +
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
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);

            // Eliminar registros relacionados en orden
            String[] tablas = {
                    "DELETE FROM favoritos WHERE user_id = ?",
                    "DELETE FROM telefonos WHERE user_id = ?",
                    "DELETE FROM detalle_pedido WHERE pedido_id IN (SELECT pedido_id FROM pedidos WHERE usuario_id = ?)",
                    "DELETE FROM citas WHERE pedido_id IN (SELECT pedido_id FROM pedidos WHERE usuario_id = ?)",
                    "DELETE FROM personalizaciones WHERE user_id = ?",
                    "DELETE FROM pedidos WHERE usuario_id = ?",
                    "DELETE FROM usuarios WHERE user_id = ? AND rol_id = 2"
            };

            for (String sql : tablas) {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null)
                con.rollback();
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    // ─── SERVICIOS (tabla arreglos) ───────────────────────────────

    public List<Map<String, Object>> obtenerServicios() throws Exception {
        String sql = "SELECT arreglo_id, arreglo_nombre, arreglo_descripcion, " +
                "arreglo_precio_base, arreglo_imagen_url " +
                "FROM arreglos WHERE arreglo_disponible = 1 ORDER BY arreglo_id ASC";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> s = new LinkedHashMap<>();
                s.put("servicioId", rs.getInt("arreglo_id"));
                s.put("nombre", rs.getString("arreglo_nombre"));
                s.put("descripcion", rs.getString("arreglo_descripcion"));
                s.put("precio", rs.getDouble("arreglo_precio_base"));
                s.put("imagen", rs.getString("arreglo_imagen_url"));
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener servicios: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarServicio(int servicioId) throws Exception {
        String sql = "UPDATE arreglos SET arreglo_disponible = 0 WHERE arreglo_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al eliminar servicio: " + e.getMessage());
        }
    }

    public boolean actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws Exception {
        String sql = "UPDATE pedidos SET pedido_estado = ? WHERE pedido_id = ?";
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
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, " +
                "u.user_nombre, u.user_email, u.user_ubicacion_direccion, " +
                "c.cita_fecha_hora, c.cita_notas, c.cita_estado, " +
                "a.arreglo_nombre, a.arreglo_precio_base " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON c.pedido_id = p.pedido_id " +
                "LEFT JOIN personalizaciones per ON per.personalizacion_id = p.pedido_id " +
                "LEFT JOIN arreglos a ON a.arreglo_id = per.arreglo_id " +
                "WHERE p.pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> detalle = new LinkedHashMap<>();
                    detalle.put("pedidoId", rs.getInt("pedido_id"));
                    detalle.put("estado", rs.getString("pedido_estado"));
                    detalle.put("fecha", rs.getTimestamp("pedido_fecha_creacion"));
                    detalle.put("cliente", rs.getString("user_nombre"));
                    detalle.put("email", rs.getString("user_email"));
                    detalle.put("direccion", rs.getString("user_ubicacion_direccion"));
                    detalle.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                    detalle.put("citaNotas", rs.getString("cita_notas"));
                    detalle.put("citaEstado", rs.getString("cita_estado"));
                    detalle.put("servicio", rs.getString("arreglo_nombre"));
                    detalle.put("precio", rs.getDouble("arreglo_precio_base"));
                    return detalle;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener detalle: " + e.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> obtenerCitasHoy() throws Exception {
        String sql = "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, " +
                "u.user_nombre, u.user_email, p.pedido_id " +
                "FROM citas c " +
                "JOIN pedidos p ON p.pedido_id = c.pedido_id " +
                "JOIN usuarios u ON u.user_id = p.usuario_id " +
                "WHERE DATE(c.cita_fecha_hora) = CURDATE() " +
                "ORDER BY c.cita_fecha_hora ASC";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> cita = new LinkedHashMap<>();
                cita.put("citaId", rs.getInt("cita_id"));
                cita.put("pedidoId", rs.getInt("pedido_id"));
                cita.put("fechaHora", rs.getTimestamp("cita_fecha_hora"));
                cita.put("estado", rs.getString("cita_estado"));
                cita.put("notas", rs.getString("cita_notas"));
                cita.put("cliente", rs.getString("user_nombre"));
                lista.add(cita);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas: " + e.getMessage());
        }
        return lista;
    }
}