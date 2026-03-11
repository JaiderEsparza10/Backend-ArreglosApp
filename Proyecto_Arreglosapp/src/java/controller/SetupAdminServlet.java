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
 * Servlet de Utilidad: Configuración Inicial del Sistema.
 * Facilita el aprovisionamiento manual del primer usuario administrador mediante la generación de Hash BCrypt.
 * Proporciona scripts SQL e intentos de inserción automática para bootstrapping.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/SetupAdminServlet")
public class SetupAdminServlet extends HttpServlet {

    /**
     * Genera y visualiza la información del bootstrap administrativo.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            // Generación de Hash Seguro BCrypt para credenciales estáticas iniciales
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

            // Exposición de scripts SQL para despliegue manual en entornos de producción
            out.println("<h2>SQL para insertar el administrador:</h2>");
            out.println("<pre style='background:#f0f0f0;padding:10px;overflow-x:auto;'>");
            out.println("-- Paso 1: Limpiar registros previos con el mismo identificador");
            out.println("DELETE FROM USUARIOS WHERE user_email = 'admin@arreglosapp.com';");
            out.println();
            out.println("-- Paso 2: Inserción de cuenta administrativa raíz");
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

            out.println("<h2>Verificación de integridad de Hash:</h2>");
            boolean verifica = BCrypt.checkpw(password, hash);
            out.println("<p>Verificación del hash: <strong>" + (verifica ? "CORRECTO ✓" : "INCORRECTO ✗")
                    + "</strong></p>");

            out.println("<p><a href='index.jsp'>Ir al inicio de sesión</a></p>");

            // Intento de Bootstrapping Automático: Provisión directa en la DB si está disponible
            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();

                if (usuarioDAO.existeAdministrador()) {
                    out.println("<p style='color:orange;'>⚠️ El administrador maestro ya existe en el sistema.</p>");
                } else {
                    // Inserción programática mediante la capa DAO
                    boolean creado = usuarioDAO.crearAdministradorPorDefecto();

                    if (creado) {
                        out.println(
                                "<p style='color:green;'>✅ Bootstrap Exitoso: Administrador creado mediante DAO.</p>");
                    } else {
                        out.println("<p style='color:red;'>❌ Fallo en el Bootstrap automático.</p>");
                    }
                }

            } catch (Exception e) {
                out.println("<p style='color:red;'>Excepción durante Bootstrap: " + e.getMessage() + "</p>");
            }
        }
    }
}
