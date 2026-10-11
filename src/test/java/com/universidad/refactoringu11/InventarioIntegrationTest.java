package com.universidad.refactoringu11;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import com.universidad.refactoringu11.reportes.ReporteBajoStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InventarioIntegrationTest {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ReporteBajoStockService reporteBajoStockService;

    @Test
    void contextoSpringIniciaYNotificacionBajoStockFuncionaPuntaAPunta() {
        // Verificar que los beans se instancian e inyectan correctamente sin ciclo
        assertNotNull(inventarioService, "El bean InventarioService debe estar instanciado por Spring");
        assertNotNull(reporteBajoStockService, "El bean ReporteBajoStockService debe estar instanciado por Spring");
        assertSame(reporteBajoStockService, inventarioService.getNotificador(),
                "Spring debe inyectar el adaptador ReporteBajoStockService en InventarioService");

        // 1. Registrar un producto en el inventario gestionado por Spring
        Producto monitor = new Producto(100L, "Monitor Gamer", TipoProducto.ELECTRONICO, 350.0, 10, 5);
        inventarioService.agregarProducto(monitor);

        // 2. Provocar una salida que deje el stock bajo el minimo (10 - 8 = 2, menor al minimo de 5)
        String resultado = inventarioService.actualizarInventario(100L, 8, "SALIDA");

        // 3. Comprobar resultado del servicio de inventario: formula (5 * 3) - 2 = 13 unidades a reordenar
        assertEquals("OK_BAJO_STOCK_REORDEN_13", resultado);
        assertEquals(2, monitor.getStock());

        // 4. Comprobar que el adaptador recibe la alerta y la cantidad de reorden esperada
        assertEquals(1, reporteBajoStockService.getAlertas().size());
        String alerta = reporteBajoStockService.getAlertas().get(0);
        assertTrue(alerta.contains("Monitor Gamer"));
        assertTrue(alerta.contains("13"));
        assertTrue(reporteBajoStockService.generarReporte().contains("1 alertas"));
    }
}
