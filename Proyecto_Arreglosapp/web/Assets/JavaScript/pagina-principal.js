var buscador = document.getElementById('buscador');
var btnLimpiar = document.getElementById('btnLimpiar');
var sinResultados = document.getElementById('sinResultados');
var textoBuscado = document.getElementById('textoBuscado');
var cards = document.querySelectorAll('.tarjeta-arreglo');
var precioMax = document.getElementById('precioMax');
var labelPrecioMax = document.getElementById('labelPrecioMax');

function filtrar() {
    var termino = buscador.value.trim().toLowerCase();
    var pMax = parseFloat(precioMax.value);
    var visibles = 0;

    labelPrecioMax.textContent = '$' + pMax.toLocaleString('es-CO');

    cards.forEach(function (card) {
        var nombre = card.getAttribute('data-nombre') || '';
        var precio = parseFloat(card.getAttribute('data-price') || card.getAttribute('data-precio') || 0);
        var titulo = card.querySelector('.tarjeta-arreglo__nombre').textContent.toLowerCase();
        
        var coincideNombre = termino === '' || nombre.includes(termino) || titulo.includes(termino);
        var coincidePrecio = precio <= pMax;

        if (coincideNombre && coincidePrecio) {
            card.style.display = '';
            visibles++;
        } else {
            card.style.display = 'none';
        }
    });

    // Botón limpiar
    btnLimpiar.style.display = termino !== '' ? 'flex' : 'none';

    // Mensaje sin resultados
    if (visibles === 0 && (termino !== '' || pMax < 500000)) {
        sinResultados.style.display = 'block';
        textoBuscado.textContent = termino || 'el rango de precio';
    } else {
        sinResultados.style.display = 'none';
    }
}

buscador.addEventListener('input', filtrar);
precioMax.addEventListener('input', filtrar);

// Limpiar búsqueda
btnLimpiar.addEventListener('click', function () {
    buscador.value = '';
    filtrar();
    buscador.focus();
});