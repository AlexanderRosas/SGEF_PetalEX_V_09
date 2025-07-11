package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;

public class ItemVenta {
    private final IntegerProperty item        = new SimpleIntegerProperty();
    private final StringProperty variedad     = new SimpleStringProperty();
    private final StringProperty paquete      = new SimpleStringProperty();
    private final IntegerProperty cantidad    = new SimpleIntegerProperty();
    private final DoubleProperty precioUnit   = new SimpleDoubleProperty();
    private final DoubleProperty precioTotal  = new SimpleDoubleProperty();

    public ItemVenta() {}

    public int getItem() { return item.get(); }
    public void setItem(int i) { item.set(i); }
    public IntegerProperty itemProperty() { return item; }

    public String getVariedad() { return variedad.get(); }
    public void setVariedad(String v) { variedad.set(v); }
    public StringProperty variedadProperty() { return variedad; }

    public String getPaquete() { return paquete.get(); }
    public void setPaquete(String p) { paquete.set(p); }
    public StringProperty paqueteProperty() { return paquete; }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int c) { cantidad.set(c); }
    public IntegerProperty cantidadProperty() { return cantidad; }

    public double getPrecioUnit() { return precioUnit.get(); }
    public void setPrecioUnit(double p) { precioUnit.set(p); }
    public DoubleProperty precioUnitProperty() { return precioUnit; }

    public double getPrecioTotal() { return precioTotal.get(); }
    public void setPrecioTotal(double t) { precioTotal.set(t); }
    public DoubleProperty precioTotalProperty() { return precioTotal; }
}
