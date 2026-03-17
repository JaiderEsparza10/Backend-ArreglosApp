<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@ page import="dao.AdminDAO" %> <%@ page import="model.Usuario" %> <%@ page import="java.util.List" %> <%@ page import="java.util.Map" %> <%@ page import="java.util.ArrayList" %> <%@ page import="java.text.SimpleDateFormat" %> <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1) { response.sendRedirect(request.getContextPath() + "/index.jsp");
                                return; } AdminDAO adminDAO=new AdminDAO(); int totalPedidosActivos=0; int
                                totalCitasHoy=0; int totalTodasCitas=0; List<Map<String, Object>> pedidosRecientes = new
                                ArrayList<>();
                                    List<Map<String, Object>> citasHoy = new ArrayList<>();
                                            List<Map<String, Object>> todasLasCitas = new ArrayList<>();
                                                    try {
                                                    totalPedidosActivos = adminDAO.contarPedidosActivos();
                                                    totalCitasHoy = adminDAO.contarCitasHoy();
                                                    totalTodasCitas = adminDAO.contarTodasLasCitas();
                                                    String fFecha = request.getParameter("fFecha"); String fCliente = request.getParameter("fCliente"); String fEstado = request.getParameter("fEstado");
                                                    if (fFecha != null || fCliente != null) { todasLasCitas =
                                                    adminDAO.obtenerCitasFiltradas(fFecha, fCliente); }
                                                    else { todasLasCitas = adminDAO.obtenerTodasLasCitas(); }
                                                    if (fEstado != null && !fEstado.isEmpty()) { pedidosRecientes =
                                                    adminDAO.obtenerPedidosFiltrados(null, fEstado); }
                                                    else { pedidosRecientes = adminDAO.obtenerPedidosRecientes(); }
                                                    citasHoy = adminDAO.obtenerCitasHoy();
                                                    } catch (Exception e) { e.printStackTrace(); }
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm a"); String vistaParam = request.getParameter("vista"); String vistaInicial = "pedidos"; if ("citas".equals(vistaParam)) vistaInicial = "citas"; else if ("todasCitas".equals(vistaParam)) vistaInicial = "todasCitas"; String fEstadoActual = request.getParameter("fEstado");
                                                    %>
                                                    <!DOCTYPE html>
                                                    <html lang="es">

                                                    <head>
                                                        <meta charset="UTF-8"> <meta name="viewport"
                                                            content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
                                                        <title>Dashboard Administrador</title>
                                                    </head>

                                                    <body class="grid-principal"> <a href="#contenido-principal" class="skip-link">Saltar al
                                                            contenido</a>
                                                        <div id="toast" class="toast"></div> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo"
                                                                src="../../Assets/image/logo-app.png" alt="logo"> <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                                                        </header>
                                                        <main class="dashboard" id="contenido-principal" role="main"> <section class="dashboard__estadisticas"> <article class="estadistica"
                                                                    onclick="mostrarVista('pedidos')"
                                                                    style="cursor:pointer;"> <div class="estadistica__contenido"><span class="estadistica__valor">
                                                                            <%= totalPedidosActivos %>
                                                                        </span><img class="estadistica__icono"
                                                                            src="../../Assets/icons/hacia-adelante.png"
                                                                            alt=""></div> <span class="estadistica__label">Pedidos
                                                                        activos</span>
                                                                </article>
                                                                <article class="estadistica"
                                                                    onclick="mostrarVista('citas')"
                                                                    style="cursor:pointer;"> <div class="estadistica__contenido"><span class="estadistica__valor">
                                                                            <%= totalCitasHoy %>
                                                                        </span><img class="estadistica__icono"
                                                                            src="../../Assets/icons/hacia-adelante.png"
                                                                            alt=""></div> <span class="estadistica__label">Citas hoy</span>
                                                                </article>
                                                                <article class="estadistica"
                                                                    onclick="mostrarVista('todasCitas')"
                                                                    style="cursor:pointer;"> <div class="estadistica__contenido"><span class="estadistica__valor">
                                                                            <%= totalTodasCitas %>
                                                                        </span><img class="estadistica__icono"
                                                                            src="../../Assets/icons/hacia-adelante.png"
                                                                            alt=""></div> <span class="estadistica__label">Total citas</span>
                                                                </article>
                                                            </section>

                                                            <!-- VISTA PEDIDOS -->
                                                            <section class="dashboard__seccion-pedidos"
                                                                id="vistaPedidos"> <div class="pedidos__encabezado-filtro"
                                                                    style="flex-direction:column;align-items:flex-start;gap:15px;margin-bottom:25px;"> <h2 class="pedidos__titulo">Control de Pedidos</h2> <form action="administrador-dashboard.jsp"
                                                                        method="get" class="pedidos__filtros"
                                                                        style="display:flex;flex-wrap:wrap;gap:12px;width:100%;"> <input type="hidden" name="vista"
                                                                            value="pedidos"> <select name="fEstado"
                                                                            class="pedidos__filtro-btn"
                                                                            style="padding:8px 15px;border-radius:25px;border:1px solid #e0e0e0;background:white;font-size:14px;color:#555;cursor:pointer;outline:none;"> <option value="" <%=(fEstadoActual==null || fEstadoActual.isEmpty()) ? "selected"
                                                                                : "" %>>Todos los activos</option> <option value="pendiente" <%="pendiente"
                                                                                .equals(fEstadoActual) ? "selected" : ""
                                                                                %>>Pendiente de Revision</option>
                                                                            <option value="confirmado" <%="confirmado"
                                                                                .equals(fEstadoActual) ? "selected" : ""
                                                                                %>>Pendiente de Inicio</option>
                                                                            <option value="terminado" <%="terminado"
                                                                                .equals(fEstadoActual) ? "selected" : ""
                                                                                %>>Terminado</option>
                                                                            <option value="cancelado" <%="cancelado"
                                                                                .equals(fEstadoActual) ? "selected" : ""
                                                                                %>>Cancelados</option>
                                                                        </select>
                                                                        <button type="submit"
                                                                            class="pedidos__filtro-btn pedidos__filtro-btn--activo"
                                                                            style="padding:8px 20px;border-radius:25px;background:#673ab7;color:white;border:none;font-weight:500;cursor:pointer;">Filtrar</button>
                                                                        <% if (fEstadoActual !=null &&
                                                                            !fEstadoActual.isEmpty()) { %><a
                                                                                href="administrador-dashboard.jsp"
                                                                                class="pedidos__filtro-btn"
                                                                                style="padding:8px 18px;border-radius:25px;border:1px solid #673ab7;color:#673ab7;background:transparent;text-decoration:none;font-weight:500;display:inline-flex;align-items:center;">Limpiar</a>
                                                                            <% } %>
                                                                    </form>
                                                                </div>
                                                                <div class="pedidos__lista" id="listaPedidos"
                                                                    style="margin-top:12px;">
                                                                    <% if (pedidosRecientes==null ||
                                                                        pedidosRecientes.isEmpty()) { %>
                                                                        <p
                                                                            style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                                            No hay pedidos</p>
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
                                                                                String citaStr = citaFecha != null ?
                                                                                sdf.format(citaFecha) : "Sin cita";
                                                                                String estadoClass =
                                                                                "pedido__estado--pendiente"; String estadoLabel = "Pendiente de Revision"; if ("confirmado".equals(estado)) {
                                                                                estadoClass =
                                                                                "pedido__estado--confirmado"; estadoLabel = "Pendiente de Inicio"; } else if ("terminado".equals(estado)) {
                                                                                estadoClass =
                                                                                "pedido__estado--entregado"; estadoLabel = "Terminado"; } else if ("cancelado".equals(estado)) {
                                                                                estadoClass =
                                                                                "pedido__estado--cancelado"; estadoLabel = "Cancelado"; }
                                                                                String citaEstadoData = citaEstado !=
                                                                                null ? citaEstado : "programada";
                                                                                %>
                                                                                <article class="pedido"
                                                                                    data-estado="<%= citaEstadoData %>"> <div class="pedido__contenido"> <span class="pedido__id">ID: #P- <%= String.format("%05d",
                                                                                                pedidoId) %></span>
                                                                                        <div
                                                                                            class="pedido__info-cliente">
                                                                                            <div
                                                                                                class="pedido__cliente-row">
                                                                                                <span
                                                                                                    class="pedido__label">Cliente:</span><span class="pedido__cliente">
                                                                                                    <%= cliente %>
                                                                                                </span></div>
                                                                                            <span
                                                                                                class="pedido__cita">Cita:
                                                                                                <%= citaStr %></span>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="pedido__acciones">
                                                                                        <span
                                                                                            class="pedido__estado <%= estadoClass %>">
                                                                                            <%= estadoLabel %>
                                                                                        </span>
                                                                                        <a href="<%= request.getContextPath() %>/Public/admin/detalle-pedido-admin-simple.jsp?pedidoId=<%= pedidoId %>"
                                                                                            class="pedido__enlace">Detalles</a>
                                                                                    </div>
                                                                                </article>
                                                                                <% } %>
                                                                </div>
                                                            </section>

                                                            <!-- VISTA CITAS HOY -->
                                                            <section class="dashboard__seccion-pedidos" id="vistaCitas"
                                                                style="display:none;"> <h2 class="pedidos__titulo">Citas de Hoy</h2> <div class="pedidos__lista">
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
                                                                                String motivoCita = (String)
                                                                                cita.get("motivo");
                                                                                java.sql.Timestamp fechaHoraCita =
                                                                                (java.sql.Timestamp)
                                                                                cita.get("fechaHora");
                                                                                String horaStr = fechaHoraCita != null ?
                                                                                sdfHora.format(fechaHoraCita) : "Sin hora"; String motivoLabel = "Consulta"; if ("entrega_prenda".equals(motivoCita)) motivoLabel = "Entrega";
                                                                                else if
                                                                                ("recogida_prenda".equals(motivoCita)) motivoLabel = "Recogida";
                                                                                else if
                                                                                ("toma_medidas".equals(motivoCita)) motivoLabel = "Medidas";
                                                                                String estadoCitaClass =
                                                                                "pedido__estado--pendiente"; String estadoCitaLabel = "Programada"; if ("confirmada".equals(estadoCita)) {
                                                                                estadoCitaClass =
                                                                                "pedido__estado--confirmado"; estadoCitaLabel = "Confirmada"; }
                                                                                else if
                                                                                ("completada".equals(estadoCita)) {
                                                                                estadoCitaClass =
                                                                                "pedido__estado--entregado"; estadoCitaLabel = "Completada"; } else if ("cancelada".equals(estadoCita))
                                                                                { estadoCitaClass =
                                                                                "pedido__estado--cancelado"; estadoCitaLabel = "Cancelada"; }
                                                                                boolean citaFinalizada =
                                                                                "completada".equals(estadoCita) || "cancelada".equals(estadoCita);
                                                                                %>
                                                                                <article class="pedido"> <div class="pedido__contenido"> <span class="pedido__id"> <%=
                                                                                                horaStr %> — <b>
                                                                                                    <%= motivoLabel %>
                                                                                                </b></span>
                                                                                        <div
                                                                                            class="pedido__info-cliente">
                                                                                            <div
                                                                                                class="pedido__cliente-row">
                                                                                                <span
                                                                                                    class="pedido__label">Cliente:</span><span class="pedido__cliente">
                                                                                                    <%= clienteCita %>
                                                                                                </span></div>
                                                                                            <% if (notasCita !=null &&
                                                                                                !notasCita.trim().isEmpty())
                                                                                                { %><span
                                                                                                    class="pedido__cita">
                                                                                                    <%= notasCita %>
                                                                                                        </span>
                                                                                                <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="pedido__acciones">
                                                                                        <span
                                                                                            class="pedido__estado <%= estadoCitaClass %>">
                                                                                            <%= estadoCitaLabel %>
                                                                                        </span>
                                                                                        <% if (!citaFinalizada) { %>
                                                                                            <button
                                                                                                class="pedido__enlace pedido__enlace--btn"
                                                                                                onclick="abrirModalCita(<%= citaId %>, '<%= estadoCita %>')">Gestionar</button>
                                                                                            <% } %>
                                                                                    </div>
                                                                                </article>
                                                                                <% } %>
                                                                </div>
                                                            </section>

                                                            <!-- VISTA TODAS LAS CITAS -->
                                                            <section class="dashboard__seccion-pedidos"
                                                                id="vistaTodasCitas" style="display:none;"> <div class="pedidos__encabezado-filtro"
                                                                    style="flex-direction:column;align-items:flex-start;gap:15px;margin-bottom:25px;"> <h2 class="pedidos__titulo">Historial y Agenda
                                                                        Completa</h2>
                                                                    <form action="administrador-dashboard.jsp"
                                                                        method="get" class="pedidos__filtros"
                                                                        style="display:flex;flex-wrap:wrap;gap:12px;width:100%;"> <input type="hidden" name="vista"
                                                                            value="todasCitas"> <input type="date" name="fFecha"
                                                                            value="<%= request.getParameter("fFecha") !=null ? request.getParameter("fFecha") : ""
                                                                            %>" class="pedidos__filtro-btn"
                                                                        style="padding:8px
                                                                        15px;border-radius:25px;border:1px solid
                                                                        #e0e0e0;background:white;font-size:14px;outline:none;"> <input type="text" name="fCliente"
                                                                            placeholder="Nombre cliente..."
                                                                            value="<%= request.getParameter("fCliente") !=null ? request.getParameter("fCliente") : "" %>" class="pedidos__filtro-btn"
                                                                        style="padding:8px
                                                                        15px;border-radius:25px;border:1px solid
                                                                        #e0e0e0;background:white;font-size:14px;flex-grow:1;outline:none;"> <button type="submit"
                                                                            class="pedidos__filtro-btn pedidos__filtro-btn--activo"
                                                                            style="padding:8px 20px;border-radius:25px;background:#673ab7;color:white;border:none;font-weight:500;cursor:pointer;">Buscar</button> <a href="administrador-dashboard.jsp?vista=todasCitas"
                                                                            class="pedidos__filtro-btn"
                                                                            style="padding:8px 18px;border-radius:25px;border:1px solid #673ab7;color:#673ab7;background:transparent;text-decoration:none;font-weight:500;display:inline-flex;align-items:center;">Limpiar</a>
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
                                                                                String fechaStr2 = fechaHoraCita2 !=
                                                                                null ? sdf.format(fechaHoraCita2) : "Sin fecha";
                                                                                String estadoCitaClass2 =
                                                                                "pedido__estado--pendiente"; String estadoCitaLabel2 = "Programada"; if ("confirmada".equals(estadoCita2)) {
                                                                                estadoCitaClass2 =
                                                                                "pedido__estado--confirmado"; estadoCitaLabel2 = "Confirmada"; }
                                                                                else if
                                                                                ("completada".equals(estadoCita2)) {
                                                                                estadoCitaClass2 =
                                                                                "pedido__estado--entregado"; estadoCitaLabel2 = "Completada"; }
                                                                                else if
                                                                                ("cancelada".equals(estadoCita2)) {
                                                                                estadoCitaClass2 =
                                                                                "pedido__estado--cancelado"; estadoCitaLabel2 = "Cancelada"; }
                                                                                boolean citaFinalizada2 =
                                                                                "completada".equals(estadoCita2) || "cancelada".equals(estadoCita2);
                                                                                %>
                                                                                <article class="pedido"> <div class="pedido__contenido"> <span class="pedido__id"> <%= fechaStr2 %></span>
                                                                                        <div
                                                                                            class="pedido__info-cliente">
                                                                                            <div
                                                                                                class="pedido__cliente-row">
                                                                                                <span
                                                                                                    class="pedido__label">Cliente:</span><span class="pedido__cliente">
                                                                                                    <%= clienteCita2 %>
                                                                                                </span></div>
                                                                                            <% if (notasCita2 !=null &&
                                                                                                !notasCita2.trim().isEmpty())
                                                                                                { %><span
                                                                                                    class="pedido__cita">
                                                                                                    <%= notasCita2 %>
                                                                                                        </span>
                                                                                                <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="pedido__acciones">
                                                                                        <span
                                                                                            class="pedido__estado <%= estadoCitaClass2 %>">
                                                                                            <%= estadoCitaLabel2 %>
                                                                                        </span>
                                                                                        <% if (!citaFinalizada2) { %>
                                                                                            <button
                                                                                                class="pedido__enlace pedido__enlace--btn"
                                                                                                onclick="abrirModalCita(<%= citaId2 %>, '<%= estadoCita2 %>')">Gestionar</button>
                                                                                            <% } %>
                                                                                    </div>
                                                                                </article>
                                                                                <% } %>
                                                                </div>
                                                            </section>

                                                        </main>

                                                        <!-- MODAL GESTIONAR CITA -->
                                                        <div id="modalCita" class="modal-overlay" style="display:none;"> <div class="modal-contenido"> <h2 class="modal__titulo">Gestionar Cita</h2> <p class="modal__descripcion" id="modalCitaDescripcion">
                                                                    Selecciona una accion</p>
                                                                <div class="modal-cita__opciones" id="modalCitaBotones">
                                                                </div>
                                                                <button class="btn-modal btn-modal--cancelar"
                                                                    style="width:100%;margin-top:12px;"
                                                                    onclick="cerrarModalCita()">Cerrar</button>
                                                            </div>
                                                        </div>

                                                        <form method="post" action="/Proyecto_Arreglosapp/AdminServlet"
                                                            id="formCita" style="display:none;"> <input type="hidden" name="accion"
                                                                value="cambiarEstadoCita"> <input type="hidden" name="citaId" id="inputCitaId"> <input type="hidden" name="nuevoEstado"
                                                                id="inputNuevoEstado">
                                                        </form>

                                                        <footer class="navbar"> <nav class="navbar-inferior" role="navigation"
                                                                aria-label="Navegacion principal"> <a href="administrador-dashboard.jsp"
                                                                    class="navbar-inferior__item navbar-inferior__item--activo"
                                                                    aria-current="page" aria-label="Dashboard"><img src="../../Assets/icons/diagrama-dashboard.png"
                                                                        class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Dashboard</span></a> <a href="administrador-servicios.jsp"
                                                                    class="navbar-inferior__item"
                                                                    aria-label="Servicios"><img src="../../Assets/icons/catalogo-de-productos.png"
                                                                        class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Servicios</span></a> <a href="administrador-usuarios.jsp"
                                                                    class="navbar-inferior__item"
                                                                    aria-label="Usuarios"><img src="../../Assets/icons/anadir-grupo.png"
                                                                        class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Usuarios</span></a> <a href="/Proyecto_Arreglosapp/LogoutServlet"
                                                                    class="navbar-inferior__item"
                                                                    aria-label="Cerrar sesion"><img src="../../Assets/icons/salir-aplicacion.png"
                                                                        class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Salir</span></a>
                                                            </nav>
                                                        </footer>

                                                        <script>
                                                            var vistaInicial = '<%= vistaInicial %>';

                                                            window.addEventListener('load', function () {
                                                                mostrarVista(vistaInicial);
                                                                var params = new URLSearchParams(window.location.search);
                                                                if (params.get('actualizado') === '1') { mostrarToast('Estado actualizado correctamente', 'exito'); }
                                                            });

                                                            function mostrarVista(vista) {
                                                                document.getElementById('vistaPedidos').style.display = 'none';
                                                                document.getElementById('vistaCitas').style.display = 'none';
                                                                document.getElementById('vistaTodasCitas').style.display = 'none';
                                                                var estadisticas = document.querySelectorAll('.estadistica');
                                                                estadisticas.forEach(function (e) { e.style.opacity = '0.6'; });
                                                                if (vista === 'pedidos') { document.getElementById('vistaPedidos').style.display = 'block'; estadisticas[0].style.opacity = '1'; }
                                                                else if (vista === 'citas') { document.getElementById('vistaCitas').style.display = 'block'; estadisticas[1].style.opacity = '1'; }
                                                                else if (vista === 'todasCitas') { document.getElementById('vistaTodasCitas').style.display = 'block'; estadisticas[2].style.opacity = '1'; }
                                                            }

                                                            function abrirModalCita(citaId, estadoActual) {
                                                                document.getElementById('inputCitaId').value = citaId;
                                                                var botones = document.getElementById('modalCitaBotones');
                                                                var descripcion = document.getElementById('modalCitaDescripcion');
                                                                botones.innerHTML = '';
                                                                if (estadoActual === 'programada') {
                                                                    descripcion.textContent = 'Cita programada - que deseas hacer?';
                                                                    botones.innerHTML = '<button class="modal-cita__btn modal-cita__btn--confirmar" onclick="cambiarEstado(\'confirmada\')">Confirmar</button><button class="modal-cita__btn modal-cita__btn--cancelar" onclick="cambiarEstado(\'cancelada\')">Cancelar</button>';
                                                                } else if (estadoActual === 'confirmada') {
                                                                    descripcion.textContent = 'Cita confirmada - que deseas hacer?';
                                                                    botones.innerHTML = '<button class="modal-cita__btn modal-cita__btn--completar" onclick="cambiarEstado(\'completada\')">Completar</button><button class="modal-cita__btn modal-cita__btn--cancelar" onclick="cambiarEstado(\'cancelada\')">Cancelar</button>';
                                                                }
                                                                document.getElementById('modalCita').style.display = 'flex';
                                                            }

                                                            function cambiarEstado(nuevoEstado) {
                                                                document.getElementById('inputNuevoEstado').value = nuevoEstado;
                                                                document.getElementById('formCita').submit();
                                                            }

                                                            function cerrarModalCita() {
                                                                document.getElementById('modalCita').style.display = 'none';
                                                            }

                                                            function mostrarToast(msg, tipo) {
                                                                var toast = document.getElementById('toast');
                                                                toast.textContent = msg;
                                                                toast.className = 'toast toast--' + tipo + ' toast--visible';
                                                                setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                                                            }
                                                        </script>
                                                    </body>

                                                    </html>