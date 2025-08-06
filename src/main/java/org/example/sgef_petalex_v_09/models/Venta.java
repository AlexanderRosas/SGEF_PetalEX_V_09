package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Venta {

    // --- Identificador ---
    private final StringProperty id = new SimpleStringProperty();

    // --- Cliente completo ---
    private final ObjectProperty<Cliente> cliente = new SimpleObjectProperty<>();

    // --- Datos básicos ---
    private final StringProperty tipoDestino = new SimpleStringProperty();
    private final StringProperty servicio = new SimpleStringProperty("Pedido Exportado");
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();

    // --- Ítems de la venta ---
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    // --- Totales ---
    private final DoubleProperty precio = new SimpleDoubleProperty(0.0);
    private final DoubleProperty iva = new SimpleDoubleProperty(0.0);
    private final DoubleProperty total = new SimpleDoubleProperty(0.0);

    // --- Estado y responsable ---
    private final StringProperty estado = new SimpleStringProperty("Exportado");
    private final StringProperty usuarioResponsable = new SimpleStringProperty();

    // --- IVA 0 % ---
    private static final double IVA_RATE = 0.0;

    public Venta() {
        items.addListener((ListChangeListener<ItemVenta>) c -> recalcular());
    }

    public Venta(String id, Cliente cliente, String tipoDestino, LocalDate fecha,
            String usuarioResponsable) {
        this();
        setId(id);
        setCliente(cliente);
        setTipoDestino(tipoDestino);
        setFecha(fecha);
        setUsuarioResponsable(usuarioResponsable);
    }

    // --- Métodos de negocio ---

    public void addItem(ItemVenta item) {
        items.add(item);
    }

    private void recalcular() {
        double sub = items.stream()
                .mapToDouble(ItemVenta::getPrecioTotal)
                .sum();
        precio.set(sub);
        iva.set(sub * IVA_RATE);
        total.set(sub + iva.get());
    }

    /** Resumen de productos */
    public String getDetalleProductos() {
        return items.stream()
                .map(i -> i.getVariedad() + " x" + i.getCantidad())
                .collect(Collectors.joining(", "));
    }

    // --- Getters / Setters / Properties ---

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public Cliente getCliente() {
        return cliente.get();
    }

    public ObjectProperty<Cliente> clienteProperty() {
        return cliente;
    }

    public void setCliente(Cliente c) {
        cliente.set(c);
    }

    public String getTipoDestino() {
        return tipoDestino.get();
    }

    public StringProperty tipoDestinoProperty() {
        return tipoDestino;
    }

    public void setTipoDestino(String t) {
        tipoDestino.set(t);
    }

    public String getServicio() {
        return servicio.get();
    }

    public StringProperty servicioProperty() {
        return servicio;
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    public void setFecha(LocalDate f) {
        fecha.set(f);
    }

    public ObservableList<ItemVenta> getItems() {
        return items;
    }

    public double getPrecio() {
        return precio.get();
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public double getIva() {
        return iva.get();
    }

    public DoubleProperty ivaProperty() {
        return iva;
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public void setEstado(String e) {
        estado.set(e);
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable.get();
    }

    public StringProperty usuarioResponsableProperty() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String u) {
        usuarioResponsable.set(u);
    }

    // --- Propiedades auxiliares para mostrar directamente ---

    public StringProperty clienteNombreProperty() {
        return new SimpleStringProperty(
                cliente.get() != null ? cliente.get().getNombre() : "");
    }

    public StringProperty clienteIdentificadorProperty() {
        return new SimpleStringProperty(
                cliente.get() != null ? cliente.get().getIdentificadorEmpresarial() : "");
    }

    public StringProperty clienteDireccionProperty() {
        return new SimpleStringProperty(
                cliente.get() != null ? cliente.get().getDireccion() : "");
    }

    public StringProperty clientePaisProperty() {
        return new SimpleStringProperty(
                cliente.get() != null ? cliente.get().getPais() : "");
    }

    public void setItemsVenta(List<ItemVenta> nuevosItems) {
        items.setAll(nuevosItems); // O usa this.items = FXCollections.observableArrayList(nuevosItems);
    }

    public void setPrecio(double p) {
        this.precio.set(p);
    }

    public void setIva(double i) {
        this.iva.set(i);
    }

    public void setTotal(double t) {
        this.total.set(t);
    }

    public void setServicio(String s) {
        this.servicio.set(s);
    }

}