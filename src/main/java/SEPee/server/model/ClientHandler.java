package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.card.Card;
import SEPee.server.model.gameBoard.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Error;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static SEPee.server.model.Player.*;

@Getter
@Setter


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int clientId;
    private List<ClientHandler> clients;
    private PrintWriter writer;
    private Player player;

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String serializedReceivedString;
            String playerName = null;
            try {
                while ((serializedReceivedString = reader.readLine()) != null) {
                    Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedString, Message.class);
                    String input = deserializedReceivedString.getMessageType();
                    switch (input) {
                        //HelloServer wird oben behandelt beim Verbindungsaufbau
                        case "Alive":
                            System.out.println("Alive");
                            break;
                        case "PlayerValues":
                            System.out.println("Player Values erhalten");

                            String serializedPlayerValues = serializedReceivedString;
                            PlayerValues deserializedPlayerValues = Deserialisierer.deserialize(serializedPlayerValues, PlayerValues.class);
                            playerName = deserializedPlayerValues.getMessageBody().getName();
                            int playerFigure = deserializedPlayerValues.getMessageBody().getFigure();


                            for(int i = 0; i < Server.getPlayerList().size(); i++){
                                if(playerFigure == Server.getPlayerList().get(i).getFigure()){
                                    clientId = Server.getPlayerList().get(i).getId();
                                }
                            }
                            PlayerAdded playerAdded = new PlayerAdded(clientId, playerName, playerFigure);
                            associateSocketWithId(clientSocket, clientId);

                            this.player = new Player(playerName, clientId, playerFigure);

                            String serializedPlayerAdded = Serialisierer.serialize(playerAdded);

                            //playerAdded senden an alle alten Clients
                            broadcast(serializedPlayerAdded);

                            ReceivedChat joinedPlayerMessage = new ReceivedChat(playerName + " has joined the chat.", 999, false);
                            String serializedjoinedPlayerMessage = Serialisierer.serialize(joinedPlayerMessage);
                            broadcast(serializedjoinedPlayerMessage);

                            break;
                        case "SetStatus":
                            System.out.println("Set Status");
                            SetStatus setStatus = Deserialisierer.deserialize(serializedReceivedString, SetStatus.class);
                            Server.addReady(player.getId());
                            //playerList vom Server aktualisieren
                            for (int i = 0; i < Server.getPlayerList().size(); i++) {
                                if (player.getId() == Server.getPlayerList().get(i).getId()) {
                                    Server.getPlayerList().get(i).setReady(setStatus.getMessageBody().isReady());
                                }
                            }
                            //PlayerStatus an alle Clients senden
                            PlayerStatus playerStatus = new PlayerStatus(player.getId(), setStatus.getMessageBody().isReady());
                            String serializedPlayerStatus = Serialisierer.serialize(playerStatus);
                            broadcast(serializedPlayerStatus);


                            //ersten der ready drückt selectMap senden
                            if (Server.getReadyList().size() == 1) {
                                Server.setGameMap(null);
                                Server.setFirstReady(Server.getReadyList().get(Server.getReadyListIndex()));
                                Server.setReadyListIndex(Server.getReadyListIndex()+1);
                                SelectMap selectMap = new SelectMap();
                                String serializedSelectMap = Serialisierer.serialize(selectMap);
                                sendToOneClient(Server.getFirstReady(), serializedSelectMap);
                            }
                            if(!setStatus.getMessageBody().isReady() && player.getId() == Server.getFirstReady()){

                                if(checkNumReady() == 0){
                                    Server.setGameMap(null);
                                    Server.getReadyList().clear();
                                    Server.setReadyListIndex(0);
                                }else {
                                    Server.setGameMap(null);
                                    int nextId = checkNextReady();
                                    Server.setFirstReady(nextId);
                                    Server.setReadyListIndex(Server.getReadyListIndex()+1);
                                    SelectMap selectMap = new SelectMap();
                                    String serializedSelectMap = Serialisierer.serialize(selectMap);
                                    sendToOneClient(Server.getFirstReady(), serializedSelectMap);
                                }
                            }
                            break;
                        case "MapSelected":
                            System.out.println("Map Selected");
                            MapSelected mapSelected = Deserialisierer.deserialize(serializedReceivedString, MapSelected.class);
                            if(!mapSelected.getMessageBody().getMap().equals("")) {
                                broadcast(serializedReceivedString);
                                //speicher die gewählte map (die gameMap)
                                if (Server.getFirstReady() == player.getId())
                                    switch (mapSelected.getMessageBody().getMap()) {
                                        case "DizzyHighway":
                                            DizzyHighway dizzyHighway = new DizzyHighway();
                                            Server.setGameMap(dizzyHighway.getGameBoard());
                                            break;
                                    /*Später für weitere Maps
                                    case " ":

                                        break;
                                     */
                                    }

                            }
                            //check alle ready und mind 2
                            if(!Server.isGameStarted() && checkNumReady() >= 2 && checkNumReady() == Server.getPlayerList().size()
                                    && Server.getGameMap() != null){
                                Server.setGameStarted(true);
                                //erstelle das Spiel
                                Server.setGame(new Game(Server.getPlayerList(), Server.getGameMap()));
                                //Sende an alle Clients Spiel wird gestarted
                                GameStarted gameStarted = new GameStarted(Server.getGameMap());
                                String serializedGameStarted = Serialisierer.serialize(gameStarted);
                                broadcast(serializedGameStarted);
                                System.out.println("Das Spiel wird gestartet");

                                ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                String serializedActivePhase = Serialisierer.serialize(activePhase);
                                broadcast(serializedActivePhase);

                                CurrentPlayer currentplayer = new CurrentPlayer(Server.getGame().getCurrentPlayer().getId());
                                String serializedCurrentPlayer = Serialisierer.serialize(currentplayer);
                                broadcast(serializedCurrentPlayer);
                            }
                            break;
                        case "SendChat":
                            System.out.println("Send Chat");
                            SendChat receivedSendChat = Deserialisierer.deserialize(serializedReceivedString, SendChat.class);
                            String receivedSendChatMessage = receivedSendChat.getMessageBody().getMessage();

                            int receivedSendChatFrom = clientId;
                            int receivedSendChatTo = receivedSendChat.getMessageBody().getTo();

                            boolean receivedChatisPrivate;
                            if (receivedSendChatTo == -1) {
                                receivedChatisPrivate = false;
                                ReceivedChat receivedChat = new ReceivedChat(receivedSendChatMessage, receivedSendChatFrom, receivedChatisPrivate);
                                String serializedReceivedChat = Serialisierer.serialize(receivedChat);

                                broadcast(serializedReceivedChat);
                            } else {
                                receivedChatisPrivate = true;
                                ReceivedChat receivedChat = new ReceivedChat(receivedSendChatMessage, receivedSendChatFrom, receivedChatisPrivate);
                                String serializedReceivedChat = Serialisierer.serialize(receivedChat);

                                sendToOneClient(receivedSendChatTo, serializedReceivedChat);

                                // verhindert doppeltes ausgeben, falls privatnachricht an sich selbst geschickt wird
                                if (!(receivedSendChatTo == receivedSendChatFrom)) {
                                    sendToOneClient(receivedSendChatFrom, serializedReceivedChat);
                                }
                            }
                            break;
                        case "PlayCard":
                            System.out.println("Play Card");
                            PlayCard playCard = Deserialisierer.deserialize(serializedReceivedString, PlayCard.class);
                            break;
                        case "SetStartingPoint":
                            Server.setCountPlayerTurns(Server.getCountPlayerTurns()+1);
                            System.out.println("Set Starting Points");
                            SetStartingPoint setStartingPoint = Deserialisierer.deserialize(serializedReceivedString, SetStartingPoint.class);

                            StartingPointTaken startingPointTaken = new StartingPointTaken(setStartingPoint.getMessageBody().getX(), setStartingPoint.getMessageBody().getY(), clientId);
                            System.out.println("StartingPointTaken - X: " + setStartingPoint.getMessageBody().getX() + ", Y: " + setStartingPoint.getMessageBody().getY() + ", ClientID: " + clientId);

                            String serializedStartingPointTaken = Serialisierer.serialize(startingPointTaken);
                            broadcast(serializedStartingPointTaken);

                            if(Server.getCountPlayerTurns() == Server.getGame().getPlayerList().size()) { // wenn alle spieler in der aktuellen Phase dran waren -> gehe in nächste Phase
                                Server.getGame().nextCurrentPhase(); // wenn Phase 2 (Programming Phase): jeder player bekommt progDeck in seine playerMat
                                Server.setCountPlayerTurns(0);  // in neuer Phase: keiner dran bisher

                                // wenn currentPhase = 2 : YourCards (schicke jedem Client zuerst sein progDeck)
                                if (Server.getGame().getCurrentPhase() == 2) {
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        ArrayList<String> clientCards = new ArrayList<>(); // 9 KartenNamen
                                        int i=0;
                                        while (i<9) {
                                            // die ersten 9 karten ziehen
                                            Card card = player.getPlayerMat().getProgDeck().get(0);
                                            clientCards.add(card.getName());
                                            // die ersten 9 karten vom progDeck des player.getPlayerMat() entfernen
                                            player.getPlayerMat().getProgDeck().remove(0);
                                            i++;
                                        }
                                        // sendet Karten Infos an aktuellen player
                                        YourCards yourCards = new YourCards(clientCards);
                                        String serializedYourCards = Serialisierer.serialize(yourCards);
                                        System.out.println("Test: " + player.getId() + ", " + serializedYourCards);
                                        // sende an diesen Client sein ProgDeck
                                        sendToOneClient(player.getId(), serializedYourCards);

                                        // sendet Karten Infos an alle anderen player
                                        NotYourCards notYourCards = new NotYourCards(player.getId(), clientCards.size());
                                        String serializedNotYourCards = Serialisierer.serialize(notYourCards);
                                        for(int j = 0; j < Server.getGame().getPlayerList().size(); j++){
                                            if(Server.getGame().getPlayerList().get(j).getId() != player.getId()){
                                                sendToOneClient(Server.getGame().getPlayerList().get(j).getId(), serializedNotYourCards);
                                            }
                                        }
                                    }
                                }

                                // Aktive Spielphase setzen
                                ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                String serializedActivePhase = Serialisierer.serialize(activePhase);
                                broadcast(serializedActivePhase);

                                /*
                                // tester block für movement case
                                Movement movement = new Movement(2, 0,0);
                                String serializedMovement = Serialisierer.serialize(movement);
                                broadcast(serializedMovement);
                                */

                            } else {
                                Server.getGame().setNextPlayersTurn(); // setzt im game Objekt des Servers den currentPlayer, der (abhg. von Phase) dran ist
                            }

                            CurrentPlayer currentplayer = new CurrentPlayer(Server.getGame().getCurrentPlayer().getId());
                            String serializedCurrentPlayer = Serialisierer.serialize(currentplayer);
                            broadcast(serializedCurrentPlayer);

                            break;
                        case "SelectedCard":
                            System.out.println("Selected Card");
                            SelectedCard selectedCard = Deserialisierer.deserialize(serializedReceivedString, SelectedCard.class);
                            break;
                        case "SelectionFinished":
                            System.out.println("Selection Finished");
                            SelectionFinished selectionFinished = Deserialisierer.deserialize(serializedReceivedString, SelectionFinished.class);
                            break;
                        case "SelectedDamage":
                            System.out.println("Selected Damage");
                            SelectedDamage selectedDamage = Deserialisierer.deserialize(serializedReceivedString, SelectedDamage.class);
                            break;
                        default:
                            //Error-JSON an Client
                            //System.out.println("Unknown command");
                            Error error = new Error("Whoops. That did not work. Try to adjust something.");
                            String serializedError = Serialisierer.serialize(error);
                            writer.println(serializedError);
                            break;
                    }
                }
            } catch (SocketException e) {
                // Handle clientseitiger close
                System.out.println("Client disconnected: " + e.getMessage());
                //wer disconnect

                ReceivedChat leftPlayerMessage = new ReceivedChat(playerName + " has left the chat.", 999, false);
                String serializedLeftPlayerMessage = Serialisierer.serialize(leftPlayerMessage);
                broadcast(serializedLeftPlayerMessage);


                //falls das der fall ist, was dann?

            } catch (IOException e) {
                e.printStackTrace(); // Other IO exceptions can be handled separately if needed
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

    public int checkNumReady(){
        int numReady = 0;
        for(int i = 0; i < Server.getPlayerList().size(); i++){
            if(Server.getPlayerList().get(i).isReady()){
                numReady++;
            }
        }
        return numReady;
    }

    public int checkNextReady(){
        int x = 0;
        if(Server.getReadyListIndex() < Server.getReadyList().size()) {
            for (int i = 0; i < Server.getPlayerList().size(); i++) {
                if (Server.getPlayerList().get(i).getId() == Server.getReadyList().get(Server.getReadyListIndex())
                        && !Server.getPlayerList().get(i).isReady()) {
                    Server.setReadyListIndex(Server.getReadyListIndex()+1);
                    x = checkNextReady();
                } else {
                    x = Server.getReadyList().get(Server.getReadyListIndex());
                }
            }
        }
        return x;
    }


}