// Toggle contraseña visible
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

// Validar que contraseñas coincidan en tiempo real
var passNueva = document.getElementById('passNueva');
var passConfirm = document.getElementById('passConfirm');
var indicador = document.getElementById('indicadorMatch');
var textoMatch = document.getElementById('textoMatch');

if (passConfirm) {
    passConfirm.addEventListener('input', function () {
        if (passConfirm.value.length === 0) {
            indicador.style.display = 'none';
            return;
        }
        indicador.style.display = 'block';
        if (passNueva.value === passConfirm.value) {
            indicador.className = 'recuperar__indicador recuperar__indicador--ok';
            textoMatch.textContent = '✅ Las contraseñas coinciden';
        } else {
            indicador.className = 'recuperar__indicador recuperar__indicador--error';
            textoMatch.textContent = '❌ Las contraseñas no coinciden';
        }
    });

    // Validar antes de enviar
    var form = document.getElementById('formNuevaPassword');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (passNueva.value !== passConfirm.value) {
                e.preventDefault();
                indicador.style.display = 'block';
                indicador.className = 'recuperar__indicador recuperar__indicador--error';
                textoMatch.textContent = '❌ Las contraseñas no coinciden';
            }
        });
    }
}