//-------------------------------------------------------------
// Socket Connector
// By Aidan Maycock, based on code by Bill Pashby
//
// Basic socket communication program used for connecting to
// an MSP 430 using an IOT device.
//-------------------------------------------------------------

import java.util.Scanner;
import java.io.*;
import java.net.Socket;

public class SocketConnector implements Runnable 
{
	// Declaration of socket object variables.
	Socket s = null;
	DataOutputStream dos;
	DataInputStream dis;
	
	// Send and Receive variables, these could potentially be used to pass messages to and from a GUI.
	String received = "";
	String sending = "";
	
	// Current input interface (terminal) object, could easily be made into a GUI or an app interface.
	Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) 
	{	
		// Invokes program code in a non-static context.
		SocketConnector sc = new SocketConnector();
	}
	
	@SuppressWarnings("deprecation")
	public SocketConnector()
	{
		// Port and IP constants.
		String remote_ip = "";
		int port = 21;
		
		// Communication standard constants, ideally these will not be needed eventually.
		char indichar = '^';
		int code = 1997;
		
		
		System.out.println("Socket Connector has Started");
		System.out.println("You will be prompted for the IP of\nthe socket device you would like to connect to.\n\n");
		System.out.println("At this point you may send commands to the IOT device.\n\n");
		
		// Requests IP of IOT device, should be replaced by grabbing a string from the app interface.
		System.out.print("Remote IP: ");
		remote_ip = scan.next();
		
		// Initializes socket connection.
		try
        {
            s = new Socket(remote_ip, port);
            System.out.println("Connected to the socket.");
        }
        catch (Exception e)
        {
            System.out.println("Could not connect to the socket.");
            //e.printStackTrace();
            return;
        }
		
		// After successful connection established, sets up I/O objects.
		try
		{
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		}
		catch (IOException e)
		{
			System.out.println("Connection Issue");
			//e.printStackTrace();
			return;
		}
		
		// Initializes receive thread.
		Thread t = new Thread(this);
        t.start();
        
        // Loop for requesting user input, can be made to send data from an app interface.
        while (true)
        {
        	try
        	{
        		// Needs to be modified for use with GUI.
        		System.out.println("\nInput: ");
        		sending = scan.next();
				dos.writeUTF(Character.toString(indichar) + Integer.toString(code) + sending + "\r\n");
        	}
        	catch (IOException e) 
			{
				System.out.println("Host Disconnected");
				break;
				//e.printStackTrace();
			}
        }
        
        // Closes object connections on host disconnection.
        try {
			dos.close();
			dis.close();
			s.close();
			scan.close();
		} catch (IOException e) {
			// Either the program should break before getting here, or something went absurdly wrong.
			System.out.println("Everything is Broken");
			e.printStackTrace();
		}
        
        t.stop();
        
	}
	
	public void run()
	{
		// Receive thread.
		while (true)
		{
			try 
			{
				received = (String)dis.readUTF();
				
				if (!received.equals(""))
				{
					// This is where GUI interaction and math should occur.
					System.out.println("Received Message: " + received);
					
					received = "";
				}
			}
			catch (IOException e) 
			{
				System.out.println("Host Disconnected");
				break;
				//e.printStackTrace();
			}
		}
		
		// Closes object connections on host disconnection.
        try {
			dos.close();
			dis.close();
			s.close();
			scan.close();
		} catch (IOException e) {
			// Either the program should break before getting here, or something went absurdly wrong.
			System.out.println("Everything is Broken");
			e.printStackTrace();
		}
	}

}
