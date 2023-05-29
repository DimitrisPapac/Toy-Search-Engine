/*  Brief Description: Class ClientThread models a thread that is created per client that is
 *  is connected to the server (see LookupServer.java for further details) rendering 
 *  the server multithreaded. Please note that we do not need to define a protocol that
 *  governs the transaction between the client and the thread since it will only consist
 *  of one state.
 */

//Developer: Dimitris Papachristoudis
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ClientThread extends Thread
{

	private Socket connection;	     //A reference to the actual client's Socket
	private DataInputStream in;      //A DataInputStream for communicating with the actual client
	private DataOutputStream out;    //A DataOutputStream for communicating with the actual client
	private Connection conn;	     //A Connection instance representing the thread's connection to the exposed database
	private static final String serverName = "127.0.0.1";   //Database is located on the localhost
	private static final String mydatabase = "wordfreq";    //Database name
	private static final String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
	private static final String username = "commonUser",       //Connect as commonUser (please refer to create_wordfreq.sql for details)
			                    password = "commonPassword";   //

	private static final int MAX = 10;	 //The maximum number of sites to be returned as a result

	//Constructor method
	public ClientThread(Socket s)
	{
		//Attempt to load JDBC driver
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			synchronized(this)
			{
				System.out.println("ClassNotFoundException: "+e.getMessage());
				return;
			}
		}
		
		//Initialize client's socket
		connection = s;
		
		synchronized(this)
		{
			System.out.println(connection.getInetAddress().getHostName()+" \\ "+
					   connection.getInetAddress().getHostAddress()+" has logged in!!!\n");
		}
		try
		{
			//Create streams for communication with the client and a connection to the database
			in = new DataInputStream(connection.getInputStream());
			out = new DataOutputStream(connection.getOutputStream());
			conn = DriverManager.getConnection(url, username, password);
		}
		catch (IOException e)
		{
			synchronized(this)
			{
				System.err.println("Error: "+e);
			}
		}
		catch (SQLException e)
		{
			synchronized(this)
			{
				System.err.println("Error: "+e);
			}
		}
	}

	//Overload Thread's run() method
	public void run()
	{
		try
		{
			String inputLine,    		//A String variable to store the client's message
			       outputLine;   		//A String variable to store the server's message
			PreparedStatement ps;     	//The query to the database

			//Loop until client disconnects
			while ((inputLine  = in.readUTF()) != null)
			{
				//Construct a query to the database from the user's request
				ps = buildQuery(inputLine);

				//If no keywords were entered
				if (ps == null)
					outputLine = "Please enter the keyword(s) you wish to search for...";
				else   //query != null
				{
					outputLine = "";

					//Execute query
					ResultSet rs = ps.executeQuery();
					
					int i = 0;
					while (rs.next())
					{
						i++;
						outputLine += i + " " + rs.getString(1) + " " + rs.getInt(2) + "\n";
						
					}
					
					if (i == 0)
						outputLine = "No results were found!\n";
					else   //i>0
						outputLine = "Results for: " + inputLine + "\n" + outputLine;
				}

				//Send response back to client
				out.writeUTF(outputLine);
			}

			//Close the DataInputStream
			in.close();

			//Close the DataOutputStream
			out.close();

			//Terminate the connection with the client
			connection.close();

			//Close connection to the Database
			conn.close();

			synchronized(this)
			{
				System.out.println(connection.getInetAddress().getHostName()+" \\ "+
						   connection.getInetAddress().getHostAddress()+" has logged off!!!\n");
			}
		}
		catch (java.net.SocketException e)
		{
			synchronized(this)
			{
				System.err.println(connection.getInetAddress().getHostName()+" \\ "+
					   connection.getInetAddress().getHostAddress()+" was disconnected!!!\n");
			}
		}
		catch (IOException e)
		{
			synchronized(this)
			{
				System.err.println("Error: "+e);
			}
		}
		catch (SQLException e)
		{
			synchronized(this)
			{
				System.err.println("SQLError: "+e);
			}
		}

	}

	//A method for constructing an SQL query out of the given request
	private PreparedStatement buildQuery(String request)
	{
		StringTokenizer tokenizer = new StringTokenizer(request, " \t\n\r\f.,;:!?_~^'\"(){}[]-=+–—’'|«»><=//…");
		int n = tokenizer.countTokens();
		if (n == 0)
			return null;
		HashSet<String> keywords = new HashSet<String>();
		while (tokenizer.hasMoreTokens())
			keywords.add(tokenizer.nextToken());
		String query = "select S.url, R.total_freq from (select w.page_id as pid, SUM(w.freq) as total_freq from word as w where ";
		for (int i=1; i<=n-1; i++)
			query+="w.word=? or ";
		query+="w.word=? ";
		query+="group by w.page_id) as R, page as S where R.pid=S.id order by R.total_freq desc limit ?";

		PreparedStatement ps = null;
		try
		{
			int i = 1;
			ps = conn.prepareStatement(query);
			for (String s : keywords)
			{
				ps.setString(i, s);
				++i;
			}
			ps.setInt(i, MAX);
		}
		catch (SQLException e)
		{
			synchronized(this)
			{
				System.err.println("Failed to construct query!");
				return null;
			}
		}
		return ps;
	}

}
