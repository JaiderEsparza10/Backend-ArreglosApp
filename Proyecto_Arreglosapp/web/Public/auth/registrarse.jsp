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
        <form class="contenedor-login" action="../../index.html" method="get">
            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/user.png" alt="icono de usuario">
                    <label class="campo__label" for="usuario">Nombre Completo</label>
                </div>
                <input type="text" id="usuario">
                <div class="linea-separadora"></div>
            </div>
            <div class="contenedor-login__campo">
                <div class="campo__contrasena">
                    <img class="campo__icono" src="../../Assets/icons/correo-electronico.png" alt="icono de candado">
                    <label class="campo__label" for="Correo">Correo Electronico</label>
                </div>
                <input type="email" id="Correo" required>
                <div class="linea-separadora"></div>
            </div>
            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de usuario">
                    <label class="campo__label" for="contrasena">Contraseña</label>
                </div>
                <input type="password" id="contrasena" required>
                <div class="linea-separadora"></div>
            </div>
            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de usuario">
                    <label class="campo__label" for="contrasena">Confirmar Contraseña</label>
                </div>
                <input type="password" id="contrasena" required>
                <div class="linea-separadora"></div>
            </div>
            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/pasador-de-ubicacion.png" alt="icono de usuario">
                    <label class="campo__label" for="usuario">Dirección</label>
                </div>
                <input type="text" id="usuario" required>
                <div class="linea-separadora"></div>
            </div>
            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/llamada-telefonica.png" alt="icono de usuario">
                    <label class="campo__label" for="usuario">Telefono</label>
                </div>
                <input type="text" id="usuario">
                <div class="linea-separadora"></div>
            </div>
            <button type="submit" class="contenedor-login__iniciar-sesion">REGISTRARSE</button>
            <div class="contenedor-login__registrarse">
                <h3 class="registrarse__pregunta">¿Ya tienes cuenta?</h3>
                <a class="registrarse__formulario" href="../../index.html">Iniciar Sesión</a>
            </div>
        </form>
    </main>
</body>

</html>