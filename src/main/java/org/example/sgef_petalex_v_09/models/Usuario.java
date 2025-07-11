package org.example.sgef_petalex_v_09.models;

public class Usuario {
    private String nombre;
    private String correo;
    private String usuario;
    private String rol;
    private String estado;
    private String permisos;  // Nuevo atributo para permisos
    private int id;

    // Constructor sin permisos (para compatibilidad)
    public Usuario(String nombre, String correo, String usuario, String rol, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.rol = rol;
        this.estado = estado;
        this.permisos = ""; // valor por defecto vacío
    }

    // Constructor con permisos
    public Usuario(String nombre, String correo, String usuario, String rol, String estado, String permisos) {
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.rol = rol;
        this.estado = estado;
        this.permisos = permisos;
    }

    // Constructor vacío
    public Usuario() {
        this.permisos = "";
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
    public String getPermisos() { return permisos; }  // Nuevo getter
    public int getId() { return id; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setRol(String rol) { this.rol = rol; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setPermisos(String permisos) { this.permisos = permisos; }  // Nuevo setter
}
