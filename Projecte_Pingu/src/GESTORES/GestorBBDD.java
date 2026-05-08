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
	 * Guarda els esdeveniments d'una partida a la taula PINGU_EVENTS.
	 */
	public static void guardarEvents(Connection con, int partidaId, ArrayList<String> events) {
		if (con != null && events != null && !events.isEmpty()) {
			try {
				PreparedStatement ps = con.prepareStatement(
					"INSERT INTO PINGU_EVENTS (ID, PARTIDA_ID, ORDRE, TEXT) " +
					"VALUES (PINGU_EVENTS_SEQ.NEXTVAL, ?, ?, ?)"
				);
				for (int i = 0; i < events.size(); i++) {
					String text = events.get(i);
					if (text == null || text.isEmpty()) {
						text = "- -";
					}
					ps.setInt(1, partidaId);
					ps.setInt(2, i);
					String truncated = text.length() > 498 ? text.substring(0, 498) : text;
					ps.setString(3, truncated);
					ps.addBatch();
				}
				ps.executeBatch();
				ps.close();
			} catch (SQLException e) {
				System.out.println("Error guardant events: " + e.getMessage());
			}
		}
	}

	/**
	 * Carrega els esdeveniments d'una partida des de la taula PINGU_EVENTS.
	 */
	public static ArrayList<String> carregarEvents(Connection con, int partidaId) {
		ArrayList<String> events = new ArrayList<>();
		if (con == null) return events;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT TEXT FROM PINGU_EVENTS WHERE PARTIDA_ID = ? ORDER BY ORDRE"
			);
			ps.setInt(1, partidaId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				events.add(rs.getString("TEXT"));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("Error carregant events: " + e.getMessage());
		}
		return events;
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
	 * Incrementa el comptador de partides jugades d'un usuari.
	 */
	public static void incrementarPartidasJugadas(Connection con, String username) {
		if (con != null && username != null) {
			try {
				PreparedStatement ps = con.prepareStatement(
					"UPDATE PINGU_USERS SET PARTIDAS_JUGADAS = PARTIDAS_JUGADAS + 1 WHERE USERNAME = ?"
				);
				ps.setString(1, username);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				System.out.println("Error incrementant partides jugades: " + e.getMessage());
			}
		}
	}

	/**
	 * Incrementa el comptador de partides guanyades d'un usuari.
	 */
	public static void incrementarPartidasGanadas(Connection con, String username) {
		if (con != null && username != null) {
			try {
				PreparedStatement ps = con.prepareStatement(
					"UPDATE PINGU_USERS SET PARTIDAS_GANADAS = PARTIDAS_GANADAS + 1 WHERE USERNAME = ?"
				);
				ps.setString(1, username);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				System.out.println("Error incrementant partides guanyades: " + e.getMessage());
			}
		}
	}

	/**
	 * Marca una partida com a finalitzada (ACABADA = 1) i guarda el guanyador.
	 * El trigger TRG_INCR_GANADAS s'encarregarà d'incrementar PARTIDAS_GANADAS.
	 */
	public static void marcarPartidaAcabadaConGanador(Connection con, int id, String ganador) {
		if (con != null) {
			try {
				PreparedStatement ps = con.prepareStatement(
					"UPDATE PINGU_PARTIDAS SET ACABADA = 1, GANADOR = ? WHERE ID = ?"
				);
				ps.setString(1, ganador);
				ps.setInt(2, id);
				ps.executeUpdate();
				ps.close();
				System.out.println("Partida ID=" + id + " marcada acabada, guanyador=" + ganador);
			} catch (SQLException e) {
				System.out.println("Error marcant partida acabada: " + e.getMessage());
			}
		}
	}

	/**
	 * Marca una partida com a finalitzada (ACABADA = 1) sense esborrar-la.
	 */
	public static void marcarPartidaAcabada(Connection con, int id) {
		if (con != null) {
			try {
				PreparedStatement ps = con.prepareStatement(
					"UPDATE PINGU_PARTIDAS SET ACABADA = 1 WHERE ID = ?"
				);
				ps.setInt(1, id);
				ps.executeUpdate();
				ps.close();
				System.out.println("Partida ID=" + id + " marcada com acabada.");
			} catch (SQLException e) {
				System.out.println("Error marcant partida com acabada: " + e.getMessage());
			}
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
	public static int guardarPartida(Connection con, String username, String nomPartida,
			int numCasillas, String casillasTipos,
			int focaActivada, int focaPos, int focaSoborno, int focaTurnosBloq,
			int turnos, int jugadorActual,
			String[] nombresPinguinos, int[] posicionesPinguinos,
			String[][] inventariosPinguinos) {
		if (con == null) return -1;
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
			return partidaId;
		} catch (SQLException e) {
			try { con.rollback(); con.setAutoCommit(true); } catch (SQLException ex) { /* ignored */ }
			System.out.println("Error guardant partida: " + e.getMessage());
			e.printStackTrace();
			return -1;
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
				"WHERE p.USERNAME = ? AND (p.ACABADA = 0 OR p.ACABADA IS NULL) " +
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
			// Eliminar events d'aquesta partida
			PreparedStatement ps0 = con.prepareStatement(
				"DELETE FROM PINGU_EVENTS WHERE PARTIDA_ID = ?"
			);
			ps0.setInt(1, id);
			ps0.executeUpdate();
			ps0.close();

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
			// Eliminar events de totes les partides de l'usuari
			PreparedStatement psE = con.prepareStatement(
				"DELETE FROM PINGU_EVENTS WHERE PARTIDA_ID IN " +
				"(SELECT ID FROM PINGU_PARTIDAS WHERE USERNAME = ?)"
			);
			psE.setString(1, username);
			psE.executeUpdate();
			psE.close();

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
	 * Retorna la llista de noms d'usuari registrats, excloent l'usuari indicat.
	 */
	public static ArrayList<String> getUsuarios(Connection con, String exclude) {
		ArrayList<String> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT USERNAME FROM PINGU_USERS " +
				"WHERE UPPER(USERNAME) != UPPER(?) ORDER BY USERNAME"
			);
			ps.setString(1, exclude == null ? "" : exclude);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) lista.add(rs.getString("USERNAME"));
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("Error obtenint usuaris: " + e.getMessage());
		}
		return lista;
	}

	/**
	 * Crida F_PINGU_RECORD: retorna el màxim de partides guanyades (rècord).
	 */
	public static int getRecord(Connection con) {
		if (con == null) return 0;
		try {
			CallableStatement cs = con.prepareCall("BEGIN ? := F_PINGU_RECORD(); END;");
			cs.registerOutParameter(1, java.sql.Types.NUMERIC);
			cs.execute();
			int val = cs.getInt(1);
			cs.close();
			return val;
		} catch (SQLException e) {
			System.out.println("Error cridant F_PINGU_RECORD: " + e.getMessage());
			try {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT NVL(MAX(PARTIDAS_GANADAS),0) FROM PINGU_USERS");
				int val = rs.next() ? rs.getInt(1) : 0;
				rs.close(); st.close();
				return val;
			} catch (SQLException e2) { return 0; }
		}
	}

	/**
	 * Crida F_PINGU_MITJA: retorna la mitja de partides guanyades.
	 */
	public static double getMitja(Connection con) {
		if (con == null) return 0;
		try {
			CallableStatement cs = con.prepareCall("BEGIN ? := F_PINGU_MITJA(); END;");
			cs.registerOutParameter(1, java.sql.Types.NUMERIC);
			cs.execute();
			double val = cs.getDouble(1);
			cs.close();
			return val;
		} catch (SQLException e) {
			System.out.println("Error cridant F_PINGU_MITJA: " + e.getMessage());
			try {
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT ROUND(NVL(AVG(PARTIDAS_GANADAS),0),2) FROM PINGU_USERS");
				double val = rs.next() ? rs.getDouble(1) : 0;
				rs.close(); st.close();
				return val;
			} catch (SQLException e2) { return 0; }
		}
	}

	/**
	 * Crida F_PINGU_PCT_MENYS: % de jugadors que han guanyat menys de p_wins partides.
	 */
	public static double getPctMenysGuanyades(Connection con, int wins) {
		if (con == null) return 0;
		try {
			CallableStatement cs = con.prepareCall("BEGIN ? := F_PINGU_PCT_MENYS(?); END;");
			cs.registerOutParameter(1, java.sql.Types.NUMERIC);
			cs.setInt(2, wins);
			cs.execute();
			double val = cs.getDouble(1);
			cs.close();
			return val;
		} catch (SQLException e) {
			System.out.println("Error cridant F_PINGU_PCT_MENYS: " + e.getMessage());
			try {
				PreparedStatement ps = con.prepareStatement(
					"SELECT ROUND(COUNT(*)*100.0/(SELECT COUNT(*) FROM PINGU_USERS),1) " +
					"FROM PINGU_USERS WHERE PARTIDAS_GANADAS < ?"
				);
				ps.setInt(1, wins);
				ResultSet rs = ps.executeQuery();
				double val = rs.next() ? rs.getDouble(1) : 0;
				rs.close(); ps.close();
				return val;
			} catch (SQLException e2) { return 0; }
		}
	}

	/**
	 * Retorna els jugadors que tenen el rècord de partides guanyades.
	 */
	public static ArrayList<String> getJugadorsRecord(Connection con) {
		ArrayList<String> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			int record = getRecord(con);
			PreparedStatement ps = con.prepareStatement(
				"SELECT USERNAME FROM PINGU_USERS WHERE PARTIDAS_GANADAS = ? ORDER BY USERNAME"
			);
			ps.setInt(1, record);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) lista.add(rs.getString("USERNAME"));
			rs.close(); ps.close();
		} catch (SQLException e) {
			System.out.println("Error obtenint jugadors rècord: " + e.getMessage());
		}
		return lista;
	}

	/**
	 * Retorna els jugadors per sobre de la mitja de partides guanyades.
	 */
	public static ArrayList<LinkedHashMap<String, String>> getJugadorsSobreMitja(Connection con) {
		ArrayList<LinkedHashMap<String, String>> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(
				"SELECT USERNAME, PARTIDAS_GANADAS FROM PINGU_USERS " +
				"WHERE PARTIDAS_GANADAS > (SELECT AVG(PARTIDAS_GANADAS) FROM PINGU_USERS) " +
				"ORDER BY PARTIDAS_GANADAS DESC"
			);
			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();
				fila.put("USERNAME", rs.getString("USERNAME"));
				fila.put("PARTIDAS_GANADAS", rs.getString("PARTIDAS_GANADAS"));
				lista.add(fila);
			}
			rs.close(); st.close();
		} catch (SQLException e) {
			System.out.println("Error obtenint jugadors sobre mitja: " + e.getMessage());
		}
		return lista;
	}

	/**
	 * Retorna les partides guanyades d'un usuari concret.
	 */
	public static int getPartidasGanadasUsuario(Connection con, String username) {
		if (con == null) return 0;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT PARTIDAS_GANADAS FROM PINGU_USERS WHERE USERNAME = ?"
			);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			int val = rs.next() ? rs.getInt(1) : 0;
			rs.close(); ps.close();
			return val;
		} catch (SQLException e) {
			return 0;
		}
	}

	/**
	 * Rànquing de jugadors ordenat per total de partides jugades (de més a menys).
	 * Implementa la lògica del procediment P_PINGU_RANKING per a l'ús des de JavaFX.
	 */
	public static ArrayList<LinkedHashMap<String, String>> getRankingPerJugades(Connection con) {
		ArrayList<LinkedHashMap<String, String>> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(
				"SELECT USERNAME, PARTIDAS_GANADAS, PARTIDAS_JUGADAS, " +
				"  CASE WHEN PARTIDAS_JUGADAS > 0 " +
				"       THEN ROUND(PARTIDAS_GANADAS * 100.0 / PARTIDAS_JUGADAS, 1) " +
				"       ELSE 0 END AS RATIO " +
				"FROM PINGU_USERS " +
				"WHERE PARTIDAS_JUGADAS > 0 " +
				"ORDER BY PARTIDAS_JUGADAS DESC, PARTIDAS_GANADAS DESC " +
				"FETCH FIRST 20 ROWS ONLY"
			);
			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();
				fila.put("USERNAME",         rs.getString("USERNAME"));
				fila.put("PARTIDAS_GANADAS", rs.getString("PARTIDAS_GANADAS"));
				fila.put("PARTIDAS_JUGADAS", rs.getString("PARTIDAS_JUGADAS"));
				fila.put("RATIO",            rs.getString("RATIO"));
				lista.add(fila);
			}
			rs.close(); st.close();
		} catch (SQLException e) {
			System.out.println("Error obtenint ranking per jugades: " + e.getMessage());
		}
		return lista;
	}

	/**
	 * Retorna el rànquing global d'usuaris ordenat per partides guanyades.
	 * Cada fila conté USERNAME, PARTIDAS_GANADAS, PARTIDAS_JUGADAS i RATIO (%).
	 */
	public static ArrayList<LinkedHashMap<String, String>> getRanking(Connection con) {
		ArrayList<LinkedHashMap<String, String>> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(
				"SELECT USERNAME, PARTIDAS_GANADAS, PARTIDAS_JUGADAS, " +
				"  CASE WHEN PARTIDAS_JUGADAS > 0 " +
				"       THEN ROUND(PARTIDAS_GANADAS * 100.0 / PARTIDAS_JUGADAS, 1) " +
				"       ELSE 0 END AS RATIO " +
				"FROM PINGU_USERS " +
				"ORDER BY PARTIDAS_GANADAS DESC, RATIO DESC " +
				"FETCH FIRST 20 ROWS ONLY"
			);
			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();
				fila.put("USERNAME",         rs.getString("USERNAME"));
				fila.put("PARTIDAS_GANADAS", rs.getString("PARTIDAS_GANADAS"));
				fila.put("PARTIDAS_JUGADAS", rs.getString("PARTIDAS_JUGADAS"));
				fila.put("RATIO",            rs.getString("RATIO"));
				lista.add(fila);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.out.println("Error obtenint ranking: " + e.getMessage());
		}
		return lista;
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
		} else {
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
