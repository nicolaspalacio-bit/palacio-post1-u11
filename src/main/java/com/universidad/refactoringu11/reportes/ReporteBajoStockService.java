package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.NotificadorBajoStock;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de reportes que implementa el puerto NotificadorBajoStock del dominio.
 * Cumple la regla de arquitectura: la dependencia fluye de reportes (infraestructura)
 * hacia inventario (dominio), eliminando cualquier ciclo entre paquetes y evitando
 * dependencias circulares a nivel de componentes o beans de Spring.
 */
@Service
public class ReporteBajoStockService implements NotificadorBajoStock {

    private final List<String> alertas = new ArrayList<>();

    public ReporteBajoStockService() {
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
