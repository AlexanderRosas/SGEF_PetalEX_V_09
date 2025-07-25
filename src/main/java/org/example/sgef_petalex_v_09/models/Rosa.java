package org.example.sgef_petalex_v_09.models;

import java.util.Arrays;
import java.util.Optional;

public class Rosa {

    public enum TipoRosa {
        FREEDOM, EXPLORER, MONDIAL, PINKMONDIAL,
        GOTCHA, QUEEN_SAND, NINA, PLAYA_BLANCA,
        MOMENTUM, PINK_FLOYD, VENDELA, HERMOSA;
    }

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
        CAJA_TABACO("Caja Tabaco", 100, 125, "Mercados premium (EE.UU./Canadá)"),
        CAJA_FULL("Caja Full", 300, 350, "Mercados masivos (Rusia/Holanda)"),
        CUARTOS("Cuartos", 100, 125, "Pedidos pequeños");

        private final String nombre;
        private final int cantidadMin;
        private final int cantidadMax;
        private final String destino;

        TipoEmpaque(String nombre, int cantidadMin, int cantidadMax, String destino) {
            this.nombre = nombre;
            this.cantidadMin = cantidadMin;
            this.cantidadMax = cantidadMax;
            this.destino = destino;
        }

        public boolean permiteCantidad(int cantidad) {
            return cantidad >= cantidadMin && cantidad <= cantidadMax;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDestino() {
            return destino;
        }

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
