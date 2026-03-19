/**
 * ========================================
 * TOAST NOTIFICATIONS - JAVASCRIPT GLOBAL
 * ========================================
 * 
 * Este archivo proporciona la función global mostrarToast() para
 * mostrar notificaciones consistentes en todo el proyecto ArreglosApp.
 * 
 * REQUISITOS:
 * - Incluir estilos.css principal (que importa toast-uniforme.css)
 * - Tener un div con id="toastContainer" en el body
 * 
 * USO:
 * mostrarToast('Mensaje de éxito', 'success'); // Verde
 * mostrarToast('Mensaje de error', 'error');   // Rojo
 * mostrarToast('Información', 'info');         // Azul
 * mostrarToast('Advertencia', 'warning');      // Amarillo
 * 
 * El toast se mostrará durante 3 segundos y luego desaparecerá automáticamente.
 * ========================================
 */

/**
 * Muestra un toast notification en la esquina superior derecha
 * @param {string} mensaje - El mensaje a mostrar
 * @param {string} tipo - El tipo de toast: 'success', 'error', 'info', 'warning'
 * @param {number} duracion - Duración en milisegundos (default: 3000ms)
 */
function mostrarToast(mensaje, tipo, duracion) {
    // Mapeo dinámico de Tipos a Clases CSS (Meta 2)
    var tipoClase = 'toast--exito'; // Default success
    if (tipo === 'error' || tipo === 'danger' || mensaje.toLowerCase().includes('error') || mensaje.toLowerCase().includes('canceló')) {
        tipoClase = 'toast--error';
    } else if (tipo === 'warning' || tipo === 'advertencia' || mensaje.toLowerCase().includes('pendiente') || mensaje.toLowerCase().includes('proceso')) {
        tipoClase = 'toast--advertencia';
    } else if (tipo === 'info') {
        tipoClase = 'toast--info'; // Requires CSS class if needed, otherwise fallback
    } else if (tipo === 'success' || tipo === 'exito') {
        tipoClase = 'toast--exito';
    }

    // Obtener o crear el contenedor
    var container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    
    // Crear el elemento toast
    var toast = document.createElement('div');
    toast.className = 'toast ' + tipoClase;
    toast.textContent = mensaje;
    
    // Agregar al contenedor
    container.appendChild(toast);
    
    // Trigger de animación
    setTimeout(function() {
        toast.classList.add('toast--visible');
    }, 10);
    
    // Remover después de la duración (3s default)
    duracion = duracion || 3000;
    setTimeout(function() {
        toast.classList.remove('toast--visible');
        setTimeout(function() {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 500);
    }, duracion);
}

/**
 * Muestra un toast de éxito (verde)
 * @param {string} mensaje - El mensaje a mostrar
 * @param {number} duracion - Duración en milisegundos (default: 3000ms)
 */
function toastExito(mensaje, duracion) {
    mostrarToast(mensaje, 'success', duracion);
}

/**
 * Muestra un toast de error (rojo)
 * @param {string} mensaje - El mensaje a mostrar
 * @param {number} duracion - Duración en milisegundos (default: 3000ms)
 */
function toastError(mensaje, duracion) {
    mostrarToast(mensaje, 'error', duracion);
}

/**
 * Muestra un toast de información (azul)
 * @param {string} mensaje - El mensaje a mostrar
 * @param {number} duracion - Duración en milisegundos (default: 3000ms)
 */
function toastInfo(mensaje, duracion) {
    mostrarToast(mensaje, 'info', duracion);
}

/**
 * Muestra un toast de advertencia (amarillo)
 * @param {string} mensaje - El mensaje a mostrar
 * @param {number} duracion - Duración en milisegundos (default: 3000ms)
 */
function toastWarning(mensaje, duracion) {
    mostrarToast(mensaje, 'warning', duracion);
}

// ========================================
// AUTO-INICIALIZACIÓN (opcional)
// ========================================
// Si hay mensajes de éxito/error en la URL (parámetros), mostrarlos automáticamente

document.addEventListener('DOMContentLoaded', function() {
    var params = new URLSearchParams(window.location.search);
    
    // Mensaje de éxito desde URL
    if (params.get('exito')) {
        var mensaje = decodeURIComponent(params.get('exito'));
        mostrarToast('✓ ' + mensaje, 'success');
    }
    
    // Mensaje de error desde URL
    if (params.get('error')) {
        var mensaje = decodeURIComponent(params.get('error'));
        mostrarToast('✗ ' + mensaje, 'error');
    }
});
