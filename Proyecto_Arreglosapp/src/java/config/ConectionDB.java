/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Centralizar la configuración y gestión de la conexión a la base de datos MySQL.
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Esta clase se encarga de establecer el enlace con el motor de base de datos.
 */
public class ConectionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/PROYECTO_ARREGLOSAPP";
    private static final String USER = "root";
    private static final String PASSWD = "J1095581627";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Establece y retorna una conexión activa con la base de datos utilizando el
     * controlador JDBC.
     * 
     * @return El objeto Connection si tiene éxito, o null en caso de error.
     */
    public static Connection getConexion() throws SQLException {
        Connection conect = null;
        try {
            Class.forName(DRIVER);
            conect = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("Conexión Exitosa");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver no encontrado: " + e.getMessage());
            throw new SQLException("Error de driver JDBC: " + e.getMessage());
        }
        return conect;
    }
}