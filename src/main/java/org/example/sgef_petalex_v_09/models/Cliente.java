package org.example.sgef_petalex_v_09.models;

import java.time.LocalDateTime;

public class Cliente {
    private String id;
    private String nombre;
    private String identificadorEmpresarial; // RUC, EIN, VAT, etc.
    private String pais;
    private String direccion;
    private String telefono;
    private String correo;
    private String estado; // "Activa" o "Inactiva"
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;

    public Cliente() {
    }

    public Cliente(String id, String nombre, String identificadorEmpresarial, String pais,
            String direccion, String telefono, String correo, String estado,
            String usuarioModificacion, LocalDateTime fechaModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.identificadorEmpresarial = identificadorEmpresarial;
        this.pais = pais;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
        this.usuarioModificacion = usuarioModificacion;
        this.fechaModificacion = fechaModificacion;
    }

    // Getters y setters
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

    public String getIdentificadorEmpresarial() {
        return identificadorEmpresarial;
    }

    public void setIdentificadorEmpresarial(String identificadorEmpresarial) {
        this.identificadorEmpresarial = identificadorEmpresarial;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", identificador='" + identificadorEmpresarial + '\'' +
                ", pais='" + pais + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", estado='" + estado + '\'' +
                ", modificadoPor='" + usuarioModificacion + '\'' +
                '}';
    }

    public String toCSV() {
        return String.join(";",
                id,
                nombre,
                identificadorEmpresarial,
                pais,
                direccion,
                telefono,
                correo,
                estado,
                usuarioModificacion,
                fechaModificacion != null ? fechaModificacion.toString() : "");
    }

}