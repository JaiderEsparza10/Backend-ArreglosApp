<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO, model.Usuario, java.util.*, java.text.SimpleDateFormat" %>
        <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String
            pedidoIdStr=request.getParameter("pedidoId"); String actualizado=request.getParameter("actualizado"); if
            (pedidoIdStr==null) {
            response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp"); return; } int
            pedidoId=Integer.parseInt(pedidoIdStr); AdminDAO adminDAO=new AdminDAO(); Map<String, Object> detalle =
            null;

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

            String estadoClass = "";
            if ("pendiente".equals(estado)) estadoClass = "estado-badge--pendiente";
            else if ("en_proceso".equals(estado)) estadoClass = "estado-badge--en-taller";
            else if ("terminado".equals(estado)) estadoClass = "estado-badge--entregado";
            else estadoClass = "estado-badge--pendiente";
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

                <div id="toast" class="toast"></div>

                <header class="seccion-encabezado">
                    <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
                    <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                </header>

                <main class="detalle-pedido">

                    <a href="administrador-dashboard.jsp" class="detalle-pedido__volver">← Volver</a>

                    <h2 style="font-size:16px; font-weight:600; color:#3d0072;">
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

                    <!-- CAMBIAR ESTADO -->
                    <div class="estado-pedido">
                        <h3 class="estado-pedido__titulo">Estado del Pedido</h3>
                        <div class="estado-pedido__actual">
                            <span class="estado-pedido__actual-label">Estado actual:</span>
                            <span class="estado-badge <%= estadoClass %>">
                                <%= estado %>
                            </span>
                        </div>

                        <div class="estado-pedido__opciones" style="margin-top:16px;">
                            <% String[] estados={"pendiente", "confirmado" , "en_proceso" , "terminado" , "cancelado" };
                                String[] etiquetas={"Pendiente", "Confirmado" , "En Proceso" , "Terminado" , "Cancelado"
                                }; for (int i=0; i < estados.length; i++) { if (!estados[i].equals(estado)) { %>
                                <form method="post" action="/Proyecto_Arreglosapp/AdminServlet" style="margin:0;">
                                    <input type="hidden" name="accion" value="actualizarEstado">
                                    <input type="hidden" name="pedidoId" value="<%= pedidoId %>">
                                    <input type="hidden" name="nuevoEstado" value="<%= estados[i] %>">
                                    <button type="submit" class="estado-pedido__boton"
                                        style="width:100%; margin-top:8px;">
                                        Cambiar a: <%= etiquetas[i] %>
                                    </button>
                                </form>
                                <% } } %>
                        </div>
                    </div>

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

                <script>
                    window.addEventListener('load', function () {
                        var params = new URLSearchParams(window.location.search);
                        if (params.get('actualizado') === '1') {
                            var toast = document.getElementById('toast');
                            toast.textContent = '✅ Estado actualizado correctamente';
                            toast.className = 'toast toast--exito toast--visible';
                            setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                        }
                    });
                </script>
            </body>

            </html>