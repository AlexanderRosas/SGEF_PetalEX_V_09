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
    private LocalDate fechaRegistro;

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    private String estado;
    private Map<TipoRosa, Double> preciosPorTipoRosa = new HashMap<>();

    // Nuevos campos
    private LocalDate fechaModificacion;
    private String usuarioResponsable;

    // Constructor vacío
    public Proveedor() {
        this.fechaRegistro = LocalDate.now();
        this.estado = "Activo";
        this.fechaModificacion = LocalDate.now(); // Inicializar con la fecha actual
        this.usuarioResponsable = "Sistema"; // Inicializar con un valor predeterminado
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
            LocalDate fechaRegistro, String estado, LocalDate fechaModificacion, String usuarioResponsable) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.razon_social = razon_social;
        this.telefono = telefono;
        this.direccion = direccion;
        this.cuenta_bancaria = cuenta_bancaria;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
        this.estado = estado != null ? estado : "Activo";
        this.fechaModificacion = fechaModificacion != null ? fechaModificacion : LocalDate.now();
        this.usuarioResponsable = usuarioResponsable != null ? usuarioResponsable : "Sistema";
    }

    // Getters y Setters
    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public String getCuenta_bancaria() {
        return cuenta_bancaria;
    }

    public void setCuenta_bancaria(String cuenta_bancaria) {
        this.cuenta_bancaria = cuenta_bancaria;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public LocalDate getFecha_registro() {
        return fechaRegistro;
    }

    public void setFecha_registro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDate fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    // Métodos auxiliares
    public boolean isActivo() {
        return "Activo".equalsIgnoreCase(estado);
    }

    public void activar() {
        this.estado = "Activo";
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public void inactivar() {
        this.estado = "Inactivo";
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    public void toggleEstado() {
        this.estado = isActivo() ? "Inactivo" : "Activo";
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "ruc='" + ruc + '\'' +
                ", nombre='" + nombre + '\'' +
                ", razon_social='" + razon_social + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaModificacion=" + fechaModificacion +
                ", usuarioResponsable='" + usuarioResponsable + '\'' +
                '}';
    }

    public void setPrecioTipoRosa(TipoRosa tipo, double precioUnitarioSinIVA) {
        preciosPorTipoRosa.put(tipo, precioUnitarioSinIVA);
        this.fechaModificacion = LocalDate.now(); // Actualizar fecha de modificación
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