package controller;

import dao.UsuarioDAO;
import model.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Controlador de Gestión de Cuentas de Usuario.
 * RF-02: Registro de Usuarios.
 * Valida y procesa la creación de nuevos perfiles de cliente en la plataforma.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/UsuarioServlet")
public class ControllerUser extends HttpServlet {

    /**
     * Procesa la creación de un nuevo usuario mediante método POST.
     * Incluye validaciones estrictas de integridad y seguridad de datos.
     * Este método maneja la lógica de negocio para el registro de usuarios,
     * interactuando con la capa DAO para la persistencia de datos.
     * Realiza validaciones de campos obligatorios, coincidencia de contraseñas,
     * complejidad de contraseña, unicidad de email y teléfono antes de
     * intentar registrar el usuario en la base de datos.
     *
     * @param request Objeto HttpServletRequest que contiene los parámetros de la solicitud.
     * @param response Objeto HttpServletResponse para enviar la respuesta al cliente.
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extracción de parámetros desde el formulario de registro
        String nombre = request.getParameter("txtNombre");
        String email = request.getParameter("txtEmail");
        String pass = request.getParameter("txtPassword");
        String direccion = request.getParameter("txtDireccion");
        String telefono = request.getParameter("txtTelefono");

        String contextPath = request.getContextPath();
        String rutaRegistro = contextPath + "/Public/auth/registrarse.jsp";
        String rutaLogin = contextPath + "/index.jsp";

        // Validación exhaustiva de presencia de datos obligatorios
        String confirmarPass = request.getParameter("txtConfirmarPassword");
        if (nombre == null || email == null || pass == null || confirmarPass == null ||
                nombre.trim().isEmpty() || email.trim().isEmpty() || pass.trim().isEmpty() || confirmarPass.trim().isEmpty()) {
            response.sendRedirect(rutaRegistro + "?msg=camposVacios");
            return;
        }

        // Regla de negocio: Las contraseñas deben coincidir para prevenir errores de entrada
        if (!pass.equals(confirmarPass)) {
            response.sendRedirect(rutaRegistro + "?msg=contrasenasNoCoinciden&nombre=" +
                    java.net.URLEncoder.encode(nombre.trim(), "UTF-8") +
                    "&email=" + java.net.URLEncoder.encode(email.trim(), "UTF-8") +
                    "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8") +
                    "&telefono=" + java.net.URLEncoder.encode(telefono != null ? telefono.trim() : "", "UTF-8"));
            return;
        }

        // RNF: Seguridad y Complejidad. Mínimo 8 caracteres, una mayúscula y un dígito.
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
            // Validación de unicidad de identidad digital (email)
            if (dao.existeEmail(email.trim())) {
                response.sendRedirect(rutaRegistro + "?msg=emailExiste&nombre=" +
                        java.net.URLEncoder.encode(nombre.trim(), "UTF-8") +
                        "&direccion=" + java.net.URLEncoder.encode(direccion != null ? direccion.trim() : "", "UTF-8") +
                        "&telefono=" + java.net.URLEncoder.encode(telefono != null ? telefono.trim() : "", "UTF-8"));
                return;
            }

            // Validación de unicidad de medio de contacto (teléfono)
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

            // Mapeo a objeto de modelo para persistencia
            Usuario user = new Usuario();
            user.setNombre(nombre.trim());
            user.setEmail(email.trim());
            user.setPassword(pass); // El DAO se encarga del hasheo BCrypt para mayor seguridad
            user.setDireccion(direccion != null ? direccion.trim() : "");

            if (dao.registrarUsuarioCompleto(user, telefono)) {
                // Éxito: Redirección al login con mensaje flash positivo
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