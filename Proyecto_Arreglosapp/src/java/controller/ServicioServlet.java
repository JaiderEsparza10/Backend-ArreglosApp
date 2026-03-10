package controller;

import dao.ServicioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Servicio;
import model.Usuario;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/ServicioServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 25)
public class ServicioServlet extends HttpServlet {

    private ServicioDAO servicioDAO;

    @Override
    public void init() throws ServletException {
        servicioDAO = new ServicioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario admin = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (admin == null || admin.getRolId() != 1) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            try {
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String precioStr = request.getParameter("precio");
                String tiempoEst = request.getParameter("tiempoEstimado");

                if (nombre == null || nombre.trim().isEmpty()) {
                    session.setAttribute("errorServicio", "El nombre es obligatorio");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }

                double precio = 0;
                try {
                    precio = Double.parseDouble(precioStr.replace(",", ".").trim());
                } catch (NumberFormatException e) {
                    precio = 0;
                }

                String imagenUrl = procesarImagen(request);

                Servicio s = new Servicio();
                s.setNombre(nombre.trim());
                s.setDescripcion(descripcion);
                s.setPrecioBase(precio);
                s.setTiempoEstimado(tiempoEst);
                s.setImagenUrl(imagenUrl);

                boolean creado = servicioDAO.crearServicio(s);

                if (creado) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?creado=1");
                } else {
                    session.setAttribute("errorServicio", "No se pudo crear el servicio");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                }

            } catch (Exception e) {
                session.setAttribute("errorServicio", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
            }

        } else if ("editar".equals(accion)) {
            try {
                int arregloId = Integer.parseInt(request.getParameter("arregloId"));
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String precioStr = request.getParameter("precio");
                String tiempoEst = request.getParameter("tiempoEstimado");

                double precio = 0;
                try {
                    precio = Double.parseDouble(precioStr.replace(",", ".").trim());
                } catch (NumberFormatException e) {
                    precio = 0;
                }

                String imagenUrl = procesarImagen(request);

                Servicio s = new Servicio();
                s.setArregloId(arregloId);
                s.setNombre(nombre.trim());
                s.setDescripcion(descripcion);
                s.setPrecioBase(precio);
                s.setTiempoEstimado(tiempoEst);
                s.setImagenUrl(imagenUrl);

                boolean actualizado = servicioDAO.actualizarServicio(s);

                if (actualizado) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?editado=1");
                } else {
                    session.setAttribute("errorServicio", "No se pudo actualizar el servicio");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp?id=" + arregloId);
                }

            } catch (Exception e) {
                session.setAttribute("errorServicio", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp");
            }

        } else if ("eliminar".equals(accion)) {
            try {
                int arregloId = Integer.parseInt(request.getParameter("arregloId"));
                servicioDAO.eliminarServicio(arregloId);
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?eliminado=1");
            } catch (Exception e) {
                session.setAttribute("errorServicio", "Error al eliminar: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp");
            }
        }
    }

    private String procesarImagen(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("imagen");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("") + File.separator + "Assets" + File.separator
                    + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdirs();
            filePart.write(uploadPath + File.separator + fileName);
            return "Assets/uploads/" + fileName;
        }
        return null;
    }
}