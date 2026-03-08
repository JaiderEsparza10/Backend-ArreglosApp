package controller;

import dao.PersonalizacionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Personalizacion;
import model.Usuario;
import java.io.IOException;

/**
 * Este servlet permite a los usuarios personalizar sus arreglos de ropa.
 * Maneja la creación, edición y eliminación de personalizaciones, incluyendo la
 * subida de imágenes.
 */
@WebServlet("/PersonalizacionServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class PersonalizacionServlet extends HttpServlet {

    private PersonalizacionDAO personalizacionDAO;

    /**
     * Inicializa el DAO de personalización para gestionar los datos de los
     * arreglos.
     */
    @Override
    public void init() throws ServletException {
        personalizacionDAO = new PersonalizacionDAO();
    }

    /**
     * Procesa las solicitudes POST para crear o editar una personalización de
     * arreglo.
     * Valida la sesión del usuario y procesa la imagen de referencia si se
     * proporciona.
     */
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

        // ─── CREAR ───────────────────────────────────────────────
        if ("crear".equals(accion)) {
            try {
                String categoria = request.getParameter("categoria");
                String descripcion = request.getParameter("descripcion");
                String materialTela = request.getParameter("materialTela");

                if (categoria == null || categoria.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "Debes seleccionar una categoría");
                    response.sendRedirect("Public/client/personalizar-arreglo.jsp");
                    return;
                }

                String imagenReferencia = procesarImagen(request);

                Personalizacion personalizacion = new Personalizacion(
                        usuario.getId(), categoria, descripcion, materialTela, imagenReferencia);

                boolean creada = personalizacionDAO.crearPersonalizacion(personalizacion);

                if (creada) {
                    response.sendRedirect("Public/client/mis-arreglos.jsp?creado=1");
                } else {
                    session.setAttribute("errorPersonalizacion", "No se pudo crear la personalización.");
                    response.sendRedirect("Public/client/personalizar-arreglo.jsp");
                }

            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Error: " + e.getMessage());
                response.sendRedirect("Public/client/personalizar-arreglo.jsp");
            }

            // ─── EDITAR ───────────────────────────────────────────────
        } else if ("editar".equals(accion)) {
            try {
                int personalizacionId = Integer.parseInt(request.getParameter("personalizacionId"));
                String categoria = request.getParameter("categoria");
                String descripcion = request.getParameter("descripcion");
                String materialTela = request.getParameter("materialTela");

                if (categoria == null || categoria.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "Debes seleccionar una categoría");
                    response.sendRedirect("Public/client/personalizar-arreglo.jsp?id=" + personalizacionId);
                    return;
                }

                String imagenReferencia = procesarImagen(request);

                Personalizacion personalizacion = new Personalizacion(
                        usuario.getId(), categoria, descripcion, materialTela, imagenReferencia);
                personalizacion.setPersonalizacionId(personalizacionId);

                boolean actualizado = personalizacionDAO.actualizarPersonalizacion(personalizacion);

                if (actualizado) {
                    response.sendRedirect("Public/client/mis-arreglos.jsp?editado=1");
                } else {
                    session.setAttribute("errorPersonalizacion", "No se pudo actualizar la personalización.");
                    response.sendRedirect("Public/client/personalizar-arreglo.jsp?id=" + personalizacionId);
                }

            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Error: " + e.getMessage());
                response.sendRedirect("Public/client/personalizar-arreglo.jsp");
            }

        } else {
            session.setAttribute("errorPersonalizacion", "Acción no válida");
            response.sendRedirect("Public/client/personalizar-arreglo.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // ─── ELIMINAR ─────────────────────────────────────────────
        if ("eliminar".equals(accion)) {
            try {
                int personalizacionId = Integer.parseInt(request.getParameter("id"));

                boolean eliminada = personalizacionDAO.eliminarPersonalizacion(
                        personalizacionId, usuario.getId());

                if (eliminada) {
                    response.sendRedirect("Public/client/mis-arreglos.jsp?eliminado=1");
                } else {
                    session.setAttribute("errorPersonalizacion", "No se pudo eliminar.");
                    response.sendRedirect("Public/client/mis-arreglos.jsp");
                }

            } catch (NumberFormatException e) {
                session.setAttribute("errorPersonalizacion", "ID inválido");
                response.sendRedirect("Public/client/mis-arreglos.jsp");
            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Error: " + e.getMessage());
                response.sendRedirect("Public/client/mis-arreglos.jsp");
            }

        } else {
            response.sendRedirect("Public/client/mis-arreglos.jsp");
        }
    }

    // ─── MÉTODO AUXILIAR: procesar imagen subida ──────────────────
    private String procesarImagen(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("imagenReferencia");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("")
                    + File.separator + "Assets"
                    + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdirs();
            filePart.write(uploadPath + File.separator + fileName);
            return "Assets/uploads/" + fileName;
        }
        return null;
    }
}