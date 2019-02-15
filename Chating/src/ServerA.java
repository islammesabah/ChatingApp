import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerA 
{
	static ArrayList<String> members = new ArrayList<>();
	static ArrayList<Socket> membersSocket = new ArrayList<>();
	static String clientName;
	static Socket clientSocket;
	static BufferedReader inFromClient;
    static PrintWriter outToClient;
	static ArrayList<BufferedReader> inFromClientList = new ArrayList<>();
	static ArrayList<PrintWriter> outToClientList =  new ArrayList<>();
	
	public static void main(String[] args) throws IOException 
	{
		System.out.println("Server A is running.");
        ExecutorService pool = Executors.newFixedThreadPool(20);
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(9898))
        {
            while (true) 
            {
            	clientSocket = listener.accept();
            	if(joinResponse()) 
            	{
                    pool.execute(new Thread(clientSocket, clientNumber++,clientName,inFromClient,outToClient));
            	}
            }
        }
	}
	
	private static boolean joinResponse () throws IOException 
	{
		inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
    	String id = inFromClient.readLine();
    	clientName=id;
    	if(members.contains(id)) 
    	{
    		outToClient.println("false");
    		clientSocket.close();
    		inFromClient.close();
    		outToClient.close();
    		clientName=null;
    		clientSocket=null;
    		inFromClient=null;
    		outToClient=null;
    		return false;
    	}
    	else 
    	{
    		members.add(id);
    		membersSocket.add(clientSocket);
    		inFromClientList.add(inFromClient);
    		outToClientList.add(outToClient);
    		outToClient.println("true");
    		return true;
    	}
	}
	
	private static class Thread implements Runnable 
	{
        private Socket socket;
        private int clientNumber;
        private String clientName;
        private BufferedReader inFromClient;
        private PrintWriter outToClient;

        public Thread(Socket socket, int clientNumber, String clientName, BufferedReader inFromClient, PrintWriter outToClient) 
        {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.clientName=clientName;
            this.inFromClient=inFromClient;
            this.outToClient=outToClient;
            System.out.println("New client #" + clientNumber + " connected at " + socket);
        }
        
        public void memberListResponse() throws IOException {
    		String s="";
            for(int i=0;i<members.size();i++) {
            	s+=members.get(i)+"\n";
            }
            outToClient.print(s);
            outToClient.flush();
    	}

        public void run() 
        {
            try 
            {
            	while(true) 
            	{
	                String sentence = inFromClient.readLine();
	                if(sentence !=null)
	                {
	                	if (sentence.equals("quit")) 
		                {
		                	break;
		    			}
		                else if(sentence.equals("getMemberList")) 
		                {
		                	memberListResponse();
		                }
		                else
		                {
		                	String x[]=new String[2];
		            		x=sentence.split(" ", 2);
		            		boolean founded=false;
		            		if(x[0].equals(clientName))
		            		{
		            			outToClient.println("false");
		            			continue;
		            		}
	            			for(int i=0;i<members.size();i++) 
		                	{
		                		if(members.get(i).equals(x[0])) 
		                		{
		                			outToClient.println("true");
		                			founded=true;
		                			break;
		                		}
		                	}
	            			if(!founded) 
	            			{
	                			outToClient.println("false");
	            			}
	            			else 
	            			{
	            				PrintWriter destination = null;
		            			for(int i=0;i<members.size();i++) 
			                	{
			                		if(members.get(i).equals(x[0])) 
			                		{
			                			destination=outToClientList.get(i);
			                			break;
			                		}
			                	}
		                        destination.println("*From "+clientName+" to You: "+x[1]);
		                        destination.flush();
	            			}
		                }
	                }
            	}
            } 
            catch (Exception e) 
            {
            	System.out.println(e.getMessage());
            	e.printStackTrace();
                System.out.println("Error handling client #" + clientNumber);
            } 
            finally
            {
                try 
                {
                	for(int i=0;i<members.size();i++) 
                	{
                		if(members.get(i).equals(clientName)) 
                		{
                			outToClient.println("#");
                			outToClient.flush();
                        	members.remove(i);
                        	membersSocket.remove(i);
                        	outToClientList.remove(i);
                        	inFromClientList.remove(i);
                        	inFromClient.close();
                        	outToClient.close();
                			break;
                		}
                	}
                	socket.close(); 
                } 
                catch (IOException e) {
                	
                }
                System.out.println("Connection with client # " + clientNumber + " closed");
            }
        }
    }
}