/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package controller;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt; 

@WebServlet("/ControllerUser")
public class ControllerUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // 1. Captura de datos originales del JSP
            String em = request.getParameter("txtEmail");
            String passwordPlano = request.getParameter("txtPass"); // Contraseña tal cual la escribió el usuario
            String nom = request.getParameter("txtNombre");
            String dir = request.getParameter("txtDireccion");
            int rol = Integer.parseInt(request.getParameter("txtRol"));

            // 2. ENCRIPTACIÓN: 
            // BCrypt.hashpw toma la clave y genera una sal (salt) automática.
            // El resultado es un string de 60 caracteres que es imposible de "revertir".
            String passwordEncriptado = BCrypt.hashpw(passwordPlano, BCrypt.gensalt());

            // 3. Crear el modelo con la contraseña YA encriptada
            Usuario user = new Usuario(em, passwordEncriptado, nom, dir, rol);
            UsuarioDAO dao = new UsuarioDAO();

            // 4. Guardar en la base de datos
            if (dao.insertar(user)) {
                out.println("<script>");
                out.println("alert('✅ Usuario registrado con seguridad (BCrypt)!');");
                out.println("window.location='registro.jsp';");
                out.println("</script>");
            }
            
        } catch (Exception e) {
            out.println("<h2 style='color:red'>Error en el registro seguro:</h2>");
            out.println("<p style='background:#fee; padding:10px; border:1px solid red;'>" + e.getMessage() + "</p>");
            out.println("<a href='registro.jsp'>Regresar</a>");
        }
    }
}