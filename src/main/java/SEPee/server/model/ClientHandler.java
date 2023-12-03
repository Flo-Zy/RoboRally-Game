package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import com.google.gson.Gson;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import SEPee.server.model.Game;

import static SEPee.server.model.Server.clientID;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter writer;
    private ArrayList<Player> playerList;

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
                        playerList.add(new Player(playerName, Server.getClientID(), playerFigure));
                        System.out.println(Server.getClientID());

                        /*System.out.println(deserializedPlayerValues.getMessageBody().getName()+
                                "\n"+deserializedPlayerValues.getMessageBody().getFigure());*/

                        /*PlayerAdded playerAdded = new PlayerAdded(1, "Hasan",4);
                        String serializedPlayerAdded = Serialisierer.serialize(playerAdded);
                        writer.println(serializedPlayerAdded);*/
                        break;
                    case "SetStatus":
                        System.out.println("Set Status");
                        break;
                    case "MapSelected":
                        System.out.println("Map Selected");
                        break;
                    case "SendChat":
                        System.out.println("Send Chat");

                        SendChat receivedSendChat = Deserialisierer.deserialize(serializedReceivedString, SendChat.class);

                        String receivedSendChatMessage = receivedSendChat.getMessageBody().getMessage();

                        int receivedSendChatFrom = Server.getClientID();
                        int receivedSendChatTo = receivedSendChat.getMessageBody().getTo();

                        boolean receivedChatisPrivate;

                        if (receivedSendChatTo == -1){
                            receivedChatisPrivate = false;

                        } else {
                            receivedChatisPrivate = true;
                        }


                        ReceivedChat receivedChat = new ReceivedChat(receivedSendChatMessage,receivedSendChatFrom, receivedChatisPrivate);

                        String serializedReceivedChat = Serialisierer.serialize(receivedChat);
                        writer.println(serializedReceivedChat);

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

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.writer.println(message);
        }
    }
}