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
    <main class="contenedor-principal">
        <a class="contenedor-principal__flecha-atras" href="../../index.html"> <img class="flecha-atras__imagen" src="../../Assets/icons/arrow.png" alt="flecha hacia atras"></a>
        <div class="contenedor-principal__logo-recuperar">
            <img class="contenedor-principal__imagen" src="../../Assets/icons/icono-recuperar-usuario.png" alt="imagen de recuperar contraseña">
        </div>
        <div class="contenedor-principal__formulario-recuperar">
            <h1 class="formulario-recuperar__titulo">Recuperar Contraseña</h1>
            <p class="formulario-recuperar__mensaje-recuperar">¡Que triste! pero no te preocupes hemos enviado un codigo a la siguiente cuenta de gmail: xen*****@gmail.com</p>
            <form class="formulario-recuperar__campos" action="../../Public/auth/nueva-contrasena.html" method="get">
                <div class="campos__label-codigo">
                    <input type="text" class="label-codigo__input-codigo" name="digito1" maxlength="1" required>
                    <input type="text" class="label-codigo__input-codigo" name="digito2" maxlength="1" required>
                    <input type="text" class="label-codigo__input-codigo" name="digito3" maxlength="1" required>
                    <input type="text" class="label-codigo__input-codigo" name="digito4" maxlength="1" required>
                    <input type="text" class="label-codigo__input-codigo" name="digito5" maxlength="1" required>
                </div>
                <button class="contenedor-login__iniciar-sesion" type="submit" class="boton-verificar">Cambiar Contraseña</button>
            </form>
        </div> 
    </main>
</body>
</html>