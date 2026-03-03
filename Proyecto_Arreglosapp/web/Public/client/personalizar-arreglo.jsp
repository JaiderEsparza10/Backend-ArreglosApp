<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="model.Usuario" %>
        <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) { usuario=(Usuario)
            sesion.getAttribute("usuario"); } if (usuario==null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String errorMsg=(String)
            sesion.getAttribute("errorPersonalizacion"); if (errorMsg !=null) {
            sesion.removeAttribute("errorPersonalizacion"); } %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="../../Assets/estilos.css">
                <title>Personalizar Arreglo</title>
            </head>

            <body class="grid-principal">
                <header class="seccion-encabezado">
                    <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                        alt="logo de la aplicación">
                    <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                </header>

                <!-- TOAST -->
                <div id="toast" class="toast"></div>

                <main class="contenido-personalizar">
                    <div class="contenido-personalizar__encabezado">
                        <a href="javascript:history.back()" class="encabezado__btn-volver">
                            <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt="volver"
                                class="btn-volver__icono">
                        </a>
                        <h1 class="enlace-volver__titulo">Personalizar Arreglo</h1>
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

                                    <input type="hidden" name="accion" value="crear">

                                    <!-- CATEGORÍA -->
                                    <h2 class="contenedor__titulo-seccion">Categoría del Arreglo</h2>
                                    <p class="contenedor__subtitulo">Selecciona el tipo de arreglo que necesitas</p>

                                    <div class="formulario__seleccion">
                                        <input type="radio" id="dobladillo" name="categoria" value="Dobladillo"
                                            class="seleccion__circulo">
                                        <label for="dobladillo" class="seleccion__texto">Dobladillo</label>
                                    </div>
                                    <div class="formulario__seleccion">
                                        <input type="radio" id="estrechar" name="categoria" value="Estrechar/Ensanchar"
                                            class="seleccion__circulo">
                                        <label class="seleccion__texto" for="estrechar">Estrechar / Ensanchar</label>
                                    </div>
                                    <div class="formulario__seleccion">
                                        <input type="radio" id="recortar" name="categoria" value="Recortar"
                                            class="seleccion__circulo">
                                        <label for="recortar" class="seleccion__texto">Recortar</label>
                                    </div>

                                    <!-- DESCRIPCIÓN -->
                                    <h2 class="contenedor__titulo-seccion">Descripción</h2>
                                    <div class="formulario__descripcion-contenedor">
                                        <textarea name="descripcion" id="descripcion" class="formulario__descripcion"
                                            placeholder="Describe tu petición con el mayor detalle posible..."
                                            maxlength="500"></textarea>
                                        <span class="descripcion__contador"><span id="contadorDesc">0</span>/500</span>
                                    </div>

                                    <!-- MATERIAL / TELA -->
                                    <h2 class="contenedor__titulo-seccion">Material / Tela</h2>
                                    <input type="text" name="materialTela" class="formulario__input-texto"
                                        placeholder="Ej: Algodón, Seda, Lino, etc.">

                                    <!-- IMAGEN DE REFERENCIA -->
                                    <h2 class="contenedor__titulo-seccion">Imagen de Referencia</h2>
                                    <div class="formulario__subir-foto">
                                        <input type="file" name="imagenReferencia" id="imagenReferencia"
                                            class="subir-foto__foto" accept="image/*">
                                        <label for="imagenReferencia" class="subir-foto__agregar">
                                            <img src="../../Assets/icons/agregar-imagen.png" alt="icono de agregar"
                                                class="subir-foto__icono">
                                            <span id="nombreArchivo">Agregar Foto</span>
                                        </label>
                                    </div>

                                    <!-- PREVIEW DE IMAGEN -->
                                    <div id="previewContenedor" class="formulario__preview" style="display:none;">
                                        <img id="previewImagen" class="preview__imagen" src="" alt="Vista previa">
                                        <button type="button" class="preview__btn-eliminar" onclick="quitarImagen()">✕
                                            Quitar imagen</button>
                                    </div>

                                    <!-- BOTÓN CONFIRMAR -->
                                    <div class="formulario__seccion-boton">
                                        <button
                                            class="informacion__enlace-personalizar informacion__enlace-personalizar--modificador"
                                            type="submit" id="btnConfirmar">
                                            Confirmar Arreglo
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
                            <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono">
                            <span class="navbar-inferior__texto">Perfil</span>
                        </a>
                    </nav>
                </footer>

                <script src="../../Assets/JavaScript/personalizar-arreglo.js"></script>
            </body>

            </html>