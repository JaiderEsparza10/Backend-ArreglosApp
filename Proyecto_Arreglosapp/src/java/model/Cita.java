/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Gestionar la información de las citas programadas para la recolección o entrega de prendas.
 */
package model;

import java.time.LocalDateTime;

/**
 * Representa un agendamiento en el calendario para la atención de pedidos.
 */
public class Cita {
    private int citaId;
    private int pedidoId;
    private LocalDateTime citaFechaHora;
    private String citaEstado; // Posibles estados: programada, realizada, cancelada
    private String citaNotas;
    private String direccionEntrega;
    private String citaMotivo;

    public Cita() {
    }

    /**
     * Constructor para inicializar una nueva cita vinculada a un pedido.
     * 
     * @param pedidoId Identificador del pedido asociado.
     * @param citaFechaHora Fecha y hora exacta del encuentro.
     * @param citaNotas Observaciones o detalles adicionales.
     * @param direccionEntrega Lugar donde se realizará la atención.
     * @param citaMotivo Razón del agendamiento (ej: Recogida o Entrega).
     */
    public Cita(int pedidoId, LocalDateTime citaFechaHora, String citaNotas, String direccionEntrega, String citaMotivo) {
        this.pedidoId = pedidoId;
        this.citaFechaHora = citaFechaHora;
        this.citaEstado = "programada"; // Por defecto nace en estado programada
        this.citaNotas = citaNotas;
        this.direccionEntrega = direccionEntrega;
        this.citaMotivo = citaMotivo;
    }

    // Métodos Getters y Setters para la manipulación controlada de datos
    public int getCitaId() { return citaId; }
    public void setCitaId(int citaId) { this.citaId = citaId; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public LocalDateTime getCitaFechaHora() { return citaFechaHora; }
    public void setCitaFechaHora(LocalDateTime citaFechaHora) { this.citaFechaHora = citaFechaHora; }

    public String getCitaEstado() { return citaEstado; }
    public void setCitaEstado(String citaEstado) { this.citaEstado = citaEstado; }

    public String getCitaNotas() { return citaNotas; }
    public void setCitaNotas(String citaNotas) { this.citaNotas = citaNotas; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public String getCitaMotivo() { return citaMotivo; }
    public void setCitaMotivo(String citaMotivo) { this.citaMotivo = citaMotivo; }
}