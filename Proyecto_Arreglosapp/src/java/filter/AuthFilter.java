package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Usuario;
import util.SimpleAuthUtil;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        
        // Rutas que no requieren autenticación
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si el usuario está autenticado
        HttpSession session = httpRequest.getSession(false);
        String token = null;
        Usuario usuario = null;
        
        if (session != null) {
            token = (String) session.getAttribute("token");
            usuario = (Usuario) session.getAttribute("usuario");
        }
        
        // Validar token y usuario
        if (token == null || usuario == null || SimpleAuthUtil.validateToken(token) == null) {
            // Redirigir al login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return;
        }
        
        // Verificar permisos por rol
        if (!hasAccess(path, usuario.getRolId())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return;
        }
        
        // Continuar con la solicitud
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") || 
               path.equals("/index.jsp") ||
               path.startsWith("/Assets/") ||
               path.startsWith("/Public/auth/") ||
               path.equals("/AuthServlet") ||
               path.equals("/UsuarioServlet");
    }

    private boolean hasAccess(String path, int rolId) {
        // Rutas de administrador
        if (path.startsWith("/Public/admin/")) {
            return rolId == 1; // Solo administradores
        }
        
        // Rutas de cliente
        if (path.startsWith("/Public/client/")) {
            return rolId == 2; // Solo clientes
        }
        
        return true; // Rutas públicas
    }

    @Override
    public void destroy() {
        // Limpieza del filtro
    }
}
