package com.universidad.refactoringu11.inventario;

import org.springframework.stereotype.Component;

/**
 * Colaborador especializado extraido para encapsular las politicas de reabastecimiento.
 * Cumple con el Principio de Responsabilidad Unica (SRP) al separar el algoritmo
 * de reorden de la orquestacion de inventario.
 */
@Component
public class CalculadoraReorden {

    public boolean necesitaReorden(Producto producto, int stockActual) {
        return stockActual < producto.getStockMinimo();
    }

    public int calcularCantidadReorden(Producto producto, int stockActual) {
        if (!necesitaReorden(producto, stockActual)) {
            return 0;
        }
        return (producto.getStockMinimo() * 3) - stockActual;
    }
}
