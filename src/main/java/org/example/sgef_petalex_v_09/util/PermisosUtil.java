package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.Permiso;
import org.example.sgef_petalex_v_09.models.Usuario;

import java.util.*;

public class PermisosUtil {
    private static final Map<String, List<Permiso>> PERMISOS_POR_ROL = new HashMap<>();
    private static final List<Permiso> TODOS_LOS_MODULOS = new ArrayList<>();

    static {
        inicializarModulos();
        asignarModulosPorRol();
    }

    private static void inicializarModulos() {
        TODOS_LOS_MODULOS.add(new Permiso("CLIENTES", "Clientes", "Acceso al módulo de clientes", "Clientes"));
        TODOS_LOS_MODULOS.add(
                new Permiso("PROVEEDORES", "Proveedores", "Acceso al módulo de proveedores", "Proveedores"));
        TODOS_LOS_MODULOS.add(new Permiso("COMPRAS", "Compras", "Acceso al módulo de compras", "Compras"));
        TODOS_LOS_MODULOS.add(new Permiso("VENTAS", "Ventas", "Acceso al módulo de ventas", "Ventas"));
        TODOS_LOS_MODULOS.add(new Permiso("ADMINISTRACION", "Administración",
                "Acceso al módulo de administración del sistema", "Administración"));
        TODOS_LOS_MODULOS.add(
                new Permiso("GUIA_AEREA", "Guía Aérea", "Acceso a actualizar solo la guía aérea", "Logística"));
    }

    private static void asignarModulosPorRol() {
        PERMISOS_POR_ROL.put("Administrador", List.of(
                new Permiso("CLIENTES", "Clientes", "...", "Clientes"),
                new Permiso("PROVEEDORES", "Proveedores", "...", "Proveedores"),
                new Permiso("COMPRAS", "Compras", "...", "Compras"),
                new Permiso("VENTAS", "Ventas", "...", "Ventas"),
                new Permiso("ADMINISTRACION", "Administración", "...", "Administración")));

        PERMISOS_POR_ROL.put("Gerente", List.of(
                new Permiso("CLIENTES", "Clientes", "...", "Clientes"),
                new Permiso("PROVEEDORES", "Proveedores", "...", "Proveedores"),
                new Permiso("COMPRAS", "Compras", "...", "Compras"),
                new Permiso("ADMINISTRACION", "Administración", "...", "Administración")));

        PERMISOS_POR_ROL.put("Ventas", List.of(
                new Permiso("CLIENTES", "Clientes", "...", "Clientes"),
                new Permiso("COMPRAS", "Compras", "...", "Compras"),
                new Permiso("VENTAS", "Ventas", "...", "Ventas")));

        PERMISOS_POR_ROL.put("Contabilidad", List.of(
                new Permiso("PROVEEDORES", "Proveedores", "...", "Proveedores"),
                new Permiso("COMPRAS", "Compras", "...", "Compras"),
                new Permiso("VENTAS", "Ventas", "...", "Ventas")));

        PERMISOS_POR_ROL.put("Logística", List.of(
                new Permiso("LOGÍSTICA", "Guía Aérea", "...", "Logística")));
    }

    public static List<Permiso> getModulosPorRol(String rol) {
        return PERMISOS_POR_ROL.getOrDefault(rol, List.of());
    }

    public static boolean puedeAccederA(Usuario usuario, String moduloCodigo) {
        if (usuario == null || usuario.getPermisos() == null)
            return false;
        return Arrays.stream(usuario.getPermisos().split(","))
                .map(String::trim)
                .anyMatch(p -> p.equals(moduloCodigo));
    }

    public static Set<String> getRolesDisponibles() {
        return PERMISOS_POR_ROL.keySet();
    }

    public static List<Permiso> getTodosLosPermisos() {
        return new ArrayList<>(TODOS_LOS_MODULOS);
    }
}