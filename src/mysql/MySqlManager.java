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
	
	public String dbHost;
	public String dbName;
	private String dbUsername = "pi";
	private String dbPassword = "pipassword";
	protected static boolean isDriverLoaded = false;

	
	/**
	 * Class Constructor
	 */
	public MySqlManager() {
		this.dbHost = "127.0.0.1";
		this.dbName = "churchill";
	}
	/**
	 * Class Constructor
	 */
	public MySqlManager(String dbHost, String dbName) {
		this.dbHost = dbHost;
		this.dbName = dbName;
	}
	
	public MySqlManager(String dbHost, String dbName, String dbUsername, String dbPassword) {
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
	}
	
	@SuppressWarnings("deprecation")
	protected void loadDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		isDriverLoaded = true;
	}
	
	/**
	 * Starts a connection with the MySql server
	 */
	public Connection connect() {
		return connect(this.dbUsername, this.dbPassword);
	}
	public Connection connect(String username, String password) {
		Connection c = null;
		try{
			if (!isDriverLoaded) {
				this.loadDriver();
			}
			//c = dataSource.getConnection();
			c = DriverManager.getConnection("jdbc:mysql://" + dbHost + "/" + dbName +"?" + "user=" + username + "&password=" + password);
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
						key = rs.getInt(0);
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
		
		return key;
	}
	
	
	
	
	
	
	
	
	
}




