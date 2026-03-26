/**
 * Nombre del archivo: Usuario.java
 * Descripción breve: Clase que representa la entidad de Usuario en el sistema ArreglosApp.
 * Author: Jaider Andres Esparza — Antigravity
 * Fecha de documentación: 25 de marzo de 2026
 * Versión: 1.0
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Definir la entidad Usuario para gestionar la autenticación y los perfiles del sistema.
 */
package model;

/**
 * Representa a un usuario (Cliente o Administrador) dentro de la plataforma.
 * Esta clase modela los atributos necesarios para la gestión de seguridad,
 * identificación y contacto de cada usuario en ArreglosApp.
 */
public class Usuario {
    // Identificador único del usuario autogenerado en la base de datos
    private int id;
    // Correo electrónico personal que actúa como credencial de acceso única
    private String email;
    // Almacena la contraseña hasheada (Hash BCrypt) para garantizar la seguridad
    private String password; // Almacena el Hash BCrypt
    // Nombre completo del usuario para visualización en el sistema
    private String nombre;
    // Dirección física de residencia o envío asociada al cliente
    private String direccion;
    // Identificador del nivel de acceso: 1 indica Administrador, 2 indica Cliente
    private int rolId; // 1: Admin, 2: Cliente

    /**
     * Constructor por defecto requerido para marcos de trabajo de persistencia
     * y serialización que instancian objetos antes de poblar sus atributos.
     */
    public Usuario() {
    }

    /**
     * Constructor de inicialización para procesos de registro de nuevos usuarios.
     * 
     * @param email     Correo electrónico que el usuario usará para identificarse.
     * @param password  Contraseña ya procesada mediante un algoritmo de hashing.
     * @param nombre    Nombre y apellidos completos de la persona.
     * @param direccion Ubicación física para la gestión de servicios a domicilio.
     * @param rolId     Identificador numérico que define el perfil de permisos.
     */
    public Usuario(String email, String password, String nombre, String direccion, int rolId) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.direccion = direccion;
        this.rolId = rolId;
    }

    // Acceso encapsulado a los atributos del perfil

    /**
     * Obtiene el ID único del usuario.
     * @return Entero con el identificador de base de datos.
     */
    public int getId() { return id; }

    /**
     * Establece el ID único del usuario.
     * @param id Entero con el identificador a asignar.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Obtiene el correo electrónico de acceso.
     * @return Cadena con el email registrado.
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico de acceso.
     * @param email Cadena con el nuevo email.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Obtiene el hash de la contraseña de seguridad.
     * @return Cadena con el hash BCrypt.
     */
    public String getPassword() { return password; }

    /**
     * Establece el hash de la contraseña de seguridad.
     * @param password Cadena con el nuevo hash a almacenar.
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Obtiene el nombre completo del usuario.
     * @return Cadena con el nombre y apellidos.
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre completo del usuario.
     * @param nombre Cadena con el nuevo nombre a asignar.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la dirección física registrada.
     * @return Cadena con la dirección de contacto.
     */
    public String getDireccion() { return direccion; }

    /**
     * Establece la dirección física de contacto.
     * @param direccion Cadena con la nueva dirección.
     */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Obtiene el identificador de rol para control de accesos.
     * @return Entero representing the role (1: Admin, 2: Cliente).
     */
    public int getRolId() { return rolId; }

    /**
     * Establece el rol de acceso al sistema.
     * @param rolId Entero con el nuevo ID de rol (1 o 2).
     */
    public void setRolId(int rolId) { this.rolId = rolId; }
}