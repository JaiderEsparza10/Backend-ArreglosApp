var pedidoIdACancelar = null;

// =====================
// TABS
// =====================
function cambiarTab(tab) {
    var panelActivos = document.getElementById('panelActivos');
    var panelHistorial = document.getElementById('panelHistorial');
    var tabActivos = document.getElementById('tabActivos');
    var tabHistorial = document.getElementById('tabHistorial');

    if (tab === 'activos') {
        panelActivos.classList.remove('pedidos__panel--oculto');
        panelHistorial.classList.add('pedidos__panel--oculto');
        tabActivos.classList.add('pedidos__tab--activo');
        tabHistorial.classList.remove('pedidos__tab--activo');
    } else {
        panelHistorial.classList.remove('pedidos__panel--oculto');
        panelActivos.classList.add('pedidos__panel--oculto');
        tabHistorial.classList.add('pedidos__tab--activo');
        tabActivos.classList.remove('pedidos__tab--activo');
    }
}

// =====================
// MODAL CANCELAR
// =====================
function prepararCancelacion(pedidoId) {
    pedidoIdACancelar = pedidoId;
    document.getElementById('modalCancelar').classList.add('modal--activo');
}

function cerrarModalCancelacion() {
    document.getElementById('modalCancelar').classList.remove('modal--activo');
    pedidoIdACancelar = null;
}

document.getElementById('btnConfirmarCancelacion').addEventListener('click', function () {
    if (!pedidoIdACancelar) return;

    var data = new URLSearchParams();
    data.append('accion', 'cancelar');
    data.append('pedidoId', pedidoIdACancelar);

    fetch('/Proyecto_Arreglosapp/PedidoServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: data.toString()
    })
        .then(function (res) { return res.json(); })
        .then(function (data) {
            if (data.success) {
                var card = document.getElementById('pedido-' + pedidoIdACancelar);
                cerrarModalCancelacion();
                if (card) card.remove();
                mostrarToast('✅ Pedido cancelado correctamente', true);

                // Mostrar vacío si no quedan cards
                var cards = document.querySelectorAll('#panelActivos .pedido-card');
                if (cards.length === 0) {
                    var panel = document.getElementById('panelActivos');
                    panel.innerHTML = '<div class="pedidos__vacio"><p class="pedidos__vacio-texto">No tienes pedidos activos.</p><a href="mis-arreglos.jsp" class="pedidos__vacio-btn">Ir a Mis Arreglos</a></div>';
                }
            } else {
                cerrarModalCancelacion();
                mostrarToast('❌ ' + data.message, false);
            }
        })
        .catch(function () {
            cerrarModalCancelacion();
            mostrarToast('❌ Error de conexión', false);
        });
});

// Cerrar modal al hacer clic en el fondo
document.getElementById('modalCancelar').addEventListener('click', function (e) {
    if (e.target === this) cerrarModalCancelacion();
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

// =====================
// TOAST AL CARGAR
// =====================
window.addEventListener('load', function () {
    var params = new URLSearchParams(window.location.search);
    if (params.get('citaAgendada') === '1') {
        mostrarToast('✅ Cita agendada correctamente', true);
        history.replaceState({}, document.title, window.location.pathname);
    }
});