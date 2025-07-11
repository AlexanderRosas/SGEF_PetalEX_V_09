package org.example.sgef_petalex_v_09.models;

public class Usuario {
    private String nombre;
    private String correo;
    private String usuario;
    private String rol;
    private String estado;
    private int id;

    public Usuario(String nombre, String correo, String usuario, String rol, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.rol = rol;
        this.estado = estado;
    }

    public Usuario() {

    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getUsuario() { return usuario; }
    public String getRol()     { return rol; }
    public String getEstado()  { return estado; }
    public int getId()         { return id; }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
