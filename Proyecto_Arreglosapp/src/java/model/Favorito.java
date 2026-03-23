/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar la relación entre usuarios y sus servicios o arreglos seleccionados como favoritos.
 */
package model;

import java.time.LocalDateTime;

/**
 * Entidad que representa la selección persistente de un servicio por parte de un usuario.
 */
public class Favorito {
    private int favoritoId;
    private int userId;
    private int arregloId;
    private String servicio;
    private String nombreServicio;
    private double precio;
    private String imagenUrl;
    private int cantidad;
    private LocalDateTime fechaAgregado;

    public Favorito() {
    }

    /**
     * Constructor para inicializar una selección rápida de un servicio.
     * 
     * @param userId Identificador del usuario.
     * @param arregloId Identificador técnico del arreglo.
     * @param servicio Categoría general del servicio.
     * @param nombreServicio Nombre descriptivo del arreglo.
     * @param precio Valor unitario del servicio.
     * @param imagenUrl Ruta de la imagen ilustrativa.
     */
    public Favorito(int userId, int arregloId, String servicio, String nombreServicio, double precio,
            String imagenUrl) {
        this.userId = userId;
        this.arregloId = arregloId;
        this.servicio = servicio;
        this.nombreServicio = nombreServicio;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    // Métodos para el acceso y modificación de los datos encapsulados
    public int getFavoritoId() { return favoritoId; }
    public void setFavoritoId(int favoritoId) { this.favoritoId = favoritoId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getArregloId() { return arregloId; }
    public void setArregloId(int arregloId) { this.arregloId = arregloId; }

    /**
     * Proporciona compatibilidad con la nueva arquitectura de identificadores.
     */
    public int getServicioId() { return arregloId; }
    public void setServicioId(int servicioId) { this.arregloId = servicioId; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public LocalDateTime getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(LocalDateTime fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}
