<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Prueba de Registro</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h2>Registro de Usuario Nuevo</h2>
        <form action="ControllerUser" method="POST">
            <label>Nombre:</label><br>
            <input type="text" name="txtNombre" required><br><br>
            
            <label>Email:</label><br>
            <input type="email" name="txtEmail" required><br><br>
            
            <label>Contraseña:</label><br>
            <input type="password" name="txtPass" required><br><br>
            
            <label>Dirección:</label><br>
            <input type="text" name="txtDireccion"><br><br>
            
            <label>ID de Rol (Ej: 1):</label><br>
            <input type="number" name="txtRol" value="1"><br><br>
            
            <input type="submit" value="Guardar en Base de Datos">
        </form>
    </body>
</html>