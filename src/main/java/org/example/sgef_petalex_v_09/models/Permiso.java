package org.example.sgef_petalex_v_09.models;

public class Permiso {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String modulo;

    public Permiso(String codigo, String nombre, String descripcion, String modulo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.modulo = modulo;
    }

    // Getters y setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    @Override
    public String toString() {
        return nombre;
    }
}