package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Cliente cliente;
    private LocalDate fechaPedido;
    private LocalDate fechaEstimadaEnvio;
    private EstadoPedido estado;
    private String codigoGuiaAerea;

    private List<ItemPedido> items;

    public Pedido() {
        this.items = new ArrayList<>();
    }

    public Pedido(int id, Cliente cliente, LocalDate fechaPedido, LocalDate fechaEstimadaEnvio,
                  EstadoPedido estado, String codigoGuiaAerea) {
        this.id = id;
        this.cliente = cliente;
        this.fechaPedido = fechaPedido;
        this.fechaEstimadaEnvio = fechaEstimadaEnvio;
        this.estado = estado;
        this.codigoGuiaAerea = codigoGuiaAerea;
        this.items = new ArrayList<>();
    }

    // Métodos útiles
    public void agregarItem(ItemPedido item) {
        this.items.add(item);
    }

    // Getters y Setters (puedes generar automáticamente)

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", fechaPedido=" + fechaPedido +
                ", fechaEstimadaEnvio=" + fechaEstimadaEnvio +
                ", estado=" + estado +
                ", codigoGuiaAerea='" + codigoGuiaAerea + '\'' +
                ", items=" + items +
                '}';
    }
}
