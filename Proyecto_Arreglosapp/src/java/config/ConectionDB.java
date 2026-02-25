/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/PROYECTO_ARREGLOSAPP";
    private static final String USER = "root";
    private static final String PASSWD = "J1095581627";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConexion() {
        Connection conect = null;
        try {
            Class.forName(DRIVER);
            conect = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("Conexión Exitosa");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de Conexión: " + e.getMessage());
        }
        return conect;
    }
}