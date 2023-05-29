/* Brief Description: Class LookupServer models a server for performing keyword
 * lookups for connected clients. A new thread is created for each client thus
 * making the server multithreaded. Clients communicate with the server through sockets*/

//Developer: Dimitrius G. Papachristoudis
//email: zelgius23@freemail.gr
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.io.*;
import java.net.*;

public class LookupServer
{
    //Global Variables
	private int port_number;   //The service's port number
	private int queue_size;    //The maximum number of clients awaiting in the queue
	
    	//Constructor without a parameter
	public LookupServer()
	{
		port_number=4000;    //Default port number will be 4000
		queue_size=1000;     //Default maximum number of clients 1000
	}
    
	//A method for "activating" the server
	public void enableServer()
	{
		ServerSocket server;    //The Server's socket
		int counter=0;   	//A counter of successful connections that have been established thus far
		try
		{
			//Activate Server
			server=new ServerSocket(port_number, queue_size);
			System.out.println("Server is now active!!!\n");
			while (true)
			{
				Socket connection=server.accept();   //Wait for clients
				++counter;                    	     //increment counter
				//Create a thread to serve the recently arrived client
				ClientThread cthread=new ClientThread(connection);
				//Start the thread
				cthread.start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }

	//Main Method
	public static void main(String args[])
	{
		LookupServer s=new LookupServer();
		s.enableServer();
	}

}