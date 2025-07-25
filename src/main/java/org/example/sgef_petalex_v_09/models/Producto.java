package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;

public class Producto {
    private String id;
    private String nombre;
    private String unidad_medida; // caja, ramo, tallo
    private String color;
    private String descripcion;
    private String cantidad;
    private double costo_unitario_compra;
    private double costo_flete_unitario;
    private double tasa_iva;
    private int estado_producto; // 0=activo, 1=inactivo
    private LocalDate fecha_compra;
    private LocalDate fecha_vencimiento;
    private String proveedorRuc; // Relación con proveedor

    // Constructor vacío
    public Producto() {
    }

    // Constructor completo
    public Producto(String id, String nombre, String unidad_medida, String color, String descripcion,
            String cantidad, double costo_unitario_compra, double costo_flete_unitario,
            double tasa_iva, int estado_producto,
            LocalDate fecha_compra, LocalDate fecha_vencimiento, String proveedorRuc) {
        this.id = id;
        this.nombre = nombre;
        this.unidad_medida = unidad_medida;
        this.color = color;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.costo_unitario_compra = costo_unitario_compra;
        this.costo_flete_unitario = costo_flete_unitario;
        this.tasa_iva = tasa_iva;
        this.estado_producto = estado_producto;
        this.fecha_compra = fecha_compra;
        this.fecha_vencimiento = fecha_vencimiento;
        this.proveedorRuc = proveedorRuc;
    }

    // Getters y Setters existentes
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad_medida() {
        return unidad_medida;
    }

    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public double getCosto_unitario_compra() {
        return costo_unitario_compra;
    }

    public void setCosto_unitario_compra(double costo_unitario_compra) {
        this.costo_unitario_compra = costo_unitario_compra;
    }

    public double getCosto_flete_unitario() {
        return costo_flete_unitario;
    }

    public void setCosto_flete_unitario(double costo_flete_unitario) {
        this.costo_flete_unitario = costo_flete_unitario;
    }

    public double getTasa_iva() {
        return tasa_iva;
    }

    public void setTasa_iva(double tasa_iva) {
        this.tasa_iva = tasa_iva;
    }

    public int getEstado_producto() {
        return estado_producto;
    }

    public void setEstado_producto(int estado_producto) {
        this.estado_producto = estado_producto;
    }

    // Nuevos getters y setters
    public LocalDate getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(LocalDate fecha_compra) {
        this.fecha_compra = fecha_compra;
    }

    public LocalDate getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(LocalDate fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public String getProveedorRuc() {
        return proveedorRuc;
    }

    public void setProveedorRuc(String proveedorRuc) {
        this.proveedorRuc = proveedorRuc;
    }

    // Métodos auxiliares
    public String getEstadoTexto() {
        return estado_producto == 0 ? "Activo" : "Inactivo";
    }

    public void setEstadoTexto(String estado) {
        this.estado_producto = "Activo".equals(estado) ? 0 : 1;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", color='" + color + '\'' +
                ", estado=" + getEstadoTexto() +
                '}';
    }
}
