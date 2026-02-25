<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Cambiar Contraseña</title>
</head>
<body class="grid-principal">
        <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
    </header>
    <main class="contenido-perfil">
        <h1 class="contenido-perfil__titulo-principal">Cambiar Contraseña</h1>
        <h2 class="contenido-datos__titulo titulo__descripcion">En este apartado podras cambiar tu contraseña</h2>
        <section class="contenido-perfil__contenido-datos">
            <div class="formulario__calendario contenido-arreglo__horario">
                <h1 class="horario__titulo">Constraseña Actual</h1>
                <input class="horario__ingresar" type="text" name="horaDispoble" id="horaDispoble" placeholder="">
            </div>
            <div class="formulario__calendario contenido-arreglo__horario">
                <h1 class="horario__titulo">Nueva Contraseña</h1>
                <input class="horario__ingresar" type="text" name="horaDispoble" id="horaDispoble" placeholder="">
            </div>
            <div class="formulario__calendario contenido-arreglo__horario">
                <h1 class="horario__titulo">Numero de Telefono</h1>
                <input class="horario__ingresar" type="text" name="horaDispoble" id="horaDispoble" placeholder="">
            </div>
        </section>
        <div class="formulario__seccion-boton">
            <a class="informacion__enlace-personalizar informacion__enlace-personalizar--modificador" href="#modalEliminar">Cambiar Contraseña</a>
        </div>
        <div id="modalEliminar" class="modal">
            <div class="modal-fondo">
                <div class="modal-contenido">
                    <img class="modal__imagen" src="../../Assets/icons/confirmacion-datos.png" alt="imagen de confirmado">
                    <p class="modal__descripcion">Su contraseña fue actualizada con exito</p>
                    <div class="modal__acciones">
                        <a href="mi-perfil.html" class="btn-modal btn-modal--cancelar">Volver</a>
                    </div>
                </div>
            </div>
        </div>
    </main>
    <footer class="navbar">
        <nav class="navbar-inferior">
            <a href="pagina-principal.html" class="navbar-inferior__item navbar-inferior__item--activo">
                <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono"></img> 
                <span class="navbar-inferior__texto">Inicio</span>
            </a>
            <a href="mi-seleccion.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/lista-de-deseos-transparente.png" class="navbar-inferior__icono"></img> 
                <span class="navbar-inferior__texto">Mi Selección</span>
            </a>
            <a href="mis-arreglos.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/cortar-con-tijeras-transparente.png" class="navbar-inferior__icono"></img> 
                <span class="navbar-inferior__texto">Mis Arreglos</span>
            </a>
            <a href="mis-pedidos.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono"></img> 
                <span class="navbar-inferior__texto">Pedidos</span>
            </a>
            <a href="mi-perfil.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono"></img> 
                <span class="navbar-inferior__texto">Perfil</span>
            </a>
        </nav>
    </footer>
</body>
</html>