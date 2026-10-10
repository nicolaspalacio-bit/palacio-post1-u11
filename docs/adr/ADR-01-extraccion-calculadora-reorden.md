# ADR-01: Extraccion de CalculadoraReorden como Colaborador Independiente

## Estado
Aceptado

## Contexto
En la clase `InventarioService`, el metodo original `actualizarInventario()` acumulaba mas de 40 lineas de codigo y mezclaba al menos cinco responsabilidades disimiles: validacion sintactica de parametros, busqueda en coleccion en memoria, actualizacion aritmetica de stock, evaluacion de umbrales y calculo de cantidad de reabastecimiento, persistencia de auditoria y notificacion por consola.

Al aplicar la refactorizacion guiada por pruebas para eliminar el smell *Long Method* (Bloater), se requirio descomponer dicho bloque en componentes de responsabilidad unica. Surgio la decision de diseno sobre si la logica de calculo de la cantidad de reorden (`(stockMinimo * 3) - nuevoStock`) debia extraerse como un metodo privado dentro de la misma clase o modularizarse en una clase colaboradora independiente (`CalculadoraReorden`).

## Decision
Se decidio extraer una clase colaboradora independiente denominada `CalculadoraReorden` (`com.universidad.refactoringu11.inventario.CalculadoraReorden`), inyectada en `InventarioService` mediante constructor y provista con una instancia por defecto para preservar la compatibilidad directa con las pruebas unitarias POJO.

## Alternativas Descartadas

### Alternativa 1: Conservar el calculo como metodo privado en `InventarioService`
- **Razon de descarte**: Aunque esta alternativa requeria menos clases en el paquete y evitaba la sobrecarga de instanciacion, incumplia el Principio de Responsabilidad Unica (SRP). La politica de reabastecimiento (calculo de stock de seguridad, multiplicadores comerciales, tamano de lote economico) responde a un eje de cambio corporativo y de compras, completamente desacoplado de la orquestacion del movimiento de stock (entradas y salidas de almacen).
- **Que se sacrificaria**: Se sacrificaria la cohesión y la facilidad de prueba unitaria aislada de las formulas de reorden, obligando a modificar `InventarioService` cada vez que el departamento comercial ajuste las politicas de reabastecimiento.

## Consecuencias

### Positivas
- **Alta Cohesion y SRP**: `InventarioService` se enfoca estrictamente en la orquestacion de movimientos de inventario, delegando el calculo matematico a `CalculadoraReorden`.
- **Testabilidad Aislada**: Permite anadir pruebas unitarias dedicadas (`CalculadoraReordenTest`) para cubrir valores limite, stock cero y umbrales sin requerir la instanciacion del servicio orquestador completo.
- **Extensibilidad**: Facilita la futura parametrizacion de politicas de stock sin alterar el codigo central del servicio de inventario.

### Trade-offs y Mitigaciones
- **Sobrecarga de archivos**: Introduce una clase adicional en el paquete `inventario`. Se mitiga manteniendo la clase compacta y anotada como `@Component` para integracion transparente con el contenedor de dependencias de Spring Boot.
