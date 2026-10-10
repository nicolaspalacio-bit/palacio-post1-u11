package com.universidad.refactoringu11.inventario;

/**
 * Puerto de salida del dominio para notificaciones de bajo stock.
 * Cumple con el Principio de Inversion de Dependencias (DIP) y la arquitectura
 * de Puertos y Adaptadores, permitiendo que el paquete de mayor nivel (inventario)
 * defina la abstraccion sin depender de paquetes de soporte o infraestructura (reportes).
 */
public interface NotificadorBajoStock {
    void notificar(Producto producto, int cantidadReorden);
}
