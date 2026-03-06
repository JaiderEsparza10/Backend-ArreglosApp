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

@WebServlet("/PerfilServlet")
public class PerfilServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // ─── EDITAR DATOS ─────────────────────────────────────────
        if ("editarDatos".equals(accion)) {
            try {
                String nombre = request.getParameter("nombre");
                String direccion = request.getParameter("direccion");
                String telefono = request.getParameter("telefono");

                if (nombre == null || nombre.trim().isEmpty()) {
                    session.setAttribute("errorPerfil", "El nombre no puede estar vacío");
                    response.sendRedirect("Public/client/mi-perfil.jsp");
                    return;
                }

                boolean actualizado = usuarioDAO.actualizarDatosPersonales(
                        usuario.getId(), nombre.trim(), direccion);

                if (actualizado) {
                    // Actualizar sesión con nuevos datos
                    usuario.setNombre(nombre.trim());
                    usuario.setDireccion(direccion);
                    session.setAttribute("usuario", usuario);
                }

                // Actualizar teléfono si fue enviado
                if (telefono != null && !telefono.trim().isEmpty()) {
                    usuarioDAO.actualizarTelefono(usuario.getId(), telefono.trim());
                }

                response.sendRedirect("Public/client/mi-perfil.jsp?editado=1");

            } catch (Exception e) {
                session.setAttribute("errorPerfil", "Error al actualizar: " + e.getMessage());
                response.sendRedirect("Public/client/mi-perfil.jsp");
            }

            // ─── CAMBIAR CONTRASEÑA ───────────────────────────────────
        } else if ("cambiarPassword".equals(accion)) {
            try {
                String passwordActual = request.getParameter("passwordActual");
                String passwordNueva = request.getParameter("passwordNueva");
                String passwordConfirm = request.getParameter("passwordConfirmar");

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

                if (passwordNueva.length() < 6) {
                    session.setAttribute("errorPassword", "La contraseña debe tener al menos 6 caracteres");
                    response.sendRedirect("Public/client/mi-perfil.jsp#cambiarPassword");
                    return;
                }

                boolean passwordCorrecta = usuarioDAO.verificarPassword(usuario.getId(), passwordActual);
                if (!passwordCorrecta) {
                    session.setAttribute("errorPassword", "La contraseña actual es incorrecta");
                    response.sendRedirect("Public/client/mi-perfil.jsp#cambiarPassword");
                    return;
                }

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