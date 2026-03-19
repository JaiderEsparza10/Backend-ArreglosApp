<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.AdminDAO" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%
Usuario admin = (Usuario) session.getAttribute("usuario");
if (admin == null || admin.getRolId() != 1) {
    response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
    return;
}
String busqueda = request.getParameter("busqueda");
String error = request.getParameter("error");
String exito = request.getParameter("exito");
AdminDAO adminDAO = new AdminDAO();
List<Map<String, Object>> usuarios = new ArrayList<>();
try {
    usuarios = adminDAO.obtenerUsuarios(busqueda);
} catch (Exception e) {
    e.printStackTrace();
}
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Administrador de Usuarios</title>
    <style>
        /* Toast Notifications - Estilo uniforme */
        .toast-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
        }
        
        .toast {
            padding: 15px 40px;
            border-radius: 50px;
            font-size: 14px;
            font-weight: 600;
            color: #fff;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            transform: translateX(400px);
            transition: transform 0.4s ease, opacity 0.4s ease;
            max-width: 400px;
            word-wrap: break-word;
            text-align: center;
            margin-bottom: 10px;
        }
        
        .toast--visible {
            transform: translateX(0);
        }
        
        .toast--success {
            background: #28a745;
        }
        
        .toast--error {
            background: #dc3545;
        }
        
        /* Modal de Confirmación Personalizado */
        .confirm-modal {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.6);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }
        
        .confirm-modal__content {
            background: #fff;
            border-radius: 12px;
            padding: 30px;
            max-width: 450px;
            width: 90%;
            text-align: center;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            animation: modalSlideIn 0.3s ease;
        }
        
        @keyframes modalSlideIn {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .confirm-modal__icon {
            font-size: 48px;
            margin-bottom: 15px;
        }
        
        .confirm-modal__title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
        }
        
        .confirm-modal__message {
            font-size: 14px;
            color: #666;
            line-height: 1.6;
            margin-bottom: 25px;
            text-align: left;
        }
        
        .confirm-modal__list {
            text-align: left;
            margin: 15px 0;
            padding-left: 20px;
            color: #666;
        }
        
        .confirm-modal__list li {
            margin-bottom: 8px;
            font-size: 13px;
        }
        
        .confirm-modal__warning {
            color: #dc3545;
            font-weight: 600;
            margin-top: 15px;
            padding: 10px;
            background: #f8d7da;
            border-radius: 6px;
            font-size: 13px;
        }
        
        .confirm-modal__actions {
            display: flex;
            gap: 10px;
            justify-content: center;
        }
        
        .confirm-modal__btn {
            padding: 12px 30px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .confirm-modal__btn--cancel {
            background: #e9ecef;
            color: #495057;
        }
        
        .confirm-modal__btn--cancel:hover {
            background: #dee2e6;
        }
        
        .confirm-modal__btn--delete {
            background: #dc3545;
            color: #fff;
        }
        
        .confirm-modal__btn--delete:hover {
            background: #c82333;
        }
    </style>
</head>
<body class="grid-principal">
    <a href="#contenido-principal" class="skip-link">Saltar al contenido</a>
    
    <!-- Toast Container -->
    <div id="toastContainer" class="toast-container"></div>
    
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
    </header>

    <main class="usuarios" id="contenido-principal" role="main">
        <form method="get" action="" class="usuarios__buscador">
            <img src="../../Assets/icons/lupa.png" alt="buscar" class="usuarios__buscador-icono">
            <input type="text" name="busqueda" class="usuarios__buscador-input"
                   placeholder="Buscar usuario..." value="<%= busqueda != null ? busqueda : "" %>">
        </form>

        <div class="usuarios__lista">
            <% if (usuarios.isEmpty()) { %>
                <p style="color:#888;font-size:13px;text-align:center;padding:20px;">
                    No se encontraron usuarios
                </p>
            <% } %>
            
            <% for (int i = 0; i < usuarios.size(); i++) { 
                Map<String, Object> u = usuarios.get(i);
                int userId = (int) u.get("userId");
                String nombre = (String) u.get("nombre");
                String email = (String) u.get("email");
                String tel = "Sin telefono";
                if (u.get("telefono") != null) {
                    tel = (String) u.get("telefono");
                }
                String inicial = "?";
                if (nombre != null && !nombre.isEmpty()) {
                    inicial = String.valueOf(nombre.charAt(0)).toUpperCase();
                }
            %>
            <article class="usuario-card">
                <div class="usuario-card__avatar">
                    <span class="usuario-card__inicial"><%= inicial %></span>
                </div>
                <div class="usuario-card__info">
                    <span class="usuario-card__nombre"><%= nombre %></span>
                    <span class="usuario-card__email"><%= email %></span>
                    <span class="usuario-card__email"><%= tel %></span>
                </div>
                <div class="usuario-card__acciones">
                    <a href="detalle-usuario.jsp?userId=<%= userId %>" class="usuario-card__btn-ver" style="text-decoration:none; display:inline-block; text-align:center;">
                        Ver Perfil
                    </a>
                </div>
            </article>
            <% } %>
        </div>
    </main>

    <!-- MODAL VER PERFIL -->
    <div id="modalPerfil" class="modal-overlay" style="display:none;">
        <div class="modal-contenido">
            <div class="modal-perfil__avatar">
                <span id="modalPerfil__inicial" class="modal-perfil__inicial"></span>
            </div>
            <h2 class="modal__titulo" id="modalPerfil__nombre"></h2>
            <div class="modal-perfil__datos">
                <div class="modal-perfil__fila">
                    <span class="modal-perfil__icono">✉</span>
                    <span id="modalPerfil__email" class="modal-perfil__valor"></span>
                </div>
                <div class="modal-perfil__fila">
                    <span class="modal-perfil__icono">📞</span>
                    <span id="modalPerfil__tel" class="modal-perfil__valor"></span>
                </div>
            </div>
            <div class="modal__acciones">
                <button class="btn-modal btn-modal--cancelar" onclick="cerrarModalPerfil()">Cerrar</button>
                <button class="btn-modal btn-modal--eliminar" onclick="confirmarEliminar()">Eliminar</button>
            </div>
        </div>
    </div>

    <!-- MODAL CONFIRMAR ELIMINAR PERSONALIZADO -->
    <div id="confirmModal" class="confirm-modal" style="display:none;">
        <div class="confirm-modal__content">
            <div class="confirm-modal__icon">⚠️</div>
            <h2 class="confirm-modal__title">¿Eliminar usuario?</h2>
            <p class="confirm-modal__message">
                Estás a punto de eliminar al usuario <strong id="confirmModalUserName"></strong>.
                <br><br>
                Esta acción solo es posible si el usuario cumple estas condiciones:
            </p>
            <ul class="confirm-modal__list">
                <li>✓ No tiene favoritos registrados</li>
                <li>✓ No tiene pedidos activos</li>
                <li>✓ No tiene citas pendientes</li>
            </ul>
            <div class="confirm-modal__warning">
                ⚠️ Esta acción no se puede deshacer
            </div>
            <div class="confirm-modal__actions">
                <button class="confirm-modal__btn confirm-modal__btn--cancel" onclick="cerrarConfirmModal()">
                    Cancelar
                </button>
                <button class="confirm-modal__btn confirm-modal__btn--delete" onclick="confirmarEliminacionFinal()">
                    Eliminar Usuario
                </button>
            </div>
        </div>
    </div>

    <!-- MODAL CONFIRMAR ELIMINAR -->
    <div id="modalEliminar" class="modal-overlay" style="display:none;">
        <div class="modal-contenido">
            <h2 class="modal__titulo">¿Eliminar usuario?</h2>
            <p class="modal__descripcion">Esta acción solo se puede realizar si el usuario no tiene datos asociados.</p>
            <p class="modal__subtitulo" id="modalNombreUsuario"></p>
            <div class="modal__acciones">
                <button class="btn-modal btn-modal--cancelar" onclick="cerrarModalEliminar()">Cancelar</button>
                <form method="get" action="/Proyecto_Arreglosapp/UsuarioServlet" id="formEliminar" style="flex:1;display:flex;">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="hidden" name="userId" id="inputUserId">
                    <button type="submit" class="btn-modal btn-modal--eliminar" style="flex:1;">Eliminar</button>
                </form>
            </div>
        </div>
    </div>

    <footer class="navbar">
        <nav class="navbar-inferior" role="navigation" aria-label="Navegación principal">
            <a href="administrador-dashboard.jsp" class="navbar-inferior__item" aria-label="Dashboard">
                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Dashboard</span>
            </a>
            <a href="administrador-servicios.jsp" class="navbar-inferior__item" aria-label="Servicios">
                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Servicios</span>
            </a>
            <a href="administrador-usuarios.jsp" class="navbar-inferior__item navbar-inferior__item--activo" aria-current="page" aria-label="Usuarios">
                <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Usuarios</span>
            </a>
            <a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item" aria-label="Cerrar sesión">
                <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Salir</span>
            </a>
        </nav>
    </footer>

    <script>
        var perfilUserId = 0;

        function verPerfil(userId, nombre, email, tel) {
            perfilUserId = userId;
            document.getElementById('modalPerfil__inicial').textContent = nombre.charAt(0).toUpperCase();
            document.getElementById('modalPerfil__nombre').textContent = nombre;
            document.getElementById('modalPerfil__email').textContent = email;
            document.getElementById('modalPerfil__tel').textContent = tel;
            document.getElementById('modalPerfil').style.display = 'flex';
        }

        function cerrarModalPerfil() {
            document.getElementById('modalPerfil').style.display = 'none';
        }

        function confirmarEliminar() {
            var nombreUsuario = document.getElementById('modalPerfil__nombre').textContent;
            document.getElementById('confirmModalUserName').textContent = nombreUsuario;
            document.getElementById('confirmModal').style.display = 'flex';
        }

        function cerrarConfirmModal() {
            document.getElementById('confirmModal').style.display = 'none';
        }

        function confirmarEliminacionFinal() {
            var nombreUsuario = document.getElementById('modalPerfil__nombre').textContent;
            document.getElementById('modalNombreUsuario').textContent = nombreUsuario;
            document.getElementById('inputUserId').value = perfilUserId;
            cerrarConfirmModal();
            cerrarModalPerfil();
            document.getElementById('modalEliminar').style.display = 'flex';
        }

        function cerrarModalEliminar() {
            document.getElementById('modalEliminar').style.display = 'none';
        }

        window.addEventListener('load', function () {
            var params = new URLSearchParams(window.location.search);
            
            // Mensajes de éxito
            if (params.get('exito') === 'usuarioEliminado') {
                mostrarToast('✓ Usuario eliminado exitosamente', 'success');
            }
            
            // Mensajes de error
            if (params.get('error')) {
                var errorMsg = params.get('error');
                if (errorMsg === 'usuarioNoValido') {
                    mostrarToast('✗ ID de usuario no válido', 'error');
                } else if (errorMsg === 'idInvalido') {
                    mostrarToast('✗ ID de usuario inválido', 'error');
                } else if (errorMsg === 'noEncontrado') {
                    mostrarToast('✗ Usuario no encontrado', 'error');
                } else if (errorMsg === 'general') {
                    mostrarToast('✗ Error al eliminar usuario', 'error');
                } else {
                    // Error personalizado desde el servidor
                    mostrarToast('✗ ' + decodeURIComponent(errorMsg), 'error');
                }
            }
        });

        function mostrarToast(mensaje, tipo) {
            var container = document.getElementById('toastContainer');
            var toast = document.createElement('div');
            toast.className = 'toast toast--' + tipo;
            toast.textContent = mensaje;
            
            container.appendChild(toast);
            
            // Trigger animation
            setTimeout(function() {
                toast.classList.add('toast--visible');
            }, 10);
            
            // Remove after 3 seconds
            setTimeout(function() {
                toast.classList.remove('toast--visible');
                setTimeout(function() {
                    container.removeChild(toast);
                }, 300);
            }, 3000);
        }
    </script>
</body>
</html>