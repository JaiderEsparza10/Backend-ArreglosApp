package controller;

import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Este servlet de utilidad facilita la configuración inicial del administrador
 * del sistema.
 * Genera el hash de la contraseña y proporciona el script SQL para la inserción
 * manual o automática.
 */
@WebServlet("/SetupAdminServlet")
public class SetupAdminServlet extends HttpServlet {

    /**
     * Procesa las solicitudes GET para mostrar la información de configuración del
     * administrador.
     * Genera un hash BCrypt y verifica si el administrador ya existe en el sistema.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            // Generar hash para la contraseña admin123
            String password = "admin123";
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());

            out.println("<h1>Generador de Hash para Administrador</h1>");
            out.println("<h2>Datos del Administrador:</h2>");
            out.println("<p><strong>Email:</strong> admin@arreglosapp.com</p>");
            out.println("<p><strong>Contraseña:</strong> " + password + "</p>");
            out.println("<p><strong>Rol:</strong> Administrador (rol_id = 1)</p>");

            out.println("<h2>Hash BCrypt generado:</h2>");
            out.println("<code style='background:#f0f0f0;padding:10px;display:block;word-break:break-all;'>");
            out.println(hash);
            out.println("</code>");

            out.println("<h2>SQL para insertar el administrador:</h2>");
            out.println("<pre style='background:#f0f0f0;padding:10px;overflow-x:auto;'>");
            out.println("-- Primero eliminar si existe");
            out.println("DELETE FROM USUARIOS WHERE user_email = 'admin@arreglosapp.com';");
            out.println();
            out.println("-- Insertar nuevo administrador");
            out.println(
                    "INSERT INTO USUARIOS (user_email, user_password_hash, user_nombre, user_ubicacion_direccion, rol_id)");
            out.println("VALUES (");
            out.println("    'admin@arreglosapp.com',");
            out.println("    '" + hash + "',");
            out.println("    'Administrador del Sistema',");
            out.println("    'Oficina Principal',");
            out.println("    1");
            out.println(");");
            out.println("</pre>");

            out.println("<h2>Verificación:</h2>");
            boolean verifica = BCrypt.checkpw(password, hash);
            out.println("<p>Verificación del hash: <strong>" + (verifica ? "CORRECTO ✓" : "INCORRECTO ✗")
                    + "</strong></p>");

            out.println("<p><a href='index.jsp'>Ir al login</a></p>");

            // Intentar crear el administrador automáticamente
            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();

                // Verificar si ya existe un administrador
                if (usuarioDAO.existeAdministrador()) {
                    out.println("<p style='color:orange;'>⚠️ Ya existe un administrador en el sistema.</p>");
                } else {
                    // Crear administrador por defecto
                    boolean creado = usuarioDAO.crearAdministradorPorDefecto();

                    if (creado) {
                        out.println(
                                "<p style='color:green;'>✅ Administrador creado exitosamente usando el método del DAO.</p>");
                    } else {
                        out.println("<p style='color:red;'>❌ No se pudo crear el administrador automáticamente.</p>");
                    }
                }

            } catch (Exception e) {
                out.println("<p style='color:red;'>Error al crear administrador: " + e.getMessage() + "</p>");
            }
        }
    }
}
