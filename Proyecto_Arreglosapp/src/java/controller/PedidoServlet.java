/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Permitir a los clientes gestionar y realizar el seguimiento de sus pedidos activos.
 */
package controller;

import dao.PedidoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Este controlador responde a solicitudes asíncronas para la cancelación y consulta de estados de pedidos desde la vista del cliente.
 */
@WebServlet("/PedidoServlet")
public class PedidoServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;

    /**
     * Inicializa el acceso a la persistencia para la gestión de pedidos.
     */
    @Override
    public void init() throws ServletException {
        // Inyección de dependencia manual del DAO
        pedidoDAO = new PedidoDAO();
    }

    /**
     * Procesa solicitudes POST, principalmente para la cancelación de pedidos.
     * Implementa una respuesta en formato JSON para facilitar la integración con el frontend (AJAX).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuración de cabeceras para respuesta RESTful básica
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Verificación de integridad de sesión del usuario
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        PrintWriter out = response.getWriter();

        if (usuario == null) {
            out.print("{\"success\":false,\"message\":\"No autenticado\"}");
            return;
        }

        String accion = request.getParameter("accion");

        if ("cancelar".equals(accion)) {
            try {
                // Obtención del ID de pedido para procesar el borrado lógico/físico
                String pedidoIdStr = request.getParameter("pedidoId");
                if (pedidoIdStr != null && !pedidoIdStr.isEmpty()) {
                    int pedidoId = Integer.parseInt(pedidoIdStr);
                    // Ejecución de la lógica de negocio controlada: No se cancelan pedidos en fases avanzadas
                    boolean cancelado = pedidoDAO.cancelarPedido(pedidoId, usuario.getId());

                if (cancelado) {
                    out.print("{\"success\":true,\"message\":\"Pedido cancelado correctamente\"}");
                } else {
                    out.print(
                            "{\"success\":false,\"message\":\"No se pudo cancelar. Solo se pueden cancelar pedidos pendientes o confirmados.\"}");
                }
                }
            } catch (NumberFormatException e) {
                // Control de errores en el formato de entrada (seguridad de tipos)
                out.print("{\"success\":false,\"message\":\"ID de pedido invalido\"}");
            } catch (Exception e) {
                // Captura de errores inesperados de la capa de persistencia
                out.print("{\"success\":false,\"message\":\"Error interno del servidor\"}");
            }
        } else {
            out.print("{\"success\":false,\"message\":\"Accion no valida\"}");
        }
    }
}
