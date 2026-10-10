package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.NotificadorBajoStock;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de reportes que implementa el puerto NotificadorBajoStock del dominio.
 * Cumple la regla de arquitectura: la dependencia fluye de reportes (infraestructura)
 * hacia inventario (dominio), eliminando cualquier ciclo entre paquetes.
 */
@Service
public class ReporteBajoStockService implements NotificadorBajoStock {

    private final InventarioService inventarioService;
    private final List<String> alertas = new ArrayList<>();

    public ReporteBajoStockService(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Override
    public void notificar(Producto producto, int cantidadReorden) {
        alertas.add("Alerta de bajo stock: " + producto.getNombre() + " requiere reordenar " + cantidadReorden + " unidades");
    }

    public String generarReporte() {
        return "Reporte de bajo stock: " + alertas.size() + " alertas registradas";
    }

    public List<String> getAlertas() {
        return new ArrayList<>(alertas);
    }
}
