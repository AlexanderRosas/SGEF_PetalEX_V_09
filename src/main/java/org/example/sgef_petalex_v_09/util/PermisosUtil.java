package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.Permiso;
import org.example.sgef_petalex_v_09.models.Usuario;

import java.util.*;

public class PermisosUtil {
    private static final Map<String, List<Permiso>> PERMISOS_POR_ROL = new HashMap<>();
    private static final List<Permiso> TODOS_LOS_PERMISOS = new ArrayList<>();

    static {
        // Inicializar permisos
        inicializarPermisos();
        // Asignar permisos por rol
        asignarPermisosPorRol();
    }

    private static void inicializarPermisos() {
        // Ventas
        TODOS_LOS_PERMISOS.add(new Permiso("VENTA_CREAR", "Crear Ventas", "Permite crear nuevas ventas", "Ventas"));
        TODOS_LOS_PERMISOS.add(new Permiso("VENTA_MODIFICAR", "Modificar Ventas", "Permite modificar ventas existentes", "Ventas"));
        TODOS_LOS_PERMISOS.add(new Permiso("VENTA_ANULAR", "Anular Ventas", "Permite anular ventas", "Ventas"));
        
        // Administración
        TODOS_LOS_PERMISOS.add(new Permiso("USUARIO_ADMIN", "Administrar Usuarios", "Gestión completa de usuarios", "Administración"));
        TODOS_LOS_PERMISOS.add(new Permiso("ROL_ADMIN", "Administrar Roles", "Gestión de roles y permisos", "Administración"));
        TODOS_LOS_PERMISOS.add(new Permiso("PARAM_ADMIN", "Administrar Parámetros", "Configuración del sistema", "Administración"));
        
        // Finanzas
        TODOS_LOS_PERMISOS.add(new Permiso("REPORTE_VER", "Ver Reportes", "Acceso a reportes financieros", "Finanzas"));
        TODOS_LOS_PERMISOS.add(new Permiso("REPORTE_EXPORTAR", "Exportar Reportes", "Exportar reportes financieros", "Finanzas"));
    }

    private static void asignarPermisosPorRol() {
        // Administrador
        List<Permiso> permisosAdmin = new ArrayList<>(TODOS_LOS_PERMISOS);
        PERMISOS_POR_ROL.put("Administrador", permisosAdmin);
        
        // Ventas
        List<Permiso> permisosVentas = TODOS_LOS_PERMISOS.stream()
            .filter(p -> p.getModulo().equals("Ventas"))
            .toList();
        PERMISOS_POR_ROL.put("Ventas", new ArrayList<>(permisosVentas));
        
        // Finanzas
        List<Permiso> permisosFinanzas = TODOS_LOS_PERMISOS.stream()
            .filter(p -> p.getModulo().equals("Finanzas"))
            .toList();
        PERMISOS_POR_ROL.put("Finanzas", new ArrayList<>(permisosFinanzas));
        
        // Gerente
        List<Permiso> permisosGerente = new ArrayList<>();
        permisosGerente.addAll(permisosVentas);
        permisosGerente.addAll(permisosFinanzas);
        PERMISOS_POR_ROL.put("Gerente", permisosGerente);
    }

    public static List<Permiso> getPermisosPorRol(String rol) {
        return PERMISOS_POR_ROL.getOrDefault(rol, new ArrayList<>());
    }

    public static List<Permiso> getTodosLosPermisos() {
        return new ArrayList<>(TODOS_LOS_PERMISOS);
    }

    public static boolean tienePermiso(Usuario usuario, String codigoPermiso) {
        if (usuario == null || usuario.getPermisos() == null) return false;
        return Arrays.stream(usuario.getPermisos().split(","))
                .map(String::trim)
                .anyMatch(p -> p.equals(codigoPermiso));
    }
}