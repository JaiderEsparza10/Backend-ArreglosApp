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

    public boolean insertar(Usuario user) {
        // SQL sin el campo user_id porque es AUTO_INCREMENT
        String sql = "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConectionDB.getConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Seteamos los valores en orden (?)
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNombre());
            ps.setString(4, user.getDireccion());
            ps.setInt(5, user.getRolId());

            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0; // Retorna true si se insertó correctamente

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }
}
