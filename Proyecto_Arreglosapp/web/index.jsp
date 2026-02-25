<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="Assets/estilos.css">
    <title>Inicio de Sesión</title>
</head>

<body>
    <header class="seccion-logo">
        <img class="seccion-logo__logo" src="Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
    </header>
    <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">INICIAR SESIÓN</h1>
        <img class="contenedor-login__imagen" src="Assets/icons/user-inicio.png" alt="logo de usuario">
        <form class="contenedor-login" action="Public/client/pagina-principal.html" method="get">
            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="Assets/icons/user.png" alt="icono de usuario">
                    <label class="campo__label" for="usuario">Usuario</label>
                </div>
                <input type="text" id="usuario" placeholder="">
                <div class="linea-separadora"></div>
            </div>
            <div class="contenedor-login__campo">
                <div class="campo__contrasena">
                    <img class="campo__icono" src="Assets/icons/padlock.png" alt="icono de candado">
                    <label class="campo__label" for="contrasena">Contraseña</label>
                </div>
                <input type="password" id="contrasena" placeholder="">
                <div class="linea-separadora"></div>
            </div>
            <a class="contenedor-login__formulario-recuperar" href="Public/auth/recuperar-contrasena.html">¿Olvidaste tu
                contraseña?</a>
            <button type="submit" class="contenedor-login__iniciar-sesion">INICIAR SESIÓN</button>
            <div class="contenedor-login__registrarse">
                <h3 class="registrarse__pregunta">¿No tienes cuenta?</h3>
                <a class="registrarse__formulario" href="Public/auth/registrarse.html">Registrarse</a>
            </div>
        </form>
    </main>
</body>

</html>