<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@ page import="model.Usuario" %> <%@ page import="model.Servicio" %> <%@ page import="dao.ServicioDAO" %>
                <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) {
                    usuario=(Usuario) sesion.getAttribute("usuario"); } if (usuario==null) { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String idParam=request.getParameter("id"); Servicio servicio=null; if (idParam !=null &&
                    !idParam.trim().isEmpty()) { try { int sid=Integer.parseInt(idParam); ServicioDAO dao=new
                    ServicioDAO(); servicio=dao.obtenerPorId(sid); } catch (Exception e) { e.printStackTrace(); } } if
                    (servicio==null) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/client/pagina-principal.jsp"); return; } String
                    ctx=request.getContextPath(); String imgUrl=servicio.getImagenUrl(); String imgSrc=ctx
                    + "/Assets/image/logo-app.png" ; if (imgUrl !=null && !imgUrl.trim().isEmpty()) { if (imgUrl.startsWith("../../")) { imgSrc=ctx + "/" + imgUrl.replace("../../", "" ); } else if (imgUrl.startsWith("Assets/")) { imgSrc=ctx + "/" + imgUrl; } else if (imgUrl.startsWith("http")) { imgSrc=imgUrl; } else { imgSrc=ctx + "/" + imgUrl; } } String nombre=servicio.getNombre() !=null ? servicio.getNombre() : "" ; String descripcion=servicio.getDescripcion() !=null ? servicio.getDescripcion() : "Sin descripcion" ; String tiempo=servicio.getTiempoEstimado() !=null ? servicio.getTiempoEstimado() : "No definido" ; String precio=String.format("%,.0f",
                    servicio.getPrecioBase()); int servicioId=servicio.getArregloId(); double
                    precioDouble=servicio.getPrecioBase(); String imgUrlEncoded=imgUrl !=null ? imgUrl : "" ; %>
                    <!DOCTYPE html>
                    <html lang="es">

                    <head>
                        <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
                        <title>
                            <%= nombre %>
                        </title>
                    </head>

                    <body class="grid-principal"> <div id="toast" class="toast"></div> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo"> <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                        </header>

                        <main class="detalle-servicio"> <div class="detalle-servicio__encabezado"> <a href="pagina-principal.jsp" class="detalle-servicio__volver"> <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt="volver"
                                        class="btn-volver__icono">
                                </a>
                                <h1 class="detalle-servicio__titulo">
                                    <%= nombre %>
                                </h1>
                            </div>

                            <div class="detalle-servicio__card"> <img src="<%= imgSrc %>" alt="<%= nombre %>" class="detalle-servicio__card-imagen"> <div class="detalle-servicio__card-info"> <h2 class="detalle-servicio__card-nombre">
                                        <%= nombre %>
                                    </h2>
                                    <span class="detalle-servicio__card-precio">$<%= precio %></span> <span class="detalle-servicio__card-tiempo">⏱ <%= tiempo %></span>
                                </div>
                            </div>

                            <div class="detalle-servicio__descripcion-contenedor"> <h2 class="detalle-servicio__subtitulo">Descripción</h2> <p class="detalle-servicio__descripcion">
                                    <%= descripcion %>
                                </p>
                            </div>

                            <div class="detalle-servicio__acciones"> <button class="detalle-servicio__btn detalle-servicio__btn--seleccion" id="btnAgregar"
                                    onclick="agregarASeleccion()">
                                    ♡ Agregar a Mi Selección
                                </button>
                                <a href="personalizar-arreglo.jsp"
                                    class="detalle-servicio__btn detalle-servicio__btn--personalizar">
                                    ✂ Personalizar Arreglo
                                </a>
                            </div>
                        </main>

                        <footer class="navbar"> <nav class="navbar-inferior"> <a href="pagina-principal.jsp"
                                    class="navbar-inferior__item navbar-inferior__item--activo"> <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Inicio</span>
                                </a>
                                <a href="mi-seleccion.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/lista-de-deseos-transparente.png"
                                        class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Mi selección</span>
                                </a>
                                <a href="mis-arreglos.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/cortar-con-tijeras-transparente.png"
                                        class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Mis Arreglos</span>
                                </a>
                                <a href="mis-pedidos.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Pedidos</span>
                                </a>
                                <a href="mi-perfil.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/usuario-transparente.png"
                                        class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Perfil</span>
                                </a>
                            </nav>
                        </footer>

                        <script>
                            var servicioId = <%= servicioId %>;
                            var nombre = "<%= nombre.replace("\"", "\\\"") %> ";
                            var precioVal = <%= precioDouble %>;
                            var imagenUrl = "<%= imgUrlEncoded.replace("\"", "\\\"") %> ";

                            function agregarASeleccion() {
                                var btn = document.getElementById('btnAgregar');
                                btn.disabled = true;
                                btn.textContent = 'Agregando...';

                                var params = new URLSearchParams();
                                params.append('accion', 'agregar');
                                params.append('arregloId', servicioId);
                                params.append('categoria', nombre);
                                params.append('nombreCategoria', nombre);
                                params.append('precio', precioVal);
                                params.append('imagenUrl', imagenUrl);

                                fetch('/Proyecto_Arreglosapp/FavoritoServlet', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                    body: params.toString()
                                })
                                    .then(function (res) { return res.json(); })
                                    .then(function (data) {
                                        if (data.success) {
                                            mostrarToast(data.message, 'exito');
                                            btn.textContent = '✓ En Mi Selección';
                                        } else {
                                            mostrarToast(data.message, 'error');
                                            btn.disabled = false;
                                            btn.textContent = '♡ Agregar a Mi Selección';
                                        }
                                    })
                                    .catch(function () {
                                        mostrarToast('Error de conexion', 'error');
                                        btn.disabled = false;
                                        btn.textContent = '♡ Agregar a Mi Selección';
                                    });
                            }

                            function mostrarToast(msg, tipo) {
                                var toast = document.getElementById('toast');
                                toast.textContent = msg;
                                toast.className = 'toast toast--' + (tipo === 'exito' ? 'exito' : 'error') + ' toast--visible';
                                setTimeout(function () {
                                    toast.classList.remove('toast--visible');
                                }, 3000);
                            }
                        </script>
                    </body>

                    </html>