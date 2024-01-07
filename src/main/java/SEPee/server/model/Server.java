package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.field.Field;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
public class Server extends Thread{
    private static final int PORT = 8886;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static int idCounter = 1;
    @Getter
    private static int clientID;
    @Getter
    private static ArrayList<Player> playerList = new ArrayList<>();
    @Getter
    @Setter
    private static ArrayList<Integer> readyList = new ArrayList<>();
    @Getter
    @Setter
    private static int readyListIndex = 0;
    @Getter
    @Setter
    private static GameBoard gameMap;
    @Getter
    @Setter
    private static int firstReady;
    @Getter
    @Setter
    private static boolean gameStarted = false;
    @Getter
    @Setter
    private static Game game;
    @Getter
    @Setter
    private static int countPlayerTurns = 0; // wie viele Player waren in aktueller Phase schon dran
    @Getter
    @Setter
    private static int timerSend = 0;
    @Getter
    @Setter
    private static boolean waitForDamage = false;
    @Getter
    @Setter
    private static int registerCounter = 0;
    @Getter
    @Setter
    private static int selectedDamageCounter = 0;
    @Getter
    @Setter
    private static int numPickDamage = 0;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server wurde gestartet. Warte auf Verbindungen...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Neue potentielle Verbindung: " + clientSocket);
                // Sende HelloClient an den Client
                HelloClient helloClient = new HelloClient("Version 1.0");
                String serializedHelloClient = Serialisierer.serialize(helloClient);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println(serializedHelloClient);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Empfange Antwort vom Client
                String serializedHelloServer = reader.readLine();
                System.out.println(serializedHelloServer);
                HelloServer deserializedHelloServer = Deserialisierer.deserialize(serializedHelloServer, HelloServer.class);
                if (deserializedHelloServer != null && "Version 1.0".equals(deserializedHelloServer.getMessageBody().getProtocol())) {
                    clientID = assigningClientID();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients, clientID);
                    clients.add(clientHandler);
                    //associate socket with ID in the Player object
                    Player.associateSocketWithId(clientSocket, clientID);

                    System.out.println("101 server: " + Player.getClientIdFromSocket(clientSocket));

                    new Thread(clientHandler).start();
                    System.out.println("Verbindung erfolgreich. Client verbunden: " + clientSocket);
                    //welcome erstellen und an den Client schicken

                    //alive message sender erst raus weil stört unnormal.
                    // new Thread(new AliveMessageSender()).start();

                    System.out.println(clientID);
                    playerList.add(new Player("", clientID, -9999));
                    Welcome welcome = new Welcome(clientID);
                    String serializedWelcome = Serialisierer.serialize(welcome);
                    writer.println(serializedWelcome);

                    for(Player player : playerList){
                        PlayerAdded playerAdded = new PlayerAdded(player.getId(), player.getName(), player.getFigure()-1);
                        String serializedPlayerAdded = Serialisierer.serialize(playerAdded);
                        clientHandler.sendToOneClient(clientID, serializedPlayerAdded);
                    }
                } else {
                    System.out.println("Verbindung abgelehnt. Client verwendet falsches Protokoll.");
                    clientSocket.close();

                    //FEHLERMELDUNG BEHEBEN socket muss richtig geschlossen werden
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class AliveMessageSender implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    sendAliveMessages();
                    Thread.sleep(5000);  // Warte 5 Sekunden
                }
            } catch (InterruptedException e) {
                System.out.println("AliveMessageSender wurde unterbrochen");
            }
        }
    }

    private static void sendAliveMessages() {
        synchronized (clients) {
            for (ClientHandler clientHandler : clients) {
                clientHandler.sendAliveMessage();
            }
        }
    }

    public static synchronized int assigningClientID(){
        int assignedClientID = idCounter;
        idCounter++;
        return assignedClientID;
    }

    public static void addReady(int id){
        readyList.add(id);
    }
}
