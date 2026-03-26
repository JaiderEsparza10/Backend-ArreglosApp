/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: AuthServlet.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Motor central de seguridad y autenticación (AAA).
 *               Gestiona el ciclo de vida de la sesión, validación BCrypt 
 *               y emisión de tokens JWT para persistencia de identidad.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package controller;

import dao.UsuarioDAO;
import util.JWTUtil;
import java.io.IOException;

import model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Controlador (Servlet) que gestiona el flujo de autenticación.
 * Procesa inicios de sesión, valida credenciales y liquida sesiones activas.
 */
@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {

    /**
     * Procesa solicitudes POST para validar credenciales de usuario.
     * Este método es el punto de entrada para el inicio de sesión.
     * Implementa seguridad mediante tokens JWT almacenados en la sesión del servidor.
     *
     * @param request El objeto HttpServletRequest que contiene los parámetros de la solicitud (email, password).
     * @param response El objeto HttpServletResponse para enviar la respuesta al cliente (redirecciones).
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Extracción de parámetros de la solicitud
        // Se obtienen el email y la contraseña enviados desde el formulario de login.
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Rutas de redirección comunes
        String contextPath = request.getContextPath();
        String rutaLogin = contextPath + "/index.jsp";

        // 2. Validación de parámetros de entrada
        // Se verifica que los campos de email y contraseña no sean nulos o estén vacíos.
        if (email == null || password == null ||
                email.trim().isEmpty() || password.trim().isEmpty()) {
            // Si los campos están vacíos, se redirige al login con un mensaje de error.
            response.sendRedirect(rutaLogin + "?msg=camposVacios&email=" +
                    java.net.URLEncoder.encode(email != null ? email.trim() : "", "UTF-8"));
            return; // Se detiene la ejecución del método.
        }

        // 3. Instanciación del DAO para interacción con la base de datos
        UsuarioDAO dao = new UsuarioDAO();
        try {
            // 4. Verificación de credenciales
            // Se llama al método autenticarUsuario del DAO, que valida el email y la contraseña
            // (incluyendo la verificación del hash BCrypt de la contraseña).
            Usuario usuario = dao.autenticarUsuario(email.trim(), password);

            // 5. Generación de Token JWT
            // Si la autenticación es exitosa, se genera un token JWT.
            // Este token encapsula la identidad del usuario (email, ID, rol, nombre)
            // para asegurar la persistencia de la sesión sin depender de estados pesados en el servidor.
            String token = JWTUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getId(),
                    usuario.getRolId(),
                    usuario.getNombre());

            // 6. Manejo de error en la generación del token
            if (token == null) {
                // Si el token no se pudo generar, se redirige al login con un error de servidor.
                response.sendRedirect(rutaLogin + "?msg=errorServidor&email=" +
                        java.net.URLEncoder.encode(email.trim(), "UTF-8"));
                return;
            }

            // 7. Persistencia del estado de autenticación en la sesión HTTP
            // Se obtiene la sesión actual o se crea una nueva si no existe.
            HttpSession session = request.getSession();
            // Se almacena el token JWT y el objeto Usuario en la sesión.
            // Esto permite que otras partes de la aplicación accedan a la información del usuario autenticado.
            session.setAttribute("token", token);
            session.setAttribute("usuario", usuario);

            // 8. Lógica de enrutamiento basada en el rol del usuario (Control de Acceso Básico - ACL)
            String redirectUrl = "";
            switch (usuario.getRolId()) {
                case 1: // ROL: ADMINISTRADOR
                    // Redirección al dashboard de administrador.
                    redirectUrl = contextPath + "/Public/admin/administrador-dashboard.jsp";
                    break;
                case 2: // ROL: CLIENTE
                    // Redirección a la página principal del cliente.
                    redirectUrl = contextPath + "/Public/client/pagina-principal.jsp";
                    break;
                default:
                    // Si el rol no es reconocido, se redirige a la página de inicio por defecto.
                    redirectUrl = contextPath + "/index.jsp";
                    break;
            }

            // 9. Redirección final tras el login exitoso
            response.sendRedirect(redirectUrl + "?msg=exitoLogin");

        } catch (Exception e) {
            // 10. Manejo de excepciones durante la autenticación
            // Se registra el error en la consola del servidor para depuración técnica.
            System.out.println("=== ERROR AuthServlet ===");
            System.out.println("Mensaje: " + e.getMessage());

            String errorMsg = e.getMessage();
            String redirectMsg = "";

            // 11. Mapeo de excepciones técnicas a mensajes de usuario amigables
            // Se traduce el mensaje de error interno a un mensaje más comprensible para el usuario final.
            if ("EMAIL_NOT_FOUND".equals(errorMsg)) {
                redirectMsg = "emailNoExiste";
            } else if ("PASSWORD_INCORRECT".equals(errorMsg)) {
                redirectMsg = "passwordIncorrecta";
            } else {
                redirectMsg = "errorServidor"; // Error genérico del servidor.
            }

            // 12. Redirección al login con el mensaje de error correspondiente
            response.sendRedirect(rutaLogin + "?msg=" + redirectMsg + "&email=" +
                    java.net.URLEncoder.encode(email.trim(), "UTF-8"));
        }
    }

    /**
     * Procesa solicitudes GET para la gestión de estado, principalmente el cierre de sesión.
     *
     * @param request El objeto HttpServletRequest que contiene los parámetros de la solicitud (ej. "action=logout").
     * @param response El objeto HttpServletResponse para enviar la respuesta al cliente (redirecciones).
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Extracción del parámetro 'action'
        // Se verifica si la solicitud GET incluye una acción específica.
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            // 2. Procesamiento de la acción de cierre de sesión (logout)
            // Se obtiene la sesión HTTP actual.
            HttpSession session = request.getSession();
            // Se invalida completamente la sesión. Esto elimina todos los atributos de sesión
            // y asegura que el usuario ya no esté autenticado en el servidor.
            session.invalidate();
            // Se redirige al usuario a la página de inicio con un mensaje de sesión cerrada.
            response.sendRedirect(request.getContextPath() + "/index.jsp?msg=sesionCerrada");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
