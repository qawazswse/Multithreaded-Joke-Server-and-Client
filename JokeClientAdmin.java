/*--------------------------------------------------------

1. Name / Date:	
	Shibo Zhang / 9/28/2019
	
2. Java version used:
	Java 1.8
	
3. Precise command-line compilation examples / instructions:
	> javac JokeClientAdmin.java

4. Precise examples / instructions to run this program:
	> java JokeServer
	> java JokeClient
	> java JokeClientAdmin

5. List of files needed for running the program.
	a. JokeServer.java
	b. JokeClient.java
	c. JokeClientAdmin.java

6. Notes:
	Cannot shut down the server or itself.

----------------------------------------------------------*/
import java.io.*;
import java.net.*;

public class JokeClientAdmin {
	public static void main(String[] args) {
		String serverName;
		if(args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];

		System.out.println("Shibo Zhang's JokeClientAdmin.");
		System.out.println("Using server: " + serverName + ", Port: 5050");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try{
			String command;
			do{
				System.out.println("COMMANDS: proverb (proverb mode) | joke (joke mode) | shutdown (shut server down)");
				System.out.flush();
				
				command = in.readLine();	//get a command from administrator
				boolean wrongCommand = true;
				while(wrongCommand) {	//check if the administrator type a right command.
					if (command.equals("joke") || command.equals("proverb") || command.equals("shutdown")) {
						wrongCommand = false;
						sendCommand(command, serverName);	//send the command to server.
					}
					else {
						System.out.println("Please type \"proverb\" or \"joke\" or \"shutdown\" :");
						command = in.readLine();	//if it's not a right command, type again.
					}
				}
			} while (!command.equals("shutdown"));
			System.out.println("Cancelled by user request.");
		} catch (IOException x ) {x.printStackTrace();}
	}

	static void sendCommand (String command, String serverName) {
		Socket s;
		BufferedReader fromServer;
		PrintStream toServer;

		try{
			s = new Socket(serverName, 5050);
			fromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			toServer = new PrintStream(s.getOutputStream());
			
			toServer.println(command); 	//send command to server.
			toServer.flush();

			System.out.println(fromServer.readLine());	//print feed back from server.
				
			s.close();	//close the Socket.

		} catch (IOException x) {x.printStackTrace ();}
	}
}