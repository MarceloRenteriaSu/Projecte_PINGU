# DOCUMENTACIÓN COMPLETA — EL JOC DEL PINGU
### Proyecto Java + Oracle SQL | Explicación detallada del código

---

## ÍNDICE

1. [¿Qué es este proyecto?](#1-qué-es-este-proyecto)
2. [Estructura general de carpetas](#2-estructura-general-de-carpetas)
3. [Punto de entrada: MAIN_PRUEBAS](#3-punto-de-entrada-main_pruebas)
4. [MODELOS — Los objetos del juego](#4-modelos--los-objetos-del-juego)
   - 4.1 Jugador (clase base)
   - 4.2 Pinguino
   - 4.3 Foca
   - 4.4 Inventario
   - 4.5 Items: Dado, Bola, Pez
   - 4.6 Casilla (clase base)
   - 4.7 Tipos de casilla: Normal, Agujero, Trineo, Oso, SueloQuebradizo, Evento
   - 4.8 Tablero
   - 4.9 Partida
5. [GESTORES — La lógica del juego](#5-gestores--la-lógica-del-juego)
   - 5.1 GestorBBDD
   - 5.2 GestorJugador
   - 5.3 GestorTablero
   - 5.4 GestorPartida
6. [VISTAS — Las pantallas](#6-vistas--las-pantallas)
   - 6.1 PantallaLogin
   - 6.2 PantallaMenu
   - 6.3 PantallaConfig
   - 6.4 PantallaJuego
   - 6.5 PantallaGuerra
   - 6.6 PantallaFin
   - 6.7 PantallaCargarPartida
   - 6.8 PantallaAjustes
   - 6.9 PantallaRegistro
   - 6.10 CursorManager
   - 6.11 MusicManager
   - 6.12 PinguinoRenderer
7. [Base de datos Oracle — Tablas y SQL](#7-base-de-datos-oracle--tablas-y-sql)
8. [Flujo completo del juego paso a paso](#8-flujo-completo-del-juego-paso-a-paso)
9. [Mecánicas y reglas del juego](#9-mecánicas-y-reglas-del-juego)
10. [Diagrama de herencia de clases](#10-diagrama-de-herencia-de-clases)

---

## 1. ¿Qué es este proyecto?

**El Joc del Pingu** es un juego de mesa digital para 1 a 4 jugadores, similar al clásico "Serpientes y Escaleras" pero ambientado en el mundo ártico de los pingüinos.

Los jugadores mueven sus fichas (pingüinos de colores) por un tablero con casillas especiales: pueden encontrarse con un oso polar, resbalar por un trineo, caer en agujeros, o descubrir eventos sorpresa. También existe una **Foca** controlada por el ordenador que puede molestar a los jugadores si está activada.

El proyecto está hecho con **Java + JavaFX** para la interfaz gráfica y usa una **base de datos Oracle** para guardar jugadores, partidas y estadísticas.

**Tecnologías usadas:**
- **Java** — lógica del juego
- **JavaFX** — interfaz visual (pantallas, animaciones, botones)
- **Oracle SQL** — base de datos para usuarios y partidas
- **JDBC** — conexión entre Java y Oracle

---

## 2. Estructura general de carpetas

```
src/
├── clases/
│   └── MAIN_PRUEBAS.java        ← Punto de entrada de la aplicación
│
├── GESTORES/                    ← Clases que controlan la lógica
│   ├── GestorBBDD.java          ← Todo lo relacionado con la base de datos
│   ├── GestorJugador.java       ← Movimiento e interacciones de jugadores
│   ├── GestorPartida.java       ← Control del flujo de la partida
│   └── GestorTablero.java       ← Control de casillas y fin de turno
│
├── MODELOS/                     ← Las "piezas" del juego (objetos)
│   ├── Jugador.java             ← Clase base abstracta para todos los jugadores
│   ├── Pinguino.java            ← El jugador controlado por el usuario
│   ├── Foca.java                ← El enemigo CPU
│   ├── Item.java                ← Clase base para ítems del inventario
│   ├── Dado.java                ← El dado (Normal, Lento, Rápido)
│   ├── Bola.java                ← Bola de nieve (para batallas)
│   ├── Pez.java                 ← Pez (para sobornar a la foca / escapar del oso)
│   ├── Inventario.java          ← Lista de ítems de un pingüino
│   ├── Casilla.java             ← Clase base abstracta para casillas
│   ├── Normal.java              ← Casilla sin efecto
│   ├── Agujero.java             ← Retrocede al agujero anterior
│   ├── Trineo.java              ← Avanza al siguiente trineo
│   ├── Oso.java                 ← Te envía al inicio si no tienes pez
│   ├── SueloQuebradizo.java     ← Penaliza según el inventario
│   ├── Evento.java              ← Casilla con efecto aleatorio
│   ├── Tablero.java             ← El tablero de juego con todas las casillas
│   └── Partida.java             ← El estado actual de la partida
│
└── VISTAS/                      ← Las pantallas de la aplicación
    ├── PantallaLogin.java        ← Pantalla de inicio de sesión
    ├── PantallaMenu.java         ← Menú principal
    ├── PantallaConfig.java       ← Configuración de la partida
    ├── PantallaJuego.java        ← La pantalla del juego en sí
    ├── PantallaGuerra.java       ← Pantalla de batalla entre pingüinos
    ├── PantallaFin.java          ← Pantalla de fin de partida
    ├── PantallaCargarPartida.java← Cargar partida guardada
    ├── PantallaRegistro.java     ← Registro de nuevo usuario
    ├── PantallaAjustes.java      ← Opciones (volumen de música)
    ├── CursorManager.java        ← Cursores personalizados del ratón
    ├── MusicManager.java         ← Música de fondo
    └── PinguinoRenderer.java     ← Dibuja las fichas de pingüino con color
```

---

## 3. Punto de entrada: MAIN_PRUEBAS

**Archivo:** `src/clases/MAIN_PRUEBAS.java`

Este es el archivo que **arranca toda la aplicación**. Es como el "interruptor de encendido".

```java
public class MAIN_PRUEBAS extends Application {
    public void start(Stage stage) throws Exception {
        // 1. Carga la pantalla de login desde el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/VISTAS/PantallaLogin.fxml"));
        
        // 2. Crea la ventana con esa pantalla
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("El Joc del Pingu — Login");
        stage.setMaximized(true);  // Pantalla completa
        stage.show();
        
        // 3. Aplica los cursores personalizados
        CursorManager.apply(scene);
        
        // 4. Inicia la música de fondo
        MusicManager.getInstance().play();
    }
}
```

**Qué hace paso a paso:**
1. Abre la aplicación JavaFX mostrando la pantalla de login.
2. La ventana se abre en modo pantalla completa.
3. Se cargan los cursores personalizados con imágenes de hielo/pingüino.
4. Empieza a sonar la música de fondo que se mantiene en todas las pantallas.

---

## 4. MODELOS — Los objetos del juego

Los modelos son las **representaciones de los elementos del juego**. Piénsalo como los objetos físicos de un juego de mesa real: las fichas, el tablero, los dados, las cartas, etc.

---

### 4.1 Jugador (clase base abstracta)

**Archivo:** `src/MODELOS/Jugador.java`

`Jugador` es la clase **padre** de la que heredan tanto `Pinguino` como `Foca`. Define lo mínimo que tiene cualquier jugador:

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `nom` | String | Nombre del jugador |
| `pos` | int | Posición actual en el tablero (número de casilla) |

**Métodos:**
- `getPos()` / `setPos()` — obtener o cambiar la posición en el tablero
- `getNom()` / `setNom()` — obtener o cambiar el nombre
- `moverPosicio(int p)` — método que los hijos sobreescriben para moverse

> **¿Por qué es "abstracta"?** Porque nunca se crea un "Jugador" genérico directamente. Siempre se crea un Pingüino o una Foca específica. La clase abstracta es como una plantilla.

---

### 4.2 Pinguino

**Archivo:** `src/MODELOS/Pinguino.java`

`Pinguino` representa a **cada jugador humano** de la partida. Hereda de `Jugador` y añade:

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `color` | String | Color de la ficha (ej: "#2563EB" azul) |
| `inv` | Inventario | Su bolsa de ítems |
| `juega` | boolean | Si puede jugar en este turno o debe esperar |

**Métodos importantes:**

#### `GestionarBatalla(Pinguino oponente)`
Cuando dos pingüinos caen en la misma casilla, se pelean con bolas de nieve:
- Cuenta cuántas bolas tiene cada uno.
- El que tiene menos bolas retrocede tantas casillas como diferencia hay.
- Ambos pierden 1 bola de nieve al terminar.

```
Ejemplo: Pingüino A tiene 3 bolas, Pingüino B tiene 1 bola.
→ B retrocede 2 casillas (3-1=2). Ambos pierden 1 bola.
```

#### `agregarItem(Item i)`
Añade un ítem al inventario respetando los **límites máximos**:
- Máximo **3 dados** en total (de cualquier tipo)
- Máximo **2 peces**
- Máximo **6 bolas** de nieve

#### `usarItem(Item i)`
Usa un ítem del inventario:
- **Pez:** Se consume (cantidad -1). Si llega a 0, desaparece del inventario.
- **Dado Normal:** No se consume, siempre queda (cantidad = 1).
- **Dado Lento/Rápido:** Se consume al usarse.

#### `perderMitadItems()`
Quita aleatoriamente la mitad del inventario (lo usa la foca al golpear).

#### `perderTurno()`
Marca `juega = false` para que el jugador pierda su próximo turno.

#### `moverPosicio(int p)`
Mueve al pingüino `p` posiciones. Si llegaría a negativo, se queda en 0.

---

### 4.3 Foca

**Archivo:** `src/MODELOS/Foca.java`

La `Foca` es el **enemigo controlado por el ordenador**. También hereda de `Jugador`.

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `Soborno` | boolean | Si ha sido sobornada (no se mueve) |
| `turnosBloquejada` | int | Turnos que le quedan bloqueada |

**Métodos:**

#### `moverPosicio(int p)`
La foca se mueve sola. Cada turno:
- Si está **bloqueada** (sobornada): descuenta un turno. Al llegar a 0, vuelve a moverse.
- Si **no está bloqueada**: tira un dado Normal (1-6) y avanza.

#### `golpearJugador(Partida p, Pinguino pingu)`
La foca **pasa por encima** de un pingüino de camino (no se detiene): el pingüino pierde la mitad de su inventario.

#### `aplastarJugador(Partida partida, Pinguino pingu)`
La foca **se detiene** en la misma casilla que un pingüino:
- Si el pingüino tiene al menos **1 pez** → usa el pez para sobornar a la foca (se bloquea 2 turnos).
- Si no tiene peces → el pingüino retrocede hasta el agujero anterior, o al inicio si no hay ninguno.

#### `esSobornado(Partida partida, Pinguino p)`
Activa el estado de soborno: `Soborno = true`, `turnosBloquejada = 2`.

---

### 4.4 Inventario

**Archivo:** `src/MODELOS/Inventario.java`

El `Inventario` es la **mochila** del pingüino. Guarda una lista de ítems.

| Método | Descripción |
|--------|-------------|
| `contarItem(Item item)` | Devuelve cuántas unidades hay de un ítem concreto |
| `totalItems()` | Devuelve el total de todos los ítems sumados |
| `contarDados()` | Cuenta cuántos tipos de dado diferentes hay (no la cantidad) |

> **Nota:** El inventario distingue ítems por su nombre. Si tienes 3 bolas y 1 pez, `totalItems()` devuelve 4.

---

### 4.5 Items: Dado, Bola, Pez

**Clase base `Item`** (`src/MODELOS/Item.java`):
Todos los ítems tienen `nom` (nombre) y `cantidad`.

#### Dado (`src/MODELOS/Dado.java`)
El dado se puede tirar para moverse. Hay 3 tipos:

| Tipo | Rango de valores | Descripción |
|------|-----------------|-------------|
| Normal | 1 a 6 | El dado estándar |
| Lento | 1 a 3 | Avanza poco, útil para movimientos precisos |
| Rápido | 5 a 10 | Avanza mucho, ideal para acercarse a la meta |

El método `tirar()` genera un número aleatorio dentro del rango.

#### Bola (`src/MODELOS/Bola.java`)
La bola de nieve se usa en las **batallas** entre pingüinos. El que tenga más bolas gana y el rival retrocede. Máximo 6 por jugador.

#### Pez (`src/MODELOS/Pez.java`)
El pez sirve para dos cosas:
1. **Sobornar a la foca** cuando te aplasta (se bloquea 2 turnos).
2. **Escapar del oso** cuando caes en su casilla.
Máximo 2 por jugador.

---

### 4.6 Casilla (clase base abstracta)

**Archivo:** `src/MODELOS/Casilla.java`

Todas las casillas del tablero heredan de `Casilla`. Lo único que tiene es:
- `pos` (int): posición en el tablero
- `realizarAccion(Partida p, Jugador j)`: método que cada casilla implementa de forma diferente

> Al igual que `Jugador`, nunca se crea una `Casilla` genérica, siempre un tipo concreto.

---

### 4.7 Tipos de casilla

#### Normal (`src/MODELOS/Normal.java`)
No hace nada. El pingüino cae aquí y se queda sin consecuencias. La primera y la última casilla del tablero son siempre de tipo Normal.

#### Agujero (`src/MODELOS/Agujero.java`)
Cuando un pingüino cae en un agujero, **retrocede al agujero anterior** que ya haya pasado. Si no hay ningún agujero anterior, vuelve al inicio (casilla 0).

```
Ejemplo: tablero tiene agujeros en posición 5, 12, 20.
Si el pingüino cae en la posición 20 → va a la posición 12.
Si el pingüino cae en la posición 5 → va a la posición 0 (inicio).
```

#### Trineo (`src/MODELOS/Trineo.java`)
El trineo **lanza al pingüino hacia adelante** hasta el siguiente trineo del tablero. Si no hay más trineos por delante, avanza el 10% del tamaño total del tablero.

```
Ejemplo: tablero de 100 casillas, trineos en posiciones 10, 35, 70.
Si el pingüino está en posición 10 → va a la posición 35.
Si el pingüino está en posición 70 → avanza 10 casillas (100/10=10) → va a la 80.
```

#### Oso (`src/MODELOS/Oso.java`)
El oso polar es una casilla peligrosa:
- Si el pingüino tiene **al menos 1 pez** en el inventario: usa el pez para distraer al oso y se queda en su sitio.
- Si **no tiene peces**: el oso lo envía de vuelta al inicio (posición 0).

#### SueloQuebradizo (`src/MODELOS/SueloQuebradizo.java`)
El suelo se rompe según el peso del inventario:
- Si tiene **más de 5 ítems en total**: demasiado pesado, el suelo cede → va al inicio (posición 0).
- Si tiene entre **1 y 5 ítems**: el suelo cruje pero aguanta → pierde el siguiente turno.
- Si tiene **0 ítems**: no pasa nada (no hay peso).

#### Evento (`src/MODELOS/Evento.java`)
La casilla de evento es una **sorpresa aleatoria**. Al caer aquí, se genera un evento al azar con las siguientes probabilidades:

| Evento | Probabilidad | Efecto |
|--------|-------------|--------|
| Dados | 15% | Recibe un Dado Lento o Rápido al azar |
| Moto de Nieve | 15% | Avanza hasta el siguiente trineo |
| Bola de nieve | 30% | Recibe 1 a 3 bolas de nieve |
| Pez | 20% | Recibe 1 pez |
| Perder turno | 10% | Pierde el siguiente turno |
| Perder objeto | 10% | Pierde un ítem aleatorio del inventario |

---

### 4.8 Tablero

**Archivo:** `src/MODELOS/Tablero.java`

El `Tablero` contiene todas las casillas y se genera **automáticamente** al crear una partida nueva.

| Atributo | Descripción |
|----------|-------------|
| `casillas` | Lista de todas las casillas del tablero |
| `tamanyo` | Número total de casillas (mínimo 50, máximo 150) |

**Generación del tablero:**
El tablero siempre empieza y termina con una casilla Normal. Las casillas intermedias se generan aleatoriamente con estas probabilidades:

| Tipo | Probabilidad acumulada | Probabilidad real |
|------|----------------------|-------------------|
| Normal | 0–15% | 15% |
| Oso | 15–30% | 15% |
| Agujero | 30–45% | 15% |
| Trineo | 45–65% | 20% |
| Evento | 65–85% | 20% |
| SueloQuebradizo | 85–100% | 15% |

**Métodos de búsqueda:**
- `agujeroAnterior(int posActual)` — busca hacia atrás el agujero más cercano
- `trineoPosterior(int posActual)` — busca hacia adelante el trineo más cercano
- `getCasilla(int pos)` — devuelve la casilla de una posición concreta

---

### 4.9 Partida

**Archivo:** `src/MODELOS/Partida.java`

La `Partida` es el **estado completo del juego en un momento dado**. Contiene todo lo necesario para saber cómo está la partida:

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `tablero` | Tablero | El tablero con todas las casillas |
| `jugadores` | ArrayList\<Jugador\> | Lista de todos los jugadores (pingüinos + foca) |
| `turnos` | int | Contador total de turnos jugados |
| `jugadorActual` | int | Índice del jugador que tiene el turno |
| `finalizada` | boolean | Si la partida ha terminado |
| `ganador` | Jugador | El jugador que ganó (null si no ha acabado) |

**Método `siguienteTurno()`:**
Avanza al siguiente jugador de forma circular:
```
jugadorActual = (jugadorActual + 1) % jugadores.size()
```
Esto significa: si hay 3 jugadores (índices 0, 1, 2), después del índice 2 vuelve al 0.
También incrementa el contador de turnos.

---

## 5. GESTORES — La lógica del juego

Los gestores son las clases que **hacen que las cosas pasen**. Utilizan los modelos para aplicar las reglas del juego.

---

### 5.1 GestorBBDD

**Archivo:** `src/GESTORES/GestorBBDD.java`

Es la clase más larga del proyecto (~965 líneas). Se encarga de **toda la comunicación con la base de datos Oracle**.

#### Conexión a la base de datos

Hay dos métodos para conectarse:

**`conectarBaseDatos(Scanner scan)`** — versión por consola (para pruebas):
- Pregunta si estás dentro o fuera del centro para elegir la URL correcta.
- URL dentro del centro: `jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2`
- URL fuera del centro: `jdbc:oracle:thin:@//oracle.ilerna.com:1521/XEPDB2`
- Usuario: `DW2526_GR02_PINGU` | Contraseña: `ACOMRDT`

**`conectarBBDD(String entorno, String user, String pass)`** — versión para JavaFX:
- No necesita Scanner. Recibe el entorno, usuario y contraseña como parámetros.
- Es el que usa la aplicación gráfica (se conecta siempre como "fuera").

#### Gestión de usuarios

| Método | Descripción |
|--------|-------------|
| `loginUsuario(con, username, password)` | Devuelve `true` si el usuario y contraseña son correctos |
| `registrarUsuario(con, username, password)` | Registra un nuevo usuario. Devuelve 0=ok, 1=ya existe, -1=error |
| `usuarioExiste(con, username)` | Comprueba si un nombre de usuario ya está ocupado |
| `incrementarPartidasJugadas(con, username)` | Suma 1 a las partidas jugadas del usuario |
| `incrementarPartidasGanadas(con, username)` | Suma 1 a las partidas ganadas del usuario |
| `getUsuarios(con, exclude)` | Devuelve la lista de todos los usuarios excepto uno concreto |

#### Gestión de partidas guardadas

**`guardarPartida(...)`** — Guarda una partida en **3 tablas simultáneamente**:
1. Primero guarda los datos generales en `PINGU_PARTIDAS`.
2. Luego guarda cada pingüino en `PINGU_PINGUINOS`.
3. Finalmente guarda el inventario de cada pingüino en `PINGU_INVENTARIS`.
Usa una **transacción** (`commit`/`rollback`) para que si falla algo a mitad, no se guarde nada a medias.

**`cargarPartidaPorId(con, id)`** — Carga una partida completa:
1. Lee los datos generales de `PINGU_PARTIDAS`.
2. Lee los pingüinos de `PINGU_PINGUINOS`.
3. Para cada pingüino, lee su inventario de `PINGU_INVENTARIS`.
4. Devuelve todo junto en un mapa.

**`listarPartidas(con, username)`** — Lista todas las partidas **no acabadas** de un usuario, ordenadas de más reciente a más antigua. Usa `LISTAGG` de Oracle para unir los nombres de los jugadores en una sola cadena.

**`borrarPartidaPorId(con, id)`** — Borra una partida eliminando en cascada:
1. Primero borra los eventos (`PINGU_EVENTS`)
2. Luego los inventarios (`PINGU_INVENTARIS`)
3. Luego los pingüinos (`PINGU_PINGUINOS`)
4. Finalmente la partida principal (`PINGU_PARTIDAS`)

#### Ranking y estadísticas

| Método | Descripción |
|--------|-------------|
| `getRanking(con)` | Top 20 jugadores ordenados por victorias |
| `getRankingPerJugades(con)` | Top 20 jugadores ordenados por partidas jugadas |
| `getRecord(con)` | Llama a la función Oracle `F_PINGU_RECORD()` para obtener el máximo de victorias |
| `getMitja(con)` | Llama a `F_PINGU_MITJA()` para obtener la media de victorias |
| `getPctMenysGuanyades(con, wins)` | Llama a `F_PINGU_PCT_MENYS()` para el % de jugadores con menos victorias |
| `getJugadorsRecord(con)` | Lista los jugadores que tienen el récord de victorias |
| `getJugadorsSobreMitja(con)` | Lista jugadores con victorias por encima de la media |
| `getPartidasGanadasUsuario(con, username)` | Victorias de un usuario concreto |

> **Sobre las funciones PL/SQL:** `F_PINGU_RECORD`, `F_PINGU_MITJA` y `F_PINGU_PCT_MENYS` son funciones que viven en la base de datos Oracle. Si no están disponibles, el código tiene un "plan B" que calcula lo mismo directamente con SQL.

#### Métodos de utilidad general

| Método | Descripción |
|--------|-------------|
| `select(con, sql)` | Ejecuta cualquier SELECT y devuelve resultados como lista de mapas |
| `insert(con, sql)` | Ejecuta un INSERT |
| `update(con, sql)` | Ejecuta un UPDATE |
| `delete(con, sql)` | Ejecuta un DELETE |
| `print(con, sql, columnas)` | Imprime los resultados de un SELECT por consola |
| `cerrar(con)` | Cierra la conexión con la base de datos |

---

### 5.2 GestorJugador

**Archivo:** `src/GESTORES/GestorJugador.java`

Clase pequeña que gestiona las **acciones directas de los jugadores**:

| Método | Descripción |
|--------|-------------|
| `jugadorSeMueve(j, pasos, tablero)` | Mueve al jugador `pasos` posiciones. No deja salir del tablero (clamp entre 0 y tamaño-1) |
| `jugadorFinalizaTurno(j)` | Marca al pingüino como que ya jugó (`juega = false`) |
| `piguinoEvento(p, partida, c)` | Ejecuta el evento de una casilla Evento sobre un pingüino |
| `pinguinoGuerra(p1, p2)` | Inicia la batalla entre dos pingüinos |
| `focaInteractua(p, f, partida)` | La foca golpea a un pingüino (pierde mitad del inventario) |
| `jugadorUsaItem(j, i)` | Hace que el jugador use un ítem de su inventario |

---

### 5.3 GestorTablero

**Archivo:** `src/GESTORES/GestorTablero.java`

Clase pequeña que gestiona las **acciones del tablero**:

#### `ejecutarCasilla(partida, pinguino, casilla)`
Verifica que el pingüino realmente está en esa casilla y ejecuta su efecto. Después comprueba si el pingüino llegó a la última casilla → si es así, **la partida termina** y ese pingüino es el ganador.

#### `comprobarFinTurno(partida)`
Se llama al terminar cada turno. Si la partida no ha acabado, avanza al siguiente jugador. Además reactiva el flag `juega = true` del jugador que acaba de jugar para que pueda jugar en su próximo turno.

---

### 5.4 GestorPartida

**Archivo:** `src/GESTORES/GestorPartida.java`

Es el **director de orquesta** del juego. Coordina a los demás gestores para que una partida completa funcione correctamente.

| Atributo | Descripción |
|----------|-------------|
| `partida` | La partida actual |
| `gestorTablero` | Para gestionar casillas y fin de turno |
| `gestorJugador` | Para mover jugadores e interacciones |

#### `nuevaPartida(Tablero t, ArrayList<Jugador> j)`
Crea una nueva partida con el tablero y jugadores indicados.

#### `tirarDado(Jugador j, Dado dado)`
Lanza el dado y mueve al jugador la cantidad de casillas que salga.

#### `ejecutarTurnoCompleto()`
Ejecuta todo el turno del jugador actual en orden:
1. Obtiene quién debe jugar ahora.
2. Llama a `procesarTurnoJugador`.
3. Actualiza el estado del tablero (corrige posiciones fuera de límites).
4. Comprueba si el turno ha terminado.

#### `procesarTurnoJugador(Jugador j)`
La lógica de qué pasa en el turno de cada tipo de jugador:

**Si es Pingüino:**
- Si tiene `juega = false` → pierde el turno, lo resetea a `true`.
- Si puede jugar → tira dado Normal, mueve, ejecuta la casilla donde cae.
- Comprueba si coincide con otro pingüino → batalla.
- Finaliza el turno.

**Si es Foca:**
- Se mueve automáticamente.
- Si coincide con algún pingüino → la foca interactúa (golpea).

#### `siguienteTurno()`
Avanza al siguiente jugador (si la partida no ha terminado).

---

## 6. VISTAS — Las pantallas

Las vistas son las **pantallas que ve el usuario**. Están hechas con JavaFX y cada una tiene un archivo `.java` (lógica) y un archivo `.fxml` (diseño visual).

---

### 6.1 PantallaLogin

**Archivo:** `src/VISTAS/PantallaLogin.java`

Es la **primera pantalla** que ve el usuario al abrir la aplicación. Tiene dos formularios:

**Formulario de Login:**
- Campo de usuario y contraseña.
- Validaciones: mínimo 3 caracteres, sin espacios, contraseña de más de 3 caracteres.
- Si es correcto → va al menú principal.

**Formulario de Registro:**
- Campos de usuario, contraseña y confirmación.
- Validaciones: mismas que el login + las contraseñas deben coincidir.
- Si hay éxito → cierra el formulario de registro y abre el de login con el usuario ya relleno.

**Efecto visual:**
Cuando se abre un formulario, el fondo se desenfoca (efecto `GaussianBlur`).

**Flujo tras el login exitoso:**
Carga `PantallaMenu.fxml`, pasa el nombre del usuario y muestra la pantalla del menú.

---

### 6.2 PantallaMenu

**Archivo:** `src/VISTAS/PantallaMenu.java`

El **menú principal** del juego. Muestra el nombre del usuario que ha iniciado sesión y ofrece estas opciones:

| Botón | Acción |
|-------|--------|
| Nueva Partida | Abre `PantallaConfig` para configurar la partida |
| Cargar Partida | Abre `PantallaCargarPartida` para elegir una partida guardada |
| Ranking | Muestra la ventana de ranking y estadísticas |
| Opciones | Abre `PantallaAjustes` para ajustar el volumen |
| Créditos | Muestra un mensaje con los nombres de los creadores |
| Salir | Cierra la conexión con la BD y cierra la aplicación |

**Ventana de Ranking:**
Se abre como ventana flotante con dos pestañas:
1. **Clasificación:** Tabla con los 20 mejores jugadores (jugadas, victorias, % victoria).
2. **Estadísticas:** Récord mundial, media de victorias, jugadores sobre la media, y la posición del usuario actual.

---

### 6.3 PantallaConfig

**Archivo:** `src/VISTAS/PantallaConfig.java`

Pantalla de **configuración antes de empezar la partida**. Permite elegir:

1. **Número de casillas** del tablero (entre 50 y 150).
2. **Número de jugadores** (1 a 4). El jugador 1 siempre es el usuario logueado.
3. **Jugadores 2-4:** Se eligen de una lista desplegable con los usuarios registrados.
4. **Color de cada jugador:** Un botón de color que abre una paleta de 16 colores distintos.
5. **Foca activada/desactivada:** Checkbox.

**Validaciones:**
- Las casillas deben estar entre 50 y 150.
- No puede haber jugadores duplicados.
- No puede haber dos jugadores con el mismo color.

**Paleta de colores:**
16 colores predefinidos organizados en una cuadrícula 4x4. Al pasar el ratón encima, el color se agranda ligeramente. Al hacer clic, se actualiza el botón y se redibuja la miniatura del pingüino con ese color.

Al pulsar "Comenzar", se crea la `PantallaJuego` y se inicia la partida.

---

### 6.4 PantallaJuego

**Archivo:** `src/VISTAS/PantallaJuego.java`

Es la **pantalla más grande y compleja** del proyecto (~1681 líneas). Contiene toda la experiencia de juego visual.

**Elementos principales:**

| Elemento | Descripción |
|----------|-------------|
| Tablero (`GridPane`) | La cuadrícula con todas las casillas del juego |
| Fichas (Canvas P1-P5) | Hasta 4 pingüinos + 1 foca, dibujados sobre el tablero |
| Panel de inventario | 4 slots: Dado Rápido, Dado Lento, Peces, Bolas |
| Lista de eventos | Historial de lo que ha pasado en la partida |
| Botón "Tirar Dado" | Inicia el turno del jugador actual |
| Botón "Auto-play" | El juego se juega solo automáticamente |
| Menú | Nueva partida, guardar, cargar, salir |

**Funcionamiento del turno:**
1. El usuario pulsa "Tirar Dado" (o selecciona un dado especial del inventario).
2. Se lanza el dado y el pingüino se **anima** casilla por casilla hasta llegar a su destino.
3. Se ejecuta el efecto de la casilla.
4. Si coincide con otro pingüino → se abre `PantallaGuerra`.
5. Se actualiza el inventario y la lista de eventos.
6. Pasa al siguiente jugador.

**Animación de movimiento:**
Las fichas no "saltan" directamente a la nueva posición. Se mueven casilla por casilla con un pequeño retraso entre cada paso (usando `SequentialTransition` y `TranslateTransition`), lo que da una sensación de movimiento fluido.

**Auto-play:**
Cuando está activado, un `Timeline` ejecuta automáticamente el turno de cada jugador cada cierto tiempo. Ideal para ver la partida jugarse sola o si hay jugadores CPU.

**Guardar y cargar partidas:**
- **Guardar:** Pide un nombre para la partida y llama a `GestorBBDD.guardarPartida()` con el estado actual.
- **Cargar:** Abre `PantallaCargarPartida`, el usuario elige una partida y se restaura todo el estado del juego con `restaurarPartida()`.

**`restaurarPartida(datos)`:**
Lee los datos del mapa devuelto por la BD y reconstruye toda la partida: tablero, posiciones, inventarios, turno actual, estado de la foca.

---

### 6.5 PantallaGuerra

**Archivo:** `src/VISTAS/PantallaGuerra.java`

Ventana modal que aparece cuando dos pingüinos coinciden en la misma casilla. Presenta dos opciones al jugador:

1. **Escapar** — El pingüino intenta huir retrocediendo algunas casillas.
2. **Batallar** — Se comparan las bolas de nieve. El que tiene menos retrocede.

---

### 6.6 PantallaFin

**Archivo:** `src/VISTAS/PantallaFin.java`

Pantalla que aparece cuando alguien **gana la partida** (llega a la última casilla). Muestra el nombre del ganador y da opciones para volver al menú o salir.

---

### 6.7 PantallaCargarPartida

**Archivo:** `src/VISTAS/PantallaCargarPartida.java`

Ventana modal que muestra la lista de **partidas guardadas** del usuario. Muestra para cada partida:
- Nombre de la partida
- Jugadores que participaban
- Número de turnos jugados
- Fecha en que se guardó

Al seleccionar una y pulsar "Cargar", se devuelven los datos a la pantalla del menú para continuar la partida.

---

### 6.8 PantallaAjustes

**Archivo:** `src/VISTAS/PantallaAjustes.java`

Ventana simple de **opciones** con un slider (barra deslizante) para controlar el volumen de la música de fondo. Llama a `MusicManager.setVolume()` para ajustar el volumen en tiempo real.

---

### 6.9 PantallaRegistro

**Archivo:** `src/VISTAS/PantallaRegistro.java`

Pantalla alternativa de registro (versión separada de la de login). Tiene la misma funcionalidad que el formulario de registro de `PantallaLogin`.

---

### 6.10 CursorManager

**Archivo:** `src/VISTAS/CursorManager.java`

Gestiona los **dos cursores personalizados** de la aplicación:
- `cursor1.png` — Cursor de flecha (navegación normal)
- `cursor2.png` — Cursor de mano (elementos clicables)

**Cómo funciona:**
1. Al cargar la aplicación, carga las imágenes de los cursores.
2. `apply(scene)` — Recorre todos los elementos de la pantalla y asigna el cursor de mano a todos los botones, checkboxes, combos, sliders, etc.
3. `applyClickable(node)` — Asigna el cursor de mano a un elemento concreto.

**Singleton de carga "lazy":** Los cursores solo se cargan la primera vez que se necesitan y se reutilizan en todas las pantallas.

---

### 6.11 MusicManager

**Archivo:** `src/VISTAS/MusicManager.java`

Gestiona la **música de fondo** que suena durante todo el juego. Usa el patrón **Singleton** (una sola instancia en toda la aplicación).

Características:
- Reproduce el archivo `Frost_Menu_Drift.mp3` en bucle infinito.
- La música **persiste entre cambios de pantalla** porque el reproductor vive en esta clase, no en las pantallas individuales.
- `play()` — Inicia la música (si ya está sonando, no hace nada).
- `stop()` — Para la música y libera recursos.
- `setVolume(double)` — Ajusta el volumen entre 0.0 y 1.0.
- `getVolume()` — Devuelve el volumen actual.

---

### 6.12 PinguinoRenderer

**Archivo:** `src/VISTAS/PinguinoRenderer.java`

Clase que **dibuja las fichas de pingüino** en el tablero. Los pingüinos son sprites píxel-art (diseño en píxeles) que se colorean dinámicamente con el color elegido por cada jugador.

Funciona usando un patrón de bits (0 y 1) que define la silueta del pingüino. Los píxeles de color 1 se pintan con el color del jugador, y los de color 2 con el color de contraste (para las partes blancas del pingüino).

`draw(gc, pixelSize, color, isHighlighted)` — Dibuja el pingüino en un Canvas de JavaFX con el color y tamaño especificados.

---

## 7. Base de datos Oracle — Tablas y SQL

La aplicación usa una base de datos Oracle con **5 tablas principales**:

### Tabla PINGU_USERS
Guarda todos los usuarios registrados.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `USERNAME` | VARCHAR2 | Nombre de usuario (clave primaria) |
| `PASSWORD` | VARCHAR2 | Contraseña (texto plano) |
| `PARTIDAS_JUGADAS` | NUMBER | Contador de partidas jugadas |
| `PARTIDAS_GANADAS` | NUMBER | Contador de partidas ganadas |

---

### Tabla PINGU_PARTIDAS
Guarda los datos generales de cada partida guardada.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `ID` | NUMBER | Identificador único (auto-incremento con secuencia) |
| `USERNAME` | VARCHAR2 | Usuario propietario de la partida |
| `NOM_PARTIDA` | VARCHAR2 | Nombre dado a la partida al guardar |
| `NUM_CASILLAS` | NUMBER | Tamaño del tablero |
| `CASILLAS_TIPOS` | VARCHAR2 | Tipos de casillas del tablero (serializado) |
| `FOCA_ACTIVADA` | NUMBER | 1 si la foca está activa, 0 si no |
| `FOCA_POS` | NUMBER | Posición actual de la foca |
| `FOCA_SOBORNO` | NUMBER | 1 si la foca está sobornada |
| `FOCA_TURNOS_BLOQ` | NUMBER | Turnos que le quedan bloqueada |
| `TURNOS` | NUMBER | Número de turnos jugados hasta el momento |
| `JUGADOR_ACTUAL` | NUMBER | Índice del jugador que debe jugar |
| `FECHA_GUARDADO` | TIMESTAMP | Fecha y hora en que se guardó |
| `ACABADA` | NUMBER | 1 si la partida terminó, 0 si no |
| `GANADOR` | VARCHAR2 | Nombre del ganador (si acabó) |

---

### Tabla PINGU_PINGUINOS
Guarda el estado de cada pingüino de una partida guardada.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `ID` | NUMBER | Identificador único |
| `PARTIDA_ID` | NUMBER | ID de la partida a la que pertenece |
| `INDEX_JUG` | NUMBER | Orden del jugador (0, 1, 2, 3) |
| `NOM` | VARCHAR2 | Nombre del pingüino |
| `POSICIO` | NUMBER | Posición en el tablero al guardar |

---

### Tabla PINGU_INVENTARIS
Guarda los ítems del inventario de cada pingüino.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `ID` | NUMBER | Identificador único |
| `PINGUINO_ID` | NUMBER | ID del pingüino al que pertenece |
| `NOM_ITEM` | VARCHAR2 | Nombre del ítem (Normal, Lento, Rapido, Pez, Bola) |
| `QUANTITAT` | NUMBER | Cantidad de ese ítem |

---

### Tabla PINGU_EVENTS
Guarda el historial de eventos de cada partida.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `ID` | NUMBER | Identificador único |
| `PARTIDA_ID` | NUMBER | ID de la partida |
| `ORDRE` | NUMBER | Orden del evento en el historial |
| `TEXT` | VARCHAR2(500) | Texto descriptivo del evento |

---

### Funciones PL/SQL en Oracle

La base de datos tiene funciones que calculan estadísticas:

| Función | Parámetros | Devuelve |
|---------|-----------|---------|
| `F_PINGU_RECORD()` | — | El máximo de partidas ganadas por cualquier jugador |
| `F_PINGU_MITJA()` | — | La media de partidas ganadas de todos los jugadores |
| `F_PINGU_PCT_MENYS(p_wins)` | Un número de victorias | % de jugadores que han ganado menos de ese número |

También existe un **trigger** `TRG_INCR_GANADAS` que se activa automáticamente cuando una partida se marca como acabada (`ACABADA = 1`): incrementa el contador de victorias del ganador en `PINGU_USERS`.

---

### Secuencias de auto-incremento

Oracle no tiene `AUTO_INCREMENT` como MySQL. En su lugar se usan **secuencias**:
- `PINGU_PARTIDAS_SEQ` — Genera IDs para PINGU_PARTIDAS
- `PINGU_PINGUINOS_SEQ` — Genera IDs para PINGU_PINGUINOS
- `PINGU_INVENTARIS_SEQ` — Genera IDs para PINGU_INVENTARIS
- `PINGU_EVENTS_SEQ` — Genera IDs para PINGU_EVENTS

Tras insertar, se obtiene el ID generado con:
```sql
SELECT PINGU_PARTIDAS_SEQ.CURRVAL FROM DUAL
```

---

## 8. Flujo completo del juego paso a paso

Aquí se explica todo lo que ocurre desde que abres la app hasta que alguien gana:

```
1. INICIO DE LA APLICACIÓN
   └── MAIN_PRUEBAS.start() abre PantallaLogin
       └── Carga cursores personalizados
       └── Inicia música de fondo

2. LOGIN / REGISTRO
   └── Usuario introduce nombre y contraseña
   └── GestorBBDD.loginUsuario() consulta PINGU_USERS
   └── Si correcto → PantallaMenu con el nombre del usuario

3. MENÚ PRINCIPAL
   └── Nueva Partida → PantallaConfig
   └── Cargar Partida → PantallaCargarPartida → PantallaJuego
   └── Ranking → Ventana de estadísticas desde la BD
   └── Opciones → Control de volumen
   └── Créditos → Mensaje con los autores

4. CONFIGURACIÓN DE PARTIDA (nueva)
   └── Elige número de casillas (50-150)
   └── Elige número de jugadores (1-4)
   └── Elige colores para cada jugador
   └── Activa/desactiva la foca
   └── Pulsa "Comenzar" → PantallaJuego.iniciarJoc()

5. INICIO DEL JUEGO
   └── Se crea el Tablero con casillas aleatorias
   └── Se crean los Pinguino con inventario vacío
   └── Si hay foca: se crea la Foca en posición 0
   └── Todos los jugadores empiezan en la casilla 0
   └── Se dibuja el tablero y las fichas

6. TURNO DE UN JUGADOR (se repite hasta que alguien gane)
   └── Es el turno del jugador con índice jugadorActual
   
   A. Si el jugador tiene juega=false (penalizado):
      └── Se salta su turno, juega se pone a true
   
   B. Si puede jugar:
      └── (Opcional) El usuario selecciona un dado especial del inventario
      └── Pulsa "Tirar Dado" → GestorPartida.tirarDado()
      └── Se genera número aleatorio según el tipo de dado
      └── La ficha se anima moviéndose casilla por casilla
      └── Se ejecuta el efecto de la casilla donde cae:
          - Normal: nada
          - Agujero: retrocede al agujero anterior
          - Trineo: avanza al siguiente trineo
          - Oso: usa pez o va al inicio
          - SueloQuebradizo: pierde turno o va al inicio según inventario
          - Evento: efecto aleatorio (dado, bola, pez, trineo, perder turno, perder objeto)
      └── Comprueba si coincide con otro pingüino → PantallaGuerra
      └── Se actualiza el inventario en la UI
      └── Se añade el evento a la lista de eventos
   
   C. Si es el turno de la Foca:
      └── Se mueve automáticamente
      └── Si coincide con un pingüino → golpea (pierde mitad del inventario)
   
   D. GestorTablero.comprobarFinTurno():
      └── Si el pingüino llegó a la última casilla → PARTIDA TERMINADA
      └── Si no → siguienteTurno() → vuelve al paso 6 con el siguiente jugador

7. FIN DE PARTIDA
   └── PantallaFin muestra el nombre del ganador
   └── GestorBBDD.marcarPartidaAcabadaConGanador() actualiza la BD
   └── El trigger TRG_INCR_GANADAS suma 1 victoria al ganador
   └── GestorBBDD.incrementarPartidasJugadas() para todos los jugadores
   └── Opción de volver al menú o salir
```

---

## 9. Mecánicas y reglas del juego

### El tablero
- Tiene entre **50 y 150 casillas**.
- La primera casilla (0) y la última son siempre Normal.
- Las casillas intermedias son generadas aleatoriamente.
- Para ganar hay que **llegar exactamente a la última casilla** (o pasarla).

### El movimiento
- Cada turno se tira un dado.
- El dado Normal da valores del 1 al 6.
- El dado Lento da del 1 al 3 (útil para no pasarte de la meta).
- El dado Rápido da del 5 al 10 (para avanzar rápido).
- Los dados Lento y Rápido son de **un solo uso** y se obtienen en casillas Evento.

### El inventario
Cada pingüino tiene una mochila con límites:

| Ítem | Máximo | Para qué sirve |
|------|--------|----------------|
| Dado Lento | 3 en total entre todos los dados | Movimiento conservador |
| Dado Rápido | 3 en total entre todos los dados | Movimiento agresivo |
| Pez | 2 | Escapar del oso / sobornar a la foca |
| Bola de Nieve | 6 | Ganar batallas contra otros pingüinos |

### Las batallas
Cuando dos pingüinos coinciden en la misma casilla:
- Se abre la pantalla de batalla.
- Se pueden comparar bolas de nieve.
- El que tenga **menos bolas retrocede** tantas casillas como diferencia hay.
- Ambos pierden 1 bola al terminar la batalla.
- Si uno elige huir, puede retroceder casillas para evitar la batalla.

### La foca (opcional)
- Empieza en la casilla 0 y se mueve con un dado Normal cada turno.
- Si **pasa por encima** de un pingüino (de camino a otra casilla): el pingüino pierde la mitad de su inventario.
- Si se **detiene en la misma casilla** que un pingüino:
  - Con pez → foca bloqueada 2 turnos, se consume el pez.
  - Sin pez → pingüino va al agujero anterior o al inicio.
- La foca puede ser **sobornada** por 2 turnos máximo.

---

## 10. Diagrama de herencia de clases

```
Object
│
├── Jugador (abstracta)
│   ├── Pinguino
│   └── Foca
│
├── Casilla (abstracta)
│   ├── Normal
│   ├── Agujero
│   ├── Trineo
│   ├── Oso
│   ├── SueloQuebradizo
│   └── Evento
│
├── Item (abstracta)
│   ├── Dado
│   ├── Bola
│   └── Pez
│
├── Inventario
├── Tablero
└── Partida

Gestores (clases de servicio, no heredan):
├── GestorBBDD
├── GestorJugador
├── GestorTablero
└── GestorPartida

Vistas (controladores JavaFX):
├── PantallaLogin
├── PantallaMenu
├── PantallaConfig
├── PantallaJuego
├── PantallaGuerra
├── PantallaFin
├── PantallaCargarPartida
├── PantallaAjustes
├── PantallaRegistro
├── CursorManager (utilidad estática)
├── MusicManager (singleton)
└── PinguinoRenderer (utilidad estática)
```

---

## RESUMEN RÁPIDO

| Pregunta | Respuesta |
|----------|-----------|
| ¿Qué es el proyecto? | Juego de mesa digital tipo "Serpientes y Escaleras" con pingüinos |
| ¿Cuántos jugadores? | 1 a 4 jugadores humanos + 1 foca CPU (opcional) |
| ¿Cómo se guardan los datos? | Base de datos Oracle con 5 tablas (usuarios, partidas, pingüinos, inventarios, eventos) |
| ¿Cómo se hace la interfaz? | JavaFX con archivos FXML para el diseño y clases Java para la lógica |
| ¿Dónde está la lógica del juego? | En el paquete GESTORES, especialmente GestorPartida |
| ¿Cómo se generan los tableros? | Aleatoriamente con probabilidades por tipo de casilla |
| ¿Cómo se gana? | Llegando a la última casilla del tablero |
| ¿Qué hace la foca? | Entorpece a los jugadores quitándoles ítems o enviándoles atrás |
| ¿Qué son los ítems? | Herramientas del inventario: dados especiales, bolas de nieve, peces |

---

*Documentación generada el 13 de mayo de 2026 para el proyecto "El Joc del Pingu".*
*Creadores: Carlos Oros Bendezú, Marcelo Renteria Su, Denis Tineo Dias.*