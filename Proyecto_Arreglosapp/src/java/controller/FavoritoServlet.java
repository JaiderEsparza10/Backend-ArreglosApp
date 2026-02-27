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

@WebServlet("/FavoritoServlet")
public class FavoritoServlet extends HttpServlet {

    private FavoritoDAO favoritoDAO = new FavoritoDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            // Verificar si el usuario está autenticado
            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            
            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(gson.toJson(new ResponseData(false, "Debes iniciar sesión para agregar favoritos")));
                return;
            }
            
            String accion = request.getParameter("accion");
            
            if ("agregar".equals(accion)) {
                String arregloIdStr = request.getParameter("arregloId");
                String categoria = request.getParameter("categoria");
                String nombreCategoria = request.getParameter("nombreCategoria");
                String precioStr = request.getParameter("precio");
                String imagenUrl = request.getParameter("imagenUrl");
                
                try {
                    int arregloId = Integer.parseInt(arregloIdStr);
                    
                    // Verificar si ya existe el favorito
                    if (favoritoDAO.existeFavoritoPorArreglo(usuario.getId(), arregloId)) {
                        out.print(gson.toJson(new ResponseData(false, "Este servicio ya está en tus favoritos")));
                        return;
                    }
                    
                    // Crear nuevo favorito
                    double precio = Double.parseDouble(precioStr);
                    Favorito favorito = new Favorito(usuario.getId(), arregloId, categoria, nombreCategoria, precio, imagenUrl);
                    
                    boolean agregado = favoritoDAO.agregarFavorito(favorito);
                    
                    if (agregado) {
                        out.print(gson.toJson(new ResponseData(true, "Favorito agregado correctamente")));
                    } else {
                        out.print(gson.toJson(new ResponseData(false, "No se pudo agregar el favorito")));
                    }
                    
                } catch (NumberFormatException e) {
                    out.print(gson.toJson(new ResponseData(false, "Datos inválidos")));
                } catch (Exception e) {
                    out.print(gson.toJson(new ResponseData(false, "Error: " + e.getMessage())));
                }
            } else if ("eliminar".equals(accion)) {
                String favoritoIdStr = request.getParameter("favoritoId");
                
                try {
                    int favoritoId = Integer.parseInt(favoritoIdStr);
                    
                    boolean eliminado = favoritoDAO.eliminarFavorito(favoritoId, usuario.getId());
                    
                    if (eliminado) {
                        out.print(gson.toJson(new ResponseData(true, "Favorito eliminado correctamente")));
                    } else {
                        out.print(gson.toJson(new ResponseData(false, "No se pudo eliminar el favorito")));
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
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            
            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(gson.toJson(new ResponseData(false, "Debes iniciar sesión")));
                return;
            }
            
            try {
                // Obtener favoritos del usuario
                var favoritos = favoritoDAO.obtenerFavoritosPorUsuario(usuario.getId());
                out.print(gson.toJson(new ResponseData(true, "Favoritos obtenidos", favoritos)));
                
            } catch (Exception e) {
                out.print(gson.toJson(new ResponseData(false, "Error: " + e.getMessage())));
            }
        }
    }
    
    // Clase para respuestas JSON
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
