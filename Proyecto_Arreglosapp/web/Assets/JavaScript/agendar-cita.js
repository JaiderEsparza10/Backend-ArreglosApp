/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestión interactiva del calendario y selección de horarios para agendar citas.
 */
// =====================
// ESTADO
// =====================
var fechaSeleccionada = null;
var horaSeleccionada = null;
var horasOcupadasEnBD = [];
var mesActual = new Date().getMonth();
var anioActual = new Date().getFullYear();

var meses = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
];

var horasDisponibles = [
    '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00', '17:30',
    '18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00', '21:30', '22:00'
];

// =====================
// CALENDARIO
// =====================
function renderCalendario() {
    var grid = document.getElementById('calendarioGrid');
    var labelMes = document.getElementById('mesActual');
    var hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    labelMes.textContent = meses[mesActual] + ' ' + anioActual;
    grid.innerHTML = '';

    // Primer día del mes (ajustar: lunes=0)
    var primerDia = new Date(anioActual, mesActual, 1).getDay();
    primerDia = (primerDia === 0) ? 6 : primerDia - 1;

    var diasEnMes = new Date(anioActual, mesActual + 1, 0).getDate();

    // Celdas vacías antes del día 1
    for (var i = 0; i < primerDia; i++) {
        var vacio = document.createElement('div');
        vacio.className = 'calendario__dia calendario__dia--vacio';
        grid.appendChild(vacio);
    }

    // Días del mes
    for (var d = 1; d <= diasEnMes; d++) {
        var fecha = new Date(anioActual, mesActual, d);
        fecha.setHours(0, 0, 0, 0);
        var diaSemana = fecha.getDay(); // 0=dom, 6=sab
        var esPasado = fecha < hoy;
        var esFinde = diaSemana === 0 || diaSemana === 6;
        var esHoy = fecha.getTime() === hoy.getTime();

        var celda = document.createElement('div');
        celda.textContent = d;
        celda.className = 'calendario__dia';

        if (esPasado || esFinde) {
            celda.classList.add('calendario__dia--deshabilitado');
        } else {
            if (esHoy) celda.classList.add('calendario__dia--hoy');

            var fechaStr = anioActual + '-'
                + String(mesActual + 1).padStart(2, '0') + '-'
                + String(d).padStart(2, '0');

            if (fechaSeleccionada === fechaStr) {
                celda.classList.add('calendario__dia--seleccionado');
            }

            (function (fs, el) {
                el.addEventListener('click', function () {
                    seleccionarFecha(fs);
                });
            })(fechaStr, celda);
        }

        grid.appendChild(celda);
    }
}

function seleccionarFecha(fechaStr) {
    fechaSeleccionada = fechaStr;
    document.getElementById('fechaCitaInput').value = fechaStr;

    var partes = fechaStr.split('-');
    var texto = partes[2] + ' de ' + meses[parseInt(partes[1]) - 1] + ' de ' + partes[0];
    document.getElementById('textoFechaSeleccionada').textContent = '📅 ' + texto;
    document.getElementById('textoFechaSeleccionada').classList.add('cita__fecha-seleccionada--activa');

    renderCalendario();
    
    // Ajax request to get already reserved slots for this specific date
    var data = new URLSearchParams();
    data.append('accion', 'obtenerHorasOcupadas');
    data.append('fechaCita', fechaStr);

    fetch('/Proyecto_Arreglosapp/CitaServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: data.toString()
    })
    .then(function(response) {
        if (!response.ok) throw new Error('Error de conexión');
        return response.json();
    })
    .then(function(horasBloqueadas) {
        horasOcupadasEnBD = horasBloqueadas;
        renderHoras();
    })
    .catch(function(error) {
        horasOcupadasEnBD = [];
        renderHoras();
    });
}

document.getElementById('btnMesAnterior').addEventListener('click', function () {
    var hoy = new Date();
    if (anioActual > hoy.getFullYear() || (anioActual === hoy.getFullYear() && mesActual > hoy.getMonth())) {
        mesActual--;
        if (mesActual < 0) { mesActual = 11; anioActual--; }
        renderCalendario();
    }
});

document.getElementById('btnMesSiguiente').addEventListener('click', function () {
    mesActual++;
    if (mesActual > 11) { mesActual = 0; anioActual++; }
    renderCalendario();
});

// =====================
// HORAS
// =====================
function renderHoras() {
    var grid = document.getElementById('horasGrid');
    grid.innerHTML = '';

    // Verificar si hay fecha seleccionada
    if (!fechaSeleccionada) {
        grid.innerHTML = '<p style="color:#888;text-align:center;padding:20px;">Primero selecciona una fecha</p>';
        return;
    }

    // Verificar si es fin de semana
    var fecha = new Date(fechaSeleccionada + 'T00:00:00');
    var diaSemana = fecha.getDay(); // 0=dom, 6=sab
    if (diaSemana === 0 || diaSemana === 6) {
        grid.innerHTML = '<p style="color:#dc3545;text-align:center;padding:20px;font-weight:500;"> Solo atendemos de lunes a viernes</p>';
        return;
    }

    // Obtener hora actual para comparación
    var ahora = new Date();
    var esHoy = fecha.toDateString() === ahora.toDateString();

    horasDisponibles.forEach(function (hora) {
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = formatHora(hora);
        btn.className = 'cita__hora-btn';

        // Verificar si la hora ya pasó (solo si es hoy)
        var horaPasada = false;
        if (esHoy) {
            var partesHora = hora.split(':');
            var horaSlot = parseInt(partesHora[0]);
            var minSlot = parseInt(partesHora[1]);
            var horaActual = ahora.getHours();
            var minActual = ahora.getMinutes();

            // Considerar pasada si la hora actual es mayor o igual al slot
            horaPasada = (horaActual > horaSlot) ||
                        (horaActual === horaSlot && minActual >= minSlot);
        }

        if (horaPasada) {
            btn.classList.add('cita__hora-btn--deshabilitado');
            btn.disabled = true;
            btn.title = 'Esta hora ya no está disponible';
        } else if (horasOcupadasEnBD.includes(hora)) {
            btn.classList.add('cita__hora-btn--deshabilitado');
            btn.disabled = true;
            btn.title = 'Hora ocupada por otro usuario';
        } else {
            if (horaSeleccionada === hora) btn.classList.add('cita__hora-btn--seleccionado');

            btn.addEventListener('click', function () {
                horaSeleccionada = hora;
                document.getElementById('horaCitaInput').value = hora;
                document.getElementById('textoHoraSeleccionada').textContent = ' ' + formatHora(hora);
                document.getElementById('textoHoraSeleccionada').classList.add('cita__hora-seleccionada--activa');
                renderHoras();
            });
        }

        grid.appendChild(btn);
    });
}

function formatHora(hora) {
    var partes = hora.split(':');
    var h = parseInt(partes[0]);
    var m = partes[1];
    var sufijo = h >= 12 ? 'pm' : 'am';
    var h12 = h > 12 ? h - 12 : h;
    return h12 + ':' + m + ' ' + sufijo;
}

// =====================
// VALIDACIÓN
// =====================
document.getElementById('formCita').addEventListener('submit', function (e) {
    if (!fechaSeleccionada) {
        e.preventDefault();
        mostrarToast('❌ Debes seleccionar una fecha', false);
        return;
    }
    if (!horaSeleccionada) {
        e.preventDefault();
        mostrarToast('❌ Debes seleccionar una hora', false);
        return;
    }

    var btn = document.getElementById('btnConfirmarCita');
    btn.disabled = true;
    btn.textContent = 'Agendando...';
});

// =====================
// TOAST DINÁMICO (Meta 2)
// =====================
function mostrarToast(mensaje, tipo) {
    var toast = document.getElementById('toast');
    if (!toast) return;

    if (!tipo) {
        var msg = mensaje.toLowerCase();
        if (msg.includes('✅') || msg.includes('éxito') || msg.includes('agendada')) {
            tipo = 'exito';
        } else if (msg.includes('❌') || msg.includes('error') || msg.includes('reservado')) {
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
// INICIALIZAR
// =====================
renderCalendario();
renderHoras();

window.addEventListener('load', function() {
    var serverErrorInput = document.getElementById('serverErrorMsg');
    if (serverErrorInput && serverErrorInput.value) {
        mostrarToast('❌ ' + serverErrorInput.value, 'error');
    }
});