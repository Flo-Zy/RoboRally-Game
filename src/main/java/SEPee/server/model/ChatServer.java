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
import java.util.List;

public class ChatServer {
    private static final int PORT = 8887;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int idCounter = 1;

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
                    //erstelle den Spieler auf Client Seite bevor du die ID zurückschickst
                    Welcome welcome = new Welcome(assigningClientID());
                    String serializedWelcome = Serialisierer.serialize(welcome);
                    writer.println(serializedWelcome);
                } else {
                    System.out.println("Verbindung abgelehnt. Client verwendet falsches Protokoll.");
                    clientSocket.close();
                }
                System.out.println("1");
                String serializedReceivedString = reader.readLine();
                Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedString, Message.class);
                String input = deserializedReceivedString.getMessageType();
                System.out.println("2");
                switch (input) {
                    //HelloServer wird oben behandelt beim Verbindungsaufbau
                    case "Alive":
                        System.out.println("Alive");
                        continue;
                    case "PlayerValues":
                        System.out.println("Player Values");
                        continue;
                    case "SetStatus":
                        System.out.println("Set Status");
                        continue;
                    case "MapSelected":
                        System.out.println("Map Selected");
                        continue;
                    case "SendChat":
                        System.out.println("Send Chat");
                        continue;
                    case "PlayCard":
                        System.out.println("Play Card");
                        continue;
                    default:
                        //Error-JSON an Client
                        System.out.println("Unknown command");
                        break;
                }
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
}
