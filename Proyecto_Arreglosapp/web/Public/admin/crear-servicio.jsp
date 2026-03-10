<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="model.Usuario" %>
        <%@ page import="model.Servicio" %>
            <%@ page import="dao.ServicioDAO" %>
                <% HttpSession sesion=request.getSession(false); Usuario admin=null; if (sesion !=null) {
                    admin=(Usuario) sesion.getAttribute("usuario"); } if (admin==null || admin.getRolId() !=1) {
                    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String errorMsg=(String)
                    sesion.getAttribute("errorServicio"); if (errorMsg !=null) {
                    sesion.removeAttribute("errorServicio"); } Servicio editando=null; String
                    idParam=request.getParameter("id"); if (idParam !=null && !idParam.trim().isEmpty()) { try { int
                    sid=Integer.parseInt(idParam); ServicioDAO dao=new ServicioDAO(); editando=dao.obtenerPorId(sid); }
                    catch (Exception e) { e.printStackTrace(); } } boolean esEdicion=editando !=null; String
                    accionForm=esEdicion ? "editar" : "crear" ; String tituloBtn=esEdicion ? "Guardar Cambios"
                    : "Crear Servicio" ; String tituloPag=esEdicion ? "Editar Servicio" : "Crear Servicio" ; String
                    valNombre=esEdicion ? editando.getNombre() : "" ; String valDesc=esEdicion ?
                    (editando.getDescripcion() !=null ? editando.getDescripcion() : "" ) : "" ; String
                    valPrecio=esEdicion ? String.valueOf(editando.getPrecioBase()) : "" ; String valTiempo=esEdicion ?
                    (editando.getTiempoEstimado() !=null ? editando.getTiempoEstimado() : "" ) : "" ; int
                    valId=esEdicion ? editando.getArregloId() : 0; %>
                    <!DOCTYPE html>
                    <html lang="es">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <link rel="stylesheet" href="../../Assets/estilos.css">
                        <title>
                            <%= tituloPag %>
                        </title>
                    </head>

                    <body class="grid-principal">

                        <header class="seccion-encabezado">
                            <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
                            <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                        </header>

                        <main class="dashboard">
                            <a href="administrador-servicios.jsp" class="detalle-pedido__volver">
                                <img src="../../Assets/icons/flecha-atras-negra.png" alt="volver"
                                    class="detalle-pedido__volver-icono">
                            </a>

                            <h2 class="formulario-servicio__titulo">
                                <%= tituloPag %>
                            </h2>

                            <% if (errorMsg !=null) { %>
                                <div class="cita__alerta cita__alerta--error">❌ <%= errorMsg %>
                                </div>
                                <% } %>

                                    <form class="formulario-servicio" action="/Proyecto_Arreglosapp/ServicioServlet"
                                        method="post" enctype="multipart/form-data">

                                        <input type="hidden" name="accion" value="<%= accionForm %>">
                                        <% if (esEdicion) { %>
                                            <input type="hidden" name="arregloId" value="<%= valId %>">
                                            <% } %>

                                                <div class="formulario-servicio__grupo">
                                                    <label class="formulario-servicio__label">Nombre del
                                                        Servicio</label>
                                                    <input type="text" name="nombre" class="formulario-servicio__input"
                                                        placeholder="Costura y Reparación General..."
                                                        value="<%= valNombre %>" required>
                                                </div>

                                                <div class="formulario-servicio__grupo">
                                                    <label class="formulario-servicio__label">Descripción del
                                                        Servicio</label>
                                                    <textarea name="descripcion" class="formulario-servicio__textarea"
                                                        placeholder="Descripción del servicio..."><%= valDesc %></textarea>
                                                </div>

                                                <div class="formulario-servicio__grupo">
                                                    <label class="formulario-servicio__label">Precio Base</label>
                                                    <input type="text" name="precio" class="formulario-servicio__input"
                                                        placeholder="25000" value="<%= valPrecio %>" required>
                                                </div>

                                                <div class="formulario-servicio__grupo">
                                                    <label class="formulario-servicio__label">Tiempo Estimado</label>
                                                    <input type="text" name="tiempoEstimado"
                                                        class="formulario-servicio__input" placeholder="Ej: 2-3 días"
                                                        value="<%= valTiempo %>">
                                                </div>

                                                <% if (esEdicion && editando.getImagenUrl() !=null) { %>
                                                    <div class="formulario__imagen-actual">
                                                        <img src="<%= request.getContextPath() + " /" +
                                                            editando.getImagenUrl() %>"
                                                        alt="Imagen actual" class="preview__imagen">
                                                        <p class="imagen-actual__texto">Imagen actual — sube una nueva
                                                            para reemplazarla</p>
                                                    </div>
                                                    <% } %>

                                                        <div class="formulario__subir-foto">
                                                            <input type="file" name="imagen" id="imagenServicio"
                                                                class="subir-foto__foto" accept="image/*">
                                                            <label for="imagenServicio" class="subir-foto__agregar">
                                                                <img src="../../Assets/icons/agregar-imagen.png"
                                                                    alt="icono agregar" class="subir-foto__icono">
                                                                <span id="nombreArchivo">Subir Foto</span>
                                                            </label>
                                                        </div>

                                                        <div class="formulario__seccion-boton">
                                                            <button type="submit"
                                                                class="informacion__enlace-personalizar informacion__enlace-personalizar--modificador">
                                                                <%= tituloBtn %>
                                                            </button>
                                                        </div>
                                    </form>
                        </main>

                        <footer class="navbar">
                            <nav class="navbar-inferior">
                                <a href="administrador-dashboard.jsp" class="navbar-inferior__item">
                                    <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono">
                                    <span class="navbar-inferior__texto">Dashboard</span>
                                </a>
                                <a href="administrador-servicios.jsp"
                                    class="navbar-inferior__item navbar-inferior__item--activo">
                                    <img src="../../Assets/icons/catalogo-de-productos.png"
                                        class="navbar-inferior__icono">
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
                            document.getElementById('imagenServicio').addEventListener('change', function () {
                                var nombre = this.files[0] ? this.files[0].name : 'Subir Foto';
                                document.getElementById('nombreArchivo').textContent = nombre;
                            });
                        </script>
                    </body>

                    </html>