/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Actuar como el controlador principal para todas las operaciones de administración del sistema.
 */
package controller;

import dao.AdminDAO;
import dao.UsuarioDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Gestiona el panel de control, la supervisión de pedidos, citas, usuarios y el catálogo de servicios.
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    private AdminDAO adminDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización de la capa de persistencia administrativa
        adminDAO = new AdminDAO();
        usuarioDAO = new UsuarioDAO();
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
                    String userIdStr = request.getParameter("userId");
                    if (userIdStr != null && !userIdStr.isEmpty()) {
                        int userId = Integer.parseInt(userIdStr);
                        adminDAO.eliminarUsuario(userId);
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-usuarios.jsp?eliminado=1");
                    }
                    break;
                }

                case "eliminarServicio": {
                    // Borrado lógico de servicio del catálogo
                    String servicioIdStr = request.getParameter("servicioId");
                    if (servicioIdStr != null && !servicioIdStr.isEmpty()) {
                        int servicioId = Integer.parseInt(servicioIdStr);
                        adminDAO.eliminarServicio(servicioId);
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?eliminado=1");
                    }
                    break;
                }

                case "actualizarEstado": {
                    // Cambio de fase en el ciclo de vida del pedido (pendiente -> en proceso -> terminado)
                    String pedidoIdStr = request.getParameter("pedidoId");
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    if (pedidoIdStr != null && !pedidoIdStr.isEmpty() && nuevoEstado != null && !nuevoEstado.isEmpty()) {
                        int pedidoId = Integer.parseInt(pedidoIdStr);
                        int usuarioId = adminDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
                        
                        if (usuarioId != -1) {
                            usuarioDAO.insertarNotificacion(usuarioId, "El estado de tu pedido #" + pedidoId + " ha cambiado a: " + nuevoEstado);
                        }
                        
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId=" + pedidoId + "&actualizado=1");
                    }
                    break;
                }

                case "cambiarEstadoCita": {
                    // Gestión del flujo de citas (pendiente -> confirmada -> realizada)
                    String citaIdStr = request.getParameter("citaId");
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    if (citaIdStr != null && !citaIdStr.isEmpty() && nuevoEstado != null && !nuevoEstado.isEmpty()) {
                        int citaId = Integer.parseInt(citaIdStr);
                        int usuarioId = adminDAO.cambiarEstadoCita(citaId, nuevoEstado);
                        
                        if (usuarioId != -1) {
                            usuarioDAO.insertarNotificacion(usuarioId, "El estado de tu cita #" + citaId + " ha cambiado a: " + nuevoEstado);
                            
                            // Notificación adicional sobre el pedido si la cita se completa
                            if ("completada".equalsIgnoreCase(nuevoEstado)) {
                                usuarioDAO.insertarNotificacion(usuarioId, "Tu cita ha sido completada. El pedido asociado está ahora en estado: EN TALLER.");
                            } else if ("confirmada".equalsIgnoreCase(nuevoEstado)) {
                                usuarioDAO.insertarNotificacion(usuarioId, "Tu cita ha sido confirmada. El pedido asociado está ahora en estado: CONFIRMADO.");
                            }
                        }
                        
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp?vista=citas&actualizado=1");
                    }
                    break;
                }

                case "registrarAsistencia": {
                    // Registro técnico de si el cliente asistió o no a la cita agendada
                    String citaIdStr = request.getParameter("citaId");
                    String asistencia = request.getParameter("asistencia");
                    if (citaIdStr != null && !citaIdStr.isEmpty() && asistencia != null && !asistencia.isEmpty()) {
                        int citaId = Integer.parseInt(citaIdStr);
                        adminDAO.actualizarAsistenciaCita(citaId, asistencia);
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp?vista=citas&actualizado=1");
                    }
                    break;
                }

                default:
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            
            // Para eliminación de usuario, pasar el mensaje específico de error
            if ("eliminarUsuario".equals(accion)) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    // Codificar el mensaje para pasarlo como parámetro URL
                    String encodedMsg = java.net.URLEncoder.encode(errorMsg, "UTF-8");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-usuarios.jsp?error=" + encodedMsg);
                } else {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-usuarios.jsp?error=general");
                }
            } else {
                // Para otras acciones, mantener el error genérico
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp?error=1");
            }
        }
    }
}
