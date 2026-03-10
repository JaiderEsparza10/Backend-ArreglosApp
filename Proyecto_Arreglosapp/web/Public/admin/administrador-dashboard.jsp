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
                                totalCitasHoy=0; List<Map<String, Object>> pedidosRecientes = new ArrayList<>();
                                    List<Map<String, Object>> todasLasCitas = new ArrayList<>();
                                            try {
                                            totalPedidosActivos = adminDAO.contarPedidosActivos();
                                            totalCitasHoy = adminDAO.contarCitasHoy();
                                            pedidosRecientes = adminDAO.obtenerPedidosRecientes();
                                            todasLasCitas = adminDAO.obtenerTodasLasCitas();
                                            } catch (Exception e) {
                                            e.printStackTrace();
                                            }
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
                                            SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm a");
                                            String vistaParam = request.getParameter("vista");
                                            String vistaInicial = (vistaParam != null && vistaParam.equals("citas")) ?
                                            "citas" : "pedidos";
                                            %>
                                            <!DOCTYPE html>
                                            <html lang="es">

                                            <head>
                                                <meta charset="UTF-8">
                                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                                <link rel="stylesheet" href="../../Assets/estilos.css">
                                                <title>Dashboard Administrador</title>
                                            </head>

                                            <body class="grid-principal">

                                                <div id="toast" class="toast"></div>

                                                <header class="seccion-encabezado">
                                                    <img class="seccion-encabezado__logo"
                                                        src="../../Assets/image/logo-app.png" alt="logo">
                                                    <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                                                </header>

                                                <main class="dashboard">

                                                    <section class="dashboard__estadisticas">
                                                        <article class="estadistica" onclick="mostrarVista('pedidos')"
                                                            style="cursor:pointer;">
                                                            <div class="estadistica__contenido">
                                                                <span class="estadistica__valor">
                                                                    <%= totalPedidosActivos %>
                                                                </span>
                                                                <img class="estadistica__icono"
                                                                    src="../../Assets/icons/hacia-adelante.png"
                                                                    alt="flecha">
                                                            </div>
                                                            <span class="estadistica__label">Pedidos activos</span>
                                                        </article>
                                                        <article class="estadistica" onclick="mostrarVista('citas')"
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
                                                    </section>

                                                    <!-- VISTA PEDIDOS -->
                                                    <section class="dashboard__seccion-pedidos" id="vistaPedidos">
                                                        <h2 class="pedidos__titulo">Pedidos Recientes</h2>
                                                        <div class="pedidos__lista">
                                                            <% if (pedidosRecientes==null || pedidosRecientes.isEmpty())
                                                                { %>
                                                                <p
                                                                    style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                                    No hay pedidos recientes</p>
                                                                <% } %>
                                                                    <% for (int i=0; i < pedidosRecientes.size(); i++) {
                                                                        Map<String, Object> pedido =
                                                                        pedidosRecientes.get(i);
                                                                        int pedidoId = (Integer) pedido.get("pedidoId");
                                                                        String estado = (String) pedido.get("estado");
                                                                        String cliente = (String) pedido.get("cliente");
                                                                        java.sql.Timestamp citaFecha =
                                                                        (java.sql.Timestamp) pedido.get("citaFecha");
                                                                        String citaStr = "Sin cita";
                                                                        if (citaFecha != null) { citaStr =
                                                                        sdf.format(citaFecha); }
                                                                        String estadoClass =
                                                                        "pedido__estado--pendiente";
                                                                        String estadoLabel = "Pendiente";
                                                                        if (estado != null) {
                                                                        if (estado.equals("confirmado")) { estadoClass =
                                                                        "pedido__estado--confirmado"; estadoLabel =
                                                                        "Confirmado"; }
                                                                        else if (estado.equals("en_proceso")) {
                                                                        estadoClass = "pedido__estado--en-taller";
                                                                        estadoLabel = "En Proceso"; }
                                                                        else if (estado.equals("terminado")) {
                                                                        estadoClass = "pedido__estado--entregado";
                                                                        estadoLabel = "Terminado"; }
                                                                        else if (estado.equals("cancelado")) {
                                                                        estadoClass = "pedido__estado--cancelado";
                                                                        estadoLabel = "Cancelado"; }
                                                                        }
                                                                        %>
                                                                        <article class="pedido">
                                                                            <div class="pedido__contenido">
                                                                                <span class="pedido__id">ID: #P-<%=
                                                                                        String.format("%05d", pedidoId)
                                                                                        %></span>
                                                                                <div class="pedido__info-cliente">
                                                                                    <div class="pedido__cliente-row">
                                                                                        <span
                                                                                            class="pedido__label">Cliente:</span>
                                                                                        <span class="pedido__cliente">
                                                                                            <%= cliente %>
                                                                                        </span>
                                                                                    </div>
                                                                                    <span class="pedido__cita">Cita: <%=
                                                                                            citaStr %></span>
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

                                                    <!-- VISTA CITAS -->
                                                    <section class="dashboard__seccion-pedidos" id="vistaCitas"
                                                        style="display:none;">
                                                        <h2 class="pedidos__titulo">Todas las Citas</h2>
                                                        <div class="pedidos__lista">
                                                            <% if (todasLasCitas==null || todasLasCitas.isEmpty()) { %>
                                                                <p
                                                                    style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                                    No hay citas registradas</p>
                                                                <% } %>
                                                                    <% for (int j=0; j < todasLasCitas.size(); j++) {
                                                                        Map<String, Object> cita = todasLasCitas.get(j);
                                                                        int citaId = (Integer) cita.get("citaId");
                                                                        int pedidoIdCita = (Integer)
                                                                        cita.get("pedidoId");
                                                                        String clienteCita = (String)
                                                                        cita.get("cliente");
                                                                        String estadoCita = (String) cita.get("estado");
                                                                        String notasCita = (String) cita.get("notas");
                                                                        java.sql.Timestamp fechaHoraCita =
                                                                        (java.sql.Timestamp) cita.get("fechaHora");
                                                                        String fechaStr = "Sin fecha";
                                                                        if (fechaHoraCita != null) { fechaStr =
                                                                        sdf.format(fechaHoraCita); }
                                                                        String notasTexto = (notasCita != null &&
                                                                        !notasCita.trim().isEmpty()) ? notasCita : "";
                                                                        String estadoCitaClass =
                                                                        "pedido__estado--pendiente";
                                                                        String estadoCitaLabel = "Programada";
                                                                        if (estadoCita != null) {
                                                                        if (estadoCita.equals("confirmada")) {
                                                                        estadoCitaClass = "pedido__estado--confirmado";
                                                                        estadoCitaLabel = "Confirmada"; }
                                                                        else if (estadoCita.equals("completada")) {
                                                                        estadoCitaClass = "pedido__estado--entregado";
                                                                        estadoCitaLabel = "Completada"; }
                                                                        else if (estadoCita.equals("cancelada")) {
                                                                        estadoCitaClass = "pedido__estado--cancelado";
                                                                        estadoCitaLabel = "Cancelada"; }
                                                                        }
                                                                        %>
                                                                        <article class="pedido">
                                                                            <div class="pedido__contenido">
                                                                                <span class="pedido__id">🗓 <%= fechaStr
                                                                                        %></span>
                                                                                <div class="pedido__info-cliente">
                                                                                    <div class="pedido__cliente-row">
                                                                                        <span
                                                                                            class="pedido__label">Cliente:</span>
                                                                                        <span class="pedido__cliente">
                                                                                            <%= clienteCita %>
                                                                                        </span>
                                                                                    </div>
                                                                                    <% if (!notasTexto.isEmpty()) { %>
                                                                                        <span class="pedido__cita">📍
                                                                                            <%= notasTexto %></span>
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
                                                                                    onclick="abrirModalCita(<%= citaId %>, '<%= estadoCita %>')">
                                                                                    Gestionar
                                                                                </button>
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
                                                        <p class="modal__descripcion">Selecciona el nuevo estado para
                                                            esta cita</p>
                                                        <div class="modal-cita__opciones">
                                                            <button class="modal-cita__btn modal-cita__btn--confirmar"
                                                                onclick="cambiarEstado('confirmada')">✓
                                                                Confirmar</button>
                                                            <button class="modal-cita__btn modal-cita__btn--completar"
                                                                onclick="cambiarEstado('completada')">✅
                                                                Completar</button>
                                                            <button class="modal-cita__btn modal-cita__btn--cancelar"
                                                                onclick="cambiarEstado('cancelada')">✕ Cancelar</button>
                                                        </div>
                                                        <form method="post" action="/Proyecto_Arreglosapp/AdminServlet"
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
                                                    <nav class="navbar-inferior">
                                                        <a href="administrador-dashboard.jsp"
                                                            class="navbar-inferior__item navbar-inferior__item--activo">
                                                            <img src="../../Assets/icons/diagrama-dashboard.png"
                                                                class="navbar-inferior__icono">
                                                            <span class="navbar-inferior__texto">Dashboard</span>
                                                        </a>
                                                        <a href="administrador-servicios.jsp"
                                                            class="navbar-inferior__item">
                                                            <img src="../../Assets/icons/catalogo-de-productos.png"
                                                                class="navbar-inferior__icono">
                                                            <span class="navbar-inferior__texto">Servicios</span>
                                                        </a>
                                                        <a href="administrador-usuarios.jsp"
                                                            class="navbar-inferior__item">
                                                            <img src="../../Assets/icons/anadir-grupo.png"
                                                                class="navbar-inferior__icono">
                                                            <span class="navbar-inferior__texto">Usuarios</span>
                                                        </a>
                                                        <a href="/Proyecto_Arreglosapp/LogoutServlet"
                                                            class="navbar-inferior__item">
                                                            <img src="../../Assets/icons/salir-aplicacion.png"
                                                                class="navbar-inferior__icono">
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
                                                        var vistaPedidos = document.getElementById('vistaPedidos');
                                                        var vistaCitas = document.getElementById('vistaCitas');
                                                        var estadPedidos = document.querySelectorAll('.estadistica')[0];
                                                        var estadCitas = document.querySelectorAll('.estadistica')[1];
                                                        if (vista === 'pedidos') {
                                                            vistaPedidos.style.display = 'block';
                                                            vistaCitas.style.display = 'none';
                                                            estadPedidos.style.opacity = '1';
                                                            estadCitas.style.opacity = '0.6';
                                                        } else {
                                                            vistaPedidos.style.display = 'none';
                                                            vistaCitas.style.display = 'block';
                                                            estadPedidos.style.opacity = '0.6';
                                                            estadCitas.style.opacity = '1';
                                                        }
                                                    }

                                                    function abrirModalCita(citaId, estadoActual) {
                                                        citaIdActual = citaId;
                                                        document.getElementById('inputCitaId').value = citaId;
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