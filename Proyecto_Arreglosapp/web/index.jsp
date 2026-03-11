<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <% String msg=request.getParameter("msg"); String email=request.getParameter("email"); String
    passwordRecuperada=request.getParameter("passwordRecuperada"); %>
    <!DOCTYPE html>
    <html lang="es">

    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link rel="stylesheet" href="Assets/estilos.css">
      <title>Inicio de Sesión</title>
    </head>

    <body>
      <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
      <div id="toast" class="toast"></div>

      <header class="seccion-logo">
        <img class="seccion-logo__logo" src="Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
      </header>

      <main class="contenedor-registro" id="contenido-principal" role="main">
        <h1 class="contenedor-registro__titulo">INICIAR SESIÓN</h1>
        <img class="contenedor-login__imagen" src="Assets/icons/user-inicio.png" alt="logo de usuario">

        <form class="contenedor-login" action="${pageContext.request.contextPath}/AuthServlet" method="post">

          <div class="contenedor-login__campo">
            <div class="campo__usuario">
              <img class="campo__icono" src="Assets/icons/user.png" alt="icono de usuario">
              <label class="campo__label" for="email">Correo Electrónico <span aria-hidden="true">*</span></label>
            </div>
            <input type="email" id="email" name="email" value="<%= email != null ? email : "" %>"
              placeholder="Ingrese su correo electrónico" 
              aria-required="true"
              aria-describedby="email-error"
              required>
            <span id="email-error" class="campo-error" role="alert" style="display:none;">
              El correo electrónico es obligatorio
            </span>
            <div class="linea-separadora"></div>
          </div>

          <div class="contenedor-login__campo">
            <div class="campo__contrasena">
              <img class="campo__icono" src="Assets/icons/padlock.png" alt="icono de candado">
              <label class="campo__label" for="password">Contraseña</label>
            </div>
            <input type="password" id="password" name="password" placeholder="Ingrese su contraseña" required>
            <div class="linea-separadora"></div>
          </div>

          <a class="contenedor-login__formulario-recuperar" href="Public/auth/recuperar-contrasena.jsp">
            ¿Olvidaste tu contraseña?
          </a>

          <button type="submit" class="contenedor-login__iniciar-sesion">INICIAR SESIÓN</button>

          <div class="contenedor-login__registrarse">
            <h3 class="registrarse__pregunta">¿No tienes cuenta?</h3>
            <a class="registrarse__formulario" href="Public/auth/registrarse.jsp">Registrarse</a>
          </div>
        </form>
      </main>

      <script src="Assets/JavaScript/alerts.js"></script>
      <script>
        window.addEventListener('load', function () {
          var params = new URLSearchParams(window.location.search);
          var toast = document.getElementById('toast');

          if (params.get('passwordRecuperada') === '1') {
            toast.textContent = '✅ Contraseña actualizada. Inicia sesión';
            toast.className = 'toast toast--exito toast--visible';
            setTimeout(function () { toast.classList.remove('toast--visible'); }, 3500);
            history.replaceState({}, document.title, window.location.pathname);
          }

          if (params.get('msg') === 'exitoRegistro') {
            toast.textContent = '✅ ¡Registro exitoso! Ya puedes iniciar sesión';
            toast.className = 'toast toast--exito toast--visible';
            setTimeout(function () { toast.classList.remove('toast--visible'); }, 4000);
            history.replaceState({}, document.title, window.location.pathname);
          }

          if (params.get('msg') === 'exitoLogin') {
            toast.textContent = '✅ Bienvenido de nuevo';
            toast.className = 'toast toast--exito toast--visible';
            setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
            history.replaceState({}, document.title, window.location.pathname);
          }
        });

        document.querySelector('.contenedor-login').addEventListener('submit', function (e) {
          var valido = true;
          var campos = this.querySelectorAll('[aria-required="true"]');
          campos.forEach(function (campo) {
            var errorId = campo.getAttribute('aria-describedby');
            var errorEl = document.getElementById(errorId);
            if (!campo.value.trim()) {
              campo.setAttribute('aria-invalid', 'true');
              if (errorEl) errorEl.style.display = 'flex';
              valido = false;
            } else {
              campo.setAttribute('aria-invalid', 'false');
              if (errorEl) errorEl.style.display = 'none';
            }
          });
          if (!valido) e.preventDefault();
        });
      </script>
    </body>

    </html>