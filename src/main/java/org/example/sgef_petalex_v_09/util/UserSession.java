package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.models.Venta;

public class UserSession {
    private static Usuario usuarioActual;
    private static String tipoDestino;
    private static Venta ventaSeleccionada;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static String getPuntoEmision() {
        return usuarioActual != null ? "001" : null;
    }

    public static String getSucursal() {
        return usuarioActual != null ? usuarioActual.getSucursal() : null;
    }

    public static String getRuc() {
        return usuarioActual != null ? usuarioActual.getRuc() : null;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static String getTipoDestino() {
        return tipoDestino;
    }

    public static void setTipoDestino(String tipo) {
        tipoDestino = tipo;
    }

    public static Venta getVentaSeleccionada() {
        return ventaSeleccionada;
    }

    public static void setVentaSeleccionada(Venta venta) {
        ventaSeleccionada = venta;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        tipoDestino = null;
        ventaSeleccionada = null;
    }
}