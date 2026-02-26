<%@page contentType="text/html" pageEncoding="UTF-8" %>
<% 
    // Obtener parámetros de la URL
    String msg = request.getParameter("msg"); 
    String email = request.getParameter("email"); 
%>
<!DOCTYPE html>
<html lang="es">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="Assets/estilos.css">
  <title>Inicio de Sesion</title>
</head>

<body>

  <!-- Sección de logo y título -->
  <header class="seccion-logo">
    <img class="seccion-logo__logo" src="Assets/image/logo-app.png" alt="logo de la aplicación">
    <h1 class="seccion-logo__nombre">Arreglos App</h1>
  </header>
  
  <!-- Contenedor principal del formulario de login -->
  <main class="contenedor-registro">
    <h1 class="contenedor-registro__titulo">INICIAR SESION</h1>
    <img class="contenedor-login__imagen" src="Assets/icons/user-inicio.png" alt="logo de usuario">
    
    <!-- Formulario de autenticación -->
    <form class="contenedor-login" action="${pageContext.request.contextPath}/AuthServlet" method="post">
      
      <!-- Campo de Correo Electrónico -->
      <div class="contenedor-login__campo">
        <div class="campo__usuario">
          <img class="campo__icono" src="Assets/icons/user.png" alt="icono de usuario">
          <label class="campo__label" for="email">Correo Electrónico</label>
        </div>
        <input 
            type="email" 
            id="email" 
            name="email" 
            value="<%= email != null ? email : "" %>" 
            placeholder="Ingrese su correo electrónico"
            required
        >
        <div class="linea-separadora"></div>
      </div>
      
      <!-- Campo de contraseña -->
      <div class="contenedor-login__campo">
        <div class="campo__contrasena">
          <img class="campo__icono" src="Assets/icons/padlock.png" alt="icono de candado">
          <label class="campo__label" for="password">contraseña</label>
        </div>
        <input 
            type="password" 
            id="password" 
            name="password" 
            placeholder="Ingrese su contraseña"
            required
        >
        <div class="linea-separadora"></div>
      </div>
      
      <!-- Enlace para recuperar contraseña -->
      <a class="contenedor-login__formulario-recuperar" href="Public/auth/recuperar-contrasena.jsp">
        ¿Olvidaste tu contraseña?
      </a>
      
      <!-- Botón de inicio de sesión -->
      <button type="submit" class="contenedor-login__iniciar-sesion">INICIAR SESION</button>
      
      <!-- Sección para registrarse -->
      <div class="contenedor-login__registrarse">
        <h3 class="registrarse__pregunta">¿No tienes cuenta?</h3>
        <a class="registrarse__formulario" href="Public/auth/registrarse.jsp">Registrarse</a>
      </div>
    </form>
  </main>
  
  <!-- Scripts -->
  <script src="Assets/JavaScript/alerts.js"></script>
</body>

</html>

