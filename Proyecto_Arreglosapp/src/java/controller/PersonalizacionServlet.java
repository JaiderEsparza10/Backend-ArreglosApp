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

@WebServlet("/PersonalizacionServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class PersonalizacionServlet extends HttpServlet {

    private PersonalizacionDAO personalizacionDAO = new PersonalizacionDAO();

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

        if ("crear".equals(accion)) {
            try {
                String categoria = request.getParameter("categoria");
                String descripcion = request.getParameter("descripcion");
                String materialTela = request.getParameter("materialTela");

                // Validar campo obligatorio ANTES de procesar imagen
                if (categoria == null || categoria.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "Debes seleccionar una categoría");
                    response.sendRedirect("Public/client/personalizar-arreglo.jsp");
                    return;
                }

                // Procesar imagen si fue subida
                String imagenReferencia = null;
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
                    imagenReferencia = "Assets/uploads/" + fileName;
                }

                Personalizacion personalizacion = new Personalizacion(
                        usuario.getId(),
                        categoria,
                        descripcion,
                        materialTela,
                        imagenReferencia);

                boolean creada = personalizacionDAO.crearPersonalizacion(personalizacion);

                if (creada) {
                    // CORREGIDO: usar session para que el mensaje sobreviva el redirect
                    session.setAttribute("mensajePersonalizacion",
                            "Personalización creada correctamente. Nos pondremos en contacto contigo pronto.");
                } else {
                    session.setAttribute("errorPersonalizacion",
                            "No se pudo crear la personalización. Intenta nuevamente.");
                }

                response.sendRedirect("Public/client/mis-arreglos.jsp");

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

        if ("eliminar".equals(accion)) {
            try {
                int personalizacionId = Integer.parseInt(request.getParameter("id"));
                boolean eliminada = personalizacionDAO.eliminarPersonalizacion(personalizacionId, usuario.getId());

                if (eliminada) {
                    session.setAttribute("mensajePersonalizacion", "Personalización eliminada correctamente");
                } else {
                    session.setAttribute("errorPersonalizacion", "No se pudo eliminar la personalización");
                }

            } catch (NumberFormatException e) {
                session.setAttribute("errorPersonalizacion", "ID inválido");
            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Error: " + e.getMessage());
            }

            response.sendRedirect("Public/client/mis-arreglos.jsp");
        }
    }
}