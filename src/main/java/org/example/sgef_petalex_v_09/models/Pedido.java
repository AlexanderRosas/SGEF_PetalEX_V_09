package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Rosa.TipoEmpaque tipoEmpaque;
    private String empresaTransporte; // Nuevo campo
    private LocalDateTime fechaModificacion; // Fecha de última actualización

    private String usuarioModificacion; // Usuario responsable de la última actualización
    private LocalDateTime fechaCreacion;
    private String usuarioResponsable;

    // ---------------------------------------------
    // GETTERS / SETTERS
    // ---------------------------------------------
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

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
        actualizarTipoEmpaque(); // Actualizamos empaque cuando se setea la lista
    }

    public void agregarItemVenta(ItemVenta item) {
        if (item != null) {
            itemsVenta.add(item);
            actualizarTipoEmpaque(); // Actualizamos empaque al agregar item
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

    public String getEmpresaTransporte() {
        return empresaTransporte;
    }

    public void setEmpresaTransporte(String empresaTransporte) {
        this.empresaTransporte = empresaTransporte;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public int getCantidadTotalRosas() {
        return itemsVenta.stream()
                .mapToInt(ItemVenta::getCantidad)
                .sum();
    }

    public void actualizarTipoEmpaque() {
        int total = getCantidadTotalRosas();
        this.tipoEmpaque = Rosa.TipoEmpaque.detectarPorCantidad(total).orElse(null);
    }

    public Venta generarVentaDesdePedido() {
    Venta nuevaVenta = new Venta();
    nuevaVenta.setCliente(this.cliente); // Aquí debes pasar el objeto Cliente completo
    nuevaVenta.setFecha(LocalDate.now());
    nuevaVenta.servicioProperty().set("Pedido Exportado"); // Establece el servicio

    for (ItemVenta item : this.itemsVenta) {
        nuevaVenta.addItem(item);
    }

    // Establece el total usando la propiedad
    nuevaVenta.totalProperty().set(this.getPrecioTotal());

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
                ", empresaTransporte='" + empresaTransporte + '\'' +
                ", fechaModificacion=" + fechaModificacion +
                ", usuarioModificacion='" + usuarioModificacion + '\'' +
                '}';
    }
}