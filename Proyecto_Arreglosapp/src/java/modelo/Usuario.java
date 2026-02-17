/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package modelo;

/**
 *
 * @author Propietario
 */
public class Usuario {
    // Atributos privados (Encapsulamiento)
    private int id;
    private String email;
    private String password;
    private String nombre;
    private String direccion;
    private int rolId;

    // 1. Constructor vacío (Obligatorio para frameworks y Reflection)
    public Usuario() {
    }

    // 2. Constructor para INSERTAR (Sin el ID, porque la DB lo genera solo)
    public Usuario(String email, String password, String nombre, String direccion, int rolId) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.direccion = direccion;
        this.rolId = rolId;
    }

    // 3. Getters y Setters (Para acceder a los datos desde el DAO o JSP)
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