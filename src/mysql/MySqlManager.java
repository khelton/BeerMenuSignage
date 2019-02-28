package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;


/**
 * This class is used to connect to a MySql server
 * 
 * @author kehelton
 *
 */
public class MySqlManager {
	
	public static String dbHost = "127.0.0.1";
	public static String dbPort = "3306";
	public static String dbName;
	private static String dbUsername = "pi";
	private static String dbPassword = "pipassword";
	protected static boolean isDriverLoaded = false;

	
	/**
	 * Class Constructor
	 */
	public MySqlManager() {
		//this.dbHost = "127.0.0.1";
		MySqlManager.dbName = "churchill";
	}
	/**
	 * Class Constructor
	 */
	public MySqlManager(String dbHost, String dbName) {
		this();
		MySqlManager.dbHost = dbHost;
		MySqlManager.dbName = dbName;
	}
	
	public MySqlManager(String dbHost, String dbName, String dbUsername, String dbPassword) {
		this();
		MySqlManager.dbHost = dbHost;
		MySqlManager.dbName = dbName;
		MySqlManager.dbUsername = dbUsername;
		MySqlManager.dbPassword = dbPassword;
	}
	
	@SuppressWarnings("deprecation")
	protected void loadDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		isDriverLoaded = true;
	}
	
	public static void setUsername(String username) {
		dbUsername = username;
	}
	
	public static void setPassword(String password) {
		dbPassword = password;
	}
	
	/**
	 * Starts a connection with the MySql server
	 */
	public Connection connect() {
		return connect(dbUsername, dbPassword);
	}
	public Connection connect(String username, String password) {
		Connection c = null;
		try{
			if (!isDriverLoaded) {
				this.loadDriver();
			}
			//c = dataSource.getConnection();
			c = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName +"?" + "user=" + username + "&password=" + password);
		//this.rs = stmt.executeQuery("SELECT ID FROM USERS");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return c;
	}
	
	/**
	 * Closes a connection with a MySql server
	 */
	public void close(Connection conn) {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Converts a Java Date object into a MySql formatted
	 * Timestamp object
	 * 
	 * @param date java date object to convert
	 * @return timestamp formatted for a MySql server
	 */
	protected Timestamp getCurrentTimeStamp(Date date) {
		return new Timestamp(date.getTime());
	}
	
	public void runQuery(String queryString, MySqlQueryRunner callback) {
		Connection conn = null;
		try {
			conn = connect();
			runQuery(conn, queryString, callback);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public void runQuery(Connection conn, String queryString, MySqlQueryRunner callback) {
		try {
			System.out.println("Query:\n" + queryString);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(queryString);
			try {
				if (callback != null) {
					while (rs.next()) {
						callback.runRowInstructions(rs);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					stmt.close();
					rs.close();
				}
			catch (Exception e) {
				e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int runInsertQuery(Connection conn, String queryString) {
		int key = 0;
		try {
			System.out.println("Query:\n" + queryString);
			Statement stmt = conn.createStatement();
			ResultSet rs = null;
			boolean keysReturned = stmt.execute(queryString, Statement.RETURN_GENERATED_KEYS);
			try {
				if (keysReturned) {
					rs = stmt.getGeneratedKeys();
					if (rs.next()) {
						key = rs.getInt(1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					stmt.close();
					if (rs != null)
						rs.close();
				}
			catch (Exception e) {
				e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	
	
	
	
	
	
	
	
}




