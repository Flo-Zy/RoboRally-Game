package SEPee.server.model;

import SEPee.client.viewModel.MapController.MapController;
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
    private static List<ClientHandler> clients;
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

                            PlayerValues deserializedPlayerValues = Deserialisierer.deserialize(serializedReceivedString, PlayerValues.class);
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
                            String serializedJoinedPlayerMessage = Serialisierer.serialize(joinedPlayerMessage);
                            broadcast(serializedJoinedPlayerMessage);


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
                                if (Server.getFirstReady() == player.getId()) {
                                    switch (mapSelected.getMessageBody().getMap()) {
                                        case "DizzyHighway":
                                            DizzyHighway dizzyHighway = new DizzyHighway();
                                            Server.setGameMap(dizzyHighway);
                                            break;
                                        case "ExtraCrispy":
                                            ExtraCrispy extraCrispy = new ExtraCrispy();
                                            Server.setGameMap(extraCrispy);
                                            break;
                                        case "LostBearings":
                                            LostBearings lostBearings = new LostBearings();
                                            Server.setGameMap(lostBearings);
                                            break;
                                        case "DeathTrap":
                                            DeathTrap deathTrap = new DeathTrap();
                                            Server.setGameMap(deathTrap);
                                            break;
                                    }
                                }
                            }
                            //check alle ready und mind 2
                            if (!Server.isGameStarted() && checkNumReady() >= 2 && checkNumReady() == Server.getPlayerList().size()
                                    && Server.getGameMap() != null) {
                                Server.setGameStarted(true);
                                //erstelle das Spiel
                                Server.setGame(new Game(Server.getPlayerList(), Server.getGameMap().getGameBoard(), Server.getGameMap()));

                                MapSelected mapSelected1 = new MapSelected(Server.getGameMap().getBordName());
                                String serializedMapSelected1 = Serialisierer.serialize(mapSelected1);
                                broadcast(serializedMapSelected1);

                                //Sende an alle Clients Spiel wird gestarted
                                GameStarted gameStarted = new GameStarted(Server.getGameMap().getGameBoard());
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
                            Server.setCountPlayerTurns(Server.getCountPlayerTurns() + 1);
                            PlayCard playCard = Deserialisierer.deserialize(serializedReceivedString, PlayCard.class);

                            // Card played für Karten Verständnis an alle clients schicken
                            String playCardCard = playCard.getMessageBody().getCard();
                            CardPlayed cardPlayed = new CardPlayed(clientId, playCardCard);
                            String serializedCardPlayed = Serialisierer.serialize(cardPlayed);
                            System.out.println("id: " + clientId + "played: " + playCardCard);

                            if (!playCardCard.equals("Again")) {
                                lastPlayedCard = playCardCard;
                            }

                            //logik für karteneffekte
                            switch (lastPlayedCard) {
                                case "BackUp": //vielleicht auch Move Back steht beides in Anleitung Seite 24
                                    handleRobotMovement(1, false);
                                    break;
                                case "MoveI":
                                    handleRobotMovement(1, true);
                                    break;
                                case "MoveII":
                                    handleRobotMovement(2, true);
                                    break;
                                case "MoveIII":
                                    handleRobotMovement(3, true);
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

                            // wenn letzter Player aus PlayerList dran ist
                            if (Server.getCountPlayerTurns() == Server.getGame().getPlayerList().size()) {

                                for (Player player : Server.getGame().getPlayerList()) {
                                    player.getPlayerMat().getReceivedDamageCards().clear();
                                }

                                fieldActivation(); // Belts, lasers, checkpoints.. etc.

                                for (Player player : Server.getGame().getPlayerList()) {
                                    if (!player.getPlayerMat().getReceivedDamageCards().isEmpty()) {
                                        DrawDamage drawDamage = new DrawDamage(player.getId(), player.getPlayerMat().getReceivedDamageCards());
                                        String serializedDrawDamage = Serialisierer.serialize(drawDamage);
                                        broadcast(serializedDrawDamage);
                                    }
                                }

                                Server.getGame().setNextPlayersTurn(); // setze playerIndex = 0, PlayerList mit neuen Priorities, currentPlayer = playerList.get(playerIndex), playerIndex++

                                if (Server.getRegisterCounter() <= 4) {
                                    // n. register wird an alle gesendet
                                    ArrayList<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        activeCards.add(new CurrentCards.ActiveCard(player.getId(), player.getPlayerMat().getRegister().get(Server.getRegisterCounter()))); // n. element aus register von jedem Player
                                    }

                                    discardCurrentRegister();

                                    Server.setRegisterCounter(Server.getRegisterCounter() + 1);

                                    CurrentCards currentCards = new CurrentCards(activeCards);
                                    String serializedCurrentCards = Serialisierer.serialize(currentCards);
                                    broadcast(serializedCurrentCards);

                                    Server.setCountPlayerTurns(0);
                                    CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getCurrentPlayer().getId());
                                    String serializedCurrentPlayer = Serialisierer.serialize(currentPlayer);
                                    broadcast(serializedCurrentPlayer);

                                    // test
                                    for (CurrentCards.ActiveCard activeCard : currentCards.getMessageBody().getActiveCards()) {
                                        System.out.println(activeCard.getCard());
                                    }
                                } else { // Server.getRegisterCounter() größer 4
                                    Server.setRegisterCounter(0);
                                    Server.setTimerSend(0);
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        player.getPlayerMat().setNumRegister(0);
                                    }
                                    Server.getGame().nextCurrentPhase();

                                    for (Player player : Server.getGame().getPlayerList()) {
                                        System.out.println("Player " + player.getId() + " discardPile: " + player.getPlayerMat().getDiscardPile());
                                    }

                                    for (Player player : Server.getGame().getPlayerList()) {
                                        System.out.println("Player " + player.getId() + " register: " + player.getPlayerMat().getRegister());
                                    }

                                    // entferne nachdem alle register in Phase 2 abgearbeitet wurden von jedem player das gesamte register
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        player.getPlayerMat().getRegister().clear();
                                    }

                                    // teste ob alle register leer
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        System.out.println("Player " + player.getId() + " register: " + player.getPlayerMat().getRegister());
                                    }

                                    // YourCards an Client senden wenn ActivePhase = 2
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        if (player.getPlayerMat().getProgDeck().size() >= 9) {
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
                                        } else {
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
                            } else { //alle spieler waren noch nicht im aktuellen register dran, nächster Spieler soll seine Karte Spielen
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

                            if(Server.getGameMap().getBordName().equals("DeathTrap")) {
                                this.robot = new Robot(0, 0, "left");
                            } else {
                                this.robot = new Robot(0, 0, "right");
                            }

                            this.robot.setY(setStartingPoint.getMessageBody().getY());
                            this.robot.setX(setStartingPoint.getMessageBody().getX());

                            this.robot.setStartingPointX(setStartingPoint.getMessageBody().getX());
                            this.robot.setStartingPointY(setStartingPoint.getMessageBody().getY());

                            //finds the associated Player and set the robot for that player
                            Player associatedPlayer = Server.getGame().getPlayerList().get(clientId - 1);
                            associatedPlayer.setRobot(this.robot);

                            // Adds the game class(which implements RobotPositionChangeListener)as a listener to the robot
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
                                        System.out.println(Server.getGame().getPlayerList().get(i).getPlayerMat().getNumRegister());
                                    }
                                }
                            }
                            for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                // füge in register card hinzu
                                if (Server.getGame().getPlayerList().get(i).getId() == clientId && card != null) {
                                    //gelegte Karte dem Register hinzufügen
                                    Server.getGame().getPlayerList().get(i).getPlayerMat().getRegister().add(cardRegister, card);
                                    //gelegte Karte von der Hand entfernen
                                    for (int j = 0; j < Server.getGame().getPlayerList().get(i).getPlayerMat().getClientHand().size(); j++) {
                                        if (card.equals(Server.getGame().getPlayerList().get(i).getPlayerMat().getClientHand().get(j))) {
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
                                    if (Server.getTimerSend() == 0) {
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

                                                Server.setRegisterCounter(Server.getRegisterCounter() + 1);

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
                                                for (CurrentCards.ActiveCard activeCard : currentCards.getMessageBody().getActiveCards()) {
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

    private static void broadcast(String serializedObjectToSend) {
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

    private void handleRobotMovement(int moves, boolean isForward) throws InterruptedException {
        for (int i = 0; i < moves; i++) {
            boolean canMove = movePossibleWall(checkRobotField(this.robot), this.robot, isForward);

            if (canMove) {
                checkForRobotsAndMove(this.robot, isForward);

                if (isForward) {
                    MoveI.makeEffect(this.robot);
                } else{
                    BackUp.makeEffect(this.robot);
                }
            } else {
                if (isForward) {
                    System.out.println("Roboter mit ID: " + this.clientId + " läuft gegen wand.");
                } else {
                    System.out.println("Roboter mit ID: " + this.clientId + " steht mit dem Rücken gegen die Wand.");
                }
            }

            int x = this.robot.getX();
            int y = this.robot.getY();
            int clientID = this.clientId;

            Movement movement = new Movement(clientID, x, y);
            String serializedMovement = Serialisierer.serialize(movement);
            Thread.sleep(750);
            broadcast(serializedMovement);
        }
    }

    private void checkForRobotsAndMove(Robot robot, boolean isForward) {
        String orientation = robot.getOrientation();
        int xCoordinatePushingRobot = robot.getX();
        int yCoordinatePushingRobot = robot.getY();

        for (Player player : Server.getGame().getPlayerList()) {
            int xPlayerFleeingRobot = player.getRobot().getX();
            int yPlayerFleeingRobot = player.getRobot().getY();

            if (shouldPush(isForward, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                movePlayerRobot(player, isForward, orientation);
            }
        }
    }
    private boolean shouldPush(boolean isForward, String orientation, int xPushing, int yPushing, int xFleeing, int yFleeing) {
        switch (orientation) {
            case "top":
                boolean topCondition = (isForward && yFleeing == yPushing - 1 && xFleeing == xPushing) ||
                        (!isForward && yFleeing == yPushing + 1 && xFleeing == xPushing);
                System.out.println("Top condition: " + topCondition);
                return topCondition;
            case "right":
                boolean rightCondition = (isForward && xFleeing == xPushing + 1 && yFleeing == yPushing) ||
                        (!isForward && xFleeing == xPushing - 1 && yFleeing == yPushing);
                System.out.println("Right condition: " + rightCondition);
                return rightCondition;
            case "left":
                boolean leftCondition = (isForward && xFleeing == xPushing - 1 && yFleeing == yPushing) ||
                        (!isForward && xFleeing == xPushing + 1 && yFleeing == yPushing);
                System.out.println("Left condition: " + leftCondition);
                return leftCondition;
            case "bottom":
                boolean bottomCondition = (isForward && yFleeing == yPushing + 1 && xFleeing == xPushing) ||
                        (!isForward && yFleeing == yPushing - 1 && xFleeing == xPushing);
                System.out.println("Bottom condition: " + bottomCondition);
                return bottomCondition;
            default:
                return false;
        }
    }

    private void movePlayerRobot(Player player, boolean isForward, String orientation) {
        int x = player.getRobot().getX();
        int y = player.getRobot().getY();

        switch (orientation) {
            case "top":
                y = isForward ? y - 1 : y + 1;
                break;
            case "right":
                x = isForward ? x + 1 : x - 1;
                break;
            case "left":
                x = isForward ? x - 1 : x + 1;
                break;
            case "bottom":
                y = isForward ? y + 1 : y - 1;
                break;
        }

        player.getRobot().setX(x);
        player.getRobot().setY(y);

        System.out.println(player.getName() + " wird geschoben");

        Movement movement = new Movement(player.getId(), x, y);
        String serializedMovement = Serialisierer.serialize(movement);
        broadcast(serializedMovement);
    }


    private void rebootThisRobot(Robot robot, boolean toReboot){

    }

    private boolean checkForRebootAndTeleport(Robot robot) {
        String orientation = robot.getOrientation();
        int xCoordinate = robot.getX();
        int yCoordinate = robot.getY();

        switch (orientation) {
            case "top":
                if (yCoordinate - 1 < 0 && xCoordinate < 3) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (robot.equals(player.getRobot())) {
                            player.getRobot().setX(player.getRobot().getStartingPointX());
                            player.getRobot().setY(player.getRobot().getStartingPointY());

                            Movement movement = new Movement(player.getId(), player.getRobot().getX(), player.getRobot().getY());
                            String serializedMovement = Serialisierer.serialize(movement);
                            broadcast(serializedMovement);

                            Reboot reboot = new Reboot(player.getId());
                            String serializedReboot = Serialisierer.serialize(reboot);
                            broadcast(serializedReboot);

                            RebootDirection rebootDirection = new RebootDirection("bottom");
                            String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
                            broadcast(serializedRebootDirection);

                            return false;
                        }
                    }
                } else if (yCoordinate - 1 < 0 && xCoordinate >= 3) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (robot.equals(player.getRobot())) {
                            player.getRobot().setX(7);
                            player.getRobot().setY(3);
                            /*
                            player.getRobot().setOrientation("right");

                            PlayerTurning playerTurning = new PlayerTurning();

                             */

                            Movement movement = new Movement(player.getId(), player.getRobot().getX(), player.getRobot().getY());
                            String serializedMovement = Serialisierer.serialize(movement);
                            broadcast(serializedMovement);

                            Reboot reboot = new Reboot(player.getId());
                            String serializedReboot = Serialisierer.serialize(reboot);
                            broadcast(serializedReboot);

                            //Reboot direction verschicken
                            RebootDirection rebootDirection = new RebootDirection("bottom");
                            String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
                            broadcast(serializedRebootDirection);

                            return false;
                        }
                    }
                }
                break;
            case "right":
                if (xCoordinate + 1 > 12) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (robot.equals(player.getRobot())) {
                            player.getRobot().setX(7);
                            player.getRobot().setY(3);

                            Movement movement = new Movement(player.getId(), player.getRobot().getX(), player.getRobot().getY());
                            String serializedMovement = Serialisierer.serialize(movement);
                            broadcast(serializedMovement);

                            Reboot reboot = new Reboot(player.getId());
                            String serializedReboot = Serialisierer.serialize(reboot);
                            broadcast(serializedReboot);

                            RebootDirection rebootDirection = new RebootDirection("bottom");
                            String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
                            broadcast(serializedRebootDirection);

                            return false;
                        }
                    }
                }
                break;
            case "bottom":
                if (yCoordinate + 1 > 10 && xCoordinate < 3) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (robot.equals(player.getRobot())) {
                            player.getRobot().setX(player.getRobot().getStartingPointX());
                            player.getRobot().setY(player.getRobot().getStartingPointY());

                            Movement movement = new Movement(player.getId(), player.getRobot().getX(), player.getRobot().getY());
                            String serializedMovement = Serialisierer.serialize(movement);
                            broadcast(serializedMovement);

                            Reboot reboot = new Reboot(player.getId());
                            String serializedReboot = Serialisierer.serialize(reboot);
                            broadcast(serializedReboot);

                            RebootDirection rebootDirection = new RebootDirection("bottom");
                            String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
                            broadcast(serializedRebootDirection);

                            return false;
                        }
                    }
                } else if (yCoordinate + 1 > 10 && xCoordinate > 2) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (robot.equals(player.getRobot())) {
                            player.getRobot().setX(7);
                            player.getRobot().setY(3);

                            Movement movement = new Movement(player.getId(), player.getRobot().getX(), player.getRobot().getY());
                            String serializedMovement = Serialisierer.serialize(movement);
                            broadcast(serializedMovement);

                            Reboot reboot = new Reboot(player.getId());
                            String serializedReboot = Serialisierer.serialize(reboot);
                            broadcast(serializedReboot);

                            RebootDirection rebootDirection = new RebootDirection("bottom");
                            String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
                            broadcast(serializedRebootDirection);

                            return false;
                        }
                    }
                }

                break;
            case "left":
                if (xCoordinate - 1 < 0) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (robot.equals(player.getRobot())) {
                            player.getRobot().setX(player.getRobot().getStartingPointX());
                            player.getRobot().setY(player.getRobot().getStartingPointY());

                            Movement movement = new Movement(player.getId(), player.getRobot().getX(), player.getRobot().getY());
                            String serializedMovement = Serialisierer.serialize(movement);
                            broadcast(serializedMovement);

                            Reboot reboot = new Reboot(player.getId());
                            String serializedReboot = Serialisierer.serialize(reboot);
                            broadcast(serializedReboot);

                            RebootDirection rebootDirection = new RebootDirection("bottom");
                            String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
                            broadcast(serializedRebootDirection);

                            return false;
                        }
                    }
                }

                break;
        }
        return true;
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

        List<Field> fields = new ArrayList<>();

        if(Server.getGameMap().getBordName().equals("DizzyHighway")) {
            DizzyHighway highway = new DizzyHighway();  // Create a new instance or use an existing one
            fields = highway.getFieldsAt(robotX, robotY);
        } else if(Server.getGameMap().getBordName().equals("ExtraCrispy")) {
            ExtraCrispy extraCrispy = new ExtraCrispy();
            fields = extraCrispy.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("DeathTrap")) {
            DeathTrap deathTrap = new DeathTrap();
            fields = deathTrap.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("LostBearings")) {
            LostBearings lostBearings = new LostBearings();
            fields = lostBearings.getFieldsAt(robotX, robotY);
        }

        //tester string
        System.out.println("Fields at position (" + robotX + ", " + robotY + "): " + fields);

        StringBuilder result = new StringBuilder();

        for (Field field : fields) {
            if (field instanceof ConveyorBelt) {
                // Additional checks or actions for conveyor belt
                System.out.println("ConveyorBelt");
                String[] orientations = field.getOrientation();
                int speed = field.getSpeed();

                result.append("ConveyorBelt " + speed + " " + Arrays.toString(orientations) + ", ");

            } else if (field instanceof Laser) {
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
                int checkPointNumber = field.getCheckPointNumber();
                result.append("CheckPoint [" + checkPointNumber + "], ");
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

    public static boolean movePossibleWall(String fieldCheck, Robot robot, boolean isForward) {
        boolean canMove = true;

        if (fieldCheck.contains("Wall [bottom") && (robot.getOrientation().equals("bottom") && isForward || robot.getOrientation().equals("top") && !isForward)) {
            canMove = false;
        } else if (fieldCheck.contains("Wall [top") && (robot.getOrientation().equals("top") && isForward || robot.getOrientation().equals("bottom") && !isForward)) {
            canMove = false;
        } else if (fieldCheck.contains("Wall [right") && (robot.getOrientation().equals("right") && isForward || robot.getOrientation().equals("left") && !isForward)) {
            canMove = false;
        } else if (fieldCheck.contains("Wall [left") && (robot.getOrientation().equals("left") && isForward || robot.getOrientation().equals("right") && !isForward)) {
            canMove = false;
        }
        return canMove;
    }

    public void fieldActivation() throws InterruptedException {
        // Conveyor Belts
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkBlueConveyorBelts(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkGreenConveyorBelts(i);
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

    private void checkBlueConveyorBelts (int i) throws InterruptedException {
        //blue conveyor
        String standingOnBlueConveyor = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnBlueConveyor.contains("ConveyorBelt 2")) {
            if (standingOnBlueConveyor.contains("ConveyorBelt 2 [top")) {

                //falls austreten aus blu conveyor funktioniert set nur einseitig!
                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondBlue = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondBlue.contains("ConveyorBelt 2 [top")) {
                    if (secondBlue.contains("ConveyorBelt 2 [right")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

                checkConveyorBeltAgain(i, secondBlue);


            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [right")) {
                Server.getGame().getPlayerList().get(i).getRobot().setX(Server.getGame().getPlayerList().get(i).getRobot().getX() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondBlue = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondBlue.contains("ConveyorBelt 2 [right")) {
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

                checkConveyorBeltAgain(i, secondBlue);

            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [bottom")) {

                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondBlue = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondBlue.contains("ConveyorBelt 2 [bottom")) {
                    if (secondBlue.contains("ConveyorBelt 2 [right")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

                checkConveyorBeltAgain(i, secondBlue);

            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [left")) {

                Server.getGame().getPlayerList().get(i).getRobot().setX(Server.getGame().getPlayerList().get(i).getRobot().getX() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondBlue = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondBlue.contains("ConveyorBelt 2 [left")) {
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

                checkConveyorBeltAgain(i, secondBlue);

            }
        }
    }

    private void checkGreenConveyorBelts (int i){
        //blue conveyor
        String standingOnGreenConveyor = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnGreenConveyor.contains("ConveyorBelt 1")) {
            if (standingOnGreenConveyor.contains("ConveyorBelt 1 [top")) {
                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondGreen = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondGreen.contains("ConveyorBelt 1 [top")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [right")) {
                Server.getGame().getPlayerList().get(i).getRobot().setX(Server.getGame().getPlayerList().get(i).getRobot().getX() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondGreen = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondGreen.contains("ConveyorBelt 1 [right")) {
                    if (secondGreen.contains("ConveyorBelt 1 [top")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [bottom")) {
                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondGreen = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondGreen.contains("ConveyorBelt 1 [bottom")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [left")) {
                Server.getGame().getPlayerList().get(i).getRobot().setX(Server.getGame().getPlayerList().get(i).getRobot().getX() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondGreen = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondGreen.contains("ConveyorBelt 1 [left")) {
                    if (secondGreen.contains("ConveyorBelt 1 [top")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }
            }
        }
    }

    private void checkBoardLaser(int i) {
        String standingOnBoardLaser = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnBoardLaser.contains("Laser")) {
            if (Server.getGame().getSpam() > 0) {
                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                Server.getGame().getPlayerList().get(i).getPlayerMat().getReceivedDamageCards().add("Spam");
                Server.getGame().getPlayerList().get(i).getPlayerMat().getDiscardPile().add("Spam");
            }
        }
    }

    private void checkRobotLasers(int i) {
        String robotOrientation = Server.getGame().getPlayerList().get(i).getRobot().getOrientation();
        Robot yourRobot = Server.getGame().getPlayerList().get(i).getRobot();

        switch (robotOrientation) {
            case "top":
                Robot targetRobot1 = new Robot(yourRobot.getX(), -9999, "top");
                for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                    if (!yourRobot.equals(Server.getGame().getPlayerList().get(j).getRobot()) &&
                            Server.getGame().getPlayerList().get(j).getRobot().getX() == yourRobot.getX() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getY() < yourRobot.getY() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getY() > targetRobot1.getY()) {
                        targetRobot1 = Server.getGame().getPlayerList().get(j).getRobot();
                    }
                }
                if (targetRobot1.getY() != -9999) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (player.getRobot().equals(targetRobot1)) {
                            if (Server.getGame().getSpam() > 0) {
                                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                player.getPlayerMat().getDiscardPile().add("Spam");
                            } //hier kann man später mit else erweitern, wenn man PickDamage machen soll
                        }
                    }
                }
                break;
            case "right":
                Robot targetRobot2 = new Robot(9999, yourRobot.getY(), "right");
                for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                    if (!yourRobot.equals(Server.getGame().getPlayerList().get(j).getRobot()) &&
                            Server.getGame().getPlayerList().get(j).getRobot().getY() == yourRobot.getY() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getX() > yourRobot.getX() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getX() < targetRobot2.getX()) {
                        targetRobot2 = Server.getGame().getPlayerList().get(j).getRobot();
                    }
                }
                if (targetRobot2.getX() != 9999) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (player.getRobot().equals(targetRobot2)) {
                            if (Server.getGame().getSpam() > 0) {
                                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                player.getPlayerMat().getDiscardPile().add("Spam");
                            } //hier kann man später mit else erweitern, wenn man PickDamage machen soll
                        }
                    }
                }
                break;
            case "bottom":
                Robot targetRobot3 = new Robot(yourRobot.getX(), 9999, "bottom");
                for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                    if (!yourRobot.equals(Server.getGame().getPlayerList().get(j).getRobot()) &&
                            Server.getGame().getPlayerList().get(j).getRobot().getX() == yourRobot.getX() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getY() > yourRobot.getY() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getY() < targetRobot3.getY()) {
                        targetRobot3 = Server.getGame().getPlayerList().get(j).getRobot();
                    }
                }
                if (targetRobot3.getY() != 9999) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (player.getRobot().equals(targetRobot3)) {
                            if (Server.getGame().getSpam() > 0) {
                                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                player.getPlayerMat().getDiscardPile().add("Spam");
                            } //hier kann man später mit else erweitern, wenn man PickDamage machen soll
                        }
                    }
                }
                break;
            case "left":
                Robot targetRobot4 = new Robot(-9999, yourRobot.getY(), "left");
                for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                    if (!yourRobot.equals(Server.getGame().getPlayerList().get(j).getRobot()) &&
                            Server.getGame().getPlayerList().get(j).getRobot().getY() == yourRobot.getY() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getX() < yourRobot.getX() &&
                            Server.getGame().getPlayerList().get(j).getRobot().getX() > targetRobot4.getX()) {
                        targetRobot4 = Server.getGame().getPlayerList().get(j).getRobot();
                    }
                }
                if (targetRobot4.getX() != -9999) {
                    for (Player player : Server.getGame().getPlayerList()) {
                        if (player.getRobot().equals(targetRobot4)) {
                            if (Server.getGame().getSpam() > 0) {
                                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                player.getPlayerMat().getDiscardPile().add("Spam");
                            } //hier kann man später mit else erweitern, wenn man PickDamage machen soll
                        }
                    }
                }
                break;
        }
    }

    private void checkCheckpoint(int i) {
        String standingOnCheckPoint = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnCheckPoint.contains("CheckPoint [1")) {
            if(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 0){ //check whether no checkPoints were reached before
                checkGameFinished(i);
            }
        }else if(standingOnCheckPoint.contains("CheckPoint [2")){
            if(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 1){ //check whether one checkPoint was reached before
                checkGameFinished(i);
            }
        }else if(standingOnCheckPoint.contains("CheckPoint [3")){
            if(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 2){
                checkGameFinished(i);
            }
        }else if(standingOnCheckPoint.contains("CheckPoint [4")){
            if(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 3){
                checkGameFinished(i);
            }
        }else if(standingOnCheckPoint.contains("CheckPoint [5")){
            if(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 4){
                checkGameFinished(i);
            }
        }
    }

    private void checkGameFinished(int i){
        Server.getGame().getPlayerList().get(i).getPlayerMat().setTokenCount(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() + 1);
        int playerTokenAmount = Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount();

        if (Server.getGame().getBoardClass().getCheckpointAmount() == playerTokenAmount) {
            GameFinished gameFinished = new GameFinished(Server.getGame().getPlayerList().get(i).getId());
            String serializedGameFinished = Serialisierer.serialize(gameFinished);
            broadcast(serializedGameFinished);
        }
    }



    private void checkConveyorBeltAgain(int j, String standingOnBlueConveyorBelt) throws InterruptedException {

        Thread.sleep(750);



        if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2")) {
            if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [top")) {
                Server.getGame().getPlayerList().get(j).getRobot().setY(Server.getGame().getPlayerList().get(j).getRobot().getY() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);
                String stillOnBlue = checkRobotField(Server.getGame().getPlayerList().get(j).getRobot());

                if (!stillOnBlue.contains("ConveyorBelt 2 [top")) {
                    if (stillOnBlue.contains("ConveyorBelt 2 [right")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            } else if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [right")) {
                Server.getGame().getPlayerList().get(j).getRobot().setX(Server.getGame().getPlayerList().get(j).getRobot().getX() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String stillOnBlue = checkRobotField(Server.getGame().getPlayerList().get(j).getRobot());

                if (!stillOnBlue.contains("ConveyorBelt 2 [right")) {
                    if (stillOnBlue.contains("ConveyorBelt 2 [bottom")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [top")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            } else if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [bottom")) {
                Server.getGame().getPlayerList().get(j).getRobot().setY(Server.getGame().getPlayerList().get(j).getRobot().getY() + 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String stillOnBlue = checkRobotField(Server.getGame().getPlayerList().get(j).getRobot());

                if (!stillOnBlue.contains("ConveyorBelt 2 [bottom")) {

                    if (stillOnBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [right")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }


            } else if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [left")) {

                Server.getGame().getPlayerList().get(j).getRobot().setX(Server.getGame().getPlayerList().get(j).getRobot().getX() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), Server.getGame().getPlayerList().get(j).getRobot().getX(), Server.getGame().getPlayerList().get(j).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String stillOnBlue = checkRobotField(Server.getGame().getPlayerList().get(j).getRobot());

                if (!stillOnBlue.contains("ConveyorBelt 2 [left")) {

                    if (stillOnBlue.contains("ConveyorBelt 2 [top")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [bottom")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }
                }

            }
        }


    }

    private String getResultingOrientation(String turningDirection, Robot robot) {
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
        } else {
            switch (robot.getOrientation()) {
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

    public void discardCurrentRegister() {
        for (Player player : Server.getGame().getPlayerList()) {
            if (player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("Spam")) {
                Server.getGame().setSpam(Server.getGame().getSpam() + 1);
            } else if (player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("TrojanHorse")) {
                Server.getGame().setSpam(Server.getGame().getTrojanHorse() + 1);
            } else if (player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("Virus")) {
                Server.getGame().setSpam(Server.getGame().getVirus() + 1);
            } else if (player.getPlayerMat().getRegister().get(Server.getRegisterCounter()).equals("Wurm")) {
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
            for (String discardCard : player.getPlayerMat().getDiscardPile()) {
                System.out.println(player.getId() + ": " + discardCard);
            }
        }
    }

    public ArrayList<Card> stringToCard(ArrayList<String> stringCards) {
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

    public static void rebootThisRobot(int xCoordinate, int yCoordinate, String rebootTo){
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++){
            if ((Server.getGame().getPlayerList().get(i).getRobot().getX() == xCoordinate ) && (Server.getGame().getPlayerList().get(i).getRobot().getY() == yCoordinate)){
                Robot robot = Server.getGame().getPlayerList().get(i).getRobot();


                if (rebootTo.equals("rebootField")){
                    robot.setX(Server.getGame().getBoardClass().getRebootX());
                    System.out.println("test print rebootThisRobot to x: " + Server.getGame().getBoardClass().getRebootX());
                    robot.setY(Server.getGame().getBoardClass().getRebootY());
                    System.out.println("test print rebootThisRobot to y: " + Server.getGame().getBoardClass().getRebootY());

                    robot.setAlreadyRebooted(false);
                    Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getBoardClass().getRebootX(), Server.getGame().getBoardClass().getRebootY());
                    String serializedMovement = Serialisierer.serialize(movement);

                    broadcast(serializedMovement);
                    

                } else if (rebootTo.equals("startingPoint")) {

                } else {
                    System.out.println("Invalid Reboot String");
                }

            }
        }
    }
}
