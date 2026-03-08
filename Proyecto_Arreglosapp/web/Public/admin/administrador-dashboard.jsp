<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO, model.Usuario, java.util.*, java.text.SimpleDateFormat" %>
        <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } AdminDAO adminDAO=new AdminDAO(); int
            totalPedidosActivos=0; int totalCitasHoy=0; List<Map<String, Object>> pedidosRecientes = new ArrayList<>();

                try {
                totalPedidosActivos = adminDAO.contarPedidosActivos();
                totalCitasHoy = adminDAO.contarCitasHoy();
                pedidosRecientes = adminDAO.obtenerPedidosRecientes();
                } catch (Exception e) {
                e.printStackTrace();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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

                    <header class="seccion-encabezado">
                        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
                        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                    </header>

                    <main class="dashboard">

                        <section class="dashboard__estadisticas">
                            <article class="estadistica">
                                <div class="estadistica__contenido">
                                    <span class="estadistica__valor">
                                        <%= totalPedidosActivos %>
                                    </span>
                                    <img class="estadistica__icono" src="../../Assets/icons/hacia-adelante.png"
                                        alt="flecha">
                                </div>
                                <span class="estadistica__label">Pedidos activos</span>
                            </article>
                            <article class="estadistica">
                                <div class="estadistica__contenido">
                                    <span class="estadistica__valor">
                                        <%= totalCitasHoy %>
                                    </span>
                                    <img class="estadistica__icono" src="../../Assets/icons/hacia-adelante.png"
                                        alt="flecha">
                                </div>
                                <span class="estadistica__label">Citas hoy</span>
                            </article>
                        </section>

                        <section class="dashboard__seccion-pedidos">
                            <h2 class="pedidos__titulo">Pedidos Recientes</h2>
                            <div class="pedidos__lista">
                                <% if (pedidosRecientes.isEmpty()) { %>
                                    <p style="color:#888; font-size:13px; text-align:center; padding:20px;">
                                        No hay pedidos recientes
                                    </p>
                                    <% } %>
                                        <% for (Map<String, Object> pedido : pedidosRecientes) {
                                            int pedidoId = (int) pedido.get("pedidoId");
                                            String estado = (String) pedido.get("estado");
                                            String cliente = (String) pedido.get("cliente");
                                            java.sql.Timestamp citaFecha = (java.sql.Timestamp) pedido.get("citaFecha");
                                            String citaStr = citaFecha != null ? sdf.format(citaFecha) : "Sin cita";
                                            String estadoClass = "";
                                            if ("pendiente".equals(estado)) estadoClass = "pedido__estado--pendiente";
                                            else if ("en_proceso".equals(estado)) estadoClass =
                                            "pedido__estado--en-taller";
                                            else if ("terminado".equals(estado)) estadoClass =
                                            "pedido__estado--entregado";
                                            else estadoClass = "pedido__estado--pendiente";
                                            %>
                                            <article class="pedido">
                                                <div class="pedido__contenido">
                                                    <span class="pedido__id">ID: #P-<%= String.format("%05d", pedidoId)
                                                            %></span>
                                                    <div class="pedido__info-cliente">
                                                        <div class="pedido__cliente-row">
                                                            <span class="pedido__label">Cliente:</span>
                                                            <span class="pedido__cliente">
                                                                <%= cliente %>
                                                            </span>
                                                        </div>
                                                        <span class="pedido__cita">Cita: <%= citaStr %></span>
                                                    </div>
                                                </div>
                                                <div class="pedido__acciones">
                                                    <span class="pedido__estado <%= estadoClass %>">
                                                        <%= estado %>
                                                    </span>
                                                    <a href="detalle-pedido-admin.jsp?pedidoId=<%= pedidoId %>"
                                                        class="pedido__enlace">Detalles</a>
                                                </div>
                                            </article>
                                            <% } %>
                            </div>
                        </section>
                    </main>

                    <footer class="navbar">
                        <nav class="navbar-inferior">
                            <a href="administrador-dashboard.jsp"
                                class="navbar-inferior__item navbar-inferior__item--activo">
                                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono">
                                <span class="navbar-inferior__texto">Dashboard</span>
                            </a>
                            <a href="administrador-servicios.jsp" class="navbar-inferior__item">
                                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono">
                                <span class="navbar-inferior__texto">Servicios</span>
                            </a>
                            <a href="administrador-usuarios.jsp" class="navbar-inferior__item">
                                <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono">
                                <span class="navbar-inferior__texto">Usuarios</span>
                            </a>
                            <a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item">
                                <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono">
                                <span class="navbar-inferior__texto">Salir</span>
                            </a>
                        </nav>
                    </footer>
                </body>

                </html>