/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package controller;

/**
 *
 * @author Propietario
 */

import dao.UsuarioDAO;
import modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/ControllerUser")
public class ControllerUser extends HttpServlet {

    UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recibir datos del formulario (los 'name' del JSP)
        String nom = request.getParameter("txtNombre");
        String em = request.getParameter("txtEmail");
        String ps = request.getParameter("txtPass");
        String dir = request.getParameter("txtDireccion");
        int rol = Integer.parseInt(request.getParameter("txtRol"));

        // 2. Crear el objeto modelo
        Usuario u = new Usuario(em, ps, nom, dir, rol);

        // 3. Intentar guardar
        boolean exito = dao.insertar(u);

        // 4. Responder al usuario
        if (exito) {
            response.getWriter().println("<h1>Usuario guardado correctamente en la BD!</h1>");
            response.getWriter().println("<a href='registro.jsp'>Volver</a>");
        } else {
            response.getWriter().println("<h1>Error al guardar. Revisa la consola de NetBeans.</h1>");
        }
    }
}
