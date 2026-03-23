/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Lógica de interfaz para la gestión del perfil del usuario, incluyendo toggles de secciones y notificaciones.
 * REQUISITOS FUNCIONALES: RF3, RF4, RF20.
 */

// =====================
// TOGGLE SECCIONES
// =====================
function toggleSeccion(id) {
    var contenido = document.getElementById(id);
    var flecha = document.getElementById('flecha' + id.charAt(0).toUpperCase() + id.slice(1));

    if (contenido.style.display === 'none' || contenido.style.display === '') {
        contenido.style.display = 'block';
        if (flecha) flecha.textContent = '▲';
    } else {
        contenido.style.display = 'none';
        if (flecha) flecha.textContent = '▼';
    }
}

// =====================
// TOGGLE CONTRASEÑA
// =====================
function togglePass(inputId, ojoId) {
    var input = document.getElementById(inputId);
    var ojo = document.getElementById(ojoId);
    if (input.type === 'password') {
        input.type = 'text';
        ojo.textContent = '🙈';
    } else {
        input.type = 'password';
        ojo.textContent = '👁';
    }
}

// =====================
// TOAST DINÁMICO (Meta 2)
// =====================
function mostrarToast(mensaje, tipo) {
    var toast = document.getElementById('toast');
    if (!toast) return;

    if (!tipo) {
        var msg = mensaje.toLowerCase();
        if (msg.includes('✅') || msg.includes('éxito') || msg.includes('correctamente')) {
            tipo = 'exito';
        } else if (msg.includes('❌') || msg.includes('error')) {
            tipo = 'error';
        } else if (msg.includes('pendiente') || msg.includes('en proceso')) {
            tipo = 'advertencia';
        } else {
            tipo = 'exito';
        }
    }

    toast.textContent = mensaje;
    toast.className = 'toast toast--' + tipo;
    toast.classList.add('toast--visible');

    setTimeout(function () {
        toast.classList.remove('toast--visible');
    }, 3000);
}

// =====================
// EVENTOS AL CARGAR
// =====================
window.addEventListener('load', function () {
    var params = new URLSearchParams(window.location.search);
    if (params.get('editado') === '1') {
        mostrarToast('✅ Datos actualizados correctamente', 'exito');
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('passwordCambiada') === '1') {
        mostrarToast('✅ Contraseña cambiada correctamente', 'exito');
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('telefonoAgregado') === '1') {
        mostrarToast('✅ Teléfono agregado exitosamente', 'exito');
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('telefonoEliminado') === '1') {
        mostrarToast('✅ Teléfono eliminado exitosamente', 'exito');
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('telefonoPrincipal') === '1') {
        mostrarToast('✅ Teléfono marcado como principal', 'exito');
        history.replaceState({}, document.title, window.location.pathname);
    }

    // Abrir sección si hay error de contraseña
    var hash = window.location.hash;
    if (hash === '#cambiarPassword') {
        toggleSeccion('formPassword');
    }

    // VALIDACIÓN DE NOMBRE (Nuevo Requerimiento: No números)
    var formEditar = document.querySelector('form[action*="PerfilServlet"]');
    if (formEditar) {
        formEditar.addEventListener('submit', function (e) {
            var inputNombre = this.querySelector('input[name="nombre"]');
            if(inputNombre) {
                var nombre = inputNombre.value.trim();
                // Regex: Mínimo 3 caracteres, letras, espacios y tildes solamente.
                var regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{3,}$/;
                if (!regexNombre.test(nombre)) {
                    e.preventDefault();
                    mostrarToast('❌ El nombre solo debe contener letras y tener al menos 3 caracteres.', 'error');
                }
            }
        });
    }

    // VALIDACIÓN DE TELÉFONO EN TIEMPO REAL
    var inputTelefono = document.querySelector('input[name="nuevoTelefono"]');
    if (inputTelefono) {
        inputTelefono.addEventListener('input', function(e) {
            if (/[^0-9]/.test(this.value)) {
                mostrarToast('❌ El teléfono solo debe contener números', 'error');
                // Limpiar todo lo que no sea número inmediatamente
                this.value = this.value.replace(/[^0-9]/g, '');
            }
        });
    }
});