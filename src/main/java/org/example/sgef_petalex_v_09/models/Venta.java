package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Modelo para representar una venta en el sistema,
 * con IVA “quemado” en cero.
 */
public class Venta {
    // — Propiedades básicas —
    private final StringProperty id            = new SimpleStringProperty();
    private final StringProperty tipoDestino  = new SimpleStringProperty();
    private final StringProperty servicio     = new SimpleStringProperty("Ventas");
    private final StringProperty cliente      = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>(LocalDate.now());

    // — Detalle de ítems —
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    // — Totales calculados —
    private final DoubleProperty precio = new SimpleDoubleProperty(0.0);
    private final DoubleProperty iva    = new SimpleDoubleProperty(0.0);
    private final DoubleProperty total  = new SimpleDoubleProperty(0.0);

    // — Estado y metadatos —
    private final StringProperty estado = new SimpleStringProperty("Activo");
    private String direccion;
    private boolean activa = true;

    // Tasa de IVA, “quemada” en 0%
    private static final double IVA_RATE = 0.0;

    public Venta() {
        // Recalcula automáticamente cuando cambian los ítems
        items.addListener((ListChangeListener<ItemVenta>) c -> calcularTotales());
    }

    public Venta(String id, String tipoDestino, String cliente, String direccion, LocalDate fecha) {
        this();
        setId(id);
        setTipoDestino(tipoDestino);
        setCliente(cliente);
        this.direccion = direccion;
        setFecha(fecha);
    }

    /** Agrega un ítem y recalcula totales. */
    public void addItem(ItemVenta item) {
        items.add(item);
    }

    /** Elimina un ítem y recalcula totales. */
    public void removeItem(ItemVenta item) {
        items.remove(item);
    }

    /** Recalcula subtotal, IVA y total. */
    private void calcularTotales() {
        double sub = items.stream()
                .mapToDouble(ItemVenta::getPrecioTotal)
                .sum();
        precio.set(sub);

        double calcIva = sub * IVA_RATE;
        iva.set(calcIva);

        total.set(sub + calcIva);
    }

    /** Genera un resumen concatenado de las descripciones de los ítems. */
    public String getDetalleResumen() {
        return "Flores Varias";
    }

    // —— Getters, setters y propiedades JavaFX —— //

    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }
    public StringProperty idProperty() { return id; }

    public String getTipoDestino() { return tipoDestino.get(); }
    public void setTipoDestino(String td) { this.tipoDestino.set(td); }
    public StringProperty tipoDestinoProperty() { return tipoDestino; }

    public String getServicio() { return servicio.get(); }
    public StringProperty servicioProperty() { return servicio; }

    public String getCliente() { return cliente.get(); }
    public void setCliente(String c) { this.cliente.set(c); }
    public StringProperty clienteProperty() { return cliente; }

    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate f) { this.fecha.set(f); }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }

    public double getPrecio() { return precio.get(); }
    public DoubleProperty precioProperty() { return precio; }

    public double getIva() { return iva.get(); }
    public ObservableValue<Double> ivaProperty() {
        // asObject() lo convierte a ObservableValue<Double>
        return iva.asObject();
    }

    public double getTotal() { return total.get(); }
    public void setTotal(double t) { this.total.set(t); }
    public DoubleProperty totalProperty() { return total; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String e) { this.estado.set(e); }
    public StringProperty estadoProperty() { return estado; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String d) { this.direccion = d; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean a) { this.activa = a; }

    public void setPrecio(double precio) { this.precio.set(precio); }
    public void setIva(double iva)     { this.iva.set(iva); }
    //public void setTotal(double total) { this.total.set(total); }

    public ObservableList<ItemVenta> getItems() { return items; }

    public void setServicio(String trim) {
        this.servicio.set(trim);
    }
}
