<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.AdminDAO" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    System.out.println("DEBUG: JSP simple iniciado");
    
    Usuario admin = (Usuario) session.getAttribute("usuario");
    System.out.println("DEBUG: admin = " + (admin != null ? "encontrado" : "null"));
    
    if (admin == null || admin.getRolId() != 1) {
        System.out.println("DEBUG: Redirigiendo - no autorizado");
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String pedidoIdStr = request.getParameter("pedidoId");
    System.out.println("DEBUG: pedidoIdStr = " + pedidoIdStr);
    
    if (pedidoIdStr == null) {
        System.out.println("DEBUG: Redirigiendo - pedidoId null");
        response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp");
        return;
    }

    try {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        AdminDAO adminDAO = new AdminDAO();
        Map<String, Object> detalle = adminDAO.obtenerDetallePedido(pedidoId);
        
        System.out.println("DEBUG: detalle = " + (detalle != null ? "encontrado" : "null"));
        
        if (detalle == null) {
            System.out.println("DEBUG: Redirigiendo - detalle null");
            response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp");
            return;
        }
        
        System.out.println("DEBUG: Continuando a mostrar página");
        
    } catch (Exception e) {
        System.out.println("DEBUG: Error - " + e.getMessage());
        e.printStackTrace();
        response.sendRedirect(request.getContextPath() + "/Public/admin/administrador-dashboard.jsp?error=exception");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../../Assets/estilos.css">
    <title>Detalle Pedido (Simple)</title>
</head>
<body class="grid-principal">
    <header class="seccion-encabezado">
        <img class="seccion-encabezado__logo" src="../../Assets/image/logo-app.png" alt="logo">
        <h1 class="seccion-encabezado__nombre">ArreglosApp</h1>
    </header>
    
    <main class="dashboard" id="contenido-principal" role="main">
        <section class="info-pedido">
            <div class="info-seccion">
                <h2 class="info-seccion__titulo">✅ Página de Detalles Cargada Correctamente</h2>
                <p>El JSP se ha compilado y ejecutado sin errores.</p>
                <p>Pedido ID: <%= pedidoIdStr %></p>
                <p>Usuario: <%= admin.getNombre() %></p>
                
                <div style="margin-top: 20px;">
                    <a href="<%= request.getContextPath() %>/Public/admin/administrador-dashboard.jsp" 
                       class="pedido__enlace">Volver al Dashboard</a>
                </div>
            </div>
        </section>
    </main>
    
    <footer class="navbar">
        <nav class="navbar-inferior" role="navigation" aria-label="Navegacion principal">
            <a href="administrador-dashboard.jsp" class="navbar-inferior__item navbar-inferior__item--activo" 
               aria-current="page" aria-label="Dashboard">
                <img src="../../Assets/icons/diagrama-dashboard.png" class="navbar-inferior__icono" alt="">
                <span class="navbar-inferior__texto">Dashboard</span>
            </a>
        </nav>
    </footer>
</body>
</html>
