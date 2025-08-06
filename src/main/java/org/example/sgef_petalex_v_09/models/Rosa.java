package org.example.sgef_petalex_v_09.models;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class Rosa {

    public enum TipoRosa {
        FREEDOM, EXPLORER, MONDIAL, PINKMONDIAL,
        GOTCHA, QUEEN_SAND, NINA, PLAYA_BLANCA,
        MOMENTUM, PINK_FLOYD, VENDELA, HERMOSA;
    }



    private String nombre;
    private File imagen;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public File getImagen() {
        return imagen;
    }

    public void setImagen(File imagen) {
        this.imagen = imagen;
    }

    public Rosa(String nombre, File imagen, TipoRosa tipoRosa, TipoCorte tipoCorte, int cantidad) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.tipoRosa = tipoRosa;
        this.tipoCorte = tipoCorte;
        this.cantidad = cantidad;
    }

    // Getter/Setters...

    public enum TipoCorte {
        RUSO(70), AMERICANO(80);

        private final int largoEnCm;

        TipoCorte(int largoEnCm) {
            this.largoEnCm = largoEnCm;
        }

        public int getLargoEnCm() {
            return largoEnCm;
        }
    }
    public enum TipoEmpaque {
    CAJA_TABACO("Caja Tabaco", 125, "Mercados premium (EE.UU./Canadá)"),
    CAJA_FULL("Caja Full", 350, "Mercados masivos (Rusia/Holanda)"),
    CUARTOS("Cuartos", 125, "Pedidos pequeños");

    private final String nombre;
    private final int cantidad;  // cantidad fija
    private final String destino;

    TipoEmpaque(String nombre, int cantidad, String destino) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.destino = destino;
    }

    // Cambiar la validación para solo permitir exactamente esa cantidad fija
    public boolean permiteCantidad(int cantidad) {
        return this.cantidad == cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getDestino() {
        return destino;
    }

    // Detectar empaque por cantidad exacta
    public static Optional<TipoEmpaque> detectarPorCantidad(int cantidad) {
        return Arrays.stream(values())
                .filter(empaque -> empaque.permiteCantidad(cantidad))
                .findFirst();
    }
}

    private TipoRosa tipoRosa;
    private TipoCorte tipoCorte;
    private int cantidad;

    public Rosa(TipoRosa tipoRosa, TipoCorte tipoCorte, int cantidad) {
        this.tipoRosa = tipoRosa;
        this.tipoCorte = tipoCorte;
        this.cantidad = cantidad;
    }

    /**
     * Calcula el costo total basado en precio unitario externo,
     * pues precio no está aquí.
     * 
     * @param precioUnitario precio unitario definido por proveedor
     * @return costo total redondeado a 2 decimales
     */
    public double calcularCostoTotal(double precioUnitario) {
        return Math.round(cantidad * precioUnitario * 100.0) / 100.0;
    }

    public Optional<TipoEmpaque> getTipoEmpaqueSugerido() {
        return TipoEmpaque.detectarPorCantidad(cantidad);
    }

    // Getters y Setters
    public TipoRosa getTipoRosa() {
        return tipoRosa;
    }

    public void setTipoRosa(TipoRosa tipoRosa) {
        this.tipoRosa = tipoRosa;
    }

    public TipoCorte getTipoCorte() {
        return tipoCorte;
    }

    public void setTipoCorte(TipoCorte tipoCorte) {
        this.tipoCorte = tipoCorte;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
