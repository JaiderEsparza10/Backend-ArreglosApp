package dao;

import config.ConectionDB;
import model.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public int crearPedido(int userId, int personalizacionId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);

            // 1. Obtener arreglo_id y precio desde personalizaciones
            int arregloId = -1;
            double precio = 0;

            String sqlPer = "SELECT p.arreglo_id, a.arreglo_precio_base " +
                    "FROM personalizaciones p " +
                    "JOIN arreglos a ON a.arreglo_id = p.arreglo_id " +
                    "WHERE p.personalizacion_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlPer)) {
                ps.setInt(1, personalizacionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        arregloId = rs.getInt("arreglo_id");
                        precio = rs.getDouble("arreglo_precio_base");
                    }
                }
            }

            // 2. Crear pedido
            int pedidoId = -1;
            String sqlPedido = "INSERT INTO pedidos (usuario_id, pedido_estado, pedido_total) VALUES (?, 'pendiente', ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setDouble(2, precio);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        pedidoId = rs.getInt(1);
                }
            }

            if (pedidoId == -1) {
                con.rollback();
                return -1;
            }

            // 3. Insertar en detalle_pedido si hay arreglo
            if (arregloId != -1) {
                String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, arreglo_id, detalle_cantidad, " +
                        "detalle_precio_unitario, detalle_subtotal) VALUES (?, ?, 1, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    ps.setInt(1, pedidoId);
                    ps.setInt(2, arregloId);
                    ps.setDouble(3, precio);
                    ps.setDouble(4, precio);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return pedidoId;

        } catch (SQLException e) {
            if (con != null)
                con.rollback();
            throw new Exception("Error al crear pedido: " + e.getMessage());
        } finally {
            if (con != null)
                con.close();
        }
    }

    // Mantener compatibilidad con llamadas sin personalizacionId
    public int crearPedido(int userId) throws Exception {
        return crearPedido(userId, -1);
    }

    public boolean crearCita(Cita cita) throws Exception {
        String sql = "INSERT INTO citas (pedido_id, cita_fecha_hora, cita_estado, cita_notas) VALUES (?, ?, ?, ?)";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getPedidoId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getCitaFechaHora()));
            ps.setString(3, cita.getCitaEstado());
            String notasCompletas = "Dirección: " + cita.getDireccionEntrega();
            if (cita.getCitaNotas() != null && !cita.getCitaNotas().trim().isEmpty()) {
                notasCompletas += " | Notas: " + cita.getCitaNotas();
            }
            ps.setString(4, notasCompletas);
            int filas = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    cita.setCitaId(rs.getInt(1));
            }
            return filas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al crear cita: " + e.getMessage());
        }
    }

    public List<Cita> obtenerCitasPorUsuario(int userId) throws Exception {
        String sql = "SELECT c.* FROM citas c " +
                "JOIN pedidos p ON c.pedido_id = p.pedido_id " +
                "WHERE p.usuario_id = ? ORDER BY c.cita_fecha_hora DESC";
        List<Cita> citas = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cita c = new Cita();
                    c.setCitaId(rs.getInt("cita_id"));
                    c.setPedidoId(rs.getInt("pedido_id"));
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        c.setCitaFechaHora(rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    c.setCitaEstado(rs.getString("cita_estado"));
                    c.setCitaNotas(rs.getString("cita_notas"));
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas: " + e.getMessage());
        }
        return citas;
    }
}