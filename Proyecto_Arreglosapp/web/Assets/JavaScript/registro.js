/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// Assets/js/registro.js

function validarFormulario() {
    const nombre    = document.getElementById("txtNombre").value.trim();
    const email     = document.getElementById("txtEmail").value.trim();
    const password  = document.getElementById("txtPassword").value.trim();
    const confirmar = document.getElementById("txtConfirmarPassword").value.trim();
    const direccion = document.getElementById("txtDireccion").value.trim();
    const telefono  = document.getElementById("txtTelefono").value.trim();

    // 1. Campos obligatorios vacíos
    if (!nombre || !email || !password || !confirmar || !direccion) {
        mostrarAlerta("warning", "Por favor completa todos los campos obligatorios.");
        return false;
    }

    // 2. Validar nombre (solo letras y espacios, mínimo 3 caracteres)
    const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{3,}$/;
    if (!regexNombre.test(nombre)) {
        mostrarAlerta("warning", "El nombre solo debe contener letras y mínimo 3 caracteres.");
        return false;
    }

    // 3. Validar formato de email
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexEmail.test(email)) {
        mostrarAlerta("warning", "Ingresa un correo electrónico válido.");
        return false;
    }

    // 4. Validar contraseña (mínimo 8 caracteres, una mayúscula, un número)
    const regexPass = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (!regexPass.test(password)) {
        mostrarAlerta("warning", "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número.");
        return false;
    }

    // 5. Confirmar que las contraseñas coincidan
    if (password !== confirmar) {
        mostrarAlerta("error", "Las contraseñas no coinciden.");
        return false;
    }

    // 6. Validar teléfono (solo si lo escribió, entre 7 y 15 dígitos)
    if (telefono !== "") {
        const regexTel = /^\d{7,15}$/;
        if (!regexTel.test(telefono)) {
            mostrarAlerta("warning", "El teléfono debe contener entre 7 y 15 dígitos numéricos.");
            return false;
        }
    }

    // Todo OK, se envía el formulario
    return true;
}