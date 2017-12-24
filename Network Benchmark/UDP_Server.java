import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;

public class UDP_Server implements Runnable      // Implement runnable interface
{
   Socket csocket;							
   static int port;
   ServerSocket serverSocket1;
   Socket sock;
   DatagramSocket serverSocket;
   UDP_Server nsu;
   String host;
   UDP_Server()				//Declare default constructor
   {
   }  
   UDP_Server(int portno, String host) 	//		declare parameterized constructor which takes portno as parameter
   {		
		port=portno;
		this.host=host;
   } 
   UDP_Server(Socket csocket,DatagramSocket serverSocket)    //		Declare parameterized constructor which takes Socket and serverSocket as parameter  
   {
      this.csocket = csocket;
	  this.serverSocket=serverSocket;
   }
   public static void main(String args[]) throws Exception 		
   {      		
	    String host_name ="localhost";
		System.out.println("Host "+ host_name+ " port "+port);			
		UDP_Server ns=new UDP_Server();
		ns.server_setup();//call server_setup
   }
   public void server_setup() throws Exception							// This method is responsible for seting up server and creating separate thread as request comes. 	
   {
		try
		{		
			serverSocket1 = new ServerSocket(0);						// Create Socket connection.
			port=serverSocket1.getLocalPort();
			System.out.println("Port number listening: "+serverSocket1.getLocalPort());
			serverSocket = new DatagramSocket(serverSocket1.getLocalPort());  					// Create DatagramSocket
		while (true) 									
		{	  			
			sock = serverSocket1.accept();								// Accept incoming request.
			new Thread(new UDP_Server(sock,serverSocket)).start();			// create separate thread of each inciming request.		
		}
		}
		catch(Exception e)
		{
			 System.out.println("seerver_setup"+e);
		}
   }
   public void run()  {													// This method is called by each new thread
     // System.out.println("New Thread");
	  try 
	  {		 					
			execute(serverSocket,serverSocket1);						//call execute method.
      }
      catch (Exception e) {
         System.out.println("run"+e);
      }
   }
   public void execute(DatagramSocket serverSocket,ServerSocket serverSocket1)  // This method is resonsible for carring out actual send receive operation
	{																			// which takes DatagramSocket and ServerSocket as input parameter.
		try
		{											
			BufferedReader s_input=new BufferedReader(new InputStreamReader(csocket.getInputStream())); 
			PrintWriter s_out=new PrintWriter(csocket.getOutputStream(),true);  
			String str=s_input.readLine();           									// Get the length of packet from client
			int len=Integer.parseInt(str);	
			
			str=s_input.readLine();         											// Get the number of iterations from client.  
			int iterations=Integer.parseInt(str);
			csocket.close();															// close socket.
			DatagramPacket receivePacket;
			DatagramPacket sendPacket;	
			InetAddress address1 = InetAddress.getByName(host);	
			DatagramSocket clientSocket = new DatagramSocket(port);  	
			Socket socket1 = new Socket(address1, port);
			byte[] receiveData = new byte[len];  										// create receiveDate byte array.
            byte[] sendData = new byte[len]; 											// Create sendData byte array.
			int counter=iterations;
			InetAddress IPAddress;
			int port1;
			while(counter>0)  
            {  
                  receivePacket = new DatagramPacket(receiveData, receiveData.length);  			// create DatagramPacket packet to receive data.
                  serverSocket.receive(receivePacket);  														// receive incoming packet
                  String sentence = new String( receivePacket.getData());  										// convert it to string.
				  counter--;	
            }  
}
	catch(Exception e)			
	{
		//System.out.println("Hi"+e);
	}
	}	  
}
