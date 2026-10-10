# ADR-02: Mecanismo de Polimorfismo para Reemplazar Sentencias Switch Duplicadas

## Estado
Aceptado

## Contexto
Las clases `ClasificadorProducto` y `EtiquetadoProducto` presentaban sentencias `switch` identicas evaluando las cuatro constantes del enum `TipoProducto` (`LIBRO`, `ELECTRONICO`, `ROPA`, `ALIMENTO`). Esta estructura de control duplicada configuraba los code smells *Switch Statements* y *Shotgun Surgery*, violando directamente el Principio Abierto/Cerrado (OCP): cualquier incorporacion de un nuevo tipo de producto exigia localizar y modificar multiples archivos en el codigo fuente.

Para sustituir esta logica condicional por polimorfismo (*Replace Conditional with Polymorphism*, Fowler 2018), se plantearon dos mecanismos viables en el ecosistema Java:
1. Dotar al enum `TipoProducto` de metodos abstractos con implementaciones por constante.
2. Crear una jerarquia de clases de estrategia (patron Strategy) gobernada por interfaces independientes (`RecargoEnvioStrategy` y `EtiquetadoStrategy`).

## Decision
Se decidio implementar el polimorfismo directamente en las constantes del enum `TipoProducto`, definiendo los metodos abstractos `calcularRecargoEnvio(double precio)` y `generarEtiqueta(String nombre)`. En consecuencia, `ClasificadorProducto` y `EtiquetadoProducto` delegan unilinealmente el comportamiento al tipo de producto, preservando sus firmas originales.

## Alternativas Descartadas

### Alternativa 1: Jerarquia de Estrategias con Clases Concretas (Patron Strategy GoF)
- **Razon de descarte**: Crear interfaces separadas para el recargo y el etiquetado, junto con una clase por cada combinacion de tipo de producto (4 estrategias de recargo + 4 estrategias de etiquetado = 8 clases adicionales), representaba una sobreingenieria desproporcionada para el dominio evaluado. El conjunto de tipos en este servicio de inventario es cerrado, cohesionado y bien delimitado.
- **Que se sacrificaria**: Se aumentaria significativamente el costo cognitivo, la dispersion del codigo y el esfuerzo de navegacion en el proyecto, violando el principio YAGNI (*You Aren't Gonna Need It*).

## Consecuencias

### Positivas
- **Eliminacion Total de Condicionales**: Se erradicaron al 100% las estructuras `switch` y cadenas `if-else` en `ClasificadorProducto` y `EtiquetadoProducto`.
- **Cumplimiento de OCP**: La incorporacion de un nuevo tipo (por ejemplo, `JUGUETE`) se realiza exclusivamente agregando una nueva constante al enum `TipoProducto` con sus dos metodos implementados, sin requerir modificacion alguna en las clases clasificadoras o etiquetadoras.
- **Simplicidad y Cohesion**: Aprovecha la caracteristica de enums avanzados en Java (*Effective Java Item 34*), manteniendo las reglas asociadas a cada tipo reunidas en una definicion concisa.
- **Preservacion de Contratos**: No se altero la firma publica de `calcularRecargoEnvio(Producto)` ni de `generarEtiqueta(Producto)`, garantizando que las pruebas de caracterizacion pasaran sin un solo cambio.

### Trade-offs y Mitigaciones
- **Extensibilidad con Inyeccion de Dependencias**: Si en el futuro un tipo de producto necesitara interactuar con dependencias externas gestionadas por Spring (por ejemplo, un servicio de cotizacion arancelaria en linea para `ELECTRONICO`), los enums no pueden recibir dependencias inyectadas por constructor de forma estandar. En dicho escenario evolutivo, la arquitectura debera migrar hacia la jerarquia de clases Strategy con factoria o mapa de inyeccion.
