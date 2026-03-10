package controller;

import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Este servlet gestiona el proceso de recuperación de contraseña de los
 * usuarios.
 * Permite verificar el correo electrónico y establecer una nueva contraseña de
 * acceso.
 */
@WebServlet("/RecuperarPasswordServlet")
public class RecuperarPasswordServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    /**
     * Inicializa el DAO de usuarios para realizar búsquedas y actualizaciones de
     * contraseñas.
     */
    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * Redirige las solicitudes GET al método POST para unificar el manejo de la
     * lógica.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Procesa las solicitudes POST para las distintas etapas de recuperación de
     * contraseña.
     * Maneja las acciones de verificación de email y actualización de la nueva
     * contraseña.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String accion = request.getParameter("accion");
        if (accion != null) {
            accion = accion.trim();
        }

        System.out.println("=== RecuperarPasswordServlet ===");
        System.out.println("Metodo: " + request.getMethod());
        System.out.println("Accion recibida: [" + accion + "]");
        System.out.println("Email param: " + request.getParameter("email"));
        System.out.println("Es verificarEmail: " + "verificarEmail".equals(accion));
        System.out.println("Es cambiarPassword: " + "cambiarPassword".equals(accion));

        if (accion == null) {
            System.out.println("Accion es NULL, redirigiendo a recuperar");
            response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
            return;
        }

        if ("verificarEmail".equals(accion)) {
            System.out.println("Entrando a verificarEmail...");
            try {
                String email = request.getParameter("email");

                if (email == null || email.trim().isEmpty()) {
                    session.setAttribute("errorRecuperar", "Ingresa tu correo electrónico");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                System.out.println("Verificando email: " + email.trim());
                boolean existe = usuarioDAO.existeEmail(email.trim());
                System.out.println("Email existe: " + existe);

                if (existe) {
                    session.setAttribute("emailRecuperar", email.trim());
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                } else {
                    session.setAttribute("errorRecuperar", "No existe una cuenta con ese correo");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                }

            } catch (Exception e) {
                System.out.println("ERROR en verificarEmail: " + e.getMessage());
                e.printStackTrace();
                session.setAttribute("errorRecuperar", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
            }

        } else if ("cambiarPassword".equals(accion)) {
            System.out.println("Entrando a cambiarPassword...");
            try {
                String email = (String) session.getAttribute("emailRecuperar");
                String passwordNueva = request.getParameter("passwordNueva");
                String passwordConfirm = request.getParameter("passwordConfirmar");

                System.out.println("Email sesion: " + email);

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

                if (!passwordNueva.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    session.setAttribute("errorRecuperar", "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                    return;
                }

                boolean cambiada = usuarioDAO.actualizarPassword(email, passwordNueva);
                System.out.println("Password cambiada: " + cambiada);

                if (cambiada) {
                    session.removeAttribute("emailRecuperar");
                    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp?passwordRecuperada=1");
                } else {
                    session.setAttribute("errorRecuperar", "No se pudo actualizar la contraseña");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                }

            } catch (Exception e) {
                System.out.println("ERROR en cambiarPassword: " + e.getMessage());
                e.printStackTrace();
                session.setAttribute("errorRecuperar", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
            }

        } else {
            System.out.println("Accion no reconocida: [" + accion + "], redirigiendo");
            response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
        }
    }
}