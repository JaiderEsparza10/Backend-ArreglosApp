<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.ServicioDAO" %>
<%@ page import="model.Usuario" %>
<%@ page import="model.Servicio" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
Usuario admin = (Usuario) session.getAttribute("usuario");
if (admin == null || admin.getRolId() != 1) {
    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
    return;
}
String eliminado = request.getParameter("eliminado");
String creado = request.getParameter("creado");
String editado = request.getParameter("editado");
ServicioDAO servicioDAO = new ServicioDAO();
List<Servicio> servicios = new ArrayList<>();
try {
    servicios = servicioDAO.obtenerServicios();
} catch (Exception e) {
    e.printStackTrace();
}
String ctx = request.getContextPath();
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
<a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
<div id="toast" class="toast"></div>
<header class="seccion-encabezado">
<img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
<h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
</header>
<main class="dashboard" id="contenido-principal" role="main">
<div class="dashboard__encabezado-accion">
<h2 class="dashboard__titulo-seccion">Servicios Disponibles</h2>
<a href="crear-servicio.jsp" class="btn-agregar-servicio">+ Agregar</a>
</div>
<div class="servicios__lista">
<% if (servicios.isEmpty()) { %>
<p style="color:#888;font-size:13px;text-align:center;padding:20px;">No hay servicios registrados</p>
<% } %>
<% for (int i = 0; i < servicios.size(); i++) {
Servicio s = servicios.get(i);
String imgUrl = s.getImagenUrl();
String imgSrc = ctx + "/Assets/image/logo-app.png";
if (imgUrl != null && !imgUrl.trim().isEmpty()) {
    if (imgUrl.startsWith("../../")) {
        imgSrc = ctx + "/" + imgUrl.replace("../../", "");
    } else if (imgUrl.startsWith("Assets/")) {
        imgSrc = ctx + "/" + imgUrl;
    } else if (imgUrl.startsWith("http")) {
        imgSrc = imgUrl;
    } else {
        imgSrc = ctx + "/" + imgUrl;
    }
}
String desc = s.getDescripcion() != null ? s.getDescripcion() : "";
String descCorta = desc.length() > 60 ? desc.substring(0, 60) + "..." : desc;
String tiempo = s.getTiempoEstimado() != null ? s.getTiempoEstimado() : "No definido";
%>
<article class="servicio-card">
<img src="<%= imgSrc %>" alt="<%= s.getNombre() %>" class="servicio-card__imagen">
<div class="servicio-card__info">
<span class="servicio-card__nombre"><%= s.getNombre() %></span>
<span class="servicio-card__descripcion"><%= descCorta %></span>
<span class="servicio-card__precio">$<%= String.format("%,.0f", s.getPrecioBase()) %></span>
<span class="servicio-card__tiempo">⏱ <%= tiempo %></span>
</div>
<div class="servicio-card__acciones">
<a href="crear-servicio.jsp?id=<%= s.getArregloId() %>" class="servicio-card__btn servicio-card__btn--editar">Editar</a>
<button class="servicio-card__btn servicio-card__btn--eliminar" onclick="confirmarEliminar(<%= s.getArregloId() %>, '<%= s.getNombre() %>')">Eliminar</button>
</div>
</article>
<% } %>
</div>
</main>
<div id="modalEliminar" class="modal-overlay" style="display:none;">
<div class="modal-contenido">
<h2 class="modal__titulo">¿Eliminar servicio?</h2>
<p class="modal__descripcion">Esta acción desactivará el servicio:</p>
<p class="modal__subtitulo" id="modalNombreServicio"></p>
<div class="modal__acciones">
<button class="btn-modal btn-modal--cancelar" onclick="cerrarModal()">Cancelar</button>
<form method="post" action="/Proyecto_Arreglosapp/ServicioServlet" id="formEliminar" style="flex:1;display:flex;">
<input type="hidden" name="accion" value="eliminar">
<input type="hidden" name="arregloId" id="inputServicioId">
<button type="submit" class="btn-modal btn-modal--eliminar" style="flex:1;">Eliminar</button>
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
<a href="administrador-servicios.jsp" class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page" aria-label="Servicios">
<img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono" alt="">
<span class="navbar-inferior__texto">Servicios</span>
</a>
<a href="administrador-usuarios.jsp" class="navbar-inferior__item" aria-label="Usuarios">
<img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono" alt="">
<span class="navbar-inferior__texto">Usuarios</span>
</a>

<a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item" aria-label="Cerrar sesión">
<img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono" alt="">
<span class="navbar-inferior__texto">Salir</span>
</a>
</nav>
</footer>
<script>
function confirmarEliminar(servicioId, nombre) {
    document.getElementById('modalNombreServicio').textContent = nombre;
    document.getElementById('inputServicioId').value = servicioId;
    var modal = document.getElementById('modalEliminar');
    modal.style.display = 'flex';
}
function cerrarModal() {
    var modal = document.getElementById('modalEliminar');
    modal.style.display = 'none';
}
window.addEventListener('load', function () {
    var params = new URLSearchParams(window.location.search);
    if (params.get('eliminado') === '1') {
        mostrarToast('✅ Servicio eliminado correctamente', 'exito');
    } else if (params.get('creado') === '1') {
        mostrarToast('✅ Servicio creado correctamente', 'exito');
    } else if (params.get('editado') === '1') {
        mostrarToast('✅ Servicio actualizado correctamente', 'exito');
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