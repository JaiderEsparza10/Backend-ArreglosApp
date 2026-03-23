/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestión de la interfaz de pedidos del cliente, incluyendo cambio de pestañas y cancelación de pedidos.
 * REQUISITOS FUNCIONALES: RF6, RF8.
 */
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
// TOAST DINÁMICO (Meta 2)
// =====================
function mostrarToast(mensaje, tipo) {
    var toast = document.getElementById('toast');
    if (!toast) return;

    // Determinar tipo si no se proporciona (inferencia por contenido)
    if (!tipo) {
        var msg = mensaje.toLowerCase();
        if (msg.includes('éxito') || msg.includes('correctamente') || msg.includes('confirmado') || msg.includes('completado')) {
            tipo = 'exito';
        } else if (msg.includes('error') || msg.includes('cancelado') || msg.includes('eliminado')) {
            tipo = 'error';
        } else if (msg.includes('pendiente') || msg.includes('en proceso')) {
            tipo = 'advertencia';
        } else {
            tipo = 'exito'; // Por defecto
        }
    }

    toast.textContent = mensaje;
    // Limpiar clases previas y aplicar la nueva
    toast.className = 'toast toast--' + tipo;
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
    if (params.get('estadoActualizado') === '1') {
        mostrarToast('✅ Estado de la cita actualizado con éxito', true);
        history.replaceState({}, document.title, window.location.pathname);
    }
});