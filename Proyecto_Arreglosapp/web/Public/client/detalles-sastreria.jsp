<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Sastrería y Dobladillos Manuales</title>
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
            <img class="informacion-detalles__imagen-sastreria" src="../../Assets/image/imagen-sastreria.jpg"
                alt="Imagen de detalles del arreglo">
            <div class="informacion-detalles__informacion">
                <h1 class="informacion__titulo">Sastrería y Dobladillos</h1>
                <p class="informacion__parrafo">Destaca la calidad y el cuidado artesanal de los arreglos, enfatizando
                    la precisión y la atención al detalle en trabajos que requieren técnica manual.</p>
                <a class="informacion__enlace" href="#" id="btnAgregarSeleccion">
                    <img class="enlace__icono" src="../../Assets/icons/agregar-recordatorio__color.png"
                        alt="icono de una campana">
                    Agregar a mi selección
                </a>
                <div class="informacion__tiempo-estimado">
                    <p>Tiempo de espera:</p>
                    <p class="tiempo-estimado__dias">5 a 7 dias</p>
                </div>
                <div class="informacion__acciones">
                    <a class="informacion__enlace-personalizar" href="personalizar-arreglo.jsp">Personalizar Arreglo</a>
                    <button class="informacion__boton-favorito"
                        onclick="agregarFavorito(1, 'Sastrería y Dobladillos', 'sastreria', 25000, '../../Assets/image/imagen-sastreria.jpg')">
                        <img src="../../Assets/icons/corazon.png" alt="icono de favoritos"
                            class="boton-favorito__icono">
                        Agregar a Favoritos
                    </button>
                </div>
            </div>
        </div>
    </main>

    <script>
        document.getElementById('btnAgregarSeleccion').addEventListener('click', function (e) {
            e.preventDefault();
            const data = new URLSearchParams();
            data.append('accion', 'agregar');
            data.append('arregloId', '1');
            data.append('categoria', 'Sastrería y Dobladillos');
            data.append('nombreCategoria', 'Sastrería y Dobladillos');
            data.append('precio', '25000');
            data.append('imagenUrl', '../../Assets/image/imagen-sastreria.jpg');

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

        function agregarFavorito(arregloId, nombreCategoria, categoria, precio, imagenUrl) {
            // Enviar solicitud al servidor para agregar favorito
            fetch('../../FavoritoServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'accion=agregar&arregloId=' + arregloId +
                    '&nombreCategoria=' + encodeURIComponent(nombreCategoria) +
                    '&categoria=' + encodeURIComponent(categoria) +
                    '&precio=' + precio +
                    '&imagenUrl=' + encodeURIComponent(imagenUrl)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('✅ ' + nombreCategoria + ' agregado a favoritos');
                        // Opcional: cambiar el botón a "agregado"
                        event.target.innerHTML = '✓ Agregado a Favoritos';
                        event.target.disabled = true;
                    } else {
                        alert('❌ Error: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('❌ Error al agregar a favoritos');
                });
        }
    </script>
</body>

</html>