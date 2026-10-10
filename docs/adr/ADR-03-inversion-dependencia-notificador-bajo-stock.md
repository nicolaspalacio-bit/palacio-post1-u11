# ADR-03: Inversion de Dependencias y Ubicacion de NotificadorBajoStock

## Estado
Aceptado

## Contexto
Al incorporar el modulo de reportes mediante la clase `ReporteBajoStockService` en el paquete `com.universidad.refactoringu11.reportes`, se genero una dependencia bidireccional:
1. `ReporteBajoStockService` importaba `InventarioService` para consultar datos de stock.
2. `InventarioService` importaba `ReporteBajoStockService` para disparar la generacion de reportes al detectar niveles criticos.

Esta situacion origino una dependencia ciclica (`inventario <-> reportes`), detectada y reportada en rojo por la prueba de arquitectura de ArchUnit (`ArquitecturaPaquetesTest`). Para romper este ciclo, resultaba imperativo aplicar el Principio de Inversion de Dependencias (DIP) y la arquitectura de Puertos y Adaptadores (Hexagonal). Se debia determinar en que paquete debia residir la nueva interfaz de abstraccion contractual.

## Decision
Se decidio extraer la interfaz `NotificadorBajoStock` y ubicarla estrictamente dentro del paquete `com.universidad.refactoringu11.inventario`. `InventarioService` paso a depender exclusivamente de esta interfaz interna, mientras que `ReporteBajoStockService` (en el paquete `reportes`) implementa dicho contrato actuando como un adaptador de salida.

## Justificacion Arquitectonica
Conforme a los fundamentos de Clean Architecture (Martin, 2017) y Arquitectura Hexagonal (Cockburn, 2005):
1. **Regla de Dependencias**: Los paquetes de soporte o infraestructura dependen de los paquetes de dominio; el dominio **nunca** depende de infraestructura.
2. **Nivel de Abstraccion**: En este sistema, el paquete `inventario` constituye el nucleo del dominio de negocio (gestiona la verdad transaccional de productos, stock y reorden), situandose en el mas alto nivel de abstraccion y estabilidad. El paquete `reportes` representa un mecanismo secundario de notificacion y formateo de texto plano (menor nivel de abstraccion, mayor volatilidad).
3. **Control del Contrato**: El dominio debe dictar que eventos son relevantes y bajo que contrato se notifican; ubicar la interfaz en el dominio garantiza su completa autonomia y fan-out cero hacia detalles externos.

## Alternativas Descartadas

### Alternativa 1: Ubicar la interfaz `NotificadorBajoStock` en el paquete `reportes`
- **Razon de descarte**: Si la interfaz residiera en `reportes`, `InventarioService` seguiria teniendo que importar el paquete `reportes` (`import ...reportes.NotificadorBajoStock`), y a su vez `ReporteBajoStockService` importaria `InventarioService`. El ciclo entre los paquetes se mantendria intacto.
- **Que se sacrificaria**: No se resolveria el problema arquitectonico diagnosticado por ArchUnit y se mantendria la fuga de abstraccion.

### Alternativa 2: Crear un paquete compartido `com.universidad.refactoringu11.common`
- **Razon de descarte**: Crear paquetes genericos tipo *common* o *shared* para interfaces puntuales diluye los limites de los modulos y oculta las responsabilidades del dominio, promoviendo acoplamiento oculto.

## Consecuencias

### Positivas
- **Ruptura Definitiva del Ciclo**: La dependencia fluye ahora en una sola direccion permitida: `reportes -> inventario`. El paquete `inventario` no posee ningun `import` hacia `reportes`.
- **Validacion Automatica en Verde**: La regla `beFreeOfCycles()` de ArchUnit pasa satisfactoriamente.
- **Independencia del Dominio**: El modulo `inventario` puede compilarse, probarse y desplegarse de manera totalmente aislada sin requerir la presencia del modulo de reportes.

### Trade-offs y Mitigaciones
- El paquete `reportes` acopla su implementacion a los tipos del dominio (`Producto`), lo cual es el comportamiento deseado en una arquitectura hexagonal limpia donde los adaptadores conocen a los puertos y modelos del dominio.
