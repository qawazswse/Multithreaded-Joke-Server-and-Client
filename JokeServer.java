/*--------------------------------------------------------

1. Name / Date:	
	Shibo Zhang / 9/28/2019
	
2. Java version used:
	Java 1.8
	
3. Precise command-line compilation examples / instructions:
	> javac JokeServer.java

4. Precise examples / instructions to run this program:
	> java JokeServer
	> java JokeClient
	> java JokeClientAdmin

5. List of files needed for running the program.
	a. JokeServer.java
	b. JokeClient.java
	c. JokeClientAdmin.java

6. Notes:
	Cannot be shut down by administrator.

----------------------------------------------------------*/
import java.io.*;
import java.net.*;

public class JokeServer{	//the main Server connect the client to the worker thread and starts a Mode server.
	
	public static boolean adminControlSwitch = true;
	   
    public static void main(String[] args) throws IOException {
        int q_len = 6;
        int port = 4546;
        Socket s;
        
        ModeServer ms = new ModeServer();
        Thread t = new Thread(ms);
        t.start();

        ServerSocket ss = new ServerSocket(port, q_len);

        System.out.println("Shibo Zhang's JokeServer starting up, listening at port 4546.");

        while(adminControlSwitch){
            s = ss.accept();
            new Worker(s).start();
        }
        ss.close();
    }
}

class Worker extends Thread {	//process the requests from client and give some feed back.
    Socket sock;
    static String[] content = new String[8];
    
    Worker(Socket s) {
        sock = s;
        //jokes [0/1/2/3(position number) *2 + 0(joke mode number)].
        content[0] = "JA #: What’s a foot long and slippery? A slipper.";
        content[2] = "JB #: As a scarecrow, people say I’m outstanding in my field. But hay, it’s in my jeans.";
        content[4] = "JC #: How did the hipster burn his mouth? He ate the pizza before it was cool.";
        content[6] = "JD #: What’s red and moves up and down? A tomato in an elevator";
        //proverbs [0/1/2/3(position number) *2 + 1(proverb mode number)].
        content[1] = "PA #: Let sleeping dogs lie.";
        content[3] = "PB #: Life begins at forty.";
        content[5] = "PC #: Lightning never strikes twice in the same place.";
        content[7] = "PD #: Look before you leap.";
    }
    public void run(){
        PrintStream out;
        BufferedReader in;
        
        try{
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintStream(sock.getOutputStream());
            try{
        		out.println(Mode.getValue());						//send client the mode
        		out.flush();
        		
                int number = Integer.parseInt(in.readLine());			//get number from client
                
                out.println(content[number]);							//send client the content
                out.flush();
                
                System.out.println("Content " + number + " sent");
                
            } catch(IOException ioe) {ioe.printStackTrace();}
        }catch(IOException ioe){ioe.printStackTrace();}
    }
}

class ModeServer implements Runnable {	
	
	public static boolean adminControlSwitch = true;
    
    public void run() {
    	System.out.println("In the admin looper thread");

        int q_len = 6;
        int port = 5050;	//I found the number from the Overview
        Socket sock;
   
        try{        
            ServerSocket ss = new ServerSocket(port, q_len);

            System.out.println("Shibo Zhang's ModeServer starting up, listening at port 5050.");

            while(adminControlSwitch){
                sock = ss.accept();
                new ModeWorker(sock).start();
            }
            
            ss.close();
            
        } catch (IOException ioe) {System.out.println(ioe);}
        
    }
}

class ModeWorker extends Thread {	//process the requests from administrator and give some feed back.
    Socket workerSock;
    String state;
    ModeWorker(Socket s) {
        workerSock = s;
    }
    public void run(){
        PrintStream out = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(workerSock.getInputStream()));
            out = new PrintStream(workerSock.getOutputStream());
            
			String command = in.readLine();
			if(command.equals("joke")) {
				Mode.setValue(0);
				String msg = "Server change to joke mode.";
				out.println(msg);
				System.out.println(msg);
			}
			else if(command.equals("proverb")) {
				Mode.setValue(1);
				String msg = "Server change to proverb mode.";
				out.println(msg);
				System.out.println(msg);
			}
			else if(command.equals("shutdown")) {
				JokeServer.adminControlSwitch = false;
				ModeServer.adminControlSwitch = false;
				System.out.println("Administrator left.");
				out.println("Good Bye.");
			}
		} catch (IOException ioe) {ioe.printStackTrace();}
    }
}

class Mode{		//this can store Mode and share.
	private static int value = 0;

	public static int getValue() {	//getter
		return value;
	}

	public static void setValue(int value) {	//setter
		Mode.value = value;
	}
}






















