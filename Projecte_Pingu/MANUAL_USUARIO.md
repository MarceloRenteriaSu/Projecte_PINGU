# 🐧 Joc de'n Pingu — Manual de Usuario

> **Versión del documento:** 1.0  
> **Fecha:** Mayo 2026  
> **Autores:** Carlos Oros Bendezú · Marcelo Renteria Su · Denis Tineo Dias

---

## Índice

1. [Introducción](#1-introducción)
2. [Requisitos del sistema](#2-requisitos-del-sistema)
3. [Instalación e inicio del juego](#3-instalación-e-inicio-del-juego)
4. [Pantalla de Login y Registro](#4-pantalla-de-login-y-registro)
5. [Menú Principal](#5-menú-principal)
6. [Configuración de la Partida](#6-configuración-de-la-partida)
7. [Pantalla de Juego — Interfaz](#7-pantalla-de-juego--interfaz)
8. [Normas del juego](#8-normas-del-juego)
9. [Tipos de casillas](#9-tipos-de-casillas)
10. [Sistema de ítems e inventario](#10-sistema-de-ítems-e-inventario)
11. [La Foca (enemigo CPU)](#11-la-foca-enemigo-cpu)
12. [Batallas de nieve](#12-batallas-de-nieve)
13. [Modo Auto-Play](#13-modo-auto-play)
14. [Guardar y cargar partidas](#14-guardar-y-cargar-partidas)
15. [Ranking y estadísticas](#15-ranking-y-estadísticas)
16. [Ajustes (audio)](#16-ajustes-audio)
17. [Pantalla de fin de partida](#17-pantalla-de-fin-de-partida)
18. [Posibles errores y cómo solucionarlos](#18-posibles-errores-y-cómo-solucionarlos)
19. [Créditos](#19-créditos)

---

## 1. Introducción

**Joc de'n Pingu** es un juego de mesa digital multijugador (1–4 jugadores) desarrollado con Java y JavaFX. Cada jugador controla un pingüino que debe recorrer un tablero de casillas desde el inicio (**S**) hasta el final (**F**), enfrentándose a eventos aleatorios, trampas y a la temible **Foca** controlada por la CPU.

El primer pingüino en llegar a la última casilla gana la partida.

---

## 2. Requisitos del sistema

| Componente | Requisito mínimo |
|---|---|
| **Sistema operativo** | Windows 10/11, macOS o Linux |
| **Java** | JDK 21 o superior |
| **JavaFX** | SDK de JavaFX (incluido en las librerías del proyecto) |
| **Driver Oracle** | `ojdbc` (incluido en la librería "BD" del proyecto) |
| **Conexión a Internet** | Necesaria para conectarse a la base de datos Oracle remota |
| **Resolución de pantalla** | 1280 × 720 px o superior (recomendado) |
| **Audio** | Altavoces o auriculares (opcional, para la música de fondo) |

---

## 3. Instalación e inicio del juego

### 3.1. Desde Eclipse (entorno de desarrollo)

1. **Importar el proyecto:**
   - Abre Eclipse y selecciona `File → Import → General → Existing Projects into Workspace`.
   - Navega hasta la carpeta `Projecte_Pingu` y haz clic en **Finish**.

2. **Configurar las librerías:**
   - Asegúrate de que las **User Libraries** `JavaFX` y `BD` estén definidas en Eclipse:
     - `Window → Preferences → Java → Build Path → User Libraries`.
     - `JavaFX` debe apuntar a los JARs del SDK de JavaFX.
     - `BD` debe contener el driver `ojdbc` para Oracle.

3. **Configurar los VM Arguments de JavaFX:**
   - En `Run → Run Configurations → Arguments → VM arguments`, añade:
     ```
     --module-path "RUTA_A_TU_JAVAFX_LIB" --add-modules javafx.controls,javafx.fxml,javafx.media
     ```

4. **Ejecutar el juego:**
   - Haz clic derecho sobre la clase principal del proyecto → `Run As → Java Application`.
   - La ventana de login aparecerá automáticamente.

### 3.2. Desde línea de comandos

```bash
java --module-path "ruta/javafx-sdk/lib" \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     -cp "bin;lib/ojdbc11.jar" \
     clases.MAIN_PRUEBAS
```

> **IMPORTANTE:** El juego necesita conexión a Internet para acceder a la base de datos Oracle remota (`oracle.ilerna.com`). Sin conexión, no podrás iniciar sesión, registrarte, guardar partidas ni consultar el ranking.

---

## 4. Pantalla de Login y Registro

Al iniciar el juego se muestra la pantalla de bienvenida con el fondo del juego y el logo de Pingu. En la parte inferior hay dos botones principales.

### 4.1. Iniciar sesión

1. Pulsa el botón **"Iniciar sesión"** en el dock inferior.
2. Se despliega un formulario con los campos:
   - **Usuario:** mínimo 3 caracteres, sin espacios.
   - **Contraseña:** más de 3 caracteres.
3. Pulsa **"Entrar"**.
4. Si las credenciales son correctas, serás redirigido al **Menú Principal**.

### 4.2. Registrarse

1. Pulsa el botón **"Registrarse"** en el dock inferior.
2. Rellena los campos:
   - **Usuario:** mínimo 3 caracteres, sin espacios.
   - **Contraseña:** más de 3 caracteres.
   - **Confirmar contraseña:** debe coincidir con la contraseña.
3. Pulsa **"Registrar"**.
4. Si el registro es exitoso, el formulario de login se mostrará automáticamente con tu usuario rellenado y el mensaje *"✓ Cuenta creada. ¡Ahora inicia sesión!"*.

> **NOTA:** Si el nombre de usuario ya existe, se mostrará el mensaje *"⚠ El nombre de usuario ya existe. Elige otro."*

---

## 5. Menú Principal

Tras iniciar sesión, llegarás al menú principal con las siguientes opciones:

| Botón | Función |
|---|---|
| **Nueva Partida** | Abre la pantalla de configuración para crear una partida nueva |
| **Cargar Partida** | Muestra una lista de partidas guardadas previamente para reanudarlas |
| **Ranking** | Muestra el ranking mundial y estadísticas de todos los jugadores |
| **Ajustes** | Permite modificar el volumen de la música y silenciar el sonido |
| **Créditos** | Muestra los nombres de los creadores del juego |
| **Salir** | Cierra el juego completamente |

---

## 6. Configuración de la Partida

Antes de empezar una nueva partida, debes configurar los parámetros:

### 6.1. Número de casillas

- Introduce el número de casillas del tablero en el campo de texto.
- **Mínimo:** 50 casillas · **Máximo:** 150 casillas.
- Valor por defecto: **50**.

### 6.2. Número de jugadores

- Selecciona de **1 a 4 jugadores** mediante el desplegable.
- El **Jugador 1** siempre es el usuario que inició sesión (campo bloqueado).
- Los jugadores 2–4 se seleccionan de la lista de usuarios registrados en la base de datos.

### 6.3. Colores de los pingüinos

- Cada jugador tiene un botón de color junto a su nombre.
- Al pulsarlo, se despliega una **paleta de 16 colores** para elegir.
- **No se permiten colores repetidos** entre jugadores.
- Una vista previa del pingüino se actualiza al cambiar de color.

### 6.4. Activar la Foca

- La casilla **"Foca"** viene activada por defecto.
- Si la desactivas, la Foca (enemigo CPU) no aparecerá en la partida.

### 6.5. Comenzar

- Pulsa **"Comenzar"** para iniciar la partida.
- Si hay errores de validación (casillas fuera de rango, nombres duplicados, colores repetidos), se mostrará un mensaje en rojo.

---

## 7. Pantalla de Juego — Interfaz

La pantalla de juego se divide en las siguientes zonas:

### 7.1. Tablero (zona central)

- Un tablero en forma de **serpiente** (izquierda→derecha, luego derecha→izquierda en la fila siguiente).
- Cada casilla muestra un **icono** que representa su tipo y un número de posición.
- La casilla de inicio está marcada con **"S"** y la final con **"F"**.
- Las fichas de los pingüinos (y la foca, si está activa) se mueven con animación celda por celda.

### 7.2. Panel de dados (zona derecha superior)

- **Botón "Tirar Dado":** tira el dado normal (1–6) para avanzar.
- **Texto de resultado:** muestra el número obtenido y el jugador actual.

### 7.3. Inventario (zona derecha)

Cuatro ranuras muestran los ítems del jugador actual:

| Ranura | Ítem | Borde |
|---|---|---|
| Rojo | **Dado Rápido** (5–10) | Rojo |
| Azul | **Dado Lento** (1–3) | Azul |
| Verde | **Pez** (protección) | Verde |
| Rosa | **Bola de Nieve** (combate) | Rosa |

- Las ranuras de **Dado Rápido** y **Dado Lento** son **seleccionables**: haz clic para seleccionarlas y luego pulsa **"Usar"** para tirar con ese dado en lugar del normal.
- Las ranuras de **Pez** y **Bola de Nieve** son **solo informativas** (se usan automáticamente cuando corresponde).
- Las ranuras se atenúan cuando la cantidad es 0.

### 7.4. Panel de eventos (zona derecha inferior)

- Una lista que muestra en tiempo real todo lo que sucede en la partida: tiradas, eventos, batallas, acciones de la foca, etc.
- Se desplaza automáticamente hacia el evento más reciente.

### 7.5. Barra de menú (parte superior)

| Opción | Función |
|---|---|
| **Partida → Nueva Partida** | Vuelve al menú principal para empezar una nueva partida |
| **Partida → Guardar** | Guarda el estado actual de la partida en la base de datos |
| **Partida → Cargar** | Carga una partida guardada previamente |
| **Partida → Salir** | Cierra el juego |

### 7.6. Botón Auto-Play

- Situado junto al botón de tirar dado.
- Alterna entre **"Auto: OFF"** y **"Auto: ON"**.
- Cuando está activado, el juego tira el dado automáticamente cada 1,5 segundos.

---

## 8. Normas del juego

### 8.1. Objetivo

Ser el **primer pingüino** en llegar a la **casilla final** (marcada con "F").

### 8.2. Turnos

1. Los jugadores actúan por turnos, en el orden en que fueron configurados.
2. En cada turno, el jugador **tira el dado** (normal, rápido o lento) y avanza el número de casillas indicado.
3. Al caer en una casilla especial, su efecto se aplica automáticamente.
4. Si dos pingüinos coinciden en la misma casilla, se activa una **Batalla de Nieve**.
5. Después de que **todos los pingüinos** hayan jugado su turno en la ronda, la **Foca** (si está activada) realiza su turno.

### 8.3. Condiciones de victoria

- Un pingüino gana al llegar a la **última casilla** (posición F).
- La **Foca** también puede ganar si llega al final antes que cualquier pingüino.

### 8.4. Perder turno

- Ciertos eventos y casillas pueden hacer que un pingüino **pierda su siguiente turno**. En ese caso, es saltado automáticamente.

---

## 9. Tipos de casillas

El tablero se genera aleatoriamente con los siguientes tipos de casilla:

### Casilla Normal (~15%)
- **Sin efecto.** El pingüino simplemente se queda en esta casilla.
- Siempre es la **primera** y la **última** casilla del tablero.

### Casilla Oso (~15%)
- Un oso polar bloquea el paso.
- **Si tienes un Pez:** se consume automáticamente para sobornar al oso. Te quedas en la casilla.
- **Si NO tienes un Pez:** el pingüino es enviado a la **casilla 0** (inicio).

### Casilla Agujero (~15%)
- El pingüino cae por el agujero.
- Es transportado al **agujero anterior** más cercano.
- Si no existe un agujero anterior, vuelve a la **casilla 0**.

### Casilla Trineo (~20%)
- ¡Impulso! El pingüino avanza rápidamente.
- Es transportado al **siguiente trineo** en el tablero.
- Si no hay más trineos, avanza el **10% del total de casillas**.

### Casilla Evento (~20%)
- Un evento aleatorio se activa. Los posibles resultados son:

| Evento | Probabilidad | Efecto |
|---|---|---|
| Dado | 15% | Recibes un Dado Lento (60%) o un Dado Rápido (40%) |
| Moto de Nieve | 15% | Avanzas hasta el siguiente trineo |
| Bola de Nieve | 30% | Recibes 1–3 Bolas de Nieve |
| Pez | 20% | Recibes 1 Pez |
| Perder turno | 10% | Pierdes tu siguiente turno |
| Perder objeto | 10% | Pierdes un objeto aleatorio del inventario |

### Casilla Suelo Quebradizo (~15%)
- El suelo se agrieta bajo el peso del pingüino.
- **Si tienes más de 5 ítems en total:** vuelves a la **casilla 0** (demasiado peso).
- **Si tienes entre 1 y 5 ítems:** pierdes el **siguiente turno**.
- **Si tu inventario está vacío:** no pasa nada.

---

## 10. Sistema de ítems e inventario

Cada pingüino comienza con un **Dado Normal** en su inventario. A lo largo de la partida puede obtener más ítems:

### 10.1. Tipos de ítems

| Ítem | Descripción | Máximo |
|---|---|---|
| **Dado Normal** | Tira entre 1 y 6 | Siempre disponible (no se puede perder) |
| **Dado Rápido** | Tira entre 5 y 10 | Máximo 3 dados en total |
| **Dado Lento** | Tira entre 1 y 3 | Máximo 3 dados en total |
| **Pez** | Protege contra el Oso y puede sobornar a la Foca | Máximo 2 |
| **Bola de Nieve** | Se usa en las Batallas de Nieve | Máximo 6 |

### 10.2. Cómo usar dados especiales

1. Haz clic en la ranura del dado que quieres usar (Rápido o Lento).
2. La ranura se ilumina con un resplandor blanco y aparece el botón **"Usar"**.
3. Pulsa **"Usar"** para tirar con ese dado. Se consumirá una unidad.
4. Para deseleccionar, haz clic otra vez en la misma ranura.

### 10.3. Uso automático de ítems

- Los **Peces** se usan automáticamente al caer en una casilla de Oso o al ser atrapado por la Foca.
- Las **Bolas de Nieve** se usan automáticamente en las Batallas de Nieve.

---

## 11. La Foca (enemigo CPU)

La Foca es un enemigo controlado por la inteligencia artificial que persigue a los pingüinos.

### 11.1. Comportamiento

- La Foca juega **después de que todos los pingüinos** hayan completado su turno en la ronda.
- Tira un Dado Normal (1–6) y se mueve hacia adelante.
- **IA de persecución:** la Foca intenta alcanzar al pingüino más avanzado. Si la tirada es suficiente para llegar a él, se posiciona exactamente en su casilla.

### 11.2. Efectos sobre los pingüinos

| Situación | Efecto |
|---|---|
| La Foca **pasa por encima** de un pingüino | El pingüino pierde la **mitad de sus ítems** (aleatorio) |
| La Foca **se detiene** en la casilla de un pingüino **sin pez** | El pingüino es enviado al **agujero anterior** o a la casilla 0 |
| La Foca **se detiene** en la casilla de un pingüino **con pez** | El pez se consume y la Foca es **sobornada** (bloqueada 2 turnos) |

### 11.3. Soborno

- Cuando un pingüino soborna a la Foca con un pez, esta queda **bloqueada durante 2 turnos** y no se mueve.
- Después de los 2 turnos, la Foca vuelve a moverse con normalidad.

---

## 12. Batallas de Nieve

Cuando un pingüino cae en una casilla **ocupada por otro pingüino**, se activa una Batalla de Nieve.

### 12.1. Opciones del defensor

El jugador que **ya estaba** en la casilla (defensor) elige:

1. **Tirar el dado y escapar:** El defensor tira un dado normal y avanza ese número de casillas, alejándose del atacante.

2. **Entrar en batalla:** Se compara el número de bolas de nieve de ambos jugadores:
   - El que tiene **más bolas gana**. El perdedor **retrocede** tantas casillas como la diferencia.
   - Si hay **empate**, nadie retrocede.
   - En ambos casos, **los dos jugadores pierden todas sus bolas de nieve**.

> **NOTA:** Si ninguno de los dos pingüinos tiene bolas de nieve, el botón de batalla estará deshabilitado y el defensor solo podrá tirar el dado para escapar.

---

## 13. Modo Auto-Play

El modo Auto-Play permite que el juego se juegue solo, tirando el dado automáticamente:

1. Pulsa el botón **"Auto: OFF"** para activarlo → cambiará a **"Auto: ON"**.
2. El dado se tirará automáticamente cada **1,5 segundos**.
3. Las batallas de nieve se resuelven automáticamente (batalla si hay bolas, escapar si no).
4. Para detenerlo, pulsa el botón otra vez → volverá a **"Auto: OFF"**.

> **CONSEJO:** El modo Auto-Play es útil para probar el juego rápidamente o para simular partidas completas.

---

## 14. Guardar y cargar partidas

### 14.1. Guardar una partida

1. Durante la partida, ve a **Partida → Guardar** en la barra de menú.
2. Se abrirá un diálogo pidiendo un **nombre para la partida**.
3. Introduce un nombre descriptivo y pulsa **Aceptar**.
4. Un mensaje en el panel de eventos confirmará: *"¡Partida guardada correctamente!"*.
5. Se guarda en la base de datos: tablero, posiciones, inventarios, estado de la foca y los eventos.

> **AVISO:** No se pueden guardar partidas que ya hayan finalizado.

### 14.2. Cargar una partida

1. Desde el **Menú Principal**, pulsa **"Cargar Partida"**, o desde la partida: **Partida → Cargar**.
2. Se muestra una tabla con tus partidas guardadas, incluyendo:
   - **Nombre** de la partida
   - **Jugadores** participantes
   - **Turnos** jugados
   - **Fecha** del guardado
3. Selecciona una partida y pulsa **"Cargar"**.
4. La partida se restaura exactamente en el estado en que se guardó.

### 14.3. Borrar una partida guardada

1. En la pantalla de cargar partida, selecciona una partida.
2. Pulsa **"Borrar"**.
3. Se pedirá confirmación. Pulsa **"Sí"** para eliminarla permanentemente.

---

## 15. Ranking y estadísticas

Accede al ranking pulsando **"Ranking"** en el menú principal. Se abre una ventana con dos pestañas:

### 15.1. Pestaña "Clasificación"

Una tabla con todos los jugadores ordenados, mostrando:

| Columna | Descripción |
|---|---|
| **#** | Posición en el ranking |
| **Jugador** | Nombre de usuario |
| **Partidas** | Total de partidas jugadas |
| **Victorias** | Total de partidas ganadas |
| **% Victoria** | Porcentaje de victorias sobre partidas jugadas |

### 15.2. Pestaña "Estadísticas"

- **Récord mundial:** Jugador(es) con más victorias y su cifra.
- **Media de victorias:** Promedio de victorias por jugador.
- **Por encima de la media:** Jugadores que superan la media de victorias.
- **Tu posición:** Tus victorias y el porcentaje de jugadores que superas.

---

## 16. Ajustes (audio)

Accede a los ajustes pulsando **"Ajustes"** en el menú principal:

- **Slider de volumen:** Ajusta el volumen de la música de fondo (0%–100%).
- **Casilla "Silenciar":** Silencia completamente la música. Al desmarcarla, se restaura el volumen anterior.

El juego reproduce música de fondo (*Frost Menu Drift*) en bucle durante toda la sesión.

---

## 17. Pantalla de fin de partida

Cuando un jugador (o la foca) llega a la casilla final:

1. Se muestra una ventana con:
   - Icono de trofeo
   - **Nombre del ganador**
   - **Número de turnos jugados**
2. Opciones:
   - **"Volver al menú":** Regresa al menú principal y muestra automáticamente el ranking actualizado.
   - **"Cerrar":** Cierra el juego completamente.

La victoria se registra automáticamente en la base de datos (incrementando las partidas ganadas del jugador).

---

## 18. Posibles errores y cómo solucionarlos

### "Error al conectar con la base de datos"

| Causa posible | Solución |
|---|---|
| Sin conexión a Internet | Verifica tu conexión de red |
| Servidor Oracle no disponible | Espera unos minutos e inténtalo de nuevo |
| Driver JDBC no encontrado | Asegúrate de que la librería `BD` (ojdbc) está en el classpath |

### "El usuario debe tener al menos 3 caracteres y sin espacios"

- Asegúrate de que el nombre de usuario tiene **al menos 3 caracteres** y **no contiene espacios**.

### "La contraseña debe tener más de 3 caracteres"

- Introduce una contraseña con **más de 3 caracteres**.

### "Las contraseñas no coinciden"

- En el registro, asegúrate de que el campo de confirmación es **idéntico** a la contraseña.

### "Usuario o contraseña incorrectos"

- Verifica que el nombre de usuario y la contraseña son correctos.
- Si no tienes cuenta, regístrate primero.

### "El nombre de usuario ya existe"

- Elige un nombre de usuario diferente al registrarte.

### "El número de casillas debe ser entre 50 y 150"

- Introduce un valor numérico entre **50** y **150** en el campo de casillas.

### "No puede haber nombres repetidos"

- Asegúrate de que cada jugador tiene un usuario diferente.

### "Los jugadores X y Y tienen el mismo color"

- Selecciona un **color diferente** para cada jugador.

### "No hay partida para guardar" / "La partida ya ha finalizado"

- Solo puedes guardar partidas que estén **en curso** (no finalizadas).

### La música no suena

- Verifica que el archivo `Frost_Menu_Drift.mp3` está en la carpeta `src/`.
- Comprueba que el volumen no está silenciado en los ajustes.
- Asegúrate de que el módulo `javafx.media` está incluido en los VM arguments.

### La aplicación no se inicia (errores de JavaFX)

- Verifica que los **VM Arguments** incluyen los módulos necesarios:
  ```
  --module-path "ruta/javafx-sdk/lib"
  --add-modules javafx.controls,javafx.fxml,javafx.media
  ```
- Asegúrate de usar **JDK 21** o superior.

### Pantalla en blanco o elementos mal posicionados

- Usa una resolución de pantalla de al menos **1280 × 720 px**.
- Si la ventana es demasiado pequeña, redimensiónala manualmente.

---

## 19. Créditos

| Rol | Nombre |
|---|---|
| Desarrollo y diseño | **Carlos Oros Bendezú** |
| Desarrollo y diseño | **Marcelo Renteria Su** |
| Desarrollo y diseño | **Denis Tineo Dias** |

---

> *¡Buena suerte en el hielo, pingüino!*
