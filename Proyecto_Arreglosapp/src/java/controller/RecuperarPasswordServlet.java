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
 * Controlador de Recuperación de Credenciales.
 * RF-04: Recuperar Contraseña.
 * Implementa un flujo de dos pasos: verificación de email y restablecimiento de contraseña.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/RecuperarPasswordServlet")
public class RecuperarPasswordServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización del motor de búsqueda y actualización de usuarios
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * Estandariza solicitudes GET redirigiéndolas al procesador de negocio POST.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Canaliza las etapas del proceso de recuperación basado en el parámetro 'accion'.
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

        // ─── PASO 1: VERIFICACIÓN DE IDENTIDAD POR EMAIL ──────────────
        if ("verificarEmail".equals(accion)) {
            try {
                String email = request.getParameter("email");

                // Validación de entrada obligatoria
                if (email == null || email.trim().isEmpty()) {
                    session.setAttribute("errorRecuperar", "Ingresa tu correo electrónico");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                // Consulta de existencia en la base de datos
                boolean existe = usuarioDAO.existeEmail(email.trim());

                if (existe) {
                    // Almacenamiento temporal en sesión para el siguiente paso del flujo
                    session.setAttribute("emailRecuperar", email.trim());
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                } else {
                    session.setAttribute("errorRecuperar", "No existe una cuenta con ese correo");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                }

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorRecuperar", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
            }

        // ─── PASO 2: ACTUALIZACIÓN DE CREDENCIALES ────────────────────
        } else if ("cambiarPassword".equals(accion)) {
            try {
                // Recuperación del contexto desde la sesión segura
                String email = (String) session.getAttribute("emailRecuperar");
                String passwordNueva = request.getParameter("passwordNueva");
                String passwordConfirm = request.getParameter("passwordConfirmar");

                if (email == null) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                // Validaciones de integridad y coincidencia de contraseñas
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

                // RNF: Cumplimiento de políticas de complejidad (mín. 8 char, uppercase, digits)
                if (!passwordNueva.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    session.setAttribute("errorRecuperar", "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                    return;
                }

                // Persistencia del nuevo Hash BCrypt mediante el motor de usuario
                boolean cambiada = usuarioDAO.actualizarPassword(email, passwordNueva);

                if (cambiada) {
                    // Limpieza de sesión tras éxito en la transacción
                    session.removeAttribute("emailRecuperar");
                    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp?passwordRecuperada=1");
                } else {
                    session.setAttribute("errorRecuperar", "No se pudo actualizar la contraseña");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                }

            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorRecuperar", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
            }

        } else {
            // Manejo de acciones no definidas o malformadas
            response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
        }
    }
}