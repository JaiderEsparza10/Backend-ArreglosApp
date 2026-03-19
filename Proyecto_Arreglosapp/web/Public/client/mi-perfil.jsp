<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Usuario" %>
<%@ page import="dao.UsuarioDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>

<% 
    /**
     * VISTA: Perfil del Usuario (Cliente).
     * Propósito: Visualizar información personal, resumen de actividad y gestionar credenciales.
     * Requisitos Funcionales: RF3, RF4, RF20.
     * Seguridad: Requiere sesión activa de Cliente. Redirige a Admin Dashboard si el rol es Admin.
     */
    HttpSession sesion = request.getSession(false); 
    Usuario usuario = null; 
    
    if (sesion != null) {
        usuario = (Usuario) sesion.getAttribute("usuario"); 
    } 

    if (usuario == null) {
        response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); 
        return; 
    } 

    // Redirección lógica para administradores que intentan entrar a vista de cliente
    if (usuario.getRolId() == 1) { 
        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp"); 
        return; 
    }

    UsuarioDAO usuarioDAO = new UsuarioDAO(); 
    String errorPerfil = (String) sesion.getAttribute("errorPerfil");  String errorPassword = (String) sesion.getAttribute("errorPassword");  sesion.removeAttribute("errorPerfil"); sesion.removeAttribute("errorPassword"); 

    int totalPedidos = 0; 
    int totalFavoritos = 0; 
    int totalPersonalizaciones = 0; 
    String telefono = ""; 

    try {
        // Carga de contadores para el dashboard de cliente
        totalPedidos = usuarioDAO.contarPedidosActivos(usuario.getId());
        totalFavoritos = usuarioDAO.contarFavoritos(usuario.getId());
        totalPersonalizaciones = usuarioDAO.contarPersonalizaciones(usuario.getId());
        
        String tel = usuarioDAO.obtenerTelefonoPrincipal(usuario.getId()); 
        telefono = tel != null ? tel : ""; 

        // Recuperación de mensajes automáticos (RF20)
        List<Map<String, Object>> notificaciones = usuarioDAO.obtenerNotificaciones(usuario.getId());
        request.setAttribute("notificaciones", notificaciones);
    } catch (Exception e) { 
        e.printStackTrace(); 
    } 

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); String nombreActual = usuario.getNombre() != null ? usuario.getNombre() : "";  String emailActual = usuario.getEmail() != null ? usuario.getEmail() : ""; String direccionActual = usuario.getDireccion() != null ? usuario.getDireccion() : "";  String rolTexto = usuario.getRolId() == 1 ? "Administrador" : "Cliente";  String inicialNombre = nombreActual.isEmpty() ? "U" : String.valueOf(nombreActual.charAt(0)).toUpperCase(); 
%>
                <!DOCTYPE html>
                <html lang="es">

                <head>
                    <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
                    <title>Mi Perfil</title>
                </head>

                <body class="grid-principal"> <a href="#contenido-principal" class="skip-link">Saltar al contenido</a> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                            alt="logo de la aplicación"> <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                    </header>

                    <div id="toast" class="toast"></div> <main class="contenido-perfil" id="contenido-principal" role="main">

                        <!-- ENCABEZADO -->
                        <div class="contenido-perfil__enlace-volver"> <a href="javascript:history.back()" class="encabezado__btn-volver"> <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt="volver"
                                    class="btn-volver__icono">
                            </a>
                            <h1 class="enlace-volver__titulo">Mi Perfil</h1>
                        </div>

                        <!-- ALERTAS -->
                        <% if (errorPerfil !=null) { %>
                            <div class="perfil__alerta perfil__alerta--error">❌ <%= errorPerfil %>
                            </div>
                            <% } %>
                                <% if (errorPassword !=null) { %>
                                    <div class="perfil__alerta perfil__alerta--error">❌ <%= errorPassword %>
                                    </div>
                                    <% } %>

                                        <!-- TARJETA DE PERFIL -->
                                        <section class="perfil-informacion"> <div class="perfil__avatar"> <span class="perfil__inicial">
                                                    <%= inicialNombre %>
                                                </span>
                                            </div>
                                            <div class="perfil__datos"> <h2 class="perfil__nombre">
                                                    <%= nombreActual %>
                                                </h2>
                                                <p class="perfil__email">
                                                    <%= emailActual %>
                                                </p>
                                                <% if (!direccionActual.isEmpty()) { %>
                                                    <p class="perfil__direccion">📍 <%= direccionActual %>
                                                    </p>
                                                    <% } %>
                                                        <% if (!telefono.isEmpty()) { %>
                                                            <p class="perfil__telefono">📞 <%= telefono %>
                                                            </p>
                                                            <% } %>
                                                                <span class="perfil__rol">
                                                                    <%= rolTexto %>
                                                                </span>
                                            </div>
                                        </section>

                                        <!-- RESUMEN DE ACTIVIDAD -->
                                        <section class="perfil-resumen"> <h3 class="resumen__titulo">Resumen de Actividad</h3> <div class="resumen__grid"> <a href="mis-pedidos.jsp" class="resumen__item"> <h4 class="resumen__numero">
                                                        <%= totalPedidos %>
                                                    </h4>
                                                    <p class="resumen__texto">Pedidos Activos</p>
                                                </a>
                                                <a href="mi-seleccion.jsp" class="resumen__item"> <h4 class="resumen__numero">
                                                        <%= totalFavoritos %>
                                                    </h4>
                                                    <p class="resumen__texto">Favoritos</p>
                                                </a>
                                                <a href="mis-arreglos.jsp" class="resumen__item"> <h4 class="resumen__numero">
                                                        <%= totalPersonalizaciones %>
                                                    </h4>
                                                    <p class="resumen__texto">Personalizaciones</p>
                                                </a>
                                            </div>
                                        </section>

                                        <!-- EDITAR DATOS -->
                                        <section class="perfil-seccion" id="editarDatos"> <div class="perfil-seccion__cabecera"
                                                onclick="toggleSeccion('formEditarDatos')"> <h3 class="perfil-seccion__titulo">✏️ Editar Datos Personales</h3> <span class="perfil-seccion__flecha" id="flechaFormEditarDatos">▼</span>
                                            </div>
                                            <div class="perfil-seccion__contenido" id="formEditarDatos"
                                                style="display:none;"> <form action="/Proyecto_Arreglosapp/PerfilServlet" method="post"
                                                    class="perfil__form"> <input type="hidden" name="accion" value="editarDatos"> <label class="perfil__label">Nombre completo</label> <input type="text" name="nombre" class="perfil__input"
                                                        value="<%= nombreActual %>" required> <label class="perfil__label">Dirección</label> <input type="text" name="direccion" class="perfil__input"
                                                        value="<%= direccionActual %>"
                                                        placeholder="Ej: Calle 10 # 5-20, Barrio Centro"> <label class="perfil__label">Teléfono</label> <input type="tel" name="telefono" class="perfil__input"
                                                        value="<%= telefono %>" placeholder="Ej: 3001234567"
                                                        pattern="[0-9]*" inputmode="numeric"
                                                        oninput="this.value = this.value.replace(/[^0-9]/g, '')"> <button type="submit" class="perfil__btn-guardar">Guardar
                                                        Cambios</button>
                                                </form>
                                            </div>
                                        </section>

                                        <!-- MENSAJES AUTOMÁTICOS (RF20) -->
                                        <section class="perfil-seccion" id="notificaciones"> <div class="perfil-seccion__cabecera" onclick="toggleSeccion('listaNotificaciones')"> <h3 class="perfil-seccion__titulo">📩 Mensajes y Notificaciones</h3> <span class="perfil-seccion__flecha" id="flechaListaNotificaciones">▼</span>
                                            </div>
                                            <div class="perfil-seccion__contenido" id="listaNotificaciones" style="display:none; padding: 10px;">
                                                <% 
                                                List<Map<String, Object>> notifs = (List<Map<String, Object>>) request.getAttribute("notificaciones");
                                                if (notifs != null && !notifs.isEmpty()) {
                                                    for (Map<String, Object> n : notifs) {
                                                        String msgNotif = (String) n.get("mensaje");
                                                        String lowerMsg = msgNotif.toLowerCase();
                                                        String claseItem = "";
                                                        
                                                        if (lowerMsg.contains("completada") || lowerMsg.contains("confirmada") || lowerMsg.contains("éxito") || lowerMsg.contains("exito")) {
                                                            claseItem = "notificacion-item--exito";
                                                        } else if (lowerMsg.contains("cancelada") || lowerMsg.contains("error") || lowerMsg.contains("rechazada")) {
                                                            claseItem = "notificacion-item--error";
                                                        } else if (lowerMsg.contains("pendiente") || lowerMsg.contains("proceso")) {
                                                            claseItem = "notificacion-item--alerta";
                                                        }
                                                %>
                                                    <div class="notificacion-item <%= claseItem %>">
                                                        <p class="notificacion-item__mensaje"><%= msgNotif %></p>
                                                        <span class="notificacion-item__fecha"><%= sdf.format(n.get("fecha")) %></span>
                                                    </div>
                                                <% 
                                                    }
                                                } else { 
                                                %>
                                                    <p style="text-align: center; color: #666; font-size: 14px; padding: 20px;">No tienes notificaciones aún.</p>
                                                <% } %>
                                            </div>
                                        </section>

                                        <!-- CAMBIAR CONTRASEÑA -->
                                        <section class="perfil-seccion" id="cambiarPassword"> <div class="perfil-seccion__cabecera"
                                                onclick="toggleSeccion('formPassword')"> <h3 class="perfil-seccion__titulo">🔒 Cambiar Contraseña</h3> <span class="perfil-seccion__flecha" id="flechaFormPassword">▼</span>
                                            </div>
                                            <div class="perfil-seccion__contenido" id="formPassword"
                                                style="display:none;"> <form action="/Proyecto_Arreglosapp/PerfilServlet" method="post"
                                                    class="perfil__form"> <input type="hidden" name="accion" value="cambiarPassword"> <label class="perfil__label">Contraseña actual</label> <div class="perfil__input-password"> <input type="password" name="passwordActual" id="passActual"
                                                            class="perfil__input" required> <button type="button" class="perfil__ojo"
                                                            onclick="togglePass('passActual', 'ojoActual')"> <span id="ojoActual">👁</span>
                                                        </button>
                                                    </div>

                                                    <label class="perfil__label">Nueva contraseña</label> <div class="perfil__input-password"> <input type="password" name="passwordNueva" id="passNueva"
                                                            class="perfil__input" required minlength="6"> <button type="button" class="perfil__ojo"
                                                            onclick="togglePass('passNueva', 'ojoNueva')"> <span id="ojoNueva">👁</span>
                                                        </button>
                                                    </div>

                                                    <label class="perfil__label">Confirmar nueva contraseña</label> <div class="perfil__input-password"> <input type="password" name="passwordConfirmar" id="passConfirm"
                                                            class="perfil__input" required minlength="6"> <button type="button" class="perfil__ojo"
                                                            onclick="togglePass('passConfirm', 'ojoConfirm')"> <span id="ojoConfirm">👁</span>
                                                        </button>
                                                    </div>

                                                    <button type="submit" class="perfil__btn-guardar">Cambiar
                                                        Contraseña</button>
                                                </form>
                                            </div>
                                        </section>

                                        <!-- CERRAR SESIÓN -->
                                        <div class="perfil__cerrar-sesion"> <a href="/Proyecto_Arreglosapp/LogoutServlet"
                                                class="perfil__btn-cerrar-sesion">
                                                Cerrar Sesión
                                            </a>
                                        </div>

                    </main>

                    <footer class="navbar"> <nav class="navbar-inferior" role="navigation" aria-label="Navegación principal"> <a href="pagina-principal.jsp" class="navbar-inferior__item" aria-label="Inicio"> <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono" alt=""> <span class="navbar-inferior__texto">Inicio</span>
                            </a>
                            <a href="mi-seleccion.jsp" class="navbar-inferior__item" aria-label="Mi selección"> <img src="../../Assets/icons/lista-de-deseos-transparente.png" class="navbar-inferior__icono" alt=""> <span class="navbar-inferior__texto">Mi selección</span>
                            </a>
                            <a href="mis-arreglos.jsp" class="navbar-inferior__item" aria-label="Mis Arreglos"> <img src="../../Assets/icons/cortar-con-tijeras-transparente.png" class="navbar-inferior__icono" alt=""> <span class="navbar-inferior__texto">Mis Arreglos</span>
                            </a>
                            <a href="mis-pedidos.jsp" class="navbar-inferior__item" aria-label="Pedidos"> <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono" alt=""> <span class="navbar-inferior__texto">Pedidos</span>
                            </a>
                            <a href="mi-perfil.jsp" class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page" aria-label="Perfil"> <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono" alt=""> <span class="navbar-inferior__texto">Perfil</span>
                            </a>
                        </nav>
                    </footer>

                    <script src="../../Assets/JavaScript/mi-perfil.js"></script>
                </body>

                </html>