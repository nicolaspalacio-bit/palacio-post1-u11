# Guia de Pruebas y Verificacion — Refactoring U11

Esta guia documenta los comandos de ejecucion, los casos de prueba automatizados y los resultados esperados para validar tanto la Parte 1 (refactorizacion de code smells de metodo/clase) como la Parte 2 (resolucion de acoplamiento de paquetes).

---

## 1. Comandos de Ejecucion

### Ejecucion Completa de la Suite de Pruebas
```powershell
# Windows PowerShell
mvn clean test
```
```bash
# Bash (Linux / macOS / WSL)
mvn clean test
```

### Ejecucion Específica de la Prueba de Arquitectura (ArchUnit)
```powershell
mvn test -Dtest=ArquitecturaPaquetesTest
```

### Generacion de Reportes de Cobertura de Codigo (JaCoCo)
```powershell
mvn test jacoco:report
```
El reporte HTML resultante se ubica en: `target/site/jacoco/index.html`.

---

## 2. Inventario de Pruebas Automatizadas

### A. Pruebas de Caracterizacion de Inventario (`InventarioServiceTest`)
Ubicacion: `src/test/java/com/universidad/refactoringu11/inventario/InventarioServiceTest.java`

| Metodo de Prueba | Entrada | Salida Esperada | Comportamiento Validado |
| :--- | :--- | :--- | :--- |
| `entrada_incrementaStockYRetornaOk` | ID: 1L, Cantidad: 10, Tipo: "ENTRADA" | `"OK_30"`, Stock: 30 | Incremento de stock y confirmacion de operacion. |
| `salida_conStockSuficiente_descuentaYRetornaOk` | ID: 1L, Cantidad: 8, Tipo: "SALIDA" | `"OK_12"`, Stock: 12 | Descuento regular de existencias sin cruzar stock minimo. |
| `salida_conStockInsuficiente_retornaErrorYNoModificaStock` | ID: 1L, Cantidad: 50, Tipo: "SALIDA" | `"ERROR_STOCK_INSUFICIENTE"`, Stock: 20 | Rechazo de operacion cuando la demanda supera el disponible. |
| `salida_dejaStockBajoElMinimo_activaReordenConCantidadCalculada` | ID: 1L, Cantidad: 17, Tipo: "SALIDA" | `"OK_BAJO_STOCK_REORDEN_12"`, Stock: 3 | Deteccion de umbral critico y calculo de lote de compra: `(5 * 3) - 3 = 12`. |
| `tipoMovimientoInvalido_retornaError` | ID: 1L, Cantidad: 5, Tipo: "TRANSFERENCIA" | `"ERROR_DATOS_INVALIDOS"` | Validacion de tipos de movimiento permitidos ("ENTRADA"/"SALIDA"). |
| `productoInexistente_retornaError` | ID: 99L, Cantidad: 5, Tipo: "ENTRADA" | `"ERROR_PRODUCTO_NO_EXISTE"` | Manejo de identificador no registrado en el inventario. |

---

### B. Pruebas de Clasificacion de Recargos (`ClasificadorProductoTest`)
Ubicacion: `src/test/java/com/universidad/refactoringu11/inventario/ClasificadorProductoTest.java`

| Metodo de Prueba | Tipo de Producto | Precio Base | Salida Esperada | Regla de Negocio |
| :--- | :--- | :--- | :--- | :--- |
| `libro_noTieneRecargo` | `LIBRO` | $60.0 | `0.0` | Libros exentos de recargo de envio. |
| `electronico_recargaOchoPorCiento` | `ELECTRONICO` | $100.0 | `8.0` | Recargo del 8% sobre el precio de lista. |
| `ropa_recargaTresPorCiento` | `ROPA` | $40.0 | `1.2` | Recargo del 3% sobre el precio de lista. |
| `alimento_recargoFijo` | `ALIMENTO` | $15.0 | `2.50` | Tarifa plana de $2.50 para perecederos. |

---

### C. Pruebas de Generacion de Etiquetas (`EtiquetadoProductoTest`)
Ubicacion: `src/test/java/com/universidad/refactoringu11/inventario/EtiquetadoProductoTest.java`

| Metodo de Prueba | Tipo de Producto | Salida Esperada | Regla de Formato |
| :--- | :--- | :--- | :--- |
| `libro_incluyeExentoDeIva` | `LIBRO` ("Refactoring") | `"[LIBRO] Refactoring - Exento de IVA"` | Prefijo y condicion fiscal. |
| `electronico_incluyeGarantia` | `ELECTRONICO` ("Teclado") | `"[ELECTRO] Teclado - Garantia 1 ano"` | Prefijo y garantia estandar. |
| `ropa_incluyeVerTalla` | `ROPA` ("Camisa") | `"[ROPA] Camisa - Ver talla"` | Prefijo y advertencia de tallaje. |
| `alimento_incluyePerecedero` | `ALIMENTO` ("Cafe") | `"[ALIMENTO] Cafe - Perecedero"` | Prefijo y clasificacion perecedera. |

---

### D. Pruebas del Colaborador de Reabastecimiento (`CalculadoraReordenTest`)
Ubicacion: `src/test/java/com/universidad/refactoringu11/inventario/CalculadoraReordenTest.java`

| Metodo de Prueba | Escenario | Salida Esperada | Proposito |
| :--- | :--- | :--- | :--- |
| `cuandoStockEsMayorOIgualAlMinimo_noNecesitaReorden` | Stock = 5, Minimo = 5 | `necesitaReorden = false`, `cantidad = 0` | No disparar compras si se mantiene el margen de seguridad. |
| `cuandoStockEsMenorAlMinimo_calculaCantidadReorden` | Stock = 3, Minimo = 5 | `necesitaReorden = true`, `cantidad = 12` | Calculo correcto: `(5 * 3) - 3 = 12`. |

---

### E. Prueba de Arquitectura de Paquetes (`ArquitecturaPaquetesTest`)
Ubicacion: `src/test/java/com/universidad/refactoringu11/ArquitecturaPaquetesTest.java`

| Regla Verificada | Criterio de Exito | Resultado Esperado |
| :--- | :--- | :--- |
| `slices().matching("com.universidad.refactoringu11.(*)..").should().beFreeOfCycles()` | Ausencia total de dependencias circulares entre cualquier subpaquete del proyecto. | **VERDE (0 violaciones)** tras la refactorizacion con DIP (`NotificadorBajoStock`). |

---

### F. Prueba del Adaptador de Reportes (`ReporteBajoStockServiceTest`)
Ubicacion: `src/test/java/com/universidad/refactoringu11/reportes/ReporteBajoStockServiceTest.java`

| Metodo de Prueba | Escenario | Salida Esperada | Proposito |
| :--- | :--- | :--- | :--- |
| `notificar_registraAlertaYGeneraReporte` | Invocacion via `notificar(Producto, 13)` | Alertas acumuladas = 1, reporte contiene "1 alertas" | Verifica recepcion del evento de dominio y generacion del texto de reporte. |

---

## 3. Resumen Consolidado de Ejecucion

```text
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Total time: ~6.5 s
```
