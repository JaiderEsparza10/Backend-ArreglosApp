/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Garantizar la destrucción segura de la sesión HTTP en el servidor al cerrar la sesión.
 */
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Este controlador invalida el contexto de seguridad del usuario y lo redirige a la página de inicio.
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    /**
     * Invalida la sesión actual y redirige a la página de bienvenida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtención de la sesión sin forzar creación si no existiera
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Borrado definitivo de todos los atributos de sesión del servidor
            session.invalidate();
        }
        // Redirección al punto de entrada tras la terminación del contexto de seguridad
        response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirección simétrica para mayor compatibilidad con diversos orígenes de invocación
        doGet(request, response);
    }
}