<%@page contentType="text/html" pageEncoding="UTF-8" %>
  <% String msg=request.getParameter("msg"); String nombre=request.getParameter("nombre"); String
    email=request.getParameter("email"); String direccion=request.getParameter("direccion"); String
    telefono=request.getParameter("telefono"); %>
    <!DOCTYPE html>
    <html lang="es">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link rel="stylesheet" href="../../Assets/estilos.css">
      <title>Registrarse</title>
    </head>

    <body>
      <header class="seccion-logo">
        <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
      </header>
      <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">REGISTRARSE</h1>
        <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">
        <form class="contenedor-login" action="${pageContext.request.contextPath}/UsuarioServlet" method="post">

          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/user.png" alt="icono de usuario">
              <label class="campo__label" for="txtNombre">Nombre Completo</label>
            </div>
            <input type="text" name="txtNombre" id="txtNombre" value="<%= nombre != null ? nombre : "" %>">
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__contrasena">
              <img class="campo__icono" src="../../Assets/icons/correo-electronico.png" alt="icono de correo">
              <label class="campo__label" for="txtEmail">Correo Electronico</label>
            </div>
            <input type="email" id="txtEmail" name="txtEmail" value="<%= email != null ? email : "" %>">
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="txtPassword">Contraseña</label>
            </div>
            <input type="password" id="txtPassword" name="txtPassword">
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="txtConfirmarPassword">Confirmar Contraseña</label>
            </div>
            <input type="password" id="txtConfirmarPassword" name="txtConfirmarPassword">
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/pasador-de-ubicacion.png" alt="icono de ubicacion">
              <label class="campo__label" for="txtDireccion">Direccion</label>
            </div>
            <input type="text" id="txtDireccion" name="txtDireccion" value="<%= direccion != null ? direccion : "" %>">
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/llamada-telefonica.png" alt="icono de telefono">
              <label class="campo__label" for="txtTelefono">Telefono</label>
            </div>
            <input type="text" id="txtTelefono" name="txtTelefono" value="<%= telefono != null ? telefono : "" %>">
            <div class="linea-separadora"></div>
          </div>
          <button type="submit" class="contenedor-login__iniciar-sesion"
            onclick="return validarFormulario()">REGISTRARSE</button>
          <div class="contenedor-login__registrarse">
            <h3 class="registrarse__pregunta">¿Ya tienes cuenta?</h3>
            <a class="registrarse__formulario" href="../../index.jsp">Iniciar Sesion</a>
          </div>
        </form>
        <script src="../../Assets/JavaScript/registro.js"></script>
        <script src="../../Assets/JavaScript/alerts.js"></script>
      </main>
    </body>

    </html>