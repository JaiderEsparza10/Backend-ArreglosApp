package model;

import java.time.LocalDateTime;

/**
 * Modelo de Datos: Favorito / Selección.
 * Representa la asociación persistente entre un usuario y un servicio de su interés.
 * 
 * @author Antigravity - Senior Architect
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
     * Constructor para mapeo rápido de selecciones.
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

    // Getters y Setters con encapsulamiento
    public int getFavoritoId() { return favoritoId; }
    public void setFavoritoId(int favoritoId) { this.favoritoId = favoritoId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getArregloId() { return arregloId; }
    public void setArregloId(int arregloId) { this.arregloId = arregloId; }

    // Método de compatibilidad para la nueva arquitectura
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
