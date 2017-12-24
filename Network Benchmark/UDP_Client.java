import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;

public class UDP_Client extends Thread            //  extends Thread
{
	BufferedReader br;
	int len,iterations,No_Of_Threads;
	String file_name,Threard_Name;			
	String host;
	int port;
	int k=-2;
	int cnt=0,cnt1=0;
	int size;
	static int mp=0;
	double startTime=0.0,duration1=0.0;
	static File file;
	static BufferedWriter bw = null;
	static FileWriter fw = null;
	int byte_iterations,kb_iterations,mb_iterations;
	static double udp_byte_time=0.0,udp_byte_time_2=0.0,udp_kb_time=0.0,udp_64kb_time=0.0,udp_kb_time_2=0.0,udp_64kb_time_2=0.0;
	UDP_Client() 											                                // Declare default construct Network_Client_UDP
	{
	}
	UDP_Client(int No_Of_Threads,String Threard_Name,int size,String host_name,int portno)  
	{																				//No_Of_Threads,Threard_Name,size,host_name and portno as input.
		this.No_Of_Threads=No_Of_Threads;
		this.Threard_Name=Threard_Name;
		this.size=size;	
		host=host_name;
		port=portno;													
		mb_iterations=size*16;											
		}
	synchronized public void run()											
	{
		try
		{						
			    sender(64*1000,mb_iterations);								//	call sender method to send 64 kbyte packet data to UDP server
				udp_64kb_time=udp_64kb_time+duration1;
		}
		catch(Exception e)
		{
			System.out.println("Error In Run"+e);
		}
	}
	String randomString( int len )											// This method takes length as parameter and generates random string of that lenght.
	{	
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
		sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );					
		return sb.toString();
	}
	void sender(int len,int iteration)										// This method takes lenghth and iterations as parameter, send fixed sized packet to UDP server.
	{		
		try
		{			
			String main_string=randomString(len);								// Put random strings to HashMap.					
			InetAddress address1 = InetAddress.getByName(host);			
			Socket socket1 = new Socket(address1, port);					// Create and establish socket connection on port 25003 of given address.
			
			BufferedReader s_input1=new BufferedReader(new InputStreamReader(socket1.getInputStream())); 
			PrintWriter s_out1=new PrintWriter(socket1.getOutputStream(),true); 			
			String str=Integer.toString(len);												// Convert lenghth to string 
			s_out1.println(str);															// send length to server to tell i am sending this size of packets.
			str=Integer.toString(iteration);												// Convert iteratins to string 
			s_out1.println(str);															// Send no if iterations to server.
			socket1.close();																// close socket.			
			String sentence;
			DatagramSocket clientSocket = new DatagramSocket();  							// Create DatagramSocket
			InetAddress IPAddress = InetAddress.getByName(host); 
			//Client Socket is created  
			DatagramPacket sendPacket;														// Create DatagramSocket to send packet
			DatagramPacket receivePacket;													// Create DatagramSocket to receive packet			
			byte[] sendData = new byte[len];  												// Create byte array to send.  											// Create byte array to receive.			
			startTime = System.nanoTime();				// Start timer.
			sentence=main_string;
			sendData = sentence.getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 25002);  				// Create packet 
			for(int i=0;i<iteration;i++)			
			{		
			
				clientSocket.send(sendPacket);  												// send it over network.
			}			
			duration1 = System.nanoTime() - startTime;									// Stop timer and compute time.			
			clientSocket.close();  																// close socket		
		}	
		catch(Exception e)
		{
			System.out.println("HI"+e);
		}		
	}
	public void	display(int No_Of_Threads,int size) throws IOException								// Compuet throughput,latency and display it.
	{	
		System.out.println("Throughput of UDP operations of 64KB packet size "+((8*No_Of_Threads*size)/((udp_64kb_time)/1000000000))+" (MBits/Sec)");		
		bw.write("\n File size: "+size+" "+"MB\n");
		bw.write("\n Packet/Buffer size: "+"64 KB"+"\n");
		bw.write("\n No pf Threads: "+No_Of_Threads+"\n");
		bw.write("\n Throughput: "+((8*No_Of_Threads*size)/((udp_64kb_time)/1000000000))+" (MBits/Sec)"+"\n");
		System.out.println();
		bw.write("\n Latency of UDP operations of 64KB packet size: "+(((udp_64kb_time)/1000000)/(No_Of_Threads*size))+" (Milliseconds)");
		System.out.println("Latency of UDP operations of 64KB packet size "+(((udp_64kb_time)/1000000)/(No_Of_Threads*size))+" (Milliseconds)");		
	}
	public static void main(String args[])
	{	
		int No_Of_Threads=0;
		BufferedReader sc_input=new BufferedReader(new InputStreamReader(System.in)); 
		int size=0;
		try
		{		
			System.out.println("Enter the no of threads to create:-");					// Enter number of thread
			No_Of_Threads=Integer.parseInt(sc_input.readLine());
			Thread[] t=new Thread[No_Of_Threads];
			size =1024;
			System.out.println("Enter the port no");			                        // Enter number of port
			int port=Integer.parseInt(sc_input.readLine());
			String host_name="localhost";
			System.out.println("Host "+ host_name+ " port "+port);
			file = new File(" UDP Network Benchmrk.txt");
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for(int i=0;i<No_Of_Threads;i++)
			{
				t[i]=new UDP_Client(No_Of_Threads,"Thread"+i,size,host_name,port);
				t[i].start();
			}
			for(int i=0;i<No_Of_Threads;i++)
			{
				t[i].join();
			}
			UDP_Client ncu=new UDP_Client();											
			ncu.display(No_Of_Threads,size);	                                          //calls display function for throughput and latency
			bw.close();
			fw.close();                                                                
		}
		catch(Exception e)
		{
			System.out.println(e);
		}		
	}
}

