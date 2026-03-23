<%-- 
    Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
    Propósito: Visualización de los servicios marcados como favoritos por el cliente.
--%>
<%@page import="model.Usuario"%>
<%@page import="model.Favorito"%>
<%@page import="java.util.List"%>
<%
    // Validación de integridad de sesión para el acceso a preferencias
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("../../index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Mis Favoritos</title>
</head>
<body class="grid-principal"> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación"> <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
    </header>
    
    <main class="contenido-arreglos"> <div class="contenido-arreglos__enlace-volver"> <a href="pagina-principal.jsp" class="enlace-volver__texto">← Volver</a> <h1 class="enlace-volver__titulo">Mis Favoritos</h1>
        </div>
        
        <section class="arreglos-grid">
            <!-- Los favoritos se cargarán dinámicamente con JavaScript -->
            <div id="favoritos-container"> <div class="loading-message">
                    <p>Cargando favoritos...</p>
                </div>
            </div>
        </section>
    </main>
    
    <footer class="navbar"> <nav class="navbar-inferior"> <a href="pagina-principal.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/inicio.png" alt="icono de inicio" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Inicio</span>
            </a>
            <a href="mis-arreglos.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/arreglos.png" alt="icono de arreglos" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Mis Arreglos</span>
            </a>
            <a href="mi-perfil.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/usuario.png" alt="icono de usuario" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Perfil</span>
            </a>
        </nav>
    </footer>
    
    <script>
        // Cargar favoritos cuando la página se carga
        document.addEventListener('DOMContentLoaded', function() {
            cargarFavoritos();
        });
        
        function cargarFavoritos() {
            fetch('../../FavoritoServlet')
                .then(response => response.json())
                .then(data => {
                    const container = document.getElementById('favoritos-container');
                    
                    if (data.success && data.data && data.data.length > 0) {
                        container.innerHTML = data.data.map(favorito => `
                            <article class="tarjeta-arreglo"> <div class="tarjeta-arreglo__imagen-contenedor"> <img src="${favorito.imagenUrl}" alt="${favorito.nombreServicio}" class="tarjeta-arreglo__imagen">
                                </div>
                                <div class="tarjeta-arreglo__contenido"> <h3 class="tarjeta-arreglo__nombre">${favorito.nombreServicio}</h3> <p class="tarjeta-arreglo__categoria">${favorito.servicio}</p> <div class="tarjeta-arreglo__footer"> <p class="tarjeta-arreglo__precio">$${favorito.precio.toLocaleString('es-CO')}</p> <button onclick="eliminarFavorito(${favorito.favoritoId})" class="tarjeta-arreglo__eliminar">
                                            Eliminar
                                        </button>
                                    </div>
                                </div>
                            </article>
                        `).join('');
                    } else {
                        container.innerHTML = `
                            <div class="empty-state"> <img src="../../Assets/icons/corazon-vacio.png" alt="icono de favoritos vacíos" class="empty-state__icono"> <h3 class="empty-state__titulo">No tienes favoritos</h3> <p class="empty-state__texto">Agrega servicios a tus favoritos para verlos aquí</p> <a href="pagina-principal.jsp" class="empty-state__boton">Explorar Servicios</a>
                            </div>
                        `;
                    }
                })
                .catch(error => {
                    console.error('Error al cargar favoritos:', error);
                    document.getElementById('favoritos-container').innerHTML = `
                        <div class="error-message">
                            <p>Error al cargar los favoritos. Intenta recargar la página.</p>
                            <button onclick="location.reload()" class="error-message__boton">Recargar</button>
                        </div>
                    `;
                });
        }
        
        function eliminarFavorito(favoritoId) {
            if (confirm('¿Estás seguro de que quieres eliminar este favorito?')) {
                fetch('../../FavoritoServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'accion=eliminar&favoritoId=' + favoritoId
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('✅ Favorito eliminado correctamente');
                        cargarFavoritos(); // Recargar la lista
                    } else {
                        alert('❌ Error: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('❌ Error al eliminar favorito');
                });
            }
        }
    </script>
</body>
</html>
