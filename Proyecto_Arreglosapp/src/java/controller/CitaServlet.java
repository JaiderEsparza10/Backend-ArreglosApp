package controller;

import dao.CitaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cita;
import model.Usuario;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO;

    @Override
    public void init() throws ServletException {
        citaDAO = new CitaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if ("agendar".equals(accion)) {
            try {
                String fechaStr = request.getParameter("fechaCita");
                String horaStr = request.getParameter("horaCita");
                String direccion = request.getParameter("direccionEntrega");
                String notas = request.getParameter("notas");

                // Obtener personalizacionId
                String personalizacionIdStr = request.getParameter("personalizacionId");
                int personalizacionId = -1;
                if (personalizacionIdStr != null && !personalizacionIdStr.trim().isEmpty()) {
                    try {
                        personalizacionId = Integer.parseInt(personalizacionIdStr.trim());
                    } catch (NumberFormatException ex) {
                        personalizacionId = -1;
                    }
                }

                // Validar campos obligatorios
                if (fechaStr == null || fechaStr.trim().isEmpty()) {
                    session.setAttribute("errorCita", "Debes seleccionar una fecha");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }
                if (horaStr == null || horaStr.trim().isEmpty()) {
                    session.setAttribute("errorCita", "Debes seleccionar una hora");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }
                if (direccion == null || direccion.trim().isEmpty()) {
                    session.setAttribute("errorCita", "Debes ingresar un lugar de entrega");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // Construir LocalDateTime
                String fechaHoraStr = fechaStr + "T" + horaStr + ":00";
                LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                // Crear pedido con detalle
                int pedidoId = citaDAO.crearPedido(usuario.getId(), personalizacionId);
                if (pedidoId == -1) {
                    session.setAttribute("errorCita", "No se pudo crear el pedido");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // Crear cita
                Cita cita = new Cita(pedidoId, fechaHora, notas, direccion);
                boolean creada = citaDAO.crearCita(cita);

                if (creada) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp?citaAgendada=1");
                } else {
                    session.setAttribute("errorCita", "No se pudo agendar la cita. Intenta nuevamente.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                }

            } catch (Exception e) {
                session.setAttribute("errorCita", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
            }

        } else {
            session.setAttribute("errorCita", "Acción no válida");
            response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
        }
    }
}