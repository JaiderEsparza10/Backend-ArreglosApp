package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.*;

/**
 * Versión corregida de AdminDAO con métodos funcionales
 */
public class AdminDAO {

    /**
     * Cambia el estado de una cita y sincroniza el estado del pedido asociado.
     * 
     * @param citaId ID de la cita a actualizar
     * @param nuevoEstado Nuevo estado de la cita
     * @throws Exception Si ocurre un error
     */
    public int cambiarEstadoCita(int citaId, String nuevoEstado) throws Exception {
        Connection con = null;
        int usuarioId = -1;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);

            // 0. Obtener ID del usuario
            String sqlUser = "SELECT p.usuario_id FROM PEDIDOS p JOIN CITAS c ON p.pedido_id = c.pedido_id WHERE c.cita_id = ?";
            try (PreparedStatement psU = con.prepareStatement(sqlUser)) {
                psU.setInt(1, citaId);
                try (ResultSet rs = psU.executeQuery()) {
                    if (rs.next()) usuarioId = rs.getInt("usuario_id");
                }
            }

            // 1. Actualizar estado de la cita
            String sqlCita = "UPDATE CITAS SET cita_estado = ? WHERE cita_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCita)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, citaId);
                ps.executeUpdate();
            }

            // 2. Determinar estado del pedido según estado de la cita
            String estadoPedido = null;
            if ("pendiente".equalsIgnoreCase(nuevoEstado) || "programada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "pendiente";
            } else if ("confirmada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "confirmado";
            } else if ("completada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "en_proceso";
            } else if ("cancelada".equalsIgnoreCase(nuevoEstado)) {
                estadoPedido = "cancelado";
            }

            // 3. Actualizar estado del pedido asociado
            if (estadoPedido != null) {
                String sqlPedido = "UPDATE PEDIDOS SET pedido_estado = ? WHERE pedido_id IN (SELECT pedido_id FROM CITAS WHERE cita_id = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setString(1, estadoPedido);
                    ps.setInt(2, citaId);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return usuarioId;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new Exception("Error al cambiar estado de cita: " + e.getMessage());
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
     * Lista y filtra pedidos según su estado y búsqueda.
     */
    public List<Map<String, Object>> obtenerPedidosFiltrados(String search, String estadoPedido) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT p.pedido_id, p.pedido_estado, p.pedido_total, p.pedido_fecha, "
                        + "u.user_nombre, u.user_email, "
                        + "c.cita_fecha_hora, c.cita_estado "
                        + "FROM PEDIDOS p "
                        + "INNER JOIN USUARIOS u ON p.usuario_id = u.user_id "
                        + "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id "
                        + "WHERE 1=1 ");

        if (estadoPedido != null && !estadoPedido.isEmpty()) {
            sql.append(" AND p.pedido_estado = ? ");
        } else {
            sql.append(" AND p.pedido_estado IN ('pendiente','confirmado','en_proceso','terminado') ");
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (u.user_nombre LIKE ? OR p.pedido_id LIKE ?) ");
        }

        sql.append(" ORDER BY p.pedido_fecha DESC");

        List<Map<String, Object>> pedidos = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIdx = 1;
            if (estadoPedido != null && !estadoPedido.isEmpty()) {
                ps.setString(paramIdx++, estadoPedido);
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIdx++, searchPattern);
                ps.setString(paramIdx++, searchPattern);
            }

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
            throw new Exception("Error al obtener pedidos filtrados: " + e.getMessage());
        }
        return pedidos;
    }

    /**
     * Actualiza el estado de un pedido y retorna el usuarioId asociado.
     */
    public int actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws Exception {
        String sql = "UPDATE PEDIDOS SET pedido_estado = ? WHERE pedido_id = ?";
        String sqlUser = "SELECT usuario_id FROM PEDIDOS WHERE pedido_id = ?";
        int usuarioId = -1;

        try (Connection con = ConectionDB.getConexion()) {
            try (PreparedStatement psU = con.prepareStatement(sqlUser)) {
                psU.setInt(1, pedidoId);
                try (ResultSet rs = psU.executeQuery()) {
                    if (rs.next()) usuarioId = rs.getInt("usuario_id");
                }
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, pedidoId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar estado del pedido: " + e.getMessage());
        }
        return usuarioId;
    }




    // ─────────────────────────────────────────────────────────────────────────
    // SECCIÓN 3: GESTIÓN DE CITAS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Obtiene el detalle completo de un pedido, incluyendo cliente, cita y personalización.
     */
    public Map<String, Object> obtenerDetallePedido(int pedidoId) throws Exception {
        String sql = "SELECT p.pedido_estado, p.pedido_total, " +
                     "u.user_nombre, u.user_email, u.user_ubicacion_direccion, " +
                     "c.cita_fecha_hora, c.cita_estado, c.cita_motivo, c.cita_notas, " +
                     "per.descripcion, per.material_tela, per.imagen_referencia, " +
                     "s.servicio_nombre " +
                     "FROM PEDIDOS p " +
                     "INNER JOIN USUARIOS u ON p.usuario_id = u.user_id " +
                     "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                     "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                     "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                     "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                     "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                     "WHERE p.pedido_id = ?";

        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> d = new LinkedHashMap<>();
                    d.put("estado", rs.getString("pedido_estado"));
                    d.put("total", rs.getDouble("pedido_total"));
                    d.put("cliente", rs.getString("user_nombre"));
                    d.put("email", rs.getString("user_email"));
                    d.put("direccion", rs.getString("user_ubicacion_direccion"));
                    d.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                    d.put("citaEstado", rs.getString("cita_estado"));
                    d.put("citaMotivo", rs.getString("cita_motivo"));
                    d.put("citaNotas", rs.getString("cita_notas"));
                    d.put("personalizacionDescripcion", rs.getString("descripcion"));
                    d.put("personalizacionMaterial", rs.getString("material_tela"));
                    d.put("personalizacionServicio", rs.getString("servicio_nombre"));
                    d.put("personalizacionImagen", rs.getString("imagen_referencia"));
                    return d;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener detalle del pedido: " + e.getMessage());
        }
        return null;
    }

    /**
     * Registra si el cliente asistió o no a la cita agendada.
     */
    public void actualizarAsistenciaCita(int citaId, String asistencia) throws Exception {
        String sql = "UPDATE CITAS SET cita_notas = CONCAT(IFNULL(cita_notas,''), ' | Asistencia: ', ?) WHERE cita_id = ?";

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
     */
    public void eliminarUsuario(int userId) throws Exception {
        String sql = "DELETE FROM USUARIOS WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    /**
     * Elimina un servicio/arreglo del catálogo (borrado físico).
     */
    public void eliminarServicio(int servicioId) throws Exception {
        String sql = "DELETE FROM ARREGLOS WHERE arreglo_id = ?";
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
     */
    public List<Map<String, Object>> obtenerUsuarios(String busqueda) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.user_nombre, u.user_email, t.telefono_numero "
                        + "FROM USUARIOS u "
                        + "LEFT JOIN TELEFONOS t ON u.user_id = t.user_id AND t.telefono_es_principal = true "
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

    /**
     * Cuenta el número de pedidos en estados activos (pendiente, confirmado, en taller).
     */
    public int contarPedidosActivos() throws Exception {
        String sql = "SELECT COUNT(*) FROM PEDIDOS WHERE pedido_estado IN ('pendiente','confirmado','en_proceso')";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new Exception("Error al contar pedidos activos: " + e.getMessage());
        }
    }

    /**
     * Cuenta el número de citas programadas para el día de hoy.
     */
    public int contarCitasHoy() throws Exception {
        String sql = "SELECT COUNT(*) FROM CITAS WHERE DATE(cita_fecha_hora) = CURDATE() AND cita_estado != 'cancelada'";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new Exception("Error al contar citas de hoy: " + e.getMessage());
        }
    }

    /**
     * Cuenta el total histórico de citas registradas.
     */
    public int contarTodasLasCitas() throws Exception {
        String sql = "SELECT COUNT(*) FROM CITAS";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new Exception("Error al contar todas las citas: " + e.getMessage());
        }
    }

    /**
     * Obtiene una lista de citas filtradas por fecha o nombre del cliente.
     */
    public List<Map<String, Object>> obtenerCitasFiltradas(String fFecha, String fCliente) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT c.cita_id, u.user_nombre AS cliente, c.cita_estado AS estado, c.cita_notas AS notas, c.cita_fecha_hora AS fechaHora, c.cita_motivo AS motivo " +
            "FROM CITAS c " +
            "JOIN PEDIDOS p ON c.pedido_id = p.pedido_id " +
            "JOIN USUARIOS u ON p.usuario_id = u.user_id " +
            "WHERE 1=1 "
        );

        if (fFecha != null && !fFecha.isEmpty()) sql.append(" AND DATE(c.cita_fecha_hora) = ? ");
        if (fCliente != null && !fCliente.trim().isEmpty()) sql.append(" AND u.user_nombre LIKE ? ");

        sql.append(" ORDER BY c.cita_fecha_hora DESC");

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (fFecha != null && !fFecha.isEmpty()) ps.setString(paramIndex++, fFecha);
            if (fCliente != null && !fCliente.trim().isEmpty()) ps.setString(paramIndex++, "%" + fCliente.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> c = new HashMap<>();
                    c.put("citaId", rs.getInt("cita_id"));
                    c.put("cliente", rs.getString("cliente"));
                    c.put("estado", rs.getString("estado"));
                    c.put("notas", rs.getString("notas"));
                    c.put("fechaHora", rs.getTimestamp("fechaHora"));
                    c.put("motivo", rs.getString("motivo"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas filtradas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene todas las citas registradas en el sistema.
     */
    public List<Map<String, Object>> obtenerTodasLasCitas() throws Exception {
        return obtenerCitasFiltradas(null, null);
    }

    /**
     * Obtiene los 10 pedidos más recientes que están activos.
     */
    public List<Map<String, Object>> obtenerPedidosRecientes() throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, u.user_nombre, c.cita_estado, c.cita_fecha_hora " +
                     "FROM PEDIDOS p " +
                     "INNER JOIN USUARIOS u ON p.usuario_id = u.user_id " +
                     "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                     "WHERE p.pedido_estado IN ('pendiente','confirmado','en_proceso','terminado') " +
                     "ORDER BY p.pedido_id DESC LIMIT 10";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> p = new HashMap<>();
                p.put("pedidoId", rs.getInt("pedido_id"));
                p.put("estado", rs.getString("pedido_estado"));
                p.put("cliente", rs.getString("user_nombre"));
                p.put("citaEstado", rs.getString("cita_estado"));
                p.put("citaFecha", rs.getTimestamp("cita_fecha_hora"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener pedidos recientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene las citas programadas para el día de hoy.
     */
    public List<Map<String, Object>> obtenerCitasHoy() throws Exception {
        String sql = "SELECT c.cita_id, u.user_nombre AS cliente, c.cita_estado AS estado, c.cita_notas AS notas, c.cita_motivo AS motivo, c.cita_fecha_hora AS fechaHora " +
                     "FROM CITAS c " +
                     "JOIN PEDIDOS p ON c.pedido_id = p.pedido_id " +
                     "JOIN USUARIOS u ON p.usuario_id = u.user_id " +
                     "WHERE DATE(c.cita_fecha_hora) = CURDATE() " +
                     "ORDER BY c.cita_fecha_hora ASC";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> c = new HashMap<>();
                c.put("citaId", rs.getInt("cita_id"));
                c.put("cliente", rs.getString("cliente"));
                c.put("estado", rs.getString("estado"));
                c.put("notas", rs.getString("notas"));
                c.put("motivo", rs.getString("motivo"));
                c.put("fechaHora", rs.getTimestamp("fechaHora"));
                lista.add(c);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener citas de hoy: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene todas las personalizaciones (pedidos especiales) realizadas por un usuario específico.
     */
    public List<Map<String, Object>> obtenerPersonalizacionesPorUsuario(int userId) throws Exception {
        String sql = "SELECT p.personalizacion_id, p.descripcion, p.material_tela, p.imagen_referencia, " +
                     "p.estado, p.fecha_creacion, s.servicio_nombre " +
                     "FROM PERSONALIZACIONES p " +
                     "INNER JOIN SERVICIOS s ON p.servicio_id = s.servicio_id " +
                     "WHERE p.user_id = ? " +
                     "ORDER BY p.fecha_creacion DESC";
        
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> per = new HashMap<>();
                    per.put("id", rs.getInt("personalizacion_id"));
                    per.put("descripcion", rs.getString("descripcion"));
                    per.put("material", rs.getString("material_tela"));
                    per.put("imagen", rs.getString("imagen_referencia"));
                    per.put("estado", rs.getString("estado"));
                    per.put("fecha", rs.getTimestamp("fecha_creacion"));
                    per.put("servicio", rs.getString("servicio_nombre"));
                    lista.add(per);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener personalizaciones del usuario: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene la información detallada de un usuario por su ID.
     */
    public Map<String, Object> obtenerUsuarioPorId(int userId) throws Exception {
        String sql = "SELECT u.user_id, u.user_nombre, u.user_email, u.user_ubicacion_direccion, t.telefono_numero " +
                     "FROM USUARIOS u " +
                     "LEFT JOIN TELEFONOS t ON u.user_id = t.user_id AND t.telefono_es_principal = true " +
                     "WHERE u.user_id = ?";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("userId", rs.getInt("user_id"));
                    user.put("nombre", rs.getString("user_nombre"));
                    user.put("email", rs.getString("user_email"));
                    user.put("direccion", rs.getString("user_ubicacion_direccion"));
                    user.put("telefono", rs.getString("telefono_numero"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene todas las personalizaciones del sistema con información del cliente y servicio.
     * @param busqueda Filtro opcional por nombre de cliente o servicio.
     */
    public List<Map<String, Object>> obtenerPersonalizacionesGenerales(String busqueda) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT p.personalizacion_id, p.descripcion, p.material_tela, p.estado, p.fecha_creacion, " +
            "u.user_nombre AS cliente, u.user_id, s.servicio_nombre AS servicio " +
            "FROM PERSONALIZACIONES p " +
            "INNER JOIN USUARIOS u ON p.user_id = u.user_id " +
            "INNER JOIN SERVICIOS s ON p.servicio_id = s.servicio_id "
        );

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" WHERE u.user_nombre LIKE ? OR s.servicio_nombre LIKE ? OR p.descripcion LIKE ? ");
        }
        sql.append(" ORDER BY p.fecha_creacion DESC");

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                String term = "%" + busqueda.trim() + "%";
                ps.setString(1, term);
                ps.setString(2, term);
                ps.setString(3, term);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> per = new HashMap<>();
                    per.put("id", rs.getInt("personalizacion_id"));
                    per.put("descripcion", rs.getString("descripcion"));
                    per.put("material", rs.getString("material_tela"));
                    per.put("estado", rs.getString("estado"));
                    per.put("fecha", rs.getTimestamp("fecha_creacion"));
                    per.put("cliente", rs.getString("cliente"));
                    per.put("userId", rs.getInt("user_id"));
                    per.put("servicio", rs.getString("servicio"));
                    lista.add(per);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener personalizaciones generales: " + e.getMessage());
        }
        return lista;
    }
}
