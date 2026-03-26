/**
 * Nombre del archivo: Personalizacion.java
 * Descripción breve: Clase que gestiona las preferencias y detalles técnicos de personalización de los arreglos.
 * Author: Jaider Andres Esparza — Antigravity
 * Fecha de documentación: 25 de marzo de 2026
 * Versión: 1.0
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar los detalles técnicos y de diseño para la personalización de los arreglos solicitados por los usuarios.
 */
package model;

import java.time.LocalDateTime;

/**
 * Entidad que almacena las preferencias detalladas de un ajuste o diseño a medida.
 * Esta clase permite registrar especificaciones como el tipo de tela, descripciones
 * particulares y referencias visuales para que el sastre ejecute el trabajo con precisión.
 */
public class Personalizacion {
    // Identificador único de la solicitud de personalización
    private int personalizacionId;
    // Identificador del usuario que solicita la personalización
    private int userId;
    // Identificador del arreglo base seleccionado (opcional)
    private Integer arregloId;
    // Identificador del servicio técnico relacionado
    private Integer servicioId; 
    // Nombre del servicio para visualización rápida
    private String servicio; 
    // Descripción textual de los ajustes o cambios deseados
    private String descripcion;
    // Especificación del material o tipo de tela de la prenda
    private String materialTela;
    // Ruta o URL de la imagen que sirve como referencia de diseño
    private String imagenReferencia;
    // Estado actual de la personalización: pendiente, en_proceso o completado
    private String estado; // Posibles: pendiente, en_proceso, completado
    // Marca de tiempo de la creación del registro
    private LocalDateTime fechaCreacion;
    // Marca de tiempo de la última modificación realizada
    private LocalDateTime fechaActualizacion;

    /**
     * Constructor por defecto para marcos de trabajo de persistencia.
     */
    public Personalizacion() {
    }

    /**
     * Constructor para registrar una nueva solicitud de personalización.
     * Facilita la creación de una solicitud con todos sus parámetros iniciales.
     * 
     * @param userId           Identificador del usuario propietario.
     * @param arregloId       Identificador del arreglo base (si aplica).
     * @param servicioId      Identificador del servicio relacionado.
     * @param descripcion     Detalle textual de lo que el usuario desea.
     * @param materialTela    Tipo de tela o material especificado para el trabajo.
     * @param imagenReferencia Ruta de la imagen proporcionada como guía visual.
     */
    public Personalizacion(int userId, Integer arregloId, Integer servicioId, String descripcion, String materialTela,
            String imagenReferencia) {
        this.userId = userId;
        this.arregloId = arregloId;
        this.servicioId = servicioId; 
        this.descripcion = descripcion;
        this.materialTela = materialTela;
        this.imagenReferencia = imagenReferencia;
        this.estado = "pendiente"; // Estado inicial automático asignado por negocio
    }

    // Métodos para la gestión segura de los atributos de la entidad (Encapsulamiento)

    /**
     * Obtiene el identificador único de personalización.
     * @return El ID de personalización.
     */
    public int getPersonalizacionId() { return personalizacionId; }

    /**
     * Establece el identificador único de personalización.
     * @param personalizacionId El nuevo ID a asignar.
     */
    public void setPersonalizacionId(int personalizacionId) { this.personalizacionId = personalizacionId; }

    /**
     * Obtiene el ID del usuario solicitante.
     * @return El ID del usuario.
     */
    public int getUserId() { return userId; }

    /**
     * Establece el ID del usuario solicitante.
     * @param userId El nuevo ID de usuario.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Obtiene el ID del arreglo base.
     * @return El ID del arreglo o null si no aplica.
     */
    public Integer getArregloId() { return arregloId; }

    /**
     * Establece el ID del arreglo base.
     * @param arregloId El nuevo ID de arreglo.
     */
    public void setArregloId(Integer arregloId) { this.arregloId = arregloId; }

    /**
     * Obtiene el ID del servicio relacionado.
     * @return El ID del servicio.
     */
    public Integer getServicioId() { return servicioId; }

    /**
     * Establece el ID del servicio relacionado.
     * @param servicioId El nuevo ID de servicio.
     */
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }

    /**
     * Obtiene el nombre del servicio.
     * @return El nombre del servicio asignado.
     */
    public String getServicio() { return servicio; }

    /**
     * Establece el nombre del servicio.
     * @param servicio El nuevo nombre de servicio.
     */
    public void setServicio(String servicio) { this.servicio = servicio; }

    /**
     * Obtiene la descripción detallada de la personalización.
     * @return El texto descriptivo.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Establece la descripción detallada de la personalización.
     * @param descripcion La nueva descripción.
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Obtiene el tipo de material o tela especificado.
     * @return El material de la tela.
     */
    public String getMaterialTela() { return materialTela; }

    /**
     * Establece el tipo de material o tela.
     * @param materialTela El nuevo material.
     */
    public void setMaterialTela(String materialTela) { this.materialTela = materialTela; }

    /**
     * Obtiene la ruta de la imagen de referencia.
     * @return La ubicación de la imagen.
     */
    public String getImagenReferencia() { return imagenReferencia; }

    /**
     * Establece la ruta de la imagen de referencia.
     * @param imagenReferencia La nueva ruta de la imagen.
     */
    public void setImagenReferencia(String imagenReferencia) { this.imagenReferencia = imagenReferencia; }

    /**
     * Obtiene el estado actual de la solicitud.
     * @return El estado de la personalización.
     */
    public String getEstado() { return estado; }

    /**
     * Establece el estado actual de la solicitud.
     * @param estado El nuevo estado (ej. completado).
     */
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Obtiene la fecha de creación del registro.
     * @return Objeto LocalDateTime con el momento de creación.
     */
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    /**
     * Establece la fecha de creación del registro.
     * @param fechaCreacion El nuevo momento de creación.
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    /**
     * Obtiene la fecha de la última actualización.
     * @return Objeto LocalDateTime con el momento del último cambio.
     */
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    /**
     * Establece la fecha de la última actualización.
     * @param fechaActualizacion El nuevo momento de actualización.
     */
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}