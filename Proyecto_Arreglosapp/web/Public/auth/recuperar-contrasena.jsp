<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <% String msg=request.getParameter("msg"); %>
    <!DOCTYPE html>
    <html lang="es">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link rel="stylesheet" href="../../Assets/estilos.css">
      <title>Recuperar Contraseña</title>
    </head>

    <body>
      <header class="seccion-logo">
        <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
      </header>
      <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">RECUPERAR CONTRASEÑA</h1>
        <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">

        <form class="contenedor-login" action="${pageContext.request.contextPath}/RecuperarServlet" method="post">
          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/correo-electronico.png" alt="icono de correo">
              <label class="campo__label" for="email">Correo Electronico</label>
            </div>
            <input type="email" id="email" name="email" placeholder="Ingresa tu correo electrónico">
            <div class="linea-separadora"></div>
          </div>

          <button type="submit" class="contenedor-login__iniciar-sesion">ENVIAR CÓDIGO DE RECUPERACIÓN</button>
          <div class="contenedor-login__registrarse">
            <h3 class="registrarse__pregunta">¿Ya tienes cuenta?</h3>
            <a class="registrarse__formulario" href="../../index.jsp">Iniciar Sesion</a>
          </div>
        </form>
      </main>
      <script src="../../Assets/JavaScript/alerts.js"></script>
    </body>

    </html>