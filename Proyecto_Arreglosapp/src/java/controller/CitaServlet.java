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

/**
 * Controlador de Gestión de Citas.
 * RF-08: Agendamiento de Citas.
 * Maneja el flujo desde la selección de horarios hasta la confirmación del pedido y la cita.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización de la capa de acceso a datos para citas
        citaDAO = new CitaDAO();
    }

    /**
     * Procesa la solicitud de agendamiento de una nueva cita.
     * Coordina la creación del pedido y su cita física asociada en una sola flujo de operación.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // Verificación de sesión activa mediante el objeto Usuario
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if ("agendar".equals(accion)) {
            try {
                // Extracción de parámetros de fecha, hora y ubicación
                String fechaStr = request.getParameter("fechaCita");
                String horaStr = request.getParameter("horaCita");
                String direccion = request.getParameter("direccionEntrega");
                String motivo = request.getParameter("motivoCita");
                String notas = request.getParameter("notas");

                // Vínculo con una personalización previa opcional
                String personalizacionIdStr = request.getParameter("personalizacionId");
                int personalizacionId = -1;
                if (personalizacionIdStr != null && !personalizacionIdStr.trim().isEmpty()) {
                    try {
                        personalizacionId = Integer.parseInt(personalizacionIdStr.trim());
                    } catch (NumberFormatException ex) {
                        personalizacionId = -1;
                    }
                }

                // Validaciones de integridad de la solicitud (Lado del servidor)
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

                // Transformación de datos temporales a formato ISO para persistencia
                String fechaHoraStr = fechaStr + "T" + horaStr + ":00";
                LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                // Paso 1: Crear el registro de pedido (Cabecera y Detalle)
                int pedidoId = citaDAO.crearPedido(usuario.getId(), personalizacionId);
                if (pedidoId == -1) {
                    session.setAttribute("errorCita", "No se pudo crear el pedido");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // Paso 2: Vincular la cita física al pedido recién creado
                if (motivo == null || motivo.trim().isEmpty()) motivo = "entrega_prenda";
                Cita cita = new Cita(pedidoId, fechaHora, notas, direccion, motivo);
                boolean creada = citaDAO.crearCita(cita);

                if (creada) {
                    // Redirección exitosa con flag de confirmación
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp?citaAgendada=1");
                } else {
                    session.setAttribute("errorCita", "No se pudo agendar la cita. Intenta nuevamente.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                }

            } catch (Exception e) {
                // Manejo de excepciones técnicas durante la transacción
                session.setAttribute("errorCita", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
            }

        } else {
            session.setAttribute("errorCita", "Acción no válida");
            response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
        }
    }
}