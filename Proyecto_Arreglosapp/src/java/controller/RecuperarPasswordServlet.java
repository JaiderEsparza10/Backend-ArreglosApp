/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: RecuperarPasswordServlet.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Gestor de flujo de recuperación de credenciales (Restauración).
 *               Implementa validación de identidad por fases mediante 
 *               verificación de email y actualización atómica de contraseñas.
 * ══════════════════════════════════════════════════════════════════════════════
 */
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
 * Controlador (Servlet) que gestiona el proceso de olvidó de contraseña.
 * Permite a los usuarios restaurar el acceso tras validar su correo electrónico.
 */
@WebServlet("/RecuperarPasswordServlet")
public class RecuperarPasswordServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * Canaliza solicitudes GET hacia el procesador unificado de negocio.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Procesa las etapas de recuperación: 'verificarEmail' y 'cambiarPassword'.
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

        // ─── ETAPA 1: VALIDACIÓN DE EXISTENCIA POR CORREO ──────────────
        if ("verificarEmail".equals(accion)) {
            try {
                String email = request.getParameter("email");

                if (email == null || email.trim().isEmpty()) {
                    session.setAttribute("errorRecuperar", "Ingresa tu correo electrónico");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                // Consulta de integridad en el repositorio de usuarios
                boolean existe = usuarioDAO.existeEmail(email.trim());

                if (existe) {
                    // Pre-autenticación por email para la fase de cambio
                    session.setAttribute("emailRecuperar", email.trim());
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                } else {
                    session.setAttribute("errorRecuperar", "No existe una cuenta con ese correo");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                }

            } catch (Exception e) {
                session.setAttribute("errorRecuperar", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
            }

        // ─── ETAPA 2: REASIGNACIÓN DE CREDENCIALES (CAMBIO FINAL) ──────
        } else if ("cambiarPassword".equals(accion)) {
            try {
                String email = (String) session.getAttribute("emailRecuperar");
                String passwordNueva = request.getParameter("passwordNueva");
                String passwordConfirm = request.getParameter("passwordConfirmar");

                if (email == null) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
                    return;
                }

                // Validación de coincidencia y vacío
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

                // RNF: Validación de políticas de seguridad (Complejidad)
                if (!passwordNueva.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    session.setAttribute("errorRecuperar", "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                    return;
                }

                // Aplicación de cambio final con cifrado atómico
                boolean cambiada = usuarioDAO.actualizarPassword(email, passwordNueva);

                if (cambiada) {
                    session.removeAttribute("emailRecuperar");
                    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp?passwordRecuperada=1");
                } else {
                    session.setAttribute("errorRecuperar", "No se pudo actualizar");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
                }

            } catch (Exception e) {
                session.setAttribute("errorRecuperar", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp?paso=2");
            }

        } else {
            response.sendRedirect("/Proyecto_Arreglosapp/Public/auth/recuperar-contrasena.jsp");
        }
    }
}