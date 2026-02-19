<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Pagina Principal</title>
</head>
<body class="grid-principal">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
    </header>
    <main class="contenido">
        <div class="contenido__barra-busqueda">
            <img src="../../Assets/icons/lupa.png" alt="lupa de carga">
            <input type="text" name="buscador" id="buscador" placeholder="Buscar Arreglos">
        </div>
        <h2 class="contenido__titulo-seccion">Categorías</h2>
        <section class="contenido__categorias">
            <div class="categorias__grid">
                <article class="tarjeta-arreglo">
                    <div class="tarjeta-arreglo__imagen-contenedor">
                        <a href="arreglos-medida.html"><img src="../../Assets/image/imagen-arreglos-de-vestidos-de-fiesta.jpg" alt="imagen de medidas y arreglos"></a>
                    </div>
                    <div class="tarjeta-arreglo__contenido">
                        <h3 class="tarjeta-arreglo__nombre">Arreglo de Medidas y Ajustes</h3>
                        <div class="tarjeta-arreglo__footer">
                            <p class="tarjeta-arreglo__precio">$15.000</p>
                            <a href="arreglos-medida.html" class="tarjeta-arreglo__enlace">Detalles</a>
                        </div>
                    </div>
                </article>
                <article class="tarjeta-arreglo">
                    <div class="tarjeta-arreglo__imagen-contenedor">
                        <a href="vestidos-finos.html"><img src="../../Assets/image/imagen-vestidos-finos.jpg" alt="imagen de ajustes de vestidos y ropa"></a>
                    </div>
                    <div class="tarjeta-arreglo__contenido">
                        <h3 class="tarjeta-arreglo__nombre">Ajuste Fino de Vestidos y Ropa</h3>
                        <div class="tarjeta-arreglo__footer">
                            <p class="tarjeta-arreglo__precio">$20.000</p>
                            <a href="vestidos-finos.html" class="tarjeta-arreglo__enlace">Detalles</a>
                        </div>
                    </div>
                </article>

                <article class="tarjeta-arreglo">
                    <div class="tarjeta-arreglo__imagen-contenedor">
                        <a href="detalles-costuras.html"><img src="../../Assets/image/imagen-costura.jpg" alt="imagen de costura general"></a>
                    </div>
                    <div class="tarjeta-arreglo__contenido">
                        <h3 class="tarjeta-arreglo__nombre">Costura y Reparación General</h3>
                        <div class="tarjeta-arreglo__footer">
                            <p class="tarjeta-arreglo__precio">$25.000</p>
                            <a href="detalles-costuras.html" class="tarjeta-arreglo__enlace">Detalles</a>
                        </div>
                    </div>
                </article>

                <article class="tarjeta-arreglo">
                    <div class="tarjeta-arreglo__imagen-contenedor">
                        <a href="detalles-sastreria.html"><img src="../../Assets/image/imagen-sastreria.jpg" alt="imagen de sastreria y Dobladillos"></a>
                    </div>
                    <div class="tarjeta-arreglo__contenido">
                        <h3 class="tarjeta-arreglo__nombre">Sastrería y Dobladillos Manuales</h3>
                        <div class="tarjeta-arreglo__footer">
                            <p class="tarjeta-arreglo__precio">$35.000</p>
                            <a href="detalles-sastreria.html" class="tarjeta-arreglo__enlace">Detalles</a>
                        </div>
                    </div>
                </article>

                <article class="tarjeta-arreglo">
                    <div class="tarjeta-arreglo__imagen-contenedor">
                        <a href="detalles-arreglos-bolsillos.html"><img src="../../Assets/image/image-arreglo-bolsillos.jpg" alt="imagen de arreglo de bolsillos"></a>
                    </div>
                    <div class="tarjeta-arreglo__contenido">
                        <h3 class="tarjeta-arreglo__nombre">Arreglo de Bolsillos y Remiendos</h3>
                        <div class="tarjeta-arreglo__footer">
                            <p class="tarjeta-arreglo__precio">$18.000</p>
                            <a href="detalles-arreglos-bolsillos.html" class="tarjeta-arreglo__enlace">Detalles</a>
                        </div>
                    </div>
                </article>

                <article class="tarjeta-arreglo">
                    <div class="tarjeta-arreglo__imagen-contenedor">
                        <a href="detalles-planchado.html"><img src="../../Assets/image/imagen-planchado de ropa.jpg" alt="imagen de una persona planchando"></a>
                    </div>
                    <div class="tarjeta-arreglo__contenido">
                        <h3 class="tarjeta-arreglo__nombre">Servicios Planchado de Ropa</h3>
                        <div class="tarjeta-arreglo__footer">
                            <p class="tarjeta-arreglo__precio">$12.000</p>
                            <a href="detalles-planchado.html" class="tarjeta-arreglo__enlace">Detalles</a>
                        </div>
                    </div>
                </article>  
            </div>
        </section>
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