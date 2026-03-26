/**
 * Nombre del archivo: Servicio.java
 * Descripción breve: Entidad que representa los servicios de sastrería y arreglos ofrecidos por el taller.
 * Author: Jaider Andres Esparza — Antigravity
 * Fecha de documentación: 25 de marzo de 2026
 * Versión: 1.0
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Definir la entidad Servicio que representa los trabajos de costura y arreglos disponibles.
 */
package model;

/**
 * Estructura de datos para los servicios base ofrecidos por el taller.
 * Esta clase centraliza la información de catálogo, incluyendo descripciones,
 * precios base y tiempos estimados para cada arreglo textil.
 */
public class Servicio {
    // Identificador único del servicio en el catálogo
    private int servicioId;
    // Nombre comercial o descriptivo del arreglo (ej. "Basta de pantalón")
    private String servicioNombre;
    // Detalle extenso de lo que incluye el trabajo de costura
    private String servicioDescripcion;
    // Costo mínimo sugerido para el trabajo antes de personalizaciones
    private double servicioPrecioBase;
    // Tiempo promedio de entrega expresado en horas o días
    private int servicioTiempoEstimado;
    // Estado de disponibilidad del servicio en la plataforma
    private boolean servicioActivo;
    
    // Campos para auditoría e identificación visual
    // Fecha en la que el servicio fue dado de alta en la plataforma
    private String fechaCreacion;
    // Ruta o enlace a la imagen representativa del servicio
    private String imagenUrl;
    
    /**
     * Obtiene el identificador interno del servicio.
     * @return Entero con el ID único.
     */
    public int getServicioId() { return servicioId; }

    /**
     * Asigna el identificador interno al servicio.
     * @param servicioId Nuevo ID único a establecer.
     */
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }

    /**
     * Obtiene el nombre del servicio.
     * @return Cadena con el nombre comercial.
     */
    public String getServicioNombre() { return servicioNombre; }

    /**
     * Establece el nombre del servicio.
     * @param servicioNombre Nuevo nombre para el servicio.
     */
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    /**
     * Obtiene la descripción detallada del trabajo.
     * @return Cadena con el detalle del servicio.
     */
    public String getServicioDescripcion() { return servicioDescripcion; }

    /**
     * Establece la descripción detallada del trabajo.
     * @param servicioDescripcion Nuevo detalle descriptivo.
     */
    public void setServicioDescripcion(String servicioDescripcion) { this.servicioDescripcion = servicioDescripcion; }

    /**
     * Obtiene el precio base de mercado para el arreglo.
     * @return Valor punto flotante con el precio.
     */
    public double getServicioPrecioBase() { return servicioPrecioBase; }

    /**
     * Establece el precio base de mercado para el arreglo.
     * @param servicioPrecioBase Nuevo valor de precio base.
     */
    public void setServicioPrecioBase(double servicioPrecioBase) { this.servicioPrecioBase = servicioPrecioBase; }

    /**
     * Obtiene el tiempo de entrega estimado.
     * @return Entero que representa la duración estimada.
     */
    public int getServicioTiempoEstimado() { return servicioTiempoEstimado; }

    /**
     * Establece el tiempo de entrega estimado.
     * @param servicioTiempoEstimado Nueva duración estimada a asignar.
     */
    public void setServicioTiempoEstimado(int servicioTiempoEstimado) { this.servicioTiempoEstimado = servicioTiempoEstimado; }

    /**
     * Verifica si el servicio se encuentra actualmente habilitado.
     * @return Verdadero si el servicio está activo, falso de lo contrario.
     */
    public boolean isServicioActivo() { return servicioActivo; }

    /**
     * Modifica el estado de activación del servicio.
     * @param servicioActivo Booleano con el nuevo estado de disponibilidad.
     */
    public void setServicioActivo(boolean servicioActivo) { this.servicioActivo = servicioActivo; }

    /**
     * Obtiene la fecha de registro del servicio.
     * @return Cadena con la fecha de creación en el sistema.
     */
    public String getFechaCreacion() { return fechaCreacion; }

    /**
     * Registra la fecha de creación del servicio.
     * @param fechaCreacion Cadena con la fecha de auditoría.
     */
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Métodos de compatibilidad para asegurar el funcionamiento con código heredado
    // Estas funciones actúan como alias para los atributos con nombres renovados
    
    /**
     * Alias para obtener el nombre completo del servicio.
     * @return Cadena con el nombre del servicio.
     */
    public String getNombre() { return servicioNombre; }

    /**
     * Alias para establecer el nombre del servicio.
     * @param nombre Cadena con el nombre a asignar.
     */
    public void setNombre(String nombre) { this.servicioNombre = nombre; }
    
    /**
     * Alias para obtener la descripción del servicio.
     * @return Cadena con el detalle descriptivo.
     */
    public String getDescripcion() { return servicioDescripcion; }

    /**
     * Alias para establecer la descripción del servicio.
     * @param descripcion Cadena con la descripción del servicio.
     */
    public void setDescripcion(String descripcion) { this.servicioDescripcion = descripcion; }
    
    /**
     * Alias para obtener el precio base del servicio.
     * @return Valor numérico con el precio base.
     */
    public double getPrecioBase() { return servicioPrecioBase; }

    /**
     * Alias para establecer el precio base del servicio.
     * @param precioBase Nuevo precio base para el servicio.
     */
    public void setPrecioBase(double precioBase) { this.servicioPrecioBase = precioBase; }
    
    /**
     * Alias para obtener el tiempo estimado de realización.
     * @return Entero con la duración esperada.
     */
    public int getTiempoEstimado() { return servicioTiempoEstimado; }

    /**
     * Alias para establecer el tiempo estimado de realización.
     * @param tiempoEstimado Nueva duración estimada a establecer.
     */
    public void setTiempoEstimado(int tiempoEstimado) { this.servicioTiempoEstimado = tiempoEstimado; }
    
    /**
     * Alias para verificar si el servicio está disponible.
     * @return Booleano que indica disponibilidad para los clientes.
     */
    public boolean isDisponible() { return servicioActivo; }

    /**
     * Alias para establecer la disponibilidad del servicio.
     * @param disponible Booleano con el estado de disponibilidad.
     */
    public void setDisponible(boolean disponible) { this.servicioActivo = disponible; }
    
    /**
     * Alias para obtener el identificador único (utilizado en versiones previas).
     * @return Identificador numérico del arreglo.
     */
    public int getArregloId() { return servicioId; }

    /**
     * Alias para establecer el identificador único.
     * @param arregloId Nuevo ID de sastrería a establecer.
     */
    public void setArregloId(int arregloId) { this.servicioId = arregloId; }
    
    /**
     * Retorna la URL de la imagen del servicio, proporcionando una predeterminada si no existe.
     * Esto asegura que la interfaz de usuario siempre muestre un elemento visual coherente.
     * 
     * @return Cadena con la ruta de la imagen activa o la imagen por defecto.
     */
    public String getImagenUrl() { return imagenUrl != null ? imagenUrl : "Assets/image/imagen-sastreria.jpg"; }

    /**
     * Asigna el recurso gráfico representativo del servicio.
     * @param imagenUrl Cadena con la ruta o URL pública de la imagen.
     */
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}