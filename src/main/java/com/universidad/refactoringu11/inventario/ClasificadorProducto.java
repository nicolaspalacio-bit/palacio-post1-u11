package com.universidad.refactoringu11.inventario;

/**
 * Servicio clasificador que delega polimorficamente el calculo de recargos al tipo de producto.
 * Elimina la sentencia switch redundante y cumple el Principio Abierto/Cerrado.
 */
public class ClasificadorProducto {

    public double calcularRecargoEnvio(Producto producto) {
        return producto.getTipo().calcularRecargoEnvio(producto.getPrecio());
    }
}
