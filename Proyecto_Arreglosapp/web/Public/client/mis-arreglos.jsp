<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ page import="java.util.List" %>
    <%@ page import="model.Personalizacion" %>
      <%@ page import="model.Usuario" %>
        <%@ page import="dao.PersonalizacionDAO" %>
          <%@ page import="java.time.format.DateTimeFormatter" %>
            <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) {
              usuario=(Usuario) sesion.getAttribute("usuario"); } if (usuario==null) {
              response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String mensajeOk=(String)
              sesion.getAttribute("mensajePersonalizacion"); String mensajeErr=(String)
              sesion.getAttribute("errorPersonalizacion"); sesion.removeAttribute("mensajePersonalizacion");
              sesion.removeAttribute("errorPersonalizacion"); PersonalizacionDAO personalizacionDAO=new
              PersonalizacionDAO(); List<Personalizacion> misArreglos = null;

              try {
              misArreglos = personalizacionDAO.obtenerPersonalizacionesPorUsuario(usuario.getId());
              } catch (Exception e) {
              e.printStackTrace();
              }

              DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd - MM - yyyy");
              String ctx = request.getContextPath();
              %>
              <!DOCTYPE html>
              <html lang="es">

              <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="../../Assets/estilos.css">
                <title>Mis Arreglos</title>
              </head>

              <body class="grid-principal">

                <header class="seccion-encabezado">
                  <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                    alt="logo de la aplicación">
                  <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                </header>

                <div id="toast" class="toast"></div>

                <main class="contenido-arreglos">
                  <h1 class="contenido__titulo-seleccion">Mis Arreglos</h1>

                  <% if (misArreglos !=null && !misArreglos.isEmpty()) { %>
                    <% for (Personalizacion arreglo : misArreglos) { %>
                      <% 
                        String imgUrl = arreglo.getImagenReferencia();
                        String imgSrc = "";
                        if (imgUrl == null || imgUrl.trim().isEmpty()) {
                          String cat = arreglo.getCategoria() != null ? arreglo.getCategoria().toLowerCase() : "";
                          if (cat.contains("dobladillo") || cat.contains("sastr")) {
                            imgSrc = ctx + "/Assets/image/imagen-sastreria.jpg";
                          } else if (cat.contains("estrech") || cat.contains("ensanch")) {
                            imgSrc = ctx + "/Assets/image/imagen-arreglos-de-vestidos-de-fiesta.jpg";
                          } else if (cat.contains("recortar")) {
                            imgSrc = ctx + "/Assets/image/image-arreglo-bolsillos.jpg";
                          } else {
                            imgSrc = ctx + "/Assets/image/imagen-costura.jpg";
                          }
                        } else {
                          if (imgUrl.startsWith("http")) {
                            imgSrc = imgUrl;
                          } else if (imgUrl.startsWith("/")) {
                            imgSrc = ctx + imgUrl;
                          } else if (imgUrl.startsWith("../../")) {
                            imgSrc = ctx + "/" + imgUrl.replace("../../", "");
                          } else {
                            imgSrc = ctx + "/" + imgUrl;
                          }
                        }
                        String fechaTexto = "Sin fecha";
                        if (arreglo.getFechaCreacion() != null) {
                          fechaTexto = arreglo.getFechaCreacion().format(fmt);
                        }
                        String estado = arreglo.getEstado() != null ? arreglo.getEstado() : "pendiente";
                        String badgeClase = "badge--pendiente";
                        String estadoTexto = "Pendiente";
                        if (estado.equalsIgnoreCase("en_proceso")) {
                          badgeClase = "badge--proceso";
                          estadoTexto = "En proceso";
                        } else if (estado.equalsIgnoreCase("completado")) {
                          badgeClase = "badge--completado";
                          estadoTexto = "Completado";
                        }
                        String desc = arreglo.getDescripcion() != null ? arreglo.getDescripcion() : "";
                        String descCorta = desc.length() > 60 ? desc.substring(0, 60) + "..." : desc;
                        String urlEditar = "personalizar-arreglo.jsp?id=" + arreglo.getPersonalizacionId();
                        String urlCita = "agendar-cita.jsp?personalizacionId=" + arreglo.getPersonalizacionId();
                        %>
                        <section class="contenido-seleccion__contenedor arreglo-card">
                          <img class="contenedor__imagen" src="<%= imgSrc %>" alt="Imagen del arreglo">
                          <div class="contenido-seleccion__contenedor-informacion">
                            <div class="contenedor-informacion__informacion">
                              <div class="informacion__fila-titulo">
                                <h2 class="informacion__titulo-arreglo">
                                  <%= arreglo.getCategoria() %>
                                </h2>
                                <span class="informacion__badge <%= badgeClase %>">
                                  <%= estadoTexto %>
                                </span>
                              </div>
                              <div class="informacion__detalles-texto">
                                <% if (!desc.trim().isEmpty()) { %>
                                  <p class="informacion__descripcion arreglo__descripcion">
                                    <%= descCorta %>
                                  </p>
                                  <% } %>
                                    <% if (arreglo.getMaterialTela() !=null &&
                                      !arreglo.getMaterialTela().trim().isEmpty()) { %>
                                      <p class="informacion__descripcion"><strong>Tela:</strong>
                                        <%= arreglo.getMaterialTela() %>
                                      </p>
                                      <% } %>
                                        <p class="informacion__descripcion arreglo__fecha">Creado: <%= fechaTexto %>
                                        </p>
                              </div>
                            </div>
                            <div class="contenedor-informacion__enlaces">
                              <a class="enlaces__enlace-arreglos" href="<%= urlEditar %>">Editar</a>
                              <a class="enlaces__enlace-arreglos" href="#"
                                onclick="prepararEliminarArreglo(<%= arreglo.getPersonalizacionId() %>); return false;">
                                Eliminar
                              </a>
                              <a class="enlace__cita-usuario" href="<%= urlCita %>">Agendar Cita</a>
                            </div>
                          </div>
                        </section>
                        <% } %>
                          <% } else { %>
                            <div class="arreglos__vacio">
                              <p class="arreglos__vacio-texto">No tienes arreglos personalizados aún.</p>
                              <a href="pagina-principal.jsp" class="informacion__enlace-personalizar">Ver Servicios</a>
                            </div>
                            <% } %>
                </main>

                <!-- MODAL ELIMINAR -->
                <div id="modalEliminarArreglo" class="modal">
                  <div class="modal-contenido">
                    <h3 class="modal__titulo">¿Eliminar arreglo?</h3>
                    <p class="modal__descripcion">¿Estás seguro que quieres eliminar esta personalización? Esta acción
                      no se puede deshacer.</p>
                    <div class="modal__acciones">
                      <button class="btn-modal btn-modal--cancelar" onclick="cerrarModalArreglo()">CANCELAR</button>
                      <button class="btn-modal btn-modal--eliminar" id="btnConfirmarEliminarArreglo">ELIMINAR</button>
                    </div>
                  </div>
                </div>

                <footer class="navbar">
                  <nav class="navbar-inferior">
                    <a href="pagina-principal.jsp" class="navbar-inferior__item">
                      <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono">
                      <span class="navbar-inferior__texto">Inicio</span>
                    </a>
                    <a href="mi-seleccion.jsp" class="navbar-inferior__item">
                      <img src="../../Assets/icons/lista-de-deseos-transparente.png" class="navbar-inferior__icono">
                      <span class="navbar-inferior__texto">Mi selección</span>
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

                <script src="../../Assets/JavaScript/mis-arreglos.js"></script>
              </body>

              </html>