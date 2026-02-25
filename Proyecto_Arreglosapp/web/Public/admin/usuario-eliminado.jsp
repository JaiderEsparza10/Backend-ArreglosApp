<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Usuario Eliminado</title>
</head>

<body class="grid-principal">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
    </header>

    <main class="detalle-usuario">
        <a href="administrador-usuarios.html" class="detalle-usuario__volver">
            <img src="../../Assets/icons/flecha-atras-negra.png" alt="volver" class="detalle-usuario__volver-icono">
        </a>

        <section class="info-usuario">
            <h2 class="info-usuario__titulo">Información Personal</h2>

            <div class="info-usuario__campo">
                <span class="info-usuario__label">Nombre</span>
                <span class="info-usuario__valor">Jaider Andrés Esparza Arenas</span>
            </div>

            <div class="info-usuario__campo">
                <span class="info-usuario__label">Correo</span>
                <span class="info-usuario__valor">aenna2003@gmail.com</span>
            </div>
        </section>

        <div class="modal" style="visibility: visible; opacity: 1;">
            <div class="modal-contenido">
                <img src="../../Assets/icons/confirmacion-datos.png" alt="éxito" class="modal__imagen">
                <h2 class="modal__titulo">La cuenta fue eliminada con éxito</h2>
                <div class="modal__acciones">
                    <a href="administrador-usuarios.html" class="btn-modal btn-modal--cancelar">Volver</a>
                </div>
            </div>
        </div>
    </main>

    <footer class="navbar">
        <nav class="navbar-inferior">
            <a href="administrador-dashboard.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Dashboard</span>
            </a>
            <a href="administrador-servicios.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Servicios</span>
            </a>
            <a href="administrador-usuarios.html" class="navbar-inferior__item navbar-inferior__item--activo">
                <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Usuarios</span>
            </a>
            <a href="../../index.html" class="navbar-inferior__item">
                <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Salir</span>
            </a>
        </nav>
    </footer>
</body>

</html>