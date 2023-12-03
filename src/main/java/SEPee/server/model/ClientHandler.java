package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.Message;
import SEPee.serialisierung.messageType.PlayerAdded;
import SEPee.serialisierung.messageType.PlayerValues;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import SEPee.server.model.Game;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter writer;


    public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;

        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String serializedReceivedString;
            while ((serializedReceivedString = reader.readLine()) != null) {
                Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedString, Message.class);
                String input = deserializedReceivedString.getMessageType();
                //System.out.println("2");
                switch (input) {
                    //HelloServer wird oben behandelt beim Verbindungsaufbau
                    case "Alive":
                        System.out.println("Alive");
                        break;
                    case "PlayerValues":
                        System.out.println("Player Values erhalten");
                        String serializedPlayerValues = serializedReceivedString;
                        PlayerValues deserializedPlayerValues = Deserialisierer.deserialize(serializedPlayerValues, PlayerValues.class);
                        String playerName = deserializedPlayerValues.getMessageBody().getName();
                        int playerFigure = deserializedPlayerValues.getMessageBody().getFigure();
                        //speichert Spielerobjekt in playerList im Server
                        Server.getPlayerList().add(new Player(playerName, Server.getClientID(), playerFigure));
                        break;
                    case "SetStatus":
                        System.out.println("Set Status");
                        break;
                    case "MapSelected":
                        System.out.println("Map Selected");
                        break;
                    case "SendChat":
                        System.out.println("Send Chat");
                        break;
                    case "PlayCard":
                        System.out.println("Play Card");
                        break;
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

    private void broadcast(String serializedObjectToSend) {
        for (ClientHandler client : clients) {
            client.writer.println(serializedObjectToSend);
        }
    }
}