import org.mindrot.jbcrypt.BCrypt;

public class test_hash {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        
        System.out.println("Contraseña: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Verificación: " + BCrypt.checkpw(password, hash));
        
        // Generar nuevo hash
        String nuevoHash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Nuevo hash: " + nuevoHash);
        System.out.println("Verificación nuevo: " + BCrypt.checkpw(password, nuevoHash));
    }
}
