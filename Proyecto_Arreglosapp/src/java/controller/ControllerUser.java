package controller;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/UsuarioServlet")
public class ControllerUser extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre    = request.getParameter("txtNombre");
        String email     = request.getParameter("txtEmail");
        String pass      = request.getParameter("txtPassword");
        String direccion = request.getParameter("txtDireccion");
        String telefono  = request.getParameter("txtTelefono");

        // Validaciones backend
        if (nombre == null || nombre.trim().isEmpty() ||
            email  == null || email.trim().isEmpty()  ||
            pass   == null || pass.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/Public/auth/registrarse.jsp?msg=camposVacios");
            return;
        }

        if (pass.length() < 6) {
            response.sendRedirect(request.getContextPath() + "/Public/auth/registrarse.jsp?msg=passCorta");
            return;
        }

        // Hashear contraseña
        String passwordHash = BCrypt.hashpw(pass, BCrypt.gensalt());

        Usuario user = new Usuario();
        user.setNombre(nombre.trim());
        user.setEmail(email.trim());
        user.setPassword(passwordHash);
        user.setDireccion(direccion != null ? direccion.trim() : "");

        UsuarioDAO dao = new UsuarioDAO();
        try {
            if (dao.registrarUsuarioCompleto(user, telefono)) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?msg=exito");
            } else {
                response.sendRedirect(request.getContextPath() + "/Public/auth/registrarse.jsp?msg=error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry")) {
                response.sendRedirect(request.getContextPath() + "/Public/auth/registrarse.jsp?msg=emailDuplicado");
            } else {
                response.sendRedirect(request.getContextPath() + "/Public/auth/registrarse.jsp?msg=error");
            }
        }
    }
}