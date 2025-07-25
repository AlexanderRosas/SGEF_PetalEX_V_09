package org.example.sgef_petalex_v_09.models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.example.sgef_petalex_v_09.models.Rosa.TipoRosa;

public class Proveedor {
    private String ruc;
    private String nombre;
    private String razon_social;
    private String telefono;
    private String direccion;
    private String cuenta_bancaria;
    private String correo;
    private LocalDate fecha_registro;
    private String estado;
    private Map<TipoRosa, Double> preciosPorTipoRosa = new HashMap<>();

    // Constructor vacío
    public Proveedor() {
        this.fecha_registro = LocalDate.now();
        this.estado = "Activo";
    }

    // Constructor básico (para compatibilidad)
    public Proveedor(String ruc, String nombre, String telefono, String direccion, String estado) {
        this();
        this.ruc = ruc;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
        this.razon_social = nombre; // Por defecto igual al nombre
    }

    // Constructor completo
    public Proveedor(String ruc, String nombre, String razon_social, String telefono,
            String direccion, String cuenta_bancaria, String correo,
            LocalDate fecha_registro, String estado) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.razon_social = razon_social;
        this.telefono = telefono;
        this.direccion = direccion;
        this.cuenta_bancaria = cuenta_bancaria;
        this.correo = correo;
        this.fecha_registro = fecha_registro != null ? fecha_registro : LocalDate.now();
        this.estado = estado != null ? estado : "Activo";
    }

    // Getters y Setters
    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCuenta_bancaria() {
        return cuenta_bancaria;
    }

    public void setCuenta_bancaria(String cuenta_bancaria) {
        this.cuenta_bancaria = cuenta_bancaria;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDate fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Métodos auxiliares
    public boolean isActivo() {
        return "Activo".equalsIgnoreCase(estado);
    }

    public void activar() {
        this.estado = "Activo";
    }

    public void inactivar() {
        this.estado = "Inactivo";
    }

    public void toggleEstado() {
        this.estado = isActivo() ? "Inactivo" : "Activo";
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "ruc='" + ruc + '\'' +
                ", nombre='" + nombre + '\'' +
                ", razon_social='" + razon_social + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }

    public void setPrecioTipoRosa(TipoRosa tipo, double precioUnitarioSinIVA) {
        preciosPorTipoRosa.put(tipo, precioUnitarioSinIVA);
    }

    /**
     * Obtiene el precio unitario SIN IVA para un tipo de rosa dado.
     * Devuelve null si no está definido.
     */
    public Double getPrecioTipoRosa(TipoRosa tipo) {
        return preciosPorTipoRosa.get(tipo);
    }

    /**
     * Obtiene el mapa completo de precios por tipo de rosa.
     */
    public Map<TipoRosa, Double> getPreciosPorTipoRosa() {
        return preciosPorTipoRosa;
    }

}