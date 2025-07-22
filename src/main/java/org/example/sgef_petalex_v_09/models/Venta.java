package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private final StringProperty cliente = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final StringProperty estado = new SimpleStringProperty("Activo"); // Por defecto "Activo"
    private final List<ItemVenta> items = new ArrayList<>();
    private String direccion;
    private boolean activa = true; // true=activa, false=inactiva

    public Venta() {
    }

    public Venta(String cliente, String direccion, LocalDate fecha) {
        this.cliente.set(cliente);
        this.direccion = direccion;
        this.fecha.set(fecha);
        this.estado.set("Activo"); // Estado inicial por defecto
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getCliente() {
        return cliente.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String e) {
        estado.set(e);
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public void addItem(ItemVenta item) {
        items.add(item);
        setTotal(getTotal() + item.getPrecioTotal());
    }
}
