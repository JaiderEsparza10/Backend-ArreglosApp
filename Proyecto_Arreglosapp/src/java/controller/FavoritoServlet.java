/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: FavoritoServlet.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Controlador asíncrono de pre-selección (Wishlist).
 *               Permite la interacción dinámica mediante JSON para agregar,
 *               eliminar y listar servicios de interés del usuario.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package controller;

import dao.FavoritoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Favorito;
import model.Usuario;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Este controlador utiliza respuestas JSON para permitir que los usuarios marquen servicios de interés sin recargar la página.
 */
@WebServlet("/FavoritoServlet")
public class FavoritoServlet extends HttpServlet {

    private FavoritoDAO favoritoDAO = new FavoritoDAO();
    private Gson gson = new Gson(); // Utilizado para la serialización de respuestas REST-like

    /**
     * Procesa acciones POST para la persistencia de favoritos.
     * Soporta las acciones 'agregar' y 'eliminar'.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuración de cabeceras para interoperabilidad con llamadas AJAX
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Validación de estado de autenticación (Capa de Seguridad de Aplicación)
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(gson.toJson(new ResponseData(false, "Debes iniciar sesión para agregar favoritos")));
            return;
        }

        String accion = request.getParameter("accion");

        if ("agregar".equals(accion)) {
            try {
                // Extracción y parseo de metadatos del servicio seleccionado
                String servicioIdStr = request.getParameter("servicioId");
                String servicio = request.getParameter("servicio");
                String nombreServicio = request.getParameter("nombreServicio");
                String precioStr = request.getParameter("precio");
                int servicioId = 0;
                double precio = 0;
                if (servicioIdStr != null && !servicioIdStr.isEmpty()) {
                    servicioId = Integer.parseInt(servicioIdStr);
                }
                if (precioStr != null && !precioStr.isEmpty()) {
                    precio = Double.parseDouble(precioStr);
                }
                String imagenUrl = request.getParameter("imagenUrl");

                // Regla de Negocio: Evitar duplicidad de un mismo servicio en la selección del cliente
                if (favoritoDAO.existeFavoritoPorServicio(usuario.getId(), servicioId)) {
                    out.print(gson.toJson(new ResponseData(false, "Este servicio ya está en tu selección")));
                    return;
                }

                // Mapeo a objeto de transferencia para persistencia
                Favorito favorito = new Favorito(usuario.getId(), servicioId, servicio, nombreServicio, precio,
                        imagenUrl);
                boolean agregado = favoritoDAO.agregarFavorito(favorito);

                if (agregado) {
                    out.print(gson.toJson(new ResponseData(true, "Agregado a tu selección correctamente")));
                } else {
                    out.print(gson.toJson(new ResponseData(false, "No se pudo agregar a la selección")));
                }

            } catch (NumberFormatException e) {
                out.print(gson.toJson(new ResponseData(false, "Datos inválidos")));
            } catch (Exception e) {
                out.print(gson.toJson(new ResponseData(false, "Error: " + e.getMessage())));
            }

        } else if ("eliminar".equals(accion)) {
            try {
                // Borrado físico del registro de favorito/selección
                String favoritoIdStr = request.getParameter("favoritoId");
                if (favoritoIdStr != null && !favoritoIdStr.isEmpty()) {
                    int favoritoId = Integer.parseInt(favoritoIdStr);
                    boolean eliminado = favoritoDAO.eliminarFavorito(favoritoId, usuario.getId());

                    if (eliminado) {
                        out.print(gson.toJson(new ResponseData(true, "Eliminado de tu selección correctamente")));
                    } else {
                        out.print(gson.toJson(new ResponseData(false, "No se pudo eliminar")));
                    }
                } else {
                    out.print(gson.toJson(new ResponseData(false, "ID inválido")));
                }

            } catch (NumberFormatException e) {
                out.print(gson.toJson(new ResponseData(false, "ID inválido")));
            } catch (Exception e) {
                out.print(gson.toJson(new ResponseData(false, "Error: " + e.getMessage())));
            }

        } else {
            out.print(gson.toJson(new ResponseData(false, "Acción no válida")));
        }
    }

    /**
     * Consulta el listado completo de favoritos del usuario autenticado.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(gson.toJson(new ResponseData(false, "Debes iniciar sesión")));
            return;
        }

        try {
            // Recuperación de datos desde la capa DAO y serialización a JSON
            List<Favorito> favoritos = favoritoDAO.obtenerFavoritosPorUsuario(usuario.getId());
            out.print(gson.toJson(new ResponseData(true, "Favoritos obtenidos", favoritos)));
        } catch (Exception e) {
            out.print(gson.toJson(new ResponseData(false, "Error: " + e.getMessage())));
        }
    }

    /**
     * Estructura interna para el intercambio de datos entre el backend y el cliente.
     */
    private static class ResponseData {
        boolean success;
        String message;
        Object data;

        public ResponseData(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public ResponseData(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }
}