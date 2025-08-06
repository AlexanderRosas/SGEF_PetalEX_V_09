package org.example.sgef_petalex_v_09.models;

public class Iva {
    private double porcentaje;

    public Iva() { }               // Constructor vacío para CSV-Util
    public Iva(double porcentaje) { this.porcentaje = porcentaje; }

    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }

    @Override
    public String toString() {
        return String.valueOf(porcentaje);
    }
}