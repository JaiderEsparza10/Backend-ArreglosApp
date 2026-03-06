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
// TOAST AL CARGAR
// =====================
window.addEventListener('load', function () {
    var params = new URLSearchParams(window.location.search);
    if (params.get('editado') === '1') {
        mostrarToast('✅ Datos actualizados correctamente', true);
        history.replaceState({}, document.title, window.location.pathname);
    }
    if (params.get('passwordCambiada') === '1') {
        mostrarToast('✅ Contraseña cambiada correctamente', true);
        history.replaceState({}, document.title, window.location.pathname);
    }

    // Abrir sección si hay error de contraseña
    var hash = window.location.hash;
    if (hash === '#cambiarPassword') {
        toggleSeccion('formPassword');
    }
});