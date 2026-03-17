package dao;

import config.ConectionDB;
import model.Cita;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
     * Constante que define el número máximo de citas permitidas por slot de tiempo.
     * Valor 1 = solo una cita por horario. Si se necesita permitir múltiples citas,
     * solo se debe cambiar este valor.
     */
    public static final int MAX_APPOINTMENTS_PER_SLOT = 1;

    /**
     * Verifica si un slot de tiempo específico está disponible para agendar una cita.
     * 
     * @param date Fecha de la cita (LocalDate)
     * @param time Hora de la cita (LocalTime)
     * @return true si el horario está disponible, false si ya está ocupado
     * @throws Exception Error de base de datos
     */
    public boolean isSlotAvailable(LocalDate date, LocalTime time) throws Exception {
        String sql = "SELECT COUNT(*) FROM citas c " +
                     "JOIN pedidos p ON c.pedido_id = p.pedido_id " +
                     "WHERE DATE(c.cita_fecha_hora) = ? " +
                     "AND TIME(c.cita_fecha_hora) = ? " +
                     "AND c.cita_estado NOT IN ('cancelada')";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(time));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count < MAX_APPOINTMENTS_PER_SLOT;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar disponibilidad de horario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Sobrecarga del método para verificar disponibilidad con LocalDateTime.
     * 
     * @param fechaHora Fecha y hora de la cita (LocalDateTime)
     * @return true si el horario está disponible, false si ya está ocupado
     * @throws Exception Error de base de datos
     */
    public boolean isSlotAvailable(LocalDateTime fechaHora) throws Exception {
        return isSlotAvailable(fechaHora.toLocalDate(), fechaHora.toLocalTime());
    }

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

            String sqlPer = "SELECT p.categoria_id, a.arreglo_id, a.arreglo_precio_base " +
                    "FROM personalizaciones p " +
                    "JOIN arreglos a ON a.categoria_id = p.categoria_id " +
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
                String sqlCat = "SELECT categoria_id FROM personalizaciones WHERE personalizacion_id = ?";
                int categoriaId = 0;
                try (PreparedStatement ps = con.prepareStatement(sqlCat)) {
                    ps.setInt(1, personalizacionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next())
                            categoriaId = rs.getInt("categoria_id");
                    }
                }

                // Mapeo directo por categoria_id
                String sqlArreglo = "";
                switch (categoriaId) {
                    case 1: // Sastreria
                        sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE categoria_id = 1";
                        break;
                    case 2: // Costuras
                        sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE categoria_id = 2";
                        break;
                    case 3: // Planchado
                        sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE categoria_id = 3";
                        break;
                    case 4: // Arreglos de Medidas
                        sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE categoria_id = 4";
                        break;
                    default:
                        sqlArreglo = "SELECT arreglo_id, arreglo_precio_base FROM arreglos WHERE categoria_id = 1";
                        break;
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
        String sql = "INSERT INTO citas (pedido_id, cita_fecha_hora, cita_estado, cita_notas, cita_motivo, cita_direccion_entrega) VALUES (?, ?, ?, ?, ?, 'Calle 9nb 2occ 04 - Barrio Bavaria 2')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getPedidoId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getCitaFechaHora()));
            ps.setString(3, cita.getCitaEstado());
            ps.setString(4, cita.getCitaNotas()); // Solo las notas, sin dirección
            ps.setString(5, cita.getCitaMotivo());
            // La dirección es fija: "Calle 9nb 2occ 04 - Barrio Bavaria 2"
            
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
        String sql = "SELECT c.*, c.cita_direccion_entrega FROM citas c " +
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
                    c.setDireccionEntrega(rs.getString("cita_direccion_entrega")); // Mapear dirección
                    c.setCitaMotivo(rs.getString("cita_motivo"));
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas: " + e.getMessage());
        }
        return citas;
    }
}