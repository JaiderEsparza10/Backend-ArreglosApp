<%-- 
    Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
    Propósito: Formulario dinámico para la creación de nuevos servicios o edición de existentes en el catálogo.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@ page import="model.Usuario" %> <%@ page import="model.Servicio" %> <%@ page import="dao.ServicioDAO" %>
                <% HttpSession sesion=request.getSession(false); Usuario admin=null; if (sesion !=null) {
                    admin=(Usuario) sesion.getAttribute("usuario"); } if (admin==null || admin.getRolId() !=1) { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String errorMsg=(String) sesion.getAttribute("errorServicio"); if (errorMsg !=null) { sesion.removeAttribute("errorServicio"); } Servicio editando=null; String idParam=request.getParameter("id"); if (idParam !=null && !idParam.trim().isEmpty()) { try { int
                    sid=Integer.parseInt(idParam); ServicioDAO dao=new ServicioDAO(); editando=dao.obtenerPorId(sid); }
                    catch (Exception e) { e.printStackTrace(); } } boolean esEdicion=editando !=null; String
                    accionForm=esEdicion ? "editar" : "crear" ; String tituloBtn=esEdicion ? "Guardar Cambios"
                    : "Crear Servicio" ; String tituloPag=esEdicion ? "Editar Servicio" : "Nuevo Servicio" ; String valNombre=esEdicion ? editando.getNombre() : "" ; String valDesc=esEdicion ? (editando.getDescripcion() !=null ? editando.getDescripcion() : "" ) : "" ; String valPrecio=esEdicion ? String.valueOf(editando.getPrecioBase()) : "" ; String valTiempo=esEdicion ? String.valueOf(editando.getTiempoEstimado()) : "" ; int
                    valId=esEdicion ? editando.getArregloId() : 0; String ctx=request.getContextPath(); %>
                    <!DOCTYPE html>
                    <html lang="es">

                    <head>
                        <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
                        <title>
                            <%= tituloPag %>
                        </title>
                    </head>

                    <body class="grid-principal"> <a href="#contenido-principal" class="skip-link">Saltar al contenido</a> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                                alt="Logo ArreglosApp"> <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                        </header>

                        <main class="form-servicio-page" id="contenido-principal" role="main"> <div class="form-servicio-page__encabezado"> <a href="administrador-servicios.jsp" class="form-servicio-page__volver"
                                    aria-label="Volver a servicios"> <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt=""
                                        class="btn-volver__icono">
                                </a>
                                <h1 class="form-servicio-page__titulo">
                                    <%= tituloPag %>
                                </h1>
                            </div>

                            <% if (errorMsg !=null) { %>
                                <div class="form-servicio-page__error" role="alert">❌ <%= errorMsg %>
                                </div>
                                <% } %>

                                    <form class="form-servicio" action="/Proyecto_Arreglosapp/ServicioServlet"
                                        method="post" enctype="multipart/form-data" novalidate> <input type="hidden" name="accion" value="<%= accionForm %>">
                                        <% if (esEdicion) { %>
                                            <input type="hidden" name="servicioId" value="<%= valId %>">
                                            <% } %>

                                                <div class="form-servicio__campo"> <label class="form-servicio__label" for="nombreServicio"> Nombre del Servicio <span aria-hidden="true">*</span>
                                                    </label>
                                                    <input type="text" id="nombreServicio" name="nombre"
                                                        class="form-servicio__input"
                                                        placeholder="Ej: Costura y Reparación General"
                                                        value="<%= valNombre %>" aria-required="true"
                                                        aria-describedby="nombreServicio-error" required> <span id="nombreServicio-error" class="campo-error" role="alert"
                                                        style="display:none;">
                                                        El nombre del servicio es obligatorio
                                                    </span>
                                                </div>

                                                <div class="form-servicio__campo"> <label class="form-servicio__label" for="descripcionServicio"> Descripción <span aria-hidden="true">*</span>
                                                    </label>
                                                    <textarea id="descripcionServicio" name="descripcion"
                                                        class="form-servicio__textarea"
                                                        placeholder="Describe el servicio..."
                                                        aria-required="true"
                                                        aria-describedby="descripcionServicio-error" required><%= valDesc %></textarea> <span id="descripcionServicio-error" class="campo-error" role="alert"
                                                        style="display:none;">
                                                        La descripción del servicio es obligatoria
                                                    </span>
                                                </div>

                                                <div class="form-servicio__fila"> <div class="form-servicio__campo"> <label class="form-servicio__label" for="precioServicio"> Precio Base <span aria-hidden="true">*</span>
                                                        </label>
                                                        <div class="form-servicio__input-prefix"> <span class="form-servicio__prefix"
                                                                aria-hidden="true">$</span> <input type="text" id="precioServicio" name="precio"
                                                                class="form-servicio__input form-servicio__input--con-prefix"
                                                                placeholder="25000" value="<%= valPrecio %>"
                                                                aria-required="true"
                                                                aria-describedby="precioServicio-error" required>
                                                        </div>
                                                        <span id="precioServicio-error" class="campo-error" role="alert"
                                                            style="display:none;">
                                                            El precio base es obligatorio
                                                        </span>
                                                    </div>
                                                    <div class="form-servicio__campo"> <label class="form-servicio__label" for="tiempoEstimado">Tiempo
                                                            Estimado (Días)</label>
                                                        <input type="number" id="tiempoEstimado" name="tiempoEstimado"
                                                            class="form-servicio__input" placeholder="Ej: 3"
                                                            value="<%= valTiempo %>" min="1"
                                                            aria-describedby="tiempoEstimado-hint"> <span id="tiempoEstimado-hint"
                                                            style="font-size:11px;color:#888;">Ingresa solo números</span>
                                                    </div>
                                                </div>

                                                <div class="form-servicio__campo"> <label class="form-servicio__label">Imagen del Servicio</label>
                                                    <% if (esEdicion && editando.getImagenUrl() !=null) { %>
                                                        <div class="form-servicio__img-actual"> <img src="<%= ctx + " /" + editando.getImagenUrl().replace("../../", "" ) %>"
                                                            alt="Imagen actual del servicio"
                                                            class="form-servicio__img-preview"> <p class="form-servicio__img-texto">Imagen actual — sube una
                                                                nueva para reemplazarla</p>
                                                        </div>
                                                        <% } %>
                                                            <label for="imagenServicio"
                                                                class="form-servicio__upload-label" id="uploadLabel"
                                                                aria-label="Subir imagen del servicio"> <img src="../../Assets/icons/agregar-imagen.png" alt=""
                                                                    class="form-servicio__upload-icono"> <span id="nombreArchivo">Toca para subir una foto</span>
                                                            </label>
                                                            <input type="file" name="imagen" id="imagenServicio"
                                                                class="form-servicio__upload-input" accept="image/*"
                                                                aria-describedby="imagenServicio-hint"> <span id="imagenServicio-hint"
                                                                style="font-size:11px;color:#888;display:block;margin-top:4px;">Opcional
                                                                — formatos: JPG, PNG, WEBP</span>
                                                </div>

                                                <div id="previewContenedor" class="form-servicio__preview"
                                                    style="display:none;"> <img id="previewImagen" class="form-servicio__img-preview" src=""
                                                        alt="Vista previa de la imagen seleccionada"> <button type="button" class="form-servicio__quitar-img"
                                                        onclick="quitarImagen()"
                                                        aria-label="Quitar imagen seleccionada">
                                                        ✕ Quitar imagen
                                                    </button>
                                                </div>

                                                <button type="submit" class="form-servicio__btn-submit">
                                                    <%= tituloBtn %>
                                                </button>

                                    </form>
                        </main>

                        <footer class="navbar"> <nav class="navbar-inferior" role="navigation" aria-label="Navegación principal"> <a href="administrador-dashboard.jsp" class="navbar-inferior__item"
                                    aria-label="Dashboard"> <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono"
                                        alt=""> <span class="navbar-inferior__texto">Dashboard</span>
                                </a>
                                <a href="administrador-servicios.jsp"
                                    class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page"
                                    aria-label="Servicios"> <img src="../../Assets/icons/catalogo-de-productos.png"
                                        class="navbar-inferior__icono" alt=""> <span class="navbar-inferior__texto">Servicios</span>
                                </a>
                                <a href="administrador-usuarios.jsp" class="navbar-inferior__item"
                                    aria-label="Usuarios"> <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono"
                                        alt=""> <span class="navbar-inferior__texto">Usuarios</span>
                                </a>
                                <a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item"
                                    aria-label="Cerrar sesion"> <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono"
                                        alt=""> <span class="navbar-inferior__texto">Salir</span>
                                </a>
                            </nav>
                        </footer>

                        <script>
                            document.getElementById('imagenServicio').addEventListener('change', function () {
                                var archivo = this.files[0];
                                if (archivo) {
                                    document.getElementById('nombreArchivo').textContent = archivo.name;
                                    var reader = new FileReader();
                                    reader.onload = function (e) {
                                        document.getElementById('previewImagen').src = e.target.result;
                                        document.getElementById('previewContenedor').style.display = 'block';
                                    };
                                    reader.readAsDataURL(archivo);
                                }
                            });

                            function quitarImagen() {
                                document.getElementById('imagenServicio').value = '';
                                document.getElementById('nombreArchivo').textContent = 'Toca para subir una foto';
                                document.getElementById('previewContenedor').style.display = 'none';
                            }

                            document.querySelector('.form-servicio').addEventListener('submit', function (e) {
                                var valido = true;
                                var campos = this.querySelectorAll('[aria-required="true"]');
                                campos.forEach(function (campo) {
                                    var errorId = campo.getAttribute('aria-describedby');
                                    var errorEl = document.getElementById(errorId);
                                    if (!campo.value.trim()) {
                                        campo.setAttribute('aria-invalid', 'true');
                                        if (errorEl) errorEl.style.display = 'flex';
                                        valido = false;
                                    } else {
                                        campo.setAttribute('aria-invalid', 'false');
                                        if (errorEl) errorEl.style.display = 'none';
                                    }
                                });
                                if (!valido) e.preventDefault();
                            });
                        </script>
                    </body>

                    </html>