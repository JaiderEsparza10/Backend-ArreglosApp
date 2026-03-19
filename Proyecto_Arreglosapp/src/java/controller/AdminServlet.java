package controller;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        doPost(request, response);
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
                    // Borrado físico de usuario (Admin)
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    adminDAO.eliminarUsuario(userId);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-usuarios.jsp?eliminado=1");
                    break;
                }

                case "eliminarServicio": {
                    // Borrado lógico de servicio del catálogo
                    int servicioId = Integer.parseInt(request.getParameter("servicioId"));
                    adminDAO.eliminarServicio(servicioId);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?eliminado=1");
                    break;
                }

                case "actualizarEstado": {
                    // Cambio de fase en el ciclo de vida del pedido (pendiente -> en proceso -> terminado)
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    adminDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "cambiarEstadoPago": {
                    // Registro manual de pago total
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    adminDAO.actualizarPagoPedido(pedidoId, "pagado");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "cambiarEstadoEntrega": {
                    // Registro manual de entrega física de la prenda
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    adminDAO.actualizarEntregaPedido(pedidoId, "entregado");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "confirmarPagoEntrega": {
                    // Operación atómica de finalización de transacción
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    adminDAO.actualizarPagoPedido(pedidoId, "pagado");
                    adminDAO.actualizarEntregaPedido(pedidoId, "entregado");
                    adminDAO.actualizarEstadoPedido(pedidoId, "terminado");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }

                case "registrarAbono": {
                    // RF-05: Gestión de Abonos. Permite pagos parciales.
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    double monto = Double.parseDouble(request.getParameter("montoAbono"));
                    adminDAO.registrarAbono(pedidoId, monto);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }
                case "cambiarEstadoCita": {
                    // Gestión del flujo de citas (pendiente -> confirmada -> realizada)
                    int citaId = Integer.parseInt(request.getParameter("citaId"));
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    adminDAO.cambiarEstadoCita(citaId, nuevoEstado);
                    response.sendRedirect(
                            "/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp?vista=citas&actualizado=1");
                    break;
                }

                case "registrarAsistencia": {
                    // Registro técnico de si el cliente asistió o no a la cita agendada
                    int citaId = Integer.parseInt(request.getParameter("citaId"));
                    String asistencia = request.getParameter("asistencia");
                    adminDAO.actualizarAsistenciaCita(citaId, asistencia);
                    response.sendRedirect(
                            "/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp?vista=citas&actualizado=1");
                    break;
                }

                default:
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp?error=1");
        }
    }
}
