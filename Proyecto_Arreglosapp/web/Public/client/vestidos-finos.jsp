<%-- 
    Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
    Propósito: Detalle informativo sobre ajustes de alta costura para vestidos y prendas delicadas.
--%>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Ajuste Fino de Vestidos y Ropa</title>
</head>

<body class="contenedor-encabezado-detalles"> <header class="contenedor-encabezado-detalles__encabezado"> <a class="encabezado__enlace" href="pagina-principal.jsp"> <img class="enlace__imagen" src="${pageContext.request.contextPath}/Assets/icons/flecha-izquierda__blanca.png" alt="flecha atras">
        </a>
        <h1 class="encabezado__titulo">Detalles</h1>
    </header>
    <main class="contenido-detalles"> <div class="contenido-detalles_informacion-detalles"> <img class="informacion-detalles__imagen-arreglo" src="../../Assets/image/imagen-vestidos-finos.jpg"
                alt="Imagen de detalles del arreglo"> <div class="informacion-detalles__informacion"> <h1 class="informacion__titulo">Ajuste Fino de Vestidos y Ropa</h1> <p class="informacion__parrafo">Especializados en ajustes precisos para vestidos de fiesta,
                    trajes formales y ropa delicada. Trabajamos con materiales finos garantizando el mayor
                    cuidado en cada prenda.</p>
                <a class="informacion__enlace" href="#" id="btnAgregarSeleccion"> <img class="enlace__icono" src="../../Assets/icons/agregar-recordatorio__color.png"
                        alt="icono de una campana">
                    Agregar a mi selección
                </a>
                <div class="informacion__tiempo-estimado">
                    <p>Tiempo de espera:</p>
                    <p class="tiempo-estimado__dias">5 a 7 dias</p>
                </div>
                <a class="informacion__enlace-personalizar" href="personalizar-arreglo.jsp">Personalizar Arreglo</a>
            </div>
        </div>
    </main>
    <div id="toast" class="toast"></div> <script src="../../Assets/JavaScript/agregar-seleccion-vestidos.js"></script>
</body>

</html>