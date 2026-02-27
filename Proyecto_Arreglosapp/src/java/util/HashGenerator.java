package util;

import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        
        System.out.println("Contraseña: " + password);
        System.out.println("Hash generado: " + hash);
        System.out.println();
        System.out.println("SQL para insertar:");
        System.out.println("INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id) ");
        System.out.println("VALUES (");
        System.out.println("    'admin@arreglosapp.com',");
        System.out.println("    '" + hash + "',");
        System.out.println("    'Administrador del Sistema',");
        System.out.println("    'Oficina Principal',");
        System.out.println("    1");
        System.out.println(");");
        
        // Verificar que el hash funciona
        boolean verifica = BCrypt.checkpw(password, hash);
        System.out.println();
        System.out.println("Verificación del hash: " + (verifica ? "CORRECTO" : "INCORRECTO"));
    }
}
