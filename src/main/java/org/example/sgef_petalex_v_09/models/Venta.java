package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

public class Venta {
    private final StringProperty cliente = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final StringProperty estado = new SimpleStringProperty();
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();
    private String direccion;
    private boolean activa;

    // Constructores
    public Venta() {
        this.estado.set("Activo");
        this.total.set(0.0);
        this.activa = true;
    }

    public Venta(String cliente, String direccion, LocalDate fecha) {
        this();
        this.cliente.set(cliente);
        this.direccion = direccion;
        this.fecha.set(fecha);
    }

    // Getters y Setters
    public String getCliente() {
        return cliente.get();
    }

    public void setCliente(String cliente) {
        this.cliente.set(cliente);
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public void setFecha(LocalDate fecha) {
        this.fecha.set(fecha);
    }

    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    public double getTotal() {
        return total.get();
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public ObservableList<ItemVenta> getItems() {
        return items;
    }

    public void addItem(ItemVenta item) {
        items.add(item);
        calcularTotal();
    }

    public void removeItem(ItemVenta item) {
        items.remove(item);
        calcularTotal();
    }

    private void calcularTotal() {
        double nuevoTotal = items.stream()
                .mapToDouble(ItemVenta::getPrecioTotal)
                .sum();
        setTotal(nuevoTotal);
    }

    @Override
    public String toString() {
        return "Venta{" +
                "cliente=" + getCliente() +
                ", fecha=" + getFecha() +
                ", total=" + getTotal() +
                ", estado=" + getEstado() +
                '}';
    }
}
