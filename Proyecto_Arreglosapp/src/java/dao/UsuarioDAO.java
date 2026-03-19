package dao;

import config.ConectionDB;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase proporciona los métodos para gestionar la persistencia de los
 * datos de los usuarios.
 * Permite realizar operaciones de autenticación, registro y actualización en la
 * base de datos MySQL.
 * 
 * @author Antigravity - Senior Architect
 */
public class UsuarioDAO {

    /**
     * Verifica si una dirección de correo electrónico ya existe en la base de datos.
     * Útil para validar registros duplicados.
     * 
     * @param email El correo electrónico a verificar.
     * @return true si el email ya está registrado, false en caso contrario.
     * @throws Exception Si ocurre un error de conexión o ejecución SQL.
     */
    public boolean existeEmail(String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM USUARIOS WHERE user_email = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si un número de teléfono ya se encuentra registrado en el sistema.
     * Se consulta en la tabla TELEFONOS.
     * 
     * @param telefono El número de teléfono a verificar.
     * @return true si el teléfono existe, false en caso contrario.
     * @throws Exception Si ocurre un error en la base de datos.
     */
    public boolean existeTelefono(String telefono) throws Exception {
        String sql = "SELECT COUNT(*) FROM TELEFONOS WHERE telefono_numero = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar teléfono: " + e.getMessage());
        }
        return false;
    }

    /**
     * Autentica a un usuario verificando su correo y contraseña.
     * Soporta verificación en texto plano (para compatibilidad) y BCrypt.
     * 
     * @param email El correo electrónico del usuario.
     * @param password La contraseña proporcionada en el formulario.
     * @return El objeto Usuario con sus datos si la autenticación es exitosa.
     * @throws Exception Si las credenciales son incorrectas o el email no existe.
     */
    public Usuario autenticarUsuario(String email, String password) throws Exception {
        String sql = "SELECT user_id, user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id "
                + "FROM USUARIOS WHERE user_email = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("user_password_hash");

                    // 1. Verificación de seguridad: Texto plano vs BCrypt
                    if (password.equals(storedHash)) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("user_id"));
                        usuario.setEmail(rs.getString("user_email"));
                        usuario.setNombre(rs.getString("user_nombre"));
                        usuario.setDireccion(rs.getString("user_ubicacion_direccion"));
                        usuario.setRolId(rs.getInt("rol_id"));
                        return usuario;
                    }
                    else if (BCrypt.checkpw(password, storedHash)) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("user_id"));
                        usuario.setEmail(rs.getString("user_email"));
                        usuario.setNombre(rs.getString("user_nombre"));
                        usuario.setDireccion(rs.getString("user_ubicacion_direccion"));
                        usuario.setRolId(rs.getInt("rol_id"));
                        return usuario;
                    } else {
                        // Error de validación de contraseña
                        throw new Exception("PASSWORD_INCORRECT");
                    }
                } else {
                    // El usuario no existe en la base de datos
                    throw new Exception("EMAIL_NOT_FOUND");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al autenticar usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza la contraseña de un usuario basado en su email.
     * La nueva contraseña se almacena siempre como un hash BCrypt.
     * 
     * @param email Email del usuario.
     * @param nuevaPassword Nueva contraseña en texto plano.
     * @return true si se actualizó correctamente.
     * @throws Exception En caso de error SQL.
     */
    public boolean actualizarPassword(String email, String nuevaPassword) throws Exception {
        String sql = "UPDATE USUARIOS SET user_password_hash = ? WHERE user_email = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // Generar sal y hash seguro para la nueva clave
            String passwordHash = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt());

            ps.setString(1, passwordHash);
            ps.setString(2, email);

            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    /**
     * Realiza un registro transaccional de un nuevo usuario y su teléfono.
     * Utiliza autoCommit(false) para asegurar que se creen ambos registros o ninguno.
     * 
     * @param user Objeto Usuario con email, nombre, dirección y contraseña.
     * @param telefono Número de teléfono principal.
     * @return true si el registro completo fue exitoso.
     * @throws Exception Si ocurre un fallo en los INSERTs o en el commit.
     */
    public boolean registrarUsuarioCompleto(Usuario user, String telefono) throws Exception {
        String sqlUser = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlTel = "INSERT INTO TELEFONOS (user_id, telefono_numero, telefono_es_principal) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Iniciar transacción

            // Hashear la contraseña antes de guardar
            String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            int generatedUserId = -1;

            // 1. Insertar en la tabla USUARIOS
            try (PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, user.getEmail());
                psUser.setString(2, passwordHash);
                psUser.setString(3, user.getNombre());
                psUser.setString(4, user.getDireccion());
                psUser.setInt(5, 2); // Por defecto rol_id = 2 (CLIENTE)
                psUser.executeUpdate();

                // Recuperar el ID autogenerado para vincular el teléfono
                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedUserId = rs.getInt(1);
                    } else {
                        throw new Exception("No se obtuvo el ID del usuario.");
                    }
                }
            }

            // 2. Insertar en la tabla TELEFONOS si se proporcionó uno
            if (telefono != null && !telefono.trim().isEmpty()) {
                try (PreparedStatement psTel = con.prepareStatement(sqlTel)) {
                    psTel.setInt(1, generatedUserId);
                    psTel.setString(2, telefono.trim());
                    psTel.setBoolean(3, true);
                    psTel.executeUpdate();
                }
            }

            con.commit(); // Confirmar cambios en la DB
            return true;

        } catch (SQLException e) {
            if (con != null)
                con.rollback(); // Revertir en caso de fallo
            throw new Exception("Error al registrar: " + e.getMessage());
        } finally {
            if (con != null)
                con.close(); // Liberar conexión
        }
    }

    /**
     * Crea un usuario administrador inicial con credenciales por defecto.
     * Se usa en el Setup inicial del proyecto.
     * 
     * @return true si fue creado, false si ya existía.
     * @throws Exception Error de base de datos.
     */
    public boolean crearAdministradorPorDefecto() throws Exception {
        String emailAdmin = "admin@arreglosapp.com";
        String passwordAdmin = "admin123";
        String nombreAdmin = "Administrador";
        String direccionAdmin = "Oficina Principal";

        if (existeEmail(emailAdmin)) {
            return false;
        }

        String sql = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            String passwordHash = BCrypt.hashpw(passwordAdmin, BCrypt.gensalt());

            ps.setString(1, emailAdmin);
            ps.setString(2, passwordHash);
            ps.setString(3, nombreAdmin);
            ps.setString(4, direccionAdmin);
            ps.setInt(5, 1); // rol_id = 1 (ADMINISTRADOR)

            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al crear administrador por defecto: " + e.getMessage());
        }
    }

    /**
     * Verifica si existe al menos un usuario con rol de administrador en el sistema.
     * 
     * @return true si hay administradores.
     * @throws Exception Error SQL.
     */
    public boolean existeAdministrador() throws Exception {
        String sql = "SELECT COUNT(*) FROM USUARIOS WHERE rol_id = 1";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar administrador: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza el nombre y la dirección de un usuario existente.
     * 
     * @param userId ID del usuario a actualizar.
     * @param nombre Nuevo nombre completo.
     * @param direccion Nueva dirección de ubicación.
     * @return true si la actualización fue exitosa.
     * @throws Exception Error SQL.
     */
    public boolean actualizarDatosPersonales(int userId, String nombre, String direccion) throws Exception {
        String sql = "UPDATE USUARIOS SET user_nombre = ?, user_ubicacion_direccion = ? WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar datos: " + e.getMessage());
        }
    }

    /**
     * Comprueba si la contraseña proporcionada coincide con el hash almacenado en la DB.
     * 
     * @param userId ID del usuario.
     * @param password Contraseña a verificar.
     * @return true si coincide.
     * @throws Exception Error SQL.
     */
    public boolean verificarPassword(int userId, String password) throws Exception {
        String sql = "SELECT user_password_hash FROM USUARIOS WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("user_password_hash");
                    if (password.equals(hash))
                        return true;
                    try {
                        return BCrypt.checkpw(password, hash);
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar contraseña: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cambia la contraseña de un usuario basado en su ID único.
     * 
     * @param userId ID del usuario.
     * @param nuevaPassword Nueva contraseña en texto plano.
     * @return true si se actualizó correctamente.
     * @throws Exception Error SQL.
     */
    public boolean actualizarPasswordPorId(int userId, String nuevaPassword) throws Exception {
        String sql = "UPDATE USUARIOS SET user_password_hash = ? WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    /**
     * Recupera el número de teléfono marcado como principal para un usuario.
     * 
     * @param userId ID del usuario.
     * @return El número de teléfono o null si no existe.
     * @throws Exception Error SQL.
     */
    public String obtenerTelefonoPrincipal(int userId) throws Exception {
        String sql = "SELECT telefono_numero FROM TELEFONOS WHERE user_id = ? AND telefono_es_principal = true LIMIT 1";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("telefono_numero");
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener teléfono: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza el teléfono principal existente o inserta uno nuevo si no hay.
     * 
     * @param userId ID del usuario.
     * @param telefono Nuevo número telefónico.
     * @return true si la operación tuvo éxito.
     * @throws Exception Error SQL.
     */
    public boolean actualizarTelefono(int userId, String telefono) throws Exception {
        String sqlCheck = "SELECT COUNT(*) FROM TELEFONOS WHERE user_id = ? AND telefono_es_principal = true";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Actualización de registro existente
                    String sqlUpdate = "UPDATE TELEFONOS SET telefono_numero = ? WHERE user_id = ? AND telefono_es_principal = true";
                    try (PreparedStatement psU = con.prepareStatement(sqlUpdate)) {
                        psU.setString(1, telefono);
                        psU.setInt(2, userId);
                        return psU.executeUpdate() > 0;
                    }
                } else {
                    // Inserción de nuevo registro principal
                    String sqlInsert = "INSERT INTO TELEFONOS (user_id, telefono_numero, telefono_es_principal) VALUES (?, ?, true)";
                    try (PreparedStatement psI = con.prepareStatement(sqlInsert)) {
                        psI.setInt(1, userId);
                        psI.setString(2, telefono);
                        return psI.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar teléfono: " + e.getMessage());
        }
    }

    /**
     * Cuenta cuántos pedidos tiene el usuario en estados activos (no finalizados).
     * 
     * @param userId ID del usuario.
     * @return Cantidad de pedidos activos.
     * @throws Exception Error SQL.
     */
    public int contarPedidosActivos(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM PEDIDOS WHERE usuario_id = ? AND pedido_estado IN ('pendiente','confirmado','en_proceso')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new Exception("Error al contar pedidos: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el conteo total de servicios marcados como favoritos por el usuario.
     */
    public int contarFavoritos(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new Exception("Error al contar favoritos: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el conteo de solicitudes de personalización enviadas por el usuario.
     */
    public int contarPersonalizaciones(int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM PERSONALIZACIONES WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new Exception("Error al contar personalizaciones: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Recupera las últimas 5 notificaciones recibidas por el usuario.
     * RF-13: Sistema de Notificaciones.
     * 
     * @param userId ID del cliente.
     * @return Lista de mapas con mensaje, fecha y estado de lectura.
     * @throws Exception Error SQL.
     */
    public List<Map<String, Object>> obtenerNotificaciones(int userId) throws Exception {
        String sql = "SELECT * FROM NOTIFICACIONES WHERE user_id = ? ORDER BY fecha_creacion DESC LIMIT 5";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> n = new HashMap<>();
                    n.put("mensaje", rs.getString("mensaje"));
                    n.put("fecha", rs.getTimestamp("fecha_creacion"));
                    n.put("leida", rs.getBoolean("leida"));
                    lista.add(n);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener notificaciones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene la lista completa de pedidos del usuario con detalles del servicio asociado.
     * Utiliza LEFT JOIN para vincular tablas de pedidos y personalizaciones.
     * 
     * @param userId ID del usuario.
     * @return Lista de mapas con información detallada de pedidos.
     * @throws Exception Error SQL.
     */
    public List<Map<String, Object>> obtenerMisPedidos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha_creacion, " +
                "p.pedido_total, p.pedido_monto_abonado, p.pedido_pago_estado, " +
                "a.arreglo_nombre " +
                "FROM pedidos p " +
                "LEFT JOIN personalizaciones per ON per.personalizacion_id = p.pedido_id " +
                "LEFT JOIN arreglos a ON a.arreglo_id = per.arreglo_id " +
                "WHERE p.usuario_id = ? ORDER BY p.pedido_fecha_creacion DESC";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("estado", rs.getString("pedido_estado"));
                    p.put("fecha", rs.getTimestamp("pedido_fecha_creacion"));
                    p.put("total", rs.getDouble("pedido_total"));
                    p.put("abonado", rs.getDouble("pedido_monto_abonado"));
                    p.put("pagoEstado", rs.getString("pedido_pago_estado"));
                    p.put("servicio", rs.getString("arreglo_nombre"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener mis pedidos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Agendar una nueva cita presencial vinculada a un pedido existente.
     * 
     * @param pedidoId ID del pedido vinculado.
     * @param fechaHora Fecha y hora de la cita.
     * @param motivo Razón de la cita.
     * @param notas Observaciones adicionales.
     * @return true si se agendó correctamente.
     * @throws Exception Error SQL.
     */
    public boolean agendarCita(int pedidoId, String fechaHora, String motivo, String notas) throws Exception {
        String sql = "INSERT INTO citas (pedido_id, cita_fecha_hora, cita_motivo, cita_notas, cita_estado) VALUES (?, ?, ?, ?, 'programada')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.setString(2, fechaHora);
            ps.setString(3, motivo);
            ps.setString(4, notas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al agendar cita: " + e.getMessage());
        }
    }

    /**
     * Inserta una nueva notificación interna para un usuario específico.
     * 
     * @param userId ID del destinatario.
     * @param mensaje Contenido de la notificación.
     * @return true si se insertó correctamente.
     * @throws Exception Error SQL.
     */
    public boolean insertarNotificacion(int userId, String mensaje) throws Exception {
        String sql = "INSERT INTO NOTIFICACIONES (user_id, mensaje) VALUES (?, ?)";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, mensaje);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al insertar notificación: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario puede ser eliminado según sus dependencias.
     * Un usuario es eliminable solo si:
     * - No tiene favoritos activos
     * - No tiene pedidos (órdenes) vinculados
     * - No tiene citas activas (solo permite citas canceladas)
     * 
     * @param userId ID del usuario a validar.
     * @return Map con 'canDelete' (boolean) y 'reason' (String) si no se puede eliminar.
     * @throws Exception Error SQL.
     */
    public Map<String, Object> canDeleteUser(int userId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        result.put("reason", "");
        
        try (Connection con = ConectionDB.getConexion()) {
            
            // 1. Verificar favoritos
            String sqlFavoritos = "SELECT COUNT(*) FROM FAVORITOS WHERE user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlFavoritos)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("canDelete", false);
                        result.put("reason", "El usuario tiene " + rs.getInt(1) + " favoritos registrados");
                        return result;
                    }
                }
            }
            
            // 2. Verificar pedidos (órdenes) - solo los que impiden eliminación
            String sqlPedidos = "SELECT COUNT(*) FROM PEDIDOS WHERE usuario_id = ? AND pedido_estado NOT IN ('terminado', 'cancelado', 'entregado')";
            try (PreparedStatement ps = con.prepareStatement(sqlPedidos)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("canDelete", false);
                        result.put("reason", "El usuario tiene " + rs.getInt(1) + " pedidos activos registrados");
                        return result;
                    }
                }
            }
            
            // 3. Verificar citas activas (excluyendo completadas y canceladas)
            String sqlCitas = "SELECT COUNT(*) FROM CITAS c " +
                            "JOIN PEDIDOS p ON c.pedido_id = p.pedido_id " +
                            "WHERE p.usuario_id = ? AND c.cita_estado NOT IN ('completada', 'cancelada')";
            try (PreparedStatement ps = con.prepareStatement(sqlCitas)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("canDelete", false);
                        result.put("reason", "El usuario tiene " + rs.getInt(1) + " citas activas (no completadas ni canceladas)");
                        return result;
                    }
                }
            }
            
            // 4. Verificar personalizaciones (importante!)
            String sqlPersonalizaciones = "SELECT COUNT(*) FROM PERSONALIZACIONES WHERE user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlPersonalizaciones)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("canDelete", false);
                        result.put("reason", "El usuario tiene " + rs.getInt(1) + " personalizaciones registradas");
                        return result;
                    }
                }
            }
            
            // 5. Opcional: Contar citas canceladas para informe
            String sqlCitasCanceladas = "SELECT COUNT(*) FROM CITAS c " +
                                      "JOIN PEDIDOS p ON c.pedido_id = p.pedido_id " +
                                      "WHERE p.usuario_id = ? AND c.cita_estado = 'cancelada'";
            try (PreparedStatement ps = con.prepareStatement(sqlCitasCanceladas)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        result.put("canceledAppointments", rs.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new Exception("Error al validar eliminación de usuario: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Identifica todas las dependencias de un usuario para diagnóstico
     */
    public Map<String, Integer> diagnosticarDependencias(int userId) throws Exception {
        Map<String, Integer> dependencias = new HashMap<>();
        
        try (Connection con = ConectionDB.getConexion()) {
            // Contar cada tipo de dependencia
            String[] tablas = {"FAVORITOS", "PEDIDOS", "PERSONALIZACIONES", "TELEFONOS", "NOTIFICACIONES"};
            String[] columnas = {"user_id", "usuario_id", "user_id", "user_id", "user_id"};
            
            for (int i = 0; i < tablas.length; i++) {
                String sql = "SELECT COUNT(*) FROM " + tablas[i] + " WHERE " + columnas[i] + " = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            dependencias.put(tablas[i], rs.getInt(1));
                        }
                    }
                }
            }
            
            // Citas (requiere JOIN)
            String sqlCitas = "SELECT COUNT(*) FROM CITAS c " +
                            "JOIN PEDIDOS p ON c.pedido_id = p.pedido_id " +
                            "WHERE p.usuario_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCitas)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dependencias.put("CITAS", rs.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new Exception("Error al diagnosticar dependencias: " + e.getMessage());
        }
        
        return dependencias;
    }

    /**
     * Elimina un usuario de forma segura después de validar dependencias.
     * Realiza eliminación en cascada de datos seguros como teléfonos, notificaciones, etc.
     * NO elimina datos importantes como pedidos, personalizaciones o citas.
     * 
     * @param userId ID del usuario a eliminar.
     * @return true si la eliminación fue exitosa.
     * @throws Exception Si el usuario tiene dependencias importantes o error SQL.
     */
    public boolean eliminarUsuarioSeguro(int userId) throws Exception {
        System.out.println("DEBUG: Iniciando eliminación segura del usuario " + userId);
        
        // Primero validar si se puede eliminar
        Map<String, Object> validation = canDeleteUser(userId);
        System.out.println("DEBUG: Validación - canDelete: " + validation.get("canDelete") + ", reason: " + validation.get("reason"));
        
        if (!(Boolean) validation.get("canDelete")) {
            System.out.println("DEBUG: Bloqueado por validación: " + validation.get("reason"));
            throw new Exception("NO_SE_PUEDE_ELIMINAR: " + validation.get("reason"));
        }
        
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);
            System.out.println("DEBUG: Conexión obtenida y autoCommit=false");
            
            // 1. Eliminar dependencias seguras
            // Teléfonos
            System.out.println("DEBUG: Eliminando teléfonos del usuario " + userId);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM TELEFONOS WHERE user_id = ?")) {
                ps.setInt(1, userId);
                int telefonosEliminados = ps.executeUpdate();
                System.out.println("DEBUG: Teléfonos eliminados: " + telefonosEliminados);
            }
            
            // Notificaciones
            System.out.println("DEBUG: Eliminando notificaciones del usuario " + userId);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM NOTIFICACIONES WHERE user_id = ?")) {
                ps.setInt(1, userId);
                int notificacionesEliminadas = ps.executeUpdate();
                System.out.println("DEBUG: Notificaciones eliminadas: " + notificacionesEliminadas);
            }
            
            // 2. Eliminar dependencias con foreign keys (orden crucial)
            
            // Primero: Eliminar detalle_pedido (referencia a pedidos)
            System.out.println("DEBUG: Eliminando detalle_pedido del usuario " + userId);
            try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM detalle_pedido WHERE pedido_id IN (SELECT pedido_id FROM pedidos WHERE usuario_id = ?)")) {
                ps.setInt(1, userId);
                int detallesEliminados = ps.executeUpdate();
                System.out.println("DEBUG: Detalles de pedido eliminados: " + detallesEliminados);
            } catch (SQLException e) {
                System.out.println("ERROR en DELETE de detalle_pedido: " + e.getMessage());
                throw e;
            }
            
            // Segundo: Eliminar citas (referencia a pedidos)
            System.out.println("DEBUG: Eliminando citas del usuario " + userId);
            try (PreparedStatement ps = con.prepareStatement(
                "DELETE c FROM citas c JOIN pedidos p ON c.pedido_id = p.pedido_id WHERE p.usuario_id = ?")) {
                ps.setInt(1, userId);
                int citasEliminadas = ps.executeUpdate();
                System.out.println("DEBUG: Citas eliminadas: " + citasEliminadas);
            } catch (SQLException e) {
                System.out.println("ERROR en DELETE de citas: " + e.getMessage());
                throw e;
            }
            
            // Tercero: Eliminar pedidos (ya no tienen dependencias)
            System.out.println("DEBUG: Eliminando pedidos del usuario " + userId);
            try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM pedidos WHERE usuario_id = ?")) {
                ps.setInt(1, userId);
                int pedidosEliminados = ps.executeUpdate();
                System.out.println("DEBUG: Pedidos eliminados: " + pedidosEliminados);
            } catch (SQLException e) {
                System.out.println("ERROR en DELETE de pedidos: " + e.getMessage());
                throw e;
            }
            
            // 3. Eliminar el usuario
            System.out.println("DEBUG: Eliminando usuario principal " + userId);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM USUARIOS WHERE user_id = ? AND rol_id = 2")) {
                ps.setInt(1, userId);
                int filas = ps.executeUpdate();
                System.out.println("DEBUG: Filas de usuario eliminadas: " + filas);
                
                if (filas > 0) {
                    con.commit();
                    System.out.println("DEBUG: Commit exitoso - usuario eliminado");
                    return true;
                } else {
                    con.rollback();
                    System.out.println("DEBUG: Rollback - usuario no encontrado o no es cliente");
                    throw new Exception("Usuario no encontrado o no es cliente");
                }
            } catch (SQLException e) {
                System.out.println("ERROR en DELETE de usuario: " + e.getMessage());
                throw e;
            }
            
        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
                System.out.println("DEBUG: Rollback por SQLException: " + e.getMessage());
                System.out.println("DEBUG: SQL State: " + e.getSQLState());
                System.out.println("DEBUG: Error Code: " + e.getErrorCode());
            }
            
            if (e.getErrorCode() == 1451 || e.getErrorCode() == 1217 || e.getErrorCode() == 1216) {
                throw new Exception("NO_SE_PUEDE_ELIMINAR_TIENE_DEPENDENCIAS");
            }
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                    System.out.println("DEBUG: Conexión cerrada");
                } catch (SQLException e) {
                    System.out.println("DEBUG: Error cerrando conexión: " + e.getMessage());
                }
            }
        }
    }
}
