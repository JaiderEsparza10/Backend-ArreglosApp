<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO" %>
        <%@ page import="model.Usuario" %>
            <%@ page import="java.util.Map" %>
                <%@ page import="java.text.SimpleDateFormat" %>
                    <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1)
                        { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String
                        pedidoIdStr=request.getParameter("pedidoId"); if (pedidoIdStr==null) {
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp"); return;
                        } int pedidoId=Integer.parseInt(pedidoIdStr); AdminDAO adminDAO=new AdminDAO(); Map<String,
                        Object> detalle = null;
                        try {
                        detalle = adminDAO.obtenerDetallePedido(pedidoId);
                        } catch (Exception e) {
                        e.printStackTrace();
                        }
                        if (detalle == null) {
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp");
                        return;
                        }
                        String estado = (String) detalle.get("estado");
                        String cliente = (String) detalle.get("cliente");
                        String email = (String) detalle.get("email");
                        String direccion = (String) detalle.get("direccion");
                        String servicio = (String) detalle.get("servicio");
                        Object precioObj = detalle.get("precio");
                        double precio = (precioObj instanceof Number) ? ((Number) precioObj).doubleValue() : 0.0;
                        String citaNotas = (String) detalle.get("citaNotas");
                        java.sql.Timestamp citaFecha = (java.sql.Timestamp) detalle.get("citaFecha");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String citaStr = citaFecha != null ? sdf.format(citaFecha) : "Sin cita agendada";
                        String estadoClass = "estado-badge--pendiente";
                        if ("en_proceso".equals(estado)) estadoClass = "estado-badge--en-taller";
                        else if ("terminado".equals(estado)) estadoClass = "estado-badge--entregado";
                        else if ("cancelado".equals(estado)) estadoClass = "estado-badge--cancelado";
                        String estadoLabel = "Pendiente";
                        if ("en_proceso".equals(estado)) estadoLabel = "En Proceso";
                        else if ("terminado".equals(estado)) estadoLabel = "Terminado";
                        else if ("cancelado".equals(estado)) estadoLabel = "Cancelado";
                        else if ("confirmado".equals(estado)) { estadoLabel = "Confirmado"; estadoClass =
                        "pedido__estado--confirmado"; }
                        
                        String pagoEstado = (String) detalle.get("pagoEstado");
                        String entregaEstado = (String) detalle.get("entregaEstado");
                        Object montoAbonadoObj = detalle.get("montoAbonado");
                        double montoAbonado = (montoAbonadoObj instanceof Number) ? ((Number) montoAbonadoObj).doubleValue() : 0.0;
                        Object totalPedidoObj = detalle.get("total");
                        double totalPedido = (totalPedidoObj instanceof Number) ? ((Number) totalPedidoObj).doubleValue() : 0.0;
                        %>
                        <!DOCTYPE html>
                        <html lang="es">

                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <link rel="stylesheet" href="../../Assets/estilos.css">
                            <title>Detalle Pedido</title>
                        </head>

                        <body class="grid-principal">
                            <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
                            <div id="toast" class="toast"></div>

                            <header class="seccion-encabezado">
                                <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
                                <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                            </header>

                            <main class="detalle-pedido" id="contenido-principal" role="main">

                                <a href="administrador-dashboard.jsp" class="detalle-pedido__volver">← Volver</a>

                                <h2 style="font-size:16px;font-weight:600;color:#3d0072;">
                                    Pedido #P-<%= String.format("%05d", pedidoId) %>
                                </h2>

                                <!-- INFO CLIENTE -->
                                <div class="info-seccion">
                                    <h3 class="info-seccion__titulo">Información del Cliente</h3>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Nombre:</span>
                                        <span class="info-seccion__valor">
                                            <%= cliente %>
                                        </span>
                                    </div>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Email:</span>
                                        <span class="info-seccion__valor">
                                            <%= email %>
                                        </span>
                                    </div>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Dirección:</span>
                                        <span class="info-seccion__valor">
                                            <%= direccion !=null ? direccion : "No especificada" %>
                                        </span>
                                    </div>
                                </div>

                                <!-- INFO SERVICIO -->
                                <div class="info-seccion">
                                    <h3 class="info-seccion__titulo">Servicio Solicitado</h3>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Servicio:</span>
                                        <span class="info-seccion__valor">
                                            <%= servicio !=null ? servicio : "No especificado" %>
                                        </span>
                                    </div>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Precio:</span>
                                        <span class="info-seccion__valor">$<%= String.format("%,.0f", precio) %></span>
                                    </div>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Cita:</span>
                                        <span class="info-seccion__valor">
                                            <%= citaStr %>
                                        </span>
                                    </div>
                                <% if (citaNotas !=null && !citaNotas.isEmpty()) { %>
                                        <div class="info-seccion__campo">
                                            <span class="info-seccion__label">Notas:</span>
                                            <span class="info-seccion__valor">
                                                <%= citaNotas %>
                                            </span>
                                        </div>
                                        <% } %>
                                </div>

                                <!-- CONTROL DE CAJA / ABONOS -->
                                <div class="info-seccion" style="border-left: 4px solid #4caf50;">
                                    <h3 class="info-seccion__titulo">Control de Pago y Abonos</h3>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Total Servicio:</span>
                                        <span class="info-seccion__valor" style="font-weight:700;">$<%= String.format("%,.0f", totalPedido) %></span>
                                    </div>
                                    <div class="info-seccion__campo info-seccion__campo--inline">
                                        <span class="info-seccion__label">Monto Abonado:</span>
                                        <span class="info-seccion__valor" style="color:#4caf50;">$<%= String.format("%,.0f", montoAbonado) %></span>
                                    </div>
                                    <div class="info-seccion__campo info-seccion__campo--inline" style="margin-top:5px; border-top:1px solid #eee; padding-top:5px;">
                                        <span class="info-seccion__label">Saldo Restante:</span>
                                        <span class="info-seccion__valor" style="color:#f44336; font-size: 1.1em; font-weight: 700;">
                                            $<%= String.format("%,.0f", totalPedido - montoAbonado) %>
                                        </span>
                                    </div>
                                    
                                    <% if (!"pagado".equals(pagoEstado)) { %>
                                    <form action="/Proyecto_Arreglosapp/AdminServlet" method="post" style="margin-top:15px; display:flex; gap:10px; align-items:center;">
                                        <input type="hidden" name="accion" value="registrarAbono">
                                        <input type="hidden" name="pedidoId" value="<%= pedidoId %>">
                                        <input type="number" name="montoAbono" placeholder="Monto abono" required
                                               style="flex:1; padding:8px; border:1px solid #ddd; border-radius:4px;">
                                        <button type="submit" class="perfil__btn-guardar" style="padding: 8px 12px; width:auto; background:#4caf50;">
                                            + Abono
                                        </button>
                                    </form>
                                    <% } %>
                                </div>

                                <!-- ESTADO DEL PEDIDO - EDITABLE -->
                                <div class="estado-pedido">
                                    <h3 class="estado-pedido__titulo">Gestión del Pedido</h3>
                                    <div class="estado-pedido__actual">
                                        <span class="estado-pedido__actual-label">Estado flujo:</span>
                                        <span class="estado-badge <%= estadoClass %>">
                                            <%= estadoLabel %>
                                        </span>
                                    </div>
                                    <div class="estado-pedido__actual" style="margin-top:5px;">
                                        <span class="estado-pedido__actual-label">Pago:</span>
                                        <span class="estado-badge" style="background:<%= "pagado".equals(pagoEstado) ? "#4caf50" : "#ddd" %>; color: white;">
                                            <%= pagoEstado.toUpperCase() %>
                                        </span>
                                        <span class="estado-pedido__actual-label" style="margin-left:15px;">Entrega:</span>
                                        <span class="estado-badge" style="background:<%= "entregado".equals(entregaEstado) ? "#2196f3" : "#ddd" %>; color: white;">
                                            <%= entregaEstado.toUpperCase().replace("_", " ") %>
                                        </span>
                                    </div>
                                                                     <div class="gestiones-pedido" style="margin-top:20px; display:flex; gap:10px; flex-wrap:wrap;">
                                        <% if (!"pagado".equals(pagoEstado) || !"entregado".equals(entregaEstado)) { %>
                                        <form action="/Proyecto_Arreglosapp/AdminServlet" method="post" style="display:inline; width:100%;">
                                            <input type="hidden" name="accion" value="confirmarPagoEntrega">
                                            <input type="hidden" name="pedidoId" value="<%= pedidoId %>">
                                            <button type="submit" class="perfil__btn-guardar" style="padding: 12px; font-size: 14px; background: #673ab7; width:100%; font-weight:700;">
                                                🤝 Confirmar Pago y Entrega (Físico)
                                            </button>
                                        </form>
                                        <% } %>

                                        <div style="width:100%; border-top:1px solid #eee; margin:15px 0;"></div>
                                        
                                        <% if (!"pagado".equals(pagoEstado)) { %>
                                        <form action="/Proyecto_Arreglosapp/AdminServlet" method="post" style="display:inline;">
                                            <input type="hidden" name="accion" value="cambiarEstadoPago">
                                            <input type="hidden" name="pedidoId" value="<%= pedidoId %>">
                                            <button type="submit" class="perfil__btn-guardar" style="padding: 8px 12px; font-size: 12px; background: #4caf50;">
                                                💰 Solo Pago
                                            </button>
                                        </form>
                                        <% } %>

                                        <% if (!"entregado".equals(entregaEstado)) { %>
                                        <form action="/Proyecto_Arreglosapp/AdminServlet" method="post" style="display:inline;">
                                            <input type="hidden" name="accion" value="cambiarEstadoEntrega">
                                            <input type="hidden" name="pedidoId" value="<%= pedidoId %>">
                                            <button type="submit" class="perfil__btn-guardar" style="padding: 8px 12px; font-size: 12px; background: #2196f3;">
                                                📦 Solo Entrega
                                            </button>
                                        </form>
                                        <% } %>

                                        <div style="width:100%; border-top:1px solid #eee; margin:10px 0;"></div>

                                        <form action="/Proyecto_Arreglosapp/AdminServlet" method="post" style="display:inline;">
                                            <input type="hidden" name="accion" value="actualizarEstado">
                                            <input type="hidden" name="pedidoId" value="<%= pedidoId %>">
                                            <select name="nuevoEstado" style="padding:8px; border-radius:4px; border:1px solid #ddd; margin-right:5px;">
                                                <option value="pendiente" <%= "pendiente".equals(estado) ? "selected" : "" %>>Pendiente</option>
                                                <option value="confirmado" <%= "confirmado".equals(estado) ? "selected" : "" %>>Confirmado</option>
                                                <option value="en_proceso" <%= "en_proceso".equals(estado) ? "selected" : "" %>>En Proceso</option>
                                                <option value="terminado" <%= "terminado".equals(estado) ? "selected" : "" %>>Terminado</option>
                                                <option value="cancelado" <%= "cancelado".equals(estado) ? "selected" : "" %>>Cancelado</option>
                                            </select>
                                            <button type="submit" class="perfil__btn-guardar" style="padding: 8px 15px; font-size: 13px; display:inline-block; width:auto;">
                                                Actualizar Flujo
                                            </button>
                                        </form>
                                    </div>

                                    <p style="font-size:12px;color:#888;margin-top:12px;">
                                        El estado se sincroniza con el flujo de trabajo del administrador.
                                    </p>
                                </div>

                            </main>

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
                                window.addEventListener('load', function () {
                                    var params = new URLSearchParams(window.location.search);
                                    if (params.get('actualizado') === '1') {
                                        mostrarToast('✅ Estado actualizado correctamente', 'exito');
                                    }
                                });

                                function mostrarToast(msg, tipo) {
                                    var toast = document.getElementById('toast');
                                    toast.textContent = msg;
                                    toast.className = 'toast toast--' + tipo + ' toast--visible';
                                    setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                                }
                            </script>
                        </body>

                        </html>