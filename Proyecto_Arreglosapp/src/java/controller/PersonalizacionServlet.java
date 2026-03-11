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
 * Controlador de Personalización de Arreglos.
 * RF-06: Personalización de Servicios.
 * Permite a los usuarios definir especificaciones técnicas para sus prendas (material, descripción) 
 * y adjuntar imágenes de referencia.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/PersonalizacionServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class PersonalizacionServlet extends HttpServlet {

    private PersonalizacionDAO personalizacionDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización de la capa de persistencia para personalizaciones
        personalizacionDAO = new PersonalizacionDAO();
    }

    /**
     * Procesa la creación y edición de especificaciones de arreglo.
     * Gestiona el flujo de subida de archivos (imágenes) y datos de formulario.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // Regla de Seguridad: Verificación de sesión de cliente activa
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // ─── CREAR NUEVA PERSONALIZACIÓN ──────────────────────────────
        if ("crear".equals(accion)) {
            try {
                // Extracción de metadatos descriptivos
                String categoria = request.getParameter("categoria");
                String descripcion = request.getParameter("descripcion");
                String materialTela = request.getParameter("materialTela");

                // Validación de integridad obligatoria
                if (categoria == null || categoria.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "Debes seleccionar una categoría");
                    response.sendRedirect("Public/client/personalizar-arreglo.jsp");
                    return;
                }

                // Procesamiento de archivo binario (Imagen de referencia)
                String imagenReferencia = procesarImagen(request);

                // Mapeo al modelo de datos
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

            // ─── EDITAR PERSONALIZACIÓN EXISTENTE ────────────────────────
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

                // Actualización de imagen (opcional en edición)
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

    /**
     * Procesa solicitudes GET, principalmente para la eliminación de registros.
     */
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

        // ─── ELIMINAR PERSONALIZACIÓN ─────────────────────────────────
        if ("eliminar".equals(accion)) {
            try {
                int personalizacionId = Integer.parseInt(request.getParameter("id"));

                // Borrado físico (Cascade Delete no implementado o evitado por seguridad)
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

    /**
     * Método Auxiliar: Gestiona el almacenamiento físico de imágenes en el servidor.
     * RNF: Persistencia de Medios.
     */
    private String procesarImagen(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("imagenReferencia");
        if (filePart != null && filePart.getSize() > 0) {
            // Generación de ruta absoluta en el sistema de archivos del servidor
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("")
                    + File.separator + "Assets"
                    + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdirs(); // Creación recursiva de directorios si no existen
            
            // Escritura del binario
            filePart.write(uploadPath + File.separator + fileName);
            return "Assets/uploads/" + fileName; // Retorno de ruta relativa para almacenamiento en DB
        }
        return null;
    }
}