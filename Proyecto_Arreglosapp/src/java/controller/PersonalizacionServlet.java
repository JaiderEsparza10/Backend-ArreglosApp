/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: PersonalizacionServlet.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Motor de requerimientos técnicos y diseño (Personalizaciones).
 *               Gestiona la carga de archivos multimedia, validación de 
 *               especificaciones de tela y mapeo de servicios especializados.
 * ══════════════════════════════════════════════════════════════════════════════
 */
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
 * Controlador (Servlet) que gestiona la creación y edición de personalizaciones.
 * Soporta la carga de imágenes (Multipart) y la definición de detalles técnicos.
 */
@WebServlet("/PersonalizacionServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 5,    // 5MB
    maxRequestSize = 1024 * 1024 * 25 // 25MB
)
public class PersonalizacionServlet extends HttpServlet {

    private PersonalizacionDAO personalizacionDAO;

    @Override
    public void init() throws ServletException {
        personalizacionDAO = new PersonalizacionDAO();
    }

    /**
     * Procesa el envío de formularios de personalización (crear/editar).
     * Implementa lógica de negocio para la validación de servicios y carga de archivos.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Aseguramos que el procesamiento de caracteres sea correcto antes de leer parámetros
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if ("crear".equals(accion) || "editar".equals(accion)) {
            try {
                // 1. CAPTURA DE SERVICIO
                Integer servicioId = null;
                String servicioParam = request.getParameter("idServicio");
                String servicioNombre = "";
                
                if (servicioParam == null || servicioParam.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "Debe seleccionar un tipo de servicio");
                    response.sendRedirect(request.getContextPath() + "/Public/client/personalizar-arreglo.jsp");
                    return;
                }
                
                try {
                    servicioId = Integer.parseInt(servicioParam);
                    // Mapeo de ID a nombre de servicio para compatibilidad con el motor actual
                    switch (servicioId) {
                        case 1:
                            servicioNombre = "Sastreria";
                            break;
                        case 2:
                            servicioNombre = "Costuras";
                            break;
                        case 3:
                            servicioNombre = "Planchado";
                            break;
                        case 4:
                            servicioNombre = "Arreglos de Medidas";
                            break;
                        default:
                            servicioNombre = "Otro";
                            break;
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("errorPersonalizacion", "El valor de servicio no es válido");
                    redireccionarError(request, response, accion);
                    return;
                }

                // 2. CAPTURA DE METADATOS CON VALIDACIÓN
                String descripcion = request.getParameter("descripcion");
                String materialTela = request.getParameter("materialTela");
                
                if (descripcion == null || descripcion.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "La descripción es obligatoria.");
                    redireccionarError(request, response, accion);
                    return;
                }

                if (materialTela == null) {
                    materialTela = "No especificado";
                }
                
                Integer arregloId = null;
                String arregloIdParam = request.getParameter("arregloId");
                if (arregloIdParam != null && !arregloIdParam.trim().isEmpty()) {
                    try {
                        arregloId = Integer.parseInt(arregloIdParam.trim());
                    } catch (NumberFormatException e) {
                        arregloId = null;
                    }
                }

                // 3. PROCESAMIENTO DE ARCHIVO MULTIMEDIA (Imagen de referencia)
                String imagenReferencia = procesarImagen(request);

                // 4. CONSTRUCCIÓN DEL OBJETO DE NEGOCIO
                Personalizacion personalizacion = new Personalizacion(
                        usuario.getId(), 
                        arregloId, 
                        servicioId,  
                        descripcion, 
                        materialTela, 
                        imagenReferencia
                );
                personalizacion.setServicio(servicioNombre);

                boolean resultado = false;
                if ("crear".equals(accion)) {
                    resultado = personalizacionDAO.crearPersonalizacion(personalizacion);
                } else if ("editar".equals(accion)) {
                    String pIdParam = request.getParameter("personalizacionId");
                    if (pIdParam != null && !pIdParam.trim().isEmpty()) {
                        personalizacion.setPersonalizacionId(Integer.parseInt(pIdParam));
                        resultado = personalizacionDAO.actualizarPersonalizacion(personalizacion);
                    } else {
                        throw new Exception("ID de personalización no proporcionado");
                    }
                }

                // 5. FLUJO DE RETORNO TRAS ÉXITO
                if (resultado) {
                    String msg = "crear".equals(accion) ? "creado=1" : "editado=1";
                    response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?" + msg);
                } else {
                    throw new Exception("Error en la persistencia de datos.");
                }

            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Ocurrió un error: " + e.getMessage());
                redireccionarError(request, response, accion);
            }
        } else {
            session.setAttribute("errorPersonalizacion", "Acción no válida");
            response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp");
        }
    }

    /**
     * Canaliza redirecciones de error manteniendo el contexto de edición si aplica.
     */
    private void redireccionarError(HttpServletRequest request, HttpServletResponse response, String accion) throws IOException {
        String idEdit = request.getParameter("personalizacionId");
        String path = "/Public/client/personalizar-arreglo.jsp";
        if ("editar".equals(accion) && idEdit != null) {
            path += "?id=" + idEdit;
        }
        response.sendRedirect(request.getContextPath() + path);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // Acción de eliminación (Baja física/lógica de requerimiento)
        if ("eliminar".equals(accion)) {
            try {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.trim().isEmpty()) {
                    int id = Integer.parseInt(idParam);
                    if (personalizacionDAO.eliminarPersonalizacion(id, usuario.getId())) {
                        response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?eliminado=1");
                    } else {
                        session.setAttribute("errorPersonalizacion", "No se pudo eliminar");
                        response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=1");
                    }
                }
            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Error: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=1");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp");
        }
    }

    /**
     * Gestiona la escritura de archivos en el sistema de ficheros del servidor.
     */
    private String procesarImagen(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("imagenReferencia");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // Ofuscación temporal para evitar colisiones nominales
                fileName = System.currentTimeMillis() + "_" + fileName;
                
                String uploadPath = getServletContext().getRealPath("") + File.separator + "Assets" + File.separator + "uploads";
                
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                filePart.write(uploadPath + File.separator + fileName);
                return "Assets/uploads/" + fileName;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}