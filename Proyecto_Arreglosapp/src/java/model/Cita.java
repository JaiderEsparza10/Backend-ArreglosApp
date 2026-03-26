/**
 * Nombre del archivo: Cita.java
 * Descripción breve: Entidad que gestiona las citas programadas para servicios de sastrería.
 * Author: Jaider Andres Esparza — Antigravity
 * Fecha de documentación: 25 de marzo de 2026
 * Versión: 1.0
 */
/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar la información de las citas programadas para la recolección o entrega de prendas.
 */
package model;

import java.time.LocalDateTime;

/**
 * Representa un agendamiento en el calendario para la atención de pedidos.
 * Esta clase modela los eventos de encuentro entre el sastre y el cliente,
 * ya sea para toma de medidas, entrega o recogida de prendas trabajadas.
 */
public class Cita {
    // Identificador único de la cita en el sistema
    private int citaId;
    // Identificador del pedido relacionado con esta cita
    private int pedidoId;
    // Fecha y hora exacta programada para la cita
    private LocalDateTime citaFechaHora;
    // Estado actual del agendamiento (programada, realizada, cancelada)
    private String citaEstado; // Posibles estados: programada, realizada, cancelada
    // Comentarios o notas aclaratorias sobre la cita
    private String citaNotas;
    // Dirección específica donde se llevará a cabo el encuentro
    private String direccionEntrega;
    // Razón principal de la cita (ej. "Entrega de prenda", "Toma de medidas")
    private String citaMotivo;

    /**
     * Constructor por defecto para inicialización genérica.
     */
    public Cita() {
    }

    /**
     * Constructor para inicializar una nueva cita vinculada a un pedido específico.
     * Facilita la creación de un evento de calendario con todos sus detalles logísticos.
     * 
     * @param pedidoId         Identificador del pedido asociado.
     * @param citaFechaHora    Fecha y hora exacta del encuentro programado.
     * @param citaNotas        Observaciones o detalles adicionales para el sastre.
     * @param direccionEntrega Ubicación física acordada para la atención.
     * @param citaMotivo       Razón específica del agendamiento.
     */
    public Cita(int pedidoId, LocalDateTime citaFechaHora, String citaNotas, String direccionEntrega, String citaMotivo) {
        this.pedidoId = pedidoId;
        this.citaFechaHora = citaFechaHora;
        this.citaEstado = "programada"; // Por defecto, una nueva cita nace en estado 'programada'
        this.citaNotas = citaNotas;
        this.direccionEntrega = direccionEntrega;
        this.citaMotivo = citaMotivo;
    }

    // Métodos Getters y Setters para la manipulación controlada de datos (Encapsulamiento)

    /**
     * Obtiene el identificador de la cita.
     * @return El ID numérico de la cita.
     */
    public int getCitaId() { return citaId; }

    /**
     * Establece el identificador de la cita.
     * @param citaId El nuevo ID a asignar.
     */
    public void setCitaId(int citaId) { this.citaId = citaId; }

    /**
     * Obtiene el ID del pedido asociado.
     * @return El ID del pedido vinculado.
     */
    public int getPedidoId() { return pedidoId; }

    /**
     * Establece el ID del pedido asociado.
     * @param pedidoId El nuevo ID de pedido a vincular.
     */
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    /**
     * Obtiene la cronología programada de la cita.
     * @return Objeto LocalDateTime con fecha y hora.
     */
    public LocalDateTime getCitaFechaHora() { return citaFechaHora; }

    /**
     * Establece la cronología programada de la cita.
     * @param citaFechaHora El nuevo momento de encuentro.
     */
    public void setCitaFechaHora(LocalDateTime citaFechaHora) { this.citaFechaHora = citaFechaHora; }

    /**
     * Obtiene el estado operativo de la cita.
     * @return Cadena con el estado (ej. realizada).
     */
    public String getCitaEstado() { return citaEstado; }

    /**
     * Establece el estado operativo de la cita.
     * @param citaEstado El nuevo estado a asignar.
     */
    public void setCitaEstado(String citaEstado) { this.citaEstado = citaEstado; }

    /**
     * Obtiene las anotaciones adicionales de la cita.
     * @return Cadena con las notas registradas.
     */
    public String getCitaNotas() { return citaNotas; }

    /**
     * Establece anotaciones adicionales para la cita.
     * @param citaNotas El nuevo contenido de las notas.
     */
    public void setCitaNotas(String citaNotas) { this.citaNotas = citaNotas; }

    /**
     * Obtiene la dirección física del encuentro.
     * @return Cadena con la dirección de entrega o recogida.
     */
    public String getDireccionEntrega() { return direccionEntrega; }

    /**
     * Establece la dirección física del encuentro.
     * @param direccionEntrega La nueva dirección a registrar.
     */
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    /**
     * Obtiene el motivo o propósito del agendamiento.
     * @return Cadena con la razón de la cita.
     */
    public String getCitaMotivo() { return citaMotivo; }

    /**
     * Establece el motivo o propósito del agendamiento.
     * @param citaMotivo El nuevo motivo a definir.
     */
    public void setCitaMotivo(String citaMotivo) { this.citaMotivo = citaMotivo; }
}