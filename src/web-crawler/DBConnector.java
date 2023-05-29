/* Brief Description: The DBConnector serves as a connector to the wordFreq database */

//Developer: Dimitris Papachristoudis
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector
{
	private Connection conn = null;   //The connection to the database itself
	private String url = "jdbc:mysql://localhost/";
	private String dbName = "wordfreq?useUnicode=true&characterEncoding=utf-8&connectTimeout=0&socketTimeout=0&autoReconnect=true";
	private String driver = "com.mysql.jdbc.Driver";
	private String userName = "mai1223";   //Username (see create_wordfreq.sql for details)
	private String password = "mai1223";   //Password (see create_wordfreq.sql for details)
	private static DBConnector INSTANCE = null;   //The DBConnector's instance
												  //(defined as static since there can be one
												  //DBConnector instance at most)

	//Constructor
	DBConnector()
	{
		openConnection();
	}

	//A method for opening the connection
	private void openConnection()
	{
		//Attempt to connect to the database and load the JDBC connector 
		try
		{
			Class.forName(driver).newInstance();
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//A method for retrieving an instance of the connection
	public static DBConnector getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new DBConnector();
		return INSTANCE;
	}

	//A method for executing a query. Returns null if it fails!
	public ResultSet executeQuery(String statement)
	{
		checkConnection();
		try
		{
			Statement state = conn.createStatement();
			ResultSet results = state.executeQuery(statement);

			return results;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//A method for retrieving the connection to the database itself
	public Connection getConn()
	{
		return conn;
	}

	//Executes an update query. Yields true if the
	//update was a success and false otherwise.
	public boolean executeUpdate(String statement)
	{
		checkConnection();
		Statement state;
		try
		{
			state = conn.createStatement();

			state.executeUpdate(statement);
			return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}

	}

	//A method for checking the status of the connection
	public void checkConnection()
	{
		if (!testConnection())
			openConnection();
	}

	//A method performing tests on the connection to check its status
	public boolean testConnection()
	{
		String query = "SELECT 1";
		ResultSet rs = null;
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			if (stmt == null)
			{
				return false;
			}

			rs = stmt.executeQuery(query);
			if (rs == null)
			{
				return false;
			}

			if (rs.next())
			{
				// connection object is valid: we were able to
				// connect to the database and return something useful.
				return true;
			}

			// there is no hope any more for the validity
			// of the connection object
			return false;

		}
		catch (Exception e)
		{
			//
			// something went wrong: connection is bad
			//
			return false;
		}
		finally
		{
			//Attempt to close the ResultSet and the Statement
			try
			{
				rs.close();
				stmt.close();
			}
			catch (Exception e)
			{}
		}
	}

}
