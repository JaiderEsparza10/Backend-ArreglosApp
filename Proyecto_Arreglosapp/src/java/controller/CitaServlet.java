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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        if ("obtenerHorasOcupadas".equals(accion)) {
            try {
                String fechaStr = request.getParameter("fechaCita");
                if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                    LocalDate fecha = LocalDate.parse(fechaStr);
                    java.util.List<String> horasOcupadas = citaDAO.obtenerHorasOcupadasPorFecha(fecha);
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < horasOcupadas.size(); i++) {
                        json.append("\"").append(horasOcupadas.get(i)).append("\"");
                        if (i < horasOcupadas.size() - 1) json.append(",");
                    }
                    json.append("]");
                    
                    response.setContentType("application/json");
                    response.getWriter().write(json.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        } else if ("agendar".equals(accion)) {
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

                // Transformación de datos temporales a formato ISO para persistencia (Manejo Seguro)
                LocalDate fecha;
                LocalTime hora;
                try {
                    fecha = LocalDate.parse(fechaStr);
                    hora = LocalTime.parse(horaStr);
                } catch (Exception e) {
                    session.setAttribute("errorCita", "Formato de fecha o hora inválido.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
                LocalDateTime ahora = LocalDateTime.now();
                
                // 1. Validar que no sea fin de semana (lunes a viernes solo)
                DayOfWeek diaSemana = fecha.getDayOfWeek();
                if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
                    session.setAttribute("errorCita", "Solo atendemos de lunes a viernes.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }
                
                // 2. Validar rango horario: 2:00 PM a 10:00 PM
                LocalTime horaMinima = LocalTime.of(14, 0);
                LocalTime horaMaxima = LocalTime.of(22, 0);
                if (hora.isBefore(horaMinima) || hora.isAfter(horaMaxima)) {
                    session.setAttribute("errorCita", "El horario de atención es de 2:00 PM a 10:00 PM.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }
                
                // 3. Validar que no sea una fecha/hora pasada
                if (fechaHora.isBefore(ahora)) {
                    session.setAttribute("errorCita", "No puedes agendar citas en fechas u horarios pasados.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // 4. Validar disponibilidad de slot (Meta 4)
                if (!citaDAO.isSlotAvailable(fecha, hora)) {
                    session.setAttribute("errorCita", "No se puede agendar la cita porque el espacio ya está ocupado.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // =====================
                // PROCESAMIENTO DE PEDIDO Y CITA (Meta 4)
                // =====================
                if (personalizacionId == -1) {
                    session.setAttribute("errorCita", "Debes tener una personalización válida para agendar.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // Paso 1: Crear el registro de pedido
                int pedidoId = citaDAO.crearPedido(usuario.getId(), personalizacionId);
                
                if (pedidoId > 0) {
                    // Paso 2: Vincular la cita física al pedido recién creado
                    if (motivo == null || motivo.trim().isEmpty()) motivo = "Sin especificar";
                    
                    // Usar objeto Cita como requiere el DAO
                    Cita nuevaCita = new Cita(pedidoId, fechaHora, notas, direccion, motivo);
                    boolean creada = citaDAO.crearCita(nuevaCita);
                    
                    if (creada) {
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp?citaAgendada=1");
                    } else {
                        session.setAttribute("errorCita", "Error al crear el registro de la cita física.");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    }
                } else {
                    session.setAttribute("errorCita", "No se pudo generar el pedido asociado a la cita.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                }

            } catch (Exception e) {
                // Manejo de excepciones técnicas durante la transacción
                session.setAttribute("errorCita", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
            }
        } else if ("cambiarEstado".equals(accion)) {
            try {
                // Capturar ID de la cita y nuevo estado
                String idCitaStr = request.getParameter("idCita");
                String nuevoEstado = request.getParameter("nuevoEstado");
                
                if (idCitaStr != null && !idCitaStr.isEmpty() && nuevoEstado != null && !nuevoEstado.isEmpty()) {
                    int idCita = Integer.parseInt(idCitaStr);
                    
                    // Validar que el estado sea válido
                    if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                        session.setAttribute("errorCita", "El estado es obligatorio");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
                        return;
                    }
                    
                    // Validar estados permitidos
                    if (!nuevoEstado.equals("pendiente") && !nuevoEstado.equals("confirmada") && 
                        !nuevoEstado.equals("en_progreso") && !nuevoEstado.equals("completada") && 
                        !nuevoEstado.equals("cancelada")) {
                        session.setAttribute("errorCita", "Estado no válido");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
                        return;
                    }
                    
                    // Actualizar el estado de la cita
                    boolean actualizado = citaDAO.actualizarEstadoCita(idCita, nuevoEstado);
                    
                    if (actualizado) {
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp?estadoActualizado=1");
                    } else {
                        session.setAttribute("errorCita", "No se pudo actualizar el estado de la cita");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
                    }
                } else {
                    session.setAttribute("errorCita", "Parámetros inválidos");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
                }
                
            } catch (NumberFormatException e) {
                session.setAttribute("errorCita", "ID de cita inválido");
                response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
            } catch (Exception e) {
                session.setAttribute("errorCita", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
            }
        } else {
            session.setAttribute("errorCita", "Acción no válida");
            response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
        }
    }
}