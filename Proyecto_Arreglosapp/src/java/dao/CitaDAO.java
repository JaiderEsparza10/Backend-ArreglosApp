package dao;

import config.ConectionDB;
import model.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    // Crear pedido automáticamente y retornar su ID
    public int crearPedido(int userId) throws Exception {
        String sql = "INSERT INTO PEDIDOS (usuario_id, pedido_estado, pedido_total) VALUES (?, 'pendiente', 0)";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new Exception("Error al crear pedido: " + e.getMessage());
        }
        return -1;
    }

    // Crear la cita asociada al pedido
    public boolean crearCita(Cita cita) throws Exception {
        String sql = "INSERT INTO CITAS (pedido_id, cita_fecha_hora, cita_estado, cita_notas) VALUES (?, ?, ?, ?)";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getPedidoId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getCitaFechaHora()));
            ps.setString(3, cita.getCitaEstado());
            // Guardar notas + dirección juntos en cita_notas
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

    // Obtener citas de un usuario
    public List<Cita> obtenerCitasPorUsuario(int userId) throws Exception {
        String sql = "SELECT c.* FROM CITAS c " +
                "JOIN PEDIDOS p ON c.pedido_id = p.pedido_id " +
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