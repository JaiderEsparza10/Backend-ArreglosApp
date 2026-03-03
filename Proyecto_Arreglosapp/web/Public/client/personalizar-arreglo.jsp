<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="model.Usuario" %>
        <%@ page import="model.Personalizacion" %>
            <%@ page import="dao.PersonalizacionDAO" %>
                <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) {
                    usuario=(Usuario) sesion.getAttribute("usuario"); } if (usuario==null) {
                    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String errorMsg=(String)
                    sesion.getAttribute("errorPersonalizacion"); if (errorMsg !=null) {
                    sesion.removeAttribute("errorPersonalizacion"); } Personalizacion editando=null; String
                    idParam=request.getParameter("id"); if (idParam !=null && !idParam.trim().isEmpty()) { try { int
                    pid=Integer.parseInt(idParam); PersonalizacionDAO dao=new PersonalizacionDAO();
                    editando=dao.obtenerPorId(pid, usuario.getId()); } catch (Exception e) { e.printStackTrace(); } }
                    boolean esEdicion=editando !=null; String accionForm=esEdicion ? "editar" : "crear" ; String
                    tituloBtn=esEdicion ? "Guardar Cambios" : "Confirmar Arreglo" ; String tituloPag=esEdicion
                    ? "Editar Arreglo" : "Personalizar Arreglo" ; String catActual=esEdicion && editando.getCategoria()
                    !=null ? editando.getCategoria() : "" ; String claseDobladillo=catActual.equals("Dobladillo")
                    ? "formulario__seleccion formulario__seleccion--activo" : "formulario__seleccion" ; String
                    claseEstrechar=catActual.equals("Estrechar/Ensanchar")
                    ? "formulario__seleccion formulario__seleccion--activo" : "formulario__seleccion" ; String
                    claseRecortar=catActual.equals("Recortar") ? "formulario__seleccion formulario__seleccion--activo"
                    : "formulario__seleccion" ; String checkedDobladillo=catActual.equals("Dobladillo") ? "checked" : ""
                    ; String checkedEstrechar=catActual.equals("Estrechar/Ensanchar") ? "checked" : "" ; String
                    checkedRecortar=catActual.equals("Recortar") ? "checked" : "" ; String descActual=esEdicion &&
                    editando.getDescripcion() !=null ? editando.getDescripcion() : "" ; String materialActual=esEdicion
                    && editando.getMaterialTela() !=null ? editando.getMaterialTela() : "" ; int
                    descLen=descActual.length(); String placeholderFoto=esEdicion ? "Cambiar foto" : "Agregar Foto" ; %>
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
                            <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                                alt="logo de la aplicación">
                            <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                        </header>

                        <div id="toast" class="toast"></div>

                        <main class="contenido-personalizar">
                            <div class="contenido-personalizar__encabezado">
                                <a href="javascript:history.back()" class="encabezado__btn-volver">
                                    <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt="volver"
                                        class="btn-volver__icono">
                                </a>
                                <h1 class="enlace-volver__titulo">
                                    <%= tituloPag %>
                                </h1>
                            </div>

                            <% if (errorMsg !=null) { %>
                                <div class="personalizar__alerta personalizar__alerta--error">
                                    ❌ <%= errorMsg %>
                                </div>
                                <% } %>

                                    <section class="contenido-personalizar__contenedor">
                                        <form class="contenedor__formulario"
                                            action="/Proyecto_Arreglosapp/PersonalizacionServlet" method="post"
                                            enctype="multipart/form-data" id="formPersonalizar">

                                            <input type="hidden" name="accion" value="<%= accionForm %>">
                                            <% if (esEdicion) { %>
                                                <input type="hidden" name="personalizacionId"
                                                    value="<%= editando.getPersonalizacionId() %>">
                                                <% } %>

                                                    <!-- CATEGORÍA -->
                                                    <h2 class="contenedor__titulo-seccion">Categoría del Arreglo</h2>
                                                    <p class="contenedor__subtitulo">Selecciona el tipo de arreglo que
                                                        necesitas</p>

                                                    <div class="<%= claseDobladillo %>">
                                                        <input type="radio" id="dobladillo" name="categoria"
                                                            value="Dobladillo" class="seleccion__circulo"
                                                            <%=checkedDobladillo %>>
                                                        <label for="dobladillo"
                                                            class="seleccion__texto">Dobladillo</label>
                                                    </div>

                                                    <div class="<%= claseEstrechar %>">
                                                        <input type="radio" id="estrechar" name="categoria"
                                                            value="Estrechar/Ensanchar" class="seleccion__circulo"
                                                            <%=checkedEstrechar %>>
                                                        <label class="seleccion__texto" for="estrechar">Estrechar /
                                                            Ensanchar</label>
                                                    </div>

                                                    <div class="<%= claseRecortar %>">
                                                        <input type="radio" id="recortar" name="categoria"
                                                            value="Recortar" class="seleccion__circulo"
                                                            <%=checkedRecortar %>>
                                                        <label for="recortar" class="seleccion__texto">Recortar</label>
                                                    </div>

                                                    <!-- DESCRIPCIÓN -->
                                                    <h2 class="contenedor__titulo-seccion">Descripción</h2>
                                                    <div class="formulario__descripcion-contenedor">
                                                        <textarea name="descripcion" id="descripcion"
                                                            class="formulario__descripcion"
                                                            placeholder="Describe tu petición con el mayor detalle posible..."
                                                            maxlength="500"><%= descActual %></textarea>
                                                        <span class="descripcion__contador">
                                                            <span id="contadorDesc">
                                                                <%= descLen %>
                                                            </span>/500
                                                        </span>
                                                    </div>

                                                    <!-- MATERIAL / TELA -->
                                                    <h2 class="contenedor__titulo-seccion">Material / Tela</h2>
                                                    <input type="text" name="materialTela"
                                                        class="formulario__input-texto"
                                                        placeholder="Ej: Algodón, Seda, Lino, etc."
                                                        value="<%= materialActual %>">

                                                    <!-- IMAGEN DE REFERENCIA -->
                                                    <h2 class="contenedor__titulo-seccion">Imagen de Referencia</h2>
                                                    <% if (esEdicion && editando.getImagenReferencia() !=null) { %>
                                                        <div class="formulario__imagen-actual">
                                                            <img src="<%= request.getContextPath() + " /" +
                                                                editando.getImagenReferencia() %>"
                                                            alt="Imagen actual" class="preview__imagen">
                                                            <p class="imagen-actual__texto">Imagen actual — sube una
                                                                nueva para reemplazarla</p>
                                                        </div>
                                                        <% } %>

                                                            <div class="formulario__subir-foto">
                                                                <input type="file" name="imagenReferencia"
                                                                    id="imagenReferencia" class="subir-foto__foto"
                                                                    accept="image/*">
                                                                <label for="imagenReferencia"
                                                                    class="subir-foto__agregar">
                                                                    <img src="../../Assets/icons/agregar-imagen.png"
                                                                        alt="icono agregar" class="subir-foto__icono">
                                                                    <span id="nombreArchivo">
                                                                        <%= placeholderFoto %>
                                                                    </span>
                                                                </label>
                                                            </div>

                                                            <!-- PREVIEW NUEVA IMAGEN -->
                                                            <div id="previewContenedor" class="formulario__preview"
                                                                style="display:none;">
                                                                <img id="previewImagen" class="preview__imagen" src=""
                                                                    alt="Vista previa">
                                                                <button type="button" class="preview__btn-eliminar"
                                                                    onclick="quitarImagen()">
                                                                    ✕ Quitar imagen
                                                                </button>
                                                            </div>

                                                            <!-- BOTÓN CONFIRMAR -->
                                                            <div class="formulario__seccion-boton">
                                                                <button
                                                                    class="informacion__enlace-personalizar informacion__enlace-personalizar--modificador"
                                                                    type="submit" id="btnConfirmar">
                                                                    <%= tituloBtn %>
                                                                </button>
                                                            </div>
                                        </form>
                                    </section>
                        </main>

                        <footer class="navbar">
                            <nav class="navbar-inferior">
                                <a href="pagina-principal.jsp" class="navbar-inferior__item">
                                    <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono">
                                    <span class="navbar-inferior__texto">Inicio</span>
                                </a>
                                <a href="mi-seleccion.jsp" class="navbar-inferior__item">
                                    <img src="../../Assets/icons/lista-de-deseos-transparente.png"
                                        class="navbar-inferior__icono">
                                    <span class="navbar-inferior__texto">Mi selección</span>
                                </a>
                                <a href="mis-arreglos.jsp" class="navbar-inferior__item">
                                    <img src="../../Assets/icons/cortar-con-tijeras-transparente.png"
                                        class="navbar-inferior__icono">
                                    <span class="navbar-inferior__texto">Mis Arreglos</span>
                                </a>
                                <a href="mis-pedidos.jsp" class="navbar-inferior__item">
                                    <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono">
                                    <span class="navbar-inferior__texto">Pedidos</span>
                                </a>
                                <a href="mi-perfil.jsp" class="navbar-inferior__item">
                                    <img src="../../Assets/icons/usuario-transparente.png"
                                        class="navbar-inferior__icono">
                                    <span class="navbar-inferior__texto">Perfil</span>
                                </a>
                            </nav>
                        </footer>

                        <script src="../../Assets/JavaScript/personalizar-arreglo.js"></script>
                    </body>

                    </html>