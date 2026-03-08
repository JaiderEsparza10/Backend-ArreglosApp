<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO, model.Usuario, java.util.*" %>
        <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String
            busqueda=request.getParameter("busqueda"); String eliminado=request.getParameter("eliminado"); AdminDAO
            adminDAO=new AdminDAO(); List<Map<String, Object>> usuarios = new ArrayList<>();

                try {
                usuarios = adminDAO.obtenerUsuarios(busqueda);
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
                    <title>Administrador de Usuarios</title>
                </head>

                <body class="grid-principal">

                    <div id="toast" class="toast"></div>

                    <header class="seccion-encabezado">
                        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
                        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                    </header>

                    <main class="usuarios">

                        <form method="get" action="" class="usuarios__buscador">
                            <img src="../../Assets/icons/lupa.png" alt="buscar" class="usuarios__buscador-icono">
                            <input type="text" name="busqueda" class="usuarios__buscador-input"
                                placeholder="Buscar usuario..." value="<%= busqueda != null ? busqueda : "" %>">
                        </form>

                        <div class="usuarios__lista">
                            <% if (usuarios.isEmpty()) { %>
                                <p style="color:#888; font-size:13px; text-align:center; padding:20px;">
                                    No se encontraron usuarios
                                </p>
                                <% } %>
                                    <% for (Map<String, Object> u : usuarios) {
                                        int userId = (int) u.get("userId");
                                        String nombre = (String) u.get("nombre");
                                        String email = (String) u.get("email");
                                        String tel = u.get("telefono") != null ? (String) u.get("telefono") : "Sin
                                        teléfono";
                                        String inicial = nombre != null && !nombre.isEmpty() ?
                                        String.valueOf(nombre.charAt(0)).toUpperCase() : "?";
                                        %>
                                        <article class="usuario-card">
                                            <div class="usuario-card__avatar">
                                                <span class="usuario-card__inicial">
                                                    <%= inicial %>
                                                </span>
                                            </div>
                                            <div class="usuario-card__info">
                                                <span class="usuario-card__nombre">
                                                    <%= nombre %>
                                                </span>
                                                <span class="usuario-card__email">
                                                    <%= email %>
                                                </span>
                                                <span class="usuario-card__email">
                                                    <%= tel %>
                                                </span>
                                            </div>
                                            <div class="usuario-card__acciones">
                                                <button class="usuario-card__btn-eliminar"
                                                    onclick="confirmarEliminar(<%= userId %>, '<%= nombre %>')">
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
                            <h2 class="modal__titulo">¿Eliminar usuario?</h2>
                            <p class="modal__subtitulo" id="modalNombreUsuario"></p>
                            <div class="modal__acciones">
                                <button class="btn-modal btn-modal--cancelar" onclick="cerrarModal()">Cancelar</button>
                                <form method="post" action="/Proyecto_Arreglosapp/AdminServlet" id="formEliminar">
                                    <input type="hidden" name="accion" value="eliminarUsuario">
                                    <input type="hidden" name="userId" id="inputUserId">
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
                            <a href="administrador-servicios.jsp" class="navbar-inferior__item">
                                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono">
                                <span class="navbar-inferior__texto">Servicios</span>
                            </a>
                            <a href="administrador-usuarios.jsp"
                                class="navbar-inferior__item navbar-inferior__item--activo">
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
                        function confirmarEliminar(userId, nombre) {
                            document.getElementById('modalNombreUsuario').textContent = nombre;
                            document.getElementById('inputUserId').value = userId;
                            document.getElementById('modalEliminar').style.display = 'flex';
                        }
                        function cerrarModal() {
                            document.getElementById('modalEliminar').style.display = 'none';
                        }
                        window.addEventListener('load', function () {
                            var params = new URLSearchParams(window.location.search);
                            if (params.get('eliminado') === '1') {
                                var toast = document.getElementById('toast');
                                toast.textContent = '✅ Usuario eliminado correctamente';
                                toast.className = 'toast toast--exito toast--visible';
                                setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                            }
                        });
                    </script>
                </body>

                </html>