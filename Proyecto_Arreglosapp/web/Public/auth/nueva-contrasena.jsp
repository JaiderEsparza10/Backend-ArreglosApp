<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <% String msg=request.getParameter("msg"); Object codigoVerificadoObj=(session !=null) ?
    session.getAttribute("codigoVerificado") : null; boolean codigoVerificado=(codigoVerificadoObj instanceof Boolean) ?
    ((Boolean) codigoVerificadoObj) : false; if (!codigoVerificado) { response.sendRedirect(request.getContextPath()
    + "/Public/auth/recuperar-contrasena.jsp?msg=accesoDenegado" ); return; } %>
    <!DOCTYPE html>
    <html lang="es">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link rel="stylesheet" href="../../Assets/estilos.css">
      <title>Nueva Contraseña</title>
    </head>

    <body>
      <header class="seccion-logo">
        <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
      </header>
      <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">NUEVA CONTRASEÑA</h1>
        <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">

        <p style="text-align: center; margin-bottom: 20px; color: #666;">
          Ingresa tu nueva contraseña. ¡Asegúrate de que sea segura!
        </p>

        <form class="contenedor-login" action="${pageContext.request.contextPath}/NuevaContrasenaServlet" method="post">
          <div class="contenedor-login__campo">
            <div class="campo__contrasena">
              <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="nuevaPassword">Nueva Contraseña</label>
            </div>
            <input type="password" id="nuevaPassword" name="nuevaPassword"
              placeholder="Mínimo 8 caracteres, una mayúscula y un número">
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__contrasena">
              <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="confirmarPassword">Confirmar Contraseña</label>
            </div>
            <input type="password" id="confirmarPassword" name="confirmarPassword"
              placeholder="Confirma tu nueva contraseña">
            <div class="linea-separadora"></div>
          </div>

          <button type="submit" class="contenedor-login__iniciar-sesion">CAMBIAR CONTRASEÑA</button>
          <div class="contenedor-login__registrarse">
            <h3 class="registrarse__pregunta">¿Recordaste tu contraseña?</h3>
            <a class="registrarse__formulario" href="../../index.jsp">Iniciar Sesion</a>
          </div>
        </form>
      </main>
      <script src="../../Assets/JavaScript/alerts.js"></script>
    </body>

    </html>