<%@page contentType="text/html" pageEncoding="UTF-8" %>
<% 
    // Obtener parámetros de la URL para mantener valores después de validaciones
    String msg = request.getParameter("msg"); 
    String nombre = request.getParameter("nombre"); 
    String email = request.getParameter("email"); 
    String direccion = request.getParameter("direccion"); 
    String telefono = request.getParameter("telefono"); 
%>
    <!DOCTYPE html>
    <html lang="es">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link rel="stylesheet" href="../../Assets/estilos.css">
      <title>Registrarse</title>
    </head>

    <body>
      <!-- Sección de logo y título -->
      <header class="seccion-logo">
        <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
      </header>
      
      <!-- Contenedor principal del formulario de registro -->
      <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">REGISTRARSE</h1>
        <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">
        
        <!-- Formulario de registro de usuario -->
        <form class="contenedor-login" action="${pageContext.request.contextPath}/UsuarioServlet" method="post">

          <!-- Campo de Nombre Completo -->
          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/user.png" alt="icono de usuario">
              <label class="campo__label" for="txtNombre">Nombre Completo</label>
            </div>
            <input 
                type="text" 
                name="txtNombre" 
                id="txtNombre" 
                value="<%= nombre != null ? nombre : "" %>"
                placeholder="Ingrese su nombre completo"
                required
            >
            <div class="linea-separadora"></div>
          </div>

          <!-- Campo de Correo Electrónico -->
          <div class="contenedor-login__campo">
            <div class="campo__contrasena">
              <img class="campo__icono" src="../../Assets/icons/correo-electronico.png" alt="icono de correo">
              <label class="campo__label" for="txtEmail">Correo Electrónico</label>
            </div>
            <input 
                type="email" 
                id="txtEmail" 
                name="txtEmail" 
                value="<%= email != null ? email : "" %>"
                placeholder="Ingrese su correo electrónico"
                required
            >
            <div class="linea-separadora"></div>
          </div>

          <!-- Campo de contraseña -->
          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="txtPassword">contraseña</label>
            </div>
            <input 
                type="password" 
                id="txtPassword" 
                name="txtPassword" 
                placeholder="Ingrese su contraseña"
                required
            >
            <div class="linea-separadora"></div>
          </div>

          <!-- Campo de Confirmar contraseña -->
          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="txtConfirmarPassword">Confirmar contraseña</label>
            </div>
            <input 
                type="password" 
                id="txtConfirmarPassword" 
                name="txtConfirmarPassword" 
                placeholder="Confirme su contraseña"
                required
            >
            <div class="linea-separadora"></div>
          </div>

          <!-- Campo de Dirección -->
          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/pasador-de-ubicacion.png" alt="icono de ubicacion">
              <label class="campo__label" for="txtDireccion">Dirección</label>
            </div>
            <input 
                type="text" 
                id="txtDireccion" 
                name="txtDireccion" 
                value="<%= direccion != null ? direccion : "" %>"
                placeholder="Ingrese su dirección"
                required
            >
            <div class="linea-separadora"></div>
          </div>

          <!-- Campo de teléfono -->
          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="../../Assets/icons/llamada-telefonica.png" alt="icono de telefono">
              <label class="campo__label" for="txtTelefono">teléfono</label>
            </div>
            <input 
                type="tel" 
                id="txtTelefono" 
                name="txtTelefono" 
                value="<%= telefono != null ? telefono : "" %>"
                placeholder="Ingrese su número telefónico"
                required
            >
            <div class="linea-separadora"></div>
          </div>
          
          <!-- Botón de registro -->
          <button 
              type="submit" 
              class="contenedor-login__iniciar-sesion"
              onclick="return validarFormulario()"
          >
              REGISTRARSE
          </button>
          
          <!-- Sección para iniciar sesión -->
          <div class="contenedor-login__registrarse">
            <h3 class="registrarse__pregunta">¿Ya tienes cuenta?</h3>
            <a class="registrarse__formulario" href="../../index.jsp">Iniciar sesión</a>
          </div>
        </form>
        
        <!-- Scripts de validación -->
        <script src="../../Assets/JavaScript/registro.js"></script>
        <script src="../../Assets/JavaScript/alerts.js"></script>
      </main>
    </body>

    </html>


