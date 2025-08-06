package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;

public class Compra {
    private int id;

    private String ruc; // antes proveedorRuc
    private String tipoRosa;
    private String tipoCorte;
    private double largoTallo; // cambió a double para que coincida con controlador
    private int cantidad;
    private int cantidadDisponible;

    private double costoUnitario;
    private double precioUnitario;
    private double precioTotal;
    private LocalDate fechaCompra;

    private String estadoActual;

    private LocalDate fechaHidratacion;
    private LocalDate fechaCuartoFrio;
    private LocalDate fechaEmpaque;
    private LocalDate fechaExportacion;
    // Reemplaza esto:


    // Por esto:
    private Proveedor proveedor;

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    private String observaciones;

    private LocalDate fechaUltimaActualizacion;
    private String usuarioResponsable;

    public Compra() {
    }

    public Compra(int id, Proveedor proveedor, String tipoRosa, String tipoCorte,
            double largoTallo, int cantidad, double costoUnitario, double precioUnitario,
            LocalDate fechaCompra, String estadoActual) {
        this.id = id;
        this.proveedor = proveedor;
        this.tipoRosa = tipoRosa;
        this.tipoCorte = tipoCorte;
        this.largoTallo = largoTallo;
        this.cantidad = cantidad;
        this.cantidadDisponible = cantidad;
        this.costoUnitario = costoUnitario;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioUnitario * cantidad;
        this.fechaCompra = fechaCompra;
        this.estadoActual = estadoActual;
        this.fechaUltimaActualizacion = LocalDate.now();
        this.usuarioResponsable = "Sistema";
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

 

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTipoRosa() {
        return tipoRosa;
    }

    public void setTipoRosa(String tipoRosa) {
        this.tipoRosa = tipoRosa;
    }

    public String getTipoCorte() {
        return tipoCorte;
    }

    public void setTipoCorte(String tipoCorte) {
        this.tipoCorte = tipoCorte;
    }

    public double getLargoTallo() {
        return largoTallo;
    }

    public void setLargoTallo(double largoTallo) {
        this.largoTallo = largoTallo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.cantidadDisponible = cantidad; // Actualizar cantidad disponible
        this.precioTotal = this.precioUnitario * cantidad; // Actualizar precio total
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioUnitario * this.cantidad; // Actualizar precio total
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public LocalDate getFechaHidratacion() {
        return fechaHidratacion;
    }

    public void setFechaHidratacion(LocalDate fechaHidratacion) {
        this.fechaHidratacion = fechaHidratacion;
    }

    public LocalDate getFechaCuartoFrio() {
        return fechaCuartoFrio;
    }

    public void setFechaCuartoFrio(LocalDate fechaCuartoFrio) {
        this.fechaCuartoFrio = fechaCuartoFrio;
    }

    public LocalDate getFechaEmpaque() {
        return fechaEmpaque;
    }

    public void setFechaEmpaque(LocalDate fechaEmpaque) {
        this.fechaEmpaque = fechaEmpaque;
    }

    public LocalDate getFechaExportacion() {
        return fechaExportacion;
    }

    public void setFechaExportacion(LocalDate fechaExportacion) {
        this.fechaExportacion = fechaExportacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDate fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public void descontarRosas(int cantidad) {
        this.cantidadDisponible -= cantidad;
    }

    public void setFechaEstado(String estado, LocalDate fecha) {
        switch (estado) {
            case "Hidratado":
                setFechaHidratacion(fecha);
                break;
            case "En Cuarto Frío":
                setFechaCuartoFrio(fecha);
                break;
            case "Empacado":
                setFechaEmpaque(fecha);
                break;
            case "Exportado":
                setFechaExportacion(fecha);
                break;
            default:
                // Para "Recibido" o estados no listados no se guarda fecha especial
                break;
        }
    }

    @Override
    public String toString() {
        return "Compra{" +
                "id=" + id +
                ", proveedor='" + proveedor + '\'' +
                ", ruc='" + ruc + '\'' +
                ", tipoRosa='" + tipoRosa + '\'' +
                ", tipoCorte='" + tipoCorte + '\'' +
                ", largoTallo=" + largoTallo +
                ", cantidad=" + cantidad +
                ", cantidadDisponible=" + cantidadDisponible +
                ", costoUnitario=" + costoUnitario +
                ", precioUnitario=" + precioUnitario +
                ", precioTotal=" + precioTotal +
                ", fechaCompra=" + fechaCompra +
                ", estadoActual='" + estadoActual + '\'' +
                ", fechaHidratacion=" + fechaHidratacion +
                ", fechaCuartoFrio=" + fechaCuartoFrio +
                ", fechaEmpaque=" + fechaEmpaque +
                ", fechaExportacion=" + fechaExportacion +
                ", observaciones='" + observaciones + '\'' +
                ", fechaUltimaActualizacion=" + fechaUltimaActualizacion +
                ", usuarioResponsable='" + usuarioResponsable + '\'' +
                '}';
    }
}