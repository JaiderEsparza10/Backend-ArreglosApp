<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <% String msg=request.getParameter("msg"); String email=request.getParameter("email"); String codigoDebug=null; try {
    Object c=(session !=null) ? session.getAttribute("codigoRecuperacion") : null; codigoDebug=(c !=null) ?
    String.valueOf(c) : null; } catch (Exception e) { codigoDebug=null; } %>
    <!DOCTYPE html>
    <html lang="es">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link rel="stylesheet" href="../../Assets/estilos.css">
      <title>Verificar Código</title>
    </head>

    <body>
      <header class="seccion-logo">
        <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
      </header>
      <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">VERIFICAR CÓDIGO</h1>
        <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">

        <p style="text-align: center; margin-bottom: 20px; color: #666;">
          Hemos enviado un código de 5 dígitos a: <strong>
            <%= email !=null ? email : "" %>
          </strong>
        </p>

        <% if (codigoDebug !=null && !codigoDebug.trim().isEmpty()) { %>
          <p style="text-align: center; margin-bottom: 20px; color: #666;">
            Código (debug): <strong>
              <%= codigoDebug %>
            </strong>
          </p>
          <% } %>

            <form class="contenedor-login" action="${pageContext.request.contextPath}/VerificarCodigoServlet"
              method="post">
              <div class="contenedor-login__campo">
                <div class="campo__usuario">
                  <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
                  <label class="campo__label" for="codigo">Código de Verificación</label>
                </div>
                <input type="text" id="codigo" name="codigo" placeholder="Ingresa el código de 5 dígitos" maxlength="5"
                  pattern="[0-9]{5}">
                <div class="linea-separadora"></div>
              </div>

              <button type="submit" class="contenedor-login__iniciar-sesion">VERIFICAR CÓDIGO</button>
              <div class="contenedor-login__registrarse">
                <h3 class="registrarse__pregunta">¿No recibiste el código?</h3>
                <a class="registrarse__formulario" href="recuperar-contrasena.jsp">Reenviar código</a>
              </div>
            </form>
      </main>
      <script src="../../Assets/JavaScript/alerts.js"></script>
    </body>

    </html>