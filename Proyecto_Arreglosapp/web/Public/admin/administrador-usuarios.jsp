<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="dao.AdminDAO" %>
        <%@ page import="model.Usuario" %>
            <%@ page import="java.util.List" %>
                <%@ page import="java.util.Map" %>
                    <%@ page import="java.util.ArrayList" %>
                        <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId()
                            !=1) { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String
                            busqueda=request.getParameter("busqueda"); AdminDAO adminDAO=new AdminDAO();
                            List<Map<String, Object>> usuarios = new ArrayList<>();
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
                                    <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
                                    <div id="toast" class="toast"></div>

                                    <header class="seccion-encabezado">
                                        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                                            alt="logo">
                                        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                                    </header>

                                    <main class="usuarios" id="contenido-principal" role="main">

                                        <form method="get" action="" class="usuarios__buscador">
                                            <img src="../../Assets/icons/lupa.png" alt="buscar"
                                                class="usuarios__buscador-icono">
                                            <input type="text" name="busqueda" class="usuarios__buscador-input"
                                                placeholder="Buscar usuario..."
                                                value="<%= busqueda != null ? busqueda : "" %>">
                                        </form>

                                        <div class="usuarios__lista">
                                            <% if (usuarios.isEmpty()) { %>
                                                <p style="color:#888;font-size:13px;text-align:center;padding:20px;">No
                                                    se encontraron usuarios</p>
                                                <% } %>
                                                    <% for (int i=0; i < usuarios.size(); i++) { Map<String, Object> u =
                                                        usuarios.get(i);
                                                        int userId = (int) u.get("userId");
                                                        String nombre = (String) u.get("nombre");
                                                        String email = (String) u.get("email");
                                                        String tel = "Sin telefono";
                                                        if (u.get("telefono") != null) {
                                                        tel = (String) u.get("telefono");
                                                        }
                                                        String inicial = "?";
                                                        if (nombre != null && !nombre.isEmpty()) {
                                                        inicial = String.valueOf(nombre.charAt(0)).toUpperCase();
                                                        }
                                                        String nombreJS = nombre.replace("'", "\\'");
                                                        String emailJS = email != null ? email.replace("'", "\\'") : "";
                                                        String telJS = tel.replace("'", "\\'");
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
                                                                <button class="usuario-card__btn-ver"
                                                                    onclick="verPerfil(<%= userId %>, '<%= nombreJS %>', '<%= emailJS %>', '<%= telJS %>')">
                                                                    Ver Perfil
                                                                </button>
                                                            </div>
                                                        </article>
                                                        <% } %>
                                        </div>
                                    </main>

                                    <!-- MODAL VER PERFIL -->
                                    <div id="modalPerfil" class="modal-overlay" style="display:none;">
                                        <div class="modal-contenido">
                                            <div class="modal-perfil__avatar">
                                                <span id="modalPerfil__inicial" class="modal-perfil__inicial"></span>
                                            </div>
                                            <h2 class="modal__titulo" id="modalPerfil__nombre"></h2>
                                            <div class="modal-perfil__datos">
                                                <div class="modal-perfil__fila">
                                                    <span class="modal-perfil__icono">✉</span>
                                                    <span id="modalPerfil__email" class="modal-perfil__valor"></span>
                                                </div>
                                                <div class="modal-perfil__fila">
                                                    <span class="modal-perfil__icono">📞</span>
                                                    <span id="modalPerfil__tel" class="modal-perfil__valor"></span>
                                                </div>
                                            </div>
                                            <div class="modal__acciones">
                                                <button class="btn-modal btn-modal--cancelar"
                                                    onclick="cerrarModalPerfil()">Cerrar</button>
                                                <button class="btn-modal btn-modal--eliminar"
                                                    onclick="confirmarEliminar(perfilUserId)">Eliminar</button>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- MODAL CONFIRMAR ELIMINAR -->
                                    <div id="modalEliminar" class="modal-overlay" style="display:none;">
                                        <div class="modal-contenido">
                                            <h2 class="modal__titulo">¿Eliminar usuario?</h2>
                                            <p class="modal__descripcion">Esta acción no se puede deshacer.</p>
                                            <p class="modal__subtitulo" id="modalNombreUsuario"></p>
                                            <div class="modal__acciones">
                                                <button class="btn-modal btn-modal--cancelar"
                                                    onclick="cerrarModalEliminar()">Cancelar</button>
                                                <form method="post" action="/Proyecto_Arreglosapp/AdminServlet"
                                                    id="formEliminar" style="flex:1;display:flex;">
                                                    <input type="hidden" name="accion" value="eliminarUsuario">
                                                    <input type="hidden" name="userId" id="inputUserId">
                                                    <button type="submit" class="btn-modal btn-modal--eliminar"
                                                        style="flex:1;">Eliminar</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>

                                    <footer class="navbar">
                                        <nav class="navbar-inferior" role="navigation" aria-label="Navegación principal">
                                            <a href="administrador-dashboard.jsp" class="navbar-inferior__item" aria-label="Dashboard">
                                                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Dashboard</span>
                                            </a>
                                            <a href="administrador-servicios.jsp" class="navbar-inferior__item" aria-label="Servicios">
                                                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Servicios</span>
                                            </a>
                                            <a href="administrador-usuarios.jsp" class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page" aria-label="Usuarios">
                                                <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Usuarios</span>
                                            </a>
                                            <a href="../client/mi-perfil.jsp" class="navbar-inferior__item" aria-label="Perfil">
                                                <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Perfil</span>
                                            </a>
                                            <a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item" aria-label="Cerrar sesión">
                                                <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Salir</span>
                                            </a>
                                        </nav>
                                    </footer>

                                    <script>
                                        var perfilUserId = 0;

                                        function verPerfil(userId, nombre, email, tel) {
                                            perfilUserId = userId;
                                            document.getElementById('modalPerfil__inicial').textContent = nombre.charAt(0).toUpperCase();
                                            document.getElementById('modalPerfil__nombre').textContent = nombre;
                                            document.getElementById('modalPerfil__email').textContent = email;
                                            document.getElementById('modalPerfil__tel').textContent = tel;
                                            document.getElementById('modalPerfil').style.display = 'flex';
                                        }

                                        function cerrarModalPerfil() {
                                            document.getElementById('modalPerfil').style.display = 'none';
                                        }

                                        function confirmarEliminar(userId) {
                                            cerrarModalPerfil();
                                            document.getElementById('modalNombreUsuario').textContent = document.getElementById('modalPerfil__nombre').textContent;
                                            document.getElementById('inputUserId').value = userId;
                                            document.getElementById('modalEliminar').style.display = 'flex';
                                        }

                                        function cerrarModalEliminar() {
                                            document.getElementById('modalEliminar').style.display = 'none';
                                        }

                                        window.addEventListener('load', function () {
                                            var params = new URLSearchParams(window.location.search);
                                            if (params.get('eliminado') === '1') {
                                                mostrarToast('Usuario eliminado correctamente', 'exito');
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