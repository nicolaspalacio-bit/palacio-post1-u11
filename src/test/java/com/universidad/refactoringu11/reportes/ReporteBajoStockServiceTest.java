package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReporteBajoStockServiceTest {

    @Test
    void notificar_registraAlertaYGeneraReporte() {
        ReporteBajoStockService reporteService = new ReporteBajoStockService();

        Producto producto = new Producto(1L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 2, 5);
        reporteService.notificar(producto, 13);

        assertEquals(1, reporteService.getAlertas().size());
        assertTrue(reporteService.generarReporte().contains("1 alertas"));
    }
}
