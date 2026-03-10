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
                        double precio = (double) detalle.get("precio");
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

                                <!-- ESTADO DEL PEDIDO - SOLO LECTURA -->
                                <div class="estado-pedido">
                                    <h3 class="estado-pedido__titulo">Estado del Pedido</h3>
                                    <div class="estado-pedido__actual">
                                        <span class="estado-pedido__actual-label">Estado actual:</span>
                                        <span class="estado-badge <%= estadoClass %>">
                                            <%= estadoLabel %>
                                        </span>
                                    </div>
                                    <p style="font-size:12px;color:#888;margin-top:12px;">
                                        El estado se actualiza automáticamente al gestionar la cita desde el Dashboard.
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