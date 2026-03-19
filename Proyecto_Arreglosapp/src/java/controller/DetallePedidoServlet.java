package controller;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet para manejar los detalles de un pedido en el panel de administración.
 */
@WebServlet("/DetallePedidoServlet")
public class DetallePedidoServlet extends HttpServlet {

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DEBUG: DetallePedidoServlet doGet iniciado");
        
        HttpSession session = request.getSession(false);
        Usuario admin = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        System.out.println("DEBUG: Usuario admin = " + (admin != null ? "admin encontrado" : "null"));
        System.out.println("DEBUG: Rol ID = " + (admin != null ? admin.getRolId() : "null"));

        // Validación de sesión y rol
        if (admin == null || admin.getRolId() != 1) {
            System.out.println("DEBUG: Redirigiendo a index.jsp - no autorizado");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String pedidoIdStr = request.getParameter("pedidoId");
        System.out.println("DEBUG: pedidoIdStr = " + pedidoIdStr);
        
        if (pedidoIdStr == null || pedidoIdStr.trim().isEmpty()) {
            System.out.println("DEBUG: Redirigiendo a dashboard - pedidoId nulo o vacío");
            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp");
            return;
        }

        try {
            int pedidoId = Integer.parseInt(pedidoIdStr);
            System.out.println("DEBUG: pedidoId parseado = " + pedidoId);
            
            Map<String, Object> detalle = adminDAO.obtenerDetallePedido(pedidoId);
            System.out.println("DEBUG: detalle obtenido = " + (detalle != null ? "no es null" : "es null"));

            if (detalle == null) {
                System.out.println("DEBUG: Redirigiendo a dashboard - detalle es null");
                response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?error=pedido_no_encontrado");
                return;
            }

            // Pasar el detalle a la JSP
            request.setAttribute("detalle", detalle);
            request.setAttribute("pedidoId", pedidoId);
            System.out.println("DEBUG: Haciendo forward a detalle-pedido-admin.jsp");
            request.getRequestDispatcher("/Public/admin/detalle-pedido-admin.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.out.println("DEBUG: Error NumberFormatException - " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?error=id_invalido");
        } catch (Exception e) {
            System.out.println("DEBUG: Error general - " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?error=general");
        }
    }
}
