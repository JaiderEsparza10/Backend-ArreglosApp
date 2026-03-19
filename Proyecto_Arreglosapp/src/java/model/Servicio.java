package model;

/**
 * Modelo de Datos: Servicio.
 * Define la estructura de los servicios base del sistema.
 * Jerarquía: Servicios → Personalizaciones → Arreglos
 * 
 * @author Arquitecto de Software - DBA
 */
public class Servicio {
    private int servicioId;
    private String servicioNombre;
    private String servicioDescripcion;
    private double servicioPrecioBase;
    private int servicioTiempoEstimado;
    private boolean servicioActivo;
    
    // Campos de auditoría
    private String fechaCreacion;
    
    // Campo para imagen (compatibilidad)
    private String imagenUrl;
    // ================================================================
    
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

    // ================================================================
    // Métodos de conveniencia para compatibilidad
    // ================================================================
    
    /**
     * Método de compatibilidad para código existente
     */
    public String getNombre() { return servicioNombre; }
    public void setNombre(String nombre) { this.servicioNombre = nombre; }
    
    /**
     * Método de compatibilidad para código existente
     */
    public String getDescripcion() { return servicioDescripcion; }
    public void setDescripcion(String descripcion) { this.servicioDescripcion = descripcion; }
    
    /**
     * Método de compatibilidad para código existente
     */
    public double getPrecioBase() { return servicioPrecioBase; }
    public void setPrecioBase(double precioBase) { this.servicioPrecioBase = precioBase; }
    
    /**
     * Método de compatibilidad para código existente
     */
    public int getTiempoEstimado() { return servicioTiempoEstimado; }
    public void setTiempoEstimado(int tiempoEstimado) { this.servicioTiempoEstimado = tiempoEstimado; }
    
    /**
     * Método de compatibilidad para código existente
     */
    public boolean isDisponible() { return servicioActivo; }
    public void setDisponible(boolean disponible) { this.servicioActivo = disponible; }
    
    /**
     * Método de compatibilidad para código existente (mapeo de IDs)
     */
    public int getArregloId() { return servicioId; }
    public void setArregloId(int arregloId) { this.servicioId = arregloId; }
    
    /**
     * Método de compatibilidad para imagenUrl (para código existente)
     */
    public String getImagenUrl() { return imagenUrl != null ? imagenUrl : "Assets/image/imagen-sastreria.jpg"; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}