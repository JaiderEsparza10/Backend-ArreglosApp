<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="model.Usuario" %>
        <%@ page import="model.Servicio" %>
            <%@ page import="dao.ServicioDAO" %>
                <%@ page import="java.util.List" %>
                    <%@ page import="java.util.ArrayList" %>
                        <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) {
                            usuario=(Usuario) sesion.getAttribute("usuario"); } if (usuario==null) {
                            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } ServicioDAO
                            servicioDAO=new ServicioDAO(); List<Servicio> servicios = new ArrayList<>();
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
                                    <title>Página Principal</title>
                                </head>

                                <body class="grid-principal">
                                    <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
                                    <header class="seccion-encabezado">
                                        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                                            alt="logo de la aplicación">
                                        <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                                    </header>
                                    <main class="contenido" id="contenido-principal" role="main">
                                        <div class="contenido__barra-busqueda">
                                            <img src="../../Assets/icons/lupa.png" alt="lupa">
                                            <input type="text" name="buscador" id="buscador"
                                                placeholder="Buscar arreglos..." autocomplete="off">
                                            <button class="buscador__limpiar" id="btnLimpiar"
                                                style="display:none;">✕</button>
                                        </div>
                                        <div class="busqueda__sin-resultados" id="sinResultados" style="display:none;">
                                            <p>No se encontraron servicios para "<span id="textoBuscado"></span>"</p>
                                        </div>
                                        <h2 class="contenido__titulo-seccion" id="tituloCategorias">Categorías</h2>
                                        <section class="contenido__categorias">
                                            <div class="categorias__grid" id="gridServicios">
                                                <% if (servicios.isEmpty()) { %>
                                                    <p
                                                        style="color:#888;font-size:13px;text-align:center;padding:20px;">
                                                        No hay servicios disponibles</p>
                                                    <% } %>
                                                        <% for (int i=0; i < servicios.size(); i++) { Servicio
                                                            s=servicios.get(i); String imgUrl=s.getImagenUrl(); String
                                                            imgSrc=ctx + "/Assets/image/logo-app.png" ; if (imgUrl
                                                            !=null && !imgUrl.trim().isEmpty()) { if
                                                            (imgUrl.startsWith("../../")) { imgSrc=ctx + "/" +
                                                            imgUrl.replace("../../", "" ); } else if
                                                            (imgUrl.startsWith("Assets/")) { imgSrc=ctx + "/" + imgUrl;
                                                            } else if (imgUrl.startsWith("http")) { imgSrc=imgUrl; }
                                                            else { imgSrc=ctx + "/" + imgUrl; } } String
                                                            nombre=s.getNombre() !=null ? s.getNombre() : "" ; String
                                                            nombreLower=nombre.toLowerCase(); String
                                                            precio=String.format("%,.0f", s.getPrecioBase()); %>
                                                            <article class="tarjeta-arreglo"
                                                                data-nombre="<%= nombreLower %>">
                                                                <div class="tarjeta-arreglo__imagen-contenedor">
                                                                    <img src="<%= imgSrc %>" alt="<%= nombre %>">
                                                                </div>
                                                                <div class="tarjeta-arreglo__contenido">
                                                                    <h3 class="tarjeta-arreglo__nombre">
                                                                        <%= nombre %>
                                                                    </h3>
                                                                    <div class="tarjeta-arreglo__footer">
                                                                        <p class="tarjeta-arreglo__precio">$<%= precio
                                                                                %>
                                                                        </p>
                                                                        <a href="detalle-servicio.jsp?id=<%= s.getArregloId() %>"
                                                                            class="tarjeta-arreglo__enlace">Detalles</a>
                                                                    </div>
                                                                </div>
                                                            </article>
                                                            <% } %>
                                            </div>
                                        </section>
                                    </main>
                                    <footer class="navbar">
                                        <nav class="navbar-inferior" role="navigation" aria-label="Navegación principal">
                                            <a href="pagina-principal.jsp" class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page" aria-label="Inicio">
                                                <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Inicio</span>
                                            </a>
                                            <a href="mi-seleccion.jsp" class="navbar-inferior__item" aria-label="Mi selección">
                                                <img src="../../Assets/icons/lista-de-deseos-transparente.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Mi selección</span>
                                            </a>
                                            <a href="mis-arreglos.jsp" class="navbar-inferior__item" aria-label="Mis Arreglos">
                                                <img src="../../Assets/icons/cortar-con-tijeras-transparente.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Mis Arreglos</span>
                                            </a>
                                            <a href="mis-pedidos.jsp" class="navbar-inferior__item" aria-label="Pedidos">
                                                <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Pedidos</span>
                                            </a>
                                            <a href="mi-perfil.jsp" class="navbar-inferior__item" aria-label="Perfil">
                                                <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono" alt="">
                                                <span class="navbar-inferior__texto">Perfil</span>
                                            </a>
                                        </nav>
                                    </footer>
                                    <script src="../../Assets/JavaScript/pagina-principal.js"></script>
                                </body>

                                </html>