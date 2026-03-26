/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: CitaDAO.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Gestiona el ciclo de vida de los pedidos y la agenda de citas.
 *               Implementa reglas de negocio para la concurrencia de horarios
 *               y la integración transaccional entre personalizaciones y órdenes.
 * ══════════════════════════════════════════════════════════════════════════════
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
 * Clase de Acceso a Datos (DAO) para la gestión operativa de citas y pedidos.
 * Centraliza el control de disponibilidad y el motor de generación de pedidos.
 */
public class CitaDAO {

    /**
     * Constante que define el número máximo de citas permitidas por slot de tiempo.
     * Valor 1 = solo una cita por horario. Si se necesita permitir múltiples citas,
     * solo se debe cambiar este valor.
     */
    public static final int MAX_APPOINTMENTS_PER_SLOT = 1;

    /**
     * Valida la factibilidad técnica de agendar en un momento específico.
     * Cruza la información con las citas existentes (no canceladas).
     * 
     * @param date Fecha del calendario.
     * @param time Bloque horario (HH:mm).
     * @return true si el contador de ocupación es inferior al límite configurado (MAX_APPOINTMENTS_PER_SLOT).
     * @throws Exception Error de conexión o consulta JDBC.
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
     * Versión simplificada para validación integral con LocalDateTime.
     * 
     * @param fechaHora Instancia temporal completa.
     * @return true si hay cupo disponible.
     * @throws Exception Error JDBC.
     */
    public boolean isSlotAvailable(LocalDateTime fechaHora) throws Exception {
        return isSlotAvailable(fechaHora.toLocalDate(), fechaHora.toLocalTime());
    }

    /**
     * Genera un reporte de congestión para una fecha específica.
     * Útil para filtrar slots disponibles en el lado del cliente (Frontend).
     * 
     * @param fecha Día a inspeccionar.
     * @return Lista de horas bloqueadas (formato HH:mm).
     * @throws Exception Error SQL.
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
     * Motor de conversión: Transforma un requerimiento de personalización en una orden de venta firme.
     * Este proceso es ATÓMICO y vincula: Personalización -> Arreglo -> Pedido -> Detalle.
     * 
     * @param userId            Identificador del cliente contratante.
     * @param personalizacionId Requerimiento técnico base.
     * @return ID numérico de la nueva orden (o -1 en fallo de integridad).
     * @throws Exception Si ocurre un fallo en el "Commit" o violación de restricciones.
     */
    public int crearPedido(int userId, int personalizacionId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Apertura de transacción crítica

            // Fase 1: Extracción de parámetros técnicos de la personalización
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

            // Fase 2: Tasación económica basada en el servicio original
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

            // Fase 3: Idempotencia - Verificar existencia previa del Arreglo
            int arregloId = -1;
            String sqlCheckArreglo = "SELECT arreglo_id FROM arreglos WHERE personalizacion_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCheckArreglo)) {
                ps.setInt(1, personalizacionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        arregloId = rs.getInt("arreglo_id"); // Reutilización de entidad existente
                    }
                }
            }

            // Fase 4: Materialización del requerimiento en un Arreglo catalogado
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

            // Fase 5: Registro de la cabecera de la Orden Comercial
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

            // Fase 6: Cierre del vínculo mediante Detalle del Pedido
            String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, arreglo_id, detalle_cantidad, detalle_precio_unitario, detalle_subtotal) VALUES (?, ?, 1, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                ps.setInt(1, pedidoId);
                ps.setInt(2, arregloId);
                ps.setDouble(3, precio);
                ps.setDouble(4, precio);
                ps.executeUpdate();
            }

            // Nota Técnica: La trazabilidad se garantiza por la cadena FK en la BD.
            con.commit(); // Consolidación final de la transacción
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
     * Sobrecarga para flujos simplificados que no derivan de una personalización previa.
     * 
     * @param userId Propietario del pedido.
     * @return ID del pedido.
     * @throws Exception Error JDBC.
     */
    public int crearPedido(int userId) throws Exception {
        return crearPedido(userId, -1);
    }

    /**
     * Formaliza el agendamiento de una cita en el sistema.
     * Cumple con el Requerimiento Funcional RF-08.
     * 
     * @param cita Objeto con la metadata de la cita (fecha, motivo, notas).
     * @return true si el registro fue exitoso.
     * @throws Exception Error de base de datos.
     */
    public boolean crearCita(Cita cita) throws Exception {
        // La dirección de entrega es estática según política del taller local
        String sql = "INSERT INTO citas (pedido_id, cita_fecha_hora, cita_estado, cita_notas, cita_motivo, cita_direccion_entrega) VALUES (?, ?, ?, ?, ?, 'Calle 9nb 2occ 04 - Barrio Bavaria 2')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getPedidoId());
            ps.setTimestamp(2, Timestamp.valueOf(cita.getCitaFechaHora()));
            ps.setString(3, "pendiente"); // Estado inicial controlado por el sistema
            ps.setString(4, cita.getCitaNotas()); 
            ps.setString(5, cita.getCitaMotivo());
            
            int filas = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    cita.setCitaId(rs.getInt(1)); // Recuperación del ID autonumérico
            }
            return filas > 0;
        } catch (SQLException e) {
            throw new Exception("Error al crear cita: " + e.getMessage());
        }
    }
    
    /**
     * Transiciona el estado operativo de una cita.
     * 
     * @param citaId      Identificador único.
     * @param nuevoEstado Categoría de destino (programada, confirmada, etc).
     * @return true si hubo afectación de filas.
     * @throws Exception Error SQL.
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
     * Recupera el historial de citas de un usuario permitiendo búsquedas por servicio.
     * 
     * @param userId         Propietario de la cuenta.
     * @param filtroServicio Fragmento del nombre del servicio (opcional).
     * @return Lista de modelos Cita con la metadata hidratada.
     * @throws Exception Error en JOINs de alta profundidad.
     */
    public List<Cita> obtenerCitasFiltradas(int userId, String filtroServicio) throws Exception {
        // Query de consolidación con 6 niveles de JOIN para llegar al nombre del servicio base
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
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas filtradas: " + e.getMessage());
        }
        return citas;
    }

    /**
     * Obtiene el historial cronológico de citas para un cliente.
     * 
     * @param userId Identificador del usuario.
     * @return Lista de instancias de Cita ordenadas por fecha descendente.
     * @throws Exception Error SQL.
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
                    c.setDireccionEntrega(rs.getString("cita_direccion_entrega")); 
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