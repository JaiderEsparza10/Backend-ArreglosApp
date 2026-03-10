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

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

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
                    int userId = Integer.parseInt(request.getParameter("userId"));
                    adminDAO.eliminarUsuario(userId);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-usuarios.jsp?eliminado=1");
                    break;
                }

                case "eliminarServicio": {
                    int servicioId = Integer.parseInt(request.getParameter("servicioId"));
                    adminDAO.eliminarServicio(servicioId);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?eliminado=1");
                    break;
                }

                case "actualizarEstado": {
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    adminDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/detalle-pedido-admin.jsp?pedidoId="
                            + pedidoId + "&actualizado=1");
                    break;
                }
                case "cambiarEstadoCita": {
                    int citaId = Integer.parseInt(request.getParameter("citaId"));
                    String nuevoEstado = request.getParameter("nuevoEstado");
                    adminDAO.cambiarEstadoCita(citaId, nuevoEstado);
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