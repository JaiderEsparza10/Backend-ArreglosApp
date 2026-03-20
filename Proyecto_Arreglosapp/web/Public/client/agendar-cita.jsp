<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@ page import="model.Usuario" %>
        <% HttpSession sesion=request.getSession(false); Usuario usuario=null; if (sesion !=null) { usuario=(Usuario)
            sesion.getAttribute("usuario"); } if (usuario==null) { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String errorMsg=(String) sesion.getAttribute("errorCita"); if (errorMsg !=null) { sesion.removeAttribute("errorCita"); } String personalizacionId=request.getParameter("personalizacionId"); if (personalizacionId==null || personalizacionId.trim().isEmpty()) { response.sendRedirect("/Proyecto_Arreglosapp/Public/client/mis-arreglos.jsp"); return; } %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
                <title>Agendar Cita</title>
            </head>

            <body class="grid-principal"> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png"
                        alt="logo de la aplicación"> <h1 class="seccion-encabezado__nombre">Arreglos App</h1>
                </header>

                <div id="toast" class="toast"></div> <main class="contenido-cita"> <div class="contenido-cita__encabezado"> <a href="javascript:history.back()" class="encabezado__btn-volver"> <img src="../../Assets/icons/flecha-izquierda__blanca.png" alt="volver"
                                class="btn-volver__icono">
                        </a>
                        <h1 class="enlace-volver__titulo">Agendar Cita</h1>
                    </div>

                    <% if (errorMsg != null) { %>
                        <input type="hidden" id="serverErrorMsg" value="<%= errorMsg %>">
                    <% } %>

                            <form class="cita__formulario" action="/Proyecto_Arreglosapp/CitaServlet" method="post"
                                id="formCita"> <input type="hidden" name="accion" value="agendar"> <input type="hidden" name="personalizacionId" value="<%= personalizacionId %>"> <input type="hidden" name="fechaCita" id="fechaCitaInput">

                                <!-- CALENDARIO DINÁMICO -->
                                <div class="cita__seccion"> <h2 class="cita__titulo-seccion">Selecciona una Fecha</h2> <div class="cita__calendario" id="calendario"> <div class="calendario__cabecera"> <button type="button" class="calendario__btn-nav"
                                                id="btnMesAnterior">&#8249;</button> <span class="calendario__mes-actual" id="mesActual"></span> <button type="button" class="calendario__btn-nav"
                                                id="btnMesSiguiente">&#8250;</button>
                                        </div>
                                        <div class="calendario__semana-header">
                                            <span>Lun</span><span>Mar</span><span>Mié</span>
                                            <span>Jue</span><span>Vie</span><span>Sáb</span><span>Dom</span>
                                        </div>
                                        <div class="calendario__grid" id="calendarioGrid"></div>
                                    </div>
                                    <p class="cita__fecha-seleccionada" id="textoFechaSeleccionada">
                                        Ninguna fecha seleccionada
                                    </p>
                                </div>

                                <!-- HORA -->
                                <div class="cita__seccion"> <h2 class="cita__titulo-seccion">Hora de la Cita</h2> <p class="cita__subtitulo">Disponible de lunes a viernes — 2:00 pm a 10:00 pm</p> <div class="cita__horas-grid" id="horasGrid"></div> <input type="hidden" name="horaCita" id="horaCitaInput"> <p class="cita__hora-seleccionada" id="textoHoraSeleccionada">
                                        Ninguna hora seleccionada
                                    </p>
                                </div>

                                <!-- MOTIVO DE LA CITA -->
                                <div class="cita__seccion"> <h2 class="cita__titulo-seccion">Motivo de la Cita</h2> <div class="cita__input-icono"> <select name="motivoCita" class="cita__input" required style="padding-left:10px;"> <option value="entrega_prenda">Entrega de prenda para arreglo</option> <option value="recogida_prenda">Recogida de prenda terminada</option> <option value="toma_medidas">Toma de medidas</option> <option value="otro">Otro motivo</option>
                                        </select>
                                    </div>
                                </div>

                                <!-- LUGAR DE ENTREGA -->
                                <div class="cita__seccion"> <h2 class="cita__titulo-seccion">Lugar de Entrega / Recogida</h2> <div class="cita__input-icono"> <img src="../../Assets/icons/entrega-rapida.png" alt="entrega"
                                            class="cita__icono-input"> <input type="text" name="direccionEntrega" class="cita__input"
                                            placeholder="Ej: Calle 10 # 5-20, Barrio Centro" required>
                                    </div>
                                </div>

                                <!-- NOTAS ADICIONALES -->
                                <div class="cita__seccion"> <h2 class="cita__titulo-seccion"> Notas Adicionales <span class="cita__opcional">(opcional)</span>
                                    </h2>
                                    <textarea name="notas" class="cita__textarea"
                                        placeholder="Alguna indicación especial para el sastre..."
                                        maxlength="300"></textarea>
                                </div>

                                <!-- BOTÓN -->
                                <div class="cita__seccion-boton"> <button type="submit" class="cita__btn-confirmar" id="btnConfirmarCita">
                                        Confirmar Cita
                                    </button>
                                </div>
                            </form>
                </main>

                <footer class="navbar"> <nav class="navbar-inferior"> <a href="pagina-principal.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/casa-blanca.png" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Inicio</span>
                        </a>
                        <a href="mi-seleccion.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/lista-de-deseos-transparente.png"
                                class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Mi selección</span>
                        </a>
                        <a href="mis-arreglos.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/cortar-con-tijeras-transparente.png"
                                class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Mis Arreglos</span>
                        </a>
                        <a href="mis-pedidos.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/caja-transparente.png" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Pedidos</span>
                        </a>
                        <a href="mi-perfil.jsp" class="navbar-inferior__item"> <img src="../../Assets/icons/usuario-transparente.png" class="navbar-inferior__icono"> <span class="navbar-inferior__texto">Perfil</span>
                        </a>
                <script src="../../Assets/JavaScript/agendar-cita.js?v=4"></script>
            </body>

            </html>