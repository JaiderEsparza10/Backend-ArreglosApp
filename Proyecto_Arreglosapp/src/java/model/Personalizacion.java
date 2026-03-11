package model;

import java.time.LocalDateTime;

/**
 * Modelo de Datos: Personalización.
 * Contiene las especificaciones técnicas y visuales de un arreglo a medida.
 * 
 * @author Antigravity - Senior Architect
 */
public class Personalizacion {
    private int personalizacionId;
    private int userId;
    private String categoria;
    private String descripcion;
    private String materialTela;
    private String imagenReferencia;
    private String estado; // pendiente, en_proceso, completado
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Personalizacion() {
    }

    /**
     * Constructor para nuevas solicitudes de personalización.
     */
    public Personalizacion(int userId, String categoria, String descripcion, String materialTela,
            String imagenReferencia) {
        this.userId = userId;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.materialTela = materialTela;
        this.imagenReferencia = imagenReferencia;
        this.estado = "pendiente";
    }

    // Getters y Setters con encapsulamiento
    public int getPersonalizacionId() { return personalizacionId; }
    public void setPersonalizacionId(int personalizacionId) { this.personalizacionId = personalizacionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getMaterialTela() { return materialTela; }
    public void setMaterialTela(String materialTela) { this.materialTela = materialTela; }

    public String getImagenReferencia() { return imagenReferencia; }
    public void setImagenReferencia(String imagenReferencia) { this.imagenReferencia = imagenReferencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
