import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CapitalizeClient {

	public static void main(String[] args) throws IOException {
		BufferedReader inFromUser=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your name to be identified with");
        String id=inFromUser.readLine();
        if(join(id)) {
        	System.out.println("If you want to get the memeber list type getMemberList."+'\n'+"If you want to connect to a specific user type his name");
        	if(inFromUser.equals("getMemberList")) {
        		System.out.println("Online Users"+'\n');
        		getMemberList();
        	}
        	else {
        		String destination=inFromUser.readLine();
        		chat(id,destination,"");
        		while(true) {
        			System.out.println("Enter your Message");
        			chat(id,destination,inFromUser.readLine());
        		}
        	}
        }
        else {
        	System.out.println("Name is used");
        }
        /*try (Socket socket = new Socket("Omar-PC", 9898)) 
        {
            System.out.println("Enter your name to be identified with");
            String id = inFromUser.readLine();
            System.out.println("I am connected at " + socket +" with unique name "+id);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            while(true) 
            {
	            System.out.println("Enter a string to send to the server:");
	            String message = inFromUser.readLine();
	            outToServer.println(message);
	            if (message.toLowerCase().equals("bye") || message.toLowerCase().equals("quit") || message.toLowerCase().equals("exit")) {
                	break;
    			}
	            System.out.println(inFromServer.readLine());
            }
        }
        */
    }
	public static boolean join(String name) throws IOException {
		try (Socket socket = new Socket("Omar-PC", 9898)) 
        {
    		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
	        outToServer.println(name);
            if(inFromServer.readLine().equals("true")) {
                System.out.println("I am connected at " + socket +" with unique name "+name);
                return true;
            }
            else {
            	return false;
            }
        }
	}
	public static void getMemberList() throws UnknownHostException, IOException{
		try (Socket socket = new Socket("Omar-PC", 9898)){
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            outToServer.println("#");
            System.out.println(inFromServer.read());
		}
	}
	public void quit() throws UnknownHostException, IOException {
		try (Socket socket = new Socket("Omar-PC", 9898)){
	        PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            outToServer.println("*");
            System.out.println("bye");
		}
	}
	public static void chat (String Source, String Destination, String Message) throws UnknownHostException, IOException {
		try (Socket socket = new Socket("Omar-PC", 9898)){
	        PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            outToServer.println(Source+'\n'+Destination+'\n'+Message);
		}
	}
}