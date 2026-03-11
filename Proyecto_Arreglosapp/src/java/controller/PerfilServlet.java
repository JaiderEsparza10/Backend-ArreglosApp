package controller;

import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import java.io.IOException;

/**
 * Controlador de Gestión de Perfil de Usuario.
 * RF-03: Gestión de Perfil.
 * Maneja la actualización de información personal y el cambio seguro de contraseñas.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/PerfilServlet")
public class PerfilServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        // Instanciación de la lógica de acceso a datos de usuario
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * Procesa las modificaciones del perfil mediante solicitudes POST.
     * Soporta las acciones 'editarDatos' y 'cambiarPassword'.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // Autenticación de seguridad en la capa de procesamiento (Session hijacking prevention)
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // ─── EDITAR DATOS PERSONALES ──────────────────────────────────
        if ("editarDatos".equals(accion)) {
            try {
                // Extracción de metadatos del perfil
                String nombre = request.getParameter("nombre");
                String direccion = request.getParameter("direccion");
                String telefono = request.getParameter("telefono");

                // Validación de integridad obligatoria
                if (nombre == null || nombre.trim().isEmpty()) {
                    session.setAttribute("errorPerfil", "El nombre no puede estar vacío");
                    response.sendRedirect("Public/client/mi-perfil.jsp");
                    return;
                }

                // Persistencia de los cambios en la base de datos
                boolean actualizado = usuarioDAO.actualizarDatosPersonales(
                        usuario.getId(), nombre.trim(), direccion);

                if (actualizado) {
                    // Sincronización proactiva de la sesión local para reflejar cambios inmediatos en el UI
                    usuario.setNombre(nombre.trim());
                    usuario.setDireccion(direccion);
                    session.setAttribute("usuario", usuario);
                }

                // Validación y actualización del medio de contacto (teléfono)
                if (telefono != null && !telefono.trim().isEmpty()) {
                    // RNF: Validación de formato numérico estricto
                    if (!telefono.trim().matches("\\d+")) {
                        session.setAttribute("errorPerfil", "El teléfono solo debe contener números");
                        response.sendRedirect("Public/client/mi-perfil.jsp");
                        return;
                    }
                    usuarioDAO.actualizarTelefono(usuario.getId(), telefono.trim());
                }

                response.sendRedirect("Public/client/mi-perfil.jsp?editado=1");

            } catch (Exception e) {
                session.setAttribute("errorPerfil", "Error al actualizar: " + e.getMessage());
                response.sendRedirect("Public/client/mi-perfil.jsp");
            }

            // ─── GESTIÓN DE SEGURIDAD (CAMBIO DE PASSWORD) ────────────────
        } else if ("cambiarPassword".equals(accion)) {
            try {
                String passwordActual = request.getParameter("passwordActual");
                String passwordNueva = request.getParameter("passwordNueva");
                String passwordConfirm = request.getParameter("passwordConfirmar");

                // Validación de integridad de la nueva contraseña
                if (passwordNueva == null || passwordNueva.trim().isEmpty()) {
                    session.setAttribute("errorPassword", "La nueva contraseña no puede estar vacía");
                    response.sendRedirect("Public/client/mi-perfil.jsp#cambiarPassword");
                    return;
                }

                if (!passwordNueva.equals(passwordConfirm)) {
                    session.setAttribute("errorPassword", "Las contraseñas no coinciden");
                    response.sendRedirect("Public/client/mi-perfil.jsp#cambiarPassword");
                    return;
                }

                // RNF: Aplicación de políticas de complejidad de seguridad
                if (!passwordNueva.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    session.setAttribute("errorPassword", "La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número");
                    response.sendRedirect("Public/client/mi-perfil.jsp#cambiarPassword");
                    return;
                }

                // Verificación de identidad mediante validación de contraseña actual (Multi-factor conceptual)
                boolean passwordCorrecta = usuarioDAO.verificarPassword(usuario.getId(), passwordActual);
                if (!passwordCorrecta) {
                    session.setAttribute("errorPassword", "La contraseña actual es incorrecta");
                    response.sendRedirect("Public/client/mi-perfil.jsp#cambiarPassword");
                    return;
                }

                // Actualización final procesando el nuevo Hash BCrypt
                boolean cambiada = usuarioDAO.actualizarPasswordPorId(usuario.getId(), passwordNueva);

                if (cambiada) {
                    response.sendRedirect("Public/client/mi-perfil.jsp?passwordCambiada=1");
                } else {
                    session.setAttribute("errorPassword", "No se pudo cambiar la contraseña");
                    response.sendRedirect("Public/client/mi-perfil.jsp");
                }

            } catch (Exception e) {
                session.setAttribute("errorPassword", "Error: " + e.getMessage());
                response.sendRedirect("Public/client/mi-perfil.jsp");
            }

        } else {
            response.sendRedirect("Public/client/mi-perfil.jsp");
        }
    }
}