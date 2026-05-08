-- ============================================================
-- CREABDPINGU.sql
-- Script de creació de la base de dades del joc Pinguins
-- ============================================================


-- ------------------------------------------------------------
-- TAULES
-- ------------------------------------------------------------

CREATE TABLE PINGU_USERS (
  USERNAME          VARCHAR2(50)  PRIMARY KEY,
  PASSWORD          VARCHAR2(100) NOT NULL,
  PARTIDAS_GANADAS  NUMBER        DEFAULT 0,
  PARTIDAS_JUGADAS  NUMBER        DEFAULT 0,
  FECHA_REGISTRO    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PINGU_PARTIDAS (
  ID               NUMBER        PRIMARY KEY,
  USERNAME         VARCHAR2(50)  NOT NULL,
  NOM_PARTIDA      VARCHAR2(100) DEFAULT 'Partida',
  NUM_CASILLAS     NUMBER        NOT NULL,
  CASILLAS_TIPOS   VARCHAR2(4000),
  FOCA_ACTIVADA    NUMBER(1)     DEFAULT 1,
  FOCA_POS         NUMBER        DEFAULT 0,
  FOCA_SOBORNO     NUMBER(1)     DEFAULT 0,
  FOCA_TURNOS_BLOQ NUMBER        DEFAULT 0,
  TURNOS           NUMBER        DEFAULT 0,
  JUGADOR_ACTUAL   NUMBER        DEFAULT 0,
  ACABADA          NUMBER(1)     DEFAULT 0,
  GANADOR          VARCHAR2(50),
  FECHA_GUARDADO   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PINGU_PINGUINOS (
  ID         NUMBER        PRIMARY KEY,
  PARTIDA_ID NUMBER        NOT NULL,
  INDEX_JUG  NUMBER        NOT NULL,
  NOM        VARCHAR2(100) NOT NULL,
  POSICIO    NUMBER        DEFAULT 0
);

CREATE TABLE PINGU_INVENTARIS (
  ID          NUMBER       PRIMARY KEY,
  PINGUINO_ID NUMBER       NOT NULL,
  NOM_ITEM    VARCHAR2(50) NOT NULL,
  QUANTITAT   NUMBER       DEFAULT 0
);

CREATE TABLE PINGU_EVENTS (
  ID         NUMBER        PRIMARY KEY,
  PARTIDA_ID NUMBER        NOT NULL,
  ORDRE      NUMBER        NOT NULL,
  TEXT       VARCHAR2(500) NOT NULL
);


-- ------------------------------------------------------------
-- FOREIGN KEYS
-- ------------------------------------------------------------

-- Las partidas pertenecen a un usuario registrado.
-- Si se borra el usuario, se borran en cascada todas sus partidas
-- (y por cascada encadenada: pingüinos, inventarios y eventos).
ALTER TABLE PINGU_PARTIDAS
  ADD CONSTRAINT FK_PARTIDAS_USER
  FOREIGN KEY (USERNAME)
  REFERENCES PINGU_USERS(USERNAME)
  ON DELETE CASCADE;

-- Los pingüinos pertenecen a una partida.
-- Cascada hacia PINGU_INVENTARIS a través de la FK siguiente.
ALTER TABLE PINGU_PINGUINOS
  ADD CONSTRAINT FK_PINGUINOS_PARTIDA
  FOREIGN KEY (PARTIDA_ID)
  REFERENCES PINGU_PARTIDAS(ID)
  ON DELETE CASCADE;

-- Los ítems del inventario pertenecen a un pingüino.
ALTER TABLE PINGU_INVENTARIS
  ADD CONSTRAINT FK_INVENTARIS_PINGUINO
  FOREIGN KEY (PINGUINO_ID)
  REFERENCES PINGU_PINGUINOS(ID)
  ON DELETE CASCADE;

-- Los eventos pertenecen a una partida.
ALTER TABLE PINGU_EVENTS
  ADD CONSTRAINT FK_EVENTS_PARTIDA
  FOREIGN KEY (PARTIDA_ID)
  REFERENCES PINGU_PARTIDAS(ID)
  ON DELETE CASCADE;
