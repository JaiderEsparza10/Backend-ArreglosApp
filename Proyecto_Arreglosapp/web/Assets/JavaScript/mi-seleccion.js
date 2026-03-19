/**
 * ARCHIVO: mi-seleccion.js
 * PROPÓSITO: Gestión de la lista de servicios favoritos/seleccionados, permitiendo la eliminación asíncrona mediante Fetch API.
 * REQUISITOS FUNCIONALES: RF1, RF4.
 */
var favoritoIdAEliminar = null;
var elementoAEliminar = null;

// =====================
// TOAST
// =====================
function mostrarToast(mensaje, tipo) {
    var toast = document.getElementById('toast');
    if (!toast) return;

    var clase = 'toast--exito';
    if (tipo === 'error' || tipo === false || mensaje.toLowerCase().includes('error')) {
        clase = 'toast--error';
    } else if (tipo === 'advertencia' || mensaje.toLowerCase().includes('pendiente')) {
        clase = 'toast--advertencia';
    }

    toast.textContent = mensaje;
    toast.className = 'toast ' + clase + ' toast--visible';

    setTimeout(function () {
        toast.classList.remove('toast--visible');
    }, 3000);
}

// =====================
// MODAL
// =====================
function prepararEliminar(favoritoId, enlace) {
    favoritoIdAEliminar = favoritoId;
    elementoAEliminar = enlace.closest('section');
    document.getElementById('modalEliminar').classList.add('modal--activo');
}

function cerrarModal() {
    document.getElementById('modalEliminar').classList.remove('modal--activo');
    favoritoIdAEliminar = null;
    elementoAEliminar = null;
}

// =====================
// CONFIRMAR ELIMINAR
// =====================
document.getElementById('btnConfirmarEliminar').addEventListener('click', function () {
    if (!favoritoIdAEliminar) return;

    var data = new URLSearchParams();
    data.append('accion', 'eliminar');
    data.append('favoritoId', favoritoIdAEliminar);

    fetch('/Proyecto_Arreglosapp/FavoritoServlet', {
        method: 'POST',
        body: data,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
        .then(function (response) {
            if (response.status === 401) {
                window.location.href = '/Proyecto_Arreglosapp/index.jsp';
                throw new Error('No autorizado');
            }
            return response.json();
        })
        .then(function (data) {
            if (data.success) {
                // ✅ Guardar referencia local ANTES de cerrar el modal
                var cardAEliminar = elementoAEliminar;

                // Cerrar modal — esto pone elementoAEliminar = null
                cerrarModal();

                // Eliminar card usando la copia local
                if (cardAEliminar) cardAEliminar.remove();

                mostrarToast('✅ Eliminado de tu selección', true);

                // Si no quedan cards mostrar mensaje vacío
                var secciones = document.querySelectorAll('.contenido-seleccion__contenedor');
                if (secciones.length === 0) {
                    var main = document.querySelector('.contenido-seleccion');
                    var msg = document.createElement('h2');
                    msg.textContent = 'No tienes arreglos en tu selección aún.';
                    msg.style.cssText = 'text-align:center;color:#333;font-size:1.5rem;margin-top:50px;';
                    main.appendChild(msg);
                }
            } else {
                cerrarModal();
                mostrarToast('❌ ' + data.message, false);
            }
        })
        .catch(function (error) {
            if (error.message !== 'No autorizado') {
                cerrarModal();
                mostrarToast('❌ Error al comunicarse con el servidor', false);
            }
        });
});

// Cerrar modal al hacer clic en el fondo oscuro
document.getElementById('modalEliminar').addEventListener('click', function (e) {
    if (e.target === this) cerrarModal();
});