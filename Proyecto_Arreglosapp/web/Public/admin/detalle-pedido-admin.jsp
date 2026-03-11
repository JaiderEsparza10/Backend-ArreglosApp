<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@ page import="dao.AdminDAO" %> <%@ page import="model.Usuario" %> <%@ page import="java.util.Map" %> <%@ page import="java.text.SimpleDateFormat" %> <% Usuario admin=(Usuario) session.getAttribute("usuario"); if (admin==null || admin.getRolId() !=1) { response.sendRedirect("/Proyecto_Arreglosapp/index.jsp"); return; } String pedidoIdStr=request.getParameter("pedidoId"); if (pedidoIdStr==null) { response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp"); return;
                        } int pedidoId=Integer.parseInt(pedidoIdStr); AdminDAO adminDAO=new AdminDAO(); Map<String,
                        Object> detalle = null;
                        try { detalle = adminDAO.obtenerDetallePedido(pedidoId); } catch (Exception e) {
                        e.printStackTrace(); }
                        if (detalle == null) {
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-dashboard.jsp"); return;
                        }
                        String estado = (String) detalle.get("estado"); String citaEstado = (String) detalle.get("citaEstado"); String citaMotivo = (String) detalle.get("citaMotivo"); String cliente = (String) detalle.get("cliente"); String email = (String) detalle.get("email"); String direccion = (String) detalle.get("direccion"); String citaNotas = (String) detalle.get("citaNotas"); double total = detalle.get("total") instanceof Number ? ((Number) detalle.get("total")).doubleValue() : 0.0; java.sql.Timestamp citaFecha = (java.sql.Timestamp) detalle.get("citaFecha"); SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); String citaStr = citaFecha != null ? sdf.format(citaFecha) : "Sin cita agendada"; String estadoClass = "estado-badge--pendiente"; String estadoLabel = "Pendiente de Revision"; if ("confirmado".equals(estado)) { estadoClass = "pedido__estado--confirmado"; estadoLabel = "Pendiente de Inicio"; } else if ("en_proceso".equals(estado)) { estadoClass = "estado-badge--en-taller"; estadoLabel = "En Taller"; } else if ("terminado".equals(estado)) { estadoClass = "estado-badge--entregado"; estadoLabel = "Terminado"; } else if ("cancelado".equals(estado)) { estadoClass = "estado-badge--cancelado"; estadoLabel = "Cancelado"; } String citaEstadoClass = "estado-badge--pendiente"; String citaEstadoLabel = "Programada"; if ("confirmada".equals(citaEstado)) { citaEstadoClass = "pedido__estado--confirmado"; citaEstadoLabel = "Confirmada"; } else if ("completada".equals(citaEstado)) { citaEstadoClass = "estado-badge--entregado"; citaEstadoLabel = "Completada"; } else if ("cancelada".equals(citaEstado)) { citaEstadoClass = "estado-badge--cancelado"; citaEstadoLabel = "Cancelada"; } String motivoLabel = "Consulta"; if ("entrega_prenda".equals(citaMotivo)) motivoLabel = "Entrega de prenda"; else if ("recogida_prenda".equals(citaMotivo)) motivoLabel = "Recogida de prenda"; else if ("toma_medidas".equals(citaMotivo)) motivoLabel = "Toma de medidas"; boolean esFinal = "terminado".equals(estado) || "cancelado".equals(estado);
                        %>
                        <!DOCTYPE html>
                        <html lang="es">

                        <head>
                            <meta charset="UTF-8"> <meta name="viewport" content="width=device-width, initial-scale=1.0"> <link rel="stylesheet" href="../../Assets/estilos.css">
                            <title>Detalle Pedido</title>
                        </head>

                        <body class="grid-principal"> <a href="#contenido-principal" class="skip-link">Saltar al contenido</a> <div id="toast" class="toast"></div> <header class="seccion-encabezado"> <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo"> <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
                            </header>
                            <main class="detalle-pedido" id="contenido-principal" role="main"> <a href="administrador-dashboard.jsp" class="detalle-pedido__volver">← Volver</a> <h2 style="font-size:16px;font-weight:600;color:#3d0072;">Pedido #P-<%= String.format("%05d", pedidoId) %>
                                </h2>

                                <!-- INFO CLIENTE -->
                                <div class="info-seccion"> <h3 class="info-seccion__titulo">Informacion del Cliente</h3> <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Nombre:</span><span class="info-seccion__valor">
                                            <%= cliente %>
                                        </span></div>
                                    <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Email:</span><span class="info-seccion__valor">
                                            <%= email %>
                                        </span></div>
                                    <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Direccion:</span><span class="info-seccion__valor"> <%= direccion !=null ? direccion : "No especificada" %>
                                        </span></div>
                                </div>

                                <!-- INFO CITA -->
                                <div class="info-seccion"> <h3 class="info-seccion__titulo">Informacion de la Cita</h3> <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Motivo:</span><span class="info-seccion__valor">
                                            <%= motivoLabel %>
                                        </span></div>
                                    <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Fecha y hora:</span><span class="info-seccion__valor">
                                            <%= citaStr %>
                                        </span></div>
                                    <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Estado cita:</span><span class="estado-badge <%= citaEstadoClass %>"
                                            style="font-size:12px;padding:3px 10px;">
                                            <%= citaEstadoLabel %>
                                        </span></div>
                                    <% if (citaNotas !=null && !citaNotas.isEmpty()) { %>
                                        <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Notas:</span><span class="info-seccion__valor">
                                                <%= citaNotas %>
                                            </span></div>
                                        <% } %>
                                </div>

                                <!-- TOTAL -->
                                <div class="info-seccion"> <h3 class="info-seccion__titulo">Total del Servicio</h3> <div class="info-seccion__campo info-seccion__campo--inline"><span class="info-seccion__label">Total:</span><span class="info-seccion__valor"
                                            style="font-weight:700;font-size:1.1em;">$<%= String.format("%,.0f", total)
                                                %></span></div>
                                </div>

                                <!-- ESTADO DEL PEDIDO -->
                                <div class="estado-pedido"> <h3 class="estado-pedido__titulo">Estado del Pedido</h3> <div class="estado-pedido__actual"> <span class="estado-pedido__actual-label">Estado actual:</span> <span class="estado-badge <%= estadoClass %>">
                                            <%= estadoLabel %>
                                        </span>
                                    </div>
                                    <p style="font-size:12px;color:#888;margin-top:8px;">El estado del pedido se
                                        actualiza automaticamente al gestionar la cita desde el Dashboard.</p>

                                    <% if (!esFinal && citaEstado !=null) { %>
                                        <div
                                            style="margin-top:20px;padding:15px;background:#f5f0ff;border-radius:10px;border-left:4px solid #673ab7;"> <p style="font-size:13px;color:#673ab7;font-weight:600;margin-bottom:12px;">
                                                Gestionar estado de la cita:</p>
                                            <div style="display:flex;gap:10px;flex-wrap:wrap;"> <% if ("programada".equals(citaEstado)) { %> <form action="/Proyecto_Arreglosapp/AdminServlet" method="post"
                                                        style="display:inline;"> <input type="hidden" name="accion" value="cambiarEstadoCita"> <input type="hidden" name="citaId" value="<%= detalle.get("citaId") !=null ? detalle.get("citaId") : "" %>"> <input type="hidden" name="nuevoEstado" value="confirmada"> <button type="submit" class="perfil__btn-guardar"
                                                            style="padding:8px 16px;width:auto;background:#673ab7;">✓
                                                            Confirmar cita</button>
                                                    </form>
                                                    <form action="/Proyecto_Arreglosapp/AdminServlet" method="post"
                                                        style="display:inline;"> <input type="hidden" name="accion" value="cambiarEstadoCita"> <input type="hidden" name="citaId" value="<%= detalle.get("citaId") !=null ? detalle.get("citaId") : "" %>"> <input type="hidden" name="nuevoEstado" value="cancelada"> <button type="submit"
                                                            style="padding:8px 16px;border:none;border-radius:8px;background:#f44336;color:white;font-weight:600;cursor:pointer;">✕
                                                            Cancelar cita</button>
                                                    </form>
                                                    <% } else if ("confirmada".equals(citaEstado)) { %> <form action="/Proyecto_Arreglosapp/AdminServlet" method="post"
                                                            style="display:inline;"> <input type="hidden" name="accion"
                                                                value="cambiarEstadoCita"> <input type="hidden" name="citaId" value="<%= detalle.get("citaId") != null ? detalle.get("citaId") : "" %>"> <input type="hidden" name="nuevoEstado" value="completada"> <button type="submit" class="perfil__btn-guardar"
                                                                style="padding:8px 16px;width:auto;background:#4caf50;">✅
                                                                Completar cita</button>
                                                        </form>
                                                        <form action="/Proyecto_Arreglosapp/AdminServlet" method="post"
                                                            style="display:inline;"> <input type="hidden" name="accion"
                                                                value="cambiarEstadoCita"> <input type="hidden" name="citaId" value="<%= detalle.get("citaId") != null ? detalle.get("citaId") : "" %>"> <input type="hidden" name="nuevoEstado" value="cancelada"> <button type="submit"
                                                                style="padding:8px 16px;border:none;border-radius:8px;background:#f44336;color:white;font-weight:600;cursor:pointer;">✕
                                                                Cancelar cita</button>
                                                        </form>
                                                        <% } %>
                                            </div>
                                        </div>
                                        <% } %>
                                </div>
                            </main>

                            <footer class="navbar"> <nav class="navbar-inferior" role="navigation" aria-label="Navegacion principal"> <a href="administrador-dashboard.jsp"
                                        class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page"
                                        aria-label="Dashboard"><img src="../../Assets/icons/diagrama-dashboard.png"
                                            class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Dashboard</span></a> <a href="administrador-servicios.jsp" class="navbar-inferior__item"
                                        aria-label="Servicios"><img src="../../Assets/icons/catalogo-de-productos.png"
                                            class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Servicios</span></a> <a href="administrador-usuarios.jsp" class="navbar-inferior__item"
                                        aria-label="Usuarios"><img src="../../Assets/icons/anadir-grupo.png"
                                            class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Usuarios</span></a> <a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item"
                                        aria-label="Cerrar sesion"><img src="../../Assets/icons/salir-aplicacion.png"
                                            class="navbar-inferior__icono" alt=""><span class="navbar-inferior__texto">Salir</span></a>
                                </nav>
                            </footer>
                            <script>
                                window.addEventListener('load', function () {
                                    var params = new URLSearchParams(window.location.search);
                                    if (params.get('actualizado') === '1') { mostrarToast('Estado actualizado correctamente', 'exito'); }
                                });
                                function mostrarToast(msg, tipo) {
                                    var toast = document.getElementById('toast');
                                    toast.textContent = msg;
                                    toast.className = 'toast toast--' + tipo + ' toast--visible';
                                    setTimeout(function () { toast.classList.remove('toast--visible'); }, 3000);
                                }
                            </script>
                        </body>

                        </html>