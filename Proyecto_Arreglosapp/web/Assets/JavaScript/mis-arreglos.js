var arregloIdAEliminar = null;

// =====================
// TOAST
// =====================
function mostrarToast(mensaje, exito) {
    var toast = document.getElementById('toast');
    toast.textContent = mensaje;
    toast.className = 'toast ' + (exito ? 'toast--exito' : 'toast--error');
    toast.classList.add('toast--visible');
    setTimeout(function () {
        toast.classList.remove('toast--visible');
    }, 3000);
}

// =====================
// MODAL ELIMINAR
// =====================
function prepararEliminarArreglo(id) {
    arregloIdAEliminar = id;
    document.getElementById('modalEliminarArreglo').classList.add('modal--activo');
}

function cerrarModalArreglo() {
    document.getElementById('modalEliminarArreglo').classList.remove('modal--activo');
    arregloIdAEliminar = null;
}

document.getElementById('btnConfirmarEliminarArreglo').addEventListener('click', function () {
    if (!arregloIdAEliminar) return;

    // Redirigir al servlet con accion=eliminar
    window.location.href = '/Proyecto_Arreglosapp/PersonalizacionServlet?accion=eliminar&id=' + arregloIdAEliminar;
});

// Cerrar modal al hacer clic en el fondo
document.getElementById('modalEliminarArreglo').addEventListener('click', function (e) {
    if (e.target === this) cerrarModalArreglo();
});

// =====================
// MOSTRAR TOAST SI HAY MENSAJE EN URL
window.addEventListener('load', function () {
    var params = new URLSearchParams(window.location.search);
    if (params.get('eliminado') === '1') {
        mostrarToast('✅ Arreglo eliminado correctamente', true);
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('creado') === '1') {
        mostrarToast('✅ Personalización creada correctamente', true);
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('editado') === '1') {
        mostrarToast('✅ Arreglo actualizado correctamente', true);
        history.replaceState({}, document.title, window.location.pathname);
    }
});