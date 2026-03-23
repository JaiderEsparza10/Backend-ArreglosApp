<%-- 
    Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
    Propósito: Perfil detallado de un usuario desde la perspectiva administrativa con opciones de gestión.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.AdminDAO" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Usuario admin = (Usuario) session.getAttribute("usuario");
    if (admin == null || admin.getRolId() != 1) {
        response.sendRedirect("../../index.jsp");
        return;
    }

    String userIdStr = request.getParameter("userId");
    if (userIdStr == null || userIdStr.isEmpty()) {
        response.sendRedirect("administrador-usuarios.jsp");
        return;
    }

    int userId = Integer.parseInt(userIdStr);
    AdminDAO adminDAO = new AdminDAO();
    Map<String, Object> userData = null;
    List<Map<String, Object>> personalizaciones = new ArrayList<>();
    
    try {
        userData = adminDAO.obtenerUsuarioPorId(userId);
        if (userData == null) {
            response.sendRedirect("administrador-usuarios.jsp?error=noEncontrado");
            return;
        }
        personalizaciones = adminDAO.obtenerPersonalizacionesPorUsuario(userId);
    } catch (Exception e) {
        e.printStackTrace();
    }

    String nombre = (String) userData.get("nombre");
    String email = (String) userData.get("email");
    String tel = (String) userData.get("telefono");
    String dir = (String) userData.get("direccion");
    String inicial = (nombre != null && !nombre.isEmpty()) ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Detalle de Usuario - <%= nombre %></title>
    <style>
        .perfil-container { padding: 20px; max-width: 600px; margin: 0 auto; min-height: 70vh; display: flex; flex-direction: column; }
        .btn-regresar { display: inline-flex; align-items: center; gap: 8px; text-decoration: none; color: #666; font-size: 14px; margin-bottom: 30px; transition: color 0.2s; font-weight: 500; }
        .btn-regresar:hover { color: #6f42c1; }
        
        .usuario-detalles-card { background: #fff; border-radius: 20px; padding: 40px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); text-align: center; }
        .usuario-avatar { width: 100px; height: 100px; background: linear-gradient(135deg, #6f42c1 0%, #a88beb 100%); color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 40px; font-weight: bold; margin: 0 auto 25px; box-shadow: 0 5px 15px rgba(111, 66, 193, 0.3); }
        
        .usuario-info-primaria { margin-bottom: 35px; }
        .usuario-email { font-size: 22px; font-weight: 700; color: #333; margin-bottom: 10px; display: block; word-break: break-all; }
        .usuario-tel { font-size: 18px; color: #666; display: flex; align-items: center; justify-content: center; gap: 8px; }
        .usuario-tel::before { content: '📞'; font-size: 16px; }

        .separador { height: 1px; background: #eee; margin: 30px 0; }

        .acciones-usuario { display: flex; flex-direction: column; gap: 15px; }
        .btn-eliminar-usuario { 
            background: #fff; 
            color: #dc3545; 
            border: 2px solid #dc3545; 
            padding: 14px; 
            border-radius: 12px; 
            font-weight: 600; 
            cursor: pointer; 
            transition: all 0.3s ease; 
            text-decoration: none;
            font-size: 16px;
        }
        .btn-eliminar-usuario:hover { 
            background: #dc3545; 
            color: #fff; 
            box-shadow: 0 5px 15px rgba(220, 53, 69, 0.3);
        }

        /* Modal simple */
        .modal-eliminar { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: none; align-items: center; justify-content: center; z-index: 2000; }
        .modal-eliminar:target { display: flex; }
        .modal-content { background: #fff; padding: 30px; border-radius: 15px; max-width: 400px; width: 90%; text-align: center; }
        .modal-btns { display: flex; gap: 10px; margin-top: 25px; }
        .btn-modal { flex: 1; padding: 12px; border-radius: 8px; font-weight: 600; text-decoration: none; cursor: pointer; border: none; font-size: 14px; }
        .btn-modal--cancelar { background: #f8f9fa; color: #333; }
        .btn-modal--confirmar { background: #dc3545; color: #fff; }
    </style>
</head>
<body class="grid-principal">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
    </header>

    <main class="perfil-container">
        <a href="administrador-usuarios.jsp" class="btn-regresar">
            <span>←</span> Volver a la Lista de Usuarios
        </a>

        <article class="usuario-detalles-card">
            <div class="usuario-avatar"><%= inicial %></div>
            
            <div class="usuario-info-primaria">
                <span class="usuario-email"><%= email %></span>
                <span class="usuario-tel"><%= tel != null ? tel : "Sin teléfono registrado" %></span>
            </div>

            <div class="separador"></div>

            <div class="acciones-usuario">
                <a href="#confirmar-eliminacion" class="btn-eliminar-usuario">Eliminar Usuario</a>
            </div>
        </article>

        <!-- Modal de Confirmación -->
        <div id="confirmar-eliminacion" class="modal-eliminar">
            <div class="modal-content">
                <h3 style="margin-bottom: 15px; color: #333;">¿Eliminar este usuario?</h3>
                <p style="font-size: 14px; color: #666; line-height: 1.5;">
                    Esta acción eliminará permanentemente la cuenta de <strong><%= email %></strong>. 
                    Solo se puede realizar si el usuario no tiene pedidos o citas activas.
                </p>
                <div class="modal-btns">
                    <a href="#" class="btn-modal btn-modal--cancelar">Cancelar</a>
                    <form action="/Proyecto_Arreglosapp/UsuarioServlet" method="get" style="flex:1;">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="userId" value="<%= userId %>">
                        <button type="submit" class="btn-modal btn-modal--confirmar" style="width:100%;">Eliminar</button>
                    </form>
                </div>
            </div>
        </div>
    </main>


    <footer class="navbar">
        <nav class="navbar-inferior">
            <a href="administrador-dashboard.jsp" class="navbar-inferior__item">
                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Dashboard</span>
            </a>
            <a href="administrador-servicios.jsp" class="navbar-inferior__item">
                <img src="../../Assets/icons/catalogo-de-productos.png" class="navbar-inferior__icono">
                <span class="navbar-inferior__texto">Servicios</span>
            </a>
            <a href="administrador-usuarios.jsp" class="navbar-inferior__item navbar-inferior__item--activo" aria-label="Usuarios">
                <img src="../../Assets/icons/anadir-grupo.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Usuarios</span>
            </a>
            <a href="/Proyecto_Arreglosapp/LogoutServlet" class="navbar-inferior__item" aria-label="Cerrar sesion">
                <img src="../../Assets/icons/salir-aplicacion.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Salir</span>
            </a>
        </nav>
    </footer>
</body>
</html>
