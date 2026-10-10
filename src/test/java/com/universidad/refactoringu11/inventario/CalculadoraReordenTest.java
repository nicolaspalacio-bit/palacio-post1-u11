package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraReordenTest {

    private final CalculadoraReorden calculadora = new CalculadoraReorden();

    @Test
    void cuandoStockEsMayorOIgualAlMinimo_noNecesitaReorden() {
        Producto p = new Producto(1L, "Libro", TipoProducto.LIBRO, 10.0, 10, 5);
        assertFalse(calculadora.necesitaReorden(p, 5));
        assertEquals(0, calculadora.calcularCantidadReorden(p, 5));
    }

    @Test
    void cuandoStockEsMenorAlMinimo_calculaCantidadReorden() {
        Producto p = new Producto(1L, "Libro", TipoProducto.LIBRO, 10.0, 10, 5);
        // stockMinimo=5, stockActual=3 -> reorden = (5 * 3) - 3 = 12
        assertTrue(calculadora.necesitaReorden(p, 3));
        assertEquals(12, calculadora.calcularCantidadReorden(p, 3));
    }
}
