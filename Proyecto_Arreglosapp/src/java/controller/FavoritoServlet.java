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
 * Controlador de Gestión de Selección de Servicios (Favoritos).
 * RF-07: Selección de Servicios.
 * Permite a los clientes pre-seleccionar servicios para su posterior personalización o agendamiento.
 * Utiliza respuestas asíncronas JSON para mejorar la experiencia de usuario reactiva.
 * 
 * @author Antigravity - Senior Architect
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
                int arregloId = Integer.parseInt(request.getParameter("arregloId"));
                String categoria = request.getParameter("categoria");
                String nombreCategoria = request.getParameter("nombreCategoria");
                double precio = Double.parseDouble(request.getParameter("precio"));
                String imagenUrl = request.getParameter("imagenUrl");

                // Regla de Negocio: Evitar duplicidad de un mismo servicio en la selección del cliente
                if (favoritoDAO.existeFavoritoPorArreglo(usuario.getId(), arregloId)) {
                    out.print(gson.toJson(new ResponseData(false, "Este servicio ya está en tu selección")));
                    return;
                }

                // Mapeo a objeto de transferencia para persistencia
                Favorito favorito = new Favorito(usuario.getId(), arregloId, categoria, nombreCategoria, precio,
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
                int favoritoId = Integer.parseInt(request.getParameter("favoritoId"));
                boolean eliminado = favoritoDAO.eliminarFavorito(favoritoId, usuario.getId());

                if (eliminado) {
                    out.print(gson.toJson(new ResponseData(true, "Eliminado de tu selección correctamente")));
                } else {
                    out.print(gson.toJson(new ResponseData(false, "No se pudo eliminar")));
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