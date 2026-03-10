<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO" %>
        <%@ page import="model.Usuario" %>
            <%@ page import="java.util.List" %>
                <%@ page import="java.util.Map" %>
                    <%@ page import="java.util.ArrayList" %>
                        <%@ page import="java.text.SimpleDateFormat" %>
                            <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null ||
                                admin.getRolId() !=1) { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
                                return; } AdminDAO adminDAO=new AdminDAO(); int totalPedidosActivos=0; int
                                totalCitasHoy=0; int totalTodasCitas=0; List<Map<String, Object>> pedidosRecientes = new
                                ArrayList<>();
                                    List<Map<String, Object>> citasHoy = new ArrayList<>();
                                            List<Map<String, Object>> todasLasCitas = new ArrayList<>();
                                                    try {
                                                    totalPedidosActivos = adminDAO.contarPedidosActivos();
                                                    totalCitasHoy = adminDAO.contarCitasHoy();
                                                    totalTodasCitas = adminDAO.contarTodasLasCitas();
                                                    
                                                    // Captura de filtros
                                                    String fFecha = request.getParameter("fFecha");
                                                    String fCliente = request.getParameter("fCliente");
                                                    String fPago = request.getParameter("fPago");
                                                    String fEstado = request.getParameter("fEstado");

                                                    if (fFecha != null || fCliente != null) {
                                                        todasLasCitas = adminDAO.obtenerCitasFiltradas(fFecha, fCliente);
                                                    } else {
                                                        todasLasCitas = adminDAO.obtenerTodasLasCitas();
                                                    }

                                                    if (fPago != null || fEstado != null) {
                                                        pedidosRecientes = adminDAO.obtenerPedidosFiltrados(fPago, fEstado);
                                                    } else {
                                                        pedidosRecientes = adminDAO.obtenerPedidosRecientes();
                                                    }
                                                    
                                                    citasHoy = adminDAO.obtenerCitasHoy();
                                                    } catch (Exception e) {
                                                    e.printStackTrace();
                                                    }
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                    SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm a");
                                                    String vistaParam = request.getParameter("vista");
                                                    String vistaInicial = "pedidos";
                                                    if (vistaParam != null) {
                                                    if (vistaParam.equals("citas")) vistaInicial = "citas";
                                                    else if (vistaParam.equals("todasCitas")) vistaInicial =
                                                    "todasCitas";
                                                    }
                                                    %>
                                                    <!DOCTYPE html>
                                                    <html lang="es">

                                                    <head>
                                                        <meta charset="UTF-8">
                                                        <meta name="viewport"
                                                            content="width=device-width, initial-scale=1.0">
                                                        <link rel="stylesheet" href="../../Assets/estilos.css">
                                                        <title>Dashboard Administrador</title>
                                                    </head>

                                                    <body class="grid-principal">
                                                        <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
                                                        <div id="toast" class="toast"></div>

                                                        <header class="seccion-encabezado">
                                                            <img class="seccion-encabezado__logo"
                                                                src="../../Assets/image/logo-app.png" alt="logo">
                                                            <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                                                        </header>

                                                        <main class="dashboard" id="contenido-principal" role="main">

                                                            <section class="dashboard__estadisticas">
                                                                <article class="estadistica"
                                                                    onclick="mostrarVista('pedidos')"
                                                                    style="cursor:pointer;">
                                                                    <div class="estadistica__contenido">
                                                                        <span class="estadistica__valor">
                                                                            <%= totalPedidosActivos %>
                                                                        </span>
                                                                        <img class="estadistica__icono"
                                                                            src="../../Assets/icons/hacia-adelante.png"
                                                                            alt="flecha">
                                                                    </div>
                                                                    <span class="estadistica__label">Pedidos
                                                                        activos</span>
                                                                </article>
                                                                <article class="estadistica"
                                                                    onclick="mostrarVista('citas')"
                                                                    style="cursor:pointer;">
                                                                    <div class="estadistica__contenido">
                                                                        <span class="estadistica__valor">
                                                                            <%= totalCitasHoy %>
                                                                        </span>
                                                                        <img class="estadistica__icono"
                                                                            src="../../Assets/icons/hacia-adelante.png"
                                                                            alt="flecha">
                                                                    </div>
                                                                    <span class="estadistica__label">Citas hoy</span>
                                                                </article>
                                                                <article class="estadistica"
                                                                    onclick="mostrarVista('todasCitas')"
                                                                    style="cursor:pointer;">
                                                                    <div class="estadistica__contenido">
                                                                        <span class="estadistica__valor">
                                                                            <%= totalTodasCitas %>
                                                                        </span>
                                                                        <img class="estadistica__icono"
                                                                            src="../../Assets/icons/hacia-adelante.png"
                                                                            alt="flecha">
                                                                    </div>
                                                                    <span class="estadistica__label">Total citas</span>
                                                                </article>
                                                            </section>

                                                            <!-- VISTA PEDIDOS -->
                                                            <section class="dashboard__seccion-pedidos"
                                                                id="vistaPedidos">
                                                                <div class="pedidos__encabezado-filtro" style="flex-direction: column; align-items: flex-start; gap: 15px; margin-bottom: 25px;">
                                                                    <h2 class="pedidos__titulo">Control de Taller e Ingresos</h2>
                                                                    <form action="administrador-dashboard.jsp" method="get" class="pedidos__filtros" style="display: flex; flex-wrap: wrap; gap: 12px; width: 100%;">
                                                                        <input type="hidden" name="vista" value="pedidos">
                                                                        <select name="fEstado" class="pedidos__filtro-btn" style="padding: 8px 15px; border-radius: 25px; border: 1px solid #e0e0e0; background: white; font-size: 14px; color: #555; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(0,0,0,0.05); outline: none;">
                                                                            <option value="">Estado: Todos</option>
                                                                            <option value="en_proceso" <%= "en_proceso".equals(request.getParameter("fEstado")) ? "selected" : "" %>>En Proceso</option>
                                                                            <option value="terminado" <%= "terminado".equals(request.getParameter("fEstado")) ? "selected" : "" %>>Listos para Entrega</option>
                                                                            <option value="cancelado" <%= "cancelado".equals(request.getParameter("fEstado")) ? "selected" : "" %>>Cancelados</option>
                                                                        </select>
                                                                        <select name="fPago" class="pedidos__filtro-btn" style="padding: 8px 15px; border-radius: 25px; border: 1px solid #e0e0e0; background: white; font-size: 14px; color: #555; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(0,0,0,0.05); outline: none;">
                                                                            <option value="">Pago: Todos</option>
                                                                            <option value="pendiente" <%= "pendiente".equals(request.getParameter("fPago")) ? "selected" : "" %>>Pendiente de Cobro</option>
                                                                            <option value="pagado" <%= "pagado".equals(request.getParameter("fPago")) ? "selected" : "" %>>Pagados</option>
                                                                        </select>
                                                                        <button type="submit" class="pedidos__filtro-btn pedidos__filtro-btn--activo" style="padding: 8px 20px; border-radius: 25px; background: #673ab7; color: white; border: none; font-weight: 500; cursor: pointer; box-shadow: 0 2px 5px rgba(103,58,183,0.3); transition: transform 0.2s ease;">Filtrar</button>
                                                                        <a href="administrador-dashboard.jsp" class="pedidos__filtro-btn" style="padding: 8px 18px; border-radius: 25px; border: 1px solid #673ab7; color: #673ab7; background: transparent; text-decoration: none; font-weight: 500; display: inline-flex; align-items: center; justify-content: center; transition: all 0.2s ease;">Limpiar</a>
                                                                    </form>
                                                                 </div>
                                                                <div class="pedidos__lista" id="listaPedidos"
                                                                    style="margin-top:12px;">
                                                                    <% if (pedidosRecientes==null ||
                                                                        pedidosRecientes.isEmpty()) { %>
                                                                        <p
                                                                            style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                                            No hay pedidos recientes</p>
                                                                        <% } %>
                                                                            <% for (int i=0; i <
                                                                                pedidosRecientes.size(); i++) {
                                                                                Map<String, Object> pedido =
                                                                                pedidosRecientes.get(i);
                                                                                int pedidoId = (Integer)
                                                                                pedido.get("pedidoId");
                                                                                String estado = (String)
                                                                                pedido.get("estado");
                                                                                String citaEstado = (String)
                                                                                pedido.get("citaEstado");
                                                                                String cliente = (String)
                                                                                pedido.get("cliente");
                                                                                java.sql.Timestamp citaFecha =
                                                                                (java.sql.Timestamp)
                                                                                pedido.get("citaFecha");
                                                                                String citaStr = "Sin cita";
                                                                                if (citaFecha != null) { citaStr =
                                                                                sdf.format(citaFecha); }
                                                                                String estadoClass =
                                                                                "pedido__estado--pendiente";
                                                                                String estadoLabel = "Pendiente";
                                                                                if (estado != null) {
                                                                                if (estado.equals("confirmado")) {
                                                                                estadoClass =
                                                                                "pedido__estado--confirmado";
                                                                                estadoLabel = "Confirmado"; }
                                                                                else if (estado.equals("en_proceso")) {
                                                                                estadoClass =
                                                                                "pedido__estado--en-taller"; estadoLabel
                                                                                = "En Proceso"; }
                                                                                else if (estado.equals("terminado")) {
                                                                                estadoClass =
                                                                                "pedido__estado--entregado"; estadoLabel
                                                                                = "Terminado"; }
                                                                                else if (estado.equals("cancelado")) {
                                                                                estadoClass =
                                                                                "pedido__estado--cancelado"; estadoLabel
                                                                                = "Cancelado"; }
                                                                                }
                                                                                String citaEstadoData = citaEstado !=
                                                                                null ? citaEstado : "pendiente";
                                                                                %>
                                                                                <article class="pedido"
                                                                                    data-estado="<%= citaEstadoData %>">
                                                                                    <div class="pedido__contenido">
                                                                                        <span class="pedido__id">ID: #P-
                                                                                            <%= String.format("%05d",
                                                                                                pedidoId) %>
                                                                                        </span>
                                                                                        <div
                                                                                            class="pedido__info-cliente">
                                                                                            <div
                                                                                                class="pedido__cliente-row">
                                                                                                <span
                                                                                                    class="pedido__label">Cliente:</span>
                                                                                                <span
                                                                                                    class="pedido__cliente">
                                                                                                    <%= cliente %>
                                                                                                </span>
                                                                                            </div>
                                                                                            <span
                                                                                                class="pedido__cita">Cita:
                                                                                                <%= citaStr %>
                                                                                            </span>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="pedido__acciones">
                                                                                        <span
                                                                                            class="pedido__estado <%= estadoClass %>">
                                                                                            <%= estadoLabel %>
                                                                                        </span>
                                                                                        <a href="detalle-pedido-admin.jsp?pedidoId=<%= pedidoId %>"
                                                                                            class="pedido__enlace">Detalles</a>
                                                                                    </div>
                                                                                </article>
                                                                                <% } %>
                                                                </div>
                                                            </section>

                                                            <!-- VISTA CITAS HOY -->
                                                            <section class="dashboard__seccion-pedidos" id="vistaCitas"
                                                                style="display:none;">
                                                                <h2 class="pedidos__titulo">Citas de Hoy</h2>
                                                                <div class="pedidos__lista">
                                                                    <% if (citasHoy==null || citasHoy.isEmpty()) { %>
                                                                        <p
                                                                            style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                                            No hay citas para hoy</p>
                                                                        <% } %>
                                                                            <% for (int j=0; j < citasHoy.size(); j++) {
                                                                                Map<String, Object> cita =
                                                                                citasHoy.get(j);
                                                                                int citaId = (Integer)
                                                                                cita.get("citaId");
                                                                                String clienteCita = (String)
                                                                                cita.get("cliente");
                                                                                String estadoCita = (String)
                                                                                cita.get("estado");
                                                                                String notasCita = (String)
                                                                                cita.get("notas");
                                                                                java.sql.Timestamp fechaHoraCita =
                                                                                (java.sql.Timestamp)
                                                                                cita.get("fechaHora");
                                                                                String horaStr = "Sin hora";
                                                                                if (fechaHoraCita != null) { horaStr =
                                                                                sdfHora.format(fechaHoraCita); }
                                                                                String notasTexto = (notasCita != null
                                                                                && !notasCita.trim().isEmpty()) ?
                                                                                notasCita : "";
                                                                                String estadoCitaClass =
                                                                                "pedido__estado--pendiente";
                                                                                String estadoCitaLabel = "Programada";
                                                                                if (estadoCita != null) {
                                                                                if (estadoCita.equals("confirmada")) {
                                                                                estadoCitaClass =
                                                                                "pedido__estado--confirmado";
                                                                                estadoCitaLabel = "Confirmada"; }
                                                                                else if
                                                                                (estadoCita.equals("completada")) {
                                                                                estadoCitaClass =
                                                                                "pedido__estado--entregado";
                                                                                estadoCitaLabel = "Completada"; }
                                                                                else if (estadoCita.equals("cancelada"))
                                                                                { estadoCitaClass =
                                                                                "pedido__estado--cancelado";
                                                                                estadoCitaLabel = "Cancelada"; }
                                                                                }
                                                                                String motivoCita = (String)cita.get("motivo");
                                                                                String asistenciaCita = (String)cita.get("asistencia");
                                                                                String motivoLabel = "Consulta";
                                                                                if("entrega_prenda".equals(motivoCita)) motivoLabel = "Entrega";
                                                                                else if("recogida_prenda".equals(motivoCita)) motivoLabel = "Recogida";
                                                                                else if("toma_medidas".equals(motivoCita)) motivoLabel = "Medidas";
                                                                                %>
                                                                                <article class="pedido">
                                                                                    <div class="pedido__contenido">
                                                                                        <span class="pedido__id">🕐 <%= horaStr %> — <b><%= motivoLabel %></b></span>
                                                                                        <div
                                                                                            class="pedido__info-cliente">
                                                                                            <div
                                                                                                class="pedido__cliente-row">
                                                                                                <span
                                                                                                    class="pedido__label">Cliente:</span>
                                                                                                <span
                                                                                                    class="pedido__cliente">
                                                                                                    <%= clienteCita %>
                                                                                                </span>
                                                                                            </div>
                                                                                            <span style="font-size:11px; color:<%= "asistio".equals(asistenciaCita) ? "#4caf50" : ("no_asistio".equals(asistenciaCita) ? "#f44336" : "#888") %>">
                                                                                                Asistencia: <%= asistenciaCita != null ? asistenciaCita.toUpperCase() : "PENDIENTE" %>
                                                                                            </span>
                                                                                            <% if
                                                                                                (!notasTexto.isEmpty())
                                                                                                { %>
                                                                                                <span
                                                                                                    class="pedido__cita">📍
                                                                                                    <%= notasTexto %>
                                                                                                </span>
                                                                                                <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="pedido__acciones">
                                                                                        <span
                                                                                            class="pedido__estado <%= estadoCitaClass %>">
                                                                                            <%= estadoCitaLabel %>
                                                                                        </span>
                                                                                        <button
                                                                                            class="pedido__enlace pedido__enlace--btn"
                                                                                            onclick="abrirModalCita(<%= citaId %>)">Gestionar</button>
                                                                                    </div>
                                                                                </article>
                                                                                <% } %>
                                                                </div>
                                                            </section>

                                                            <!-- VISTA TODAS LAS CITAS -->
                                                            <section class="dashboard__seccion-pedidos"
                                                                id="vistaTodasCitas" style="display:none;">
                                                                <div class="pedidos__encabezado-filtro" style="flex-direction: column; align-items: flex-start; gap: 15px; margin-bottom: 25px;">
                                                                    <h2 class="pedidos__titulo">Historial y Agenda Completa</h2>
                                                                    <form action="administrador-dashboard.jsp" method="get" class="pedidos__filtros" style="display: flex; flex-wrap: wrap; gap: 12px; width: 100%;">
                                                                        <input type="hidden" name="vista" value="todasCitas">
                                                                        <input type="date" name="fFecha" value="<%= request.getParameter("fFecha") != null ? request.getParameter("fFecha") : "" %>" class="pedidos__filtro-btn" style="padding: 8px 15px; border-radius: 25px; border: 1px solid #e0e0e0; background: white; font-size: 14px; color: #555; cursor: pointer; box-shadow: 0 2px 5px rgba(0,0,0,0.05); outline: none;">
                                                                        <input type="text" name="fCliente" placeholder="Nombre cliente..." value="<%= request.getParameter("fCliente") != null ? request.getParameter("fCliente") : "" %>" class="pedidos__filtro-btn" style="padding: 8px 15px; border-radius: 25px; border: 1px solid #e0e0e0; background: white; font-size: 14px; color: #555; box-shadow: 0 2px 5px rgba(0,0,0,0.05); flex-grow:1; outline: none;">
                                                                        <button type="submit" class="pedidos__filtro-btn pedidos__filtro-btn--activo" style="padding: 8px 20px; border-radius: 25px; background: #673ab7; color: white; border: none; font-weight: 500; cursor: pointer; box-shadow: 0 2px 5px rgba(103,58,183,0.3); transition: transform 0.2s ease;">Buscar</button>
                                                                        <a href="administrador-dashboard.jsp?vista=todasCitas" class="pedidos__filtro-btn" style="padding: 8px 18px; border-radius: 25px; border: 1px solid #673ab7; color: #673ab7; background: transparent; text-decoration: none; font-weight: 500; display: inline-flex; align-items: center; justify-content: center; transition: all 0.2s ease;">Limpiar</a>
                                                                    </form>
                                                                 </div>
                                                                <div class="pedidos__lista">
                                                                    <% if (todasLasCitas==null ||
                                                                        todasLasCitas.isEmpty()) { %>
                                                                        <p
                                                                            style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                                            No hay citas registradas</p>
                                                                        <% } %>
                                                                            <% for (int k=0; k < todasLasCitas.size();
                                                                                k++) { Map<String, Object> cita2 =
                                                                                todasLasCitas.get(k);
                                                                                int citaId2 = (Integer)
                                                                                cita2.get("citaId");
                                                                                String clienteCita2 = (String)
                                                                                cita2.get("cliente");
                                                                                String estadoCita2 = (String)
                                                                                cita2.get("estado");
                                                                                String notasCita2 = (String)
                                                                                cita2.get("notas");
                                                                                java.sql.Timestamp fechaHoraCita2 =
                                                                                (java.sql.Timestamp)
                                                                                cita2.get("fechaHora");
                                                                                String fechaStr2 = "Sin fecha";
                                                                                if (fechaHoraCita2 != null) { fechaStr2
                                                                                = sdf.format(fechaHoraCita2); }
                                                                                String notasTexto2 = (notasCita2 != null
                                                                                && !notasCita2.trim().isEmpty()) ?
                                                                                notasCita2 : "";
                                                                                String estadoCitaClass2 =
                                                                                "pedido__estado--pendiente";
                                                                                String estadoCitaLabel2 = "Programada";
                                                                                if (estadoCita2 != null) {
                                                                                if (estadoCita2.equals("confirmada")) {
                                                                                estadoCitaClass2 =
                                                                                "pedido__estado--confirmado";
                                                                                estadoCitaLabel2 = "Confirmada"; }
                                                                                else if
                                                                                (estadoCita2.equals("completada")) {
                                                                                estadoCitaClass2 =
                                                                                "pedido__estado--entregado";
                                                                                estadoCitaLabel2 = "Completada"; }
                                                                                else if
                                                                                (estadoCita2.equals("cancelada")) {
                                                                                estadoCitaClass2 =
                                                                                "pedido__estado--cancelado";
                                                                                estadoCitaLabel2 = "Cancelada"; }
                                                                                }
                                                                                %>
                                                                                <article class="pedido">
                                                                                    <div class="pedido__contenido">
                                                                                        <span class="pedido__id">🗓 <%=
                                                                                                fechaStr2 %></span>
                                                                                        <div
                                                                                            class="pedido__info-cliente">
                                                                                            <div
                                                                                                class="pedido__cliente-row">
                                                                                                <span
                                                                                                    class="pedido__label">Cliente:</span>
                                                                                                <span
                                                                                                    class="pedido__cliente">
                                                                                                    <%= clienteCita2 %>
                                                                                                </span>
                                                                                            </div>
                                                                                            <% if
                                                                                                (!notasTexto2.isEmpty())
                                                                                                { %>
                                                                                                <span
                                                                                                    class="pedido__cita">📍
                                                                                                    <%= notasTexto2 %>
                                                                                                </span>
                                                                                                <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="pedido__acciones">
                                                                                        <span
                                                                                            class="pedido__estado <%= estadoCitaClass2 %>">
                                                                                            <%= estadoCitaLabel2 %>
                                                                                        </span>
                                                                                        <button
                                                                                            class="pedido__enlace pedido__enlace--btn"
                                                                                            onclick="abrirModalCita(<%= citaId2 %>)">Gestionar</button>
                                                                                    </div>
                                                                                </article>
                                                                                <% } %>
                                                                </div>
                                                            </section>

                                                        </main>

                                                        <!-- MODAL GESTIONAR CITA -->
                                                        <div id="modalCita" class="modal-overlay" style="display:none;">
                                                            <div class="modal-contenido">
                                                                <h2 class="modal__titulo">Cambiar Estado</h2>
                                                                <p class="modal__descripcion">Selecciona el nuevo estado
                                                                    para esta cita</p>
                                                                 <div class="modal-cita__opciones">
                                                                     <button
                                                                         class="modal-cita__btn modal-cita__btn--confirmar"
                                                                         onclick="registrarAsistencia('asistio')">🙋
                                                                         Asistió</button>
                                                                     <button
                                                                         class="modal-cita__btn modal-cita__btn--cancelar"
                                                                         onclick="registrarAsistencia('no_asistio')">🚫
                                                                         No asistió</button>
                                                                 </div>
                                                                 <div style="border-top:1px solid #eee; margin:10px 0;"></div>
                                                                 <div class="modal-cita__opciones">
                                                                     <button
                                                                         class="modal-cita__btn modal-cita__btn--confirmar"
                                                                         onclick="cambiarEstado('confirmada')">✓
                                                                         Confirmar</button>
                                                                     <button
                                                                         class="modal-cita__btn modal-cita__btn--completar"
                                                                         onclick="cambiarEstado('completada')">✅
                                                                         Completar</button>
                                                                     <button
                                                                         class="modal-cita__btn modal-cita__btn--cancelar"
                                                                         onclick="cambiarEstado('cancelada')">✕
                                                                         Cancelar</button>
                                                                 </div>
                                                                <form method="post"
                                                                    action="/Proyecto_Arreglosapp/AdminServlet"
                                                                    id="formCita">
                                                                    <input type="hidden" name="accion"
                                                                        value="cambiarEstadoCita">
                                                                    <input type="hidden" name="citaId" id="inputCitaId">
                                                                    <input type="hidden" name="nuevoEstado"
                                                                        id="inputNuevoEstado">
                                                                </form>
                                                                <button class="btn-modal btn-modal--cancelar"
                                                                    style="width:100%;margin-top:8px;"
                                                                    onclick="cerrarModalCita()">Cerrar</button>
                                                            </div>
                                                        </div>

                                                        <footer class="navbar">
                                                            <nav class="navbar-inferior" role="navigation" aria-label="Navegación principal">
                                                                <a href="administrador-dashboard.jsp" 
                                                                   class="navbar-inferior__item navbar-inferior__item--activo"
                                                                   aria-current="page"
                                                                   aria-label="Dashboard">
                                                                    <img src="../../Assets/icons/diagrama-dashboard.png" 
                                                                         class="navbar-inferior__icono" alt="">
                                                                    <span class="navbar-inferior__texto">Dashboard</span>
                                                                </a>
                                                                <a href="administrador-servicios.jsp" 
                                                                   class="navbar-inferior__item"
                                                                   aria-label="Servicios">
                                                                    <img src="../../Assets/icons/catalogo-de-productos.png" 
                                                                         class="navbar-inferior__icono" alt="">
                                                                    <span class="navbar-inferior__texto">Servicios</span>
                                                                </a>
                                                                <a href="administrador-usuarios.jsp" 
                                                                   class="navbar-inferior__item"
                                                                   aria-label="Usuarios">
                                                                    <img src="../../Assets/icons/anadir-grupo.png" 
                                                                         class="navbar-inferior__icono" alt="">
                                                                    <span class="navbar-inferior__texto">Usuarios</span>
                                                                </a>

                                                                <a href="/Proyecto_Arreglosapp/LogoutServlet" 
                                                                   class="navbar-inferior__item"
                                                                   aria-label="Cerrar sesión">
                                                                    <img src="../../Assets/icons/salir-aplicacion.png" 
                                                                         class="navbar-inferior__icono" alt="">
                                                                    <span class="navbar-inferior__texto">Salir</span>
                                                                </a>
                                                            </nav>
                                                        </footer>

                                                        <script>
                                                            var vistaInicial = '<%= vistaInicial %>';
                                                            var citaIdActual = 0;

                                                            window.addEventListener('load', function () {
                                                                mostrarVista(vistaInicial);
                                                                var params = new URLSearchParams(window.location.search);
                                                                if (params.get('actualizado') === '1') {
                                                                    mostrarToast('Estado actualizado correctamente', 'exito');
                                                                }
                                                            });

                                                            function mostrarVista(vista) {
                                                                document.getElementById('vistaPedidos').style.display = 'none';
                                                                document.getElementById('vistaCitas').style.display = 'none';
                                                                document.getElementById('vistaTodasCitas').style.display = 'none';
                                                                var estadisticas = document.querySelectorAll('.estadistica');
                                                                estadisticas.forEach(function (e) { e.style.opacity = '0.6'; });
                                                                if (vista === 'pedidos') {
                                                                    document.getElementById('vistaPedidos').style.display = 'block';
                                                                    estadisticas[0].style.opacity = '1';
                                                                } else if (vista === 'citas') {
                                                                    document.getElementById('vistaCitas').style.display = 'block';
                                                                    estadisticas[1].style.opacity = '1';
                                                                } else if (vista === 'todasCitas') {
                                                                    document.getElementById('vistaTodasCitas').style.display = 'block';
                                                                    estadisticas[2].style.opacity = '1';
                                                                }
                                                            }

                                                            function filtrarPedidos(btn, estado) {
                                                                var btns = document.querySelectorAll('.pedidos__filtro-btn');
                                                                btns.forEach(function (b) { b.classList.remove('pedidos__filtro-btn--activo'); });
                                                                btn.classList.add('pedidos__filtro-btn--activo');
                                                                var cards = document.querySelectorAll('#listaPedidos .pedido');
                                                                cards.forEach(function (card) {
                                                                    if (estado === 'todos' || card.getAttribute('data-estado') === estado) {
                                                                        card.style.display = 'flex';
                                                                    } else {
                                                                        card.style.display = 'none';
                                                                    }
                                                                });
                                                            }

                                                            function abrirModalCita(citaId) {
                                                                citaIdActual = citaId;
                                                                document.getElementById('inputCitaId').value = citaId;
                                                                document.getElementById('modalCita').style.display = 'flex';
                                                            }

                                                            function registrarAsistencia(valor) {
                                                                const form = document.getElementById('formCita');
                                                                form.querySelector('[name="accion"]').value = 'registrarAsistencia';
                                                                document.getElementById('inputNuevoEstado').name = 'asistencia';
                                                                document.getElementById('inputNuevoEstado').value = valor;
                                                                form.submit();
                                                            }

                                                            function cambiarEstado(nuevoEstado) {
                                                                document.getElementById('inputNuevoEstado').name = 'nuevoEstado'; // Reset name in case it was changed by registrarAsistencia
                                                                document.getElementById('inputNuevoEstado').value = nuevoEstado;
                                                                document.getElementById('formCita').querySelector('[name="accion"]').value = 'cambiarEstadoCita'; // Reset accion
                                                                document.getElementById('formCita').submit();
                                                            }

                                                            function cerrarModalCita() {
                                                                document.getElementById('modalCita').style.display = 'none';
                                                            }

                                                            function mostrarToast(msg, tipo) {
                                                                var toast = document.getElementById('toast');
                                                                toast.textContent = msg;
                                                                toast.className = 'toast toast--' + (tipo === 'exito' || tipo === 'confirmada' || tipo === 'completada' ? 'exito' : 'error') + ' toast--visible';
                                                                setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                                                            }
                                                        </script>
                                                    </body>

                                                    </html>