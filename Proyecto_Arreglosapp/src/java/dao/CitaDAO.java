package dao;

import config.ConectionDB;
import model.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la creación de pedidos y el agendamiento de citas asociadas.
 * RF-07: Selección de Servicios y RF-08: Agendamiento de Citas.
 * 
 * @author Antigravity - Senior Architect
 */
public class CitaDAO {

    /**
     * Crea un pedido transaccionalmente a partir de una personalización.
     * Realiza un mapeo dinámico de la categoría al arreglo correspondiente.
     * 
     * @param userId ID del cliente.
     * @param personalizacionId ID de la personalización solicitada.
     * @return El ID del pedido generado o -1 si falló.
     */
    public int crearPedido(int userId, int personalizacionId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Iniciar flujo transaccional

            // 1. Obtener categoría desde personalizaciones y mapear a arreglo_id
            int arregloId = -1;
            double precio = 0;

            String sqlPer = "SELECT p.categoria, a.arreglo_id, a.arreglo_precio_base " +
                    "FROM personalizaciones p " +
                    "JOIN categorias c ON LOWER(c.categoria_nombre) LIKE CONCAT('%', LOWER(SUBSTRING_INDEX(p.categoria, '/', 1)), '%') "
                    +
                    "JOIN arreglos a ON a.categoria_id = c.categoria_id " +
                    "WHERE p.personalizacion_id = ? LIMIT 1";

            try (PreparedStatement ps = con.prepareStatement(sqlPer)) {
                ps.setInt(1, personalizacionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        arregloId = rs.getInt("arreglo_id");
                        precio = rs.getDouble("arreglo_precio_base");
                    }
                }
            }

            // Si no encuentra por JOIN, buscar por mapeo directo (Lógica de contingencia)
            if (arregloId == -1 && personalizacionId != -1) {
                String sqlCat = "SELECT categoria FROM personalizaciones WHERE personalizacion_id = ?";
                String categoria = "";
                try (PreparedStatement ps = con.prepareStatement(sqlCat)) {
                    ps.setInt(1, personalizacionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next())
                            categoria = rs.getString("categoria");
                    }
                }

                // Mapeo manual por palabras clave en la categoría
                String sqlArreglo = "";
                if (categoria.toLowerCase().contains("dobladillo") || categoria.toLowerCase().contains("sastr")) {
                    sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE arreglo_id = 1";
                } else if (categoria.toLowerCase().contains("costura")) {
                    sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE arreglo_id = 2";
                } else if (categoria.toLowerCase().contains("planch")) {
                    sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE arreglo_id = 3";
                } else if (categoria.toLowerCase().contains("medida") || categoria.toLowerCase().contains("ajuste")) {
                    sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE arreglo_id = 4";
                } else if (categoria.toLowerCase().contains("estrech") || categoria.toLowerCase().contains("ensanch")
                        || categoria.toLowerCase().contains("vestido")) {
                    sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE arreglo_id = 5";
                } else if (categoria.toLowerCase().contains("recortar")) {
                    sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE arreglo_id = 1";
                }

                if (!sqlArreglo.isEmpty()) {
                    try (PreparedStatement ps = con.prepareStatement(sqlArreglo);
                            ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            arregloId = rs.getInt("arreglo_id");
                            precio = rs.getDouble("arreglo_precio_base");
                        }
                    }
                }
            }

            // 2. Insertar cabecera del pedido
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

            // 3. Insertar detalle del pedido si se identificó un arreglo
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

            // 4. Vincular la personalización con el arreglo definitivo Y con el pedido creado
            if (personalizacionId != -1) {
                String sqlUpdate;
                if (arregloId != -1) {
                    sqlUpdate = "UPDATE personalizaciones SET arreglo_id = ?, pedido_id = ? WHERE personalizacion_id = ?";
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                        ps.setInt(1, arregloId);
                        ps.setInt(2, pedidoId);
                        ps.setInt(3, personalizacionId);
                        ps.executeUpdate();
                    }
                } else {
                    sqlUpdate = "UPDATE personalizaciones SET pedido_id = ? WHERE personalizacion_id = ?";
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                        ps.setInt(1, pedidoId);
                        ps.setInt(2, personalizacionId);
                        ps.executeUpdate();
                    }
                }
            }

            con.commit(); // Confirmar éxito de toda la operación
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

    /**
     * Sobrecarga para crear pedidos simples (no requiere personalizacionId).
     */
    public int crearPedido(int userId) throws Exception {
        return crearPedido(userId, -1);
    }

    /**
     * Registra una cita física para la entrega o toma de medidas de un pedido.
     * RF-08: Agendamiento de Citas.
     */
    public boolean crearCita(Cita cita) throws Exception {
        String sql = "INSERT INTO citas (pedido_id, cita_fecha_hora, cita_estado, cita_notas, cita_motivo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getPedidoId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getCitaFechaHora()));
            ps.setString(3, cita.getCitaEstado());
            
            // Construcción modular de notas
            String notasCompletas = "Dirección: " + cita.getDireccionEntrega();
            if (cita.getCitaNotas() != null && !cita.getCitaNotas().trim().isEmpty()) {
                notasCompletas += " | Notas: " + cita.getCitaNotas();
            }
            ps.setString(4, notasCompletas);
            ps.setString(5, cita.getCitaMotivo());
            
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

    /**
     * Consulta el listado de citas vinculadas a un cliente específico.
     */
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