package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.progCard.*;
import SEPee.server.model.field.ConveyorBelt;
import SEPee.server.model.field.Field;
import SEPee.server.model.field.*;
import SEPee.server.model.gameBoard.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Error;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.*;

import javafx.application.Platform;
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
    private Robot robot;
    private String lastPlayedCard = null;
    private ArrayList<String> clientHand;

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
                            System.out.println("Alive zurück bekommen");
                            //fehlt noch, dass wenn man kein alive zurückbekommt innerhalb 5 Sekunden, dann messageType connectionUpdate schicken
                            break;
                        case "PlayerValues":
                            System.out.println("Player Values erhalten");

                            String serializedPlayerValues = serializedReceivedString;
                            PlayerValues deserializedPlayerValues = Deserialisierer.deserialize(serializedPlayerValues, PlayerValues.class);
                            playerName = deserializedPlayerValues.getMessageBody().getName();
                            int playerFigure = deserializedPlayerValues.getMessageBody().getFigure();


                            for (int i = 0; i < Server.getPlayerList().size(); i++) {
                                if (playerFigure == Server.getPlayerList().get(i).getFigure()) {
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
                                Server.setReadyListIndex(Server.getReadyListIndex() + 1);
                                SelectMap selectMap = new SelectMap();
                                String serializedSelectMap = Serialisierer.serialize(selectMap);
                                sendToOneClient(Server.getFirstReady(), serializedSelectMap);
                            }
                            if (!setStatus.getMessageBody().isReady() && player.getId() == Server.getFirstReady()) {

                                if (checkNumReady() == 0) {
                                    Server.setGameMap(null);
                                    Server.getReadyList().clear();
                                    Server.setReadyListIndex(0);
                                } else {
                                    Server.setGameMap(null);
                                    int nextId = checkNextReady();
                                    Server.setFirstReady(nextId);
                                    Server.setReadyListIndex(Server.getReadyListIndex() + 1);
                                    SelectMap selectMap = new SelectMap();
                                    String serializedSelectMap = Serialisierer.serialize(selectMap);
                                    sendToOneClient(Server.getFirstReady(), serializedSelectMap);
                                }
                            }
                            break;
                        case "MapSelected":
                            System.out.println("Map Selected");
                            MapSelected mapSelected = Deserialisierer.deserialize(serializedReceivedString, MapSelected.class);
                            if (!mapSelected.getMessageBody().getMap().equals("")) {
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
                            if (!Server.isGameStarted() && checkNumReady() >= 2 && checkNumReady() == Server.getPlayerList().size()
                                    && Server.getGameMap() != null) {
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
                            Server.setCountPlayerTurns(Server.getCountPlayerTurns()+1);
                            System.out.println("Play Card");
                            PlayCard playCard = Deserialisierer.deserialize(serializedReceivedString, PlayCard.class);

                            // Card played für Karten Verständnis an alle clients schicken
                            String playCardCard = playCard.getMessageBody().getCard();
                            CardPlayed cardPlayed = new CardPlayed(clientId, playCardCard);
                            String serializedCardPlayed = Serialisierer.serialize(cardPlayed);

                            if(!playCardCard.equals("Again")){
                                lastPlayedCard = playCardCard;
                            }

                            //logik für karteneffekte
                            switch (lastPlayedCard) {
                                case "BackUp": //vielleicht auch Move Back steht beides in Anleitung Seite 24

                                    if (movePossibleWallBack(checkRobotField(this.robot), this.robot)) {
                                        BackUp.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " steht mit dem Rücken gegen die Wand.");
                                    }

                                    int xBackup = this.robot.getX();
                                    int yBackup = this.robot.getY();
                                    int clientIDBackup = this.clientId;

                                    Movement movementBackup = new Movement(clientIDBackup, xBackup, yBackup);
                                    String serializedMovementBackup = Serialisierer.serialize(movementBackup);
                                    Thread.sleep(750);
                                    broadcast(serializedMovementBackup);

                                    break;
                                case "MoveI":

                                    if (movePossibleWall(checkRobotField(this.robot), this.robot)) {
                                        MoveI.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                                    }

                                    int x = this.robot.getX();
                                    int y = this.robot.getY();
                                    int clientID = this.clientId;

                                    Movement movement = new Movement(clientID, x, y);
                                    String serializedMovement = Serialisierer.serialize(movement);
                                    Thread.sleep(750);
                                    broadcast(serializedMovement);
                                    break;
                                case "MoveII":

                                    if (movePossibleWall(checkRobotField(this.robot), this.robot)) {
                                        MoveI.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                                    }
                                    if (movePossibleWall(checkRobotField(this.robot), this.robot)) {
                                        MoveI.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                                    }

                                    int x2 = this.robot.getX();
                                    int y2 = this.robot.getY();
                                    int clientID2 = this.clientId;

                                    Movement movement2 = new Movement(clientID2, x2, y2);
                                    String serializedMovement2 = Serialisierer.serialize(movement2);
                                    Thread.sleep(750);
                                    broadcast(serializedMovement2);
                                    break;
                                case "MoveIII":

                                    if (movePossibleWall(checkRobotField(this.robot), this.robot)) {
                                        MoveI.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                                    }
                                    if (movePossibleWall(checkRobotField(this.robot), this.robot)) {
                                        MoveI.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                                    }
                                    if (movePossibleWall(checkRobotField(this.robot), this.robot)) {
                                        MoveI.makeEffect(this.robot);
                                    } else {
                                        System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                                    }

                                    int x3 = this.robot.getX();
                                    int y3 = this.robot.getY();
                                    int clientID3 = this.clientId;

                                    Movement movement3 = new Movement(clientID3, x3, y3);
                                    String serializedMovement3 = Serialisierer.serialize(movement3);
                                    Thread.sleep(750);
                                    broadcast(serializedMovement3);
                                    break;
                                case "PowerUp":
                                    //lastPlayedCard = "PowerUp";
                                    break;
                                case "RightTurn":
                                    //lastPlayedCard = "RightTurn";
                                    RightTurn.makeEffect(this.robot);
                                    int clientIDRightTurn = this.clientId;
                                    PlayerTurning playerTurningRight = new PlayerTurning(clientIDRightTurn, "clockwise");
                                    String serializedPlayerTurningRight = Serialisierer.serialize(playerTurningRight);
                                    Thread.sleep(750);
                                    broadcast(serializedPlayerTurningRight);
                                    break;
                                case "LeftTurn":
                                    //lastPlayedCard = "LeftTurn";
                                    LeftTurn.makeEffect(this.robot);
                                    int clientIDLeftTurn = this.clientId;
                                    PlayerTurning playerTurningLeft = new PlayerTurning(clientIDLeftTurn, "counterclockwise");
                                    String serializedPlayerTurningLeft = Serialisierer.serialize(playerTurningLeft);
                                    Thread.sleep(750);
                                    broadcast(serializedPlayerTurningLeft);
                                    break;
                                case "UTurn":
                                    //lastPlayedCard = "UTurn";
                                    UTurn.makeEffect(this.robot);
                                    int clientIDUTurn = this.clientId;
                                    PlayerTurning playerTurningUTurn = new PlayerTurning(clientIDUTurn, "clockwise");
                                    String serializedPlayerTurningUTurn = Serialisierer.serialize(playerTurningUTurn);
                                    //send twice turn by 90 degrees in order to end up turning 180 degrees
                                    Thread.sleep(750);
                                    broadcast(serializedPlayerTurningUTurn);
                                    broadcast(serializedPlayerTurningUTurn);
                                    break;
                                default:
                                    System.out.println("unknown card name");
                                    break;


                            }

                            broadcast(serializedCardPlayed);

                            // update im Server game Objekt den Roboter dieses Spielers
                            //hasans losung
                            /*
                            for(Player player : Server.getGame().getPlayerList()){
                                if(player.getId() == this.player.getId()){
                                    player.setRobot(this.robot);
                                }
                            }
                             */


                            // wenn letzter Player aus PlayerList dran ist
                            if(Server.getCountPlayerTurns() == Server.getGame().getPlayerList().size()){

                                fieldActivation(); // Belts, lasers, checkpoints.. etc.
                                Server.getGame().setNextPlayersTurn(); // setze playerIndex = 0, PlayerList mit neuen Priorities, currentPlayer = playerList.get(playerIndex), playerIndex++

                                if(Server.getRegisterCounter() <= 4) {
                                    // n. register wird an alle gesendet
                                    ArrayList<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        activeCards.add(new CurrentCards.ActiveCard(player.getId(), player.getPlayerMat().getRegister().get(Server.getRegisterCounter()))); // n. element aus register von jedem Player
                                    }

                                    discardCurrentRegister();

                                    Server.setRegisterCounter(Server.getRegisterCounter()+1);

                                    CurrentCards currentCards = new CurrentCards(activeCards);
                                    String serializedCurrentCards = Serialisierer.serialize(currentCards);
                                    broadcast(serializedCurrentCards);

                                    Server.setCountPlayerTurns(0);
                                    CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getCurrentPlayer().getId());
                                    String serializedCurrentPlayer = Serialisierer.serialize(currentPlayer);
                                    broadcast(serializedCurrentPlayer);

                                    // test
                                    for(CurrentCards.ActiveCard activeCard : currentCards.getMessageBody().getActiveCards()) {
                                        System.out.println(activeCard.getCard());
                                    }
                                } else{ // Server.getRegisterCounter() größer 4
                                    Server.setRegisterCounter(0);
                                    Server.setTimerSend(0);
                                    for(Player player : Server.getGame().getPlayerList()){
                                        player.getPlayerMat().setNumRegister(0);
                                    }
                                    Server.getGame().nextCurrentPhase();

                                    for(Player player : Server.getGame().getPlayerList()){
                                        System.out.println("Player " + player.getId() + " discardPile: " + player.getPlayerMat().getDiscardPile());
                                    }

                                    for(Player player : Server.getGame().getPlayerList()){
                                        System.out.println("Player " + player.getId() + " register: " + player.getPlayerMat().getRegister());
                                    }

                                    // entferne nachdem alle register in Phase 2 abgearbeitet wurden von jedem player das gesamte register
                                    for(Player player : Server.getGame().getPlayerList()){
                                        player.getPlayerMat().getRegister().clear();
                                    }

                                    // teste ob alle register leer
                                    for(Player player : Server.getGame().getPlayerList()){
                                        System.out.println("Player " + player.getId() + " register: " + player.getPlayerMat().getRegister());
                                    }

                                    // YourCards an Client senden wenn ActivePhase = 2
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        if(player.getPlayerMat().getProgDeck().size() >= 9){
                                            ArrayList<String> clientCards = new ArrayList<>(); // 9 KartenNamen
                                            int i = 0;
                                            while (i < 9) {
                                                // die ersten 9 karten ziehen
                                                Card card = player.getPlayerMat().getProgDeck().get(0);
                                                clientCards.add(card.getName());
                                                // die ersten 9 karten vom progDeck des player.getPlayerMat() entfernen
                                                player.getPlayerMat().getProgDeck().remove(0);
                                                i++;
                                            }
                                            player.getPlayerMat().setClientHand(clientCards);

                                            // test ob clientHand richtig gesetzt
                                            for (String card : player.getPlayerMat().getClientHand()) {
                                                System.out.println("Player " + player.getId() + ": " + card);
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
                                            for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                                                if (Server.getGame().getPlayerList().get(j).getId() != player.getId()) {
                                                    sendToOneClient(Server.getGame().getPlayerList().get(j).getId(), serializedNotYourCards);
                                                }
                                            }
                                        }else {
                                            ArrayList<String> clientCards = new ArrayList<>(); // 9 KartenNamen
                                            int leftCards = player.getPlayerMat().getProgDeck().size();
                                            int i = 0;
                                            while (i < leftCards) {
                                                Card card = player.getPlayerMat().getProgDeck().get(0);
                                                clientCards.add(card.getName());
                                                // die ersten restlichen Karten vom progDeck des player.getPlayerMat() entfernen
                                                player.getPlayerMat().getProgDeck().remove(0);
                                                i++;
                                            }
                                            player.getPlayerMat().setClientHand(clientCards);

                                            // test ob clientHand richtig gesetzt
                                            for (String card : player.getPlayerMat().getClientHand()) {
                                                System.out.println("Player " + player.getId() + ": " + card);
                                            }

                                            ShuffleCoding shuffleCoding = new ShuffleCoding(player.getId());
                                            String serializedShuffleCoding = Serialisierer.serialize(shuffleCoding);
                                            sendToOneClient(player.getId(), serializedShuffleCoding);

                                            ArrayList<Card> newDrawPile = stringToCard(player.getPlayerMat().getDiscardPile());
                                            Collections.shuffle(newDrawPile);
                                            player.getPlayerMat().setProgDeck(newDrawPile);

                                            i = 0;
                                            while (i < 9 - leftCards) {
                                                Card card = player.getPlayerMat().getProgDeck().get(0);
                                                clientCards.add(card.getName());
                                                // die restlichen Karten vom progDeck des player.getPlayerMat() entfernen
                                                player.getPlayerMat().getProgDeck().remove(0);
                                                i++;
                                            }

                                            YourCards yourCards = new YourCards(clientCards);
                                            String serializedYourCards = Serialisierer.serialize(yourCards);
                                            System.out.println("Test: " + player.getId() + ", " + serializedYourCards);
                                            // sende an diesen Client sein ProgDeck
                                            sendToOneClient(player.getId(), serializedYourCards);

                                            // sendet Karten Infos an alle anderen player
                                            NotYourCards notYourCards = new NotYourCards(player.getId(), clientCards.size());
                                            String serializedNotYourCards = Serialisierer.serialize(notYourCards);
                                            for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                                                if (Server.getGame().getPlayerList().get(j).getId() != player.getId()) {
                                                    sendToOneClient(Server.getGame().getPlayerList().get(j).getId(), serializedNotYourCards);
                                                }
                                            }

                                        }
                                    }

                                    ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                    String serializedActivePhase = Serialisierer.serialize(activePhase);
                                    broadcast(serializedActivePhase);

                                }
                            }else{ //alle spieler waren noch nicht im aktuellen register dran, nächster Spieler soll seine Karte Spielen
                                CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getPlayerList().get(Server.getCountPlayerTurns()).getId());
                                String serializedCurrentPlayer = Serialisierer.serialize(currentPlayer);
                                broadcast(serializedCurrentPlayer);
                            }

                            // Karteneffekt messagtype fur alle clients verschicken (Movement)

                            break;
                        case "SetStartingPoint":
                            Server.setCountPlayerTurns(Server.getCountPlayerTurns() + 1);
                            System.out.println("Set Starting Points");
                            SetStartingPoint setStartingPoint = Deserialisierer.deserialize(serializedReceivedString, SetStartingPoint.class);

                            StartingPointTaken startingPointTaken = new StartingPointTaken(setStartingPoint.getMessageBody().getX(), setStartingPoint.getMessageBody().getY(), clientId);
                            System.out.println("StartingPointTaken - X: " + setStartingPoint.getMessageBody().getX() + ", Y: " + setStartingPoint.getMessageBody().getY() + ", ClientID: " + clientId);
                            this.robot = new Robot(0, 0, "right");
                            this.robot.setY(setStartingPoint.getMessageBody().getY());
                            this.robot.setX(setStartingPoint.getMessageBody().getX());
                            /*
                            // hasans Lösung
                            for(Player player : Server.getGame().getPlayerList()){
                                if(player.getId() == this.player.getId()){
                                    player.setRobot(this.robot);
                                }
                            }
                             */

                            // Find the associated Player and set the Robot for that Player
                            Player associatedPlayer = Server.getGame().getPlayerList().get(clientId - 1);
                            associatedPlayer.setRobot(this.robot);

                            // Add the Game class (which implements RobotPositionChangeListener) as a listener to the Robot
                            this.robot.addPositionChangeListener(Server.getGame());

                            String serializedStartingPointTaken = Serialisierer.serialize(startingPointTaken);
                            broadcast(serializedStartingPointTaken);

                            if (Server.getCountPlayerTurns() == Server.getGame().getPlayerList().size()) { // wenn alle spieler in der aktuellen Phase dran waren -> gehe in nächste Phase
                                Server.getGame().nextCurrentPhase(); // wenn Phase 2 (Programming Phase): jeder player bekommt progDeck in seine playerMat
                                Server.setCountPlayerTurns(0);  // in neuer Phase: keiner dran bisher

                                // wenn WIEDER currentPhase = 2: YourCards (schicke jedem Client zuerst sein progDeck)
                                if (Server.getGame().getCurrentPhase() == 2) {
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        ArrayList<String> clientCards = new ArrayList<>(); // 9 KartenNamen
                                            int i = 0;
                                            while (i < 9) {
                                                // die ersten 9 karten ziehen
                                                Card card = player.getPlayerMat().getProgDeck().get(0);
                                                clientCards.add(card.getName());
                                                // die ersten 9 karten vom progDeck des player.getPlayerMat() entfernen
                                                player.getPlayerMat().getProgDeck().remove(0);
                                                i++;
                                            }
                                            player.getPlayerMat().setClientHand(clientCards);

                                            // test ob clientHand richtig gesetzt
                                            for (String card : player.getPlayerMat().getClientHand()) {
                                                System.out.println("Player " + player.getId() + ": " + card);
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
                                            for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                                                if (Server.getGame().getPlayerList().get(j).getId() != player.getId()) {
                                                    sendToOneClient(Server.getGame().getPlayerList().get(j).getId(), serializedNotYourCards);
                                                }
                                            }
                                    }
                                }

                                // Aktive Spielphase setzen (nachdem Karten in die Hands ausgeteilt wurden)
                                ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                String serializedActivePhase = Serialisierer.serialize(activePhase);
                                broadcast(serializedActivePhase);

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
                            String card = selectedCard.getMessageBody().getCard();
                            int cardRegister = selectedCard.getMessageBody().getRegister();

                            // NumRegister verringern, falls card == null
                            if (card == null) {
                                for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                    if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                        Server.getGame().getPlayerList().get(i).getPlayerMat().setNumRegister(
                                                Server.getGame().getPlayerList().get(i).getPlayerMat().getNumRegister() - 1);
                                    }
                                }
                            }
                            // NumRegister erhöhen, falls card != null
                            else {
                                for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                    if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                        Server.getGame().getPlayerList().get(i).getPlayerMat().setNumRegister(
                                                Server.getGame().getPlayerList().get(i).getPlayerMat().getNumRegister() + 1);
                                        System.out.println( Server.getGame().getPlayerList().get(i).getPlayerMat().getNumRegister());
                                    }
                                }
                            }
                            for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                // füge in register card hinzu
                                if (Server.getGame().getPlayerList().get(i).getId() == clientId && card != null) {
                                    //gelegte Karte dem Register hinzufügen
                                    Server.getGame().getPlayerList().get(i).getPlayerMat().getRegister().add(cardRegister, card);
                                    //gelegte Karte von der Hand entfernen
                                    for(int j = 0; j < Server.getGame().getPlayerList().get(i).getPlayerMat().getClientHand().size(); j++){
                                        if(card.equals(Server.getGame().getPlayerList().get(i).getPlayerMat().getClientHand().get(j))){
                                            Server.getGame().getPlayerList().get(i).getPlayerMat().getClientHand().remove(j);
                                            break;
                                        }
                                    }
                                }
                                // falls card == null: lösche letztes card aus register
                                else if (Server.getGame().getPlayerList().get(i).getId() == clientId && card == null) {
                                    //letze Karte vom register in die Hand einfügen
                                    Server.getGame().getPlayerList().get(i).getPlayerMat().getClientHand().add(
                                            Server.getGame().getPlayerList().get(i).getPlayerMat().getRegister().get(
                                                    Server.getGame().getPlayerList().get(i).getPlayerMat().getRegister().size() - 1));
                                    //letze Karte vom Register entfernen
                                    Server.getGame().getPlayerList().get(i).getPlayerMat().getRegister().remove(
                                            Server.getGame().getPlayerList().get(i).getPlayerMat().getRegister().size() - 1);
                                }
                            }

                            System.out.println(card);
                            CardSelected cardSelected = new CardSelected(clientId, cardRegister, true);
                            String serializedCardSelected = Serialisierer.serialize(cardSelected);
                            broadcast(serializedCardSelected);

                            for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                // wenn NumRegister == 5: sende SelectionFinished an Clients
                                if (Server.getGame().getPlayerList().get(i).getId() == clientId &&
                                        Server.getGame().getPlayerList().get(i).getPlayerMat().getNumRegister() == 5) {
                                    SelectionFinished selectionFinished = new SelectionFinished(clientId);
                                    String serializedSelectionFinished = Serialisierer.serialize(selectionFinished);
                                    broadcast(serializedSelectionFinished);
                                    if(Server.getTimerSend() == 0){
                                        // sende TimerStarted
                                        TimerStarted timerStarted = new TimerStarted();
                                        String serializedTimerStarted = Serialisierer.serialize(timerStarted);
                                        broadcast(serializedTimerStarted);
                                        Server.setTimerSend(1);
                                        // wait 30 sec to send TimerEnded
                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                // After 30 seconds, send TimerEnded
                                                ArrayList<Integer> clientIDsNotReady = new ArrayList<>();
                                                for (Player player : Server.getGame().getPlayerList()) {
                                                    if (player.getPlayerMat().getNumRegister() < 5) {
                                                        clientIDsNotReady.add(player.getId());
                                                    }
                                                }
                                                TimerEnded timerEnded = new TimerEnded(clientIDsNotReady);
                                                String serializedTimerEnded = Serialisierer.serialize(timerEnded);
                                                broadcast(serializedTimerEnded);

                                                // missingClientCards an betreffende Clients versenden
                                                ArrayList<String> missingClientCards = new ArrayList<>();

                                                for (Player player : Server.getGame().getPlayerList()) {
                                                    // wie viele Felder auf Register sind leer
                                                    int i = 0;
                                                    while (i < (5 - player.getPlayerMat().getNumRegister())) {
                                                        // karten ziehen
                                                        Card card = player.getPlayerMat().getProgDeck().get(0);
                                                        missingClientCards.add(card.getName());
                                                        player.getPlayerMat().getRegister().add(card.getName());
                                                        player.getPlayerMat().getProgDeck().remove(0);
                                                        i++;
                                                    }
                                                }
                                                for (Integer clientID : clientIDsNotReady) {
                                                    CardsYouGotNow cardsYouGotNow = new CardsYouGotNow(missingClientCards);
                                                    String serializedCardYouGotNow = Serialisierer.serialize(cardsYouGotNow);
                                                    sendToOneClient(clientID, serializedCardYouGotNow);
                                                }

                                                discardHand();

                                                discardCurrentRegister();

                                                Server.getGame().setNextPlayersTurn();

                                                ArrayList<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
                                                for (Player player : Server.getGame().getPlayerList()) {
                                                    activeCards.add(new CurrentCards.ActiveCard(player.getId(),
                                                            player.getPlayerMat().getRegister().get(Server.getRegisterCounter()))); // 0. element aus register von jedem Player
                                                }

                                                Server.setRegisterCounter(Server.getRegisterCounter()+1);

                                                Server.getGame().nextCurrentPhase();

                                                ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                                String serializedActivePhase = Serialisierer.serialize(activePhase);
                                                broadcast(serializedActivePhase);

                                                System.out.println(Server.getGame().getCurrentPhase());

                                                // nulltes register wird an alle gesendet
                                                CurrentCards currentCards = new CurrentCards(activeCards);
                                                String serializedCurrentCards = Serialisierer.serialize(currentCards);
                                                broadcast(serializedCurrentCards);

                                                Server.setCountPlayerTurns(0);
                                                CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getPlayerList().get(Server.getCountPlayerTurns()).getId());
                                                String serializedCurrentPlayer = Serialisierer.serialize(currentPlayer);
                                                broadcast(serializedCurrentPlayer);
                                                // test
                                                for(CurrentCards.ActiveCard activeCard : currentCards.getMessageBody().getActiveCards()) {
                                                    System.out.println(activeCard.getCard());
                                                }

                                                // Cancel the timer
                                                timer.cancel();
                                            }
                                        }, 30000); // 30,000 milliseconds = 30 seconds
                                    }
                                }
                            }
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

    public int checkNumReady() {
        int numReady = 0;
        for (int i = 0; i < Server.getPlayerList().size(); i++) {
            if (Server.getPlayerList().get(i).isReady()) {
                numReady++;
            }
        }
        return numReady;
    }

    public int checkNextReady() {
        int x = 0;
        if (Server.getReadyListIndex() < Server.getReadyList().size()) {
            for (int i = 0; i < Server.getPlayerList().size(); i++) {
                if (Server.getPlayerList().get(i).getId() == Server.getReadyList().get(Server.getReadyListIndex())
                        && !Server.getPlayerList().get(i).isReady()) {
                    Server.setReadyListIndex(Server.getReadyListIndex() + 1);
                    x = checkNextReady();
                } else {
                    x = Server.getReadyList().get(Server.getReadyListIndex());
                }
            }
        }
        return x;
    }

    public void sendAliveMessage() {
        Alive alive = new Alive();
        String serializedAlive = Serialisierer.serialize(alive);
        writer.println(serializedAlive);
    }

    private String checkRobotField(Robot robot) {
        // Obtain the robot's current position
        int robotX = robot.getX();
        int robotY = robot.getY();

        // Assuming you have a method in DizzyHighway to get the field at a specific position
        // You need to create an instance or use a static method of DizzyHighway class, depending on your implementation
        DizzyHighway highway = new DizzyHighway();  // Create a new instance or use an existing one
        List<Field> fields = highway.getFieldsAt(robotX, robotY);

        //tester string
        System.out.println("Fields at position (" + robotX + ", " + robotY + "): " + fields);

        StringBuilder result = new StringBuilder();

        for (Field field : fields) {
            if (field instanceof ConveyorBelt) {
                // Additional checks or actions for conveyor belt
                System.out.println("ConveyorBelt");
                String[] orientations = field.getOrientation();
                result.append("ConveyorBelt " + Arrays.toString(orientations) + ", ");
            }else if (field instanceof Laser) {
                System.out.println("Laser");
                // Additional checks or actions for laser
                result.append("Laser, ");
            } else if (field instanceof Wall) {
                System.out.println("Wall");
                // Actions for wall
                String[] orientations = field.getOrientation();
                result.append("Wall " + Arrays.toString(orientations) + ", ");
            } else if (field instanceof Empty) {
                // Actions for an empty field
                System.out.println("Empty field");
                result.append("Empty, ");
            } else if (field instanceof StartPoint) {
                System.out.println("Start point");
                // Actions for a start point
                result.append("StartPoint, ");
            } else if (field instanceof CheckPoint) {
                System.out.println("Checkpoint");
                // Actions for a check point
                result.append("CheckPoint, ");
            } else if (field instanceof EnergySpace) {
                // Actions for an energy space
                result.append("EnergySpace, ");
            } else {
                // Default case
                System.out.println("Field nicht gefunden");
                result.append("UnknownField, ");
            }
        }

        // Remove the last comma and space

        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }

        System.out.println(result);

        return result.toString();

    }

    public static boolean movePossibleWall(String fieldCheck, Robot robot) {
        if (fieldCheck.contains("Wall [bottom") && robot.getOrientation().equals("bottom")) {
            return false;
        } else if (fieldCheck.contains("Wall [top") && robot.getOrientation().equals("top")) {
            return false;
        } else if (fieldCheck.contains("Wall [right") && robot.getOrientation().equals("right")) {
            return false;
        } else if (fieldCheck.contains("Wall [left") && robot.getOrientation().equals("left")) {
            return false;
        }
        return true;
    }

    public static boolean movePossibleWallBack(String fieldCheck, Robot robot) {
        if (fieldCheck.contains("Wall [bottom") && robot.getOrientation().equals("top")) {
            return false;
        } else if (fieldCheck.contains("Wall [top") && robot.getOrientation().equals("bottom")) {
            return false;
        } else if (fieldCheck.contains("Wall [right") && robot.getOrientation().equals("left")) {
            return false;
        } else if (fieldCheck.contains("Wall [left") && robot.getOrientation().equals("right")) {
            return false;
        }
        return true;
    }

    public void fieldActivation() throws InterruptedException {
        // Conveyor Belts
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {

            //tester strings checker
            System.out.println("size of playerlist " + Server.getGame().getPlayerList().size());
            System.out.println(Server.getGame().getPlayerList().get(i));
            System.out.println("708 name: " + Server.getGame().getPlayerList().get(i).getName() + " figure: " + Server.getGame().getPlayerList().get(i).getFigure() + " x:" + Server.getGame().getPlayerList().get(i).getRobot().getX() + ", y:" + Server.getGame().getPlayerList().get(i).getRobot().getY());

            checkBlueAndGreenConveyorBelts(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkBoardLaser(i);

        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkRobotLasers(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkCheckpoint(i);
        }


    }

    private void checkBlueAndGreenConveyorBelts(int i) throws InterruptedException {
        //blue conveyor
        String standingOnBlueConveyor = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if(standingOnBlueConveyor.contains("ConveyorBelt")) {
            if (standingOnBlueConveyor.contains("ConveyorBelt [top")) {

                //falls austreten aus blu conveyor funktioniert set nur einseitig!
                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                checkConveyorBeltAgain(i, standingOnBlueConveyor);


            } else if (standingOnBlueConveyor.contains("ConveyorBelt [right")){
                Server.getGame().getPlayerList().get(i).getRobot().setX(Server.getGame().getPlayerList().get(i).getRobot().getX() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                checkConveyorBeltAgain(i,standingOnBlueConveyor);

            }else if (standingOnBlueConveyor.contains("ConveyorBelt [bottom")){

                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                checkConveyorBeltAgain(i, standingOnBlueConveyor);

            }else if (standingOnBlueConveyor.contains("ConveyorBelt [left")){

                Server.getGame().getPlayerList().get(i).getRobot().setX(Server.getGame().getPlayerList().get(i).getRobot().getX() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                checkConveyorBeltAgain(i, standingOnBlueConveyor);

            }
        }
    }

    private void checkBoardLaser(int i){

    }

    private void checkRobotLasers(int i){

    }

    private void checkCheckpoint(int i){

    }

    private void checkConveyorBeltAgain(int j, String standingOnBlueConveyorBelt) throws InterruptedException {

        Thread.sleep(750);

        String stillOnBlue = checkRobotField(Server.getGame().getPlayerList().get(j).getRobot());

        if(stillOnBlue.contains("ConveyorBelt")) {
            if (stillOnBlue.contains("ConveyorBelt [top")) {
                Server.getGame().getPlayerList().get(j).getRobot().setY(Server.getGame().getPlayerList().get(j).getRobot().getY() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                if(!standingOnBlueConveyorBelt.contains("ConveyorBelt [top")){
                    if(standingOnBlueConveyorBelt.contains("ConveyorBelt [right")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }if(standingOnBlueConveyorBelt.contains("ConveyorBelt [left")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            }else if (stillOnBlue.contains("ConveyorBelt [right")){
                Server.getGame().getPlayerList().get(j).getRobot().setX(Server.getGame().getPlayerList().get(j).getRobot().getX() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                if(!standingOnBlueConveyorBelt.contains("ConveyorBelt [right")){
                    if(standingOnBlueConveyorBelt.contains("ConveyorBelt [bottom")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }if(standingOnBlueConveyorBelt.contains("ConveyorBelt [top")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            }else if (stillOnBlue.contains("ConveyorBelt [bottom")){
                Server.getGame().getPlayerList().get(j).getRobot().setY(Server.getGame().getPlayerList().get(j).getRobot().getY() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                if(!standingOnBlueConveyorBelt.contains("ConveyorBelt [bottom")){

                    if(standingOnBlueConveyorBelt.contains("ConveyorBelt [left")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }if(standingOnBlueConveyorBelt.contains("ConveyorBelt [right")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }



            }else if (stillOnBlue.contains("ConveyorBelt [left")){
                Server.getGame().getPlayerList().get(j).getRobot().setX(Server.getGame().getPlayerList().get(j).getRobot().getX() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                if(!standingOnBlueConveyorBelt.contains("ConveyorBelt [left")){

                    if(standingOnBlueConveyorBelt.contains("ConveyorBelt [top")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }if(standingOnBlueConveyorBelt.contains("ConveyorBelt [bottom")){
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation( "clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            }
        }



    }

    private String getResultingOrientation(String turningDirection, Robot robot){
        if (turningDirection.equals("clockwise")) {
            switch (robot.getOrientation()) {
                case "top":
                    return "right";
                case "bottom":
                    return "left";
                case "left":
                    return "top";
                case "right":
                    return "bottom";
            }
        }else {
            switch (robot.getOrientation()){
                case "top":
                    return "left";
                case "bottom":
                    return "right";
                case "left":
                    return "bottom";
                case "right":
                    return "top";
            }
        }
        //da sollte man nie hinkommen
        return "---";
    }

    public void discardCurrentRegister(){
        for(Player player : Server.getGame().getPlayerList()){
            if(player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("Spam")) {
                Server.getGame().setSpam(Server.getGame().getSpam()+1);
            } else if(player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("TrojanHorse")) {
                Server.getGame().setSpam(Server.getGame().getTrojanHorse()+1);
            } else if(player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("Virus")) {
                Server.getGame().setSpam(Server.getGame().getVirus()+1);
            } else if(player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("Wurm")) {
                Server.getGame().setSpam(Server.getGame().getWurm() + 1);
            } else {
                // füge sonst dem player in playerMat in den discardPile das n. Register
                player.getPlayerMat().getDiscardPile().add(player.getPlayerMat().getRegister().get(Server.getRegisterCounter()));
            }
        }
    }

    public void discardHand() {
        for (Player player : Server.getGame().getPlayerList()) {
            for (String card : player.getPlayerMat().getClientHand()) {
                player.getPlayerMat().getDiscardPile().add(card);
            }
            player.getPlayerMat().getClientHand().clear(); // Leere die Hand des Spielers
            for(String discardCard : player.getPlayerMat().getDiscardPile()){
                System.out.println(player.getId()+ ": "+ discardCard);
            }
        }
    }

    public ArrayList<Card> stringToCard(ArrayList<String> stringCards){
        ArrayList<Card> kartenStapel = new ArrayList<>();
        for (String cardName : stringCards) {
            switch (cardName) {
                case "Again":
                    kartenStapel.add(new Again());
                    break; // Füge diese Unterbrechungspunkte hinzu, um sicherzustellen, dass nur eine Karte hinzugefügt wird
                case "BackUp":
                    kartenStapel.add(new BackUp());
                    break;
                case "LeftTurn":
                    kartenStapel.add(new LeftTurn());
                    break;
                case "MoveI":
                    kartenStapel.add(new MoveI());
                    break;
                case "MoveII":
                    kartenStapel.add(new MoveII());
                    break;
                case "MoveIII":
                    kartenStapel.add(new MoveIII());
                    break;
                case "PowerUp":
                    kartenStapel.add(new PowerUp());
                    break;
                case "RightTurn":
                    kartenStapel.add(new RightTurn());
                    break;
                case "UTurn":
                    kartenStapel.add(new UTurn());
                    break;
            }
        }
        return kartenStapel;
    }



}