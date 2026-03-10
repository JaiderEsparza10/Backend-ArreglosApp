<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.List" %>
        <%@ page import="java.util.Map" %>
            <%@ page import="model.Usuario" %>
                <%@ page import="dao.PedidoDAO" %>
                    <%@ page import="java.time.LocalDateTime" %>
                        <%@ page import="java.time.format.DateTimeFormatter" %>
                            <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) {
                                usuario=(Usuario) sesion.getAttribute("usuario"); } if (usuario==null) {
                                response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } PedidoDAO
                                pedidoDAO=new PedidoDAO(); List<Map<String, Object>> pedidosActivos = null;
                                List<Map<String, Object>> historialPedidos = null;

                                    // Captura de filtros
                                    String fEstado = request.getParameter("fEstado");

                                    try {
                                    if (fEstado != null && !fEstado.isEmpty()) {
                                        pedidosActivos = pedidoDAO.obtenerPedidosPorEstado(usuario.getId(), fEstado);
                                    } else {
                                        pedidosActivos = pedidoDAO.obtenerPedidosActivos(usuario.getId());
                                    }
                                    historialPedidos = pedidoDAO.obtenerHistorialPedidos(usuario.getId());
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                    }

                                    DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd MMM yyyy");
                                    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("hh:mm a");

                                    // Mensajes
                                    String citaAgendada = request.getParameter("citaAgendada");
                                    %>
                                    <!DOCTYPE html>
                                    <html lang="es">

                                    <head>
                                        <meta charset="UTF-8">
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                        <link rel="stylesheet" href="../../Assets/estilos.css">
                                        <title>Mis Pedidos</title>
                                    </head>

                                    <body class="grid-principal">
                                        <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
                                        <header class="seccion-encabezado">
                                            <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                                                alt="logo de la aplicación">
                                            <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                                        </header>

                                        <div id="toast" class="toast"></div>

                                        <main class="contenido-pedidos" id="contenido-principal" role="main">
                                            <h1 class="contenido__titulo-seleccion">Mis Pedidos</h1>

                                            <!-- TABS -->
                                            <div class="pedidos__tabs">
                                                <button class="pedidos__tab pedidos__tab--activo" id="tabActivos"
                                                    onclick="cambiarTab('activos')">
                                                    Activos
                                                    <% if (pedidosActivos !=null && !pedidosActivos.isEmpty()) { %>
                                                        <span class="pedidos__tab-badge">
                                                            <%= pedidosActivos.size() %>
                                                        </span>
                                                        <% } %>
                                                </button>
                                                <button class="pedidos__tab" id="tabHistorial"
                                                    onclick="cambiarTab('historial')">
                                                    Historial
                                                    <% if (historialPedidos !=null && !historialPedidos.isEmpty()) { %>
                                                        <span class="pedidos__tab-badge pedidos__tab-badge--gris">
                                                            <%= historialPedidos.size() %>
                                                        </span>
                                                        <% } %>
                                                </button>
                                            </div>

                                            <div id="filterSection" style="margin: 15px 20px; display: flex; gap: 10px; align-items: center;">
                                                <form action="mis-pedidos.jsp" method="get" style="display: flex; gap: 10px; width: 100%;">
                                                    <select name="fEstado" style="flex-grow: 1; padding: 10px; border-radius: 10px; border: 1px solid #ddd; background: #fff; font-family: inherit;">
                                                        <option value="">Todos los activos</option>
                                                        <option value="en_proceso" <%= "en_proceso".equals(request.getParameter("fEstado")) ? "selected" : "" %>>En proceso (Taller)</option>
                                                        <option value="terminado" <%= "terminado".equals(request.getParameter("fEstado")) ? "selected" : "" %>>Listos para recoger</option>
                                                    </select>
                                                    <button type="submit" style="background: #673ab7; color: white; border: none; padding: 10px 20px; border-radius: 10px; font-weight: 600; cursor: pointer;">Filtrar</button>
                                                    <% if (request.getParameter("fEstado") != null && !request.getParameter("fEstado").isEmpty()) { %>
                                                        <a href="mis-pedidos.jsp" style="text-decoration: none; color: #666; font-size: 13px; display: flex; align-items: center;">Limpiar</a>
                                                    <% } %>
                                                </form>
                                            </div>

                                            <!-- ACTIVOS -->
                                            <section id="panelActivos" class="pedidos__panel">
                                                <% if (pedidosActivos !=null && !pedidosActivos.isEmpty()) { %>
                                                    <% for (Map<String, Object> pedido : pedidosActivos) { %>
                                                        <% int pedidoId=(int) pedido.get("pedidoId"); String
                                                            estado=(String) pedido.get("pedidoEstado"); LocalDateTime
                                                            fecha=(LocalDateTime) pedido.get("pedidoFecha");
                                                            LocalDateTime citaFecha=(LocalDateTime)
                                                            pedido.get("citaFechaHora"); String citaNotas=(String)
                                                            pedido.get("citaNotas"); String fechaStr=fecha !=null ?
                                                            fecha.format(fmtFecha) : "Sin fecha" ; String
                                                            citaFechaStr=citaFecha !=null ? citaFecha.format(fmtFecha) :
                                                            null; String citaHoraStr=citaFecha !=null ?
                                                            citaFecha.format(fmtHora) : null; String
                                                            badgeClase="badge--pendiente" ; String
                                                            estadoTexto="Pendiente" ; boolean puedeCancelar=false; if
                                                            (estado.equals("confirmado")) { badgeClase="badge--proceso"
                                                            ; estadoTexto="Confirmado" ; puedeCancelar=true; } else if
                                                            (estado.equals("en_proceso")) { badgeClase="badge--proceso"
                                                            ; estadoTexto="En proceso" ; } else if
                                                            (estado.equals("pendiente")) { puedeCancelar=true; } String
                                                            numPedido=String.format("#P-%05d", pedidoId); %>
                                                            <div class="pedido-card" id="pedido-<%= pedidoId %>">
                                                                <div class="pedido-card__cabecera">
                                                                    <div class="pedido-card__numero-fila">
                                                                        <span class="pedido-card__numero">
                                                                            <%= numPedido %>
                                                                        </span>
                                                                        <span
                                                                            class="informacion__badge <%= badgeClase %>">
                                                                            <%= estadoTexto %>
                                                                        </span>
                                                                    </div>
                                                                    <span class="pedido-card__fecha">Creado: <%=
                                                                            fechaStr %></span>
                                                                </div>

                                                                <!-- RESUMEN DE CUENTA CLIENTE -->                                                                 <%
                                                                 double totalParaSaldo = 0;
                                                                 double abonadoParaSaldo = 0;
                                                                 Object totalObj = pedido.get("pedidoTotal");
                                                                 Object abonadoObj = pedido.get("pedidoMontoAbonado");

                                                                 if(totalObj instanceof java.lang.Number) totalParaSaldo = ((java.lang.Number)totalObj).doubleValue();
                                                                 if(abonadoObj instanceof java.lang.Number) abonadoParaSaldo = ((java.lang.Number)abonadoObj).doubleValue();
                                                                 
                                                                 double saldoRestante = totalParaSaldo - abonadoParaSaldo;
                                                                 %>
                                                                <div style="background:#f9f9f9; padding:10px; border-radius:8px; margin:10px 0; border:1px solid #eee;">
                                                                    <div style="display:flex; justify-content:space-between; font-size:13px; margin-bottom:4px;">
                                                                        <span style="color:#666;">Total servicio:</span>
                                                                        <span style="font-weight:600;">$<%= String.format("%,.0f", totalParaSaldo) %></span>
                                                                    </div>
                                                                    <div style="display:flex; justify-content:space-between; font-size:13px; margin-bottom:4px;">
                                                                        <span style="color:#4caf50;">Abonado:</span>
                                                                        <span style="font-weight:600; color:#4caf50;">$<%= String.format("%,.0f", abonadoParaSaldo) %></span>
                                                                    </div>
                                                                    <div style="display:flex; justify-content:space-between; font-size:14px; margin-top:5px; border-top:1px dashed #ccc; padding-top:5px;">
                                                                        <span style="font-weight:700;">SALDO PENDIENTE (Contra entrega):</span>
                                                                        <span style="font-weight:800; color:#f44336;">$<%= String.format("%,.0f", saldoRestante) %></span>
                                                                    </div>
                                                                </div>
                                                                <% if (citaFechaStr !=null) {
                                                                    String motivo = (String)pedido.get("citaMotivo");
                                                                    String motivoLabel = "Consulta";
                                                                    if("entrega_prenda".equals(motivo)) motivoLabel = "Entrega de prenda";
                                                                    else if("recogida_prenda".equals(motivo)) motivoLabel = "Recogida de prenda";
                                                                    else if("toma_medidas".equals(motivo)) motivoLabel = "Toma de medidas";
                                                                %>
                                                                    <div class="pedido-card__cita" style="border-left: 3px solid #673ab7;">
                                                                        <div class="pedido-card__cita-fila">
                                                                            <span
                                                                                class="pedido-card__cita-icono">📅</span>
                                                                            <span class="pedido-card__cita-texto">
                                                                                <b><%= motivoLabel %>:</b> <%= citaFechaStr %> a las <%= citaHoraStr %>
                                                                            </span>
                                                                        </div>
                                                                        <p style="font-size:11px; color:#4caf50; margin-left:24px; margin-top:2px;">
                                                                            Estado cita: <%= (String)pedido.get("citaEstado") %>
                                                                        </p>
                                                                        <% if (citaNotas !=null &&
                                                                            !citaNotas.trim().isEmpty()) { %>
                                                                            <p class="pedido-card__cita-notas">
                                                                                <%= citaNotas %>
                                                                            </p>
                                                                            <% } %>
                                                                    </div>
                                                                    <% } else { %>
                                                                        <p class="pedido-card__sin-cita">Sin cita
                                                                            agendada</p>
                                                                        <% } %>

                                                                            <% if (puedeCancelar) { %>
                                                                                <div class="pedido-card__acciones">
                                                                                    <button
                                                                                        class="pedido-card__btn-cancelar"
                                                                                        onclick="prepararCancelacion(<%= pedidoId %>)">
                                                                                        Cancelar pedido
                                                                                    </button>
                                                                                </div>
                                                                                <% } %>
                                                            </div>
                                                            <% } %>
                                                                <% } else { %>
                                                                    <div class="pedidos__vacio">
                                                                        <p class="pedidos__vacio-texto">No tienes
                                                                            pedidos activos.</p>
                                                                        <a href="mis-arreglos.jsp"
                                                                            class="pedidos__vacio-btn">Ir a Mis
                                                                            Arreglos</a>
                                                                    </div>
                                                                    <% } %>
                                            </section>

                                            <!-- HISTORIAL -->
                                            <section id="panelHistorial" class="pedidos__panel pedidos__panel--oculto">
                                                <% if (historialPedidos !=null && !historialPedidos.isEmpty()) { %>
                                                     <% for (Map<String, Object> pedido : historialPedidos) {
                                                                int pedidoIdH=(int) pedido.get("pedidoId"); String
                                                                estadoH=(String) pedido.get("pedidoEstado"); LocalDateTime
                                                                fechaH=(LocalDateTime) pedido.get("pedidoFecha");
                                                                LocalDateTime citaFechaH=(LocalDateTime)
                                                                pedido.get("citaFechaHora"); String fechaStrH=fechaH !=null
                                                                ? fechaH.format(fmtFecha) : "Sin fecha" ; String
                                                                citaFechaStrH=citaFechaH !=null ?
                                                                citaFechaH.format(fmtFecha) : null; String
                                                                citaHoraStrH=citaFechaH !=null ? citaFechaH.format(fmtHora)
                                                                : null; String badgeClaseH="badge--completado" ; String
                                                                estadoTextoH="Terminado" ; if ("cancelado".equals(estadoH))
                                                                { badgeClaseH="badge--cancelado" ; estadoTextoH="Cancelado"
                                                                ; } String numPedidoH=String.format("#P-%05d", pedidoIdH);
                                                                %>
                                                            <div class="pedido-card pedido-card--historial">
                                                                <div class="pedido-card__cabecera">
                                                                    <div class="pedido-card__numero-fila">
                                                                        <span class="pedido-card__numero">
                                                                            <%= numPedidoH %>
                                                                        </span>
                                                                        <span
                                                                            class="informacion__badge <%= badgeClaseH %>">
                                                                            <%= estadoTextoH %>
                                                                        </span>
                                                                    </div>
                                                                    <span class="pedido-card__fecha">Creado: <%=
                                                                            fechaStrH %></span>
                                                                </div>

                                                                <% if (citaFechaStrH !=null) { %>
                                                                    <div class="pedido-card__cita">
                                                                        <div class="pedido-card__cita-fila">
                                                                            <span
                                                                                class="pedido-card__cita-icono">📅</span>
                                                                            <span class="pedido-card__cita-texto">Cita:
                                                                                <%= citaFechaStrH %> a las <%=
                                                                                        citaHoraStrH %>
                                                                            </span>
                                                                        </div>
                                                                    </div>
                                                                    <% } %>
                                                            </div>
                                                            <% } %>
                                                                <% } else { %>
                                                                    <div class="pedidos__vacio">
                                                                        <p class="pedidos__vacio-texto">No tienes
                                                                            pedidos en el historial.</p>
                                                                    </div>
                                                                    <% } %>
                                            </section>
                                        </main>

                                        <!-- MODAL CANCELAR -->
                                        <div id="modalCancelar" class="modal">
                                            <div class="modal-contenido">
                                                <h3 class="modal__titulo">¿Cancelar pedido?</h3>
                                                <p class="modal__descripcion">Esta acción no se puede deshacer. ¿Estás
                                                    seguro que quieres cancelar este pedido?</p>
                                                <div class="modal__acciones">
                                                    <button class="btn-modal btn-modal--cancelar"
                                                        onclick="cerrarModalCancelacion()">VOLVER</button>
                                                    <button class="btn-modal btn-modal--eliminar"
                                                        id="btnConfirmarCancelacion">CANCELAR PEDIDO</button>
                                                </div>
                                            </div>
                                        </div>

                                        <footer class="navbar">
                                            <nav class="navbar-inferior" role="navigation"
                                                aria-label="Navegación principal">
                                                <a href="pagina-principal.jsp" class="navbar-inferior__item"
                                                    aria-label="Inicio">
                                                    <img src="../../Assets/icons/casa-blanca.png"
                                                        class="navbar-inferior__icono" alt="">
                                                    <span class="navbar-inferior__texto">Inicio</span>
                                                </a>
                                                <a href="mi-seleccion.jsp" class="navbar-inferior__item"
                                                    aria-label="Mi selección">
                                                    <img src="../../Assets/icons/lista-de-deseos-transparente.png"
                                                        class="navbar-inferior__icono" alt="">
                                                    <span class="navbar-inferior__texto">Mi selección</span>
                                                </a>
                                                <a href="mis-arreglos.jsp" class="navbar-inferior__item"
                                                    aria-label="Mis Arreglos">
                                                    <img src="../../Assets/icons/cortar-con-tijeras-transparente.png"
                                                        class="navbar-inferior__icono" alt="">
                                                    <span class="navbar-inferior__texto">Mis Arreglos</span>
                                                </a>
                                                <a href="mis-pedidos.jsp"
                                                    class="navbar-inferior__item navbar-inferior__item--activo"
                                                    aria-current="page" aria-label="Pedidos">
                                                    <img src="../../Assets/icons/caja-transparente.png"
                                                        class="navbar-inferior__icono" alt="">
                                                    <span class="navbar-inferior__texto">Pedidos</span>
                                                </a>
                                                <a href="mi-perfil.jsp" class="navbar-inferior__item"
                                                    aria-label="Perfil">
                                                    <img src="../../Assets/icons/usuario-transparente.png"
                                                        class="navbar-inferior__icono" alt="">
                                                    <span class="navbar-inferior__texto">Perfil</span>
                                                </a>
                                            </nav>
                                        </footer>

                                        <script src="../../Assets/JavaScript/mis-pedidos.js"></script>
                                    </body>

                                    </html>