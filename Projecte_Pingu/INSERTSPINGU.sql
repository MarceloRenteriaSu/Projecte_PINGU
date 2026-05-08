-- ============================================================
-- INSERTSPINGU.sql
-- Sentències d'inserció de registres de prova per al joc Pinguins
-- ============================================================


-- ------------------------------------------------------------
-- PINGU_USERS
-- ------------------------------------------------------------

INSERT INTO PINGU_USERS (USERNAME, PASSWORD, PARTIDAS_GANADAS, PARTIDAS_JUGADAS)
  VALUES ('Denis', '1234', 5, 10);

INSERT INTO PINGU_USERS (USERNAME, PASSWORD, PARTIDAS_GANADAS, PARTIDAS_JUGADAS)
  VALUES ('Marcelo', '1234', 3, 8);

INSERT INTO PINGU_USERS (USERNAME, PASSWORD, PARTIDAS_GANADAS, PARTIDAS_JUGADAS)
  VALUES ('Carlos', '1234', 7, 15);

INSERT INTO PINGU_USERS (USERNAME, PASSWORD, PARTIDAS_GANADAS, PARTIDAS_JUGADAS)
  VALUES ('TestUser', '1234', 0, 2);


-- ------------------------------------------------------------
-- PINGU_PARTIDAS
-- Partida 1 — acabada, guanyador Carlos
-- Partida 2 — acabada, guanyador Denis
-- Partida 3 — en curs (ACABADA = 0)
-- ------------------------------------------------------------

INSERT INTO PINGU_PARTIDAS
  (ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, CASILLAS_TIPOS,
   FOCA_ACTIVADA, FOCA_POS, FOCA_SOBORNO, FOCA_TURNOS_BLOQ,
   TURNOS, JUGADOR_ACTUAL, ACABADA, GANADOR)
VALUES
  (1, 'Denis', 'Partida Test 1', 10,
   'Normal,Oso,Normal,Agujero,Normal,Trineo,Normal,Evento,SueloQuebradizo,Normal',
   1, 3, 0, 0, 20, 0, 1, 'Carlos');

INSERT INTO PINGU_PARTIDAS
  (ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, CASILLAS_TIPOS,
   FOCA_ACTIVADA, FOCA_POS, FOCA_SOBORNO, FOCA_TURNOS_BLOQ,
   TURNOS, JUGADOR_ACTUAL, ACABADA, GANADOR)
VALUES
  (2, 'Marcelo', 'Partida Test 2', 10,
   'Normal,Normal,Trineo,Normal,Oso,Normal,Agujero,Normal,Evento,Normal',
   0, 0, 0, 0, 15, 1, 1, 'Denis');

INSERT INTO PINGU_PARTIDAS
  (ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, CASILLAS_TIPOS,
   FOCA_ACTIVADA, FOCA_POS, FOCA_SOBORNO, FOCA_TURNOS_BLOQ,
   TURNOS, JUGADOR_ACTUAL, ACABADA, GANADOR)
VALUES
  (3, 'Denis', 'Partida en curs', 10,
   'Normal,Evento,Normal,SueloQuebradizo,Normal,Oso,Normal,Trineo,Normal,Normal',
   1, 2, 0, 0, 6, 0, 0, NULL);


-- ------------------------------------------------------------
-- PINGU_PINGUINOS
-- Partida 1: Denis, Carlos, Marcelo
-- Partida 2: Marcelo, Denis, TestUser
-- Partida 3: Denis, Marcelo  (partida en curs)
-- ------------------------------------------------------------

-- Partida 1
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (1, 1, 0, 'Denis', 9);
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (2, 1, 1, 'Carlos', 9);
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (3, 1, 2, 'Marcelo', 5);

-- Partida 2
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (4, 2, 0, 'Marcelo', 7);
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (5, 2, 1, 'Denis', 9);
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (6, 2, 2, 'TestUser', 3);

-- Partida 3 (en curs)
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (7, 3, 0, 'Denis', 4);
INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO)
  VALUES (8, 3, 1, 'Marcelo', 6);


-- ------------------------------------------------------------
-- PINGU_INVENTARIS
-- Inventari dels pingüins de la partida en curs (IDs 7 i 8)
-- ------------------------------------------------------------

-- Denis (Pinguino ID=7)
INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT)
  VALUES (1, 7, 'Normal', 1);
INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT)
  VALUES (2, 7, 'Pez', 2);
INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT)
  VALUES (3, 7, 'Bola', 3);

-- Marcelo (Pinguino ID=8)
INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT)
  VALUES (4, 8, 'Normal', 1);
INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT)
  VALUES (5, 8, 'Rapido', 1);
INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT)
  VALUES (6, 8, 'Lento', 1);


-- ------------------------------------------------------------
-- PINGU_EVENTS
-- Partida 1 — events finals
-- Partida 3 — events parcials (partida en curs)
-- ------------------------------------------------------------

-- Partida 1
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (1, 1, 0, '¡El juego ha comenzado!');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (2, 1, 1, 'Denis lanza el dado: 3. Avanza a la casilla 3.');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (3, 1, 2, 'Carlos lanza el dado: 5. Avanza a la casilla 5.');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (4, 1, 3, 'Marcelo cae en una casilla Oso. Pierde el turno.');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (5, 1, 4, 'Carlos lanza el dado: 4. Avanza a la casilla 9.');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (6, 1, 5, '🏆 Carlos ¡ha ganado la partida!');

-- Partida 3 (en curs)
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (7, 3, 0, '¡El juego ha comenzado!');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (8, 3, 1, 'Denis lanza el dado: 4. Avanza a la casilla 4.');
INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT)
  VALUES (9, 3, 2, 'Marcelo lanza el dado: 6. Avanza a la casilla 6.');


-- ------------------------------------------------------------
COMMIT;
-- ------------------------------------------------------------
