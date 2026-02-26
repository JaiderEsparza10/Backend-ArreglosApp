package util;

import dao.UsuarioDAO;

/**
 * Clase utilitaria para crear el administrador por defecto
 * Ejecutar este método una sola vez para crear el admin
 */
public class CrearAdmin {
    
    public static void main(String[] args) {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            // Verificar si ya existe un administrador
            if (!usuarioDAO.existeAdministrador()) {
                // Crear administrador por defecto
                boolean creado = usuarioDAO.crearAdministradorPorDefecto();
                
                if (creado) {
                    System.out.println("=== ADMINISTRADOR POR DEFECTO CREADO ===");
                    System.out.println("Email: admin@arreglosapp.com");
                    System.out.println("Password: admin123");
                    System.out.println("=====================================");
                } else {
                    System.out.println("No se pudo crear el administrador por defecto");
                }
            } else {
                System.out.println("El administrador ya existe en el sistema");
            }
            
        } catch (Exception e) {
            System.err.println("Error al crear administrador: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
