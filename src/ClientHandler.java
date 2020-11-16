import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

// ClientHandler class
class ClientHandler implements Runnable {
    private String name;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final Socket socket;
    private boolean isLoggedIn;

    // constructor 
    public ClientHandler(Socket socket, String name,
                         DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        Server.clientsConnected++;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = socket;
        this.isLoggedIn = true;
    }

    private void welcomeMessage() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.lineSeparator();

        stringBuilder.append("Hello ").append(name).append(newLine);
        stringBuilder.append("Welcome to this chat program").append(newLine);
        stringBuilder.append("There are currently ").append(Server.clientsConnected).append(" clients online").append(newLine);
        stringBuilder.append("Chat syntax: \"your_message#client_name\"").append(newLine);
        stringBuilder.append("Available commands: /name, /welcome, /logout").append(newLine);
        stringBuilder.append("The following clients are currently connected: ").append(Server.clientHandlerVector.toString()).append(newLine);
        stringBuilder.append("pleeease enjoy your staaay").append(newLine);

        Server.clientHandlerVector.get(Server.clientHandlerVector.indexOf(this)).dataOutputStream.writeUTF(stringBuilder.toString());
    }

    @Override
    public void run() {
        String textFromClient;

        try {
            welcomeMessage();

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            try {
                // receive the string 
                textFromClient = dataInputStream.readUTF();

                System.out.println(textFromClient);

                if(textFromClient.equals("/logout")) {
                    this.isLoggedIn = false;
                    this.socket.close();
                    break;
                }

                if(textFromClient.equals("/name")) {
                    textFromClient = dataInputStream.readUTF();

                    name = textFromClient;

                    int thisClientIndexInVector = Server.clientHandlerVector.indexOf(this);
                    Server.clientHandlerVector.get(thisClientIndexInVector).name = textFromClient;
                    continue;
                }

                if(textFromClient.equals("/welcome")) {
                    welcomeMessage();
                    continue;
                }

                // todo: error handling ift. stringTokenizer pattern
                // break the string into message and recipient part 
                StringTokenizer stringTokenizer = new StringTokenizer(textFromClient, "/");
                String msgToSend = stringTokenizer.nextToken();
                String recipient = stringTokenizer.nextToken();

                // search for the recipient in the connected devices list.
                for (ClientHandler clientHandler : Server.clientHandlerVector) {
                    // if the recipient is found, write on its output stream
                    if (clientHandler.name.equals(recipient) && clientHandler.isLoggedIn) {
                        clientHandler.dataOutputStream.writeUTF(this.name+ ": " + msgToSend);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // decrement connected clients when ClientHandler stops running
        Server.clientsConnected--;

        try {
            // closing resources
            this.dataInputStream.close();
            this.dataOutputStream.close();

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}