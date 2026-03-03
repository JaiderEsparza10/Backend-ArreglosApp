<%@page import="model.Usuario" %>
    <%@page import="dao.UsuarioDAO" %>
        <% // Verificar si el usuario está autenticado Usuario usuario=(Usuario) session.getAttribute("usuario"); if
            (usuario==null) { response.sendRedirect("../../../index.jsp"); return; } %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="../../Assets/estilos.css">
                <title>Mi Perfil</title>
            </head>

            <body class="grid-principal">
                <header class="seccion-encabezado">
                    <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                        alt="logo de la aplicación">
                    <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                </header>
                <main class="contenido-perfil">
                    <div class="contenido-perfil__enlace-volver">
                        <a href="pagina-principal.jsp" class="enlace-volver__texto">← Volver</a>
                        <h1 class="enlace-volver__titulo">Mi Perfil</h1>
                    </div>

                    <section class="perfil-informacion">
                        <div class="perfil__avatar">
                            <img src="../../Assets/icons/usuario-blanco.png" alt="avatar de usuario"
                                class="perfil__imagen">
                        </div>

                        <div class="perfil__datos">
                            <h2 class="perfil__nombre">
                                <%= usuario.getNombre() %>
                            </h2>
                            <p class="perfil__email">
                                <%= usuario.getEmail() %>
                            </p>
                            <p class="perfil__direccion">
                                <%= usuario.getDireccion() !=null ? usuario.getDireccion() : "Sin dirección registrada"
                                    %>
                            </p>
                            <p class="perfil__rol">
                                <%= usuario.getRolId()==1 ? "Administrador" : "Cliente" %>
                            </p>
                        </div>

                        <div class="perfil__acciones">
                            <a href="cambiar-contraseña.jsp" class="perfil__boton perfil__boton--secundario">
                                Cambiar Contraseña
                            </a>
                            <a href="mis-medidas.jsp" class="perfil__boton perfil__boton--primario">
                                Mis Medidas
                            </a>
                        </div>
                    </section>

                    <section class="perfil-resumen">
                        <h3 class="resumen__titulo">Resumen de Actividad</h3>
                        <div class="resumen__grid">
                            <div class="resumen__item">
                                <h4 class="resumen__numero">0</h4>
                                <p class="resumen__texto">Pedidos Activos</p>
                            </div>
                            <div class="resumen__item">
                                <h4 class="resumen__numero">0</h4>
                                <p class="resumen__texto">Favoritos</p>
                            </div>
                            <div class="resumen__item">
                                <h4 class="resumen__numero">0</h4>
                                <p class="resumen__texto">Personalizaciones</p>
                            </div>
                        </div>
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
                        <a href="mi-perfil.jsp" class="navbar-inferior__item navbar-inferior__item--activo">
                            <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono">
                            <span class="navbar-inferior__texto">Perfil</span>
                        </a>
                    </nav>
                </footer>
            </body>

            </html>