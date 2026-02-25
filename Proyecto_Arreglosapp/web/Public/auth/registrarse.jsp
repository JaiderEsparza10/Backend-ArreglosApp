<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String msgError = request.getParameter("msg");
    String mensajeServidor = "";
    if (msgError != null) {
        switch (msgError) {
            case "camposVacios":   mensajeServidor = "Por favor completa todos los campos."; break;
            case "passCorta":      mensajeServidor = "La contraseña debe tener al menos 6 caracteres."; break;
            case "emailDuplicado": mensajeServidor = "Este correo ya está registrado."; break;
            case "error":          mensajeServidor = "Ocurrió un error, intenta de nuevo."; break;
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Registrarse</title>
</head>
<body>
    <header class="seccion-logo">
        <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
        <h1 class="seccion-logo__nombre">Arreglos App</h1>
    </header>
    <main class="contenedor-registro">
        <h1 class="contenedor-registro__titulo">REGISTRARSE</h1>
        <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">
        <form class="contenedor-login" action="${pageContext.request.contextPath}/UsuarioServlet" method="post">

            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/user.png" alt="icono de usuario">
                    <label class="campo__label" for="txtNombre">Nombre Completo</label>
                </div>
                <input type="text" name="txtNombre" id="txtNombre" required>
                <div class="linea-separadora"></div>
            </div>

            <div class="contenedor-login__campo">
                <div class="campo__contrasena">
                    <img class="campo__icono" src="../../Assets/icons/correo-electronico.png" alt="icono de correo">
                    <label class="campo__label" for="txtEmail">Correo Electronico</label>
                </div>
                <input type="email" id="txtEmail" name="txtEmail" required>
                <div class="linea-separadora"></div>
            </div>

            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
                    <label class="campo__label" for="txtPassword">Contraseña</label>
                </div>
                <input type="password" id="txtPassword" name="txtPassword" required>
                <div class="linea-separadora"></div>
            </div>

            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/padlock.png" alt="icono de candado">
                    <label class="campo__label" for="txtConfirmarPassword">Confirmar Contraseña</label>
                </div>
                <input type="password" id="txtConfirmarPassword" name="txtConfirmarPassword" required>
                <div class="linea-separadora"></div>
            </div>

            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/pasador-de-ubicacion.png" alt="icono de ubicacion">
                    <label class="campo__label" for="txtDireccion">Direccion</label>
                </div>
                <input type="text" id="txtDireccion" name="txtDireccion" required>
                <div class="linea-separadora"></div>
            </div>

            <div class="contenedor-login__campo">
                <div class="campo__usuario">
                    <img class="campo__icono" src="../../Assets/icons/llamada-telefonica.png" alt="icono de telefono">
                    <label class="campo__label" for="txtTelefono">Telefono</label>
                </div>
                <input type="text" id="txtTelefono" name="txtTelefono">
                <div class="linea-separadora"></div>
            </div>

            <%-- Mensaje de error del servidor o del frontend --%>
            <div id="errorMsg" style="
                display: <%= mensajeServidor.isEmpty() ? "none" : "block" %>;
                color: #cc0000; background: #ffe0e0;
                border: 1px solid #cc0000; padding: 10px;
                border-radius: 6px; margin-bottom: 10px; font-size: 14px;">
                <%= mensajeServidor %>
            </div>

            <button type="submit" class="contenedor-login__iniciar-sesion" onclick="return validarFormulario()">REGISTRARSE</button>

            <div class="contenedor-login__registrarse">
                <h3 class="registrarse__pregunta">¿Ya tienes cuenta?</h3>
                <a class="registrarse__formulario" href="../../index.jsp">Iniciar Sesion</a>
            </div>
        </form>
    </main>

    <script>
        function validarFormulario() {
            const nombre   = document.getElementById("txtNombre").value.trim();
            const email    = document.getElementById("txtEmail").value.trim();
            const pass1    = document.getElementById("txtPassword").value;
            const pass2    = document.getElementById("txtConfirmarPassword").value;
            const telefono = document.getElementById("txtTelefono").value.trim();
            const errorDiv = document.getElementById("errorMsg");

            errorDiv.style.display = "none";
            errorDiv.innerText = "";

            // 1. Campos obligatorios
            if (nombre === "" || email === "" || pass1 === "") {
                mostrarError("Por favor completa todos los campos obligatorios.");
                return false;
            }

            // 2. Formato de email
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                mostrarError("El correo electrónico no tiene un formato válido.");
                return false;
            }

            // 3. Mínimo 6 caracteres en contraseña
            if (pass1.length < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres.");
                return false;
            }

            // 4. Contraseñas coinciden
            if (pass1 !== pass2) {
                mostrarError("Las contraseñas no coinciden.");
                return false;
            }

            // 5. Teléfono solo números si fue ingresado
            if (telefono !== "") {
                const telRegex = /^[0-9]{7,15}$/;
                if (!telRegex.test(telefono)) {
                    mostrarError("El teléfono solo debe contener números (7 a 15 dígitos).");
                    return false;
                }
            }

            return true;
        }

        function mostrarError(mensaje) {
            const errorDiv = document.getElementById("errorMsg");
            errorDiv.innerText = mensaje;
            errorDiv.style.display = "block";
        }
    </script>
</body>
</html>