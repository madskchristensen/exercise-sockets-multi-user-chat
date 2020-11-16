// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/

import java.io.*;
import java.util.*;
import java.net.*;

// Server class 
public class Server {

    // Vector to store active clients 
    static Vector<ClientHandler> clientHandlerVector = new Vector<>();

    // counter for clients 
    static int clientsConnected = 0;

    public static void main(String[] args) throws IOException
    {
        // server is listening
        ServerSocket ss = new ServerSocket(6666);

        // running infinite loop for getting 
        // client request 
        while (true)
        {
            // Accept the incoming request
            Socket s = ss.accept();

            System.out.println("New client request received : " + s);

            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request. 
            ClientHandler clientHandler = new ClientHandler(s,"client " + (clientsConnected + 1), dis, dos);

            // Create a new Thread with this object. 
            Thread thread = new Thread(clientHandler);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list 
            clientHandlerVector.add(clientHandler);

            // start the thread. 
            thread.start();
        }
    }
} 