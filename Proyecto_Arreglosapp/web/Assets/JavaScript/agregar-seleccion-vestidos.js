/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Lógica para agregar ajustes finos de vestidos y ropa a la selección.
 */
function mostrarToast(mensaje, tipo) {
    var toast = document.getElementById('toast');
    if (!toast) return;
    
    var clase = 'toast--exito';
    if (tipo === 'error' || tipo === false || mensaje.toLowerCase().includes('error')) {
        clase = 'toast--error';
    } else if (tipo === 'advertencia' || mensaje.toLowerCase().includes('debes iniciar')) {
        clase = 'toast--advertencia';
    }

    toast.textContent = mensaje;
    toast.className = 'toast ' + clase + ' toast--visible';
    
    setTimeout(function () {
        toast.classList.remove('toast--visible');
    }, 3000);
}

document.getElementById('btnAgregarSeleccion').addEventListener('click', function (e) {
    e.preventDefault();
    var data = new URLSearchParams();
    data.append('accion', 'agregar');
    data.append('arregloId', '5');
    data.append('categoria', 'Ajustes Finos');
    data.append('nombreCategoria', 'Ajuste Fino de Vestidos y Ropa');
    data.append('precio', '20000');
    data.append('imagenUrl', 'Assets/image/imagen-vestidos-finos.jpg');

    fetch('/Proyecto_Arreglosapp/FavoritoServlet', {
        method: 'POST',
        body: data,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
        .then(function (response) {
            if (response.status === 401) {
                mostrarToast('⚠️ Debes iniciar sesión', false);
                setTimeout(function () { window.location.href = '/Proyecto_Arreglosapp/index.jsp'; }, 2000);
                throw new Error('No autorizado');
            }
            return response.json();
        })
        .then(function (data) {
            if (data.success) {
                mostrarToast('✅ ' + data.message, true);
                setTimeout(function () { window.location.href = 'mi-seleccion.jsp'; }, 2000);
            } else {
                mostrarToast('❌ ' + data.message, false);
            }
        })
        .catch(function (error) {
            if (error.message !== 'No autorizado') {
                mostrarToast('❌ Error al comunicarse con el servidor', false);
            }
        });
});