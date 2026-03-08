package model;

import java.time.LocalDateTime;

/**
 * Esta clase representa una cita agendada por un usuario para la entrega o
 * revisión de un pedido.
 */
public class Cita {
    private int citaId;
    private int pedidoId;
    private LocalDateTime citaFechaHora;
    private String citaEstado;
    private String citaNotas;
    private String direccionEntrega;

    public Cita() {
    }

    public Cita(int pedidoId, LocalDateTime citaFechaHora, String citaNotas, String direccionEntrega) {
        this.pedidoId = pedidoId;
        this.citaFechaHora = citaFechaHora;
        this.citaEstado = "programada";
        this.citaNotas = citaNotas;
        this.direccionEntrega = direccionEntrega;
    }

    public int getCitaId() {
        return citaId;
    }

    public void setCitaId(int citaId) {
        this.citaId = citaId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public LocalDateTime getCitaFechaHora() {
        return citaFechaHora;
    }

    public void setCitaFechaHora(LocalDateTime citaFechaHora) {
        this.citaFechaHora = citaFechaHora;
    }

    public String getCitaEstado() {
        return citaEstado;
    }

    public void setCitaEstado(String citaEstado) {
        this.citaEstado = citaEstado;
    }

    public String getCitaNotas() {
        return citaNotas;
    }

    public void setCitaNotas(String citaNotas) {
        this.citaNotas = citaNotas;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }
}