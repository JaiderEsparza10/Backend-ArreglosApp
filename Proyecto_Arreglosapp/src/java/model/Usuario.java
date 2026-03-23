/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Definir la entidad Usuario para gestionar la autenticación y los perfiles del sistema.
 */
package model;

/**
 * Representa a un usuario (Cliente o Administrador) dentro de la plataforma.
 */
public class Usuario {
    private int id;
    private String email;
    private String password; // Almacena el Hash BCrypt
    private String nombre;
    private String direccion;
    private int rolId; // 1: Admin, 2: Cliente

    public Usuario() {
    }

    /**
     * Constructor de inicialización para registro.
     */
    public Usuario(String email, String password, String nombre, String direccion, int rolId) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.direccion = direccion;
        this.rolId = rolId;
    }

    // Acceso encapsulado a los atributos del perfil
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }
}