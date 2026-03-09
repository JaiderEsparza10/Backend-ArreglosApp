package controller;

import dao.UsuarioDAO;
import util.JWTUtil;
import java.io.IOException;

import model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Este servlet maneja la autenticación de usuarios, incluyendo el inicio y
 * cierre de sesión.
 */
@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {

    /**
     * Procesa las solicitudes POST para el inicio de sesión.
     * Valida las credenciales del usuario, genera un token JWT y establece la
     * sesión.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String contextPath = request.getContextPath();
        String rutaLogin = contextPath + "/index.jsp";

        // Validar campos vacíos
        if (email == null || password == null ||
                email.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendRedirect(rutaLogin + "?msg=camposVacios&email=" +
                    java.net.URLEncoder.encode(email != null ? email.trim() : "", "UTF-8"));
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        try {
            // Autenticar usuario
            Usuario usuario = dao.autenticarUsuario(email.trim(), password);

            // Generar token
            String token = JWTUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getId(),
                    usuario.getRolId(),
                    usuario.getNombre());

            if (token == null) {
                response.sendRedirect(rutaLogin + "?msg=errorServidor&email=" +
                        java.net.URLEncoder.encode(email.trim(), "UTF-8"));
                return;
            }

            // Guardar token en sesión
            HttpSession session = request.getSession();
            session.setAttribute("token", token);
            session.setAttribute("usuario", usuario);

            // Redirigir según el rol
            String redirectUrl = "";
            switch (usuario.getRolId()) {
                case 1: // ADMINISTRADOR
                    redirectUrl = contextPath + "/Public/admin/administrador-dashboard.jsp";
                    break;
                case 2: // CLIENTE
                    redirectUrl = contextPath + "/Public/client/pagina-principal.jsp";
                    break;
                default:
                    redirectUrl = contextPath + "/index.jsp";
                    break;
            }

            response.sendRedirect(redirectUrl + "?msg=exitoLogin");

        } catch (Exception e) {
            System.out.println("=== ERROR AuthServlet ===");
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();

            String errorMsg = e.getMessage();
            String redirectMsg = "";

            if ("EMAIL_NOT_FOUND".equals(errorMsg)) {
                redirectMsg = "emailNoExiste";
            } else if ("PASSWORD_INCORRECT".equals(errorMsg)) {
                redirectMsg = "passwordIncorrecta";
            } else {
                redirectMsg = "errorServidor";
            }

            response.sendRedirect(rutaLogin + "?msg=" + redirectMsg + "&email=" +
                    java.net.URLEncoder.encode(email.trim(), "UTF-8"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Para cerrar sesión
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/index.jsp?msg=sesionCerrada");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
