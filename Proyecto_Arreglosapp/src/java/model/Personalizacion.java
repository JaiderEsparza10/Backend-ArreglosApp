package model;

import java.time.LocalDateTime;

/**
 * Modelo de Datos: Personalización.
 * Contiene las especificaciones técnicas y visuales de un arreglo a medida.
 */
public class Personalizacion {
    private int personalizacionId;
    private int userId;
    private Integer arregloId;
    
    // ✅ PASO 1: Declarar la variable que faltaba
    private Integer categoriaId; 
    
    private String categoria; // Para el nombre descriptivo (ej: "Sastrería") del JOIN
    private String descripcion;
    private String materialTela;
    private String imagenReferencia;
    private String estado; // pendiente, en_proceso, completado
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Personalizacion() {
    }

    /**
     * Constructor actualizado para incluir el categoriaId.
     */
    public Personalizacion(int userId, Integer arregloId, Integer categoriaId, String descripcion, String materialTela,
            String imagenReferencia) {
        this.userId = userId;
        this.arregloId = arregloId;
        this.categoriaId = categoriaId; // ✅ Asignar el ID
        this.descripcion = descripcion;
        this.materialTela = materialTela;
        this.imagenReferencia = imagenReferencia;
        this.estado = "pendiente";
    }

    // Getters y Setters
    public int getPersonalizacionId() { return personalizacionId; }
    public void setPersonalizacionId(int personalizacionId) { this.personalizacionId = personalizacionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getArregloId() { return arregloId; }
    public void setArregloId(Integer arregloId) { this.arregloId = arregloId; }

    // ✅ PASO 2: Estos métodos ahora funcionarán porque la variable existe
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }

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