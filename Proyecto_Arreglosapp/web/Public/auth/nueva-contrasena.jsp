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
    <main class="contenedor-principal">
        <a class="contenedor-principal__flecha-atras" href="../../Public/auth/recuperar-contrasena.html"> <img
                class="flecha-atras__imagen" src="../../Assets/icons/arrow.png" alt="flecha hacia atras"></a>
        <div class="contenedor-principal__logo-recuperar">
            <img class="contenedor-principal__imagen" src="../../Assets/icons/icono-recuperar-usuario.png"
                alt="imagen de recuperar contraseña">
        </div>
        <div class="contenedor-principal__formulario-recuperar">
            <h1 class="formulario-recuperar__titulo">Nueva Contraseña</h1>
            <p class="formulario-recuperar__mensaje-recuperar">En este apartado podras ingresar la nueva contraseña.
                ¡Ten cuidado de no volverla a perder!</p>
            <form class="formulario-recuperar__campos" action="../../Public/auth/confirmacion-contraseña.html"
                method="get">
                <div class="campos__formularios">
                    <div class="contenedor-login__campo">
                        <div class="campo__contrasena">
                            <img class="campo__contrasena__icono" src="../../Assets/icons/padlock.png"
                                alt="icono de candado">
                            <label for="contrasena">Nueva Contraseña</label>
                        </div>
                        <input type="password" id="contrasena" placeholder="">
                        <div class="linea-separadora"></div>
                    </div>
                    <div class="contenedor-login__campo">
                        <div class="campo__contrasena">
                            <img class="campo__contrasena__icono" src="../../Assets/icons/padlock.png"
                                alt="icono de candado">
                            <label for="contrasena">Confirmar Contraseña</label>
                        </div>
                        <input type="password" id="contrasena" placeholder="">
                        <div class="linea-separadora"></div>
                    </div>
                </div>
                <button class="contenedor-login__iniciar-sesion" type="submit" class="boton-verificar">Cambiar
                    Contraseña</button>
            </form>
        </div>
    </main>
</body>

</html>