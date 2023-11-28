package SEPee.server.model;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.HelloClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class ChatServer {
    private static final int PORT = 8888;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server wurde gestartet. Warte auf Verbindungen...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Neue potentielle Verbindung: " + clientSocket);

                // Sende HelloClient an den Client
                HelloClient helloClient = new HelloClient("Version 0.1");
                String serializedHelloClient = Serialisierer.serialize(helloClient);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println(serializedHelloClient);

                // Empfange Antwort vom Client
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientResponse = reader.readLine();

                // Überprüfe die Antwort des Clients
                if ("OK".equals(clientResponse)) {
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } else {
                    System.out.println("Verbindung abgelehnt. Client verwendet falsches Protokoll.");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
