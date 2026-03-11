package model;

import java.time.LocalDateTime;

/**
 * Modelo de Datos: Cita.
 * Representa un agendamiento temporal para la gestión física de un pedido.
 * 
 * @author Antigravity - Senior Architect
 */
public class Cita {
    private int citaId;
    private int pedidoId;
    private LocalDateTime citaFechaHora;
    private String citaEstado; // programada, realizada, cancelada
    private String citaNotas;
    private String direccionEntrega;
    private String citaMotivo;

    public Cita() {
    }

    /**
     * Constructor para nuevas citas.
     * @param pedidoId ID del pedido vinculado.
     * @param citaFechaHora Estampa de tiempo del agendamiento.
     * @param citaNotas Observaciones del cliente.
     * @param direccionEntrega Ubicación para el servicio.
     * @param citaMotivo Razón de la cita (Recogida/Entrega).
     */
    public Cita(int pedidoId, LocalDateTime citaFechaHora, String citaNotas, String direccionEntrega, String citaMotivo) {
        this.pedidoId = pedidoId;
        this.citaFechaHora = citaFechaHora;
        this.citaEstado = "programada"; // Estado inicial por defecto
        this.citaNotas = citaNotas;
        this.direccionEntrega = direccionEntrega;
        this.citaMotivo = citaMotivo;
    }

    // Getters y Setters con encapsulamiento estándar
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