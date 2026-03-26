/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: HashGenerator.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Utilidad de criptografía simétrica (BCrypt).
 *               Proporciona herramientas para la generación de hashes de 
 *               contraseñas y scripts SQL de aprovisionamiento, asegurando 
 *               la integridad de las credenciales en la capa de persistencia.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase utilitaria para la gestión de seguridad de contraseñas.
 * Facilita la creación de hashes robustos y la generación de comandos SQL para administración.
 */
public class HashGenerator {
    
    /**
     * Ejecuta una rutina de prueba para la generación y validación de credenciales.
     * Útil para crear la cuenta maestra inicial fuera del entorno web.
     * 
     * @param args Argumentos de sistema (sin uso actual).
     */
    public static void main(String[] args) {
        // Definición de credenciales semilla
        String password = "admin123";
        // Derivación de clave mediante BCrypt con sal automática (CostFactor por defecto)
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        
        System.out.println("--- GENERADOR DE SEGURIDAD ARREGLOSAPP ---");
        System.out.println("Clave Plana: " + password);
        System.out.println("Hash Destino: " + hash);
        System.out.println();
        
        // Generación de script SQL desacoplado para despliegue manual
        System.out.println("SCRIPT DE INSERCIÓN MAESTRA (SQL):");
        System.out.println("INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) ");
        System.out.println("VALUES (");
        System.out.println("    'admin@arreglosapp.com',");
        System.out.println("    '" + hash + "',");
        System.out.println("    'Administrador del Sistema',");
        System.out.println("    'Oficina Principal',");
        System.out.println("    1");
        System.out.println(");");
        
        // Prueba de Consistencia: Verificación cruzada del hash generado
        boolean verifica = BCrypt.checkpw(password, hash);
        System.out.println();
        System.out.println("ESTADO DE VERIFICACIÓN: " + (verifica ? "EXITOSO - INTEGRIDAD CONFIRMADA" : "FALLIDO - ERROR DE CIFRADO"));
    }
}
