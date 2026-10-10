# ADR-04: Granularidad de Notificacion: Evento Individual vs Procesamiento por Lote

## Estado
Aceptado

## Contexto
Al formalizar el contrato de la interfaz `NotificadorBajoStock`, fue necesario definir la granularidad de comunicacion para la alerta de reabastecimiento:
1. Notificacion granular por evento individual: `void notificar(Producto producto, int cantidadReorden)`.
2. Notificacion agregada por lote (batch): `void notificar(List<Producto> productosBajoStock)` o `void notificar(LoteNotificacion lote)`.

## Decision
Se adopto una firma de notificacion por evento individual: `void notificar(Producto producto, int cantidadReorden)`, disparada de forma inmediata cuando la ejecucion del metodo `actualizarInventario()` detecta que el saldo de stock resultante cruza el umbral minimo permitido.

## Justificacion Tecnica
1. **Inmediatez Operativa**: Los movimientos de inventario ocurren en tiempo real transaccional (recepcion o despacho de unidades). Detectar y alertar sobre una condicion critica de inventario en el momento exacto en que sucede permite reaccionar de forma inmediata (por ejemplo, pausar pedidos de salida o emitir solicitudes urgentes de compra) sin esperar a un ciclo diferido.
2. **Coherencia con la Semantica del Metodo**: `actualizarInventario()` procesa una sola transaccion atomica sobre un producto individual a la vez. Exigir la construccion y emision de una lista de tamano 1 agregaria sobrecarga innecesaria de memoria y estructuras auxiliares artificiales.
3. **Simplicidad de Implementacion y Testeo**: La firma individual es trivial de simular, capturar o validar en pruebas unitarias mediante aserciones directas sobre los parametros recibidos.

## Alternativas Descartadas

### Alternativa 1: Notificacion por Lote (`notificar(List<Producto>)`)
- **Razon de descarte en el contexto actual**: Forzar una interfaz por lotes en una operacion puntual introduce complejidad espuria: obligaria a envolver el objeto en colecciones artificiales (`Collections.singletonList(...)`) en cada invocacion unitaria, o requeriria que `InventarioService` retuviera un buffer interno con un temporizador para agrupar alertas, transformando un servicio sincrono simple en un subsistema asincrono con hilos y colas concurrentes.
- **Escenario donde seria preferida**: Una notificacion por lote seria ampliamente superior y recomendada en procesos nocturnos o *batch* (por ejemplo, un cierre diario de inventario que ejecuta un barrido masivo de miles de referencias y genera un consolidado consolidado en PDF o correo electronico unico para el gerente de abastecimiento, reduciendo la sobrecarga de red y el bombardeo de notificaciones atomicas).

## Consecuencias

### Positivas
- **Acoplamiento Minimo**: El consumidor recibe unicamente el `Producto` y el entero de `cantidadReorden`, sin requerir el escaneo de colecciones.
- **Disparo Oportuno**: Permite registrar la alerta en tiempo real en la lista de historicos de `ReporteBajoStockService`.
- **Rendimiento**: Cero sobrecarga de colecciones en memoria por movimiento procesado.

### Trade-offs y Mitigaciones
- Si el sistema experimentara una rafaga masiva de miles de despachos concurrentes que generen alertas consecutivas, la notificacion por evento podria sobrecargar canales externos de mensajeria. En dicho entorno, el adaptador en `reportes` puede implementar un patron de acumulacion interna o *throttling* sin alterar la firma contractual del dominio en `inventario`.
