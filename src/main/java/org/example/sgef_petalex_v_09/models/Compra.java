package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;

public class Compra {
    private int id;
    private Proveedor proveedor; // usa el RUC internamente
    private String tipoRosa;
    private String tipoCorte; // Americano, Ruso, etc.
    private int largoTallo; // en cm
    private int cantidad;
    private String unidad; // "tallos", "cajas"
    private double costoUnitario;
    private LocalDate fechaCompra;

    private EstadoProcesamiento estadoActual;
    private LocalDate fechaHidratacion;
    private LocalDate fechaCuartoFrio;
    private LocalDate fechaEmpaque;
    private LocalDate fechaExportacion;

    private String observaciones;

    public Compra() {}

    public Compra(int id, Proveedor proveedor, String tipoRosa, String tipoCorte, int largoTallo, int cantidad,
                  String unidad, double costoUnitario, LocalDate fechaCompra, EstadoProcesamiento estadoActual,
                  LocalDate fechaHidratacion, LocalDate fechaCuartoFrio, LocalDate fechaEmpaque,
                  LocalDate fechaExportacion, String observaciones) {
        this.id = id;
        this.proveedor = proveedor;
        this.tipoRosa = tipoRosa;
        this.tipoCorte = tipoCorte;
        this.largoTallo = largoTallo;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.costoUnitario = costoUnitario;
        this.fechaCompra = fechaCompra;
        this.estadoActual = estadoActual;
        this.fechaHidratacion = fechaHidratacion;
        this.fechaCuartoFrio = fechaCuartoFrio;
        this.fechaEmpaque = fechaEmpaque;
        this.fechaExportacion = fechaExportacion;
        this.observaciones = observaciones;
    }

    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public String getTipoRosa() { return tipoRosa; }
    public void setTipoRosa(String tipoRosa) { this.tipoRosa = tipoRosa; }

    public String getTipoCorte() { return tipoCorte; }
    public void setTipoCorte(String tipoCorte) { this.tipoCorte = tipoCorte; }

    public int getLargoTallo() { return largoTallo; }
    public void setLargoTallo(int largoTallo) { this.largoTallo = largoTallo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public EstadoProcesamiento getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoProcesamiento estadoActual) { this.estadoActual = estadoActual; }

    public LocalDate getFechaHidratacion() { return fechaHidratacion; }
    public void setFechaHidratacion(LocalDate fechaHidratacion) { this.fechaHidratacion = fechaHidratacion; }

    public LocalDate getFechaCuartoFrio() { return fechaCuartoFrio; }
    public void setFechaCuartoFrio(LocalDate fechaCuartoFrio) { this.fechaCuartoFrio = fechaCuartoFrio; }

    public LocalDate getFechaEmpaque() { return fechaEmpaque; }
    public void setFechaEmpaque(LocalDate fechaEmpaque) { this.fechaEmpaque = fechaEmpaque; }

    public LocalDate getFechaExportacion() { return fechaExportacion; }
    public void setFechaExportacion(LocalDate fechaExportacion) { this.fechaExportacion = fechaExportacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Compra{" +
                "id=" + id +
                ", proveedor=" + proveedor.getNombre() + " (RUC: " + proveedor.getRuc() + ")" +
                ", tipoRosa='" + tipoRosa + '\'' +
                ", tipoCorte='" + tipoCorte + '\'' +
                ", largoTallo=" + largoTallo +
                ", cantidad=" + cantidad +
                ", unidad='" + unidad + '\'' +
                ", costoUnitario=" + costoUnitario +
                ", fechaCompra=" + fechaCompra +
                ", estadoActual=" + estadoActual +
                ", fechaHidratacion=" + fechaHidratacion +
                ", fechaCuartoFrio=" + fechaCuartoFrio +
                ", fechaEmpaque=" + fechaEmpaque +
                ", fechaExportacion=" + fechaExportacion +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
