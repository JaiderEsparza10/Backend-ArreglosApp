/**
 * Nombre del archivo: UsuarioDAO.java
 * Descripción breve: Clase de acceso a datos (DAO) para la gestión integral de usuarios y sus perfiles.
 * Author: Jaider Andres Esparza — Antigravity
 * Fecha de documentación: 25 de marzo de 2026
 * Versión: 1.0
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar la persistencia y operaciones de acceso a datos relacionadas con los usuarios del sistema.
 */
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
 * Esta clase centraliza todas las consultas y actualizaciones sobre la tabla de usuarios y sus teléfonos.
 * Implementa el patrón Data Access Object (DAO) para aislar la lógica de negocio de la persistencia física.
 * Proporciona métodos para autenticación, registro, gestión de teléfonos y validación de borrado seguro.
 */
public class UsuarioDAO {

    /**
     * Verifica la existencia de un correo electrónico en la base de datos.
     * Este método es crítico para prevenir duplicidad de cuentas durante el registro.
     * 
     * @param email La dirección de correo a consultar.
     * @return true si el correo ya está en uso, false si está disponible.
     * @throws Exception Si ocurre un fallo en la conexión o en la sintaxis SQL.
     */
    public boolean existeEmail(String email) throws Exception {
        // Sentencia SQL para contar ocurrencias del email
        String sql = "SELECT COUNT(*) FROM USUARIOS WHERE user_email = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    // Retorna verdadero si el conteo es mayor a cero
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Se propaga el error con un mensaje descriptivo
            throw new Exception("Error al verificar email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Valida si un número de teléfono ya está registrado en el sistema.
     * Consulta la tabla de TELÉFONOS para asegurar la unicidad de los datos de contacto.
     * 
     * @param telefono El número telefónico a validar.
     * @return true si el número ya existe, false de lo contrario.
     * @throws Exception Si se presenta un error durante la consulta JDBC.
     */
    public boolean existeTelefono(String telefono) throws Exception {
        // Consulta dirigida a la tabla de teléfonos asociados a los usuarios
        String sql = "SELECT COUNT(*) FROM TELEFONOS WHERE telefono_numero = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    // Evaluación del resultado numérico
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar teléfono: " + e.getMessage());
        }
        return false;
    }

    /**
     * Valida las credenciales de acceso de un usuario.
     * Este método implementa un esquema de seguridad híbrido que permite la transición
     * de contraseñas en texto plano hacia el estándar seguro BCrypt.
     * 
     * @param email    Correo electrónico del usuario.
     * @param password Contraseña proporcionada para la validación.
     * @return Objeto Usuario con su perfil completo si la validación es exitosa.
     * @throws Exception Arroja excepciones específicas como 'PASSWORD_INCORRECT' o 'EMAIL_NOT_FOUND'.
     */
    public Usuario autenticarUsuario(String email, String password) throws Exception {
        // Selección de campos esenciales para la sesión del usuario
        String sql = "SELECT user_id, user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id "
                + "FROM USUARIOS WHERE user_email = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("user_password_hash");

                    // Escenario 1: Compatibilidad con registros antiguos en texto plano
                    if (password.equals(storedHash)) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("user_id"));
                        usuario.setEmail(rs.getString("user_email"));
                        usuario.setNombre(rs.getString("user_nombre"));
                        usuario.setDireccion(rs.getString("user_ubicacion_direccion"));
                        usuario.setRolId(rs.getInt("rol_id"));
                        return usuario;
                    }
                    // Escenario 2: Validación estándar mediante algoritmos de hashing seguros (BCrypt)
                    else if (BCrypt.checkpw(password, storedHash)) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("user_id"));
                        usuario.setEmail(rs.getString("user_email"));
                        usuario.setNombre(rs.getString("user_nombre"));
                        usuario.setDireccion(rs.getString("user_ubicacion_direccion"));
                        usuario.setRolId(rs.getInt("rol_id"));
                        return usuario;
                    } else {
                        // Error controlado para contraseñas erróneas
                        throw new Exception("PASSWORD_INCORRECT");
                    }
                } else {
                    // Error controlado para usuarios inexistentes
                    throw new Exception("EMAIL_NOT_FOUND");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al autenticar usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza la clave de acceso de un usuario utilizando su correo como referencia.
     * Genera automáticamente un hash BCrypt para garantizar que la contraseña nunca se almacene en texto plano.
     * 
     * @param email         Correo electrónico del usuario.
     * @param nuevaPassword Nueva contraseña en formato legible (texto plano).
     * @return true si la operación de actualización en la BD fue exitosa.
     * @throws Exception Si falla la comunicación con el servidor de base de datos.
     */
    public boolean actualizarPassword(String email, String nuevaPassword) throws Exception {
        String sql = "UPDATE USUARIOS SET user_password_hash = ? WHERE user_email = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // Generar sal y hash seguro para la nueva clave mediante BCrypt
            String passwordHash = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt());

            ps.setString(1, passwordHash);
            ps.setString(2, email);

            // Ejecución de la sentencia de actualización DML
            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    /**
     * Realiza el registro integral de un nuevo usuario junto con su información de contacto.
     * Este método implementa una transacción atómica: se crean ambos registros (usuario y teléfono)
     * de forma exitosa o no se crea ninguno (Rollback), manteniendo la integridad referencial.
     * 
     * @param user     Objeto de tipo Usuario con los datos de perfil y credenciales.
     * @param telefono Número telefónico que se marcará como contacto principal.
     * @return true si el flujo transaccional se completó con éxito.
     * @throws Exception Si ocurre un fallo en los procesos de INSERT o en el commit final.
     */
    public boolean registrarUsuarioCompleto(Usuario user, String telefono) throws Exception {
        // Consultas para inserción en tablas relacionadas
        String sqlUser = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlTel = "INSERT INTO TELEFONOS (user_id, telefono_numero, telefono_es_principal) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Desactivación de commit automático para control manual de la transacción

            // Aplicación de hashing de seguridad a la contraseña del nuevo usuario
            String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            int generatedUserId = -1;

            // Paso 1: Registro en la tabla principal de usuarios
            try (PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, user.getEmail());
                psUser.setString(2, passwordHash);
                psUser.setString(3, user.getNombre());
                psUser.setString(4, user.getDireccion());
                psUser.setInt(5, 2); // Asignación automática del rol 2 (CLIENTE)
                psUser.executeUpdate();

                // Extracción de la llave primaria autogenerada (Identity)
                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedUserId = rs.getInt(1);
                    } else {
                        throw new Exception("No se obtuvo el ID del usuario.");
                    }
                }
            }

            // Paso 2: Asociación del número telefónico principal si se ha proporcionado
            if (telefono != null && !telefono.trim().isEmpty()) {
                try (PreparedStatement psTel = con.prepareStatement(sqlTel)) {
                    psTel.setInt(1, generatedUserId);
                    psTel.setString(2, telefono.trim());
                    psTel.setBoolean(3, true); // Marca de contacto principal para la interfaz
                    psTel.executeUpdate();
                }
            }

            con.commit(); // Consolidación de todos los cambios en la base de datos
            return true;

        } catch (SQLException e) {
            // Reversión de cambios ante cualquier error de red o lógica SQL
            if (con != null)
                con.rollback(); 
            throw new Exception("Error al registrar: " + e.getMessage());
        } finally {
            if (con != null)
                con.close(); // Cierre preventivo de recursos JDBC
        }
    }

    /**
     * Genera la cuenta de administrador maestra con credenciales predeterminadas.
     * Este método se utiliza típicamente durante el despliegue inicial (Setup) para
     * garantizar que el sistema siempre tenga un punto de acceso administrativo.
     * 
     * @return true si la cuenta fue creada, false si el email ya existía previamente.
     * @throws Exception Si ocurre un error estructural en la base de datos.
     */
    public boolean crearAdministradorPorDefecto() throws Exception {
        // Datos de acceso maestra para la configuración inicial
        String emailAdmin = "admin@arreglosapp.com";
        String passwordAdmin = "admin123";
        String nombreAdmin = "Administrador";
        String direccionAdmin = "Oficina Principal";

        // Protección contra duplicidad de cuentas maestras
        if (existeEmail(emailAdmin)) {
            return false;
        }

        String sql = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            // Generación de hash seguro para la clave administrativa
            String passwordHash = BCrypt.hashpw(passwordAdmin, BCrypt.gensalt());

            ps.setString(1, emailAdmin);
            ps.setString(2, passwordHash);
            ps.setString(3, nombreAdmin);
            ps.setString(4, direccionAdmin);
            ps.setInt(5, 1); // Asignación de rol 1 (ADMINISTRADOR)

            // Validación de ejecución exitosa
            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            throw new Exception("Error al crear administrador por defecto: " + e.getMessage());
        }
    }

    /**
     * Verifica si el sistema cuenta con al menos una cuenta de administrador.
     * Esencial para controles de acceso y preventivos en la lógica de configuración.
     * 
     * @return true si existe al menos un registro con rol_id = 1.
     * @throws Exception Si ocurre un fallo en la consulta SQL.
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
     * Actualiza la información de perfil (nombre y dirección) de un usuario.
     * Este método permite la edición de datos básicos del cliente desde su panel personal.
     * 
     * @param userId    ID único del usuario a modificar.
     * @param nombre    Nuevo nombre completo o razón social.
     * @param direccion Nueva dirección de residencia o entrega.
     * @return true si la fila fue actualizada en la base de datos.
     * @throws Exception Si se presenta un error de persistencia.
     */
    public boolean actualizarDatosPersonales(int userId, String nombre, String direccion) throws Exception {
        // Sentencia SQL dirigida a los campos de identidad y ubicación
        String sql = "UPDATE USUARIOS SET user_nombre = ?, user_ubicacion_direccion = ? WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, userId);
            // Ejecución de la actualización
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar datos: " + e.getMessage());
        }
    }

    /**
     * Compara una contraseña en texto plano contra el hash almacenado para un usuario específico.
     * Soporta validación legacy (texto plano) y moderna (BCrypt).
     * 
     * @param userId   ID único del usuario.
     * @param password Contraseña a verificar.
     * @return true si la contraseña es válida, false de lo contrario.
     * @throws Exception Si ocurre un fallo en la consulta JDBC.
     */
    public boolean verificarPassword(int userId, String password) throws Exception {
        String sql = "SELECT user_password_hash FROM USUARIOS WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("user_password_hash");
                    // Validación directa para cuentas antiguas
                    if (password.equals(hash))
                        return true;
                    // Validación segura mediante el motor BCrypt
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
     * Cambia la contraseña de un usuario a partir de su identificador único.
     * Genera un nuevo hash seguro antes de persistir el cambio.
     * 
     * @param userId        ID del usuario.
     * @param nuevaPassword Nueva contraseña en texto plano.
     * @return true si la clave se modificó exitosamente.
     * @throws Exception Error de base de datos.
     */
    public boolean actualizarPasswordPorId(int userId, String nuevaPassword) throws Exception {
        String sql = "UPDATE USUARIOS SET user_password_hash = ? WHERE user_id = ?";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            // Cifrado de la nueva clave
            ps.setString(1, BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    /**
     * Obtiene el número telefónico marcado como contacto principal del usuario.
     * 
     * @param userId ID único del usuario.
     * @return El número de teléfono si existe, o null en caso contrario.
     * @throws Exception Si ocurre un fallo en la ejecución JDBC.
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
        return null; // Retorno preventivo si no se halla registro
    }

    /**
     * Sincroniza el teléfono principal del usuario.
     * Si el usuario ya posee un teléfono principal, este se actualiza; de lo contrario, se crea uno nuevo.
     * 
     * @param userId   ID único del usuario.
     * @param telefono El nuevo número telefónico a establecer.
     * @return true si la operación (UPDATE o INSERT) fue satisfactoria.
     * @throws Exception Error de base de datos.
     */
    public boolean actualizarTelefono(int userId, String telefono) throws Exception {
        // Validación de existencia de un registro principal previo
        String sqlCheck = "SELECT COUNT(*) FROM TELEFONOS WHERE user_id = ? AND telefono_es_principal = true";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Acción: Actualización de registro preexistente
                    String sqlUpdate = "UPDATE TELEFONOS SET telefono_numero = ? WHERE user_id = ? AND telefono_es_principal = true";
                    try (PreparedStatement psU = con.prepareStatement(sqlUpdate)) {
                        psU.setString(1, telefono);
                        psU.setInt(2, userId);
                        return psU.executeUpdate() > 0;
                    }
                } else {
                    // Acción: Creación de un nuevo punto de contacto principal
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
     * Recupera el listado completo de números de contacto asociados a una cuenta de usuario.
     * Prioriza el teléfono principal en el orden de los resultados.
     * 
     * @param userId ID único del usuario.
     * @return Lista de mapas estructurados con ID, número y flag de principalidad.
     * @throws Exception Error de base de datos.
     */
    public List<Map<String, Object>> obtenerTodosTelefonos(int userId) throws Exception {
        // Query que prioriza el teléfono principal mediante ordenamiento descendente
        String sql = "SELECT telefono_id, telefono_numero, telefono_es_principal FROM TELEFONOS WHERE user_id = ? ORDER BY telefono_es_principal DESC, telefono_id ASC";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> tel = new HashMap<>();
                    tel.put("id", rs.getInt("telefono_id"));
                    tel.put("numero", rs.getString("telefono_numero"));
                    tel.put("esPrincipal", rs.getBoolean("telefono_es_principal"));
                    lista.add(tel);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener teléfonos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Registra un nuevo número de contacto para el usuario.
     * Implementa lógica de negocio para auto-asignar principalidad si es el primer teléfono,
     * y limita la cantidad máxima permitida para evitar abusos.
     * 
     * @param userId   ID del usuario.
     * @param telefono Número telefónico adicional.
     * @return true si el teléfono fue agregado satisfactoriamente.
     * @throws Exception Si se excede el límite de 3 teléfonos o por error SQL.
     */
    public boolean agregarTelefonoAdicional(int userId, String telefono) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Gestión manual de la transacción
            
            // Verificación de reglas de negocio: límite de dispositivos y principalidad
            String sqlCheck = "SELECT COUNT(*) as total, SUM(CASE WHEN telefono_es_principal = 1 THEN 1 ELSE 0 END) as principales FROM TELEFONOS WHERE user_id = ?";
            boolean needsPrincipal = true;
            try (PreparedStatement psC = con.prepareStatement(sqlCheck)) {
                psC.setInt(1, userId);
                try (ResultSet rs = psC.executeQuery()) {
                    if (rs.next()) {
                        // REGLA: Máximo 3 teléfonos por cuenta
                        if (rs.getInt("total") >= 3) {
                            throw new Exception("Límite de 3 teléfonos alcanzado.");
                        }
                        // REGLA: Si no hay principales, el nuevo será el principal
                        needsPrincipal = rs.getInt("principales") == 0;
                    }
                }
            }
            
            String sql = "INSERT INTO TELEFONOS (user_id, telefono_numero, telefono_es_principal) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, telefono);
                ps.setBoolean(3, needsPrincipal);
                ps.executeUpdate();
            }
            
            con.commit(); // Confirmación de los cambios
            return true;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw new Exception("Error al agregar teléfono adicional: " + e.getMessage());
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * Remueve un número telefónico de la base de datos.
     * Si el teléfono a eliminar es el principal, el sistema designará automáticamente 
     * el registro remanente más antiguo como nuevo principal para no perder contacto.
     * 
     * @param telefonoId ID único del registro telefónico.
     * @param userId     ID del propietario del registro.
     * @return true si la eliminación y la reasignación de principalidad fueron exitosas.
     * @throws Exception Error estructural o de persistencia.
     */
    public boolean eliminarTelefono(int telefonoId, int userId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);
            
            // Verificación del estado de principalidad del registro objetivo
            boolean esPrincipal = false;
            String sqlCheck = "SELECT telefono_es_principal FROM TELEFONOS WHERE telefono_id = ? AND user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setInt(1, telefonoId);
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        esPrincipal = rs.getBoolean("telefono_es_principal");
                    } else {
                        // El registro no existe o no hay coincidencia de pertenencia
                        return false; 
                    }
                }
            }
            
            // Ejecución de la remoción física del registro
            String sqlDel = "DELETE FROM TELEFONOS WHERE telefono_id = ? AND user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlDel)) {
                ps.setInt(1, telefonoId);
                ps.setInt(2, userId);
                int deleted = ps.executeUpdate();
                if (deleted == 0) return false;
            }
            
            // Mecanismo de recuperación de contacto: reasignación de principalidad automática
            if (esPrincipal) {
                String sqlUpdate = "UPDATE TELEFONOS SET telefono_es_principal = true WHERE user_id = ? ORDER BY telefono_id ASC LIMIT 1";
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }
            
            con.commit(); // Consolidación de la operación atómica
            return true;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw new Exception("Error al eliminar teléfono: " + e.getMessage());
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * Promueve un registro telefónico específico al estado de contacto principal.
     * Resetea automáticamente cualquier otro teléfono que tuviera dicha marca previamente.
     * 
     * @param telefonoId ID del registro a promover.
     * @param userId     ID del propietario del registro.
     * @return true si el cambio de prioridad se aplicó correctamente.
     * @throws Exception Si el registro no se encuentra o no pertenece al usuario.
     */
    public boolean establecerTelefonoPrincipal(int telefonoId, int userId) throws Exception {
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Transacción para asegurar un único principal
            
            // Paso 1: Resetear el flag de principalidad para todos los números del usuario
            String sqlReset = "UPDATE TELEFONOS SET telefono_es_principal = false WHERE user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlReset)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            
            // Paso 2: Asignar el nuevo contacto preferencial
            String sqlSet = "UPDATE TELEFONOS SET telefono_es_principal = true WHERE telefono_id = ? AND user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlSet)) {
                ps.setInt(1, telefonoId);
                ps.setInt(2, userId);
                int updated = ps.executeUpdate();
                if (updated == 0) throw new Exception("Teléfono no encontrado o no pertenece al usuario.");
            }
            
            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw new Exception("Error al cambiar teléfono principal: " + e.getMessage());
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
    /**
     * Contabiliza el número de órdenes de servicio que el usuario tiene actualmente en curso.
     * Considera los estados 'pendiente', 'confirmado' y 'en_proceso'.
     * 
     * @param userId ID único del usuario.
     * @return Número total de pedidos activos detectados.
     * @throws Exception Si ocurre un fallo en el conteo SQL.
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
     * Obtiene el volumen total de servicios que el usuario ha marcado como favoritos.
     * 
     * @param userId ID del usuario.
     * @return Conteo de registros en la tabla FAVORITOS.
     * @throws Exception Error de base de datos.
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
     * Contabiliza las solicitudes de personalización registradas por el cliente.
     * 
     * @param userId ID del usuario.
     * @return Número de personalizaciones encontradas.
     * @throws Exception Error SQL.
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
     * Recupera el historial reciente de alertas y notificaciones del usuario.
     * Implementa el Requisito Funcional RF-13 para mantener informado al cliente sobre su actividad.
     * 
     * @param userId ID del destinatario.
     * @return Lista de las 5 notificaciones más recientes (orden cronológico inverso).
     * @throws Exception Si falla la extracción de datos.
     */
    public List<Map<String, Object>> obtenerNotificaciones(int userId) throws Exception {
        // Limitación a los últimos 5 eventos para optimización de UI
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
     * Obtiene el consolidado de pedidos realizados por el usuario.
     * Realiza una unión (JOIN) entre pedidos y catálogos para presentar nombres descriptivos de servicios.
     * 
     * @param userId ID único del usuario.
     * @return Lista de mapas con ID, estado, fecha, total y nombre del servicio.
     * @throws Exception Error de base de datos.
     */
    public List<Map<String, Object>> obtenerMisPedidos(int userId) throws Exception {
        // Consulta compleja con LEFT JOIN para asegurar la obtención de datos descriptivos
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, " +
                "p.pedido_total, " +
                "a.arreglo_nombre " +
                "FROM pedidos p " +
                "LEFT JOIN detalle_pedido dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN arreglos a ON a.arreglo_id = dp.arreglo_id " +
                "WHERE p.usuario_id = ? ORDER BY p.pedido_fecha DESC";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("estado", rs.getString("pedido_estado"));
                    p.put("fecha", rs.getTimestamp("pedido_fecha"));
                    p.put("total", rs.getDouble("pedido_total"));
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
     * Registra una nueva cita presencial vinculada a un pedido específico.
     * 
     * @param pedidoId  ID del pedido asociado.
     * @param fechaHora Cadena con la fecha y hora de la cita.
     * @param motivo    Causa de la reunión.
     * @param notas     Detalles u observaciones adicionales.
     * @return true si la cita fue agendada correctamente.
     * @throws Exception Error de persistencia.
     */
    public boolean agendarCita(int pedidoId, String fechaHora, String motivo, String notas) throws Exception {
        // Inserción con estado inicial 'programada'
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
     * Genera una nueva entrada de notificación para un usuario.
     * 
     * @param userId  ID del usuario destinatario.
     * @param mensaje Contenido textual de la notificación.
     * @return true si el registro fue exitoso.
     * @throws Exception Error de persistencia.
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
     * Realiza una auditoría de integridad para determinar si un usuario puede ser eliminado del sistema.
     * Un usuario bloquea su eliminación si posee:
     * 1. Favoritos activos.
     * 2. Pedidos en estados no finalizados (distintos de terminado, cancelado o entregado).
     * 3. Citas activas (no completadas ni canceladas).
     * 4. Solicitudes de personalización.
     * 
     * @param userId ID del usuario a auditar.
     * @return Map con 'canDelete' (boolean) y 'reason' (String) con el diagnóstico detallado.
     * @throws Exception Error estructural en la base de datos.
     */
    public Map<String, Object> canDeleteUser(int userId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        result.put("reason", "");
        
        try (Connection con = ConectionDB.getConexion()) {
            
            // Verificación 1: Registros de Favoritos
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
            
            // Verificación 2: Órdenes de Pedido activas
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
            
            // Verificación 3: Citas de servicio activas
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
            
            // Verificación 4: Proyectos de personalización técnica
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
            
            // Información adicional opcional para el informe administrativo: Conteo de citas canceladas
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
     * Realiza un diagnóstico técnico de todas las dependencias persistentes de un usuario.
     * Útil para auditoría y debug antes de procesos de borrado.
     * 
     * @param userId ID del usuario a diagnosticar.
     * @return Map con el nombre de la tabla y la cantidad de registros asociados.
     * @throws Exception Error SQL.
     */
    public Map<String, Integer> diagnosticarDependencias(int userId) throws Exception {
        Map<String, Integer> dependencias = new HashMap<>();
        
        try (Connection con = ConectionDB.getConexion()) {
            // Iteración sobre las tablas que poseen llaves foráneas hacia el usuario
            String[] tablas = {"FAVORITOS", "PEDIDOS", "PERSONALIZACIONES", "TELEFONOS"/*, "NOTIFICACIONES"*/};
            String[] columnas = {"user_id", "usuario_id", "user_id", "user_id"/*, "user_id"*/};
            
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
            
            // Auditoría de Citas (requiere JOIN mediante la tabla de Pedidos)
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
     * Ejecuta la eliminación definitiva y segura de un usuario del sistema.
     * Este proceso es irreversible y realiza las siguientes acciones:
     * 1. Valida si el usuario cumple con los requisitos para ser eliminado (canDeleteUser).
     * 2. Elimina registros de contacto (Teléfonos).
     * 3. Remueve vinculaciones técnicas en detalle_pedido, citas y pedidos.
     * 4. Elimina la cuenta principal de la tabla USUARIOS.
     * Todo el proceso se ejecuta bajo una transacción SQL para garantizar consistencia.
     * 
     * @param userId ID del usuario a eliminar.
     * @return true si el usuario y sus dependencias fueron removidos con éxito.
     * @throws Exception Si el usuario tiene dependencias críticas o si ocurre un error transaccional.
     */
    public boolean eliminarUsuarioSeguro(int userId) throws Exception {
        System.out.println("DEBUG: Iniciando eliminación segura del usuario " + userId);
        
        // Paso 1: Auditoría previa de viabilidad de borrado
        Map<String, Object> validation = canDeleteUser(userId);
        System.out.println("DEBUG: Validación - canDelete: " + validation.get("canDelete") + ", reason: " + validation.get("reason"));
        
        if (!(Boolean) validation.get("canDelete")) {
            System.out.println("DEBUG: Bloqueado por validación: " + validation.get("reason"));
            throw new Exception("NO_SE_PUEDE_ELIMINAR: " + validation.get("reason"));
        }
        
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Inicio de bloque transaccional
            System.out.println("DEBUG: Conexión obtenida y autoCommit=false");
            
            // Paso 2: Limpieza de dependencias directas y seguras (Teléfonos)
            System.out.println("DEBUG: Eliminando teléfonos del usuario " + userId);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM TELEFONOS WHERE user_id = ?")) {
                ps.setInt(1, userId);
                int telefonosEliminados = ps.executeUpdate();
                System.out.println("DEBUG: Teléfonos eliminados: " + telefonosEliminados);
            }
            
            // Paso 3: Remoción de dependencias con restricciones de Foreign Key (Orden jerárquico)
            
            // A. Eliminar detalle_pedido (Hijo de pedidos)
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
            
            // B. Eliminar citas (Hijo de pedidos)
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
            
            // C. Eliminar pedidos (Una vez liberado de sus hijos técnicos)
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
            
            // Paso 4: Remoción final del registro de usuario
            // REGLA: Solo se permite la auto-eliminación o eliminación de clientes (rol_id = 2)
            System.out.println("DEBUG: Eliminando usuario principal " + userId);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM USUARIOS WHERE user_id = ? AND rol_id = 2")) {
                ps.setInt(1, userId);
                int filas = ps.executeUpdate();
                System.out.println("DEBUG: Filas de usuario eliminadas: " + filas);
                
                if (filas > 0) {
                    con.commit(); // Éxito total: Consolidación de la transacción
                    System.out.println("DEBUG: Commit exitoso - usuario eliminado");
                    return true;
                } else {
                    con.rollback(); // Fallo en el paso final: Reversión completa
                    System.out.println("DEBUG: Rollback - usuario no encontrado o no es cliente");
                    throw new Exception("Usuario no encontrado o no es cliente");
                }
            } catch (SQLException e) {
                System.out.println("ERROR en DELETE de usuario: " + e.getMessage());
                throw e;
            }
            
        } catch (SQLException e) {
            // Manejo de errores de base de datos y rollback de seguridad
            if (con != null) {
                con.rollback();
                System.out.println("DEBUG: Rollback por SQLException: " + e.getMessage());
            }
            
            // Identificación de errores de restricción de integridad (FK Constraints)
            if (e.getErrorCode() == 1451 || e.getErrorCode() == 1217 || e.getErrorCode() == 1216) {
                throw new Exception("NO_SE_PUEDE_ELIMINAR_TIENE_DEPENDENCIAS");
            }
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        } finally {
            // Liberación controlada de la conexión al pool
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
