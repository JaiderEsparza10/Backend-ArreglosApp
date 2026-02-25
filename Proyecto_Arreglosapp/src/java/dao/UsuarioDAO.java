package dao;

import config.ConectionDB;
import modelo.Usuario;
import java.sql.*;

public class UsuarioDAO {

    public boolean registrarUsuarioCompleto(Usuario user, String telefono) throws Exception {
        String sqlUser = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) VALUES (?, ?, ?, ?, ?)";
        String sqlTel = "INSERT INTO TELEFONOS (user_id, telefono_numero, telefono_es_principal) VALUES (?, ?, ?)";
        
        Connection con = null;
        try {
            con = ConectionDB.getConexion();
            con.setAutoCommit(false); // Iniciamos transacción

            int generatedUserId = -1;

            // 1. Insertar Usuario
            try (PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, user.getEmail());
                psUser.setString(2, user.getPassword()); // Nota: En producción usa Hash
                psUser.setString(3, user.getNombre());
                psUser.setString(4, user.getDireccion());
                psUser.setInt(5, 2); // 2 es el rol de 'CLIENTE' según tu SQL
                psUser.executeUpdate();

                var rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    generatedUserId = rs.getInt(1);
                }
            }

            // 2. Insertar Teléfono
            try (PreparedStatement psTel = con.prepareStatement(sqlTel)) {
                psTel.setInt(1, generatedUserId);
                psTel.setString(2, telefono);
                psTel.setBoolean(3, true); // Es el principal
                psTel.executeUpdate();
            }

            con.commit(); // Guardar todo
            return true;
        } catch (SQLException e) {
            if (con != null) con.rollback(); // Cancelar todo si hay error
            throw new Exception("Error al registrar: " + e.getMessage());
        } finally {
            if (con != null) con.close();
        }
    }
}