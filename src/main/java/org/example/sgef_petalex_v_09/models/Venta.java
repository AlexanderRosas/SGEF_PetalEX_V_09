package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private final StringProperty cliente   = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fecha   = new SimpleObjectProperty<>();
    private final DoubleProperty total      = new SimpleDoubleProperty();
    private final StringProperty estado     = new SimpleStringProperty("Activo"); // Por defecto "Activo"
    private final List<ItemVenta> items     = new ArrayList<>();
    private String direccion;

    public Venta() {}

    public Venta(String cliente, String direccion, LocalDate fecha) {
        this.cliente.set(cliente);
        this.direccion = direccion;
        this.fecha.set(fecha);
        this.estado.set("Activo"); // Estado inicial por defecto
    }

    public String getCliente() { return cliente.get(); }
    public void setCliente(String c) { cliente.set(c); }
    public StringProperty clienteProperty() { return cliente; }

    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate f) { fecha.set(f); }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }

    public double getTotal() { return total.get(); }
    public void setTotal(double t) { total.set(t); }
    public ObservableValue<Number> totalProperty() { return total; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String e) { estado.set(e); }
    public StringProperty estadoProperty() { return estado; }

    public List<ItemVenta> getItems() { return items; }

    public String getDireccion() { return direccion; }

    public void addItem(ItemVenta item) {
        items.add(item);
        setTotal(getTotal() + item.getPrecioTotal());
    }
}
