<%-- 
    Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
    Propósito: Notificación de éxito confirmando la eliminación definitiva de una cuenta de usuario.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Usuario Eliminado</title>
</head>

<body class="grid-principal pagina-eliminacion">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
    </header>

    <main class="detalle-usuario">
        <!-- Modal de éxito con fondo negro -->
        <div class="modal-exito-overlay" style="display: flex;">
            <div class="modal-exito-contenido">
                <div class="modal-exito__icono">
                    <img src="../../Assets/icons/confirmacion-datos.png" alt="éxito">
                </div>
                <h2 class="modal-exito__titulo">La cuenta fue eliminada con éxito</h2>
                <div class="modal-exito__acciones">
                    <a href="administrador-usuarios.jsp" class="btn-exito">Volver a Usuarios</a>
                </div>
            </div>
        </div>
    </main>

    <footer class="navbar">
        <nav class="navbar-inferior">
            <a href="administrador-dashboard.jsp" class="navbar-inferior__item">
                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Dashboard</span>
            </a>
            <a href="administrador-servicios.jsp" class="navbar-inferior__item">
                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Servicios</span>
            </a>
            <a href="administrador-usuarios.jsp" class="navbar-inferior__item navbar-inferior__item--activo">
                <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Usuarios</span>
            </a>
            <a href="../../index.jsp" class="navbar-inferior__item">
                <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Salir</span>
            </a>
        </nav>
    </footer>
</body>

</html>
