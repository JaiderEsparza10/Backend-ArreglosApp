<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <% HttpSession sesion=request.getSession(); String errorMsg=(String) sesion.getAttribute("errorRecuperar");
        sesion.removeAttribute("errorRecuperar"); String paso=request.getParameter("paso"); boolean esPaso2="2"
        .equals(paso); String emailGuardado=(String) sesion.getAttribute("emailRecuperar"); if (esPaso2 &&
        emailGuardado==null) { response.sendRedirect("/Proyecto_Arreglosapp/Public/client/recuperar-contrasena.jsp");
        return; } %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="../../Assets/estilos.css">
            <title>Recuperar Contraseña</title>
        </head>

        <body>

            <header class="seccion-logo">
                <img class="seccion-logo__logo" src="../../Assets/image/logo-app.png" alt="logo de la aplicación">
                <h1 class="seccion-logo__nombre">Arreglos App</h1>
            </header>

            <main class="contenedor-registro">
                <h1 class="contenedor-registro__titulo">
                    <%= esPaso2 ? "NUEVA CONTRASEÑA" : "RECUPERAR CONTRASEÑA" %>
                </h1>
                <img class="contenedor-login__imagen" src="../../Assets/icons/user-inicio.png" alt="logo de usuario">

                <% if (errorMsg !=null) { %>
                    <div class="recuperar__alerta-login">❌ <%= errorMsg %>
                    </div>
                    <% } %>

                        <!-- PASO 1 — VERIFICAR EMAIL -->
                        <% if (!esPaso2) { %>
                            <form class="contenedor-login" action="/Proyecto_Arreglosapp/RecuperarPasswordServlet"
                                method="post">
                                <input type="hidden" name="accion" value="verificarEmail">

                                <div class="contenedor-login__campo">
                                    <div class="campo__usuario">
                                        <img class="campo__icono" src="../../Assets/icons/user.png" alt="icono usuario">
                                        <label class="campo__label">Correo Electrónico</label>
                                    </div>
                                    <input type="email" name="email" placeholder="tucorreo@ejemplo.com" required
                                        autofocus>
                                    <div class="linea-separadora"></div>
                                </div>

                                <p class="recuperar__subtitulo-login">
                                    Ingresa tu correo y te ayudamos a recuperar el acceso
                                </p>

                                <button type="submit" class="contenedor-login__iniciar-sesion">
                                    VERIFICAR CORREO
                                </button>

                                <div class="contenedor-login__registrarse">
                                    <a class="registrarse__formulario" href="/Proyecto_Arreglosapp/index.jsp">
                                        ← Volver al inicio de sesión
                                    </a>
                                </div>
                            </form>

                            <!-- PASO 2 — NUEVA CONTRASEÑA -->
                            <% } else { %>
                                <form class="contenedor-login" action="/Proyecto_Arreglosapp/RecuperarPasswordServlet"
                                    method="post" id="formNuevaPassword">
                                    <input type="hidden" name="accion" value="cambiarPassword">

                                    <p class="recuperar__email-login">✉️ <%= emailGuardado %>
                                    </p>

                                    <div class="contenedor-login__campo">
                                        <div class="campo__contrasena">
                                            <img class="campo__icono" src="../../Assets/icons/padlock.png"
                                                alt="icono candado">
                                            <label class="campo__label">Nueva Contraseña</label>
                                        </div>
                                        <div class="recuperar__input-pass">
                                            <input type="password" name="passwordNueva" id="passNueva"
                                                placeholder="Mínimo 6 caracteres" required minlength="6" autofocus>
                                            <button type="button" class="recuperar__ojo-login"
                                                onclick="togglePass('passNueva','ojoNueva')">
                                                <span id="ojoNueva">👁</span>
                                            </button>
                                        </div>
                                        <div class="linea-separadora"></div>
                                    </div>

                                    <div class="contenedor-login__campo">
                                        <div class="campo__contrasena">
                                            <img class="campo__icono" src="../../Assets/icons/padlock.png"
                                                alt="icono candado">
                                            <label class="campo__label">Confirmar Contraseña</label>
                                        </div>
                                        <div class="recuperar__input-pass">
                                            <input type="password" name="passwordConfirmar" id="passConfirm"
                                                placeholder="Repite la contraseña" required minlength="6">
                                            <button type="button" class="recuperar__ojo-login"
                                                onclick="togglePass('passConfirm','ojoConfirm')">
                                                <span id="ojoConfirm">👁</span>
                                            </button>
                                        </div>
                                        <div class="linea-separadora"></div>
                                    </div>

                                    <div class="recuperar__indicador-login" id="indicadorMatch" style="display:none;">
                                        <span id="textoMatch"></span>
                                    </div>

                                    <button type="submit" class="contenedor-login__iniciar-sesion" id="btnCambiar">
                                        CAMBIAR CONTRASEÑA
                                    </button>

                                    <div class="contenedor-login__registrarse">
                                        <a class="registrarse__formulario" href="/Proyecto_Arreglosapp/index.jsp">
                                            ← Volver al inicio de sesión
                                        </a>
                                    </div>
                                </form>
                                <% } %>
            </main>

            <script src="../../Assets/JavaScript/recuperar-contrasena.js"></script>
        </body>

        </html>