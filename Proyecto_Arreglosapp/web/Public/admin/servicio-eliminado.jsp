<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Servicio Eliminado</title>
</head>

<body class="grid-principal">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
    </header>

    <main class="dashboard">
        <h2 class="dashboard__titulo-seccion">Servicios Disponibles</h2>

        <!-- Modal de Éxito Visible -->
        <div class="modal" style="visibility: visible; opacity: 1;">
            <div class="modal-contenido">
                <img src="../../Assets/icons/confirmacion-datos.png" alt="éxito" class="modal__imagen">
                <h2 class="modal__titulo">El servicio fue eliminado con éxito</h2>
                <div class="modal__acciones">
                    <a href="administrador-servicios.html" class="btn-modal btn-modal--cancelar">Volver</a>
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
            <a href="administrador-servicios.html" class="navbar-inferior__item navbar-inferior__item--activo">
                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Servicios</span>
            </a>
            <a href="administrador-usuarios.html" class="navbar-inferior__item">
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