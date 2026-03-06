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

@WebServlet("/PedidoServlet")
public class PedidoServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;

    @Override
    public void init() throws ServletException {
        pedidoDAO = new PedidoDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                boolean cancelado = pedidoDAO.cancelarPedido(pedidoId, usuario.getId());

                if (cancelado) {
                    out.print("{\"success\":true,\"message\":\"Pedido cancelado correctamente\"}");
                } else {
                    out.print(
                            "{\"success\":false,\"message\":\"No se pudo cancelar. Solo se pueden cancelar pedidos pendientes o confirmados.\"}");
                }
            } catch (NumberFormatException e) {
                out.print("{\"success\":false,\"message\":\"ID de pedido invalido\"}");
            } catch (Exception e) {
                out.print("{\"success\":false,\"message\":\"Error interno del servidor\"}");
            }
        } else {
            out.print("{\"success\":false,\"message\":\"Accion no valida\"}");
        }
    }
}