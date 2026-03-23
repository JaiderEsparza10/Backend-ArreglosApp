/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Definir la entidad Servicio que representa los trabajos de costura y arreglos disponibles.
 */
package model;

/**
 * Estructura de datos para los servicios base ofrecidos por el taller.
 */
public class Servicio {
    private int servicioId;
    private String servicioNombre;
    private String servicioDescripcion;
    private double servicioPrecioBase;
    private int servicioTiempoEstimado;
    private boolean servicioActivo;
    
    // Campos para auditoría e identificación visual
    private String fechaCreacion;
    private String imagenUrl;
    
    public int getServicioId() { return servicioId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public String getServicioDescripcion() { return servicioDescripcion; }
    public void setServicioDescripcion(String servicioDescripcion) { this.servicioDescripcion = servicioDescripcion; }

    public double getServicioPrecioBase() { return servicioPrecioBase; }
    public void setServicioPrecioBase(double servicioPrecioBase) { this.servicioPrecioBase = servicioPrecioBase; }

    public int getServicioTiempoEstimado() { return servicioTiempoEstimado; }
    public void setServicioTiempoEstimado(int servicioTiempoEstimado) { this.servicioTiempoEstimado = servicioTiempoEstimado; }

    public boolean isServicioActivo() { return servicioActivo; }
    public void setServicioActivo(boolean servicioActivo) { this.servicioActivo = servicioActivo; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Métodos de compatibilidad para asegurar el funcionamiento con código heredado
    
    public String getNombre() { return servicioNombre; }
    public void setNombre(String nombre) { this.servicioNombre = nombre; }
    
    public String getDescripcion() { return servicioDescripcion; }
    public void setDescripcion(String descripcion) { this.servicioDescripcion = descripcion; }
    
    public double getPrecioBase() { return servicioPrecioBase; }
    public void setPrecioBase(double precioBase) { this.servicioPrecioBase = precioBase; }
    
    public int getTiempoEstimado() { return servicioTiempoEstimado; }
    public void setTiempoEstimado(int tiempoEstimado) { this.servicioTiempoEstimado = tiempoEstimado; }
    
    public boolean isDisponible() { return servicioActivo; }
    public void setDisponible(boolean disponible) { this.servicioActivo = disponible; }
    
    public int getArregloId() { return servicioId; }
    public void setArregloId(int arregloId) { this.servicioId = arregloId; }
    
    /**
     * Retorna la URL de la imagen del servicio, con una imagen por defecto si es nula.
     */
    public String getImagenUrl() { return imagenUrl != null ? imagenUrl : "Assets/image/imagen-sastreria.jpg"; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}