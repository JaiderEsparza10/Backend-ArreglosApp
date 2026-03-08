<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO, model.Usuario, java.util.*" %>
        <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String
            eliminado=request.getParameter("eliminado"); AdminDAO adminDAO=new AdminDAO(); List<Map<String, Object>>
            servicios = new ArrayList<>();

                try {
                servicios = adminDAO.obtenerServicios();
                } catch (Exception e) {
                e.printStackTrace();
                }
                %>
                <!DOCTYPE html>
                <html lang="es">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <link rel="stylesheet" href="../../Assets/estilos.css">
                    <title>Servicios</title>
                </head>

                <body class="grid-principal">

                    <div id="toast" class="toast"></div>

                    <header class="seccion-encabezado">
                        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
                        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                    </header>

                    <main class="dashboard">
                        <h2 class="dashboard__titulo-seccion">Servicios Disponibles</h2>

                        <div class="servicios__lista">
                            <% if (servicios.isEmpty()) { %>
                                <p style="color:#888; font-size:13px; text-align:center; padding:20px;">
                                    No hay servicios registrados
                                </p>
                                <% } %>
                                    <% for (Map<String, Object> s : servicios) {
                                        int servicioId = (int) s.get("servicioId");
                                        String nombre = (String) s.get("nombre");
                                        String desc = (String) s.get("descripcion");
                                        double precio = (double) s.get("precio");
                                        String imagen = (String) s.get("imagen");
                                        String imgSrc = imagen != null ? "../../Assets/image/" + imagen :
                                        "../../Assets/image/logo-app.png";
                                        %>
                                        <article class="servicio-card">
                                            <img src="<%= imgSrc %>" alt="<%= nombre %>" class="servicio-card__imagen">
                                            <div class="servicio-card__info">
                                                <span class="servicio-card__nombre">
                                                    <%= nombre %>
                                                </span>
                                                <span class="servicio-card__descripcion">
                                                    <%= desc %>
                                                </span>
                                                <span class="servicio-card__precio">$<%= String.format("%,.0f", precio)
                                                        %></span>
                                            </div>
                                            <div class="servicio-card__acciones">
                                                <button class="servicio-card__btn servicio-card__btn--eliminar"
                                                    onclick="confirmarEliminar(<%= servicioId %>, '<%= nombre %>')">
                                                    Eliminar
                                                </button>
                                            </div>
                                        </article>
                                        <% } %>
                        </div>
                    </main>

                    <!-- Modal eliminar -->
                    <div id="modalEliminar" class="modal-overlay" style="display:none;">
                        <div class="modal-contenido">
                            <h2 class="modal__titulo">¿Eliminar servicio?</h2>
                            <p class="modal__subtitulo" id="modalNombreServicio"></p>
                            <div class="modal__acciones">
                                <button class="btn-modal btn-modal--cancelar" onclick="cerrarModal()">Cancelar</button>
                                <form method="post" action="/Proyecto_Arreglosapp/AdminServlet" id="formEliminar">
                                    <input type="hidden" name="accion" value="eliminarServicio">
                                    <input type="hidden" name="servicioId" id="inputServicioId">
                                    <button type="submit" class="btn-modal btn-modal--eliminar">Eliminar</button>
                                </form>
                            </div>
                        </div>
                    </div>

                    <footer class="navbar">
                        <nav class="navbar-inferior">
                            <a href="administrador-dashboard.jsp" class="navbar-inferior__item">
                                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono">
                                <span class="navbar-inferior__texto">Dashboard</span>
                            </a>
                            <a href="administrador-servicios.jsp"
                                class="navbar-inferior__item navbar-inferior__item--activo">
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
                        function confirmarEliminar(servicioId, nombre) {
                            document.getElementById('modalNombreServicio').textContent = nombre;
                            document.getElementById('inputServicioId').value = servicioId;
                            document.getElementById('modalEliminar').style.display = 'flex';
                        }
                        function cerrarModal() {
                            document.getElementById('modalEliminar').style.display = 'none';
                        }
                        window.addEventListener('load', function () {
                            var params = new URLSearchParams(window.location.search);
                            if (params.get('eliminado') === '1') {
                                var toast = document.getElementById('toast');
                                toast.textContent = '✅ Servicio eliminado correctamente';
                                toast.className = 'toast toast--exito toast--visible';
                                setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                            }
                        });
                    </script>
                </body>

                </html>