/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: ConectionDB.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Motor de persistencia relacional (MySQL).
 *               Centraliza la orquestación de sesiones JDBC, gestionando 
 *               el ciclo de vida de la conexión y la carga del driver nativo
 *               para asegurar la integridad transaccional.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de configuración para la conectividad con el motor de base de datos.
 * Proporciona el punto único de acceso (Singleton-like) a la instancia de conexión.
 */
public class ConectionDB {
    
    // Parámetros de conectividad regional (Localhost)
    private static final String URL = "jdbc:mysql://localhost:3306/PROYECTO_ARREGLOSAPP";
    private static final String USER = "root";
    private static final String PASSWD = "J1095581627";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Establece y retorna una conexión activa con el servidor MySQL.
     * Implementa la carga dinámica del driver JDBC para compatibilidad en caliente.
     * 
     * @return El objeto Connection autenticado y listo para operaciones SQL.
     * @throws SQLException Si falla la autenticación o la resolución de red.
     */
    public static Connection getConexion() throws SQLException {
        Connection conect = null;
        try {
            // Carga explícita del driver para asegurar registro en el DriverManager
            Class.forName(DRIVER);
            conect = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("DEBUG: Conexión establecida con éxito al motor SQL.");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Driver JDBC no localizado en el classpath: " + e.getMessage());
            throw new SQLException("Error de driver JDBC: " + e.getMessage());
        }
        return conect;
    }
}