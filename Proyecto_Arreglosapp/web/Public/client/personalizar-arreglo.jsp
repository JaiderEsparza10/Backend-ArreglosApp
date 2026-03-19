<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Usuario" %>
<%@ page import="model.Personalizacion" %>
<%@ page import="dao.PersonalizacionDAO" %>

<% 
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;
    
    // Redirección si no hay sesión activa
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    // Captura de mensajes de error del Servlet
    String errorMsg = (String) sesion.getAttribute("errorPersonalizacion");
    if (errorMsg != null) {
        sesion.removeAttribute("errorPersonalizacion");
    }

    // Lógica para detectar si es edición
    Personalizacion editando = null;
    String idParam = request.getParameter("id");
    if (idParam != null && !idParam.trim().isEmpty()) {
        try {
            int pid = Integer.parseInt(idParam);
            PersonalizacionDAO dao = new PersonalizacionDAO();
            editando = dao.obtenerPorId(pid, usuario.getId());
            if (editando == null) {
                // No se encontró la personalización o no pertenece al usuario
                response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=no_encontrado");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=id_invalido");
            return;
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=bd_error");
            return;
        }
    }

    boolean esEdicion = editando != null;
    String accionForm = esEdicion ? "editar" : "crear";
    String tituloBtn = esEdicion ? "Guardar Cambios" : "Confirmar Arreglo";
    String tituloPag = esEdicion ? "Editar Arreglo" : "Personalizar Arreglo";

    // Obtener el ID del servicio para marcar el radio button correcto
    int servicioIdActual = 1; // Valor por defecto: Sastrería
    if (esEdicion && editando.getServicioId() != null) {
        servicioIdActual = editando.getServicioId();
    }

    // Clases CSS dinámicas para mantener el estilo visual activo
    String claseSastreria = (servicioIdActual == 1) ? "formulario__seleccion formulario__seleccion--activo" : "formulario__seleccion";
    String claseCosturas = (servicioIdActual == 2) ? "formulario__seleccion formulario__seleccion--activo" : "formulario__seleccion";
    String clasePlanchado = (servicioIdActual == 3) ? "formulario__seleccion formulario__seleccion--activo" : "formulario__seleccion";
    String claseArreglosMedidas = (servicioIdActual == 4) ? "formulario__seleccion formulario__seleccion--activo" : "formulario__seleccion";

    // Atributos checked para los radio buttons
    String checkedSastreria = (servicioIdActual == 1) ? "checked" : "";
    String checkedCosturas = (servicioIdActual == 2) ? "checked" : "";
    String checkedPlanchado = (servicioIdActual == 3) ? "checked" : "";
    String checkedArreglosMedidas = (servicioIdActual == 4) ? "checked" : "";

    String descActual = (esEdicion && editando.getDescripcion() != null) ? editando.getDescripcion() : "";
    String materialActual = (esEdicion && editando.getMaterialTela() != null) ? editando.getMaterialTela() : "";
    int descLen = descActual.length();
    String placeholderFoto = esEdicion ? "Cambiar foto" : "Agregar Foto";
    
    // Manejo seguro del parámetro arregloId
    String arregloIdParam = request.getParameter("arregloId");
    String arregloIdValue = (arregloIdParam != null && !arregloIdParam.trim().isEmpty()) ? arregloIdParam : "";
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title><%= tituloPag %></title>
</head>
<body class="grid-principal">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
        <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
    </header>

    <div id="toast" class="toast"></div>

    <main class="contenido-personalizar">
        <div class="contenido-personalizar__encabezado">
            <a href="javascript:history.back()" class="encabezado__btn-volver">
                <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt="volver" class="btn-volver__icono">
            </a>
            <h1 class="enlace-volver__titulo"><%= tituloPag %></h1>
        </div>

        <% if (errorMsg != null) { %>
            <div class="personalizar__alerta personalizar__alerta--error">
                ❌ <%= errorMsg %>
            </div>
        <% } %>

        <section class="contenido-personalizar__contenedor">
            <form class="contenedor__formulario" action="<%= request.getContextPath() %>/PersonalizacionServlet" 
                  method="post" enctype="multipart/form-data" id="formPersonalizar">
                
                <input type="hidden" name="accion" value="<%= accionForm %>">
                <% if (esEdicion) { %>
                    <input type="hidden" name="personalizacionId" value="<%= editando.getPersonalizacionId() %>">
                <% } %>
                <input type="hidden" name="arregloId" value="<%= arregloIdValue %>">

                <h2 class="contenedor__titulo-seccion">Servicio del Arreglo</h2>
                <p class="contenedor__subtitulo">Selecciona el tipo de arreglo que necesitas</p>

                <div class="<%= claseSastreria %>">
                    <input type="radio" id="sastreria" name="idServicio" value="1" 
                           class="seleccion__circulo" <%= checkedSastreria %> required>
                    <label for="sastreria" class="seleccion__texto">Sastrería</label>
                </div>

                <div class="<%= claseCosturas %>">
                    <input type="radio" id="costuras" name="idServicio" value="2" 
                           class="seleccion__circulo" <%= checkedCosturas %>>
                    <label class="seleccion__texto" for="costuras">Costuras</label>
                </div>

                <div class="<%= clasePlanchado %>">
                    <input type="radio" id="planchado" name="idServicio" value="3" 
                           class="seleccion__circulo" <%= checkedPlanchado %>>
                    <label for="planchado" class="seleccion__texto">Planchado</label>
                </div>

                <div class="<%= claseArreglosMedidas %>">
                    <input type="radio" id="arreglosMedidas" name="idServicio" value="4" 
                           class="seleccion__circulo" <%= checkedArreglosMedidas %>>
                    <label for="arreglosMedidas" class="seleccion__texto">Arreglos de Medidas</label>
                </div>

                <h2 class="contenedor__titulo-seccion">Descripción</h2>
                <div class="formulario__descripcion-contenedor">
                    <textarea name="descripcion" id="descripcion" class="formulario__descripcion" 
                              placeholder="Describe tu petición con el mayor detalle posible..." 
                              maxlength="500" required><%= descActual %></textarea>
                    <span class="descripcion__contador">
                        <span id="contadorDesc"><%= descLen %></span>/500
                    </span>
                </div>

                <h2 class="contenedor__titulo-seccion">Material / Tela</h2>
                <input type="text" name="materialTela" class="formulario__input-texto" 
                       placeholder="Ej: Algodón, Seda, Lino, etc." value="<%= materialActual %>">

                <h2 class="contenedor__titulo-seccion">Imagen de Referencia</h2>
                <% if (esEdicion && editando.getImagenReferencia() != null) { %>
                    <div class="formulario__imagen-actual">
                        <img src="<%= request.getContextPath() + "/" + editando.getImagenReferencia() %>" 
                             alt="Imagen actual" class="preview__imagen">
                        <p class="imagen-actual__texto">Imagen actual conservada</p>
                    </div>
                <% } %>

                <div class="formulario__subir-foto">
                    <input type="file" name="imagenReferencia" id="imagenReferencia" 
                           class="subir-foto__foto" accept="image/*">
                    <label for="imagenReferencia" class="subir-foto__agregar">
                        <img src="../../Assets/icons/agregar-imagen.png" alt="icono agregar" class="subir-foto__icono">
                        <span id="nombreArchivo"><%= placeholderFoto %></span>
                    </label>
                </div>

                <div id="previewContenedor" class="formulario__preview" style="display:none;">
                    <img id="previewImagen" class="preview__imagen" src="" alt="Vista previa">
                    <button type="button" class="preview__btn-eliminar" onclick="quitarImagen()">
                        ✕ Quitar imagen
                    </button>
                </div>

                <div class="formulario__seccion-boton">
                    <button class="informacion__enlace-personalizar informacion__enlace-personalizar--modificador" 
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
            <a href="mis-arreglos.jsp" class="navbar-inferior__item">
                <img src="../../Assets/icons/cortar-con-tijeras-transparente.png" class="navbar-inferior__icono">
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