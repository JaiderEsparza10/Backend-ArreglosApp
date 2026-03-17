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
import java.util.List;
import java.util.Map;

/**
 * Servlet Maestro de Operaciones Administrativas.
 * Coordina las acciones críticas de gestión: usuarios, pedidos, servicios y citas.
 * Implementa el patrón Front Controller básico mediante dispatching de acciones.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización de la capa de persistencia administrativa
        adminDAO = new AdminDAO();
    }

    /**
     * Unifica las solicitudes GET al despachador de acciones POST.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        // Manejar solicitud de detalles de pedido
        if ("verDetalle".equals(accion)) {
            manejarDetallePedido(request, response);
            return;
        }
        
        doPost(request, response);
    }
    
    private void manejarDetallePedido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("DEBUG: AdminServlet manejarDetallePedido iniciado");
        
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

    /**
     * Canaliza las diversas opereraciones administrativas basadas en el parámetro 'accion'.
     * RF-10: Gestión de Usuarios, RF-11: Gestión de Pedidos, RF-12: Gestión de Servicios.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");
        if (accion == null)
            accion = "";

        try {
            switch (accion) {

                case "eliminarUsuario": {
                    // Eliminación con dependencias básicas automáticas
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    try {
                        boolean eliminado = adminDAO.eliminarUsuario(userId);
                        if (eliminado) {
                            response.sendRedirect(request.getContextPath() + "/Public/admin/usuario-eliminado.jsp");
                        } else {
                            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-usuarios.jsp?error=no_encontrado");
                        }
                    } catch (Exception e) {
                        System.out.println("DEBUG: Error en servlet: " + e.getMessage());
                        if (e.getMessage().equals("NO_SE_PUEDE_ELIMINAR_TIENE_DEPENDENCIAS")) {
                            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-usuarios.jsp?error=dependencias");
                        } else if (e.getMessage().equals("NO_SE_PUEDE_ELIMINAR_TIENE_DEPENDENCIAS_IMPORTANTES")) {
                            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-usuarios.jsp?error=dependencias_importantes");
                        } else {
                            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-usuarios.jsp?error=general&msg=" + e.getMessage());
                        }
                    }
                    break;
                }

                case "eliminarServicio": {
                    // Borrado lógico de servicio del catálogo
                    int servicioId = Integer.parseInt(request.getParameter("servicioId"));
                    adminDAO.eliminarServicio(servicioId);
                    response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-servicios.jsp?eliminado=1");
                    break;
                }

                case "actualizarEstado": {
                    // Cambio de fase en el ciclo de vida del pedido (pendiente -> en proceso -> terminado)
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    adminDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
                    response.sendRedirect(request.getContextPath() + "/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "cambiarEstadoPago": {
                    // Registro manual de pago total
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    adminDAO.actualizarPagoPedido(pedidoId, "pagado");
                    response.sendRedirect(request.getContextPath() + "/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "cambiarEstadoEntrega": {
                    // Registro manual de entrega física de la prenda
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    adminDAO.actualizarEntregaPedido(pedidoId, "entregado");
                    response.sendRedirect(request.getContextPath() + "/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "confirmarPagoEntrega": {
                    // Operación atómica de finalización de transacción
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    adminDAO.actualizarPagoPedido(pedidoId, "pagado");
                    adminDAO.actualizarEntregaPedido(pedidoId, "entregado");
                    adminDAO.actualizarEstadoPedido(pedidoId, "terminado");
                    response.sendRedirect(request.getContextPath() + "/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "registrarAbono": {
                    // RF-05: Gestión de Abonos. Permite pagos parciales.
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    double monto = Double.parseDouble(request.getParameter("montoAbono"));
                    adminDAO.registrarAbono(pedidoId, monto);
                    response.sendRedirect(request.getContextPath() + "/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }
                case "cambiarEstadoCita": {
                    // Gestión del flujo de citas (pendiente -> confirmada -> realizada)
                    int citaId = Integer.parseInt(request.getParameter("citaId"));
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    adminDAO.cambiarEstadoCita(citaId, nuevoEstado);
                    response.sendRedirect(
                            request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?vista=citas&actualizado=1");
                    break;
                }

                case "registrarAsistencia": {
                    // Registro técnico de si el cliente asistió o no a la cita agendada
                    int citaId = Integer.parseInt(request.getParameter("citaId"));
                    String asistencia = request.getParameter("asistencia");
                    adminDAO.actualizarAsistenciaCita(citaId, asistencia);
                    response.sendRedirect(
                            request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?vista=citas&actualizado=1");
                    break;
                }

                default:
                    response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?error=1");
        }
    }
}
