package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Cliente cliente;  // Usas la clase Cliente
    private LocalDate fechaPedido;
    private LocalDate fechaEstimadaEnvio;
    private String estado;    // Estado del pedido, como texto simple
    private String codigoGuiaAerea;
    private List<String> items;

    public Pedido() {
        this.items = new ArrayList<>();
        this.estado = "Pendiente"; // valor por defecto
    }

    public Pedido(int id, Cliente cliente, LocalDate fechaPedido, LocalDate fechaEstimadaEnvio,
                  String estado, String codigoGuiaAerea) {
        this.id = id;
        this.cliente = cliente;
        this.fechaPedido = fechaPedido;
        this.fechaEstimadaEnvio = fechaEstimadaEnvio;
        this.estado = estado;
        this.codigoGuiaAerea = codigoGuiaAerea;
        this.items = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDate getFechaPedido() { return fechaPedido; }
    public LocalDate getFechaEstimadaEnvio() { return fechaEstimadaEnvio; }
    public String getEstado() { return estado; }
    public String getCodigoGuiaAerea() { return codigoGuiaAerea; }
    public List<String> getItems() { return items; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setFechaPedido(LocalDate fechaPedido) { this.fechaPedido = fechaPedido; }
    public void setFechaEstimadaEnvio(LocalDate fechaEstimadaEnvio) { this.fechaEstimadaEnvio = fechaEstimadaEnvio; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCodigoGuiaAerea(String codigoGuiaAerea) { this.codigoGuiaAerea = codigoGuiaAerea; }
    public void setItems(List<String> items) { this.items = items; }

    // Método para agregar un item como String (descripción)
    public void agregarItem(String item) {
        this.items.add(item);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", fechaPedido=" + fechaPedido +
                ", fechaEstimadaEnvio=" + fechaEstimadaEnvio +
                ", estado='" + estado + '\'' +
                ", codigoGuiaAerea='" + codigoGuiaAerea + '\'' +
                ", items=" + items +
                '}';
    }
}
