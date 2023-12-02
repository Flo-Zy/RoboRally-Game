package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
//import SEPee.serialisierung.messageType.HelloClient;
//import SEPee.serialisierung.messageType.HelloServer;
//import SEPee.serialisierung.messageType.Welcome;
//import SEPee.serialisierung.messageType.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server extends Thread{
    private static final int PORT = 8887;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static int idCounter = 1;

    private static int clientID;

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
                String serializedHelloServer = reader.readLine();
                System.out.println(serializedHelloServer);
                HelloServer deserializedHelloServer = Deserialisierer.deserialize(serializedHelloServer, HelloServer.class);

                if ("Version 0.1".equals(deserializedHelloServer.getMessageBody().getProtocol())) {
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                    System.out.println("Verbindung erfolgreich. Client verbunden: " + clientSocket);
                    //welcome erstellen und an den Client schicken
                    clientID = assigningClientID();
                    Welcome welcome = new Welcome(clientID);
                    String serializedWelcome = Serialisierer.serialize(welcome);
                    writer.println(serializedWelcome);
                } else {
                    System.out.println("Verbindung abgelehnt. Client verwendet falsches Protokoll.");
                    clientSocket.close();
                }
                //System.out.println("1");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized int assigningClientID(){
        int assignedClientID = idCounter;
        idCounter++;
        return assignedClientID;
    }

    public static int getClientID() {
        return clientID;
    }
}
