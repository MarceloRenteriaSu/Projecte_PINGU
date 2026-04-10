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

			// Check if ID column exists (new schema)
			PreparedStatement psId = con.prepareStatement(
				"SELECT COUNT(*) FROM USER_TAB_COLUMNS " +
				"WHERE TABLE_NAME = 'PINGU_PARTIDAS' AND COLUMN_NAME = 'ID'"
			);
			ResultSet rsId = psId.executeQuery();
			boolean hasId = rsId.next() && rsId.getInt(1) > 0;
			rsId.close();
			psId.close();

			if (!hasId) {
				// Old schema detected — drop and recreate
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
	 * Crea la taula de partides guardades (esquema multi-partida) si no existeix.
	 * Suporta múltiples partides per usuari gràcies a la clau primària ID.
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
				"  NUM_JUGADORES NUMBER NOT NULL," +
				"  NOMBRES_JUGADORES VARCHAR2(500)," +
				"  POSICIONES VARCHAR2(200)," +
				"  INVENTARIOS VARCHAR2(4000)," +
				"  FOCA_ACTIVADA NUMBER(1) DEFAULT 1," +
				"  FOCA_POS NUMBER DEFAULT 0," +
				"  FOCA_SOBORNO NUMBER(1) DEFAULT 0," +
				"  FOCA_TURNOS_BLOQ NUMBER DEFAULT 0," +
				"  TURNOS NUMBER DEFAULT 0," +
				"  JUGADOR_ACTUAL NUMBER DEFAULT 0," +
				"  FECHA_GUARDADO TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
				")"
			);
			System.out.println("Taula PINGU_PARTIDAS creada (esquema multi-partida).");
			st.close();
		} catch (SQLException e) {
			if (!e.getMessage().contains("ORA-00955") && !e.getMessage().contains("already")) {
				System.out.println("Info taula partidas: " + e.getMessage());
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
	 * Guarda una nova partida per a un usuari (permet múltiples partides per usuari).
	 * Utilitza la seqüència PINGU_PARTIDAS_SEQ per generar l'ID únic.
	 *
	 * @param nomPartida Nom descriptiu que l'usuari ha donat a la partida
	 */
	public static boolean guardarPartida(Connection con, String username, String nomPartida,
			int numCasillas, String casillasTipos, int numJugadores,
			String nombresJugadores, String posiciones, String inventarios,
			int focaActivada, int focaPos, int focaSoborno, int focaTurnosBloq,
			int turnos, int jugadorActual) {
		if (con == null) return false;
		try {
			con.setAutoCommit(true);
			PreparedStatement ps = con.prepareStatement(
				"INSERT INTO PINGU_PARTIDAS " +
				"(ID, USERNAME, NOM_PARTIDA, NUM_CASILLAS, CASILLAS_TIPOS, " +
				"NUM_JUGADORES, NOMBRES_JUGADORES, POSICIONES, INVENTARIOS, " +
				"FOCA_ACTIVADA, FOCA_POS, FOCA_SOBORNO, FOCA_TURNOS_BLOQ, " +
				"TURNOS, JUGADOR_ACTUAL, FECHA_GUARDADO) " +
				"VALUES (PINGU_PARTIDAS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)"
			);
			ps.setString(1, username);
			ps.setString(2, (nomPartida != null && !nomPartida.trim().isEmpty()) ? nomPartida.trim() : "Partida");
			ps.setInt(3, numCasillas);
			ps.setString(4, casillasTipos);
			ps.setInt(5, numJugadores);
			ps.setString(6, nombresJugadores);
			ps.setString(7, posiciones);
			ps.setString(8, inventarios);
			ps.setInt(9, focaActivada);
			ps.setInt(10, focaPos);
			ps.setInt(11, focaSoborno);
			ps.setInt(12, focaTurnosBloq);
			ps.setInt(13, turnos);
			ps.setInt(14, jugadorActual);
			int filas = ps.executeUpdate();
			ps.close();
			System.out.println("Partida '" + nomPartida + "' guardada per " + username + " (filas=" + filas + ")");
			return filas > 0;
		} catch (SQLException e) {
			System.out.println("Error guardant partida: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Llista totes les partides guardades d'un usuari, ordenades per data (més recent primer).
	 * Retorna les columnes bàsiques per mostrar a la pantalla de selecció.
	 *
	 * @return ArrayList amb un LinkedHashMap per partida (ID, NOM_PARTIDA, NOMBRES_JUGADORES, TURNOS, FECHA_GUARDADO)
	 */
	public static ArrayList<LinkedHashMap<String, String>> listarPartidas(Connection con, String username) {
		ArrayList<LinkedHashMap<String, String>> lista = new ArrayList<>();
		if (con == null) return lista;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT ID, NOM_PARTIDA, NOMBRES_JUGADORES, TURNOS, FECHA_GUARDADO " +
				"FROM PINGU_PARTIDAS WHERE USERNAME = ? ORDER BY FECHA_GUARDADO DESC"
			);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();
				fila.put("ID",                rs.getString("ID"));
				fila.put("NOM_PARTIDA",       rs.getString("NOM_PARTIDA"));
				fila.put("NOMBRES_JUGADORS",  rs.getString("NOMBRES_JUGADORES"));
				fila.put("TURNOS",            rs.getString("TURNOS"));
				fila.put("FECHA_GUARDADO",    rs.getString("FECHA_GUARDADO"));
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
	 * Carrega totes les dades d'una partida concreta pel seu ID.
	 *
	 * @param id ID de la partida (columna ID de PINGU_PARTIDAS)
	 * @return Map amb totes les columnes de la partida, o null si no existeix
	 */
	public static LinkedHashMap<String, String> cargarPartidaPorId(Connection con, int id) {
		if (con == null) return null;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT * FROM PINGU_PARTIDAS WHERE ID = ?"
			);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ResultSetMetaData meta = rs.getMetaData();
				int cols = meta.getColumnCount();
				LinkedHashMap<String, String> datos = new LinkedHashMap<>();
				for (int i = 1; i <= cols; i++) {
					datos.put(meta.getColumnLabel(i), rs.getString(i));
				}
				rs.close();
				ps.close();
				return datos;
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("Error carregant partida per ID: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Esborra una partida concreta pel seu ID.
	 *
	 * @param id ID de la partida a esborrar
	 */
	public static boolean borrarPartidaPorId(Connection con, int id) {
		if (con == null) return false;
		try {
			PreparedStatement ps = con.prepareStatement(
				"DELETE FROM PINGU_PARTIDAS WHERE ID = ?"
			);
			ps.setInt(1, id);
			int filas = ps.executeUpdate();
			ps.close();
			System.out.println("Partida ID=" + id + " esborrada (filas=" + filas + ")");
			return filas > 0;
		} catch (SQLException e) {
			System.out.println("Error esborrant partida per ID: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Esborra totes les partides guardades d'un usuari.
	 */
	public static boolean borrarPartida(Connection con, String username) {
		if (con == null) return false;
		try {
			PreparedStatement ps = con.prepareStatement(
				"DELETE FROM PINGU_PARTIDAS WHERE USERNAME = ?"
			);
			ps.setString(1, username);
			int filas = ps.executeUpdate();
			ps.close();
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
