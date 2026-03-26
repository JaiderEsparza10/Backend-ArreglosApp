/**
 * Nombre del archivo: Favorito.java
 * Descripción breve: Clase que representa la relación de favoritos entre un usuario y un servicio.
 * Author: Jaider Andres Esparza — Antigravity
 * Fecha de documentación: 25 de marzo de 2026
 * Versión: 1.0
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar la relación entre usuarios y sus servicios o arreglos seleccionados como favoritos.
 */
package model;

import java.time.LocalDateTime;

/**
 * Entidad que representa la selección persistente de un servicio por parte de un usuario.
 * Permite a los clientes marcar servicios de su interés para un acceso rápido posterior,
 * almacenando detalles básicos del servicio en el momento de la selección.
 */
public class Favorito {
    // Identificador único del registro de favorito en la base de datos
    private int favoritoId;
    // Identificador del usuario que marcó el servicio como favorito
    private int userId;
    // Identificador del arreglo o servicio base seleccionado
    private int arregloId;
    // Categoría o tipo general del servicio seleccionado
    private String servicio;
    // Nombre específico del servicio para mostrar en la lista de favoritos
    private String nombreServicio;
    // Precio del servicio al momento de ser agregado a favoritos
    private double precio;
    // Ruta o URL de la imagen ilustrativa del servicio
    private String imagenUrl;
    // Cantidad de veces que se ha interactuado o guardado (si aplica)
    private int cantidad;
    // Marca de tiempo de cuándo el usuario agregó el servicio a su lista
    private LocalDateTime fechaAgregado;

    /**
     * Constructor por defecto para compatibilidad con frameworks.
     */
    public Favorito() {
    }

    /**
     * Constructor para inicializar una selección rápida de un servicio como favorito.
     * 
     * @param userId         Identificador único del usuario.
     * @param arregloId     Identificador técnico del arreglo o servicio base.
     * @param servicio       Categoría general del servicio vinculado.
     * @param nombreServicio Nombre descriptivo para la interfaz de usuario.
     * @param precio         Valor comercial asignado al servicio.
     * @param imagenUrl      Ubicación del recurso gráfico representativo.
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

    // Métodos para el acceso y modificación de los datos encapsulados (Getters y Setters)

    /**
     * Obtiene el ID del registro de favorito.
     * @return Identificador único del favorito.
     */
    public int getFavoritoId() { return favoritoId; }

    /**
     * Establece el ID del registro de favorito.
     * @param favoritoId Nuevo identificador de favorito.
     */
    public void setFavoritoId(int favoritoId) { this.favoritoId = favoritoId; }

    /**
     * Obtiene el ID del usuario propietario.
     * @return El ID del usuario.
     */
    public int getUserId() { return userId; }

    /**
     * Establece el ID del usuario propietario.
     * @param userId El nuevo ID de usuario.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Obtiene el ID del arreglo vinculado.
     * @return El ID del arreglo técnico.
     */
    public int getArregloId() { return arregloId; }

    /**
     * Establece el ID del arreglo vinculado.
     * @param arregloId El nuevo ID de arreglo.
     */
    public void setArregloId(int arregloId) { this.arregloId = arregloId; }

    /**
     * Proporciona compatibilidad con la nueva arquitectura de identificadores.
     * Este método actúa como un puente hacia arregloId bajo el nombre servicioId.
     * @return El ID del arreglo (tratado como servicio).
     */
    public int getServicioId() { return arregloId; }

    /**
     * Establece el identificador de servicio mapeándolo al arregloId interno.
     * @param servicioId Nuevo ID de servicio a asignar.
     */
    public void setServicioId(int servicioId) { this.arregloId = servicioId; }

    /**
     * Obtiene la categoría del servicio.
     * @return Cadena con el tipo de servicio.
     */
    public String getServicio() { return servicio; }

    /**
     * Establece la categoría del servicio.
     * @param servicio El nombre de la categoría.
     */
    public void setServicio(String servicio) { this.servicio = servicio; }

    /**
     * Obtiene el nombre descriptivo del servicio.
     * @return Cadena con el nombre comercial.
     */
    public String getNombreServicio() { return nombreServicio; }

    /**
     * Establece el nombre descriptivo del servicio.
     * @param nombreServicio El nuevo nombre del servicio.
     */
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    /**
     * Obtiene el precio registrado del servicio.
     * @return Valor numérico con el precio.
     */
    public double getPrecio() { return precio; }

    /**
     * Establece el precio del servicio.
     * @param precio El nuevo precio a asignar.
     */
    public void setPrecio(double precio) { this.precio = precio; }

    /**
     * Obtiene la URL de la imagen del servicio.
     * @return Cadena con la ruta de la imagen.
     */
    public String getImagenUrl() { return imagenUrl; }

    /**
     * Establece la URL de la imagen del servicio.
     * @param imagenUrl La nueva ruta de la imagen.
     */
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    /**
     * Obtiene la cantidad seleccionada.
     * @return Entero con la cantidad de ítems.
     */
    public int getCantidad() { return cantidad; }

    /**
     * Establece la cantidad seleccionada.
     * @param cantidad Nueva cantidad a asignar.
     */
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /**
     * Obtiene la fecha en la que se agregó a favoritos.
     * @return Objeto LocalDateTime con la marca de tiempo.
     */
    public LocalDateTime getFechaAgregado() { return fechaAgregado; }

    /**
     * Establece la fecha de agregación a favoritos.
     * @param fechaAgregado El momento en que se guardó.
     */
    public void setFechaAgregado(LocalDateTime fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}
