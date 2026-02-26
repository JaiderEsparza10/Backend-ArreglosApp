package controller;

import dao.UsuarioDAO;
import model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/UsuarioServlet")
public class ControllerUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre    = request.getParameter("txtNombre");
        String email     = request.getParameter("txtEmail");
        String pass      = request.getParameter("txtPassword");
        String direccion = request.getParameter("txtDireccion");
        String telefono  = request.getParameter("txtTelefono");

        // ← Ruta base del contexto para los redirects
        String contextPath = request.getContextPath();
        String rutaRegistro = contextPath + "/Public/auth/registrarse.jsp";
        String rutaLogin    = contextPath + "/index.jsp";

        // Validación campos vacíos
        if (nombre == null || email == null || pass == null ||
            nombre.trim().isEmpty() || email.trim().isEmpty() || pass.trim().isEmpty()) {
            response.sendRedirect(rutaRegistro + "?msg=camposVacios");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        try {
            // Verificar email duplicado
            if (dao.existeEmail(email.trim())) {
                response.sendRedirect(rutaRegistro + "?msg=emailExiste&nombre=" + 
                    java.net.URLEncoder.encode(nombre.trim(), "UTF-8") + 
                    "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8") +
                    "&telefono=" + java.net.URLEncoder.encode(telefono != null ? telefono.trim() : "", "UTF-8"));
                return;
            }

            // Verificar teléfono duplicado
            if (telefono != null && !telefono.trim().isEmpty()) {
                if (dao.existeTelefono(telefono.trim())) {
                    response.sendRedirect(rutaRegistro + "?msg=telefonoExiste&nombre=" + 
                        java.net.URLEncoder.encode(nombre.trim(), "UTF-8") + 
                        "&email=" + java.net.URLEncoder.encode(email.trim(), "UTF-8") +
                        "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8"));
                    return;
                }
            }

            // Crear objeto usuario
            Usuario user = new Usuario();
            user.setNombre(nombre.trim());
            user.setEmail(email.trim());
            user.setPassword(pass); // Se hashea en el DAO
            user.setDireccion(direccion != null ? direccion.trim() : "");

            if (dao.registrarUsuarioCompleto(user, telefono)) {
                response.sendRedirect(rutaLogin + "?msg=exitoRegistro");
            } else {
                response.sendRedirect(rutaRegistro + "?msg=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(rutaRegistro + "?msg=errorServidor");
        }
    }
}