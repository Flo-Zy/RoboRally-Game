package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.damageCard.Spam;
import SEPee.server.model.card.damageCard.TrojanHorse;
import SEPee.server.model.card.damageCard.Virus;
import SEPee.server.model.card.damageCard.Wurm;
import SEPee.server.model.card.progCard.*;
import SEPee.server.model.field.ConveyorBelt;
import SEPee.server.model.field.Field;
import SEPee.server.model.field.*;
import SEPee.server.model.gameBoard.*;

import java.io.*;
import java.lang.Error;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.Getter;
import lombok.Setter;

import static SEPee.server.model.Player.*;

/**
 * for every connected client a ClientHandler is started
 * the ClientHandler reads the messageTypes sent by the Client and handles the according action on server-side
 * @author Franziska, Florian, Maximilian, Felix, Hasan
 */
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
    private static boolean wallFlagMovePush = false;
    @Getter
    @Setter
    private boolean playerFertig = true;
    private boolean gotAlive = true;
    private ScheduledExecutorService disconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    private Timer alive = new Timer();
    private boolean isAi;
    private boolean startingPointSet = false;

    public ClientHandler(Socket clientSocket, List<ClientHandler> clients, int clientId, boolean isAi) {
        this.clientSocket = clientSocket;
        this.clients = clients;
        this.clientId = clientId;
        this.isAi = isAi;

        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * starts the Alive-Sender thread
     * handles every incoming messageType sent by the client and sends according messages back
     * @author Franziska, Florian, Maximilian, Felix, Hasan
     */
    @Override
    public void run() {
            TimerTask taskAlive = new TimerTask() {
                @Override
                public void run() {
                    if (gotAlive) {
                        gotAlive = false;
                        if(Server.isGameStarted() && Server.getGame().getPlayerList().size() == 1){
                            Player gameVictor = Server.getGame().getPlayerList().get(0);
                            GameFinished gameFinished = new GameFinished(gameVictor.getId());
                            String serializedGameFinished = Serialisierer.serialize(gameFinished);
                            broadcast(serializedGameFinished);
                        }else {
                            Alive alive1 = new Alive();
                            String serializedAlive1 = Serialisierer.serialize(alive1);
                            sendToOneClient(clientId, serializedAlive1);
                        }
                    } else {
                        ServerLogger.writeToServerLog("Disconnect");

                        Server.setDisconnected(true);

                        Player disconnectPLayer = new Player("", -9999, -9999);
                        for (Player player : Server.getPlayerList()) {
                            if (player.getId() == clientId) {
                                disconnectPLayer = player;
                                Server.getDisconnectedPlayer().add(disconnectPLayer);
                            }
                        }
                        //Server.getPlayerList().remove(disconnectPLayer);

                        if (Server.isGameStarted()) {

                            if(Server.getGame().getPlayerList().size() > 0) {
                                ConnectionUpdate connectionUpdate = new ConnectionUpdate(clientId, false, "ignore");
                                String serializedConnectionUpdate = Serialisierer.serialize(connectionUpdate);
                                broadcast(serializedConnectionUpdate);
                            }

                            if(Server.getCountPlayerTurns() != Server.getGame().getPlayerList().size() && Server.getGame().getPlayerList().get(Server.getCountPlayerTurns()).getId() == clientId){
                                if(Server.getCountPlayerTurns() < Server.getGame().getPlayerList().size()){
                                    Server.setCountPlayerTurns(Server.getCountPlayerTurns()+1);
                                    CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getPlayerList().get(Server.getCountPlayerTurns()).getId());
                                    String serializedCurrentPlayer = Serialisierer.serialize(currentPlayer);
                                    broadcast(serializedCurrentPlayer);
                                }
                            }

                        }
                        if(Server.isGameStarted()) {
                            if (Server.getGame().getCurrentPhase() == 2) {
                                if(disconnectPLayer.getPlayerMat().registerSize() == 5){
                                    Server.setTimerSend(Server.getTimerSend()-1);
                                }
                                Server.getPlayerList().remove(disconnectPLayer);
                            }else if(!startingPointSet){
                                Server.setCountPlayerTurns(Server.getCountPlayerTurns() +1);
                            }
                        }else{
                            Server.getPlayerList().remove(disconnectPLayer);
                        }
                        alive.cancel();
                    }
                }
            };
            alive.scheduleAtFixedRate(taskAlive, 0, 5000);

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
                            ServerLogger.writeToServerLog("Alive zurück bekommen");
                            gotAlive = true;
                            disconnectScheduler.shutdownNow();
                            synchronized (Server.getPlayerList()) {
                                for (Player player : Server.getPlayerList()) {
                                    ServerLogger.writeToServerLog(player.getName());
                                }
                            }
                            break;
                        case "PlayerValues":
                            ServerLogger.writeToServerLog("Player Values erhalten");

                            PlayerValues deserializedPlayerValues = Deserialisierer.deserialize(serializedReceivedString, PlayerValues.class);
                            playerName = deserializedPlayerValues.getMessageBody().getName();
                            int playerFigure = deserializedPlayerValues.getMessageBody().getFigure();
                            PlayerAdded playerAdded = new PlayerAdded(clientId, playerName, playerFigure);
                            associateSocketWithId(clientSocket, clientId);

                            this.player = new Player(playerName, clientId, playerFigure + 1);
                            for (Player newPlayer : Server.getPlayerList()) {
                                if (newPlayer.getId() == player.getId()) {
                                    newPlayer.setName(player.getName());
                                    newPlayer.setFigure(player.getFigure());
                                }
                            }

                            String serializedPlayerAdded = Serialisierer.serialize(playerAdded);
                            //playerAdded senden an alle alten Clients
                            broadcast(serializedPlayerAdded);

                            ReceivedChat joinedPlayerMessage = new ReceivedChat(playerName + " has joined the chat.", 999, false);
                            String serializedJoinedPlayerMessage = Serialisierer.serialize(joinedPlayerMessage);
                            broadcast(serializedJoinedPlayerMessage);

                            break;
                        case "SetStatus":
                            ServerLogger.writeToServerLog("SetStatus");
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
                            ServerLogger.writeToServerLog("MapSelected");
                            MapSelected mapSelected = Deserialisierer.deserialize(serializedReceivedString, MapSelected.class);
                            if (!mapSelected.getMessageBody().getMap().equals("")) {
                                broadcast(serializedReceivedString);
                                //speicher die gewählte map (die gameMap)
                                if (Server.getFirstReady() == player.getId()) {
                                    switch (mapSelected.getMessageBody().getMap()) {
                                        case "Dizzy Highway":
                                            DizzyHighway dizzyHighway = new DizzyHighway();
                                            Server.setGameMap(dizzyHighway);
                                            break;
                                        case "Extra Crispy":
                                            ExtraCrispy extraCrispy = new ExtraCrispy();
                                            Server.setGameMap(extraCrispy);
                                            break;
                                        case "Lost Bearings":
                                            LostBearings lostBearings = new LostBearings();
                                            Server.setGameMap(lostBearings);
                                            break;
                                        case "Death Trap":
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
                                ServerLogger.writeToServerLog("Game started");

                                ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                String serializedActivePhase = Serialisierer.serialize(activePhase);
                                broadcast(serializedActivePhase);

                                CurrentPlayer currentplayer = new CurrentPlayer(Server.getGame().getCurrentPlayer().getId());
                                String serializedCurrentPlayer = Serialisierer.serialize(currentplayer);
                                broadcast(serializedCurrentPlayer);
                            }
                            break;
                        case "SendChat":
                            ServerLogger.writeToServerLog("Send Chat");
                            SendChat receivedSendChat = Deserialisierer.deserialize(serializedReceivedString, SendChat.class);
                            String receivedSendChatMessage = receivedSendChat.getMessageBody().getMessage();

                            int receivedSendChatFrom = clientId;
                            int receivedSendChatTo = receivedSendChat.getMessageBody().getTo();

                            if (receivedSendChatMessage.startsWith("/")) {
                                if (receivedSendChatMessage.contains("/teleport")) {
                                    String[] parts = receivedSendChatMessage.split(" ");

                                    // prüfen dass Nachricht mindestens drei Teile hat
                                    if (parts.length >= 3 && parts[0].equalsIgnoreCase("/teleport")) {
                                        try {
                                            int xCoordinate = Integer.parseInt(parts[1]);
                                            int yCoordinate = Integer.parseInt(parts[2]);

                                            for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                                if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                                    Server.getGame().getPlayerList().get(i).getRobot().setX(xCoordinate);
                                                    Server.getGame().getPlayerList().get(i).getRobot().setY(yCoordinate);

                                                    Movement movement = new Movement(clientId, Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                                                    String serializedMovement = Serialisierer.serialize(movement);
                                                    broadcast(serializedMovement);
                                                }
                                            }
                                        } catch (NumberFormatException e) {
                                            ServerLogger.writeToServerLog("Invalid coordinates format: ", e);
                                        }
                                    } else {
                                        ServerLogger.writeToServerLog("Invalid /teleport command format");
                                    }
                                }
                                if (receivedSendChatMessage.contains("/inactive")){
                                    for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                        if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                            Server.getGame().getPlayerList().get(i).setReboot(true);

                                        }
                                    }
                                }
                                if (receivedSendChatMessage.contains("/turnLeft")){
                                    for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                        if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                            Robot robot = Server.getGame().getPlayerList().get(i).getRobot();
                                            String newOrientation = getResultingOrientation("counterclockwise", robot);
                                            robot.setOrientation(newOrientation);

                                            PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                                            String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                                            broadcast(serializedPlayerTurning);
                                        }
                                    }
                                }
                                if (receivedSendChatMessage.contains("/turnRight")){
                                    for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                        if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                            Robot robot = Server.getGame().getPlayerList().get(i).getRobot();
                                            String newOrientation = getResultingOrientation("clockwise", robot);
                                            robot.setOrientation(newOrientation);

                                            PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                                            String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                                            broadcast(serializedPlayerTurning);
                                        }
                                    }
                                }
                                if(receivedSendChatMessage.contains("/energy")){
                                    if(Server.isGameStarted()) {
                                        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                            if (Server.getGame().getPlayerList().get(i).getId() == clientId) {
                                                String energyCubes = ">> You have " + Server.getGame().getPlayerList().get(i).getEnergyCubes() + " energy cubes!";

                                                ReceivedChat receivedChat = new ReceivedChat(energyCubes, -9999, true);
                                                String serializedReceivedChat = Serialisierer.serialize(receivedChat);

                                                sendToOneClient(clientId, serializedReceivedChat);
                                            }
                                        }
                                    }
                                }
                            }else {

                                boolean receivedChatisPrivate;
                                if (receivedSendChatTo == - 1) {
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
                            }
                            break;
                        case "PlayCard":
                            Server.setCountPlayerTurns(Server.getCountPlayerTurns() + 1);

                            if (!Server.getGame().getPlayerList().get(Server.getCountPlayerTurns() - 1).isReboot()) {
                                PlayCard playCard = Deserialisierer.deserialize(serializedReceivedString, PlayCard.class);

                                // Card played für Karten Verständnis an alle clients schicken
                                String playCardCard = playCard.getMessageBody().getCard();
                                CardPlayed cardPlayed = new CardPlayed(clientId, playCardCard);
                                String serializedCardPlayed = Serialisierer.serialize(cardPlayed);
                                ServerLogger.writeToServerLog("id: " + clientId + "played: " + playCardCard);

                                if (!playCardCard.equals("Again")) {
                                    lastPlayedCard = playCardCard;
                                }

                                //logik für karteneffekte
                                switch (lastPlayedCard) {
                                    case "BackUp":
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
                                        for(Player player : Server.getGame().getPlayerList()){
                                            if(player.getId() == clientId){
                                                player.setEnergyCubes(player.getEnergyCubes() + 1);
                                            }
                                        }
                                        break;
                                    case "TurnRight":
                                        RightTurn.makeEffect(this.robot);
                                        int clientIDRightTurn = this.clientId;
                                        PlayerTurning playerTurningRight = new PlayerTurning(clientIDRightTurn, "clockwise");
                                        String serializedPlayerTurningRight = Serialisierer.serialize(playerTurningRight);
                                        broadcast(serializedPlayerTurningRight);
                                        break;
                                    case "TurnLeft":
                                        LeftTurn.makeEffect(this.robot);
                                        int clientIDLeftTurn = this.clientId;
                                        PlayerTurning playerTurningLeft = new PlayerTurning(clientIDLeftTurn, "counterclockwise");
                                        String serializedPlayerTurningLeft = Serialisierer.serialize(playerTurningLeft);
                                        broadcast(serializedPlayerTurningLeft);
                                        break;
                                    case "UTurn":
                                        UTurn.makeEffect(this.robot);
                                        int clientIDUTurn = this.clientId;
                                        PlayerTurning playerTurningUTurn = new PlayerTurning(clientIDUTurn, "clockwise");
                                        String serializedPlayerTurningUTurn = Serialisierer.serialize(playerTurningUTurn);
                                        //send twice turn by 90 degrees in order to end up turning 180 degrees
                                        broadcast(serializedPlayerTurningUTurn);
                                        Thread.sleep(750);
                                        broadcast(serializedPlayerTurningUTurn);
                                        break;
                                    case "Spam":
                                        playTopOfProgDeck();
                                        break;
                                    case "TrojanHorse":
                                        for(Player player : Server.getGame().getPlayerList()) {
                                            if (player.getId() == clientId) {
                                                for (int i = 0; i < 2; i++) {
                                                    if (Server.getGame().getSpam() > 0) {
                                                        Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                                        player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                                        player.getPlayerMat().getDiscardPile().add("Spam");

                                                    } else {
                                                        player.setDamageCounter(player.getDamageCounter() + 1);
                                                    }
                                                }
                                            }
                                        }
                                        playTopOfProgDeck();
                                        break;
                                    case "Virus":
                                        playVirus();
                                        playTopOfProgDeck();
                                        break;
                                    case "Worm":
                                        for(Player player : Server.getGame().getPlayerList()) {
                                            if(player.getId() == clientId) {
                                                int x = player.getRobot().getX();
                                                if (Server.getGame().getBoardClass().getBordName().equals("Death Trap")) {
                                                    if (x <= 9) {
                                                        rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "rebootField");
                                                    } else if (x > 9) {
                                                        rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "startingPoint");
                                                    }
                                                } else if (x < 3) {
                                                    rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "startingPoint");
                                                } else if (x >= 3) {
                                                    rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "rebootField");
                                                }
                                            }
                                        }
                                        break;
                                    default:
                                        ServerLogger.writeToServerLog("unknown card name");
                                        break;


                                }

                                broadcast(serializedCardPlayed);
                                Thread.sleep(750);
                            }

                            // wenn letzter Player aus PlayerList dran ist
                            if (Server.getCountPlayerTurns() == Server.getGame().getPlayerList().size()) {
                                if(Server.isDisconnected()) {
                                    for(Player player : Server.getDisconnectedPlayer()){
                                        Server.getPlayerList().remove(player);
                                    }
                                    Server.getDisconnectedPlayer().clear();
                                    Server.setDisconnected(false);
                                }

                                fieldActivation();

                                for (Player player : Server.getGame().getPlayerList()) {
                                    if (!player.getPlayerMat().getReceivedDamageCards().isEmpty() && player.getDamageCounter() == 0) {
                                        DrawDamage drawDamage = new DrawDamage(player.getId(), player.getPlayerMat().getReceivedDamageCards());
                                        String serializedDrawDamage = Serialisierer.serialize(drawDamage);
                                        broadcast(serializedDrawDamage);
                                        player.getPlayerMat().getReceivedDamageCards().clear();
                                    }else if(player.getDamageCounter() > 0){
                                        ArrayList<String> avaiableDamage = new ArrayList<>();
                                        if(Server.getGame().getVirus() > 0){
                                            avaiableDamage.add("Virus");
                                        }
                                        if(Server.getGame().getTrojanHorse() > 0){
                                            avaiableDamage.add("TrojanHorse");
                                        }
                                        if(Server.getGame().getWurm() > 0){
                                            avaiableDamage.add("Worm");
                                        }
                                        if(!avaiableDamage.isEmpty()) {
                                            Server.setNumPickDamage(Server.getNumPickDamage()+1);

                                            PlayerStatus allWait = new PlayerStatus(-9999, true);
                                            String serializedAllWait = Serialisierer.serialize(allWait);
                                            broadcast(serializedAllWait);

                                            PickDamage pickDamage = new PickDamage(player.getDamageCounter(), avaiableDamage);
                                            String serializedPickDamage = Serialisierer.serialize(pickDamage);
                                            sendToOneClient(player.getId(), serializedPickDamage);
                                        }
                                    }
                                }

                                Server.getGame().setNextPlayersTurn(); // setze playerIndex = 0, PlayerList mit neuen Priorities, currentPlayer = playerList.get(playerIndex), playerIndex++

                                if (Server.getRegisterCounter() <= 4) {
                                    // n. register wird an alle gesendet
                                    ArrayList<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        activeCards.add(new CurrentCards.ActiveCard(player.getId(), player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter()))); // n. element aus register von jedem Player
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
                                } else { // Server.getRegisterCounter() größer 4
                                    for (Player player : Server.getPlayerList()) {
                                        player.setReboot(false);
                                    }
                                    Server.setRegisterCounter(0);
                                    Server.setCountPlayerTurns(0);
                                    Server.setTimerSend(0);
                                    setPlayerFertig(true);
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        player.getPlayerMat().setNumRegister(0);
                                    }
                                    Server.getGame().nextCurrentPhase();

                                    for (Player player : Server.getGame().getPlayerList()) {
                                        ServerLogger.writeToServerLog(player.getId() + " discardPile: " + player.getPlayerMat().getDiscardPile());
                                    }

                                    for (Player player : Server.getGame().getPlayerList()) {
                                        for (int i = 0; i < 5; i++) {
                                            ServerLogger.writeToServerLog(player.getPlayerMat().getRegisterIndex(i));
                                        }
                                    }

                                    // entferne nachdem alle register in Phase 2 abgearbeitet wurden von jedem player das gesamte register
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        player.getPlayerMat().clearRegister();
                                    }

                                    // teste ob alle register leer
                                    for (Player player : Server.getGame().getPlayerList()) {
                                        for (int i = 0; i < 5; i++) {
                                            ServerLogger.writeToServerLog(player.getPlayerMat().getRegisterIndex(i));
                                        }
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
                                                ServerLogger.writeToServerLog(player.getId() + ": " + card);
                                            }

                                            // sendet Karten Infos an aktuellen player
                                            YourCards yourCards = new YourCards(clientCards);
                                            String serializedYourCards = Serialisierer.serialize(yourCards);
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
                                        } else { // wenn weniger als 9 Karten auf ProgDeck
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
                                                ServerLogger.writeToServerLog(player.getId() + ": " + card);
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

                                            player.getPlayerMat().getDiscardPile().clear();

                                            YourCards yourCards = new YourCards(clientCards);
                                            String serializedYourCards = Serialisierer.serialize(yourCards);
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
                            break;
                        case "SetStartingPoint":
                            startingPointSet = true;
                            Server.setCountPlayerTurns(Server.getCountPlayerTurns() + 1);
                            ServerLogger.writeToServerLog("SetStartingPoints");
                            SetStartingPoint setStartingPoint = Deserialisierer.deserialize(serializedReceivedString, SetStartingPoint.class);

                            StartingPointTaken startingPointTaken = new StartingPointTaken(setStartingPoint.getMessageBody().getX(), setStartingPoint.getMessageBody().getY(), clientId);
                            ServerLogger.writeToServerLog("StartingPointTaken of " + clientId + ": " + setStartingPoint.getMessageBody().getX() + ", " + setStartingPoint.getMessageBody().getY());

                            if (Server.getGameMap().getBordName().equals("Death Trap")) {
                                this.robot = new Robot(0, 0, "left");
                            } else {
                                this.robot = new Robot(0, 0, "right");
                            }

                            this.robot.setY(setStartingPoint.getMessageBody().getY());
                            this.robot.setX(setStartingPoint.getMessageBody().getX());

                            this.robot.setStartingPointX(setStartingPoint.getMessageBody().getX());
                            this.robot.setStartingPointY(setStartingPoint.getMessageBody().getY());

                            Player associatedPlayer;
                            //find player and set the robot for that player
                            for (Player player: Server.getPlayerList()){
                                if(player.getId() == clientId){
                                    associatedPlayer = player;
                                    associatedPlayer.setRobot(this.robot);
                                }
                            }


                            // Add the game class(which implements RobotPositionChangeListener)as a listener to the robot
                            this.robot.addPositionChangeListener(Server.getGame());

                            String serializedStartingPointTaken = Serialisierer.serialize(startingPointTaken);
                            broadcast(serializedStartingPointTaken);

                            if (Server.getCountPlayerTurns() == Server.getGame().getPlayerList().size()) { // wenn alle spieler in der aktuellen Phase dran waren -> gehe in nächste Phase
                                Server.getGame().nextCurrentPhase(); // wenn Phase 2 (Programming Phase): jeder player bekommt progDeck in seine playerMat
                                Server.setCountPlayerTurns(0);  // in neuer Phase: keiner dran bisher

                                if(Server.isDisconnected()) {
                                    for(Player player : Server.getDisconnectedPlayer()){
                                        Server.getPlayerList().remove(player);
                                    }
                                    Server.getDisconnectedPlayer().clear();
                                    Server.setDisconnected(false);
                                }

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
                                            ServerLogger.writeToServerLog(player.getId() + ": " + card);
                                        }

                                        // sendet Karten Infos an aktuellen player
                                        YourCards yourCards = new YourCards(clientCards);
                                        String serializedYourCards = Serialisierer.serialize(yourCards);
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
                            ServerLogger.writeToServerLog("Selected Card");
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

                                        ServerLogger.writeToServerLog(Server.getGame().getPlayerList().get(i).getPlayerMat().getNumRegister());
                                    }
                                }
                            }
                            for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
                                // füge in register card hinzu
                                if (Server.getGame().getPlayerList().get(i).getId() == clientId && card != null) {
                                    //gelegte Karte dem Register hinzufügen
                                    Server.getGame().getPlayerList().get(i).getPlayerMat().setRegisterIndex(cardRegister - 1, card);
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
                                            Server.getGame().getPlayerList().get(i).getPlayerMat().getRegisterIndex(cardRegister - 1));
                                    //letze Karte vom Register entfernen
                                    Server.getGame().getPlayerList().get(i).getPlayerMat().setRegisterIndex(cardRegister - 1, null);
                                }
                            }

                            for (Player player : Server.getGame().getPlayerList()) {
                                for (int i = 0; i < 5; i++) {
                                    ServerLogger.writeToServerLog(player.getPlayerMat().getRegisterIndex(i));
                                }
                            }

                            ServerLogger.writeToServerLog(card);
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
                                }
                            }
                            break;
                        case "TimerStarted":
                            Server.setTimerSend(Server.getTimerSend() + 1);
                            if (Server.getTimerSend() == 1) {
                                // sende TimerStarted
                                TimerStarted timerStarted = new TimerStarted();
                                String serializedTimerStarted = Serialisierer.serialize(timerStarted);
                                broadcast(serializedTimerStarted);

                                // warte 30 sec bis TimerEnded schicken
                                Timer timer = new Timer();
                                Timer timer1 = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (Server.getTimerSend() == Server.getGame().getPlayerList().size()) {
                                            ServerLogger.writeToServerLog("Everyone finished");
                                            setPlayerFertig(false);
                                            Server.setTimerSend(0);
                                            // nach 30 sec, sende TimerEnded
                                            ArrayList<Integer> clientIDsNotReady = new ArrayList<>();
                                            for (Player player : Server.getGame().getPlayerList()) {
                                                if (player.getPlayerMat().getNumRegister() < 5) {
                                                    clientIDsNotReady.add(player.getId());
                                                }
                                            }
                                            TimerEnded timerEnded = new TimerEnded(clientIDsNotReady);
                                            String serializedTimerEnded = Serialisierer.serialize(timerEnded);
                                            broadcast(serializedTimerEnded);

                                            discardHand();

                                            discardCurrentRegister();

                                            Server.getGame().setNextPlayersTurn();

                                            ArrayList<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
                                            for (Player player : Server.getGame().getPlayerList()) {
                                                activeCards.add(new CurrentCards.ActiveCard(player.getId(),
                                                        player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter())));
                                            }

                                            Server.setRegisterCounter(Server.getRegisterCounter() + 1);

                                            Server.getGame().nextCurrentPhase();

                                            ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                            String serializedActivePhase = Serialisierer.serialize(activePhase);
                                            broadcast(serializedActivePhase);

                                            ServerLogger.writeToServerLog(Server.getGame().getCurrentPhase());

                                            // nulltes register wird an alle gesendet
                                            CurrentCards currentCards = new CurrentCards(activeCards);
                                            String serializedCurrentCards = Serialisierer.serialize(currentCards);
                                            broadcast(serializedCurrentCards);

                                            //Server.setCountPlayerTurns(0);
                                            CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getPlayerList().get(Server.getCountPlayerTurns()).getId());
                                            String serializedCurrentPlayer1 = Serialisierer.serialize(currentPlayer);
                                            broadcast(serializedCurrentPlayer1);
                                            // test
                                            for (CurrentCards.ActiveCard activeCard : currentCards.getMessageBody().getActiveCards()) {
                                                ServerLogger.writeToServerLog(activeCard.getCard());
                                            }
                                            Thread.currentThread().interrupt();

                                            timer1.cancel();
                                            timer.cancel();
                                        }
                                    }
                                };
                                timer.scheduleAtFixedRate(task, 1000, 1000);
                                timer1.schedule(new TimerTask() {
                                    @Override
                                    public void run(){
                                        // nach 30 sec, sende TimerEnded
                                        ArrayList<Integer> clientIDsNotReady = new ArrayList<>();
                                        for (Player player : Server.getGame().getPlayerList()) {
                                            if (player.getPlayerMat().getNumRegister() < 5) {
                                                clientIDsNotReady.add(player.getId());
                                            }
                                        }
                                        TimerEnded timerEnded = new TimerEnded(clientIDsNotReady);
                                        String serializedTimerEnded = Serialisierer.serialize(timerEnded);
                                        broadcast(serializedTimerEnded);


                                        for (Player player : Server.getGame().getPlayerList()) {
                                            if (player.getPlayerMat().getNumRegister() < 5) {
                                                // missingClientCards an betreffende Clients versenden
                                                ArrayList<String> missingClientCards = new ArrayList<>();
                                                // wie viele Felder auf Register sind leer
                                                // karten ziehen & check, ob Again an erster position steht
                                                int i = 0;
                                                int cursor = 0;
                                                if (player.getPlayerMat().getProgDeck().size() >= 5 - player.getPlayerMat().registerSize()) {
                                                    ServerLogger.writeToServerLog(player.getPlayerMat().registerSize());
                                                    while (i < (5 - player.getPlayerMat().getNumRegister())) {
                                                        if (player.getPlayerMat().registerSize() == 0 && player.getPlayerMat().getProgDeck().get(cursor).getName().equals("Again")) { // wenn erstes Register & oberste Karte auf ProgDeck Again
                                                            cursor++;
                                                            if (!player.getPlayerMat().getProgDeck().get(cursor).getName().equals("Again")) { // nächste Karte auf ProgDeck nicht Again
                                                                Card card1 = player.getPlayerMat().getProgDeck().get(cursor); // card = nächste Karte auf ProgDeck des players
                                                                missingClientCards.add(card1.getName()); // card in missingClientCards
                                                                player.getPlayerMat().fillEmptyRegister(card1.getName());
                                                                player.getPlayerMat().getProgDeck().remove(cursor);
                                                                i++;
                                                                cursor = 0;
                                                            } else {
                                                                cursor++;
                                                                Card card1 = player.getPlayerMat().getProgDeck().get(cursor); // card = nächste Karte auf ProgDeck des players
                                                                missingClientCards.add(card1.getName()); // card in missingClientCards
                                                                player.getPlayerMat().fillEmptyRegister(card1.getName());
                                                                player.getPlayerMat().getProgDeck().remove(cursor);
                                                                i++;
                                                                cursor = 0;
                                                            }
                                                        } else { //  wenn nicht erstes Register bzw. oberste Karte auf ProgDeck nicht Again
                                                            Card card1 = player.getPlayerMat().getProgDeck().get(cursor); // card = nächste Karte auf ProgDeck des players
                                                            missingClientCards.add(card1.getName()); // card in missingClientCards
                                                            player.getPlayerMat().fillEmptyRegister(card1.getName());
                                                            player.getPlayerMat().getProgDeck().remove(cursor);
                                                            i++;
                                                        }
                                                    }
                                                } else {
                                                    // nehme restlichen Karten vom ProgDeck
                                                    int numEmptyRegister = 5 - player.getPlayerMat().registerSize();
                                                    int leftCards = player.getPlayerMat().getProgDeck().size();
                                                    int validCardsCounter = 0;
                                                    int j = 0;

                                                    while (j < leftCards) {
                                                        Card card1 = player.getPlayerMat().getProgDeck().get(cursor); // card = nächste Karte auf ProgDeck des players
                                                        if (j == 0 && card1.getName().equals("Again")) {
                                                            player.getPlayerMat().getDiscardPile().add("Again"); // sonst gehen Agains aus ProgDeck verloren
                                                            player.getPlayerMat().getProgDeck().remove(cursor);
                                                            leftCards = player.getPlayerMat().getProgDeck().size();

                                                        } else {
                                                            missingClientCards.add(card1.getName()); // card in missingClientCards
                                                            player.getPlayerMat().fillEmptyRegister(card1.getName());
                                                            player.getPlayerMat().getProgDeck().remove(cursor);
                                                            validCardsCounter++;
                                                            j++;
                                                        }
                                                    }

                                                    ShuffleCoding shuffleCoding = new ShuffleCoding(player.getId());
                                                    String serializedShuffleCoding = Serialisierer.serialize(shuffleCoding);
                                                    sendToOneClient(player.getId(), serializedShuffleCoding);

                                                    ArrayList<Card> newDrawPile = stringToCard(player.getPlayerMat().getDiscardPile());
                                                    Collections.shuffle(newDrawPile);
                                                    player.getPlayerMat().setProgDeck(newDrawPile);
                                                    player.getPlayerMat().getDiscardPile().clear();

                                                    j = 0;
                                                    while (j < numEmptyRegister - validCardsCounter) { // restliche fehlende Karten
                                                        Card card1 = player.getPlayerMat().getProgDeck().get(cursor); // card = nächste Karte auf ProgDeck des players
                                                        if (j == 0 && card1.getName().equals("Again")) {
                                                            player.getPlayerMat().getDiscardPile().add("Again"); // sonst gehen Agains aus ProgDeck verloren
                                                            player.getPlayerMat().getProgDeck().remove(cursor);
                                                        } else {
                                                            missingClientCards.add(card1.getName()); // card in missingClientCards
                                                            player.getPlayerMat().fillEmptyRegister(card1.getName());
                                                            player.getPlayerMat().getProgDeck().remove(cursor);
                                                            j++;
                                                        }
                                                    }
                                                    ServerLogger.writeToServerLog(player.getId() + "'s missing Cards: " + missingClientCards);
                                                }
                                                CardsYouGotNow cardsYouGotNow = new CardsYouGotNow(missingClientCards);
                                                String serializedCardYouGotNow = Serialisierer.serialize(cardsYouGotNow);
                                                sendToOneClient(player.getId(), serializedCardYouGotNow);
                                            }
                                        }

                                        discardHand();

                                        discardCurrentRegister();

                                        Server.getGame().setNextPlayersTurn();

                                        ArrayList<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
                                        for (Player player : Server.getGame().getPlayerList()) {
                                            activeCards.add(new CurrentCards.ActiveCard(player.getId(),
                                                    player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter())));
                                        }

                                        Server.setRegisterCounter(Server.getRegisterCounter() + 1);

                                        Server.getGame().nextCurrentPhase();

                                        ActivePhase activePhase = new ActivePhase(Server.getGame().getCurrentPhase());
                                        String serializedActivePhase = Serialisierer.serialize(activePhase);
                                        broadcast(serializedActivePhase);

                                        ServerLogger.writeToServerLog(Server.getGame().getCurrentPhase());

                                        // nulltes register wird an alle gesendet
                                        CurrentCards currentCards = new CurrentCards(activeCards);
                                        String serializedCurrentCards = Serialisierer.serialize(currentCards);
                                        broadcast(serializedCurrentCards);

                                        //Server.setCountPlayerTurns(0);
                                        CurrentPlayer currentPlayer = new CurrentPlayer(Server.getGame().getPlayerList().get(Server.getCountPlayerTurns()).getId());
                                        String serializedCurrentPlayer1 = Serialisierer.serialize(currentPlayer);
                                        broadcast(serializedCurrentPlayer1);
                                        // test
                                        for (CurrentCards.ActiveCard activeCard : currentCards.getMessageBody().getActiveCards()) {
                                            ServerLogger.writeToServerLog(activeCard.getCard());
                                        }
                                        timer.cancel();
                                        timer1.cancel();
                                    }
                                }, 30000);
                            }
                            break;
                        case "SelectedDamage":
                            ServerLogger.writeToServerLog("SelectedDamage");
                            for(Player player : Server.getGame().getPlayerList()) {
                                if(player.getId() == clientId) {
                                    SelectedDamage selectedDamage = Deserialisierer.deserialize(serializedReceivedString, SelectedDamage.class);
                                    int i = 0;
                                    ArrayList<String> selectedDamageList = selectedDamage.getMessageBody().getCards();
                                    int damageListSize = selectedDamageList.size();
                                    while (i < damageListSize) {
                                        String damage = selectedDamageList.get(0);
                                        switch (damage) {
                                            case "Virus":
                                                if (Server.getGame().getVirus() > 0) {
                                                    player.getPlayerMat().getReceivedDamageCards().add(selectedDamageList.get(i));
                                                    player.getPlayerMat().getDiscardPile().add(selectedDamageList.get(i));
                                                    Server.getGame().setVirus(Server.getGame().getVirus()-1);
                                                    player.setDamageCounter(player.getDamageCounter()-1);
                                                }
                                                break;
                                            case "TrojanHorse":
                                                if (Server.getGame().getTrojanHorse() > 0) {
                                                    player.getPlayerMat().getReceivedDamageCards().add(selectedDamageList.get(i));
                                                    player.getPlayerMat().getDiscardPile().add(selectedDamageList.get(i));
                                                    Server.getGame().setTrojanHorse(Server.getGame().getTrojanHorse()-1);
                                                    player.setDamageCounter(player.getDamageCounter()-1);
                                                }
                                                break;
                                            case "Worm":
                                                if (Server.getGame().getWurm() > 0) {
                                                    player.getPlayerMat().getReceivedDamageCards().add(selectedDamageList.get(i));
                                                    player.getPlayerMat().getDiscardPile().add(selectedDamageList.get(i));
                                                    Server.getGame().setWurm(Server.getGame().getWurm()-1);
                                                    player.setDamageCounter(player.getDamageCounter()-1);
                                                }
                                                break;
                                        }
                                        i++;
                                    }
                                    if(player.getDamageCounter() > 0){
                                        ArrayList<String> avaiableDamage = new ArrayList<>();
                                        if(Server.getGame().getVirus() > 0){
                                            avaiableDamage.add("Virus");
                                        }
                                        if(Server.getGame().getTrojanHorse() > 0){
                                            avaiableDamage.add("TrojanHorse");
                                        }
                                        if(Server.getGame().getWurm() > 0){
                                            avaiableDamage.add("Worm");
                                        }
                                        if(!avaiableDamage.isEmpty()) {
                                            PickDamage pickDamage = new PickDamage(player.getDamageCounter(), avaiableDamage);
                                            String serializedPickDamage = Serialisierer.serialize(pickDamage);
                                            sendToOneClient(player.getId(), serializedPickDamage);
                                        }
                                    }else{
                                        Server.setSelectedDamageCounter(Server.getSelectedDamageCounter()+1);
                                    }
                                }
                            }
                            if(Server.getSelectedDamageCounter() == Server.getNumPickDamage()){
                                Server.setSelectedDamageCounter(0);
                                Server.setNumPickDamage(0);

                                for(Player player: Server.getGame().getPlayerList()) {
                                    if (!player.getPlayerMat().getReceivedDamageCards().isEmpty()) {
                                        DrawDamage drawDamage = new DrawDamage(player.getId(), player.getPlayerMat().getReceivedDamageCards());
                                        String serializedDrawDamage = Serialisierer.serialize(drawDamage);
                                        broadcast(serializedDrawDamage);

                                        player.getPlayerMat().getReceivedDamageCards().clear();
                                    }
                                }

                                PlayerStatus allGo = new PlayerStatus(-9999, false);
                                String serializedAllGo = Serialisierer.serialize(allGo);
                                broadcast(serializedAllGo);
                            }
                            ServerLogger.writeToServerLog("Selected Damage ende");
                            break;
                        case "RebootDirection":
                            ServerLogger.writeToServerLog("RebootDirection");
                            RebootDirection rebootDirection = Deserialisierer.deserialize(serializedReceivedString, RebootDirection.class);
                            String newRobotOrientation = rebootDirection.getMessageBody().getDirection();
                            String orientationOfRobot = robot.getOrientation();

                            //entsprechend viele PlayerTurning schicken, bis es passt
                            while (!orientationOfRobot.equals(newRobotOrientation)) {
                                ServerLogger.writeToServerLog(robot.getOrientation());
                                String resultingOrientation = getResultingOrientation("clockwise", robot);
                                robot.setOrientation(resultingOrientation);
                                orientationOfRobot = resultingOrientation;
                                PlayerTurning playerTurning = new PlayerTurning(clientId, "clockwise");
                                String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                                broadcast(serializedPlayerTurning);
                            }
                            break;
                        default:
                            //Error-JSON an Client
                            Error error = new Error("Whoops. That did not work. Try to adjust something.");
                            String serializedError = Serialisierer.serialize(error);
                            writer.println(serializedError);
                            break;
                    }
                }
            } catch (SocketException e) {
                // Handle clientseitiger close
                ServerLogger.writeToServerLog("Client disconnected: ", e);
                //wer disconnect

                ReceivedChat leftPlayerMessage = new ReceivedChat(playerName + " has left the chat.", 999, false);
                String serializedLeftPlayerMessage = Serialisierer.serialize(leftPlayerMessage);
                broadcast(serializedLeftPlayerMessage);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to all clients
     * @param serializedObjectToSend the serialized message to send to all clients
     * @author Franziska, Hasan
     */
    private static void broadcast(String serializedObjectToSend) {
        for (ClientHandler client : clients) {
            client.writer.println(serializedObjectToSend);
        }
    }

    /**
     * sends a message to only on client
     * @param clientId the id of the client for whom the message is
     * @param serializedObject the serialized String that needs to be sent
     * @author Franziska, Hasan
     */
    public void sendToOneClient(int clientId, String serializedObject) {
        for (ClientHandler client : clients) {
            if (client.getClientId() == clientId) {
                client.writer.println(serializedObject);
                return;
            }
        }
        ServerLogger.writeToServerLog("Client with ID " + clientId + " not found.");
    }

    /**
     * handles the robots movement
     * @param moves the amount of steps the robot is supposed to take
     * @param isForward true if it is a move card, false if it is a backup card
     * @throws InterruptedException
     * @author Franziska, Felix
     */
    private void handleRobotMovement(int moves, boolean isForward) throws InterruptedException {
        Player checkPlayer = new Player("", 9999, 9999);
        for (int i = 0; i < moves; i++) {
            for (Player player : Server.getGame().getPlayerList()) {
                if (player.getId() == clientId) {
                    checkPlayer = player;
                }
            }
            if (!checkPlayer.isReboot()) {
                boolean canMove = movePossibleWall(checkRobotField(this.robot), this.robot, isForward);

                if (canMove) {

                    checkForRobotsAndMove(this.robot, isForward);

                    if (!wallFlagMovePush){

                        if (isForward) {
                            MoveI.makeEffect(this.robot);
                            checkRobotField(this.robot);
                        } else {
                            BackUp.makeEffect(this.robot);
                            checkRobotField(this.robot);
                        }
                        Thread.sleep(750);
                    }
                } else {
                    if (isForward) {
                        ServerLogger.writeToServerLog("Roboter id: " + this.clientId + " runs against wall");
                    } else {
                        ServerLogger.writeToServerLog("Roboter id: " + this.clientId + " with back to wall");
                    }
                }

                int x = this.robot.getX();
                int y = this.robot.getY();
                int clientID = this.clientId;

                wallFlagMovePush = false;

                Movement movement = new Movement(clientID, x, y);
                String serializedMovement = Serialisierer.serialize(movement);

                broadcast(serializedMovement);
            }
        }
    }

    /**
     * checks whether there are robots in the way that need to be pushed when moving
     * @param robot the robot that is moving
     * @param isForward true if it is a move card, false if it is a backup card
     * @throws InterruptedException
     * @author Franziska, Felix
     */
    private void checkForRobotsAndMove(Robot robot, boolean isForward) throws InterruptedException {
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

    /**
     * returns boolean whether the robot needs to push another robot or not
     * @param isForward true if it is a move card, false if it is a backup card
     * @param orientation current orientation of the pushing robot
     * @param xPushing x coordinate of the pushing robot
     * @param yPushing y coordinate of the pushing robot
     * @param xFleeing x coordinate of the robot being pushed
     * @param yFleeing y coordinate of the robot being pushed
     * @return returns boolean whether the robot needs to push another robot or not
     * @author Franziska, Felix
     */
    private static boolean shouldPush(boolean isForward, String orientation, int xPushing, int yPushing, int xFleeing, int yFleeing) {
        switch (orientation) {
            case "top":
                boolean topCondition = (isForward && yFleeing == yPushing - 1 && xFleeing == xPushing) ||
                        (!isForward && yFleeing == yPushing + 1 && xFleeing == xPushing);
                ServerLogger.writeToServerLog("Top condition: " + topCondition);
                return topCondition;
            case "right":
                boolean rightCondition = (isForward && xFleeing == xPushing + 1 && yFleeing == yPushing) ||
                        (!isForward && xFleeing == xPushing - 1 && yFleeing == yPushing);
                ServerLogger.writeToServerLog("Right condition: " + rightCondition);
                return rightCondition;
            case "left":
                boolean leftCondition = (isForward && xFleeing == xPushing - 1 && yFleeing == yPushing) ||
                        (!isForward && xFleeing == xPushing + 1 && yFleeing == yPushing);
                ServerLogger.writeToServerLog("932 fleeing x, y: " + xFleeing + yFleeing + " , orientation: " + orientation + " is forward: " + isForward);
                ServerLogger.writeToServerLog("934 pushing x, y: " + xPushing + yPushing );
                ServerLogger.writeToServerLog("Left condition: " + leftCondition);
                return leftCondition;
            case "bottom":
                boolean bottomCondition = (isForward && yFleeing == yPushing + 1 && xFleeing == xPushing) ||
                        (!isForward && yFleeing == yPushing - 1 && xFleeing == xPushing);
                ServerLogger.writeToServerLog("Bottom condition: " + bottomCondition);
                return bottomCondition;
            default:
                return false;
        }
    }

    /**
     * pushes another robot and makes sure that it is not being pushed through a wall
     * @param player player being pushed
     * @param isForward true if it is a move card, false if it is a backup card
     * @param orientation orientation in which the robot is being pushed
     * @throws InterruptedException
     * @author Franziska, Felix
     */
    private static void movePlayerRobot(Player player, boolean isForward, String orientation) throws InterruptedException {
        int x = player.getRobot().getX();
        int y = player.getRobot().getY();

        String standingOn = checkRobotField(player.getRobot());

        if (standingOn.contains("Wall [bottom") && (orientation.equals("bottom") && isForward || orientation.equals("top") && !isForward)) {
            wallFlagMovePush = true;
        } else if (standingOn.contains("Wall [top") && (orientation.equals("top") && isForward || orientation.equals("bottom") && !isForward)) {
            wallFlagMovePush = true;
        } else if (standingOn.contains("Wall [right") && (orientation.equals("right") && isForward || orientation.equals("left") && !isForward)) {
            wallFlagMovePush = true;
        } else if (standingOn.contains("Wall [left") && (orientation.equals("left") && isForward || orientation.equals("right") && !isForward)) {
            wallFlagMovePush = true;
        }

        if(!wallFlagMovePush) {
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

            int xCoordinateNEWPushingRobot = player.getRobot().getX();
            int yCoordinateNEWPushingRobot = player.getRobot().getY();

            //FLAGPOINT PFEIL
            // wall check

            for (Player newFleeingPlayer : Server.getGame().getPlayerList()) {
                int xPlayerFleeingRobot = newFleeingPlayer.getRobot().getX();
                int yPlayerFleeingRobot = newFleeingPlayer.getRobot().getY();

                if (shouldPush(isForward, orientation, xCoordinateNEWPushingRobot, yCoordinateNEWPushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                    movePlayerRobot(newFleeingPlayer, isForward, orientation);
                }

            }

            // IF abfrage FLAG

            if(!wallFlagMovePush) {
                if (Server.getGame().getBoardClass().getBordName().equals("Death Trap")) {
                    if ((y < 0 && x <= 9) || (x < 0) || (y > 9 && x <= 9)) {
                        rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "rebootField");
                    } else if (y < 0 || x > 12 || y > 9) {
                        rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "startingPoint");
                    } else {
                        player.getRobot().setX(x);
                        player.getRobot().setY(y);

                        int updatedX = player.getRobot().getX();
                        int updatedY = player.getRobot().getY();

                        ServerLogger.writeToServerLog(player.getName() + " wird geschoben");

                        Movement movement = new Movement(player.getId(), updatedX, updatedY);
                        String serializedMovement = Serialisierer.serialize(movement);
                        broadcast(serializedMovement);
                    }
                } else if ((y < 0 && x < 3 || (x < 0) || (y > 9 && x < 3))) {
                    rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "startingPoint");
                } else if (y < 0 || x > 12 || y > 9) {
                    rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "rebootField");
                } else {
                    player.getRobot().setX(x);
                    player.getRobot().setY(y);

                    int updatedX = player.getRobot().getX();
                    int updatedY = player.getRobot().getY();

                    ServerLogger.writeToServerLog(player.getName() + " wird geschoben");

                    Movement movement = new Movement(player.getId(), updatedX, updatedY);
                    String serializedMovement = Serialisierer.serialize(movement);
                    broadcast(serializedMovement);
                }
            }
        }
    }

    /**
     * returns whether the robot can move or is walking against a wall
     * @param fieldCheck the field on which the robot is standing
     * @param robot the robot that wants to move
     * @param isForward true if it is a move card, false if it is a backup card
     * @return returns whether the robot can move or is walking against a wall
     * @author Franziska, Felix
     */
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

    /**
     * checks how many players are ready
     * @return the number of players that are ready
     * @author Hasan
     */
    public int checkNumReady() {
        int numReady = 0;
        for (int i = 0; i < Server.getPlayerList().size(); i++) {
            if (Server.getPlayerList().get(i).isReady()) {
                numReady++;
            }
        }
        return numReady;
    }

    /**
     * checks the order in which the ready button was pressed in order to forward the map selection to the next player if necessary
     * @return returns the id of the according player
     * @author Hasan
     */
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

    /**
     * checks of what type the game field is
     * @param robot the robot whose field is being checked
     * @return a String containing all the board elements on the field and there directions
     * @author Franziska, Felix
     */
    private static String checkRobotField(Robot robot) {
        int robotX = robot.getX();
        int robotY = robot.getY();

        List<Field> fields = new ArrayList<>();

        if (Server.getGameMap().getBordName().equals("Dizzy Highway")) {
            DizzyHighway highway = new DizzyHighway();
            fields = highway.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("Extra Crispy")) {
            ExtraCrispy extraCrispy = new ExtraCrispy();
            fields = extraCrispy.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("Death Trap")) {
            DeathTrap deathTrap = new DeathTrap();
            fields = deathTrap.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("Lost Bearings")) {
            LostBearings lostBearings = new LostBearings();
            fields = lostBearings.getFieldsAt(robotX, robotY);
        }

        //tester string
        ServerLogger.writeToServerLog("Fields at (" + robotX + ", " + robotY + "): " + fields);

        StringBuilder result = new StringBuilder();

        for (Field field : fields) {
            if (field instanceof ConveyorBelt) {
                ServerLogger.writeToServerLog("ConveyorBelt");
                String[] orientations = field.getOrientation();
                int speed = field.getSpeed();

                result.append("ConveyorBelt " + speed + " " + Arrays.toString(orientations) + ", ");

            } else if (field instanceof Laser) {
                ServerLogger.writeToServerLog("Laser");
                result.append("Laser, ");
            } else if (field instanceof Wall) {
                ServerLogger.writeToServerLog("Wall");
                String[] orientations = field.getOrientation();
                result.append("Wall " + Arrays.toString(orientations) + ", ");
            } else if (field instanceof Empty) {
                ServerLogger.writeToServerLog("Empty field");
                result.append("Empty, ");
            } else if (field instanceof StartPoint) {
                ServerLogger.writeToServerLog("Start point");
                result.append("StartPoint, ");
            } else if (field instanceof CheckPoint) {
                ServerLogger.writeToServerLog("Checkpoint");
                int checkPointNumber = field.getCheckPointNumber();
                result.append("CheckPoint [" + checkPointNumber + "], ");
            } else if (field instanceof EnergySpace) {
                result.append("EnergySpace, ");
            } else if (field instanceof Pit) {
                result.append("Pit, ");
                rebootThisRobot(robotX, robotY, "rebootField");
            } else if (field instanceof PushPanel) {
                String[] orientations = field.getOrientation();
                int[] registers = field.getRegisters();
                result.append("PushPanel " + Arrays.toString(orientations) + " " + Arrays.toString(registers) + ", ");
            } else if (field instanceof Gear) {
                String[] orientation = field.getOrientation();
                result.append("Gear " + Arrays.toString(orientation) + ", ");
            } else {
                ServerLogger.writeToServerLog("Field nicht gefunden");
                result.append("UnknownField, ");
            }
        }
        // Remove the last comma and space
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        ServerLogger.writeToServerLog(result);
        return result.toString();
    }


    /**
     * checks of what type the game field is
     * @param robotX x coordinate of the robot
     * @param robotY y coordinate of the robot
     * @return a String containing all the board elements on the field and there directions
     * @author Franziska, Felix
     */
    private static String checkRobotFieldForXY(int robotX, int robotY) {

        List<Field> fields = new ArrayList<>();

        if (Server.getGameMap().getBordName().equals("Dizzy Highway")) {
            DizzyHighway highway = new DizzyHighway();
            fields = highway.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("Extra Crispy")) {
            ExtraCrispy extraCrispy = new ExtraCrispy();
            fields = extraCrispy.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("Death Trap")) {
            DeathTrap deathTrap = new DeathTrap();
            fields = deathTrap.getFieldsAt(robotX, robotY);
        } else if (Server.getGameMap().getBordName().equals("Lost Bearings")) {
            LostBearings lostBearings = new LostBearings();
            fields = lostBearings.getFieldsAt(robotX, robotY);
        }

        //tester string
        ServerLogger.writeToServerLog("Fields at (" + robotX + ", " + robotY + "): " + fields);

        StringBuilder result = new StringBuilder();

        for (Field field : fields) {
            if (field instanceof ConveyorBelt) {
                ServerLogger.writeToServerLog("ConveyorBelt");
                String[] orientations = field.getOrientation();
                int speed = field.getSpeed();

                result.append("ConveyorBelt " + speed + " " + Arrays.toString(orientations) + ", ");

            } else if (field instanceof Laser) {
                ServerLogger.writeToServerLog("Laser");
                result.append("Laser, ");
            } else if (field instanceof Wall) {
                ServerLogger.writeToServerLog("Wall");
                String[] orientations = field.getOrientation();
                result.append("Wall " + Arrays.toString(orientations) + ", ");
            } else if (field instanceof Empty) {
                ServerLogger.writeToServerLog("Empty field");
                result.append("Empty, ");
            } else if (field instanceof StartPoint) {
                ServerLogger.writeToServerLog("Start point");
                result.append("StartPoint, ");
            } else if (field instanceof CheckPoint) {
                ServerLogger.writeToServerLog("Checkpoint");
                int checkPointNumber = field.getCheckPointNumber();
                result.append("CheckPoint [" + checkPointNumber + "], ");
            } else if (field instanceof EnergySpace) {
                result.append("EnergySpace, ");
            } else if (field instanceof Pit) {
                result.append("Pit");
                rebootThisRobot(robotX, robotY, "rebootField");
            } else if (field instanceof PushPanel) {
                String[] orientations = field.getOrientation();
                int[] registers = field.getRegisters();
                result.append("PushPanel " + Arrays.toString(orientations) + " " + Arrays.toString(registers) + ", ");
            } else if (field instanceof Gear) {
                String[] orientation = field.getOrientation();
                result.append("Gear " + Arrays.toString(orientation) + ", ");
            } else {
                ServerLogger.writeToServerLog("Field nicht gefunden");
                result.append("UnknownField, ");
            }
        }
        // Remove the last comma and space
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        ServerLogger.writeToServerLog(result);
        return result.toString();
    }

    /**
     * activates all the board elements in the right order
     * @throws InterruptedException
     * @author Franziska, Felix
     */
    public void fieldActivation() throws InterruptedException {
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkBlueConveyorBelts(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkGreenConveyorBelts(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkPushPanels(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkGears(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkBoardLaser(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkRobotLasers(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkEnergySpace(i);
        }
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            checkCheckpoint(i);
        }
    }

    /**
     * checks where the first field containing a blue conveyor belt moves the robot and in which direction they turn it
     * @param i the index of the player list where the robot is
     * @throws InterruptedException
     * @author Franziska, Felix, Hasan
     */
    private void checkBlueConveyorBelts(int i) throws InterruptedException {
        String standingOnBlueConveyor = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnBlueConveyor.contains("ConveyorBelt 2")) {
            Animation animation = new Animation("BlueConveyorBelt");
            String serializedAnimation = Serialisierer.serialize(animation);
            broadcast(serializedAnimation);

            Thread.sleep(750);
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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                checkConveyorBeltAgain(i, secondBlue);

            }
        }
    }

    /**
     * checks where the green conveyor belts move the robot and in which direction they turn it
     * @param i the index of the player list where the robot is
     * @author Franziska, Felix
     */
    private void checkGreenConveyorBelts(int i) {
        String standingOnGreenConveyor = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnGreenConveyor.contains("ConveyorBelt 1")) {
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (standingOnGreenConveyor.contains("ConveyorBelt 1 [top")) {
                int yCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getY() - 1;
                int xCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getX();

                String nextFieldType = checkRobotFieldForXY(xCoordinateNewField, yCoordinateNewField);

                if(!nextFieldType.contains("ConveyorBelt 1")){
                    String orientation = "top";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }


                Server.getGame().getPlayerList().get(i).getRobot().setY(Server.getGame().getPlayerList().get(i).getRobot().getY() - 1);

                Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), Server.getGame().getPlayerList().get(i).getRobot().getX(), Server.getGame().getPlayerList().get(i).getRobot().getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                String secondGreen = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());

                if (!secondGreen.contains("ConveyorBelt 1 [top")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [right")) {

                int yCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getY();
                int xCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getX() + 1;

                String nextFieldType = checkRobotFieldForXY(xCoordinateNewField, yCoordinateNewField);

                if(!nextFieldType.contains("ConveyorBelt 1")){
                    String orientation = "right";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [bottom")) {

                int yCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getY() + 1;
                int xCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getX();

                String nextFieldType = checkRobotFieldForXY(xCoordinateNewField, yCoordinateNewField);

                if(!nextFieldType.contains("ConveyorBelt 1")){
                    String orientation = "bottom";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }


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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("clockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [left")) {

                int yCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getY();
                int xCoordinateNewField = Server.getGame().getPlayerList().get(i).getRobot().getX() - 1;

                String nextFieldType = checkRobotFieldForXY(xCoordinateNewField, yCoordinateNewField);

                if(!nextFieldType.contains("ConveyorBelt 1")){
                    String orientation = "left";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }


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
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        Server.getGame().getPlayerList().get(i).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(i).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * checks whether robots get pushed by a push panel during the field activation
     * @param i the index of the player in the player list
     * @author Franziska, Felix
     */
    private void checkPushPanels(int i) {
        Robot robot = Server.getGame().getPlayerList().get(i).getRobot();
        int robotId = Server.getGame().getPlayerList().get(i).getId();
        String standingOnPushPanel = checkRobotField(robot);
        int currentRegister = Server.getRegisterCounter();
        boolean shouldActivate = false;
        int pushPanelRegister = 0;

        if (standingOnPushPanel.contains("[1, 3, 5]")) {
            pushPanelRegister = 1;

        } else if (standingOnPushPanel.contains("[2, 4]")) {
            pushPanelRegister = 2;
        }

        if (standingOnPushPanel.contains("PushPanel")) {

            if ((currentRegister == 1 || currentRegister == 3 || currentRegister == 5) && (pushPanelRegister == 1)) {
                shouldActivate = true;
            } else if ((currentRegister == 2 || currentRegister == 4) && (pushPanelRegister == 2)) {
                shouldActivate = true;
            }

            if (shouldActivate) {
                if (standingOnPushPanel.contains("PushPanel [top")) {

                    String orientation = "top";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    robot.setY(robot.getY() - 1);

                } else if (standingOnPushPanel.contains("PushPanel [left")) {

                    String orientation = "left";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    robot.setX(robot.getX() - 1);

                } else if (standingOnPushPanel.contains("PushPanel [right")) {

                    String orientation = "right";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    robot.setX(robot.getX() + 1);

                } else if (standingOnPushPanel.contains("PushPanel [bottom")) {

                    String orientation = "bottom";
                    int xCoordinatePushingRobot = robot.getX();
                    int yCoordinatePushingRobot = robot.getY();

                    for (Player player : Server.getGame().getPlayerList()) {
                        int xPlayerFleeingRobot = player.getRobot().getX();
                        int yPlayerFleeingRobot = player.getRobot().getY();

                        if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                            try {
                                movePlayerRobot(player, true, orientation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    robot.setY(robot.getY() + 1);
                }
                Movement movement = new Movement(robotId, robot.getX(), robot.getY());
                String serializedMovement = Serialisierer.serialize(movement);
                broadcast(serializedMovement);

                Animation animation = new Animation("PushPanel");
                String serializedAnimation = Serialisierer.serialize(animation);
                broadcast(serializedAnimation);
            }
        }
    }

    /**
     * checks in which direction the gears turn the robot
     * @param i the index of the player list where the robot is
     * @author Franziska, Felix
     */
    private void checkGears(int i) {
        String standingOnGear = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        Robot robot = Server.getGame().getPlayerList().get(i).getRobot();
        int robotId = Server.getGame().getPlayerList().get(i).getId();
        String resultingOrientation = "init";
        String rotateDirection = "init";

        if (standingOnGear.contains("Gear")) {
            if (standingOnGear.contains("Gear [counterclockwise]")) {
                rotateDirection = "counterclockwise";
                resultingOrientation = getResultingOrientation("counterclockwise", robot);
                robot.setOrientation(resultingOrientation);


            } else if (standingOnGear.contains("Gear [clockwise]")) {
                rotateDirection = "clockwise";
                resultingOrientation = getResultingOrientation("clockwise", robot);
                robot.setOrientation(resultingOrientation);


            }
            PlayerTurning playerTurning = new PlayerTurning(robotId, rotateDirection);
            String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
            broadcast(serializedPlayerTurning);

            Animation animation = new Animation("Gear");
            String serializedAnimation = Serialisierer.serialize(animation);
            broadcast(serializedAnimation);
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * checks which robots get hit by the board laser
     * @param i the index of the player list where the robot is
     * @author Franziska, Felix
     */
    private void checkBoardLaser(int i) {
        String standingOnBoardLaser = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        if (standingOnBoardLaser.contains("Laser")) {
            if (Server.getGame().getSpam() > 0) {
                Animation animation = new Animation("WallShooting");
                String serializedAnimation = Serialisierer.serialize(animation);
                broadcast(serializedAnimation);

                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                Server.getGame().getPlayerList().get(i).getPlayerMat().getReceivedDamageCards().add("Spam");
                Server.getGame().getPlayerList().get(i).getPlayerMat().getDiscardPile().add("Spam");
            }else{
                Server.getGame().getPlayerList().get(i).setDamageCounter(Server.getGame().getPlayerList().get(i).getDamageCounter()+1);
            }
        }
    }

    /**
     * checks whether robots get hit by other robots' lasers
     * @param i the index of the player list where the robot is
     * @author Franziska, Felix, Hasan
     */
    private void checkRobotLasers(int i) {
        String robotOrientation = Server.getGame().getPlayerList().get(i).getRobot().getOrientation();
        Robot yourRobot = Server.getGame().getPlayerList().get(i).getRobot();
        boolean safe = false;

        // if player is rebooting, dont shoot lasers
        if (!Server.getGame().getPlayerList().get(i).isReboot()) {

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
                        int y = yourRobot.getY() - 1;
                        while (y >= targetRobot1.getY()) {
                            Robot robot = new Robot(yourRobot.getX(), y, "top");
                            String checkWall = checkRobotField(robot);
                            if (checkWall.contains("Wall [bottom")) {
                                safe = true;
                                break;
                            }
                            y--;
                        }
                        if (!safe) {
                            for (Player player : Server.getGame().getPlayerList()) {
                                if (player.getRobot().equals(targetRobot1)) {
                                    if (Server.getGame().getSpam() > 0) {
                                        Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                        player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                        player.getPlayerMat().getDiscardPile().add("Spam");

                                        Animation animation = new Animation("PlayerShooting");
                                        String serializedAnimation = Serialisierer.serialize(animation);
                                        broadcast(serializedAnimation);
                                    }else {
                                        Server.getGame().getPlayerList().get(i).setDamageCounter(Server.getGame().getPlayerList().get(i).getDamageCounter()+1);
                                    }
                                }
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
                        int x = yourRobot.getX() + 1;
                        while (x <= targetRobot2.getX()) {
                            Robot robot = new Robot(x, yourRobot.getY(), "right");
                            String checkWall = checkRobotField(robot);
                            if (checkWall.contains("Wall [left")) {
                                safe = true;
                                break;
                            }
                            x++;
                        }
                        if (!safe) {
                            for (Player player : Server.getGame().getPlayerList()) {
                                if (player.getRobot().equals(targetRobot2)) {
                                    if (Server.getGame().getSpam() > 0) {
                                        Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                        player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                        player.getPlayerMat().getDiscardPile().add("Spam");

                                        Animation animation = new Animation("PlayerShooting");
                                        String serializedAnimation = Serialisierer.serialize(animation);
                                        broadcast(serializedAnimation);
                                    }else {
                                        Server.getGame().getPlayerList().get(i).setDamageCounter(Server.getGame().getPlayerList().get(i).getDamageCounter()+1);
                                    }
                                }
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
                        int y = yourRobot.getY() + 1;
                        while (y <= targetRobot3.getY()) {
                            Robot robot = new Robot(yourRobot.getX(), y, "bottom");
                            String checkWall = checkRobotField(robot);
                            if (checkWall.contains("Wall [top")) {
                                safe = true;
                                break;
                            }
                            y++;
                        }
                        if (!safe) {
                            for (Player player : Server.getGame().getPlayerList()) {
                                if (player.getRobot().equals(targetRobot3)) {
                                    if (Server.getGame().getSpam() > 0) {
                                        Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                        player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                        player.getPlayerMat().getDiscardPile().add("Spam");

                                        Animation animation = new Animation("PlayerShooting");
                                        String serializedAnimation = Serialisierer.serialize(animation);
                                        broadcast(serializedAnimation);
                                    }else {
                                        Server.getGame().getPlayerList().get(i).setDamageCounter(Server.getGame().getPlayerList().get(i).getDamageCounter()+1);
                                    }
                                }
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
                        int x = yourRobot.getX() - 1;
                        while (x >= targetRobot4.getX()) {
                            Robot robot = new Robot(x, yourRobot.getY(), "left");
                            String checkWall = checkRobotField(robot);
                            if (checkWall.contains("Wall [right")) {
                                safe = true;
                                break;
                            }
                            x--;
                        }
                        if (!safe) {
                            for (Player player : Server.getGame().getPlayerList()) {
                                if (player.getRobot().equals(targetRobot4)) {
                                    if (Server.getGame().getSpam() > 0) {
                                        Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                        player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                        player.getPlayerMat().getDiscardPile().add("Spam");

                                        Animation animation = new Animation("PlayerShooting");
                                        String serializedAnimation = Serialisierer.serialize(animation);
                                        broadcast(serializedAnimation);
                                    }else {
                                        Server.getGame().getPlayerList().get(i).setDamageCounter(Server.getGame().getPlayerList().get(i).getDamageCounter()+1);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * checks whether a robot is on a checkpoint at the end of a register
     * @param i the index of the player list where the robot is
     * @author Franziska, Hasan
     */
    private void checkCheckpoint(int i) {
        String standingOnCheckPoint = checkRobotField(Server.getGame().getPlayerList().get(i).getRobot());
        int clientIdOfCheckpointReacher = Server.getGame().getPlayerList().get(i).getId();

        if (standingOnCheckPoint.contains("CheckPoint [1")) {
            if (Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 0) { //check whether no checkPoints were reached before

                CheckPointReached checkPointReached = new CheckPointReached(clientIdOfCheckpointReacher, 1);
                String serialisedCheckPointReached = Serialisierer.serialize(checkPointReached);
                broadcast(serialisedCheckPointReached);

                checkGameFinished(i);
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [2")) {
            if (Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 1) { //check whether one checkPoint was reached before

                CheckPointReached checkPointReached = new CheckPointReached(clientIdOfCheckpointReacher, 2);
                String serialisedCheckPointReached = Serialisierer.serialize(checkPointReached);
                broadcast(serialisedCheckPointReached);

                checkGameFinished(i);
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [3")) {
            if (Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 2) {

                CheckPointReached checkPointReached = new CheckPointReached(clientIdOfCheckpointReacher, 3);
                String serialisedCheckPointReached = Serialisierer.serialize(checkPointReached);
                broadcast(serialisedCheckPointReached);

                checkGameFinished(i);
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [4")) {
            if (Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 3) {

                CheckPointReached checkPointReached = new CheckPointReached(clientIdOfCheckpointReacher, 4);
                String serialisedCheckPointReached = Serialisierer.serialize(checkPointReached);
                broadcast(serialisedCheckPointReached);

                checkGameFinished(i);
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [5")) {
            if (Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() == 4) {

                CheckPointReached checkPointReached = new CheckPointReached(clientIdOfCheckpointReacher, 5);
                String serialisedCheckPointReached = Serialisierer.serialize(checkPointReached);
                broadcast(serialisedCheckPointReached);



                checkGameFinished(i);
            }
        }
    }

    /**
     * checks whether someone has won because he has enough checkpoint tokens
     * @param i the index of the player list where the robot is
     * @author Franziska, Felix
     */
    private void checkGameFinished(int i) {
        Server.getGame().getPlayerList().get(i).getPlayerMat().setTokenCount(Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount() + 1);
        int playerTokenAmount = Server.getGame().getPlayerList().get(i).getPlayerMat().getTokenCount();

        if (Server.getGame().getBoardClass().getCheckpointAmount() == playerTokenAmount) {
            GameFinished gameFinished = new GameFinished(Server.getGame().getPlayerList().get(i).getId());
            String serializedGameFinished = Serialisierer.serialize(gameFinished);
            broadcast(serializedGameFinished);
        }
    }

    /**
     * checks the second field of the blue conveyor belt
     * @param j the index of the player list where the robot is
     * @param standingOnBlueConveyorBelt the field string where you were standing after the first blue conveyor
     * @throws InterruptedException
     * @author Franziska, Felix, Hasan
     */
    private void checkConveyorBeltAgain(int j, String standingOnBlueConveyorBelt) throws InterruptedException {
        if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2")) {
            Thread.sleep(750);
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
                        Thread.sleep(750);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [left")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        Thread.sleep(750);
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
                        Thread.sleep(750);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [top")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        Thread.sleep(750);
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
                        Thread.sleep(750);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [right")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        Thread.sleep(750);
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
                        Thread.sleep(750);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [bottom")) {
                        Server.getGame().getPlayerList().get(j).getRobot().setOrientation(getResultingOrientation("counterclockwise", Server.getGame().getPlayerList().get(j).getRobot()));
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(j).getId(), "counterclockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                        Thread.sleep(750);
                    }
                }

            }
        }


    }

    /**
     * depending on which way the robot is being turned it returns the right string to set its direction to
     * @param turningDirection either clockwise or counterclockwise
     * @param robot the robot that is being turned
     * @return string to which the robots direction needs to be set
     * @author Franziska, Felix
     */
    private static String getResultingOrientation(String turningDirection, Robot robot) {
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

    /**
     * puts the current register's card into the discard pile or if it is a damage card back into the damage card piles
     * @author Hasan, Maximilian
     */
    public void discardCurrentRegister() {
        for (Player player : Server.getGame().getPlayerList()) {
            if (player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter()).equals("Spam")) {
                Server.getGame().setSpam(Server.getGame().getSpam() + 1);
            } else if (player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter()).equals("TrojanHorse")) {
                Server.getGame().setTrojanHorse(Server.getGame().getTrojanHorse() + 1);
            } else if (player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter()).equals("Virus")) {
                Server.getGame().setVirus(Server.getGame().getVirus() + 1);
            } else if (player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter()).equals("Worm")) {
                Server.getGame().setWurm(Server.getGame().getWurm() + 1);
            } else {
                // füge sonst dem player in playerMat in den discardPile das n. Register
                player.getPlayerMat().getDiscardPile().add(player.getPlayerMat().getRegisterIndex(Server.getRegisterCounter()));
            }
        }
    }

    /**
     * puts all of the remaining cards that were not played into the discard pile
     * @author Hasan
     */
    public void discardHand() {
        for (Player player : Server.getGame().getPlayerList()) {
            for (String card : player.getPlayerMat().getClientHand()) {
                player.getPlayerMat().getDiscardPile().add(card);
            }
            player.getPlayerMat().getClientHand().clear(); // Leere die Hand des Spielers
            for (String discardCard : player.getPlayerMat().getDiscardPile()) {
                ServerLogger.writeToServerLog(player.getId() + ": " + discardCard);
            }
        }
    }

    /**
     * turns a string into the according card object
     * @param stringCards takes in an ArrayList of Strings that represent cards
     * @return the pile of card objects saved as an array list
     * @author Hasan, Franziska
     */
    public ArrayList<Card> stringToCard(ArrayList<String> stringCards) {
        ArrayList<Card> cardPile = new ArrayList<>();
        for (String cardName : stringCards) {
            switch (cardName) {
                case "Again":
                    cardPile.add(new Again());
                    break;
                case "BackUp":
                    cardPile.add(new BackUp());
                    break;
                case "TurnLeft":
                    cardPile.add(new LeftTurn());
                    break;
                case "MoveI":
                    cardPile.add(new MoveI());
                    break;
                case "MoveII":
                    cardPile.add(new MoveII());
                    break;
                case "MoveIII":
                    cardPile.add(new MoveIII());
                    break;
                case "PowerUp":
                    cardPile.add(new PowerUp());
                    break;
                case "TurnRight":
                    cardPile.add(new RightTurn());
                    break;
                case "UTurn":
                    cardPile.add(new UTurn());
                    break;
                case "TrojanHorse":
                    cardPile.add(new TrojanHorse());
                    break;
                case "Spam":
                    cardPile.add(new Spam());
                    break;
                case "Virus":
                    cardPile.add(new Virus());
                    break;
                case "Worm":
                    cardPile.add(new Wurm());
                    break;
            }
        }
        return cardPile;
    }

    /**
     * reboots the robot to the according field, depending on where he walks off the board
     * turns the robot in default direction "top" and then opens the reboot direction selection dialog
     * @param xCoordinate the robot's x coordinate
     * @param yCoordinate the robot's y coordinate
     * @param rebootTo string that tells you where to reboot the robot to ("rebootField" or "startingPoint")
     * @author Franziska, Felix, Hasan
     */
    public static void rebootThisRobot(int xCoordinate, int yCoordinate, String rebootTo) {
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            if ((Server.getGame().getPlayerList().get(i).getRobot().getX() == xCoordinate) &&
                    (Server.getGame().getPlayerList().get(i).getRobot().getY() == yCoordinate)) {
                Robot robot = Server.getGame().getPlayerList().get(i).getRobot();
                Server.getGame().getPlayerList().get(i).setReboot(true);

                ReceivedChat joinedPlayerMessage = new ReceivedChat(Server.getGame().getPlayerList().get(i).getName() + " is rebooting", 999, false);
                String serializedJoinedPlayerMessage = Serialisierer.serialize(joinedPlayerMessage);
                broadcast(serializedJoinedPlayerMessage);

                //if robot rebooted he receives two spam cards
                for(int j = 0; j < 2; j++) {
                    if(Server.getGame().getSpam() > 0) {
                        Server.getGame().getPlayerList().get(i).getPlayerMat().getDiscardPile().add("Spam");
                        Server.getGame().getPlayerList().get(i).getPlayerMat().getReceivedDamageCards().add("Spam");
                        Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                    }else{
                        Server.getGame().getPlayerList().get(i).setDamageCounter(Server.getGame().getPlayerList().get(i).getDamageCounter()+1);
                    }
                }

                if (rebootTo.equals("rebootField")) {


                    if(robotOnThisField(Server.getGame().getBoardClass().getRebootX(), Server.getGame().getBoardClass().getRebootY())){

                        // push found robots in direction of reboot field
                        String orientation = Server.getGame().getBoardClass().getOrientationOfReboot();
                        int xCoordinatePushingRobot = Server.getGame().getBoardClass().getRebootX();
                        int yCoordinatePushingRobot = Server.getGame().getBoardClass().getRebootY();

                        for (Player player : Server.getGame().getPlayerList()) {
                            int xPlayerFleeingRobot = player.getRobot().getX();
                            int yPlayerFleeingRobot = player.getRobot().getY();

                            if (shouldPush(true, orientation, xCoordinatePushingRobot, yCoordinatePushingRobot, xPlayerFleeingRobot, yPlayerFleeingRobot)) {
                                try {
                                    movePlayerRobot(player, true, orientation);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        //robot on field push in direction of reboot
                        for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                            if ((Server.getGame().getPlayerList().get(j).getRobot().getX() == xCoordinatePushingRobot) &&
                                    (Server.getGame().getPlayerList().get(j).getRobot().getY() == yCoordinatePushingRobot)) {
                                Robot robotToMoveFromReboot = Server.getGame().getPlayerList().get(j).getRobot();

                                int x = xCoordinatePushingRobot;
                                int y = yCoordinatePushingRobot;

                                switch (orientation) {
                                    case "top":
                                        y = y - 1;
                                        break;
                                    case "right":
                                        x = x + 1;
                                        break;
                                    case "left":
                                        x = x - 1;
                                        break;
                                    case "bottom":
                                        y =  y + 1;
                                        break;
                                }

                                robotToMoveFromReboot.setX(x);
                                robotToMoveFromReboot.setY(y);

                                Movement movement = new Movement(Server.getGame().getPlayerList().get(j).getId(), robotToMoveFromReboot.getX(), robotToMoveFromReboot.getY());
                                String serializedMovement = Serialisierer.serialize(movement);
                                broadcast(serializedMovement);

                            }
                        }
                    }

                    robot.setX(Server.getGame().getBoardClass().getRebootX());
                    robot.setY(Server.getGame().getBoardClass().getRebootY());
                    robot.setAlreadyRebooted(false);

                    Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), robot.getX(), robot.getY());
                    String serializedMovement = Serialisierer.serialize(movement);
                    broadcast(serializedMovement);

                    //entsprechend viele PlayerTurning schicken, bis es passt
                    String orientationOfRobot = robot.getOrientation();
                    while (!orientationOfRobot.equals("top")) {
                        String resultingOrientation = getResultingOrientation("clockwise", robot);
                        robot.setOrientation(resultingOrientation);
                        orientationOfRobot = resultingOrientation;
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }

                    Reboot reboot = new Reboot(Server.getGame().getPlayerList().get(i).getId());
                    String serializedReboot = Serialisierer.serialize(reboot);
                    broadcast(serializedReboot);


                } else if (rebootTo.equals("startingPoint")) {

                    // loop through taken startpoints until a free one is found, set there || set straightaway
                    if (robotOnThisField(robot.getStartingPointX(), robot.getStartingPointY())){
                        for (int j = 0; j < Server.getGame().getPlayerList().size(); j++) {
                            int xCheckForStartPoint = Server.getGame().getPlayerList().get(j).getRobot().getStartingPointX();
                            int yCheckForStartPoint = Server.getGame().getPlayerList().get(j).getRobot().getStartingPointY();

                            if (!robotOnThisField(xCheckForStartPoint, yCheckForStartPoint)){
                                robot.setX(xCheckForStartPoint);
                                robot.setY(yCheckForStartPoint);
                                robot.setAlreadyRebooted(false);

                                ServerLogger.writeToServerLog("rebooting to other free starting point");
                                break;
                            }
                        }
                    } else {
                        robot.setX(robot.getStartingPointX());
                        robot.setY(robot.getStartingPointY());
                        robot.setAlreadyRebooted(false);
                    }

                    Movement movement = new Movement(Server.getGame().getPlayerList().get(i).getId(), robot.getX(), robot.getY());
                    String serializedMovement = Serialisierer.serialize(movement);
                    broadcast(serializedMovement);

                    //entsprechend viele PlayerTurning schicken, bis es passt
                    String orientationOfRobot = robot.getOrientation();
                    while (!orientationOfRobot.equals("top")) {
                        String resultingOrientation = getResultingOrientation("clockwise", robot);
                        robot.setOrientation(resultingOrientation);
                        orientationOfRobot = resultingOrientation;
                        PlayerTurning playerTurning = new PlayerTurning(Server.getGame().getPlayerList().get(i).getId(), "clockwise");
                        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
                        broadcast(serializedPlayerTurning);
                    }

                    Reboot reboot = new Reboot(Server.getGame().getPlayerList().get(i).getId());
                    String serializedReboot = Serialisierer.serialize(reboot);
                    broadcast(serializedReboot);

                } else {
                    ServerLogger.writeToServerLog("Invalid Reboot String");
                }

            }
        }
    }

    /**
     * checks whether a robot is occupying this field
     * @param xField x coordinate of the field
     * @param yField y coordinate of the field
     * @return true if there is a robot on the field, otherwise false
     * @author Franziska, Felix
     */
    public static boolean robotOnThisField(int xField, int yField) {
        for (int i = 0; i < Server.getGame().getPlayerList().size(); i++) {
            if ((Server.getGame().getPlayerList().get(i).getRobot().getX() == xField) && (Server.getGame().getPlayerList().get(i).getRobot().getY() == yField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * to play the card on top of the programming deck when a damage card is played
     * @throws InterruptedException
     * @author Hasan, Franziska
     */
    public void playTopOfProgDeck() throws InterruptedException {
        String newCard = "";
        for(Player player: Server.getGame().getPlayerList()){
            if(player.getId() == clientId){
                if(!player.getPlayerMat().getProgDeck().isEmpty()){
                    newCard = player.getPlayerMat().getProgDeck().get(0).getName();
                    player.getPlayerMat().getProgDeck().remove(0);
                    if(newCard.equals("Spam") || newCard.equals("TrojanHorse") || newCard.equals("Virus") || newCard.equals("Worm")) {
                        switch(newCard){
                            case "Spam":
                                Server.getGame().setSpam(Server.getGame().getSpam() + 1);
                                break;
                            case "TrojanHorse":
                                Server.getGame().setTrojanHorse(Server.getGame().getTrojanHorse() + 1);
                                break;
                            case "Virus":
                                Server.getGame().setVirus(Server.getGame().getVirus() + 1);
                                break;
                            case "Worm":
                                Server.getGame().setWurm(Server.getGame().getWurm() + 1);
                                break;
                        }
                    }else{
                        player.getPlayerMat().getDiscardPile().add(newCard);
                    }
                }
                switch (newCard) {
                    case "BackUp":
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
                        player.setEnergyCubes(player.getEnergyCubes() + 1);
                        break;
                    case "TurnRight":
                        RightTurn.makeEffect(this.robot);
                        int clientIDRightTurn = this.clientId;
                        PlayerTurning playerTurningRight = new PlayerTurning(clientIDRightTurn, "clockwise");
                        String serializedPlayerTurningRight = Serialisierer.serialize(playerTurningRight);
                        //Thread.sleep(750);
                        broadcast(serializedPlayerTurningRight);
                        break;
                    case "TurnLeft":
                        LeftTurn.makeEffect(this.robot);
                        int clientIDLeftTurn = this.clientId;
                        PlayerTurning playerTurningLeft = new PlayerTurning(clientIDLeftTurn, "counterclockwise");
                        String serializedPlayerTurningLeft = Serialisierer.serialize(playerTurningLeft);
                        //Thread.sleep(750);
                        broadcast(serializedPlayerTurningLeft);
                        break;
                    case "UTurn":
                        UTurn.makeEffect(this.robot);
                        int clientIDUTurn = this.clientId;
                        PlayerTurning playerTurningUTurn = new PlayerTurning(clientIDUTurn, "clockwise");
                        String serializedPlayerTurningUTurn = Serialisierer.serialize(playerTurningUTurn);
                        //send twice turn by 90 degrees in order to end up turning 180 degrees
                        //Thread.sleep(750);
                        broadcast(serializedPlayerTurningUTurn);
                        broadcast(serializedPlayerTurningUTurn);
                        break;
                    case "Spam":
                        playTopOfProgDeck();
                        break;
                    case "TrojanHorse":
                        for(int i = 0; i < 2; i++){
                            if(Server.getGame().getSpam() > 0){
                                Server.getGame().setSpam(Server.getGame().getSpam() - 1);
                                player.getPlayerMat().getReceivedDamageCards().add("Spam");
                                player.getPlayerMat().getDiscardPile().add("Spam");
                            }else{
                                player.setDamageCounter(player.getDamageCounter()+1);
                            }
                        }
                        //play the top card of the draw pile this register
                        playTopOfProgDeck();
                        break;
                    case "Virus":
                        playVirus();
                        playTopOfProgDeck();
                        break;
                    case "Worm":
                        int x = player.getRobot().getX();
                        if (Server.getGame().getBoardClass().getBordName().equals("Death Trap")) {
                            if (x <= 9) {
                                rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "rebootField");
                            } else if (x > 9) {
                                rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "startingPoint");
                            }
                        } else if (x < 3) {
                            rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "startingPoint");
                        } else if (x >= 3) {
                            rebootThisRobot(player.getRobot().getX(), player.getRobot().getY(), "rebootField");
                        }
                        break;
                    default:
                        ServerLogger.writeToServerLog("unknown card name");
                        break;


                }
            }
        }

    }

    /**
     * applies the virus card's effect: ypu have to play the top card of your draw pile this register
     * and robots within a 6 field receive a virus card
     * @throws InterruptedException
     * @author Hasan, Franziska
     */
    public void playVirus() throws InterruptedException {
        Robot robot = new Robot(-9999, -9999, "");
        for(Player player1: Server.getGame().getPlayerList()){
            if(player1.getId() == clientId){
                robot = player1.getRobot();
            }
        }
        //loop through the player list and if it is not your own robot and within 6 fields of you apply the effect
        for(Player player : Server.getGame().getPlayerList()){
            if(!robot.equals(player.getRobot()) && isWithinSixFields(robot, player.getRobot())){
                if(Server.getGame().getSpam() > 0) {
                    //add a virus card to the player's discard pile
                    player.getPlayerMat().getDiscardPile().add("Spam");
                    player.getPlayerMat().getReceivedDamageCards().add("Spam");
                    Server.getGame().setSpam(Server.getGame().getSpam() -1);
                }else{
                    //if Virus cards are empty pick damage
                    player.setDamageCounter(player.getDamageCounter()+1);
                }
            }
        }
    }

    /**
     * checks whether a robot is within 6 fields of another robot
     * @param robotPlayedVirus the robot that played the virus card
     * @param robotToBeChecked the robot that is being checked whether it is within a 6 field radius
     * @return true if the robot is within 6 fields, otherwise false
     * @author Felix, Franziska, Hasan
     */
    public boolean isWithinSixFields(Robot robotPlayedVirus, Robot robotToBeChecked){
        int xRobotPlayedVirus = robotPlayedVirus.getX();
        int yRobotPlayedVirus = robotPlayedVirus.getY();

        int xRobotToBeChecked = robotToBeChecked.getX();
        int yRobotToBeChecked = robotToBeChecked.getY();

        int leftSideX = xRobotPlayedVirus - 6;
        int rightSideX = xRobotPlayedVirus + 6;
        int topY = yRobotPlayedVirus - 6;
        int bottomY = yRobotPlayedVirus + 6;
        if(leftSideX <= xRobotToBeChecked && xRobotToBeChecked <= rightSideX && topY <= yRobotToBeChecked && bottomY >= yRobotToBeChecked){
            ServerLogger.writeToServerLog("The robot " + robotToBeChecked + " is within a six field radius of the robot " + robotPlayedVirus);
            return true;
        }else{
            ServerLogger.writeToServerLog(false + "virus");
            return false;
        }
    }


    /**
     * increases the energy counter of a player when necessary
     * @param i the index of the player in the player list
     * @author Hasan, Franziska
     */
    private void checkEnergySpace(int i){
        Robot robot = Server.getGame().getPlayerList().get(i).getRobot();
        if(Server.getGame().getPlayerList().get(i).getId() == clientId) {
            if (checkRobotField(robot).contains("EnergySpace")) {
                int currentField = 10 * robot.getX() + robot.getY();
                switch (Server.getGameMap().getBordName()) {
                    case "Dizzy Highway":
                        switch (currentField) {
                            case 39:
                                if (Server.getGameMap().getEnergySpace39() == 1) {
                                    Server.getGameMap().setEnergySpace39(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 52:
                                if (Server.getGameMap().getEnergySpace52() == 1) {
                                    Server.getGameMap().setEnergySpace52(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 75:
                                if (Server.getGameMap().getEnergySpace75() == 1) {
                                    Server.getGameMap().setEnergySpace75(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 84:
                                if (Server.getGameMap().getEnergySpace84() == 1) {
                                    Server.getGameMap().setEnergySpace84(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 107:
                                if (Server.getGameMap().getEnergySpace107() == 1) {
                                    Server.getGameMap().setEnergySpace107(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                        }
                        break;
                    case "Extra Crispy":
                        switch (currentField) {
                            case 34:
                                if (Server.getGameMap().getEnergySpace34() == 1) {
                                    Server.getGameMap().setEnergySpace34(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 39:
                                if (Server.getGameMap().getEnergySpace39() == 1) {
                                    Server.getGameMap().setEnergySpace39(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 75:
                                if (Server.getGameMap().getEnergySpace75() == 1) {
                                    Server.getGameMap().setEnergySpace75(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 80:
                                if (Server.getGameMap().getEnergySpace80() == 1) {
                                    Server.getGameMap().setEnergySpace80(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 114:
                                if (Server.getGameMap().getEnergySpace114() == 1) {
                                    Server.getGameMap().setEnergySpace114(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                        }
                        break;
                    case "Lost Bearings":
                        switch (currentField) {
                            case 52:
                                if (Server.getGameMap().getEnergySpace52() == 1) {
                                    Server.getGameMap().setEnergySpace52(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 57:
                                if (Server.getGameMap().getEnergySpace57() == 1) {
                                    Server.getGameMap().setEnergySpace57(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 74:
                                if (Server.getGameMap().getEnergySpace74() == 1) {
                                    Server.getGameMap().setEnergySpace74(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 85:
                                if (Server.getGameMap().getEnergySpace85() == 1) {
                                    Server.getGameMap().setEnergySpace85(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 102:
                                if (Server.getGameMap().getEnergySpace102() == 1) {
                                    Server.getGameMap().setEnergySpace102(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 107:
                                if (Server.getGameMap().getEnergySpace107() == 1) {
                                    Server.getGameMap().setEnergySpace107(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                        }
                        break;
                    case "Death Trap":
                        switch (currentField) {
                            case 23:
                                if (Server.getGameMap().getEnergySpace23() == 1) {
                                    Server.getGameMap().setEnergySpace23(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 37:
                                if (Server.getGameMap().getEnergySpace37() == 1) {
                                    Server.getGameMap().setEnergySpace37(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 42:
                                if (Server.getGameMap().getEnergySpace42() == 1) {
                                    Server.getGameMap().setEnergySpace42(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 46:
                                if (Server.getGameMap().getEnergySpace46() == 1) {
                                    Server.getGameMap().setEnergySpace46(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 62:
                                if (Server.getGameMap().getEnergySpace62() == 1) {
                                    Server.getGameMap().setEnergySpace62(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                            case 76:
                                if (Server.getGameMap().getEnergySpace76() == 1) {
                                    Server.getGameMap().setEnergySpace76(0);
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                } else if (Server.getRegisterCounter() == 5) {
                                    Server.getGame().getPlayerList().get(i).setEnergyCubes(Server.getGame().getPlayerList().get(i).getEnergyCubes() + 1);
                                }
                                break;
                        }
                        break;
                }
            }
        }
    }

}
