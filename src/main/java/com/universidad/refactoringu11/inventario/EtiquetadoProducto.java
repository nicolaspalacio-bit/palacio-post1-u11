package com.universidad.refactoringu11.inventario;

/**
 * Servicio de etiquetado que delega polimorficamente la generacion del formato al tipo de producto.
 * Elimina la duplicacion estructural de control de flujo frente a ClasificadorProducto.
 */
public class EtiquetadoProducto {

    public String generarEtiqueta(Producto producto) {
        return producto.getTipo().generarEtiqueta(producto.getNombre());
    }
}
