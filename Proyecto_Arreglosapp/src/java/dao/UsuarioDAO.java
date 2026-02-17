/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dao;

import config.ConectionDB; // Tu clase de conexión corregida
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean insertar(Usuario user) throws Exception {
        String sql = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConectionDB.getConexion()) {
            if (con == null) {
                throw new Exception("ERROR: La conexión con la BD es nula. Revisa tu clase ConectionDB y el Driver MySQL.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getNombre());
                ps.setString(4, user.getDireccion());
                ps.setInt(5, user.getRolId());

                int filas = ps.executeUpdate();
                return filas > 0;
            }
        } catch (SQLException e) {
            // Esto atrapará errores de nombres de columnas o llaves foráneas
            throw new Exception("ERROR SQL (" + e.getErrorCode() + "): " + e.getMessage());
        }
    }
}