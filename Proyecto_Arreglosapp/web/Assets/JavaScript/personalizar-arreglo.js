// =====================
// CONTADOR DESCRIPCIÓN
// =====================
var textarea = document.getElementById('descripcion');
var contador = document.getElementById('contadorDesc');

textarea.addEventListener('input', function () {
    contador.textContent = this.value.length;
    if (this.value.length >= 450) {
        contador.style.color = '#c62828';
    } else {
        contador.style.color = '#555';
    }
});

// =====================
// PREVIEW IMAGEN
// =====================
document.getElementById('imagenReferencia').addEventListener('change', function (e) {
    var file = e.target.files[0];
    if (!file) return;

    var nombreArchivo = document.getElementById('nombreArchivo');
    var previewContenedor = document.getElementById('previewContenedor');
    var previewImagen = document.getElementById('previewImagen');

    nombreArchivo.textContent = file.name;

    var reader = new FileReader();
    reader.onload = function (event) {
        previewImagen.src = event.target.result;
        previewContenedor.style.display = 'flex';
    };
    reader.readAsDataURL(file);
});

function quitarImagen() {
    document.getElementById('imagenReferencia').value = '';
    document.getElementById('nombreArchivo').textContent = 'Agregar Foto';
    document.getElementById('previewContenedor').style.display = 'none';
    document.getElementById('previewImagen').src = '';
}

// =====================
// VALIDACIÓN ANTES DE ENVIAR
// =====================
document.getElementById('formPersonalizar').addEventListener('submit', function (e) {
    var categoria = document.querySelector('input[name="idServicio"]:checked');
    if (!categoria) {
        e.preventDefault();
        mostrarToast('❌ Debes seleccionar una categoría', false);
        // Resaltar la sección de categorías
        document.querySelector('.contenedor__formulario').scrollTop = 0;
        window.scrollTo({ top: 0, behavior: 'smooth' });
        return;
    }

    var descripcion = document.getElementById('descripcion').value.trim();
    if (!descripcion) {
        e.preventDefault();
        mostrarToast('❌ Debes ingresar una descripción', false);
        document.getElementById('descripcion').focus();
        return;
    }

    // Deshabilitar botón para evitar doble envío
    var btn = document.getElementById('btnConfirmar');
    btn.disabled = true;
    btn.textContent = 'Enviando...';
});

// =====================
// RESALTAR CARD SELECCIONADA
// =====================
var radios = document.querySelectorAll('.seleccion__circulo');
radios.forEach(function (radio) {
    radio.addEventListener('change', function () {
        // Quitar selección de todas las cards
        document.querySelectorAll('.formulario__seleccion').forEach(function (card) {
            card.classList.remove('formulario__seleccion--activo');
        });
        // Resaltar la seleccionada
        this.closest('.formulario__seleccion').classList.add('formulario__seleccion--activo');
    });
});

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