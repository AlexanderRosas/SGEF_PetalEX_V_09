package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int id;
    private Cliente cliente;
    private LocalDate fechaPedido;
    private LocalDate fechaEstimadaEnvio;
    private String estadoActual;
    private String codigoGuiaAerea;
    private List<String> items;

    private List<ItemVenta> itemsVenta = new ArrayList<>();

    private Venta venta;

    private double precioTotal;

    // Nuevo atributo para TipoEmpaque
    private Rosa.TipoEmpaque tipoEmpaque;

    public Pedido() {
        this.items = new ArrayList<>();
    }

    public Pedido(int id, Cliente cliente, LocalDate fechaPedido, LocalDate fechaEstimadaEnvio,
                  String estadoActual, String codigoGuiaAerea) {
        this.id = id;
        this.cliente = cliente;
        this.fechaPedido = fechaPedido;
        this.fechaEstimadaEnvio = fechaEstimadaEnvio;
        this.estadoActual = estadoActual;
        this.codigoGuiaAerea = codigoGuiaAerea;
        this.items = new ArrayList<>();
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDate getFechaEstimadaEnvio() {
        return fechaEstimadaEnvio;
    }

    public void setFechaEstimadaEnvio(LocalDate fechaEstimadaEnvio) {
        this.fechaEstimadaEnvio = fechaEstimadaEnvio;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getCodigoGuiaAerea() {
        return codigoGuiaAerea;
    }

    public void setCodigoGuiaAerea(String codigoGuiaAerea) {
        this.codigoGuiaAerea = codigoGuiaAerea;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void agregarItem(String item) {
        if (item != null && !item.isEmpty()) {
            this.items.add(item);
        }
    }

    public List<ItemVenta> getItemsVenta() {
        return itemsVenta;
    }

    public void setItemsVenta(List<ItemVenta> itemsVenta) {
        this.itemsVenta = itemsVenta;
        actualizarTipoEmpaque();  // Actualizamos empaque cuando se setea la lista
    }

    public void agregarItemVenta(ItemVenta item) {
        if (item != null) {
            itemsVenta.add(item);
            actualizarTipoEmpaque();  // Actualizamos empaque al agregar item
        }
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Rosa.TipoEmpaque getTipoEmpaque() {
        return tipoEmpaque;
    }

    public void setTipoEmpaque(Rosa.TipoEmpaque tipoEmpaque) {
        this.tipoEmpaque = tipoEmpaque;
    }

    /**
     * Suma la cantidad total de rosas de todos los itemsVenta.
     */
    public int getCantidadTotalRosas() {
        return itemsVenta.stream()
                .mapToInt(ItemVenta::getCantidad)
                .sum();
    }

    /**
     * Actualiza el tipo de empaque basado en la cantidad total de rosas.
     */
    public void actualizarTipoEmpaque() {
        int total = getCantidadTotalRosas();
        this.tipoEmpaque = Rosa.TipoEmpaque.detectarPorCantidad(total).orElse(null);
    }

    public Venta generarVentaDesdePedido() {
        Venta nuevaVenta = new Venta();
       //nuevaVenta.setNumeroVenta("VNT-" + this.id);
        nuevaVenta.setCliente(this.cliente.getNombre());
        nuevaVenta.setDireccion(this.cliente.getDireccion());
        nuevaVenta.setFecha(LocalDate.now());

        for (ItemVenta item : this.itemsVenta) {
            nuevaVenta.addItem(item);
        }

        nuevaVenta.setTotal(this.getPrecioTotal());

        return nuevaVenta;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", fechaPedido=" + fechaPedido +
                ", fechaEstimadaEnvio=" + fechaEstimadaEnvio +
                ", estadoActual='" + estadoActual + '\'' +
                ", codigoGuiaAerea='" + codigoGuiaAerea + '\'' +
                ", items=" + items +
                ", precioTotal=" + precioTotal +
                ", tipoEmpaque=" + (tipoEmpaque != null ? tipoEmpaque.getNombre() : "No definido") +
                '}';
    }
}
