package org.example.sgef_petalex_v_09.models;

public class Usuario {
    private final String nombre;
    private final String correo;
    private final String usuario;
    private final String rol;
    private final String estado;

    public Usuario(String nombre, String correo, String usuario, String rol, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.rol = rol;
        this.estado = estado;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getUsuario() { return usuario; }
    public String getRol()     { return rol; }
    public String getEstado()  { return estado; }
}
