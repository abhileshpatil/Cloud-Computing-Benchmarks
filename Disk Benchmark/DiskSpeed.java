import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.*;

public class DiskSpeed extends Thread {
	
	int noOfThreads, fileSize;
	static int blockSize;
	static int track=0;
	double startTime = 0.0, endTime = 0.0;
	static double timeDiff_SeqWrite = 0.0, totalTime_SeqWrite = 0.0;
	static double timeDiff_SeqRead = 0.0, totalTime_SeqRead = 0.0;
	static double timeDiff_RandWrite = 0.0, totalTime_RandWrite = 0.0;
	static double timeDiff_RandRead = 0.0, totalTime_RandRead = 0.0;
	static double timeDiff_ReadWrite = 0.0, totalTime_ReadWrite = 0.0;
	
	String filename = "storage.txt";
	static HashMap<String,Double> seqWriteResult = new HashMap<String,Double>();
	static HashMap<String,Double> seqReadResult = new HashMap<String,Double>();
	static HashMap<String,Double> randWriteResult = new HashMap<String,Double>();
	static HashMap<String,Double> randReadResult = new HashMap<String,Double>();
	static HashMap<String,Double> ReadWriteResult = new HashMap<String,Double>();
	
	static String data = "";
	
	File logFile = new File("log.txt");
	static File disk_output;
	static FileWriter fw;
	static BufferedWriter bw;
	
	DiskSpeed() {
		// TODO Auto-generated constructor stub
	}
	
	DiskSpeed(int noOfThreads, int fileSize)  
	{				
		this.noOfThreads = noOfThreads;
		this.fileSize = fileSize;
	}
	
	synchronized public void run(){		
//		System.out.println("inside run method for block size: "+blockSize);
		
		sequential_write(blockSize, filename, (fileSize*1024*1024));
//		System.out.println("Seq Write: time diff is "+timeDiff_SeqWrite);
		totalTime_SeqWrite = totalTime_SeqWrite + timeDiff_SeqWrite; 
//		System.out.println("total time is "+totalTime_SeqWrite);
		seqWriteResult.put("Sequential Write "+blockSize, totalTime_SeqWrite);
//		System.out.println("Sequential Write: data in hashmap: "+seqWriteResult);
		
		sequential_read(blockSize, filename, fileSize*1024*1024);
//		System.out.println("Seq Read: time diff is "+timeDiff_SeqRead);
		totalTime_SeqRead = totalTime_SeqRead + timeDiff_SeqRead; 
//		System.out.println("Seq Read: total time is "+totalTime_SeqRead);
		seqReadResult.put("Sequential Read "+blockSize, totalTime_SeqRead);
//		System.out.println("Sequential Read: data in hashmap: "+seqReadResult);
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("");
		writer.close();
		
		random_write(blockSize, filename, fileSize);
//		System.out.println("Random write: time diff is "+timeDiff_RandWrite);
		totalTime_RandWrite = totalTime_RandWrite + timeDiff_RandWrite; 
//		System.out.println("Random write: total time is "+totalTime_RandWrite);
		randWriteResult.put("Random Write "+blockSize, totalTime_RandWrite);
//		System.out.println("Random Write: data in hashmap: "+randWriteResult);
		
		random_read(blockSize, filename, fileSize);
//		System.out.println("Random read: time diff is "+timeDiff_RandRead);
		totalTime_RandRead = totalTime_RandRead + timeDiff_RandRead; 
//		System.out.println("Random read: total time is "+totalTime_RandRead);
		randReadResult.put("Random Read "+blockSize, totalTime_RandRead);
//		System.out.println("Random Read: data in hashmap: "+randReadResult);
		
		read_write(blockSize, filename, fileSize);
//		System.out.println("Read+Write: time diff is "+timeDiff_ReadWrite);
		totalTime_ReadWrite = totalTime_ReadWrite + timeDiff_ReadWrite; 
//		System.out.println("Read+Write: total time is "+totalTime_ReadWrite);
		ReadWriteResult.put("Read+Write "+blockSize, totalTime_ReadWrite);
//		System.out.println("Read+Write: data in hashmap: "+ReadWriteResult);
	}
	
	private void read_write(int blockLength, String inputfile, int fileSize) {
		// TODO Auto-generated method stub
		File outFile = new File("OutFile.txt");
		File inputFile = new File(inputfile);
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		byte[] byteArr = new byte[blockLength];
		
		try{
			fis = new FileInputStream(inputFile);
			fos = new FileOutputStream(outFile);
			long inFileLength = inputFile.length();
			
			startTime = System.nanoTime();
			
			while(fis.read(byteArr) > 0){
				fos.write(byteArr, 0, (int)((inFileLength) > byteArr.length ? byteArr.length : (inFileLength)));
				inFileLength = inFileLength - byteArr.length;
			}
			
			endTime = System.nanoTime();
			timeDiff_ReadWrite = endTime - startTime;
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void random_write(int blockLength, String filename, int fileSize) {
		// TODO Auto-generated method stub
//		System.out.println("inside random_write method");
		
		File random_file = new File(filename);
		RandomAccessFile randomFileStorage = null;
		FileChannel out = null;
//		String data = generateData(blockLength);
		Random rand = new Random();
				
		try{
			randomFileStorage = new RandomAccessFile(random_file, "rw");

			byte[] randomByteArr = new byte[blockLength];

			startTime = System.nanoTime();
			
			for(long j = 0; j < fileSize; j++){
				randomFileStorage.seek(rand.nextInt(fileSize));
				randomFileStorage.write(randomByteArr);
			}
			
			endTime = System.nanoTime();
			
			timeDiff_RandWrite = endTime - startTime;
			
			randomFileStorage.close();
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	void random_read(int blockLength, String filename,int fileSize){	
//		System.out.println("inside random_read method");
		
		Random rand = new Random();
		
		try 	
		{ 
			RandomAccessFile randReadFile = new RandomAccessFile(filename, "rw");
			byte[] randByteArr = new byte[blockLength];
			
			startTime = System.nanoTime();
			
			for(int i = 0; i < fileSize; i++){	
				randReadFile.seek(rand.nextInt(fileSize));
				randReadFile.read(randByteArr);
			}
			
			endTime = System.nanoTime();
			
			timeDiff_RandRead = endTime - startTime;			
			randReadFile.close();
		} 
		catch (IOException e) 
		{ 
			e.printStackTrace(); 
		} 
	}

	private void sequential_write(int blockLength, String filename, int fileSize) {
		// TODO Auto-generated method stub
//		System.out.println("inside sequential_write method");
		
		BufferedOutputStream bos = null;
		
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			bos = new BufferedOutputStream(fos);

			byte[] seqByteArr = new byte[blockLength];
			
			startTime = System.nanoTime();
			
			while(fileSize > 0){
				bos.write(seqByteArr, 0, (int)((fileSize) > seqByteArr.length ? seqByteArr.length : (fileSize)));
				fileSize = fileSize - seqByteArr.length;
			}
			
			endTime = System.nanoTime();

			timeDiff_SeqWrite = endTime - startTime;	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void sequential_read(int blockLength, String filename, int fileSize){
		
//		System.out.println("inside sequential_read method");
		
		File seq_read_file = new File(filename);
		
		if(!seq_read_file.exists()){
			System.out.println("File does not exist !!!");
		}
		else{
			try {
				FileInputStream fis = new FileInputStream(seq_read_file);
				
				byte[] seqByteArr = new byte[blockLength];
				long fileLength = seq_read_file.length();
				
				startTime = System.nanoTime();
				
				while(fileLength > 0){
					fis.read(seqByteArr, 0,(int)((fileLength) > seqByteArr.length ? seqByteArr.length : (fileLength)));
					fileLength = fileLength - seqByteArr.length;
				}
				
				endTime = System.nanoTime();

				timeDiff_SeqRead = endTime - startTime;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
	}

	private static String generateData(int blockSize) {
		// TODO Auto-generated method stub
		String str = "01";
		
		Random rand = new Random();
		
		StringBuilder sb = new StringBuilder(blockSize);	
		
		for(int i = 0; i < blockSize; i++){
			sb.append(str.charAt(rand.nextInt(str.length())));	
		}
//		System.out.println("string size is "+sb.length());
		return sb.toString();
	}

	public static void main(String args[]){
		
		DiskSpeed d1 = new DiskSpeed();
		
		disk_output = new File("disk_evaluation.txt");
		try{
			fw = new FileWriter(disk_output);
			bw = new BufferedWriter(fw);
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		Scanner src = new Scanner(System.in);
		
		int fileSize = 1280;
		
		System.out.println("Enter the number of threads for the operation");
		int noOfThreads = src.nextInt();
		
		try{
			Thread[] t = new Thread[noOfThreads];
			
			List<Integer> blockSizeList = Arrays.asList(8, 8*1024, 8*1024*1024, 80*1024*1024);
			
			for(int i = 0; i < blockSizeList.size(); i++){
				blockSize = blockSizeList.get(i);
				track=i;
//				System.out.println("starting for blocksize: "+ blockSize);
				data = generateData(blockSize);
				
				for (int j = 0; j < noOfThreads; j++) {
//					System.out.println("Starting thread "+(j+1));
				    t[j] = new Thread(new DiskSpeed(noOfThreads, (fileSize)));
				    t[j].start();
				}
				
				for (int k = 0; k < noOfThreads; k++) {
				    t[k].join();
				}
				
				
				
//				System.out.println("operations done for blocksize "+blockSize+"\n deleting all the timings...");
				timeDiff_SeqWrite = totalTime_SeqWrite = 0.0;
				timeDiff_SeqRead = totalTime_SeqRead = 0.0;
				timeDiff_RandWrite = totalTime_RandWrite = 0.0;
				timeDiff_RandRead = totalTime_RandRead = 0.0;
				timeDiff_ReadWrite = totalTime_ReadWrite = 0.0;
			}	
			
			
		}catch(Exception e){
			System.out.println(e);
		}
		d1.display(noOfThreads, fileSize);
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	private void display(int noOfThreads, int fileSize) {
		int k=0;
		// TODO Auto-generated method stub
//		System.out.println("inside display method");
		
		try {
			
			bw.write("Threads: "+noOfThreads+" FileSize: "+fileSize+"\n");
//			bw.write("\nBlockSize: "+blockSize+"\n");
			bw.write("Sequential Write\n");
			for(Map.Entry<String,Double> m : seqWriteResult.entrySet()){
				// System.out.println(m.getKey()+" Throughput is "+(((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000)))+" MB/Sec");
				// System.out.println(m.getKey()+" Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
				bw.write(m.getKey()+" :"+"Throughput is "+(((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000)))+" MB/Sec\n");
				bw.write(m.getKey()+" :"+"Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
			}
			
			bw.write("\nSequential Read\n");
			for(Map.Entry<String,Double> m : seqReadResult.entrySet()){  
				// System.out.println(m.getKey()+" Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec");
				// System.out.println(m.getKey()+" Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
				bw.write(m.getKey()+" :"+"Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec\n");
				bw.write(m.getKey()+" :"+"Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
			}
			
			bw.write("\nRandom Write\n");
			for(Map.Entry<String,Double> m : randWriteResult.entrySet()){  
				// System.out.println(m.getKey()+" Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec");
				// System.out.println(m.getKey()+" Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
				bw.write(m.getKey()+" :"+"Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec\n");
				bw.write(m.getKey()+" :"+"Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
			}
			
			bw.write("\nRandom Read\n");
			for(Map.Entry<String,Double> m : randReadResult.entrySet()){  
				// System.out.println(m.getKey()+" Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec");
				// System.out.println(m.getKey()+" Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
				bw.write(m.getKey()+" :"+"Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec\n");
				bw.write(m.getKey()+" :"+"Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
			}
			
			bw.write("\nRead + Write\n");
			for(Map.Entry<String,Double> m : ReadWriteResult.entrySet()){  
				// System.out.println(m.getKey()+" Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec");
				// System.out.println(m.getKey()+" Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
				bw.write(m.getKey()+" :"+"Throughput is "+((noOfThreads*fileSize)/(((Double)m.getValue())/1000000000))+" MB/Sec\n");
				bw.write(m.getKey()+" :"+"Latency is "+(((Double)(m.getValue())/1000000)/(noOfThreads*fileSize))+" MilliSeconds\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			}
}
