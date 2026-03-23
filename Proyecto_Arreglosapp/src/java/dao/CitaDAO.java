/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar la creación de pedidos y el agendamiento coordinado de citas presenciales.
 */
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
 * Esta clase controla la disponibilidad de horarios y vincula los pedidos con sus citas correspondientes.
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
     * Obtiene una lista de las horas (en formato HH:mm) que ya están ocupadas para una fecha dada.
     * 
     * @param fecha Fecha a consultar
     * @return Lista de horas ocupadas en formato String
     * @throws Exception Error de base de datos
     */
    public List<String> obtenerHorasOcupadasPorFecha(LocalDate fecha) throws Exception {
        String sql = "SELECT TIME_FORMAT(cita_fecha_hora, '%H:%i') FROM citas c " +
                     "WHERE DATE(c.cita_fecha_hora) = ? AND c.cita_estado NOT IN ('cancelada')";
        
        List<String> horasOcupadas = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fecha));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horasOcupadas.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener horas ocupadas: " + e.getMessage());
        }
        return horasOcupadas;
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

            // 1. Obtener datos de la personalización (Meta 4)
            int servicioId = -1;
            double precio = 0;
            String descripcion = "";
            String material = "";
            String imagen = "";

            String sqlPer = "SELECT servicio_id, descripcion, material_tela, imagen_referencia FROM personalizaciones WHERE personalizacion_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlPer)) {
                ps.setInt(1, personalizacionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        servicioId = rs.getInt("servicio_id");
                        descripcion = rs.getString("descripcion");
                        material = rs.getString("material_tela");
                        imagen = rs.getString("imagen_referencia");
                    } else {
                        con.rollback();
                        throw new Exception("Personalización no encontrada: " + personalizacionId);
                    }
                }
            }

            // 2. Obtener precio base del servicio
            String sqlServ = "SELECT servicio_precio_base FROM servicios WHERE servicio_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlServ)) {
                ps.setInt(1, servicioId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        precio = rs.getDouble("servicio_precio_base");
                    } else {
                        con.rollback();
                        throw new Exception("Servicio no encontrado: " + servicioId);
                    }
                }
            }

            // 3. Verificar si ya existe un ARREGLO para esta personalización
            int arregloId = -1;
            String sqlCheckArreglo = "SELECT arreglo_id FROM arreglos WHERE personalizacion_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCheckArreglo)) {
                ps.setInt(1, personalizacionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Ya existe un arreglo, usar el ID existente
                        arregloId = rs.getInt("arreglo_id");
                    }
                }
            }

            // 4. Si no existe, crear nuevo ARREGLO (Meta 4 - Incluir Imagen)
            if (arregloId == -1) {
                String sqlArreglo = "INSERT INTO arreglos (personalizacion_id, arreglo_nombre, arreglo_descripcion, arreglo_precio_base, arreglo_imagen_url) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlArreglo, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, personalizacionId);
                    String nombreArreglo = "Arreglo Personalizado - " + servicioId;
                    ps.setString(2, nombreArreglo);
                    ps.setString(3, descripcion);
                    ps.setDouble(4, precio);
                    ps.setString(5, imagen);
                    
                    int filas = ps.executeUpdate();
                    if (filas > 0) {
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                arregloId = rs.getInt(1);
                            }
                        }
                    }
                    
                    if (arregloId == -1) {
                        con.rollback();
                        throw new Exception("No se pudo generar el ID del arreglo");
                    }
                }
            }

            // 4. Insertar cabecera del pedido
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

            // 5. Insertar DETALLE_PEDIDO para vincular pedido con arreglo
            String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, arreglo_id, detalle_cantidad, detalle_precio_unitario, detalle_subtotal) VALUES (?, ?, 1, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                ps.setInt(1, pedidoId);
                ps.setInt(2, arregloId);
                ps.setDouble(3, precio);
                ps.setDouble(4, precio);
                ps.executeUpdate();
            }

            // 5. No se actualiza personalizaciones con pedido_id (columna no existe en la BD real)
            // La relación se mantiene a través de la secuencia: PERSONALIZACIÓN → ARREGLO → DETALLE_PEDIDO → PEDIDO

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
            ps.setString(3, "pendiente"); // Usar valor válido del ENUM
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
     * Actualiza el estado de una cita específica
     * @param citaId ID de la cita a actualizar
     * @param nuevoEstado Nuevo estado de la cita
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarEstadoCita(int citaId, String nuevoEstado) throws Exception {
        String sql = "UPDATE citas SET cita_estado = ? WHERE cita_id = ?";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nuevoEstado);
            ps.setInt(2, citaId);
            
            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar estado de cita: " + e.getMessage());
        }
    }

    /**
     * Obtiene citas filtradas por nombre de servicio usando LIKE
     * @param userId ID del usuario
     * @param filtroServicio Nombre del servicio a filtrar
     * @return Lista de citas que coinciden con el filtro
     */
    public List<Cita> obtenerCitasFiltradas(int userId, String filtroServicio) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, p.usuario_id, s.servicio_nombre " +
            "FROM citas c " +
            "JOIN pedidos p ON c.pedido_id = p.pedido_id " +
            "JOIN detalle_pedido dp ON p.pedido_id = dp.pedido_id " +
            "JOIN arreglos a ON dp.arreglo_id = a.arreglo_id " +
            "JOIN personalizaciones per ON a.personalizacion_id = per.personalizacion_id " +
            "JOIN servicios s ON per.servicio_id = s.servicio_id " +
            "WHERE p.usuario_id = ? "
        );

        if (filtroServicio != null && !filtroServicio.trim().isEmpty()) {
            sql.append("AND s.servicio_nombre LIKE ? ");
        }

        sql.append("ORDER BY c.cita_fecha_hora DESC");

        List<Cita> citas = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setInt(1, userId);
            
            if (filtroServicio != null && !filtroServicio.trim().isEmpty()) {
                ps.setString(2, "%" + filtroServicio.trim() + "%");
            }

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
                    c.setCitaMotivo(rs.getString("cita_motivo"));
                    c.setDireccionEntrega(rs.getString("cita_direccion_entrega"));
                    // Agregar nombre del servicio para mostrar en filtros
                    // c.setServicioNombre(rs.getString("servicio_nombre")); // Comentado - método no existe
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas filtradas: " + e.getMessage());
        }
        return citas;
    }

    /**
     * Consulta el listado de citas vinculadas a un cliente específico
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