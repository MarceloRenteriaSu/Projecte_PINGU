-- ============================================================
-- CONSULTASPINGU.sql
-- Consultes de prova de la base de dades del joc Pinguins
-- ============================================================


-- ------------------------------------------------------------
-- 1. PINGU_USERS
-- ------------------------------------------------------------

-- Tots els usuaris registrats
SELECT USERNAME, PARTIDAS_GANADAS, PARTIDAS_JUGADAS, FECHA_REGISTRO
FROM PINGU_USERS
ORDER BY USERNAME;

-- Rànquing per partides guanyades
SELECT USERNAME, PARTIDAS_GANADAS, PARTIDAS_JUGADAS,
       CASE WHEN PARTIDAS_JUGADAS > 0
            THEN ROUND(PARTIDAS_GANADAS * 100.0 / PARTIDAS_JUGADAS, 1)
            ELSE 0 END AS PCT_VICTORIA
FROM PINGU_USERS
ORDER BY PARTIDAS_GANADAS DESC, PCT_VICTORIA DESC;

-- Rànquing per partides jugades (usada a la pantalla Ranking)
SELECT USERNAME, PARTIDAS_GANADAS, PARTIDAS_JUGADAS,
       CASE WHEN PARTIDAS_JUGADAS > 0
            THEN ROUND(PARTIDAS_GANADAS * 100.0 / PARTIDAS_JUGADAS, 1)
            ELSE 0 END AS PCT_VICTORIA
FROM PINGU_USERS
WHERE PARTIDAS_JUGADAS > 0
ORDER BY PARTIDAS_JUGADAS DESC, PARTIDAS_GANADAS DESC;

-- Jugadors per sobre de la mitja de victòries
SELECT USERNAME, PARTIDAS_GANADAS
FROM PINGU_USERS
WHERE PARTIDAS_GANADAS > (SELECT AVG(PARTIDAS_GANADAS) FROM PINGU_USERS)
ORDER BY PARTIDAS_GANADAS DESC;

-- Jugadors amb el rècord de victòries
SELECT USERNAME, PARTIDAS_GANADAS
FROM PINGU_USERS
WHERE PARTIDAS_GANADAS = (SELECT MAX(PARTIDAS_GANADAS) FROM PINGU_USERS)
ORDER BY USERNAME;

-- Estadístiques globals d'usuaris
SELECT COUNT(*)                                    AS TOTAL_USUARIS,
       NVL(MAX(PARTIDAS_GANADAS), 0)               AS RECORD,
       ROUND(NVL(AVG(PARTIDAS_GANADAS), 0), 2)     AS MITJA_GUANYADES,
       ROUND(NVL(AVG(PARTIDAS_JUGADAS),  0), 2)    AS MITJA_JUGADES
FROM PINGU_USERS;


-- ------------------------------------------------------------
-- 2. PINGU_PARTIDAS
-- ------------------------------------------------------------

-- Totes les partides
SELECT ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS,
       FOCA_ACTIVADA, TURNOS, ACABADA, GANADOR, FECHA_GUARDADO
FROM PINGU_PARTIDAS
ORDER BY FECHA_GUARDADO DESC;

-- Partides acabades amb guanyador
SELECT ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, TURNOS, GANADOR
FROM PINGU_PARTIDAS
WHERE ACABADA = 1
ORDER BY FECHA_GUARDADO DESC;

-- Partides en curs (no acabades)
SELECT ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, TURNOS, JUGADOR_ACTUAL
FROM PINGU_PARTIDAS
WHERE ACABADA = 0 OR ACABADA IS NULL
ORDER BY FECHA_GUARDADO DESC;

-- Partides per usuari amb nombre de jugadors
SELECT p.ID, p.USERNAME, p.NOM_PARTIDA, p.ACABADA, p.GANADOR,
       COUNT(pj.ID) AS NUM_PINGUINOS
FROM PINGU_PARTIDAS p
JOIN PINGU_PINGUINOS pj ON pj.PARTIDA_ID = p.ID
GROUP BY p.ID, p.USERNAME, p.NOM_PARTIDA, p.ACABADA, p.GANADOR
ORDER BY p.ID;


-- ------------------------------------------------------------
-- 3. PINGU_PINGUINOS
-- ------------------------------------------------------------

-- Tots els pinguins de totes les partides
SELECT pj.ID, pj.PARTIDA_ID, p.NOM_PARTIDA, pj.INDEX_JUG, pj.NOM, pj.POSICIO
FROM PINGU_PINGUINOS pj
JOIN PINGU_PARTIDAS p ON p.ID = pj.PARTIDA_ID
ORDER BY pj.PARTIDA_ID, pj.INDEX_JUG;

-- Pinguins d'una partida concreta (ID = 3)
SELECT pj.INDEX_JUG, pj.NOM, pj.POSICIO
FROM PINGU_PINGUINOS pj
WHERE pj.PARTIDA_ID = 3
ORDER BY pj.INDEX_JUG;


-- ------------------------------------------------------------
-- 4. PINGU_INVENTARIS
-- ------------------------------------------------------------

-- Tot l'inventari amb el nom del pingüí i la partida
SELECT i.ID, pj.NOM AS PINGUINO, p.NOM_PARTIDA, i.NOM_ITEM, i.QUANTITAT
FROM PINGU_INVENTARIS i
JOIN PINGU_PINGUINOS pj ON pj.ID = i.PINGUINO_ID
JOIN PINGU_PARTIDAS  p  ON p.ID  = pj.PARTIDA_ID
ORDER BY pj.PARTIDA_ID, pj.INDEX_JUG, i.NOM_ITEM;

-- Inventari d'un pingüí concret (Pinguino ID = 7)
SELECT NOM_ITEM, QUANTITAT
FROM PINGU_INVENTARIS
WHERE PINGUINO_ID = 7
ORDER BY NOM_ITEM;

-- Resum d'ítems per partida en curs
SELECT pj.NOM AS PINGUINO, i.NOM_ITEM, i.QUANTITAT
FROM PINGU_INVENTARIS i
JOIN PINGU_PINGUINOS pj ON pj.ID = i.PINGUINO_ID
WHERE pj.PARTIDA_ID = 3
ORDER BY pj.INDEX_JUG, i.NOM_ITEM;


-- ------------------------------------------------------------
-- 5. PINGU_EVENTS
-- ------------------------------------------------------------

-- Tots els events ordenats per partida i ordre
SELECT e.ID, e.PARTIDA_ID, p.NOM_PARTIDA, e.ORDRE, e.TEXT
FROM PINGU_EVENTS e
JOIN PINGU_PARTIDAS p ON p.ID = e.PARTIDA_ID
ORDER BY e.PARTIDA_ID, e.ORDRE;

-- Events d'una partida concreta (ID = 1)
SELECT ORDRE, TEXT
FROM PINGU_EVENTS
WHERE PARTIDA_ID = 1
ORDER BY ORDRE;

-- Nombre d'events per partida
SELECT e.PARTIDA_ID, p.NOM_PARTIDA, COUNT(*) AS NUM_EVENTS
FROM PINGU_EVENTS e
JOIN PINGU_PARTIDAS p ON p.ID = e.PARTIDA_ID
GROUP BY e.PARTIDA_ID, p.NOM_PARTIDA
ORDER BY e.PARTIDA_ID;


-- ------------------------------------------------------------
-- 6. FUNCIONS
-- ------------------------------------------------------------

-- F_PINGU_RECORD: màxim de partides guanyades
SELECT F_PINGU_RECORD() AS RECORD FROM DUAL;

-- F_PINGU_MITJA: mitja de partides guanyades
SELECT F_PINGU_MITJA() AS MITJA FROM DUAL;

-- F_PINGU_PCT_MENYS: % de jugadors que han guanyat menys que Denis (5 victòries)
SELECT F_PINGU_PCT_MENYS(5) AS PCT_MENYS_QUE_DENIS FROM DUAL;


-- ------------------------------------------------------------
-- 7. PROCEDIMENTS (requereixen un bloc PL/SQL per llegir el cursor)
-- ------------------------------------------------------------

-- P_PINGU_JUGADORS_RECORD: jugadors amb el rècord actual
DECLARE
  v_cursor SYS_REFCURSOR;
  v_user   VARCHAR2(50);
  v_wins   NUMBER;
BEGIN
  P_PINGU_JUGADORS_RECORD(F_PINGU_RECORD(), v_cursor);
  LOOP
    FETCH v_cursor INTO v_user, v_wins;
    EXIT WHEN v_cursor%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('Record: ' || v_user || ' — ' || v_wins || ' victòries');
  END LOOP;
  CLOSE v_cursor;
END;
/

-- P_PINGU_SOBRE_MITJA: jugadors per sobre de la mitja
DECLARE
  v_cursor SYS_REFCURSOR;
  v_user   VARCHAR2(50);
  v_wins   NUMBER;
BEGIN
  P_PINGU_SOBRE_MITJA(v_cursor);
  LOOP
    FETCH v_cursor INTO v_user, v_wins;
    EXIT WHEN v_cursor%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('Sobre mitja: ' || v_user || ' — ' || v_wins || ' victòries');
  END LOOP;
  CLOSE v_cursor;
END;
/

-- P_PINGU_RANKING: rànquing complet (requereix que el jugador tingui partides guardades)
DECLARE
  v_cursor SYS_REFCURSOR;
  v_user   VARCHAR2(50);
  v_wins   NUMBER;
  v_played NUMBER;
  v_ratio  NUMBER;
BEGIN
  P_PINGU_RANKING('Denis', v_cursor);
  LOOP
    FETCH v_cursor INTO v_user, v_wins, v_played, v_ratio;
    EXIT WHEN v_cursor%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE(v_user || ' | Guanyades: ' || v_wins ||
                         ' | Jugades: ' || v_played ||
                         ' | Ratio: ' || v_ratio || '%');
  END LOOP;
  CLOSE v_cursor;
END;
/
