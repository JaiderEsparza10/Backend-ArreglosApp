var buscador = document.getElementById('buscador');
var btnLimpiar = document.getElementById('btnLimpiar');
var sinResultados = document.getElementById('sinResultados');
var textoBuscado = document.getElementById('textoBuscado');
var cards = document.querySelectorAll('.tarjeta-arreglo');

buscador.addEventListener('input', function () {
    var termino = buscador.value.trim().toLowerCase();
    var visibles = 0;

    cards.forEach(function (card) {
        var nombre = card.getAttribute('data-nombre') || '';
        var titulo = card.querySelector('.tarjeta-arreglo__nombre').textContent.toLowerCase();
        var coincide = nombre.includes(termino) || titulo.includes(termino);

        if (termino === '' || coincide) {
            card.style.display = '';
            visibles++;
        } else {
            card.style.display = 'none';
        }
    });

    // Botón limpiar
    btnLimpiar.style.display = termino !== '' ? 'flex' : 'none';

    // Mensaje sin resultados
    if (visibles === 0 && termino !== '') {
        sinResultados.style.display = 'block';
        textoBuscado.textContent = buscador.value.trim();
    } else {
        sinResultados.style.display = 'none';
    }
});

// Limpiar búsqueda
btnLimpiar.addEventListener('click', function () {
    buscador.value = '';
    buscador.dispatchEvent(new Event('input'));
    buscador.focus();
});