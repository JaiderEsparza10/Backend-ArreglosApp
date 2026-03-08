package controller;

import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/RecuperarPasswordServlet")
public class RecuperarPasswordServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    // ─── ACEPTAR TAMBIÉN GET ──────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String accion = request.getParameter("accion");

        System.out.println("=== RecuperarPasswordServlet ===");
        System.out.println("Metodo: " + request.getMethod());
        System.out.println("Accion: " + accion);
        System.out.println("Email param: " + request.getParameter("email"));

        if (accion == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
            return;
        }

        if ("verificarEmail".equals(accion)) {
            try {
                String email = request.getParameter("email");

                if (email == null || email.trim().isEmpty()) {
                    session.setAttribute("errorRecuperar", "Ingresa tu correo electrónico");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                boolean existe = usuarioDAO.existeEmail(email.trim());

                if (existe) {
                    session.setAttribute("emailRecuperar", email.trim());
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                } else {
                    session.setAttribute("errorRecuperar", "No existe una cuenta con ese correo");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                }

            } catch (Exception e) {
                System.out.println("Error verificarEmail: " + e.getMessage());
                session.setAttribute("errorRecuperar", "Error al verificar el correo");
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
            }

        } else if ("cambiarPassword".equals(accion)) {
            try {
                String email = (String) session.getAttribute("emailRecuperar");
                String passwordNueva = request.getParameter("passwordNueva");
                String passwordConfirm = request.getParameter("passwordConfirmar");

                System.out.println("Email sesion: " + email);
                System.out.println("Password nueva: " + passwordNueva);

                if (email == null) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                if (passwordNueva == null || passwordNueva.trim().isEmpty()) {
                    session.setAttribute("errorRecuperar", "La contraseña no puede estar vacía");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                    return;
                }

                if (!passwordNueva.equals(passwordConfirm)) {
                    session.setAttribute("errorRecuperar", "Las contraseñas no coinciden");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                    return;
                }

                if (passwordNueva.length() < 6) {
                    session.setAttribute("errorRecuperar", "La contraseña debe tener al menos 6 caracteres");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                    return;
                }

                boolean cambiada = usuarioDAO.actualizarPassword(email, passwordNueva);

                if (cambiada) {
                    session.removeAttribute("emailRecuperar");
                    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp?passwordRecuperada=1");
                } else {
                    session.setAttribute("errorRecuperar", "No se pudo actualizar la contraseña");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                }

            } catch (Exception e) {
                System.out.println("Error cambiarPassword: " + e.getMessage());
                session.setAttribute("errorRecuperar", "Error al cambiar la contraseña");
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
            }

        } else {
            System.out.println("Accion no reconocida: " + accion);
            response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
        }
    }
}