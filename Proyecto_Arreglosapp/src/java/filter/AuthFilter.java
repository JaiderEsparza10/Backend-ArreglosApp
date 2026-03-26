/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: AuthFilter.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Middleware de seguridad y control de acceso (IAM).
 *               Intercepta el tráfico HTTP para validar la vigencia de tokens JWT
 *               y aplicar políticas de autorización basadas en roles (RBAC) 
 *               garantizando el aislamiento de rutas privadas.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Usuario;
import util.JWTUtil;

/**
 * Filtro de seguridad perimetral de la aplicación.
 * Actúa como un guardián (Interceptor) que valida la identidad del usuario en cada petición.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    /**
     * Ciclo de vida: Inicialización del motor de filtrado.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Reservado para configuraciones futuras de parámetros de inicialización
    }

    /**
     * Orquestador principal del flujo de seguridad.
     * Evalúa si la ruta solicitada es pública o requiere sesión activa con privilegios específicos.
     *
     * @param request  Solicitud entrante (Servidor).
     * @param response Respuesta saliente (Cliente).
     * @param chain    Cadena de filtros para continuar la ejecución.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Normalización de la ruta relativa al contexto de la aplicación
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // ─── FASE 1: EXCEPCIONES DE SEGURIDAD (Rutas Públicas) ────────
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ─── FASE 2: VALIDACIÓN DE ESTADO AUTENTICADO ─────────────────
        HttpSession session = httpRequest.getSession(false);
        String token = null;
        Usuario usuario = null;

        if (session != null) {
            token = (String) session.getAttribute("token");
            usuario = (Usuario) session.getAttribute("usuario");
        }

        // Validación atómica de Token JWT y Objeto de Sesión
        if (token == null || usuario == null || JWTUtil.validateToken(token) == null) {
            // Expulsión del flujo si el token expiró o el usuario no está en sesión
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return;
        }

        // ─── FASE 3: POLÍTICAS DE AUTORIZACIÓN (RBAC) ─────────────────
        if (!hasAccess(path, usuario.getRolId())) {
            // Bloqueo de acceso por violación de privilegios de rol
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return;
        }

        // ─── FASE 4: CONTINUACIÓN DEL FLUJO ───────────────────────────
        chain.doFilter(request, response);
    }

    /**
     * Define la "White-List" de recursos accesibles sin autenticación.
     * Incluye archivos estáticos, activos multimedia y servlets de login/registro.
     */
    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.equals("/index.jsp") ||
                path.startsWith("/Assets/") ||
                path.startsWith("/Public/auth/") ||
                path.equals("/AuthServlet") ||
                path.equals("/UsuarioServlet") ||
                path.equals("/RecuperarPasswordServlet") ||
                path.equals("/LogoutServlet") ||
                path.equals("/AdminServlet") ||
                path.equals("/ServicioServlet");
    }

    /**
     * Implementa las reglas de negocio de segmentación de rutas por Rol ID.
     * 1: Administrador (Acceso total)
     * 2: Cliente (Acceso limitado a su panel)
     */
    private boolean hasAccess(String path, int rolId) {
        // Segmento Administrativo: Restricción absoluta
        if (path.startsWith("/Public/admin/")) {
            return rolId == 1;
        }

        // Segmento de Cliente: Restricción de consumo final
        if (path.startsWith("/Public/client/")) {
            return rolId == 2;
        }

        return true; // Resto de rutas se consideran neutras
    }

    @Override
    public void destroy() {
        // Liberación de recursos críticos si existieran
    }
}
