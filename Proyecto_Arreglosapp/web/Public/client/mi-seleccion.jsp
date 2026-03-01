<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ page import="java.util.List" %>
    <%@ page import="model.Favorito" %>
      <%@ page import="dao.FavoritoDAO" %>
        <%@ page import="model.Usuario" %>
          <% Usuario usuario=(Usuario) session.getAttribute("usuario"); if (usuario==null) {
            response.sendRedirect("../../index.jsp"); return; } FavoritoDAO favoritoDAO=new FavoritoDAO();
            List<Favorito> misFavoritos = null;
            try {
            misFavoritos = favoritoDAO.obtenerFavoritosPorUsuario(usuario.getId());
            } catch(Exception e) {
            e.printStackTrace();
            }
            %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <link rel="stylesheet" href="../../Assets/estilos.css">
              <title>Mi selección</title>
            </head>

            <body class="grid-principal">
              <header class="seccion-encabezado">
                <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
                <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
              </header>
              <main class="contenido-seleccion">
                <h1 class="contenido__titulo-seleccion">Mi selección</h1>

                <% if (misFavoritos !=null && !misFavoritos.isEmpty()) { for(Favorito fav : misFavoritos) { %>
                  <section class="contenido-seleccion__contenedor">
                    <img class="contenedor__imagen" src="<%= fav.getImagenUrl() %>"
                      alt="Imagen de arreglos de vestidos">
                    <div class="contenido-seleccion__contenedor-informacion">
                      <div class="contenedor-informacion__informacion">
                        <h2 class="informacion__titulo-arreglo">
                          <%= fav.getNombreCategoria() %>
                        </h2>
                        <p class="informacion__descripcion">
                          <%= fav.getCategoria() %>
                        </p>
                      </div>
                      <div class="contenedor-informacion__pago-enlace">
                        <span class="pago-enlace__precio">$<%= String.format("%,.0f", fav.getPrecio()) %></span>
                        <a class="pago-enlace__enlace" href="personalizar-arreglo.jsp">Personalizar</a>
                      </div>
                    </div>
                  </section>
                  <% } } else { %>
                    <h2 style="text-align: center; color: #333; font-size: 1.5rem; margin-top: 50px;">No tienes arreglos
                      en tu selección aún.</h2>
                    <% } %>
                      <a class="contenido-seleccion__enlace" href="eliminar-mi-seleccion.jsp">
                        <img class="enlace__icono-eliminar enlace__icono-eliminar--activo"
                          src="../../Assets/icons/eliminar.png" alt="icono de eliminar una selección">
                      </a>
              </main>
              <footer class="navbar">
                <nav class="navbar-inferior">
                  <a href="pagina-principal.jsp" class="navbar-inferior__item">
                    <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono"></img>
                    <span class="navbar-inferior__texto">Inicio</span>
                  </a>
                  <a href="mi-seleccion.jsp" class="navbar-inferior__item">
                    <img src="../../Assets/icons/lista-de-deseos-transparente.png" class="navbar-inferior__icono"></img>
                    <span class="navbar-inferior__texto">Mi selección</span>
                  </a>
                  <a href="mis-arreglos.jsp" class="navbar-inferior__item">
                    <img src="../../Assets/icons/cortar-con-tijeras-transparente.png"
                      class="navbar-inferior__icono"></img>
                    <span class="navbar-inferior__texto">Mis Arreglos</span>
                  </a>
                  <a href="mis-pedidos.jsp" class="navbar-inferior__item">
                    <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono"></img>
                    <span class="navbar-inferior__texto">Pedidos</span>
                  </a>
                  <a href="mi-perfil.jsp" class="navbar-inferior__item">
                    <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono"></img>
                    <span class="navbar-inferior__texto">Perfil</span>
                  </a>
                </nav>
              </footer>
            </body>

            </html>