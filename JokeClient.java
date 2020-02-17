/*--------------------------------------------------------

1. Name / Date:	
	Shibo Zhang / 9/28/2019
	
2. Java version used: 
	Java 1.8
	
3. Precise command-line compilation examples / instructions:
	> javac JokeClient.java

4. Precise examples / instructions to run this program:
	> java JokeServer
	> java JokeClient
	> java JokeClientAdmin

5. List of files needed for running the program.
	a. JokeServer.java
	b. JokeClient.java
	c. JokeClientAdmin.java

6. Notes:
	Every time the client launches, it's a new user.

----------------------------------------------------------*/
import java.io.*;
import java.net.*;
import java.util.Random;

public class JokeClient {
	
    private static String serverName;
    private static String userName;
    private static int mode = 0;	//0 means "joke mode", 1 means "proverb mode"
    private static int jPosition = 4;
    private static int pPosition = 4;
    private static int[] jNumbers;
    private static int[] pNumbers;
    static boolean firstJoke = true;
    static boolean firstProverb = true;
    
    public static void main(String[] args) throws IOException{
        String serverName;
        if (args.length < 1)
            serverName = "localhost";  //default
        else serverName = args[0];

        System.out.println("Shibo Zhang's JokeClient.");
        System.out.println("Client using JokeServer: " + serverName + ", Port: 4546.");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please type your name:");
        userName = in.readLine();
        
        try{
            String command;
            do{
                System.out.println("Hey " + userName + " , Press ENTER to read, type \"quit\" to quit.");
                System.out.flush();
                command = in.readLine();

                if(!command.equals("quit")) {
                	getServerContent();
                }
                
            } while (!command.equals("quit"));
            System.out.println("Cancelled by user request.");

        } catch (IOException x) {
            x.printStackTrace();
        }
    }
    
    static int[] randOrder() {	//break the old order and randomly make a new one
    	int[] numbers = new int[4];
    	for(int i = 0; i < 4; i++) numbers[i] = i;
    	Random r = new Random();
    	
		for (int i = 0; i < numbers.length; i++) {
		    int j = r.nextInt(numbers.length);
		    int temp = numbers[i];
		    numbers[i] = numbers[j];
		    numbers[j] = temp;
		}
		return numbers;
    }

    static void getServerContent() {
        Socket s;
        BufferedReader fromServer;
        PrintStream toServer;
        String[] content;
        String c;

        try{
            s = new Socket(serverName, 4546);
            fromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            toServer = new PrintStream(s.getOutputStream());
            //get mode from server.
            int newMode = Integer.parseInt(fromServer.readLine());
            if(mode != newMode) {
            	System.out.println(mode == 0 ? "Server is Joke mode now.\n" : "Server is Proverbmode now.\n");
            	mode = newMode;
            }
            //shuffle.
	        if(jPosition > 3) {
	        	jNumbers = randOrder();
		        jPosition = 0;
		        if(!firstJoke) System.out.println("JOKE CYCLE COMPLETED.");
		        firstJoke = false;
	        }
	        if(pPosition > 3) {
	        	pNumbers = randOrder();
		        pPosition = 0;
		        if(!firstProverb) System.out.println("PROVERB CYCLE COMPLETED.");
		        firstProverb = false;
	        }
            //send the number to the server to get text content.
	        //The number is 2 times a randomized position number(0~4) and plus a mode number(0/1).
	        //Therefore the number must be from 0 to 7, 8 possibilities in total.
	        //So that the server can directly get the corresponding text from content array for the client.
            toServer.println(mode == 0 ? (2*jNumbers[jPosition++] + mode) : (2*pNumbers[pPosition++] + mode));
            toServer.flush();
            //insert the name into the content.
            content = fromServer.readLine().split("#");
            c = content[0] + userName + content[1];
            //print things out!
            if (c != null) System.out.println(c + "\n");
            //close the Socket
            s.close();
        } catch (IOException ioe) {ioe.printStackTrace();}
    }
}
