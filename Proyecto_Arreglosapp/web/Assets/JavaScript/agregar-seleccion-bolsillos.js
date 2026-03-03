function mostrarToast(mensaje, exito) {
    var toast = document.getElementById('toast');
    toast.textContent = mensaje;
    toast.className = 'toast ' + (exito ? 'toast--exito' : 'toast--error');
    toast.classList.add('toast--visible');
    setTimeout(function () {
        toast.classList.remove('toast--visible');
    }, 3000);
}

document.getElementById('btnAgregarSeleccion').addEventListener('click', function (e) {
    e.preventDefault();
    var data = new URLSearchParams();
    data.append('accion', 'agregar');
    data.append('arregloId', '2');
    data.append('categoria', 'Costuras Generales');
    data.append('nombreCategoria', 'Arreglo de Bolsillos y Remiendos');
    data.append('precio', '15000');
    data.append('imagenUrl', 'Assets/image/image-arreglo-bolsillos.jpg');

    fetch('/Proyecto_Arreglosapp/FavoritoServlet', {
        method: 'POST',
        body: data,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
        .then(function (response) {
            if (response.status === 401) {
                mostrarToast('⚠️ Debes iniciar sesión', false);
                setTimeout(function () { window.location.href = '/Proyecto_Arreglosapp/index.jsp'; }, 2000);
                throw new Error('No autorizado');
            }
            return response.json();
        })
        .then(function (data) {
            if (data.success) {
                mostrarToast('✅ ' + data.message, true);
                setTimeout(function () { window.location.href = 'mi-seleccion.jsp'; }, 2000);
            } else {
                mostrarToast('❌ ' + data.message, false);
            }
        })
        .catch(function (error) {
            if (error.message !== 'No autorizado') {
                mostrarToast('❌ Error al comunicarse con el servidor', false);
            }
        });
});