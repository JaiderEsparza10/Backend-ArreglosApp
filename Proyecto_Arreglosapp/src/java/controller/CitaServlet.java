/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: CitaServlet.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Controlador central para la gestión de turnos y logística.
 *               Valida disponibilidad de horarios, reglas de atención 
 *               (L-V, 2-10 PM) y coordina la creación atómica de Pedido-Cita.
 * ══════════════════════════════════════════════════════════════════════════════
 */
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
 * Controlador (Servlet) que gestiona el workflow de agendamiento presencial.
 * Actúa como puente entre la vista de agendamiento y la persistencia logística.
 */
@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización del motor de persistencia para logística (Inyección manual)
        citaDAO = new CitaDAO();
    }

    /**
     * Orquestador de solicitudes POST para agendamiento y cambio de estados.
     * Implementa lógica de validación de negocio en el servidor para garantizar
     * la integridad de las fechas y la disponibilidad de cupos.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // Seguridad: Filtro de sesión manual para asegurar contexto de usuario
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // CASO A: Consulta asíncrona de disponibilidad (AJAX)
        if ("obtenerHorasOcupadas".equals(accion)) {
            try {
                String fechaStr = request.getParameter("fechaCita");
                if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                    LocalDate fecha = LocalDate.parse(fechaStr);
                    java.util.List<String> horasOcupadas = citaDAO.obtenerHorasOcupadasPorFecha(fecha);
                    
                    // Generación manual de JSON para evitar dependencias externas (GSON/Jackson)
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
            
        // CASO B: Procesamiento de Agendamiento Físico
        } else if ("agendar".equals(accion)) {
            try {
                // Captura de datos del formulario de logística
                String fechaStr = request.getParameter("fechaCita");
                String horaStr = request.getParameter("horaCita");
                String direccion = request.getParameter("direccionEntrega");
                String motivo = request.getParameter("motivoCita");
                String notas = request.getParameter("notas");

                // Extracción del vínculo técnico (Personalización)
                String personalizacionIdStr = request.getParameter("personalizacionId");
                int personalizacionId = -1;
                if (personalizacionIdStr != null && !personalizacionIdStr.trim().isEmpty()) {
                    try {
                        personalizacionId = Integer.parseInt(personalizacionIdStr.trim());
                    } catch (NumberFormatException ex) {
                        personalizacionId = -1;
                    }
                }

                // Fase 1: Validaciones de campos obligatorios
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

                // Fase 2: Parsing y normalización temporal
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
                
                // REGLA DE NEGOCIO 1: Calendario laboral (Lunes a Viernes)
                DayOfWeek diaSemana = fecha.getDayOfWeek();
                if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
                    session.setAttribute("errorCita", "Solo atendemos de lunes a viernes.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }
                
                // REGLA DE NEGOCIO 2: Franja horaria técnica (14:00 - 22:00)
                LocalTime horaMinima = LocalTime.of(14, 0);
                LocalTime horaMaxima = LocalTime.of(22, 0);
                if (hora.isBefore(horaMinima) || hora.isAfter(horaMaxima)) {
                    session.setAttribute("errorCita", "El horario de atención es de 2:00 PM a 10:00 PM.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }
                
                // REGLA DE NEGOCIO 3: Restricción de viajes en el tiempo
                if (fechaHora.isBefore(ahora)) {
                    session.setAttribute("errorCita", "No puedes agendar citas en fechas u horarios pasados.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // REGLA DE NEGOCIO 4: Exclusividad del slot (Evitar Overbooking)
                if (!citaDAO.isSlotAvailable(fecha, hora)) {
                    session.setAttribute("errorCita", "No se puede agendar la cita porque el espacio ya está ocupado.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // Fase 3: Integridad del flujo (Requiere personalización previa)
                if (personalizacionId == -1) {
                    session.setAttribute("errorCita", "Debes tener una personalización válida para agendar.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
                    return;
                }

                // Fase 4: Persistencia transaccional subordinada
                // Paso 4.1: Creación del Pedido Maestro
                int pedidoId = citaDAO.crearPedido(usuario.getId(), personalizacionId);
                
                if (pedidoId > 0) {
                    // Paso 4.2: Creación de la Cita Logística
                    if (motivo == null || motivo.trim().isEmpty()) motivo = "Sin especificar";
                    
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
                // Manejo de errores de infraestructura o lógica SQL
                session.setAttribute("errorCita", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
            }

        // CASO C: Modificación de estados (Dashboard/Client)
        } else if ("cambiarEstado".equals(accion)) {
            try {
                String idCitaStr = request.getParameter("idCita");
                String nuevoEstado = request.getParameter("nuevoEstado");
                
                if (idCitaStr != null && !idCitaStr.isEmpty() && nuevoEstado != null && !nuevoEstado.isEmpty()) {
                    int idCita = Integer.parseInt(idCitaStr);
                    
                    // Validación de nulos en estado
                    if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                        session.setAttribute("errorCita", "El estado es obligatorio");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
                        return;
                    }
                    
                    // Validación de máquina de estados (Estados permitidos)
                    if (!nuevoEstado.equals("pendiente") && !nuevoEstado.equals("confirmada") && 
                        !nuevoEstado.equals("en_progreso") && !nuevoEstado.equals("completada") && 
                        !nuevoEstado.equals("cancelada")) {
                        session.setAttribute("errorCita", "Estado no válido");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-pedidos.jsp");
                        return;
                    }
                    
                    // Persistencia del nuevo estado
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
            // Acción no reconocida por el controlador
            session.setAttribute("errorCita", "Acción no válida");
            response.sendRedirect("/Proyecto_Arreglosapp/Public/client/agendar-cita.jsp");
        }
    }
}