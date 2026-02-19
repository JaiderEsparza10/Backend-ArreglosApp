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

@WebServlet("/UsuarioServlet")
public class ControllerUser extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Capturar datos del formulario
        String nombre = request.getParameter("txtNombre");
        String email = request.getParameter("txtEmail");
        String pass = request.getParameter("txtPassword");
        String direccion = request.getParameter("txtDireccion");
        String telefono = request.getParameter("txtTelefono");

        // Crear objeto modelo
        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(pass);
        user.setDireccion(direccion);

        UsuarioDAO dao = new UsuarioDAO();
        try {
            if (dao.registrarUsuarioCompleto(user, telefono)) {
                response.sendRedirect("index.jsp?msg=exito");
            } else {
                response.sendRedirect("registro.jsp?msg=error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("registro.jsp?msg=" + e.getMessage());
        }
    }
}