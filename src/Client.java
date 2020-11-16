// https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 6666;

    public static void main(String[] args) throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        // getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection 
        Socket socket = new Socket(ip, ServerPort);

        // obtaining input and out streams 
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        // sendMessage thread 
        Thread threadSendMessage = new Thread(() -> {

            while (true) {

                // read the message to deliver.
                String msg = scanner.nextLine();

                try {
                    // write on the output stream
                    dataOutputStream.writeUTF(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // readMessage thread 
        Thread threadReadMessage = new Thread(() -> {

            while (true) {
                try {
                    // read the message sent to this client
                    String msg = dataInputStream.readUTF();
                    System.out.println(msg);

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        });

        threadSendMessage.start();
        threadReadMessage.start();
    }
}