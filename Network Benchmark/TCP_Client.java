import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;

public class TCP_Client extends Thread 					// Extends Thread class
{
	static int TotalFile;
	BufferedReader br;											// Variables declarations
	int len,iterations,No_Of_Threads;
	String Threard_Name;
	static InetAddress []address=new InetAddress[8];
	static Socket socket[]=new Socket[8];
	BufferedReader s_input[]=new BufferedReader[8];
	PrintWriter s_out[]=new PrintWriter[8];
	static String host = "localhost";
	int port;
	int size,k=-1;
	double startTime=0.0,duration1=0.0;
	int mb_iterations;
	static File file;
	static BufferedWriter bw = null;
	static FileWriter fw = null;
	int cnt=0,cnt1=0;
	static double tcp_byte_time=0.0,tcp_byte_time_2=0.0,tcp_kb_time=0.0,tcp_64kb_time=0.0,tcp_kb_time_2=0.0,tcp_64kb_time_2=0.0;
	TCP_Client(int No_Of_Threads,String Threard_Name,int size,String host_name,int port)			// Create parameterized constructor which takes
	{							
		this.No_Of_Threads=No_Of_Threads;
		this.Threard_Name=Threard_Name;
		this.size=size;	
		host=host_name;
		this.port=port;											
		mb_iterations=size*16;												
	}
	TCP_Client()													// Create default constructor
	{
	}
	synchronized public void run()											// This method is called by every thread.
	{
		
		try
		{	
			if(Threard_Name.equals("Thread0"))
			{k=0;}
			else if(Threard_Name.equals("Thread1"))
			{k=1;}
			else if(Threard_Name.equals("Thread2"))
			{k=2;}
			else if(Threard_Name.equals("Thread3"))
			{k=3;}
			else if(Threard_Name.equals("Thread4"))
			{k=4;}
			else if(Threard_Name.equals("Thread5"))
			{k=5;}
			else if(Threard_Name.equals("Thread6"))
			{k=6;}
			else if(Threard_Name.equals("Thread7"))
			{k=7;}
			
					address[k] = InetAddress.getByName(host);												// get host address.
					socket[k] = new Socket(address[k], port);												// Create socket
					s_input[k]=new BufferedReader(new InputStreamReader(socket[k].getInputStream())); 		// Create BufferedReader object
					s_out[k]=new PrintWriter(socket[k].getOutputStream(),true);  							// Create PrintWriter object
					sender(64*1000,mb_iterations,s_out[k],s_input[k]);												 // Call sender method for mega byte operations
					tcp_64kb_time=tcp_64kb_time+duration1;
					s_out[k].println("END");	
					socket[k].close();																	  // close socket.		
								
		}
		catch(Exception e)
		{
			System.out.println("Error In Run"+e);
		}
	}
	String randomString(int len)												// This method is responsible for making random string of given length
	{	
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
		sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		return sb.toString();
	}
	void sender(int len,int iteration,PrintWriter s_out,BufferedReader s_input)				// This method responsible for communication with server. It takes length, iterationa and PrintWriter
	{																			// as parameter.

	    String main_string =randomString(len);										// Put generated string as HashMap.		

		String str;
		try
		{
			startTime = System.nanoTime();										// Start timer
			for(int i=0;i<iteration;i++)
			{		
				s_out.println(main_string);                                       // send packet to server.
				s_input.readLine();
			}							
			duration1 = System.nanoTime() - startTime;							// Stop timer and caluclate duration.
		}		
		catch(Exception e)
		{
			System.out.println(e);
		}
	}		
	public static void main(String args[])	
	{	
		int No_Of_Threads=0;
		int iteration;
		int size;
		BufferedReader sc_input=new BufferedReader(new InputStreamReader(System.in));
		
		try
		{		
			System.out.println("Enter the no of threads to create:-");					// Take no threads to create input from user
			No_Of_Threads=Integer.parseInt(sc_input.readLine());
			Thread [] t=new Thread[No_Of_Threads];
			TotalFile=104;
			size=TotalFile/No_Of_Threads;
			System.out.println("Enter port number");		                           // Take size of data transfer from user in MB
			int port=Integer.parseInt(sc_input.readLine());
			file = new File(" TCP Network Benchmrk.txt");
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			String host_name = "localhost";
			for(int i=0;i<No_Of_Threads;i++)
			{
				t[i]=new TCP_Client(No_Of_Threads,"Thread"+i,size,host_name,port);
				t[i].start();
			}
			for(int i=0;i<No_Of_Threads;i++)
			{
				t[i].join();
			}
	
			TCP_Client nct=new TCP_Client();								// create Network_Client_TCP object
			nct.display(No_Of_Threads,size);												// call Display method 
			bw.close();
			fw.close();
		}	
		catch(Exception e)
		{
			System.out.println(e);
		}		
	}
	public void display(int No_Of_Threads,int size) throws IOException										// This method is responsible for calculating throughput and latency and displaying it to user.
	{
		System.out.println("Throughput of TCP operations of 64KB packet size "+((8*No_Of_Threads*(TotalFile)*2)/((tcp_64kb_time)/1000000000))+" (MBits/Sec)");
		bw.write("\n File size: "+size+" "+"MB\n");
		bw.write("\n Packet/Buffer size: "+"64 KB"+"\n");
		bw.write("\n No of Threads: "+No_Of_Threads+"\n");
		bw.write("\n Throughput: "+((8*No_Of_Threads*(TotalFile)*2)/((tcp_64kb_time)/1000000000))+" (MBits/Sec)"+"\n");
		System.out.println("Latency of TCP operations of 64KB packet size "+(((tcp_64kb_time)/1000000)/(No_Of_Threads*TotalFile*2))+" (Milliseconds)");		
		bw.write("\n Latency: "+(((tcp_64kb_time)/1000000)/(No_Of_Threads*TotalFile*2*8))+" (Milliseconds)");
	}
}