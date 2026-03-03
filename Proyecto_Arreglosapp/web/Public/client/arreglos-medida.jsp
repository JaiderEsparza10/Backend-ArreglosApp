<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Arreglo de Medidas y Ajustes</title>
</head>

<body class="contenedor-encabezado-detalles">
    <header class="contenedor-encabezado-detalles__encabezado">
        <a class="encabezado__enlace" href="pagina-principal.jsp">
            <img class="enlace__imagen" src="../../Assets/icons/flecha-izquierda__blanca.png" alt="flecha atras">
        </a>
        <h1 class="encabezado__titulo">Detalles</h1>
    </header>
    <main class="contenido-detalles">
        <div class="contenido-detalles_informacion-detalles">
            <img class="informacion-detalles__imagen-arreglo"
                src="../../Assets/image/imagen-arreglos-de-vestidos-de-fiesta.jpg" alt="Imagen de detalles del arreglo">
            <div class="informacion-detalles__informacion">
                <h1 class="informacion__titulo">Arreglo de Medidas y Ajustes</h1>
                <p class="informacion__parrafo">Realizamos ajustes precisos a tus prendas para que se adapten
                    perfectamente a tu cuerpo. Desde entallas hasta ensanches, garantizamos un resultado
                    profesional y cómodo.</p>
                <a class="informacion__enlace" href="#" id="btnAgregarSeleccion">
                    <img class="enlace__icono" src="../../Assets/icons/agregar-recordatorio__color.png"
                        alt="icono de una campana">
                    Agregar a mi selección
                </a>
                <div class="informacion__tiempo-estimado">
                    <p>Tiempo de espera:</p>
                    <p class="tiempo-estimado__dias">4 a 6 dias</p>
                </div>
                <a class="informacion__enlace-personalizar" href="personalizar-arreglo.jsp">Personalizar Arreglo</a>
            </div>
        </div>
    </main>
    <div id="toast" class="toast"></div>
    <script src="../../Assets/JavaScript/agregar-seleccion-medidas.js"></script>
</body>

</html>