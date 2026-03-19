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

/**
 * Controlador de Catálogo de Servicios.
 * RF-12: Gestión de Servicios por el Administrador.
 * Administra el CRUD (Crear, Leer, Actualizar, Eliminar) del catálogo oficial de arreglos disponibles.
 * 
 * @author Antigravity - Senior Architect
 */
@WebServlet("/ServicioServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 25)
public class ServicioServlet extends HttpServlet {

    private ServicioDAO servicioDAO;

    @Override
    public void init() throws ServletException {
        // Inicialización de la capa DAO para servicios
        servicioDAO = new ServicioDAO();
    }

    /**
     * Gestiona las operaciones administrativas POST sobre el catálogo de servicios.
     * Requiere privilegios de rol Administrador (rol_id = 1).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        // Control de Acceso: Verificación obligatoria de rol administrativo
        HttpSession session = request.getSession(false);
        Usuario admin = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (admin == null || admin.getRolId() != 1) {
            response.sendRedirect("/Proyecto_Arreglosapp/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // ─── CREAR NUEVO SERVICIO ─────────────────────────────────────
        if ("crear".equals(accion)) {
            try {
                // Extracción de metadatos básicos del servicio
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String precioStr = request.getParameter("precio");
                int tiempoEst = Integer.parseInt(request.getParameter("tiempoEstimado"));

                // Validaciones de Integridad de Negocio (Backend Validation)
                if (nombre == null || nombre.trim().isEmpty()) {
                    session.setAttribute("errorServicio", "El nombre del servicio es obligatorio.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }
                
                // RNF: Regla cosmética - Nombres de servicio sin números
                if (nombre.matches(".*\\d.*")) {
                    session.setAttribute("errorServicio", "El nombre del servicio no puede contener números.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }

                if (descripcion == null || descripcion.trim().isEmpty()) {
                    session.setAttribute("errorServicio", "La descripción del servicio es obligatoria.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }
                
                if (descripcion.trim().length() < 10) {
                    session.setAttribute("errorServicio", "La descripción debe tener al menos 10 caracteres.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }

                if (precioStr == null || precioStr.trim().isEmpty()) {
                    session.setAttribute("errorServicio", "El precio base es obligatorio.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }

                // Validación de tipos y rangos de precios
                double precio = 0;
                try {
                    precio = Double.parseDouble(precioStr.replace(",", ".").trim());
                    if (precio <= 0) {
                        session.setAttribute("errorServicio", "El precio debe ser mayor a 0.");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                        return;
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("errorServicio", "El formato del precio es inválido.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }

                // VALIDACIÓN DE DUPLICADOS - Evitar error 500
                if (servicioDAO.existeNombreServicio(nombre.trim())) {
                    session.setAttribute("errorServicio", "El nombre del servicio '" + nombre.trim() + "' ya existe. Por favor, elige otro nombre.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                    return;
                }

                // Procesamiento de la iconografía/fotografía del servicio
                String imagenUrl = procesarImagen(request);

                // Crear objeto Servicio con los datos del formulario
                Servicio servicio = new Servicio();
                servicio.setServicioNombre(nombre.trim());
                servicio.setServicioDescripcion(descripcion);
                servicio.setServicioPrecioBase(precio);
                servicio.setServicioTiempoEstimado(tiempoEst);
                
                // Usar el nuevo ServicioDAO para insertar en tabla SERVICIOS
                ServicioDAO servicioDAO = new ServicioDAO();
                boolean creado = servicioDAO.crearServicio(servicio);

                if (creado) {
                    // Éxito: Notificación mediante flag URL
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?creado=1");
                } else {
                    session.setAttribute("errorServicio", "No se pudo crear el servicio");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
                }

            } catch (Exception e) {
                session.setAttribute("errorServicio", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp");
            }

        // ─── EDITAR SERVICIO EXISTENTE ────────────────────────────────
        } else if ("editar".equals(accion)) {
            try {
                // Extracción de clave primaria para actualización
                String servicioIdStr = request.getParameter("servicioId");
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String precioStr = request.getParameter("precio");
                String tiempoStr = request.getParameter("tiempoEstimado");
                int servicioId = 0;
                int tiempoEst = 0;
                if (servicioIdStr != null && !servicioIdStr.isEmpty()) {
                    servicioId = Integer.parseInt(servicioIdStr);
                }
                if (tiempoStr != null && !tiempoStr.isEmpty()) {
                    tiempoEst = Integer.parseInt(tiempoStr);
                }

                // Validaciones redundantes (Safety First)
                if (nombre == null || nombre.trim().isEmpty()) {
                    session.setAttribute("errorServicio", "El nombre del servicio es obligatorio.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp?id=" + servicioId);
                    return;
                }

                // VALIDACIÓN DE DUPLICADOS (excluyendo el servicio actual)
                if (servicioDAO.existeNombreServicioExcluyendo(nombre.trim(), servicioId)) {
                    session.setAttribute("errorServicio", "El nombre del servicio '" + nombre.trim() + "' ya existe. Por favor, elige otro nombre.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp?id=" + servicioId);
                    return;
                }

                // Validación de precio
                double precio = 0.0;
                if (precioStr != null && !precioStr.trim().isEmpty()) {
                    try {
                        precio = Double.parseDouble(precioStr.replace(",", ".").trim());
                    } catch (NumberFormatException e) {
                        session.setAttribute("errorServicio", "El precio debe ser un número válido.");
                        response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp?id=" + servicioId);
                        return;
                    }
                } else {
                    session.setAttribute("errorServicio", "El precio del servicio es obligatorio.");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp?id=" + servicioId);
                    return;
                }
                String imagenUrl = procesarImagen(request);

                Servicio s = new Servicio();
                s.setServicioId(servicioId);
                s.setServicioNombre(nombre.trim());
                s.setServicioDescripcion(descripcion);
                s.setServicioPrecioBase(precio);
                s.setServicioTiempoEstimado(tiempoEst);
                s.setImagenUrl(imagenUrl);

                boolean actualizado = servicioDAO.actualizarServicio(s);

                if (actualizado) {
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?editado=1");
                } else {
                    session.setAttribute("errorServicio", "No se pudo actualizar el servicio");
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/crear-servicio.jsp?id=" + servicioId);
                }

            } catch (Exception e) {
                session.setAttribute("errorServicio", "Error: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp");
            }

        // ─── ELIMINACIÓN DE SERVICIO ──────────────────────────────────
        } else if ("eliminar".equals(accion)) {
            try {
                String servicioIdStr = request.getParameter("arregloId");
                if (servicioIdStr != null && !servicioIdStr.isEmpty()) {
                    int servicioId = Integer.parseInt(servicioIdStr);
                    // Ejecución del borrado lógico en el catálogo
                    servicioDAO.desactivarServicio(servicioId);
                    response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp?eliminado=1");
                }
            } catch (Exception e) {
                session.setAttribute("errorServicio", "Error al eliminar: " + e.getMessage());
                response.sendRedirect("/Proyecto_Arreglosapp/Public/admin/administrador-servicios.jsp");
            }
        }
    }

    /**
     * Método Auxiliar: Centraliza el procesamiento de archivos binarios (Imágenes).
     * RNF: Gestión de Activos Multimedia.
     */
    private String procesarImagen(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("imagen");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            
            // Validar que sea una imagen
            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new Exception("El archivo debe ser una imagen válida");
            }
            
            // Generar nombre único para evitar sobrescribir
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = System.currentTimeMillis() + "_" + fileName.hashCode() + fileExtension;
            
            // Construcción dinámica de la ruta de almacenamiento local
            String uploadPath = getServletContext().getRealPath("") + File.separator + "Assets" + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Escritura física en disco del servidor
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);
            
            // Retornar ruta relativa para guardar en BD
            return "Assets/uploads/" + uniqueFileName;
        }
        return null;
    }
}