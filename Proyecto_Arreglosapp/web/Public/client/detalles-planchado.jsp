<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Servicios Planchado de Ropa</title>
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
            <img class="informacion-detalles__imagen-arreglo" src="../../Assets/image/imagen-planchado de ropa.jpg"
                alt="Imagen de detalles del arreglo">
            <div class="informacion-detalles__informacion">
                <h1 class="informacion__titulo">Servicios Planchado de Ropa</h1>
                <p class="informacion__parrafo">Planchado profesional para todas tus prendas. Garantizamos un acabado
                    impecable, desde camisas y pantalones hasta ropa delicada, cuidando la calidad de cada tejido.</p>
                <a class="informacion__enlace" href="#" id="btnAgregarSeleccion">
                    <img class="enlace__icono" src="../../Assets/icons/agregar-recordatorio__color.png"
                        alt="icono de una campana">
                    Agregar a mi selección
                </a>
                <div class="informacion__tiempo-estimado">
                    <p>Tiempo de espera:</p>
                    <p class="tiempo-estimado__dias">24 horas</p>
                </div>
                <a class="informacion__enlace-personalizar" href="personalizar-arreglo.jsp">Personalizar Arreglo</a>
            </div>
        </div>
    </main>
    <script>
        document.getElementById('btnAgregarSeleccion').addEventListener('click', function (e) {
            e.preventDefault();
            const data = new URLSearchParams();
            data.append('accion', 'agregar');
            data.append('arregloId', '3');
            data.append('categoria', 'Planchado Profesional');
            data.append('nombreCategoria', 'Servicios Planchado de Ropa');
            data.append('precio', '8000');
            data.append('imagenUrl', '../../Assets/image/imagen-planchado de ropa.jpg');

            fetch('../../FavoritoServlet', {
                method: 'POST',
                body: data,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            })
                .then(response => {
                    if (response.status === 401) {
                        alert('Debes iniciar sesión para agregar a tu selección');
                        window.location.href = '../../index.jsp';
                        throw new Error('No autorizado');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        alert('✅ ' + data.message);
                        window.location.href = 'mi-seleccion.jsp';
                    } else {
                        alert('❌ ' + data.message);
                    }
                })
                .catch(error => { console.error('Error:', error); alert('❌ Error al comunicarse con el servidor. Si el error persiste, inicia sesión nuevamente.'); });
        });
    </script>
</body>

</html>