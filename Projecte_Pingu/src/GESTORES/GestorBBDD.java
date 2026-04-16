package GESTORES;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase que proporciona métodos para interactuar con una base de datos Oracle.
 */
public class GestorBBDD {

	/**
	 * Intenta establecer una conexión a la base de datos Oracle. NO HACE FALTA QUE
	 * ENTENDÁIS CÓMO FUNCIONA, SE HACE TODO DE MANERA AUTOMÁTICA.
	 *
	 * @param scan Scanner de main con el que vais a leer por consola
	 * @return Objeto Connection si la conexión es exitosa, null en caso contrario.
	 *         LA VARIABLE QUE DEVUELVE LA TENÉIS QUE GUARDAR PARA LAS DEMÁS
	 *         FUNCIONES
	 */
	public static Connection conectarBaseDatos(Scanner scan) {
		System.out.println("Intentando conectarse a la base de datos...");

		// 1) Elegir entorno con validación
		String entorno = "";
		boolean valido = false;
		while (!valido) {
			System.out.println("Selecciona centro o fuera de centro (CENTRO/FUERA):");
			entorno = scan.nextLine().trim().toLowerCase();

			if (entorno.equalsIgnoreCase("centro") || entorno.equalsIgnoreCase("fuera")) {
				valido = true;
			} else {
				System.out.println("Entrada no válida. Escribe CENTRO o FUERA.");
			}
		}

		String url = entorno.equals("centro") ? "jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2"
				: "jdbc:oracle:thin:@//oracle.ilerna.com:1521/XEPDB2";

		System.out.println("DW2526_GR02_PINGU");
		String user = scan.nextLine().trim();

		System.out.println("ACOMRDT");
		String pwd = scan.nextLine();

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, user, pwd);

			if (con.isValid(5)) {
				System.out.println("Conectados a la base de datos.");
			} else {
				System.out.println("Conexión creada, pero no parece válida. Revisa red/URL.");
			}

			return con;

		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado el driver de Oracle. ¿Está el ojdbc en el proyecto?");
		} catch (SQLException e) {
			System.out.println("No se pudo conectar. Revisa URL/usuario/contraseña.");
			System.out.println("Detalle: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Connexió a la BBDD sense Scanner (per a ús des de JavaFX).
	 * Si user/pass estan buits, utilitza credencials per defecte.
	 */
	public static Connection conectarBBDD(String entorno, String user, String pass) {
		String url = "centro".equalsIgnoreCase(entorno)
				? "jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2"
				: "jdbc:oracle:thin:@//oracle.ilerna.com:1521/XEPDB2";

		if (user == null || user.isEmpty()) user = "DW2526_GR02_PINGU";
		if (pass == null || pass.isEmpty()) pass = "ACOMRDT";

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, user, pass);
			if (con.isValid(5)) {
				System.out.println("Connexió BBDD establerta (" + entorno + ").");
				crearTaulaUsuaris(con);
				migrarTaulaPartidas(con);
				crearSequenciaPartidas(con);
				crearTaulaPartidas(con);
				crearTaulaPinguinos(con);
				crearTaulaInventaris(con);
				return con;
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Oracle no trobat.");
		} catch (SQLException e) {
			System.out.println("No s'ha pogut connectar a la BBDD: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Crea la taula d'usuaris si no existeix.
	 */
	private static void crearTaulaUsuaris(Connection con) {
		try {
			Statement st = con.createStatement();
			st.executeUpdate(
				"CREATE TABLE PINGU_USERS (" +
				"  USERNAME VARCHAR2(50) PRIMARY KEY," +
				"  PASSWORD VARCHAR2(100) NOT NULL," +
				"  PARTIDAS_GANADAS NUMBER DEFAULT 0," +
				"  FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
				")"
			);
			System.out.println("Taula PINGU_USERS creada.");
			st.close();
		} catch (SQLException e) {
			if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
				System.out.println("Info taula usuaris: " + e.getMessage());
			}
		}
	}

	/**
	 * Si la taula PINGU_PARTIDAS existeix amb l'esquema antic (sense columna ID),
	 * l'elimina per poder recrear-la amb el nou esquema multi-partida.
	 */
	private static void migrarTaulaPartidas(Connection con) {
		try {
			// Check if PINGU_PARTIDAS table exists
			PreparedStatement psExists = con.prepareStatement(
				"SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = 'PINGU_PARTIDAS'"
			);
			ResultSet rsExists = psExists.executeQuery();
			boolean tableExists = rsExists.next() && rsExists.getInt(1) > 0;
			rsExists.close();
			psExists.close();

			if (!tableExists) return; // Table doesn't exist yet, nothing to migrate

			// Check if the old schema still has the NOMBRES_JUGADORES column (pre-split schema)
			PreparedStatement psOld = con.prepareStatement(
				"SELECT COUNT(*) FROM USER_TAB_COLUMNS " +
				"WHERE TABLE_NAME = 'PINGU_PARTIDAS' AND COLUMN_NAME = 'NOMBRES_JUGADORES'"
			);
			ResultSet rsOld = psOld.executeQuery();
			boolean hasOldColumn = rsOld.next() && rsOld.getInt(1) > 0;
			rsOld.close();
			psOld.close();

			if (hasOldColumn) {
				// Old single-table schema detected — drop child tables first (if they exist), then parent
				Statement st = con.createStatement();
				try { st.executeUpdate("DROP TABLE PINGU_INVENTARIS"); } catch (SQLException ignored) {}
				try { st.executeUpdate("DROP TABLE PINGU_PINGUINOS"); } catch (SQLException ignored) {}
				st.executeUpdate("DROP TABLE PINGU_PARTIDAS");
				st.close();
				System.out.println("Esquema antic eliminat (migrant a esquema de 3 taules).");
				return;
			}

			// Also check if ID column exists (very old single-row schema without ID)
			PreparedStatement psId = con.prepareStatement(
				"SELECT COUNT(*) FROM USER_TAB_COLUMNS " +
				"WHERE TABLE_NAME = 'PINGU_PARTIDAS' AND COLUMN_NAME = 'ID'"
			);
			ResultSet rsId = psId.executeQuery();
			boolean hasId = rsId.next() && rsId.getInt(1) > 0;
			rsId.close();
			psId.close();

			if (!hasId) {
				Statement st = con.createStatement();
				st.executeUpdate("DROP TABLE PINGU_PARTIDAS");
				st.close();
				System.out.println("Taula PINGU_PARTIDAS antiga eliminada (migrant a esquema multi-partida).");
			}
		} catch (SQLException e) {
			System.out.println("Error durant migració de PINGU_PARTIDAS: " + e.getMessage());
		}
	}

	/**
	 * Crea la seqüència Oracle per generar IDs únics de partida.
	 */
	private static void crearSequenciaPartidas(Connection con) {
		try {
			Statement st = con.createStatement();
			st.executeUpdate(
				"CREATE SEQUENCE PINGU_PARTIDAS_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOORDER"
			);
			System.out.println("Seqüència PINGU_PARTIDAS_SEQ creada.");
			st.close();
		} catch (SQLException e) {
			// ORA-00955: name already used → sequence already exists, OK
			if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
				System.out.println("Info seqüència: " + e.getMessage());
			}
		}
	}

	/**
	 * Crea la taula de partides guardades si no existeix.
	 * Només emmagatzema les dades generals de la partida.
	 * Els pingüins es guarden a PINGU_PINGUINOS i els inventaris a PINGU_INVENTARIS.
	 */
	private static void crearTaulaPartidas(Connection con) {
		try {
			Statement st = con.createStatement();
			st.executeUpdate(
				"CREATE TABLE PINGU_PARTIDAS (" +
				"  ID NUMBER PRIMARY KEY," +
				"  USERNAME VARCHAR2(50) NOT NULL," +
				"  NOM_PARTIDA VARCHAR2(100) DEFAULT 'Partida'," +
				"  NUM_CASILLAS NUMBER NOT NULL," +
				"  CASILLAS_TIPOS VARCHAR2(4000)," +
				"  FOCA_ACTIVADA NUMBER(1) DEFAULT 1," +
				"  FOCA_POS NUMBER DEFAULT 0," +
				"  FOCA_SOBORNO NUMBER(1) DEFAULT 0," +
				"  FOCA_TURNOS_BLOQ NUMBER DEFAULT 0," +
				"  TURNOS NUMBER DEFAULT 0," +
				"  JUGADOR_ACTUAL NUMBER DEFAULT 0," +
				"  FECHA_GUARDADO TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
				")"
			);
			System.out.println("Taula PINGU_PARTIDAS creada.");
			st.close();
		} catch (SQLException e) {
			if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
				System.out.println("Info taula partidas: " + e.getMessage());
			}
		}
	}

	/**
	 * Crea la seqüència i la taula de pingüins per partida si no existeixen.
	 * Cada fila és un pingüí d'una partida concreta.
	 */
	private static void crearTaulaPinguinos(Connection con) {
		try {
			Statement st = con.createStatement();
			try {
				st.executeUpdate(
					"CREATE SEQUENCE PINGU_PINGUINOS_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOORDER"
				);
				System.out.println("Seqüència PINGU_PINGUINOS_SEQ creada.");
			} catch (SQLException e) {
				if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
					System.out.println("Info seqüència pinguinos: " + e.getMessage());
				}
			}
			st.executeUpdate(
				"CREATE TABLE PINGU_PINGUINOS (" +
				"  ID NUMBER PRIMARY KEY," +
				"  PARTIDA_ID NUMBER NOT NULL," +
				"  INDEX_JUG NUMBER NOT NULL," +
				"  NOM VARCHAR2(100) NOT NULL," +
				"  POSICIO NUMBER DEFAULT 0" +
				")"
			);
			System.out.println("Taula PINGU_PINGUINOS creada.");
			st.close();
		} catch (SQLException e) {
			if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
				System.out.println("Info taula pinguinos: " + e.getMessage());
			}
		}
	}

	/**
	 * Crea la seqüència i la taula d'inventaris per pingüí si no existeixen.
	 * Cada fila és un tipus d'ítem a l'inventari d'un pingüí.
	 */
	private static void crearTaulaInventaris(Connection con) {
		try {
			Statement st = con.createStatement();
			try {
				st.executeUpdate(
					"CREATE SEQUENCE PINGU_INVENTARIS_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOORDER"
				);
				System.out.println("Seqüència PINGU_INVENTARIS_SEQ creada.");
			} catch (SQLException e) {
				if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
					System.out.println("Info seqüència inventaris: " + e.getMessage());
				}
			}
			st.executeUpdate(
				"CREATE TABLE PINGU_INVENTARIS (" +
				"  ID NUMBER PRIMARY KEY," +
				"  PINGUINO_ID NUMBER NOT NULL," +
				"  NOM_ITEM VARCHAR2(50) NOT NULL," +
				"  QUANTITAT NUMBER DEFAULT 0" +
				")"
			);
			System.out.println("Taula PINGU_INVENTARIS creada.");
			st.close();
		} catch (SQLException e) {
			if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
				System.out.println("Info taula inventaris: " + e.getMessage());
			}
		}
	}


	/**
	 * Comprova si un nom d'usuari ja existeix a la BBDD.
	 * @return true si l'usuari ja existeix
	 */
	public static boolean usuarioExiste(Connection con, String username) {
		if (con == null) return false;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT COUNT(*) FROM PINGU_USERS WHERE USERNAME = ?"
			);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			boolean existe = rs.next() && rs.getInt(1) > 0;
			rs.close();
			ps.close();
			return existe;
		} catch (SQLException e) {
			System.out.println("Error comprovant existència d'usuari: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Verifica les credencials d'un usuari contra la base de dades.
	 * @return true si l'usuari existeix i la contrasenya coincideix
	 */
	public static boolean loginUsuario(Connection con, String username, String password) {
		if (con == null) return false;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT COUNT(*) FROM PINGU_USERS WHERE USERNAME = ? AND PASSWORD = ?"
			);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			boolean valid = rs.next() && rs.getInt(1) > 0;
			rs.close();
			ps.close();
			return valid;
		} catch (SQLException e) {
			System.out.println("Error en login: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Registra un nou usuari a la BBDD.
	 * Retorna 0 si ok, 1 si l'usuari ja existeix, -1 si hi ha un altre error.
	 */
	public static int registrarUsuario(Connection con, String username, String password) {
		if (con == null) return -1;
		if (usuarioExiste(con, username)) return 1;
		try {
			PreparedStatement ps = con.prepareStatement(
				"INSERT INTO PINGU_USERS (USERNAME, PASSWORD) VALUES (?, ?)"
			);
			ps.setString(1, username);
			ps.setString(2, password);
			int filas = ps.executeUpdate();
			ps.close();
			return filas > 0 ? 0 : -1;
		} catch (SQLException e) {
			System.out.println("Error en registre: " + e.getMessage());
			return -1;
		}
	}

	/**
	 * Guarda una nova partida repartida en tres taules:
	 *   PINGU_PARTIDAS  — dades generals de la partida
	 *   PINGU_PINGUINOS — un registre per cada pingüí
	 *   PINGU_INVENTARIS — un registre per cada ítem de cada pingüí
	 *
	 * @param nombresPinguinos   Noms dels pingüins en ordre de torn
	 * @param posicionesPinguinos Posicions al tauler de cada pingüí
	 * @param inventariosPinguinos [i][j] = "NomItem:quantitat" per al pingüí i, ítem j
	 */
	public static boolean guardarPartida(Connection con, String username, String nomPartida,
			int numCasillas, String casillasTipos,
			int focaActivada, int focaPos, int focaSoborno, int focaTurnosBloq,
			int turnos, int jugadorActual,
			String[] nombresPinguinos, int[] posicionesPinguinos,
			String[][] inventariosPinguinos) {
		if (con == null) return false;
		try {
			con.setAutoCommit(false);

			// 1) Insertar la partida principal
			PreparedStatement ps1 = con.prepareStatement(
				"INSERT INTO PINGU_PARTIDAS " +
				"(ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, CASILLAS_TIPOS, " +
				"FOCA_ACTIVADA, FOCA_POS, FOCA_SOBORNO, FOCA_TURNOS_BLOQ, " +
				"TURNOS, JUGADOR_ACTUAL, FECHA_GUARDADO) " +
				"VALUES (PINGU_PARTIDAS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)"
			);
			ps1.setString(1, username);
			ps1.setString(2, (nomPartida != null && !nomPartida.trim().isEmpty()) ? nomPartida.trim() : "Partida");
			ps1.setInt(3, numCasillas);
			ps1.setString(4, casillasTipos);
			ps1.setInt(5, focaActivada);
			ps1.setInt(6, focaPos);
			ps1.setInt(7, focaSoborno);
			ps1.setInt(8, focaTurnosBloq);
			ps1.setInt(9, turnos);
			ps1.setInt(10, jugadorActual);
			ps1.executeUpdate();
			ps1.close();

			// Recuperar l'ID generat per la seqüència
			Statement stId = con.createStatement();
			ResultSet rsId = stId.executeQuery("SELECT PINGU_PARTIDAS_SEQ.CURRVAL FROM DUAL");
			rsId.next();
			int partidaId = rsId.getInt(1);
			rsId.close();
			stId.close();

			// 2) Insertar cada pingüí i els seus ítems
			for (int i = 0; i < nombresPinguinos.length; i++) {
				PreparedStatement ps2 = con.prepareStatement(
					"INSERT INTO PINGU_PINGUINOS (ID, PARTIDA_ID, INDEX_JUG, NOM, POSICIO) " +
					"VALUES (PINGU_PINGUINOS_SEQ.NEXTVAL, ?, ?, ?, ?)"
				);
				ps2.setInt(1, partidaId);
				ps2.setInt(2, i);
				ps2.setString(3, nombresPinguinos[i]);
				ps2.setInt(4, posicionesPinguinos[i]);
				ps2.executeUpdate();
				ps2.close();

				// Recuperar l'ID del pingüí inserit
				Statement stPingId = con.createStatement();
				ResultSet rsPingId = stPingId.executeQuery("SELECT PINGU_PINGUINOS_SEQ.CURRVAL FROM DUAL");
				rsPingId.next();
				int pinguinoId = rsPingId.getInt(1);
				rsPingId.close();
				stPingId.close();

				// 3) Insertar els ítems de l'inventari d'aquest pingüí
				if (inventariosPinguinos != null && i < inventariosPinguinos.length
						&& inventariosPinguinos[i] != null) {
					for (String itemStr : inventariosPinguinos[i]) {
						String[] kv = itemStr.split(":");
						if (kv.length == 2) {
							PreparedStatement ps3 = con.prepareStatement(
								"INSERT INTO PINGU_INVENTARIS (ID, PINGUINO_ID, NOM_ITEM, QUANTITAT) " +
								"VALUES (PINGU_INVENTARIS_SEQ.NEXTVAL, ?, ?, ?)"
							);
							ps3.setInt(1, pinguinoId);
							ps3.setString(2, kv[0].trim());
							ps3.setInt(3, Integer.parseInt(kv[1].trim()));
							ps3.executeUpdate();
							ps3.close();
						}
					}
				}
			}

			con.commit();
			con.setAutoCommit(true);
			System.out.println("Partida '" + nomPartida + "' guardada per " + username + " (3 taules).");
			return true;
		} catch (SQLException e) {
			try { con.rollback(); con.setAutoCommit(true); } catch (SQLException ex) { /* ignored */ }
			System.out.println("Error guardant partida: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Llista totes les partides guardades d'un usuari, ordenades per data (més recent primer).
	 * Els noms dels jugadors s'obtenen de la taula PINGU_PINGUINOS mitjançant LISTAGG.
	 *
	 * @return ArrayList amb un LinkedHashMap per partida (ID, NOM_PARTIDA, NOMBRES_JUGADORS, TURNOS, FECHA_GUARDADO)
	 */
	public static ArrayList<LinkedHashMap<String, String>> listarPartidas(Connection con, String username) {
		ArrayList<LinkedHashMap<String, String>> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT p.ID, p.NOM_PARTIDA, " +
				"  (SELECT LISTAGG(pj.NOM, ',') WITHIN GROUP (ORDER BY pj.INDEX_JUG) " +
				"   FROM PINGU_PINGUINOS pj WHERE pj.PARTIDA_ID = p.ID) AS NOMBRES_JUGADORS, " +
				"  p.TURNOS, p.FECHA_GUARDADO " +
				"FROM PINGU_PARTIDAS p " +
				"WHERE p.USERNAME = ? " +
				"ORDER BY p.FECHA_GUARDADO DESC"
			);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();
				fila.put("ID",             rs.getString("ID"));
				fila.put("NOM_PARTIDA",    rs.getString("NOM_PARTIDA"));
				fila.put("NOMBRES_JUGADORS", rs.getString("NOMBRES_JUGADORS"));
				fila.put("TURNOS",         rs.getString("TURNOS"));
				fila.put("FECHA_GUARDADO", rs.getString("FECHA_GUARDADO"));
				lista.add(fila);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("Error llistant partides: " + e.getMessage());
		}
		return lista;
	}

	/**
	 * Carrega totes les dades d'una partida concreta pel seu ID, llegint les tres taules:
	 * PINGU_PARTIDAS, PINGU_PINGUINOS i PINGU_INVENTARIS.
	 * El mapa resultant inclou les claus NUM_JUGADORES, NOMBRES_JUGADORES, POSICIONES i
	 * INVENTARIOS reconstruïdes per mantenir compatibilitat amb restaurarPartida().
	 *
	 * @param id ID de la partida
	 * @return Map amb totes les dades de la partida, o null si no existeix
	 */
	public static LinkedHashMap<String, String> cargarPartidaPorId(Connection con, int id) {
		if (con == null) return null;
		try {
			// 1) Dades generals de la partida
			PreparedStatement ps = con.prepareStatement(
				"SELECT * FROM PINGU_PARTIDAS WHERE ID = ?"
			);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				rs.close(); ps.close();
				return null;
			}
			LinkedHashMap<String, String> datos = new LinkedHashMap<>();
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				datos.put(meta.getColumnLabel(i), rs.getString(i));
			}
			rs.close();
			ps.close();

			// 2) Pingüins d'aquesta partida, ordenats per índex
			PreparedStatement ps2 = con.prepareStatement(
				"SELECT ID, INDEX_JUG, NOM, POSICIO " +
				"FROM PINGU_PINGUINOS WHERE PARTIDA_ID = ? ORDER BY INDEX_JUG"
			);
			ps2.setInt(1, id);
			ResultSet rs2 = ps2.executeQuery();

			StringBuilder sbNoms = new StringBuilder();
			StringBuilder sbPos  = new StringBuilder();
			StringBuilder sbInv  = new StringBuilder();
			int numJugadores = 0;

			while (rs2.next()) {
				int pinguinoId = rs2.getInt("ID");
				if (numJugadores > 0) {
					sbNoms.append(",");
					sbPos.append(",");
					sbInv.append(";");
				}
				sbNoms.append(rs2.getString("NOM"));
				sbPos.append(rs2.getInt("POSICIO"));
				numJugadores++;

				// 3) Ítems de l'inventari d'aquest pingüí
				PreparedStatement ps3 = con.prepareStatement(
					"SELECT NOM_ITEM, QUANTITAT FROM PINGU_INVENTARIS WHERE PINGUINO_ID = ?"
				);
				ps3.setInt(1, pinguinoId);
				ResultSet rs3 = ps3.executeQuery();
				boolean firstItem = true;
				while (rs3.next()) {
					if (!firstItem) sbInv.append(",");
					sbInv.append(rs3.getString("NOM_ITEM"))
					     .append(":").append(rs3.getInt("QUANTITAT"));
					firstItem = false;
				}
				rs3.close();
				ps3.close();
			}
			rs2.close();
			ps2.close();

			// Afegir camps reconstruïts perquè restaurarPartida() els pugui llegir
			datos.put("NUM_JUGADORES",    String.valueOf(numJugadores));
			datos.put("NOMBRES_JUGADORES", sbNoms.toString());
			datos.put("POSICIONES",        sbPos.toString());
			datos.put("INVENTARIOS",       sbInv.toString());

			return datos;
		} catch (SQLException e) {
			System.out.println("Error carregant partida per ID: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Esborra una partida concreta pel seu ID, eliminant en cascada
	 * els registres de PINGU_INVENTARIS i PINGU_PINGUINOS associats.
	 *
	 * @param id ID de la partida a esborrar
	 */
	public static boolean borrarPartidaPorId(Connection con, int id) {
		if (con == null) return false;
		try {
			// Eliminar inventaris dels pingüins d'aquesta partida
			PreparedStatement ps1 = con.prepareStatement(
				"DELETE FROM PINGU_INVENTARIS WHERE PINGUINO_ID IN " +
				"(SELECT ID FROM PINGU_PINGUINOS WHERE PARTIDA_ID = ?)"
			);
			ps1.setInt(1, id);
			ps1.executeUpdate();
			ps1.close();

			// Eliminar pingüins d'aquesta partida
			PreparedStatement ps2 = con.prepareStatement(
				"DELETE FROM PINGU_PINGUINOS WHERE PARTIDA_ID = ?"
			);
			ps2.setInt(1, id);
			ps2.executeUpdate();
			ps2.close();

			// Eliminar la partida
			PreparedStatement ps3 = con.prepareStatement(
				"DELETE FROM PINGU_PARTIDAS WHERE ID = ?"
			);
			ps3.setInt(1, id);
			int filas = ps3.executeUpdate();
			ps3.close();

			System.out.println("Partida ID=" + id + " esborrada en cascada (filas=" + filas + ")");
			return filas > 0;
		} catch (SQLException e) {
			System.out.println("Error esborrant partida per ID: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Esborra totes les partides guardades d'un usuari, eliminant en cascada
	 * els registres de PINGU_INVENTARIS i PINGU_PINGUINOS associats.
	 */
	public static boolean borrarPartida(Connection con, String username) {
		if (con == null) return false;
		try {
			// Eliminar inventaris dels pingüins de totes les partides de l'usuari
			PreparedStatement ps1 = con.prepareStatement(
				"DELETE FROM PINGU_INVENTARIS WHERE PINGUINO_ID IN " +
				"(SELECT pj.ID FROM PINGU_PINGUINOS pj " +
				" JOIN PINGU_PARTIDAS p ON pj.PARTIDA_ID = p.ID " +
				" WHERE p.USERNAME = ?)"
			);
			ps1.setString(1, username);
			ps1.executeUpdate();
			ps1.close();

			// Eliminar pingüins de totes les partides de l'usuari
			PreparedStatement ps2 = con.prepareStatement(
				"DELETE FROM PINGU_PINGUINOS WHERE PARTIDA_ID IN " +
				"(SELECT ID FROM PINGU_PARTIDAS WHERE USERNAME = ?)"
			);
			ps2.setString(1, username);
			ps2.executeUpdate();
			ps2.close();

			// Eliminar les partides de l'usuari
			PreparedStatement ps3 = con.prepareStatement(
				"DELETE FROM PINGU_PARTIDAS WHERE USERNAME = ?"
			);
			ps3.setString(1, username);
			int filas = ps3.executeUpdate();
			ps3.close();

			return filas > 0;
		} catch (SQLException e) {
			System.out.println("Error esborrant partides de l'usuari: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Cierra la conexión con la BBDD.
	 */
	public static void cerrar(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ignored) {
			}
		}
	}

	/**
	 * Realiza una inserción en la base de datos.
	 */
	public static int insert(Connection con, String sql) {
		return executeInsUpDel(con, sql, "Insert");
	}

	/**
	 * Realiza una actualización en la base de datos.
	 */
	public static int update(Connection con, String sql) {
		return executeInsUpDel(con, sql, "Update");
	}

	/**
	 * Realiza una eliminación en la base de datos.
	 */
	public static int delete(Connection con, String sql) {
		return executeInsUpDel(con, sql, "Delete");
	}

	/**
	 * Realiza una consulta en la base de datos y devuelve los resultados.
	 */
	public static ArrayList<LinkedHashMap<String, String>> select(Connection con, String sql) {
		ArrayList<LinkedHashMap<String, String>> resultados = new ArrayList<>();

		if (con == null) {
			System.out.println("No hay conexión. Llama antes a conectarBaseDatos().");
			return resultados;
		}

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			ResultSetMetaData meta = rs.getMetaData();
			int numColumnas = meta.getColumnCount();

			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();
				for (int i = 1; i <= numColumnas; i++) {
					String columna = meta.getColumnLabel(i);
					String valor = rs.getString(i);
					fila.put(columna, valor);
				}
				resultados.add(fila);
			}
		} catch (SQLException e) {
			System.out.println("Error en SELECT: " + e.getMessage());
		}

		return resultados;
	}

	/**
	 * Imprime los resultados de una consulta SELECT en la base de datos.
	 */
	public static void print(Connection con, String sql, String[] listaElementosSeleccionados) {
		if (con == null) {
			System.out.println("No hay conexión. Llama antes a conectarBaseDatos().");
			return;
		}

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			int fila = 0;
			boolean hayResultados = false;

			while (rs.next()) {
				hayResultados = true;
				fila++;
				System.out.println("---- Fila " + fila + " ----");
				for (String col : listaElementosSeleccionados) {
					System.out.println(col + ": " + rs.getString(col));
				}
			}

			if (!hayResultados) {
				System.out.println("No se ha encontrado nada");
			}
		} catch (SQLException e) {
			System.out.println("Error en SELECT: " + e.getMessage());
		}
	}

	/**
	 * Ejecuta las consultas Insert, Update o Delete.
	 */
	public static int executeInsUpDel(Connection con, String sql, String etiqueta) {
		if (con == null) {
			System.out.println("No hay conexión. Llama antes a conectarBaseDatos().");
			return 0;
		}

		try (Statement st = con.createStatement()) {
			int filas = st.executeUpdate(sql);
			System.out.println(etiqueta + " hecho correctamente. Filas afectadas: " + filas);
			return filas;
		} catch (SQLException e) {
			System.out.println("Ha habido un error en " + etiqueta + ": " + e.getMessage());
			return 0;
		}
	}
}
