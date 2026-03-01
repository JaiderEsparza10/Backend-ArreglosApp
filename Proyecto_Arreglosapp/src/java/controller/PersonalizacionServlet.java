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
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
                 maxFileSize = 1024 * 1024 * 5, 
                 maxRequestSize = 1024 * 1024 * 5 * 5)
public class PersonalizacionServlet extends HttpServlet {

    private PersonalizacionDAO personalizacionDAO = new PersonalizacionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            response.sendRedirect("../../index.jsp");
            return;
        }
        
        String accion = request.getParameter("accion");
        
        if ("crear".equals(accion)) {
            try {
                // Obtener parámetros del formulario
                String categoria = request.getParameter("categoria");
                String descripcion = request.getParameter("descripcion");
                String materialTela = request.getParameter("materialTela");
                
                String imagenReferencia = null;
                Part filePart = request.getPart("imagenReferencia");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String uploadPath = getServletContext().getRealPath("") + File.separator + "Assets" + File.separator + "uploads";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    File file = new File(uploadPath + File.separator + fileName);
                    filePart.write(file.getAbsolutePath());
                    imagenReferencia = "../../Assets/uploads/" + fileName;
                }
                
                // Validar campos obligatorios
                if (categoria == null || categoria.trim().isEmpty()) {
                    request.setAttribute("error", "Debes seleccionar una categoría");
                    request.getRequestDispatcher("personalizar-arreglo.jsp").forward(request, response);
                    return;
                }
                
                // Crear personalización
                Personalizacion personalizacion = new Personalizacion(
                    usuario.getId(), 
                    categoria, 
                    descripcion, 
                    materialTela, 
                    imagenReferencia
                );
                
                boolean creada = personalizacionDAO.crearPersonalizacion(personalizacion);
                
                if (creada) {
                    request.setAttribute("mensaje", "✅ Personalización creada correctamente. Nos pondremos en contacto contigo pronto.");
                } else {
                    request.setAttribute("error", "❌ No se pudo crear la personalización");
                }
                
                // Redirigir a mis arreglos
                response.sendRedirect("mis-arreglos.jsp");
                
            } catch (Exception e) {
                request.setAttribute("error", "❌ Error: " + e.getMessage());
                request.getRequestDispatcher("personalizar-arreglo.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Acción no válida");
            request.getRequestDispatcher("personalizar-arreglo.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            response.sendRedirect("../../index.jsp");
            return;
        }
        
        String accion = request.getParameter("accion");
        
        if ("eliminar".equals(accion)) {
            try {
                int personalizacionId = Integer.parseInt(request.getParameter("id"));
                
                boolean eliminada = personalizacionDAO.eliminarPersonalizacion(personalizacionId, usuario.getId());
                
                if (eliminada) {
                    request.setAttribute("mensaje", "✅ Personalización eliminada correctamente");
                } else {
                    request.setAttribute("error", "❌ No se pudo eliminar la personalización");
                }
                
                response.sendRedirect("mis-arreglos.jsp");
                
            } catch (NumberFormatException e) {
                request.setAttribute("error", "❌ ID inválido");
                response.sendRedirect("mis-arreglos.jsp");
            } catch (Exception e) {
                request.setAttribute("error", "❌ Error: " + e.getMessage());
                response.sendRedirect("mis-arreglos.jsp");
            }
        }
    }
}
