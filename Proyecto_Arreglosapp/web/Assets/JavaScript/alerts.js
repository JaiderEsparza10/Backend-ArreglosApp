/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
// Assets/js/alerts.js
document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const msg = params.get("msg");

    if (!msg) return;

    const mensajes = {
        exitoRegistro: { tipo: "success", texto: "¡Registro exitoso! Ya puedes iniciar sesión." },
        error:         { tipo: "error",   texto: "Ocurrió un error al registrar. Intenta de nuevo." },
        camposVacios:  { tipo: "warning", texto: "Por favor completa todos los campos obligatorios." },
        errorServidor: { tipo: "error",   texto: "Error interno del servidor. Intenta más tarde." },
        exitoLogin:    { tipo: "success", texto: "¡Bienvenido!" },
        credenciales:  { tipo: "error",   texto: "Correo o contraseña incorrectos." },
        sesionCerrada: { tipo: "info",    texto: "Sesión cerrada correctamente." }
    };

    const alerta = mensajes[msg];
    if (!alerta) return;

    mostrarAlerta(alerta.tipo, alerta.texto);

    // Limpiar el parámetro de la URL sin recargar la página
    const urlLimpia = window.location.pathname;
    window.history.replaceState(null, "", urlLimpia);
});

function mostrarAlerta(tipo, texto) {
    const colores = {
        success: { bg: "#d4edda", border: "#28a745", color: "#155724" },
        error:   { bg: "#f8d7da", border: "#dc3545", color: "#721c24" },
        warning: { bg: "#fff3cd", border: "#ffc107", color: "#856404" },
        info:    { bg: "#d1ecf1", border: "#17a2b8", color: "#0c5460" }
    };

    const estilo = colores[tipo];

    const div = document.createElement("div");
    div.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        padding: 15px 20px;
        border-radius: 8px;
        border-left: 5px solid ${estilo.border};
        background-color: ${estilo.bg};
        color: ${estilo.color};
        font-family: sans-serif;
        font-size: 14px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        max-width: 350px;
        cursor: pointer;
    `;
    div.textContent = texto;

    // Click para cerrar manualmente
    div.addEventListener("click", () => div.remove());

    document.body.appendChild(div);

    // Se cierra solo después de 4 segundos
    setTimeout(() => div.remove(), 4000);
}