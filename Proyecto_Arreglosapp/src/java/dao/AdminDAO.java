package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.*;

/**
 * Esta clase gestiona las operaciones administrativas del sistema.
 * Incluye estadísticas para el Dashboard, gestión avanzada de pedidos,
 * control de citas globales, y administración de usuarios y servicios.
 * 
 * @author Antigravity - Senior Architect
 */
public class AdminDAO {

    // ─── DASHBOARD: ESTADÍSTICAS EN TIEMPO REAL ───────────────────

    /**
     * Cuenta el total de pedidos que requieren atención (no cancelados).
     * @return Cantidad de pedidos activos.
     * @throws Exception Error SQL.
     */
    public int contarPedidosActivos() throws Exception {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE pedido_estado IN ('pendiente','confirmado','en_proceso','terminado')";
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

    /**
     * Cuenta cuántas citas están programadas para la fecha actual.
     */
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

    /**
     * Cuenta el histórico total de citas registradas.
     */
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

    /**
     * Recupera los últimos 20 pedidos realizados para mostrar en el Dashboard.
     * Incluye datos del cliente y de la cita asociada mediante JOINs.
     * 
     * @return Lista de pedidos recientes con detalles.
     * @throws Exception Error SQL.
     */
    public List<Map<String, Object>> obtenerPedidosRecientes() throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_total, p.pedido_fecha_creacion, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_fecha_hora, c.cita_estado " +
                "FROM pedidos p " +
                "LEFT JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON c.pedido_id = p.pedido_id " +
                "WHERE p.pedido_estado IN ('pendiente','confirmado','en_proceso','terminado') " +
                "ORDER BY p.pedido_fecha_creacion DESC LIMIT 20";

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
                pedido.put("total", rs.getDouble("pedido_total"));
                lista.add(pedido);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener pedidos recientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Filtra la lista de pedidos según criterios dinámicos.
     * RF-11: Control de Estados por Admin.
     * 
     * @param estadoPago (Obsoleto en BD actual, se mantiene por compatibilidad).
     * @param estadoPedido Estado deseado (pendiente, confirmado, etc).
     * @return Lista filtrada de pedidos.
     */
    public List<Map<String, Object>> obtenerPedidosFiltrados(String estadoPago, String estadoPedido) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT p.pedido_id, p.pedido_estado, p.pedido_total, p.pedido_fecha_creacion, " +
                        "u.user_nombre, u.user_email, " +
                        "c.cita_fecha_hora, c.cita_estado " +
                        "FROM pedidos p " +
                        "LEFT JOIN usuarios u ON p.usuario_id = u.user_id " +
                        "LEFT JOIN citas c ON p.pedido_id = c.pedido_id WHERE 1=1 ");

        if (estadoPedido != null && !estadoPedido.isEmpty()) {
            sql.append(" AND p.pedido_estado = ? ");
        } else {
            sql.append(" AND p.pedido_estado IN ('pendiente','confirmado','en_proceso','terminado') ");
        }
        sql.append(" ORDER BY p.pedido_fecha_creacion DESC");

        List<Map<String, Object>> pedidos = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (estadoPedido != null && !estadoPedido.isEmpty())
                ps.setString(1, estadoPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new LinkedHashMap<>();
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("cliente", rs.getString("user_nombre"));
                    p.put("email", rs.getString("user_email"));
                    p.put("estado", rs.getString("pedido_estado"));
                    p.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                    p.put("citaEstado", rs.getString("cita_estado"));
                    p.put("total", rs.getDouble("pedido_total"));
                    pedidos.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al filtrar pedidos: " + e.getMessage());
        }
        return pedidos;
    }

    /**
     * Obtiene todas las citas programadas para el día de hoy con datos del cliente.
     */
    public List<Map<String, Object>> obtenerCitasHoy() throws Exception {
        String sql = "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, " +
                "u.user_nombre, p.pedido_id " +
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
                cita.put("motivo", rs.getString("cita_motivo"));
                cita.put("cliente", rs.getString("user_nombre"));
                lista.add(cita);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas hoy: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Recupera el listado completo de citas agendadas en el sistema.
     */
    public List<Map<String, Object>> obtenerTodasLasCitas() throws Exception {
        String sql = "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, " +
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
                cita.put("motivo", rs.getString("cita_motivo"));
                cita.put("cliente", rs.getString("user_nombre"));
                lista.add(cita);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Filtra citas por fecha o nombre del cliente.
     */
    public List<Map<String, Object>> obtenerCitasFiltradas(String fecha, String clienteNombre) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, " +
                        "u.user_nombre, p.pedido_id " +
                        "FROM citas c " +
                        "JOIN pedidos p ON c.pedido_id = p.pedido_id " +
                        "JOIN usuarios u ON p.usuario_id = u.user_id WHERE 1=1 ");

        if (fecha != null && !fecha.isEmpty())
            sql.append(" AND DATE(c.cita_fecha_hora) = ? ");
        if (clienteNombre != null && !clienteNombre.isEmpty())
            sql.append(" AND u.user_nombre LIKE ? ");
        sql.append(" ORDER BY c.cita_fecha_hora ASC");

        List<Map<String, Object>> citas = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (fecha != null && !fecha.isEmpty())
                ps.setString(idx++, fecha);
            if (clienteNombre != null && !clienteNombre.isEmpty())
                ps.setString(idx++, "%" + clienteNombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> c = new LinkedHashMap<>();
                    c.put("citaId", rs.getInt("cita_id"));
                    c.put("fechaHora", rs.getTimestamp("cita_fecha_hora"));
                    c.put("cliente", rs.getString("user_nombre"));
                    c.put("estado", rs.getString("cita_estado"));
                    c.put("notas", rs.getString("cita_notas"));
                    c.put("motivo", rs.getString("cita_motivo"));
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al filtrar citas: " + e.getMessage());
        }
        return citas;
    }

    /**
     * Cambia el estado de una cita y actualiza el pedido vinculado.
     * RF-13: Dispara una notificación automática al cliente.
     * 
     * @param citaId ID de la cita.
     * @param nuevoEstado (confirmada, completada, cancelada).
     * @return true si el proceso transaccional fue exitoso.
     */
    public boolean cambiarEstadoCita(int citaId, String nuevoEstado) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Iniciar transacción

            // 1. Actualizar estado de la cita
            String sqlCita = "UPDATE citas SET cita_estado = ? WHERE cita_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCita)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, citaId);
                ps.executeUpdate();
            }

            // 2. Mapear estado de cita a estado de pedido
            String nuevoPedidoEstado = null;
            if ("confirmada".equals(nuevoEstado))
                nuevoPedidoEstado = "confirmado";
            else if ("completada".equals(nuevoEstado))
                nuevoPedidoEstado = "terminado";
            else if ("cancelada".equals(nuevoEstado))
                nuevoPedidoEstado = "cancelado";

            if (nuevoPedidoEstado != null) {
                String sqlPedido = "UPDATE pedidos SET pedido_estado = ?, pedido_fecha_actualizacion = CURRENT_TIMESTAMP "
                        +
                        "WHERE pedido_id = (SELECT pedido_id FROM citas WHERE cita_id = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setString(1, nuevoPedidoEstado);
                    ps.setInt(2, citaId);
                    ps.executeUpdate();
                }
            }

            // 3. Obtener el ID del usuario para enviar notificación (RF-13)
            String sqlGetUserId = "SELECT p.usuario_id FROM pedidos p JOIN citas c ON p.pedido_id = c.pedido_id WHERE c.cita_id = ?";
            int userId = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlGetUserId)) {
                ps.setInt(1, citaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        userId = rs.getInt("usuario_id");
                }
            }

            if (userId != -1) {
                String mensaje = "Tu cita ha sido marcada como: " + nuevoEstado;
                String sqlNotif = "INSERT INTO NOTIFICACIONES (user_id, mensaje) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlNotif)) {
                    ps.setInt(1, userId);
                    ps.setString(2, mensaje);
                    ps.executeUpdate();
                }
            }

            con.commit(); // Confirmar transacción
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

    /**
     * Actualiza manualmente el estado operacional de un pedido.
     * RF-11/RF-13: Notifica al cliente sobre el cambio de estado.
     */
    public boolean actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);

            // 1. Actualizar pedido
            String sql = "UPDATE pedidos SET pedido_estado = ?, pedido_fecha_actualizacion = CURRENT_TIMESTAMP WHERE pedido_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, pedidoId);
                ps.executeUpdate();
            }

            // 2. Notificación automática
            String sqlGetUserId = "SELECT usuario_id FROM pedidos WHERE pedido_id = ?";
            int userId = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlGetUserId)) {
                ps.setInt(1, pedidoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        userId = rs.getInt("usuario_id");
                }
            }

            if (userId != -1) {
                String mensaje = "El estado de tu pedido #" + pedidoId + " ha cambiado a: " + nuevoEstado;
                String sqlNotif = "INSERT INTO NOTIFICACIONES (user_id, mensaje) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlNotif)) {
                    ps.setInt(1, userId);
                    ps.setString(2, mensaje);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null)
                con.rollback();
            throw new Exception("Error al actualizar estado: " + e.getMessage());
        } finally {
            if (con != null)
                con.close();
        }
    }

    /**
     * Recupera el detalle completo de un pedido específico para la vista del Administrador.
     */
    public Map<String, Object> obtenerDetallePedido(int pedidoId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, " +
                "u.user_nombre, u.user_email, u.user_ubicacion_direccion, " +
                "c.cita_fecha_hora, c.cita_notas, c.cita_estado, c.cita_motivo " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON c.pedido_id = p.pedido_id " +
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
                    detalle.put("total", rs.getDouble("pedido_total"));
                    detalle.put("cliente", rs.getString("user_nombre"));
                    detalle.put("email", rs.getString("user_email"));
                    detalle.put("direccion", rs.getString("user_ubicacion_direccion"));
                    detalle.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                    detalle.put("citaNotas", rs.getString("cita_notas"));
                    detalle.put("citaEstado", rs.getString("cita_estado"));
                    detalle.put("citaMotivo", rs.getString("cita_motivo"));
                    return detalle;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener detalle: " + e.getMessage());
        }
        return null;
    }

    // ─── GESTIÓN DE USUARIOS ──────────────────────────────────────

    /**
     * Obtiene el listado de todos los clientes registrados (rol_id = 2).
     * @param busqueda Filtro opcional por nombre o email.
     */
    public List<Map<String, Object>> obtenerUsuarios(String busqueda) throws Exception {
        String sql = "SELECT u.user_id, u.user_nombre, u.user_email, u.rol_id, t.telefono_numero " +
                "FROM usuarios u " +
                "LEFT JOIN telefonos t ON t.user_id = u.user_id AND t.telefono_es_principal = true " +
                "WHERE u.rol_id = 2 ";
        if (busqueda != null && !busqueda.trim().isEmpty())
            sql += "AND (u.user_nombre LIKE ? OR u.user_email LIKE ?) ";
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

    /**
     * Elimina físicamente a un usuario y todos sus datos relacionados (Favoritos, Telefonos, Pedidos).
     * RF-12: Gestión Admin.
     */
    public boolean eliminarUsuario(int userId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);
            // Cascada de borrado manual por integridad referencial
            String[] sqls = {
                    "DELETE FROM favoritos WHERE user_id = ?",
                    "DELETE FROM telefonos WHERE user_id = ?",
                    "DELETE FROM citas WHERE pedido_id IN (SELECT pedido_id FROM pedidos WHERE usuario_id = ?)",
                    "DELETE FROM personalizaciones WHERE user_id = ?",
                    "DELETE FROM pedidos WHERE usuario_id = ?",
                    "DELETE FROM usuarios WHERE user_id = ? AND rol_id = 2"
            };
            for (String sql : sqls) {
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

    // ─── GESTIÓN DE SERVICIOS ─────────────────────────────────────

    /**
     * Obtiene el catálogo de servicios disponibles para administración.
     */
    public List<Map<String, Object>> obtenerServicios() throws Exception {
        String sql = "SELECT arreglo_id, arreglo_nombre, arreglo_descripcion, arreglo_precio_base, arreglo_imagen_url "
                +
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

    /**
     * Realiza un borrado lógico de un servicio (lo marca como no disponible).
     * RF-10: Gestión Admin (CRUD).
     */
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

    // ─── MÉTODOS DE COMPATIBILIDAD (STUBS) ────────────────────────

    /**
     * Stub para actualizar el pago (Mapeado al estado general del pedido).
     */
    public boolean actualizarPagoPedido(int pedidoId, String estado) throws Exception {
        return actualizarEstadoPedido(pedidoId, "terminado");
    }

    /**
     * Stub para entrega de pedidos (Mapeado al estado general del pedido).
     */
    public boolean actualizarEntregaPedido(int pedidoId, String estado) throws Exception {
        return actualizarEstadoPedido(pedidoId, "terminado");
    }

    /**
     * Simulación de registro de abono.
     */
    public boolean registrarAbono(int pedidoId, double monto) throws Exception {
        return true;
    }

    /**
     * Registra la asistencia a una cita mediante la concatenación en las notas de la cita.
     * Añade información sobre la asistencia al campo `cita_notas`.
     * RF-13: Notificaciones (podría disparar una notificación de asistencia registrada).
     *
     * @param citaId El ID de la cita a actualizar.
     * @param asistencia El estado de asistencia (ej. "Asistió", "No Asistió").
     * @return true si la asistencia fue registrada exitosamente, false en caso contrario.
     * @throws Exception Si ocurre un error durante la actualización en la base de datos.
     */
    public boolean actualizarAsistenciaCita(int citaId, String asistencia) throws Exception {
        // Consulta SQL para concatenar la información de asistencia a las notas existentes
        String sql = "UPDATE citas SET cita_notas = CONCAT(IFNULL(cita_notas,''), ' | Asistencia: " + asistencia
                + "') WHERE cita_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, citaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al registrar asistencia: " + e.getMessage());
        }
    }
}