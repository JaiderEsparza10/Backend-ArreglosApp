package controller;

import dao.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/NuevaContrasenaServlet")
public class NuevaContrasenaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nuevaPassword = request.getParameter("nuevaPassword");
        String confirmarPassword = request.getParameter("confirmarPassword");
        String contextPath = request.getContextPath();
        String rutaLogin = contextPath + "/index.jsp";

        // Validar campos vacíos
        if (nuevaPassword == null || confirmarPassword == null ||
            nuevaPassword.trim().isEmpty() || confirmarPassword.trim().isEmpty()) {
            response.sendRedirect(contextPath + "/Public/auth/nueva-contrasena.jsp?msg=camposVacios");
            return;
        }

        // Validar que las contraseñas coincidan
        if (!nuevaPassword.equals(confirmarPassword)) {
            response.sendRedirect(contextPath + "/Public/auth/nueva-contrasena.jsp?msg=contrasenasNoCoinciden");
            return;
        }

        // Validar formato de contraseña
        String regexPass = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!nuevaPassword.matches(regexPass)) {
            response.sendRedirect(contextPath + "/Public/auth/nueva-contrasena.jsp?msg=contrasenaInvalida");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(contextPath + "/Public/auth/recuperar-contrasena.jsp?msg=sesionExpirada");
            return;
        }

        String emailRecuperacion = (String) session.getAttribute("emailRecuperacion");
        Boolean codigoVerificado = (Boolean) session.getAttribute("codigoVerificado");

        if (emailRecuperacion == null || codigoVerificado == null || !codigoVerificado) {
            response.sendRedirect(contextPath + "/Public/auth/recuperar-contrasena.jsp?msg=accesoDenegado");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        try {
            // Actualizar contraseña
            if (dao.actualizarPassword(emailRecuperacion, nuevaPassword)) {
                // Limpiar sesión de recuperación
                session.removeAttribute("codigoRecuperacion");
                session.removeAttribute("emailRecuperacion");
                session.removeAttribute("tiempoCodigo");
                session.removeAttribute("codigoVerificado");

                response.sendRedirect(rutaLogin + "?msg=contrasenaActualizada");
            } else {
                response.sendRedirect(contextPath + "/Public/auth/nueva-contrasena.jsp?msg=errorActualizar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/Public/auth/nueva-contrasena.jsp?msg=errorServidor");
        }
    }
}
