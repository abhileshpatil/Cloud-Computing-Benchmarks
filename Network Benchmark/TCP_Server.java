import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;

public class TCP_Server implements Runnable     // Runnable interface
{ 
	
   Socket csocket;
   static int port;
   Socket sock;
   ServerSocket serverSocket;
   BufferedReader br;
   PrintWriter pw;
   TCP_Server(Socket csocket) 					
   {
      this.csocket = csocket;
   }
   public TCP_Server() {
	// TODO Auto-generated constructor stub
}
public static void main(String args[]) throws Exception 
   {     
		TCP_Server set=new TCP_Server();				           
		set.server_ini();  											// Call server_setup method.
   }
   public void server_ini()									// This method is responsible for making connection with client.
   {
		try
		{
		serverSocket = new ServerSocket(port);					// Create serverSocket object.
		System.out.println("listening on port: " + serverSocket.getLocalPort());
		System.out.println("Listening");	
		while (true) 
		{	  
			sock = serverSocket.accept();						// accepts incoming sockets from  client
			System.out.println("Connected");
			System.out.println("Server Threads");
			new Thread(new TCP_Server(sock)).start();	       // Create sepaate thread of each incoming socket
		}
		}
		catch(Exception e)
		{
			 System.out.println(e);
		}
   }
   public void run() {												// This method is called by each thread. 
      try 
	  {
		 
		 String line;         
		 
		 br=new BufferedReader(new InputStreamReader(csocket.getInputStream()));    // Create BufferedReader 
		 pw=new PrintWriter(csocket.getOutputStream(),true);  							// Create PrintWriter object 
		
		 while(!((line= br.readLine()).equals("END")))							// Read till the rline != "END";
		 {
			pw.println(line);
}	
         csocket.close();																	// Close socket
      }
      catch (IOException e) {
         System.out.println(e);
      }
   }
}