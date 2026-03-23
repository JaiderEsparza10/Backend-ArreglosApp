/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Administrar la creación y edición de solicitudes de personalización de arreglos.
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
 * Gestiona el envío de formularios con archivos multimedia para detallar los requerimientos específicos de un ajuste de prenda.
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
                
                // Depuración: Imprimir el valor recibido
                System.out.println("DEBUG: servicioParam recibido = '" + servicioParam + "'");
                
                if (servicioParam == null || servicioParam.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "Debe seleccionar un tipo de servicio");
                    response.sendRedirect(request.getContextPath() + "/Public/client/personalizar-arreglo.jsp");
                    return;
                }
                
                if (servicioParam != null && !servicioParam.trim().isEmpty()) {
                    try {
                        servicioId = Integer.parseInt(servicioParam);
                        // Convertir ID a nombre de servicio para BD actual
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
                        System.out.println("DEBUG: servicio convertido a = '" + servicioNombre + "'");
                    } catch (NumberFormatException e) {
                        System.out.println("DEBUG: Error al convertir servicioParam a número: " + servicioParam);
                        session.setAttribute("errorPersonalizacion", "El valor de servicio no es válido: " + servicioParam);
                        redireccionarError(request, response, accion);
                        return;
                    }
                } else {
                    System.out.println("DEBUG: servicioParam es nulo o vacío");
                    session.setAttribute("errorPersonalizacion", "Debes seleccionar un servicio obligatoriamente. (Valor recibido: " + servicioParam + ")");
                    redireccionarError(request, response, accion);
                    return;
                }

                // 2. CAPTURA DE METADATOS CON VALIDACIÓN (Meta 1)
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

                // 3. PROCESAR IMAGEN
                String imagenReferencia = procesarImagen(request);

                // 4. MAPEO AL MODELO
                System.out.println("DEBUG: Creando objeto Personalizacion...");
                Personalizacion personalizacion = new Personalizacion(
                        usuario.getId(), 
                        arregloId, 
                        servicioId,  
                        descripcion, 
                        materialTela, 
                        imagenReferencia
                );
                // Establecer el nombre del servicio para la BD actual
                personalizacion.setServicio(servicioNombre);
                System.out.println("DEBUG: Objeto Personalizacion creado con servicio: " + servicioNombre);

                boolean resultado = false;
                if ("crear".equals(accion)) {
                    System.out.println("DEBUG: Intentando crear personalización en BD...");
                    resultado = personalizacionDAO.crearPersonalizacion(personalizacion);
                    System.out.println("DEBUG: Resultado de creación: " + resultado);
                } else if ("editar".equals(accion)) {
                    System.out.println("DEBUG: Modo edición - obteniendo ID...");
                    String pIdParam = request.getParameter("personalizacionId");
                    if (pIdParam != null && !pIdParam.trim().isEmpty()) {
                        try {
                            personalizacion.setPersonalizacionId(Integer.parseInt(pIdParam));
                            System.out.println("DEBUG: Actualizando personalización ID: " + pIdParam);
                            resultado = personalizacionDAO.actualizarPersonalizacion(personalizacion);
                            System.out.println("DEBUG: Resultado de actualización: " + resultado);
                        } catch (NumberFormatException e) {
                            throw new Exception("ID de personalización inválido");
                        }
                    } else {
                        throw new Exception("ID de personalización no proporcionado para edición");
                    }
                }

                // 5. REDIRECCIÓN DE ÉXITO
                if (resultado) {
                    String msg = "crear".equals(accion) ? "creado=1" : "editado=1";
                    String redirectUrl = request.getContextPath() + "/Public/client/mis-arreglos.jsp?" + msg;
                    System.out.println("DEBUG: Redirigiendo a: " + redirectUrl);
                    response.sendRedirect(redirectUrl);
                } else {
                    System.out.println("DEBUG: La base de datos devolvió false");
                    throw new Exception("La base de datos no pudo procesar la solicitud.");
                }

            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Ocurrió un error: " + e.getMessage());
                redireccionarError(request, response, accion);
            }
        } else {
            // Acción no reconocida en POST
            session.setAttribute("errorPersonalizacion", "Acción no válida");
            response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp");
        }
    }

    // Método auxiliar para manejar redirecciones de error de forma limpia
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

        // Validación de sesión
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // Manejo de acción eliminar
        if ("eliminar".equals(accion)) {
            try {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    session.setAttribute("errorPersonalizacion", "ID de personalización no proporcionado");
                    response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=1");
                    return;
                }
                
                int id = Integer.parseInt(idParam);
                if (personalizacionDAO.eliminarPersonalizacion(id, usuario.getId())) {
                    response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?eliminado=1");
                } else {
                    session.setAttribute("errorPersonalizacion", "No se pudo eliminar la personalización");
                    response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=1");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorPersonalizacion", "ID inválido");
                response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=1");
            } catch (Exception e) {
                session.setAttribute("errorPersonalizacion", "Error al eliminar: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp?error=1");
            }
        } else {
            // Acción no reconocida
            response.sendRedirect(request.getContextPath() + "/Public/client/mis-arreglos.jsp");
        }
    }

    private String procesarImagen(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("imagenReferencia");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // Generar nombre único para evitar sobrescritura (Opcional pero recomendado)
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