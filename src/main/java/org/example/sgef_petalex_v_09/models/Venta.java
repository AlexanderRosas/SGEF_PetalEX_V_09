package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Modelo para representar una venta en el sistema.
 */
public class Venta {
    // Propiedades de la venta
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty tipoDestino = new SimpleStringProperty();
    private final StringProperty servicio = new SimpleStringProperty("Ventas");
    private final StringProperty cliente = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();

    // Detalle de ítems
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    // Totales calculados
    private final DoubleProperty precio = new SimpleDoubleProperty(0.0);
    private ObservableValue<Double> iva;
    private final DoubleProperty total = new SimpleDoubleProperty(0.0);

    // Estado y otros campos
    private final StringProperty estado = new SimpleStringProperty("Activo");
    private String direccion;
    private boolean activa = true;

    private static final double IVA_RATE = 0.15;
    private double subtotal;

    // Constructors
    public Venta() {
        this.fecha.set(LocalDate.now());
        calcularTotales();
    }

    public Venta(String id, String tipoDestino, String cliente, String direccion, LocalDate fecha) {
        this();
        this.id.set(id);
        this.tipoDestino.set(tipoDestino);
        this.cliente.set(cliente);
        this.direccion = direccion;
        this.fecha.set(fecha);
    }

    // Métodos para ítems
    public void addItem(ItemVenta item) {
        items.add(item);
        calcularTotales();
    }

    public void removeItem(ItemVenta item) {
        items.remove(item);
        calcularTotales();
    }

    private void calcularTotales() {
        double sum = items.stream()
                .mapToDouble(ItemVenta::getPrecioTotal)
                .sum();
        precio.set(sum);
        total.doubleValue();
    }

    // Resúmenes
    public Object getDetalleResumen() {
        return items.stream()
                .map(ItemVenta::getDescripcion)
                .collect(Collectors.toList());
    }

    // Getters, setters, properties
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }
    public StringProperty idProperty() { return id; }

    public String getTipoDestino() { return tipoDestino.get(); }
    public void setTipoDestino(String tipo) { this.tipoDestino.set(tipo); }
    public StringProperty tipoDestinoProperty() { return tipoDestino; }

    public String getServicio() { return servicio.get(); }
    public StringProperty servicioProperty() { return servicio; }

    public String getCliente() { return cliente.get(); }
    public void setCliente(String cliente) { this.cliente.set(cliente); }
    public StringProperty clienteProperty() { return cliente; }

    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate fecha) { this.fecha.set(fecha); }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }

    public double getPrecio() { return precio.get(); }
    public DoubleProperty precioProperty() { return precio; }

    public double getIva() { return iva.getValue(); }
    public ObservableValue<Double> ivaProperty() { return iva; }

    public double getTotal() { return total.get(); }
    public void setTotal(double sum) { this.total.set(sum); }
    public DoubleProperty totalProperty() { return total; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public StringProperty estadoProperty() { return estado; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public ObservableList<ItemVenta> getItems() { return items; }

    public void setClienteNombre(String clienteNombre) { this.cliente.set(clienteNombre); }

    public void setServicio(String trim) {
        this.servicio.set(trim);
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setIva(ObservableValue<Double> iva) {
        this.iva = iva;
    }


}