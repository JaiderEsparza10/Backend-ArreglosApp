package dao;

import config.ConectionDB;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UsuarioDAO {

    // Verificar si el email ya existe
    public boolean existeEmail(String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM USUARIOS WHERE user_email = ?";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar email: " + e.getMessage());
        }
        return false;
    }

    // Verificar si el teléfono ya existe
    public boolean existeTelefono(String telefono) throws Exception {
        String sql = "SELECT COUNT(*) FROM TELEFONOS WHERE telefono_numero = ?";
        try (Connection con = ConectionDB.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar teléfono: " + e.getMessage());
        }
        return false;
    }

    // Autenticar usuario
    public Usuario autenticarUsuario(String email, String password) throws Exception {
        String sql = "SELECT user_id, user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id "
                   + "FROM USUARIOS WHERE user_email = ?";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("user_password_hash");
                    
                    // Verificar contraseña (texto plano para pruebas)
                    if (password.equals(storedHash)) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("user_id"));
                        usuario.setEmail(rs.getString("user_email"));
                        usuario.setNombre(rs.getString("user_nombre"));
                        usuario.setDireccion(rs.getString("user_ubicacion_direccion"));
                        usuario.setRolId(rs.getInt("rol_id"));
                        return usuario;
                    } 
                    // Si no es texto plano, intentar con BCrypt
                    else if (BCrypt.checkpw(password, storedHash)) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("user_id"));
                        usuario.setEmail(rs.getString("user_email"));
                        usuario.setNombre(rs.getString("user_nombre"));
                        usuario.setDireccion(rs.getString("user_ubicacion_direccion"));
                        usuario.setRolId(rs.getInt("rol_id"));
                        return usuario;
                    } else {
                        // Contraseña incorrecta
                        throw new Exception("PASSWORD_INCORRECT");
                    }
                } else {
                    // Email no existe
                    throw new Exception("EMAIL_NOT_FOUND");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al autenticar usuario: " + e.getMessage());
        }
    }

    // Actualizar contraseña de usuario
    public boolean actualizarPassword(String email, String nuevaPassword) throws Exception {
        String sql = "UPDATE USUARIOS SET user_password_hash = ? WHERE user_email = ?";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Hashear la nueva contraseña
            String passwordHash = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt());
            
            ps.setString(1, passwordHash);
            ps.setString(2, email);
            
            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;
            
        } catch (SQLException e) {
            throw new Exception("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    public boolean registrarUsuarioCompleto(Usuario user, String telefono) throws Exception {
        String sqlUser = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) "
                        + "VALUES (?, ?, ?, ?, ?)";
        String sqlTel  = "INSERT INTO TELEFONOS (user_id, telefono_numero, telefono_es_principal) VALUES (?, ?, ?)";

        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false);

            // ← FIX 1: Hashear la contraseña aquí en el DAO
            String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            int generatedUserId = -1;

            // 1. Insertar usuario
            try (PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, user.getEmail());
                psUser.setString(2, passwordHash); // ← Hash correcto
                psUser.setString(3, user.getNombre());
                psUser.setString(4, user.getDireccion());
                psUser.setInt(5, 2); // rol_id = 2 (CLIENTE)
                psUser.executeUpdate();

                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedUserId = rs.getInt(1);
                    } else {
                        throw new Exception("No se obtuvo el ID del usuario.");
                    }
                }
            }

            // ← FIX 2: Solo insertar teléfono si no viene vacío
            if (telefono != null && !telefono.trim().isEmpty()) {
                try (PreparedStatement psTel = con.prepareStatement(sqlTel)) {
                    psTel.setInt(1, generatedUserId);
                    psTel.setString(2, telefono.trim());
                    psTel.setBoolean(3, true);
                    psTel.executeUpdate();
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw new Exception("Error al registrar: " + e.getMessage());
        } finally {
            if (con != null) con.close();
        }
    }

    // Crear administrador por defecto si no existe
    public boolean crearAdministradorPorDefecto() throws Exception {
        String emailAdmin = "admin@arreglosapp.com";
        String passwordAdmin = "admin123";
        String nombreAdmin = "Administrador";
        String direccionAdmin = "Oficina Principal";
        
        // Verificar si ya existe el administrador
        if (existeEmail(emailAdmin)) {
            return false; // Ya existe
        }
        
        String sql = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Hashear la contraseña del administrador
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
    
    // Verificar si existe administrador
    public boolean existeAdministrador() throws Exception {
        String sql = "SELECT COUNT(*) FROM USUARIOS WHERE rol_id = 1";
        try (Connection con = ConectionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar administrador: " + e.getMessage());
        }
        return false;
    }
}
