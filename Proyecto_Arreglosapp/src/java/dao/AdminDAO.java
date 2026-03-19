package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.*;

/**
 * Capa de Acceso a Datos para operaciones administrativas del sistema.
 *
 * Responsabilidades:
 * - Estadísticas del Dashboard (pedidos activos, citas de hoy).
 * - Gestión avanzada de pedidos (obtener, filtrar, actualizar
 * estado/pago/entrega, abonos).
 * - Control global de citas (obtener, filtrar, cambiar estado, registrar
 * asistencia).
 * - Administración de usuarios y servicios.
 *
 * Todos los métodos propagan excepciones verificadas para que el Servlet o la
 * JSP
 * puedan manejar el error de forma adecuada.
 *
 * @author JAIDER - ESPAR
 */
public class AdminDAO {

    // ─────────────────────────────────────────────────────────────────────────
    // SECCIÓN 1: ESTADÍSTICAS PARA EL DASHBOARD
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cuenta el total de pedidos activos (excluye cancelados).
     * Se usa en el widget de resumen del Dashboard administrativo.
     *
     * @return Número de pedidos en estados: pendiente, confirmado, en_proceso o
     *         terminado.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public int contarPedidosActivos() throws Exception {
        String sql = "SELECT COUNT(*) FROM pedidos "
                + "WHERE pedido_estado IN ('pendiente','confirmado','en_proceso','terminado')";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            throw new Exception("Error al contar pedidos activos: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Cuenta las citas agendadas para el día de hoy con estado activo.
     * Se usa en el widget de resumen del Dashboard.
     *
     * @return Número de citas de hoy en estado 'programada' o 'confirmada'.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public int contarCitasHoy() throws Exception {
        String sql = "SELECT COUNT(*) FROM citas "
                + "WHERE DATE(cita_fecha_hora) = CURDATE() "
                + "AND cita_estado IN ('programada','confirmada')";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            throw new Exception("Error al contar citas de hoy: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Cuenta el total histórico de citas registradas en el sistema.
     *
     * @return Total de citas en la tabla, sin importar su estado.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public int contarTodasLasCitas() throws Exception {
        String sql = "SELECT COUNT(*) FROM citas";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            throw new Exception("Error al contar todas las citas: " + e.getMessage());
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECCIÓN 2: GESTIÓN DE PEDIDOS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Obtiene los últimos 20 pedidos activos para mostrar en el Dashboard.
     * Incluye nombre del cliente, su cita asociada (si existe) y el total del
     * pedido.
     *
     * @return Lista de mapas con los datos de cada pedido reciente.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public List<Map<String, Object>> obtenerPedidosRecientes() throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_total, p.pedido_fecha_creacion, "
                + "u.user_nombre, u.user_email, "
                + "c.cita_fecha_hora, c.cita_estado "
                + "FROM pedidos p "
                + "LEFT JOIN usuarios u ON p.usuario_id = u.user_id "
                + "LEFT JOIN citas c ON c.pedido_id = p.pedido_id "
                + "WHERE p.pedido_estado IN ('pendiente','confirmado','en_proceso','terminado') "
                + "ORDER BY p.pedido_fecha_creacion DESC LIMIT 20";

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
     * Obtiene el detalle completo de un pedido específico para la vista
     * administrativa.
     *
     * CORRECCIÓN (Bug resuelto): Se agrega un LEFT JOIN con la tabla
     * PERSONALIZACIONES
     * para incluir los datos de personalización del cliente (categoría,
     * descripción,
     * material e imagen de referencia). Anteriormente estos datos nunca se
     * consultaban
     * y la sección "Detalles de la Personalización" en la vista del administrador
     * siempre aparecía vacía/oculta.
     *
     * La personalización se vincula al pedido mediante el usuario_id del pedido,
     * tomando la personalización más reciente de ese usuario cuando coincide
     * la categoría con alguno de los arreglos del pedido.
     * Se usa un LEFT JOIN para que pedidos sin personalización igualmente
     * muestren toda la información disponible.
     *
     * @param pedidoId ID del pedido a consultar.
     * @return Mapa con todos los datos del pedido, la cita y la personalización
     *         asociada.
     *         Retorna null si el pedido no existe.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public Map<String, Object> obtenerDetallePedido(int pedidoId) throws Exception {
        String sql = "SELECT "
                + "    p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, p.pedido_total, "
                + "    u.user_nombre, u.user_email, u.user_ubicacion_direccion, "
                + "    c.cita_id, c.cita_fecha_hora, c.cita_notas, c.cita_estado, c.cita_motivo, "
                + "    per.personalizacion_id, cp.categoria_nombre AS per_categoria, "
                + "    per.descripcion AS per_descripcion, per.material_tela AS per_material, "
                + "    per.imagen_referencia AS per_imagen "
                + "FROM pedidos p "
                + "LEFT JOIN usuarios u ON p.usuario_id = u.user_id "
                + "LEFT JOIN citas c ON c.pedido_id = p.pedido_id "
                + "LEFT JOIN personalizaciones per ON per.pedido_id = p.pedido_id "
                + "LEFT JOIN categorias cp ON per.categoria_id = cp.categoria_id "
                + "WHERE p.pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> detalle = new LinkedHashMap<>();

                    // ── Pedido ──
                    detalle.put("pedidoId", rs.getInt("pedido_id"));
                    detalle.put("estado", rs.getString("pedido_estado"));
                    detalle.put("fecha", rs.getTimestamp("pedido_fecha_creacion"));
                    detalle.put("total", rs.getDouble("pedido_total"));

                    // ── Cliente ──
                    detalle.put("cliente", rs.getString("user_nombre"));
                    detalle.put("email", rs.getString("user_email"));
                    detalle.put("direccion", rs.getString("user_ubicacion_direccion"));

                    // ── Cita (puede ser null si no hay cita agendada) ──
                    detalle.put("citaId", rs.getObject("cita_id")); // getObject para soportar NULL
                    detalle.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                    detalle.put("citaNotas", rs.getString("cita_notas"));
                    detalle.put("citaEstado", rs.getString("cita_estado"));
                    detalle.put("citaMotivo", rs.getString("cita_motivo"));

                    // ── Personalización (NUEVO — antes nunca se incluía) ──
                    // Estos valores llegan como null si el pedido no tiene personalización.
                    // La vista detalle-pedido-admin.jsp ya evalúa null antes de mostrar la sección.
                    detalle.put("personalizacionId", rs.getObject("personalizacion_id"));
                    detalle.put("personalizacionCategoria", rs.getString("per_categoria"));
                    detalle.put("personalizacionDescripcion", rs.getString("per_descripcion"));
                    detalle.put("personalizacionMaterial", rs.getString("per_material"));
                    detalle.put("personalizacionImagen", rs.getString("per_imagen"));

                    return detalle;
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al obtener detalle del pedido: " + e.getMessage());
        }

        return null; // El pedido no existe
    }

    /**
     * Lista y filtra pedidos según su estado.
     * Se usa en el Dashboard cuando el administrador aplica filtros de estado.
     *
     * @param estadoPago   Parámetro mantenido por compatibilidad (actualmente no
     *                     usado en BD).
     * @param estadoPedido Estado de filtro (pendiente, confirmado, en_proceso,
     *                     terminado).
     *                     Si es null o vacío, devuelve todos los pedidos activos.
     * @return Lista de pedidos que cumplen el filtro.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public List<Map<String, Object>> obtenerPedidosFiltrados(String estadoPago, String estadoPedido) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT p.pedido_id, p.pedido_estado, p.pedido_total, p.pedido_fecha_creacion, "
                        + "u.user_nombre, u.user_email, "
                        + "c.cita_fecha_hora, c.cita_estado "
                        + "FROM pedidos p "
                        + "LEFT JOIN usuarios u ON p.usuario_id = u.user_id "
                        + "LEFT JOIN citas c ON p.pedido_id = c.pedido_id "
                        + "WHERE 1=1 ");

        // Filtro dinámico: si se especifica un estado, se filtra por él; si no, se
        // traen todos los activos
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
     * Actualiza el estado del ciclo de vida de un pedido.
     * RF-11: Control de Estados por Administrador.
     *
     * @param pedidoId    ID del pedido a actualizar.
     * @param nuevoEstado Nuevo estado (pendiente, confirmado, en_proceso,
     *                    terminado, cancelado).
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Transacción para Estado y Notificación

            // 1. Actualizar el estado del pedido
            String sqlUpdate = "UPDATE pedidos SET pedido_estado = ? WHERE pedido_id = ?";
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, nuevoEstado);
                psUpdate.setInt(2, pedidoId);
                psUpdate.executeUpdate();
            }

            // 2. Obtener el usuario_id del pedido para notificar
            int usuarioId = -1;
            String sqlSelect = "SELECT usuario_id FROM pedidos WHERE pedido_id = ?";
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, pedidoId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        usuarioId = rs.getInt("usuario_id");
                    }
                }
            }

            // 3. Insertar notificación para el cliente
            if (usuarioId != -1) {
                String mensaje = "Tu pedido #" + pedidoId + " ha cambiado de estado a '" + nuevoEstado + "'.";
                if ("terminado".equalsIgnoreCase(nuevoEstado)) {
                    mensaje = "Tu pedido #" + pedidoId + " ha sido finalizado. ¡Ya puedes recoger tu prenda!";
                } else if ("en_proceso".equalsIgnoreCase(nuevoEstado)) {
                    mensaje = "Tu pedido #" + pedidoId + " ya está en el taller siendo confeccionado.";
                }

                String sqlNotif = "INSERT INTO notificaciones (user_id, mensaje) VALUES (?, ?)";
                try (PreparedStatement psNotif = con.prepareStatement(sqlNotif)) {
                    psNotif.setInt(1, usuarioId);
                    psNotif.setString(2, mensaje);
                    psNotif.executeUpdate();
                }
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new Exception("Error al actualizar estado del pedido: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Registra el estado de pago de un pedido (pagado/pendiente).
     * RF-05: Gestión de Pagos.
     *
     * @param pedidoId   ID del pedido.
     * @param estadoPago Nuevo estado de pago ('pagado' o 'pendiente').
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void actualizarPagoPedido(int pedidoId, String estadoPago) throws Exception {
        // Nota: si la BD tiene un campo separado para estado_pago, actualizar el nombre
        // aquí.
        // Por compatibilidad con el modelo actual se actualiza el estado global del
        // pedido.
        String sql = "UPDATE pedidos SET pedido_estado = ? WHERE pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estadoPago);
            ps.setInt(2, pedidoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al actualizar pago del pedido: " + e.getMessage());
        }
    }

    /**
     * Registra la entrega física de la prenda al cliente.
     * RF-05: Gestión de Entregas.
     *
     * @param pedidoId      ID del pedido.
     * @param estadoEntrega Nuevo estado de entrega ('entregado').
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void actualizarEntregaPedido(int pedidoId, String estadoEntrega) throws Exception {
        // Actualiza el estado general del pedido al estado de entrega indicado
        String sql = "UPDATE pedidos SET pedido_estado = ? WHERE pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estadoEntrega);
            ps.setInt(2, pedidoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al actualizar entrega del pedido: " + e.getMessage());
        }
    }

    /**
     * Registra un abono (pago parcial) sobre el total de un pedido.
     * RF-05: Gestión de Abonos.
     *
     * @param pedidoId ID del pedido al que se aplica el abono.
     * @param monto    Valor del abono a descontar del total.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void registrarAbono(int pedidoId, double monto) throws Exception {
        // Descuenta el abono del total pendiente del pedido
        String sql = "UPDATE pedidos SET pedido_total = GREATEST(0, pedido_total - ?) WHERE pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, monto);
            ps.setInt(2, pedidoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al registrar abono: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECCIÓN 3: GESTIÓN DE CITAS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Obtiene todas las citas programadas para el día de hoy.
     * Se muestra en la sección de citas del Dashboard.
     *
     * @return Lista de citas de hoy con datos del cliente y pedido.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public List<Map<String, Object>> obtenerCitasHoy() throws Exception {
        String sql = "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, "
                + "u.user_nombre, p.pedido_id "
                + "FROM citas c "
                + "JOIN pedidos p ON p.pedido_id = c.pedido_id "
                + "JOIN usuarios u ON u.user_id = p.usuario_id "
                + "WHERE DATE(c.cita_fecha_hora) = CURDATE() "
                + "ORDER BY c.cita_fecha_hora ASC";

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
            throw new Exception("Error al obtener citas de hoy: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Recupera el listado completo de todas las citas registradas en el sistema,
     * ordenadas de la más reciente a la más antigua.
     *
     * CORRECCIÓN (Bug resuelto): Anteriormente este método estaba duplicado en el
     * archivo con errores de llaves que impedían la compilación. Se eliminó la
     * copia
     * incorrecta y se dejó solo esta versión funcional.
     *
     * @return Lista de todas las citas con datos del cliente y pedido.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public List<Map<String, Object>> obtenerTodasLasCitas() throws Exception {
        String sql = "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, "
                + "u.user_nombre, p.pedido_id "
                + "FROM citas c "
                + "JOIN pedidos p ON p.pedido_id = c.pedido_id "
                + "JOIN usuarios u ON u.user_id = p.usuario_id "
                + "ORDER BY c.cita_fecha_hora DESC";

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
            throw new Exception("Error al obtener todas las citas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Filtra las citas por fecha específica y/o nombre del cliente.
     * Ambos parámetros son opcionales; si son null o vacíos se omiten del filtro.
     *
     * @param fecha         Fecha en formato 'YYYY-MM-DD' (puede ser null).
     * @param clienteNombre Nombre parcial del cliente para búsqueda LIKE (puede ser
     *                      null).
     * @return Lista de citas que cumplen los criterios de filtro.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public List<Map<String, Object>> obtenerCitasFiltradas(String fecha, String clienteNombre) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, "
                        + "u.user_nombre, p.pedido_id "
                        + "FROM citas c "
                        + "JOIN pedidos p ON c.pedido_id = p.pedido_id "
                        + "JOIN usuarios u ON p.usuario_id = u.user_id "
                        + "WHERE 1=1 ");

        // Filtros dinámicos opcionales
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
     * Cambia el estado de una cita (programada → confirmada → completada /
     * cancelada).
     * RF-08: Gestión del ciclo de vida de citas.
     *
     * @param citaId      ID de la cita a actualizar.
     * @param nuevoEstado Nuevo estado de la cita.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void cambiarEstadoCita(int citaId, String nuevoEstado) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Iniciar transacción

            // 1. Actualizar el estado de la cita
            String sqlCita = "UPDATE citas SET cita_estado = ? WHERE cita_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCita)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, citaId);
                ps.executeUpdate();
            }

            // 2. Determinar el mapeo de estados
            String estadoPedido = null;
            if ("pendiente".equalsIgnoreCase(nuevoEstado) || "programada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "pendiente"; // 'Pendiente de Revisión' visualmente
            } else if ("confirmada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "confirmado"; // 'Pendiente de Inicio' visualmente
            } else if ("completada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "terminado"; // 'Terminado' visualmente
            } else if ("cancelada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "cancelado"; 
            }

            // 3. Sincronizar el pedido asociado si hay mapeo correspondiente
            if (estadoPedido != null) {
                String sqlPedido = "UPDATE pedidos p JOIN citas c ON p.pedido_id = c.pedido_id "
                        + "SET p.pedido_estado = ? WHERE c.cita_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setString(1, estadoPedido);
                    ps.setInt(2, citaId);
                    ps.executeUpdate();
                }
            }

            // 4. Obtener usuario_id y pedido_id para notificar
            int usuarioId = -1;
            int pedidoId = -1;
            String sqlSelectUser = "SELECT p.usuario_id, p.pedido_id FROM citas c JOIN pedidos p ON c.pedido_id = p.pedido_id WHERE c.cita_id = ?";
            try (PreparedStatement psSel = con.prepareStatement(sqlSelectUser)) {
                psSel.setInt(1, citaId);
                try (ResultSet rs = psSel.executeQuery()) {
                    if (rs.next()) {
                        usuarioId = rs.getInt("usuario_id");
                        pedidoId = rs.getInt("pedido_id");
                    }
                }
            }

            // 5. Insertar notificación para el cliente
            if (usuarioId != -1) {
                String mensaje = "Tu cita para el pedido #" + pedidoId + " ahora está '" + nuevoEstado + "'.";
                if ("confirmada".equalsIgnoreCase(nuevoEstado)) {
                    mensaje = "¡Buenas noticias! Tu cita para el pedido #" + pedidoId + " ha sido confirmada.";
                } else if ("completada".equalsIgnoreCase(nuevoEstado)) {
                    mensaje = "Tu cita para el pedido #" + pedidoId + " se ha completado. El pedido ahora está finalizado.";
                } else if ("cancelada".equalsIgnoreCase(nuevoEstado)) {
                    mensaje = "Tu cita para el pedido #" + pedidoId + " ha sido lamentablemente cancelada.";
                }

                String sqlNotif = "INSERT INTO notificaciones (user_id, mensaje) VALUES (?, ?)";
                try (PreparedStatement psNotif = con.prepareStatement(sqlNotif)) {
                    psNotif.setInt(1, usuarioId);
                    psNotif.setString(2, mensaje);
                    psNotif.executeUpdate();
                }
            }

            con.commit(); // Operaciones exitosas

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new Exception("Error al sincronizar estado de cita y pedido: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Registra si el cliente asistió o no a la cita agendada.
     * Útil para control de historial y estadísticas de asistencia.
     *
     * @param citaId     ID de la cita.
     * @param asistencia Valor de asistencia ('si' o 'no').
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void actualizarAsistenciaCita(int citaId, String asistencia) throws Exception {
        // Las notas de la cita se usan para registrar la asistencia como texto
        String sql = "UPDATE citas SET cita_notas = CONCAT(IFNULL(cita_notas,''), ' | Asistencia: ', ?) WHERE cita_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, asistencia);
            ps.setInt(2, citaId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al actualizar asistencia: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECCIÓN 4: GESTIÓN DE USUARIOS Y SERVICIOS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Elimina un usuario del sistema (borrado físico).
     * RF-10: Gestión de Usuarios por Administrador.
     * PRECAUCIÓN: Esta operación es irreversible y puede fallar si el usuario
     * tiene pedidos activos (por restricciones de FK en la BD).
     *
     * @param userId ID del usuario a eliminar.
     * @throws Exception Si ocurre un error de conexión, SQL o restricción de FK.
     */
    public void eliminarUsuario(int userId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Transacción para validación y borrado

            // 1. Validar PEDIDOS ACTIVOS (Que NO sean 'terminado' ni 'cancelado')
            String sqlPedidos = "SELECT COUNT(*) FROM pedidos WHERE usuario_id = ? AND pedido_estado NOT IN ('terminado', 'cancelado')";
            try (PreparedStatement ps = con.prepareStatement(sqlPedidos)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("El usuario no puede ser eliminado porque tiene pedidos activos en curso.");
                    }
                }
            }

            // 2. Validar CITAS ACTIVAS (Vía JOIN con pedidos, que la cita NO sea 'completada' ni 'cancelada')
            String sqlCitas = "SELECT COUNT(*) FROM citas c JOIN pedidos p ON c.pedido_id = p.pedido_id "
                    + "WHERE p.usuario_id = ? AND c.cita_estado NOT IN ('completada', 'cancelada')";
            try (PreparedStatement ps = con.prepareStatement(sqlCitas)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("El usuario no puede ser eliminado porque tiene citas activas pendientes.");
                    }
                }
            }

            // 3. Validar PERSONALIZACIONES ACTIVAS (Que NO sean 'completado' ni 'cancelado')
            String sqlPer = "SELECT COUNT(*) FROM personalizaciones WHERE user_id = ? AND estado NOT IN ('completado', 'cancelado')";
            try (PreparedStatement ps = con.prepareStatement(sqlPer)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Exception("El usuario no puede ser eliminado porque tiene solicitudes de personalización activas.");
                    }
                }
            }

            // Si llegamos aquí, el usuario tiene 0 pendientes activos.
            // 4. Comprobar si tiene registros en el Historial para decidir entre Borrado Físico o Lógico
            boolean tieneHistorial = false;
            String checkHistorial = "SELECT COUNT(*) FROM pedidos WHERE usuario_id = ?";
            try (PreparedStatement psH = con.prepareStatement(checkHistorial)) {
                psH.setInt(1, userId);
                try (ResultSet rsH = psH.executeQuery()) {
                    if (rsH.next() && rsH.getInt(1) > 0) {
                        tieneHistorial = true;
                    }
                }
            }

            // 5. Proceder a la eliminación
            if (tieneHistorial) {
                // BORRADO LÓGICO: Anonimizar la cuenta para conservar la integridad referencial del historial
                String sqlLogico = "UPDATE usuarios SET user_email = CONCAT('deleted_', user_id, '@arreglosapp.local'), "
                        + "user_password_hash = 'ELIMINADO', user_nombre = 'Usuario Eliminado', "
                        + "user_ubicacion_direccion = 'N/A' WHERE user_id = ?";
                try (PreparedStatement psLog = con.prepareStatement(sqlLogico)) {
                    psLog.setInt(1, userId);
                    psLog.executeUpdate();
                }
            } else {
                // BORRADO FÍSICO: Si no hay historial, se puede borrar de la BD con seguridad
                String sqlFisico = "DELETE FROM usuarios WHERE user_id = ?";
                try (PreparedStatement psFis = con.prepareStatement(sqlFisico)) {
                    psFis.setInt(1, userId);
                    psFis.executeUpdate();
                }
            }

            con.commit();
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e; // Propagar la excepción original (o la de validación)
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Elimina un servicio/arreglo del catálogo (borrado físico).
     * RF-12: Gestión de Servicios por Administrador.
     *
     * @param servicioId ID del servicio/arreglo a eliminar.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public void eliminarServicio(int servicioId) throws Exception {
        String sql = "DELETE FROM arreglos WHERE arreglo_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, servicioId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error al eliminar servicio: " + e.getMessage());
        }
    }

    /**
     * Obtiene la lista de usuarios clientes, opcionalmente filtrada por búsqueda.
     * RF-10: Gestión de Usuarios por Administrador.
     *
     * @param busqueda Texto a buscar por nombre o correo (puede ser null).
     * @return Lista de mapas con los datos de cada usuario.
     * @throws Exception Si ocurre un error de conexión o SQL.
     */
    public List<Map<String, Object>> obtenerUsuarios(String busqueda) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.user_nombre, u.user_email, t.telefono_numero "
                        + "FROM usuarios u "
                        + "LEFT JOIN telefonos t ON u.user_id = t.user_id AND t.telefono_es_principal = true "
                        + "WHERE u.rol_id = 2 ");

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append("AND (u.user_nombre LIKE ? OR u.user_email LIKE ?) ");
        }
        sql.append("ORDER BY u.user_nombre ASC");

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                String filtro = "%" + busqueda.trim() + "%";
                ps.setString(1, filtro);
                ps.setString(2, filtro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> u = new HashMap<>();
                    u.put("userId", rs.getInt("user_id"));
                    u.put("nombre", rs.getString("user_nombre"));
                    u.put("email", rs.getString("user_email"));
                    u.put("telefono", rs.getString("telefono_numero"));
                    lista.add(u);
                }
            }

        } catch (SQLException e) {
            throw new Exception("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }
}