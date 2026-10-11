package com.universidad.refactoringu11.inventario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService {

    private final List<Producto> productos = new ArrayList<>();
    private final List<String> movimientos = new ArrayList<>();
    private final CalculadoraReorden calculadoraReorden;
    private NotificadorBajoStock notificador;

    public InventarioService() {
        this(new CalculadoraReorden());
    }

    public InventarioService(CalculadoraReorden calculadoraReorden) {
        this.calculadoraReorden = calculadoraReorden;
    }

    @Autowired(required = false)
    public void setNotificador(NotificadorBajoStock notificador) {
        this.notificador = notificador;
    }

    public NotificadorBajoStock getNotificador() {
        return notificador;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public String actualizarInventario(Long productoId, int cantidad, String tipoMovimiento) {
        if (!esValido(productoId, cantidad, tipoMovimiento)) return "ERROR_DATOS_INVALIDOS";
        Producto producto = buscarProducto(productoId);
        if (producto == null) return "ERROR_PRODUCTO_NO_EXISTE";
        int nuevoStock = calcularNuevoStock(producto, cantidad, tipoMovimiento);
        if (nuevoStock < 0) return "ERROR_STOCK_INSUFICIENTE";
        producto.setStock(nuevoStock);
        registrarAuditoria(tipoMovimiento, cantidad, producto, nuevoStock);
        return procesarResultadoReorden(producto, nuevoStock);
    }

    private boolean esValido(Long productoId, int cantidad, String tipoMovimiento) {
        return productoId != null && cantidad != 0
                && ("ENTRADA".equals(tipoMovimiento) || "SALIDA".equals(tipoMovimiento));
    }

    private Producto buscarProducto(Long productoId) {
        for (Producto p : productos) {
            if (p.getId().equals(productoId)) return p;
        }
        return null;
    }

    private int calcularNuevoStock(Producto producto, int cantidad, String tipoMovimiento) {
        return "ENTRADA".equals(tipoMovimiento)
                ? producto.getStock() + cantidad
                : producto.getStock() - cantidad;
    }

    private void registrarAuditoria(String tipoMovimiento, int cantidad, Producto producto, int nuevoStock) {
        String registro = tipoMovimiento + " de " + cantidad + " unidades de "
                + producto.getNombre() + " -> stock final: " + nuevoStock;
        movimientos.add(registro);
        System.out.println(registro);
    }

    private String procesarResultadoReorden(Producto producto, int nuevoStock) {
        if (calculadoraReorden.necesitaReorden(producto, nuevoStock)) {
            int reorden = calculadoraReorden.calcularCantidadReorden(producto, nuevoStock);
            if (notificador != null) {
                notificador.notificar(producto, reorden);
            }
            System.out.println("ALERTA: " + producto.getNombre()
                    + " bajo stock minimo. Reordenar " + reorden + " unidades.");
            return "OK_BAJO_STOCK_REORDEN_" + reorden;
        }
        return "OK_" + nuevoStock;
    }
}
