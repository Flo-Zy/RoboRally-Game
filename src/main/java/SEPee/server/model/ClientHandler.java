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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int clientId;
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
                        clientId = Server.getClientID();
                        Server.getPlayerList().add(new Player(playerName, clientId, playerFigure));
                        //playerAdded senden an alle Clients
                        PlayerAdded playerAdded = new PlayerAdded(clientId, playerName, playerFigure);
                        String serializedPlayerAdded = Serialisierer.serialize(playerAdded);
                        broadcast(serializedPlayerAdded);

                        //send Playerlist to new Player
                        GivePlayerList givePlayerList= new GivePlayerList(Server.getPlayerList());
                        String serializedGivePlayerList = Serialisierer.serialize(givePlayerList);
                        sendToOneClient(clientId, serializedGivePlayerList);
                        break;
                    case "SetStatus":
                        System.out.println("Set Status");
                        SetStatus setStatus = Deserialisierer.deserialize(serializedReceivedString, SetStatus.class);
                        //playerList vom Server aktualisieren
                        for(int i = 0; i < Server.getPlayerList().size(); i++){
                            if(setStatus.getMessageBody().getClientID() == Server.getPlayerList().get(i).getId()){
                                Server.getPlayerList().get(i).setReady(setStatus.getMessageBody().isReady());
                            }
                        }

                        //PlayerStatus an alle Clients senden
                        PlayerStatus playerStatus = new PlayerStatus(setStatus.getMessageBody().getClientID(), setStatus.getMessageBody().isReady());
                        String serializedPlayerStatus = Serialisierer.serialize(playerStatus);
                        broadcast(serializedPlayerStatus);

                        //ersten der ready drückt selectMap senden
                        if(Server.counterSetStatus == 0) {
                            int first = setStatus.getMessageBody().getClientID();
                            SelectMap selectMap = new SelectMap();
                            String serializedSelectMap = Serialisierer.serialize(selectMap);
                            sendToOneClient(first, serializedSelectMap);
                            Server.counterSetStatus++;
                        }
                        break;
                    case "MapSelected":
                        System.out.println("Map Selected");
                        break;
                    case "SendChat":
                        System.out.println("Send Chat");

                        SendChat receivedSendChat = Deserialisierer.deserialize(serializedReceivedString, SendChat.class);

                        String receivedSendChatMessage = receivedSendChat.getMessageBody().getMessage();

                        int receivedSendChatFrom = receivedSendChat.getMessageBody().getClientID(); //darf nicht getclientid sein muss checken von wem die message kommt und nincht in der sendchat
                        int receivedSendChatTo = receivedSendChat.getMessageBody().getTo();

                        boolean receivedChatisPrivate;

                        if (receivedSendChatTo == -1){
                            receivedChatisPrivate = false;
                            ReceivedChat receivedChat = new ReceivedChat(receivedSendChatMessage,receivedSendChatFrom, receivedChatisPrivate);

                            String serializedReceivedChat = Serialisierer.serialize(receivedChat);
                            broadcast(serializedReceivedChat);

                        } else {
                            receivedChatisPrivate = true;
                            ReceivedChat receivedChat = new ReceivedChat(receivedSendChatMessage,receivedSendChatFrom, receivedChatisPrivate);

                            String serializedReceivedChat = Serialisierer.serialize(receivedChat);
                            sendToOneClient(receivedSendChatTo, serializedReceivedChat);
                        }



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

    public void sendToOneClient(int clientId, String serializedObject) {
        for (ClientHandler client : clients) {
            if (client.getClientId() == clientId) {
                // Found the target client, send the message to its socket
                client.writer.println(serializedObject);
                return; // Exit the loop after sending the message
            }
        }
        // If the loop completes and the target client is not found, you may handle it accordingly.
        System.out.println("Client with ID " + clientId + " not found.");
    }


}