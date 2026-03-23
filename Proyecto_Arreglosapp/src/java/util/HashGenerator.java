/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Generar y verificar hashes de contraseñas utilizando la librería BCrypt para asegurar la autenticación.
 */
package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Esta clase proporciona una herramienta para crear hashes seguros y probar la validación de credenciales.
 */
public class HashGenerator {
    /**
     * Punto de entrada principal para ejecutar la generación de un hash de prueba.
     * 
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        // Contraseña de ejemplo para la generación del hash
        String password = "admin123";
        // Genera el hash utilizando una sal aleatoria
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        
        System.out.println("Contraseña: " + password);
        System.out.println("Hash generado: " + hash);
        System.out.println();
        
        // Muestra un ejemplo de sentencia SQL para insertar manualmente un administrador
        System.out.println("SQL para insertar:");
        System.out.println("INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) ");
        System.out.println("VALUES (");
        System.out.println("    'admin@arreglosapp.com',");
        System.out.println("    '" + hash + "',");
        System.out.println("    'Administrador del Sistema',");
        System.out.println("    'Oficina Principal',");
        System.out.println("    1");
        System.out.println(");");
        
        // Realiza una prueba de validación comparando la contraseña plana con el hash generado
        boolean verifica = BCrypt.checkpw(password, hash);
        System.out.println();
        System.out.println("Verificación del hash: " + (verifica ? "CORRECTO" : "INCORRECTO"));
    }
}
