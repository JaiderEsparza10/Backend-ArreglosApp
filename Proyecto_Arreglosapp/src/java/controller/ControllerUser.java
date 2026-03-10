package controller;

import dao.UsuarioDAO;
import model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Este servlet controla el registro de nuevos usuarios en el sistema.
 * Valida la información del usuario, verifica duplicados y registra los datos
 * en la base de datos.
 */
@WebServlet("/UsuarioServlet")
public class ControllerUser extends HttpServlet {

    /**
     * Procesa las solicitudes POST para registrar un nuevo usuario.
     * Valida los campos obligatorios, verifica unicidad de email y teléfono, y
     * guarda el usuario.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("txtNombre");
        String email = request.getParameter("txtEmail");
        String pass = request.getParameter("txtPassword");
        String direccion = request.getParameter("txtDireccion");
        String telefono = request.getParameter("txtTelefono");

        // ← Ruta base del contexto para los redirects
        String contextPath = request.getContextPath();
        String rutaRegistro = contextPath + "/Public/auth/registrarse.jsp";
        String rutaLogin = contextPath + "/index.jsp";

        // Validación campos vacíos
        String confirmarPass = request.getParameter("txtConfirmarPassword");
        if (nombre == null || email == null || pass == null || confirmarPass == null ||
                nombre.trim().isEmpty() || email.trim().isEmpty() || pass.trim().isEmpty() || confirmarPass.trim().isEmpty()) {
            response.sendRedirect(rutaRegistro + "?msg=camposVacios");
            return;
        }

        // Validación de coincidencia de contraseñas
        if (!pass.equals(confirmarPass)) {
            response.sendRedirect(rutaRegistro + "?msg=contrasenasNoCoinciden&nombre=" +
                    java.net.URLEncoder.encode(nombre.trim(), "UTF-8") +
                    "&email=" + java.net.URLEncoder.encode(email.trim(), "UTF-8") +
                    "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8") +
                    "&telefono=" + java.net.URLEncoder.encode(telefono != null ? telefono.trim() : "", "UTF-8"));
            return;
        }

        // Validación de complejidad de contraseña (mínimo 8 caracteres, 1 mayúscula, 1 número)
        if (!pass.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            response.sendRedirect(rutaRegistro + "?msg=contrasenaInvalida&nombre=" +
                    java.net.URLEncoder.encode(nombre.trim(), "UTF-8") +
                    "&email=" + java.net.URLEncoder.encode(email.trim(), "UTF-8") +
                    "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8") +
                    "&telefono=" + java.net.URLEncoder.encode(telefono != null ? telefono.trim() : "", "UTF-8"));
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        try {
            // Verificar email duplicado
            if (dao.existeEmail(email.trim())) {
                response.sendRedirect(rutaRegistro + "?msg=emailExiste&nombre=" +
                        java.net.URLEncoder.encode(nombre.trim(), "UTF-8") +
                        "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8") +
                        "&telefono=" + java.net.URLEncoder.encode(telefono != null ? telefono.trim() : "", "UTF-8"));
                return;
            }

            // Verificar teléfono duplicado
            if (telefono != null && !telefono.trim().isEmpty()) {
                if (dao.existeTelefono(telefono.trim())) {
                    response.sendRedirect(rutaRegistro + "?msg=telefonoExiste&nombre=" +
                            java.net.URLEncoder.encode(nombre.trim(), "UTF-8") +
                            "&email=" + java.net.URLEncoder.encode(email.trim(), "UTF-8") +
                            "&direccion="
                            + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8"));
                    return;
                }
            }

            // Crear objeto usuario
            Usuario user = new Usuario();
            user.setNombre(nombre.trim());
            user.setEmail(email.trim());
            user.setPassword(pass); // Se hashea en el DAO
            user.setDireccion(direccion != null ? direccion.trim() : "");

            if (dao.registrarUsuarioCompleto(user, telefono)) {
                response.sendRedirect(rutaLogin + "?msg=exitoRegistro");
            } else {
                response.sendRedirect(rutaRegistro + "?msg=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(rutaRegistro + "?msg=errorServidor");
        }
    }
}