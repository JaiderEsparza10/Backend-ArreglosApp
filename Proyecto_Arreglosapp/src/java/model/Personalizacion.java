/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar los detalles técnicos y de diseño para la personalización de los arreglos solicitados por los usuarios.
 */
package model;

import java.time.LocalDateTime;

/**
 * Entidad que almacena las preferencias detalladas de un ajuste o diseño a medida.
 */
public class Personalizacion {
    private int personalizacionId;
    private int userId;
    private Integer arregloId;
    private Integer servicioId; 
    private String servicio; 
    private String descripcion;
    private String materialTela;
    private String imagenReferencia;
    private String estado; // Posibles: pendiente, en_proceso, completado
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Personalizacion() {
    }

    /**
     * Constructor para registrar una nueva solicitud de personalización.
     * 
     * @param userId Identificador del usuario propietario.
     * @param arregloId Identificador del arreglo base.
     * @param servicioId Identificador del servicio relacionado.
     * @param descripcion Detalle textual de lo que el usuario desea.
     * @param materialTela Tipo de tela o material especificado.
     * @param imagenReferencia Ruta de la imagen proporcionada como guía.
     */
    public Personalizacion(int userId, Integer arregloId, Integer servicioId, String descripcion, String materialTela,
            String imagenReferencia) {
        this.userId = userId;
        this.arregloId = arregloId;
        this.servicioId = servicioId; 
        this.descripcion = descripcion;
        this.materialTela = materialTela;
        this.imagenReferencia = imagenReferencia;
        this.estado = "pendiente"; // Estado inicial automático
    }

    // Métodos para la gestión segura de los atributos de la entidad
    public int getPersonalizacionId() { return personalizacionId; }
    public void setPersonalizacionId(int personalizacionId) { this.personalizacionId = personalizacionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getArregloId() { return arregloId; }
    public void setArregloId(Integer arregloId) { this.arregloId = arregloId; }

    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

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