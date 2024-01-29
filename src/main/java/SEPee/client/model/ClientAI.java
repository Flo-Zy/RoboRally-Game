package SEPee.client.model;

import SEPee.client.ClientAILogger;
import SEPee.client.ClientLogger;
import SEPee.client.viewModel.ClientController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.serialisierung.messageType.Error;
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.damageCard.*;
import SEPee.server.model.card.progCard.*;
import SEPee.server.model.gameBoard.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;

/**
 * the version of the client that functions as an AI, chooses options automatically for the dialogs
 * @author Hasan, Franziska, Maximilian
 */
@Getter
public class ClientAI extends Application {

    //private static final String SERVER_IP = "sep21.dbs.ifi.lmu.de";
    //private static final int SERVER_PORT = 52018;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8886;

    @Getter
    @Setter
    private static ArrayList<Player> playerListClientAI = new ArrayList<>();
    @Getter
    @Setter
    private static ArrayList<String> mapList = new ArrayList<>();
    @Getter
    private static String selectedMap1;
    @Getter
    @Setter
    private static ArrayList<Integer> takenFigures = new ArrayList<>();
    private boolean receivedHelloClient = false;
    @Getter
    private static PrintWriter writer;
    private int registerCounter = 1;
    @Getter
    private static ArrayList<CurrentCards.ActiveCard> activeRegister = new ArrayList<>();
    private boolean wait = false;
    private AIBestMove aiBestMove = new AIBestMove();
    private SmartAi smartAi = new SmartAi();
    private GameBoard gameBoard;
    private RobotAI aiRobot = new RobotAI();
    private int numCheckpointToken = 0;

    /**
     * main method
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * establishes a connection with the server
     * @param primaryStage the stage that needs to be displayed
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/Client.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("ClientAI");
            primaryStage.setScene(scene);

            ClientController controller = loader.getController();
            controller.setName("AI");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // receive HelloClient from Server
            String serializedHelloClient = reader.readLine();
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);

            if (deserializedHelloClient.getMessageType().equals("HelloClient") && deserializedHelloClient.getMessageBody().getProtocol().equals("Version 1.0")) {
                // Send HelloServer back to the server
                HelloServer helloServer = new HelloServer("EifrigeEremiten", true, "Version 1.0");
                String serializedHelloServer = Serialisierer.serialize(helloServer);
                writer.println(serializedHelloServer);

                receivedHelloClient = true; // Update flag after receiving HelloClient and Welcome

            } else {
                controller.shutdown();
            }

            startServerMessageProcessing(socket, reader, controller, primaryStage, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * starts processing the messages sent by the server
     * @param socket the socket on which the client communicates with the server
     * @param reader the reader to read from the socket
     * @param controller the AI's client controller
     * @param primaryStage the stage
     * @param writer the writer to print to the socket
     */
    private void startServerMessageProcessing(Socket socket, BufferedReader reader, ClientController controller, Stage primaryStage, PrintWriter writer) {
        new Thread(() -> {
            try {
                while (!receivedHelloClient) {
                    // Wait until HelloClient and Welcome are received
                    Thread.sleep(100); // Add a short delay to avoid busy waiting
                }

                // Start processing subsequent messages after receiving HelloClient and Welcome
                while (true) {
                    String serializedReceivedString = reader.readLine();
                    Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedString, Message.class);
                    String messageType = deserializedReceivedString.getMessageType();

                    switch (messageType) {
                        case "Alive":
                            Alive alive = new Alive();
                            String serializedAlive = Serialisierer.serialize(alive);
                            writer.println(serializedAlive);
                            break;
                        case "Welcome":
                            ClientAILogger.writeToClientLog("Welcome");
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedReceivedString, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            controller.setId(receivedId);

                            Platform.runLater(() -> {
                                primaryStage.setOnCloseRequest(event -> controller.shutdown());
                                controller.initAI(this, primaryStage);
                                PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure()-1);
                                String serializedPlayerValues = Serialisierer.serialize(playerValues);
                                writer.println(serializedPlayerValues);
                            });
                            break;
                        case "PlayerAdded":
                            ClientAILogger.writeToClientLog("PlayerAdded");
                            PlayerAdded playerAdded = Deserialisierer.deserialize(serializedReceivedString, PlayerAdded.class);
                            String name = playerAdded.getMessageBody().getName();
                            int id = playerAdded.getMessageBody().getClientID();
                            int figure = playerAdded.getMessageBody().getFigure();

                            Player newPlayer = new Player(name, id, figure+1);
                            boolean exists = false;
                            synchronized (playerListClientAI) {
                                for (Player player : playerListClientAI) {
                                    if (player.getId() == id) {
                                        player.setName(name);
                                        player.setFigure(figure + 1);
                                        exists = true;
                                    }
                                }
                            }
                            if(!exists){
                                synchronized (playerListClientAI) {
                                    playerListClientAI.add(newPlayer);
                                }
                            }

                            ClientAILogger.writeToClientLog("1. " + takenFigures);
                            synchronized (playerListClientAI) {
                                ClientAILogger.writeToClientLog(playerListClientAI);
                                for (Player player : playerListClientAI) {
                                    if(!takenFigures.contains(player.getFigure())) {
                                        takenFigures.add(player.getFigure());
                                    }
                                }
                            }
                            ClientAILogger.writeToClientLog("2. " + takenFigures);

                            ClientAILogger.writeToClientLog("Player added");
                            synchronized (playerListClientAI) {
                                for (int i = 0; i < playerListClientAI.size(); i++) {
                                    ClientAILogger.writeToClientLog(playerListClientAI.get(i).getName() + "," + playerListClientAI.get(i).getId());
                                }
                            }
                            break;
                        case "PlayerStatus":
                            ClientAILogger.writeToClientLog("PlayerStatus");
                            PlayerStatus playerStatus = Deserialisierer.deserialize(serializedReceivedString, PlayerStatus.class);
                            if(playerStatus.getMessageBody().getClientID() == -9999){
                                wait = playerStatus.getMessageBody().isReady();
                            }
                            synchronized (playerListClientAI) {
                                for (int i = 0; i < playerListClientAI.size(); i++) {
                                    if (playerStatus.getMessageBody().getClientID() == playerListClientAI.get(i).getId()) {
                                        playerListClientAI.get(i).setReady(playerStatus.getMessageBody().isReady());
                                    }
                                }
                            }
                            break;
                        case "SelectMap":
                            ClientAILogger.writeToClientLog("SelectMap von " + controller.getName());
                            SelectMap selectMap = Deserialisierer.deserialize(serializedReceivedString, SelectMap.class);
                            mapList = selectMap.getMessageBody().getAvailableMaps();

                            selectedMap1 = "Dizzy Highway";
                            controller.sendReadyAI();

                            MapSelected mapSelected = new MapSelected(selectedMap1);
                            String serializedMapSelected = Serialisierer.serialize(mapSelected);
                            writer.println(serializedMapSelected);

                            break;
                        case "MapSelected":
                            ClientAILogger.writeToClientLog("Map wurde gewählt");
                            String serializedReceivedMap = serializedReceivedString;
                            MapSelected deserializedReceivedMap = Deserialisierer.deserialize(serializedReceivedMap, MapSelected.class);

                            controller.sendReadyAI();

                            switch (deserializedReceivedMap.getMessageBody().getMap()) {

                                case "Dizzy Highway":
                                    selectedMap1 = "Dizzy Highway";
                                    break;
                                case "Extra Crispy":
                                    selectedMap1 = "Extra Crispy";
                                    break;
                                case "Lost Bearings":
                                    selectedMap1 = "Lost Bearings";
                                    break;
                                case "Death Trap":
                                    selectedMap1 = "Death Trap";
                                    break;

                                default:
                                    ClientAILogger.writeToClientLog("Invalid Map");
                                    break;
                            }
                            ClientAILogger.writeToClientLog(selectedMap1);
                            break;

                        case "GameStarted":
                            ClientAILogger.writeToClientLog("Game Started");
                            GameStarted gameStarted = Deserialisierer.deserialize(serializedReceivedString, GameStarted.class);
                            ClientAILogger.writeToClientLog(selectedMap1);
                            if(selectedMap1.equals("Dizzy Highway")) {
                                controller.loadDizzyHighwayFXMLAI(this, primaryStage);
                                gameBoard = new DizzyHighway();
                            } else if(selectedMap1.equals("Extra Crispy")) {
                                controller.loadExtraCrispyFXMLAI(this, primaryStage);
                                gameBoard = new ExtraCrispy();
                            } else if(selectedMap1.equals("Lost Bearings")) {
                                controller.loadLostBearingsFXMLAI(this, primaryStage);
                                gameBoard = new LostBearings();
                            } else if(selectedMap1.equals("Death Trap")) {
                                controller.loadDeathTrapFXMLAI(this, primaryStage);
                                gameBoard = new DeathTrap();
                            }
                            aiBestMove.setGameBoard(gameBoard);
                            smartAi.setGameBoard(gameBoard);

                            break;
                        case "ReceivedChat":
                            String serializedReceivedChat = serializedReceivedString;
                            ReceivedChat deserializedReceivedChat = Deserialisierer.deserialize(serializedReceivedChat, ReceivedChat.class);

                            String fromName = null;
                            synchronized (playerListClientAI) {
                                for (int i = 0; i < playerListClientAI.size(); i++) {
                                    if (deserializedReceivedChat.getMessageBody().getFrom() == playerListClientAI.get(i).getId()) {
                                        fromName = playerListClientAI.get(i).getName();
                                    }
                                }
                            }
                            if (fromName != null) {
                                String receivedMessage = (fromName + ": " + deserializedReceivedChat.getMessageBody().getMessage());
                                controller.appendToChatArea(receivedMessage);
                            } else {
                                String receivedMessage = (deserializedReceivedChat.getMessageBody().getMessage());
                                controller.appendToChatArea(receivedMessage);
                            }
                            break;
                        case "Error":
                            Error deserializedError = Deserialisierer.deserialize(serializedReceivedString, Error.class);
                            ClientAILogger.writeToClientLog(deserializedError.getMessageBody().getError());
                            break;
                        case "ConnectionUpdate":
                            ClientAILogger.writeToClientLog("Connection Update");
                            ConnectionUpdate connectionUpdate = Deserialisierer.deserialize(serializedReceivedString, ConnectionUpdate.class);
                            //remove Player from playerList if he lost his connection
                            int clientIdToRemove = connectionUpdate.getMessageBody().getClientID();
                            synchronized (playerListClientAI) {
                                Iterator<Player> iterator = playerListClientAI.iterator();
                                while (iterator.hasNext()) {
                                    Player player = iterator.next();
                                    if (clientIdToRemove == player.getId()) {
                                        iterator.remove();
                                    }
                                }
                            }
                            break;
                        case "CardPlayed":
                            ClientAILogger.writeToClientLog("Card Played");
                            CardPlayed cardPlayed = Deserialisierer.deserialize(serializedReceivedString, CardPlayed.class);
                            controller.appendToChatArea("> Player " + cardPlayed.getMessageBody().getClientID() +
                                    " played card " + cardPlayed.getMessageBody().getCard());

                            break;
                        case "ActivePhase":
                            ClientAILogger.writeToClientLog("Active Phase");
                            ActivePhase activePhase = Deserialisierer.deserialize(serializedReceivedString, ActivePhase.class);
                            controller.setCurrentPhase(activePhase.getMessageBody().getPhase());
                            controller.appendToChatArea(">> Active Phase: " + controller.getCurrentPhase());
                            // wenn Phase 2: SelectedCard an Server (ClientHandler) senden
                            if(controller.getCurrentPhase() == 2){
                                controller.setRegisterVisibilityFalse();
                                ClientAILogger.writeToClientLog("ICH SETTE REGISTER");
                                //smartAi.setRegister(aiRobot, controller.getHandAi());
                                ClientAILogger.writeToClientLog(" Programmierungsphase");
                            }
                            if (controller.getCurrentPhase() == 3){
                                ClientAILogger.writeToClientLog(" Aktivierungsphase");
                            }
                            break;
                        case "CurrentPlayer":
                            ClientAILogger.writeToClientLog("Current Player");
                            CurrentPlayer currentPlayer = Deserialisierer.deserialize(serializedReceivedString, CurrentPlayer.class);
                            ClientAILogger.writeToClientLog("Client current Player checker: " + currentPlayer.getMessageBody().getClientID());

                            switch (controller.getCurrentPhase()) {
                                case 0:
                                    if (controller.getId() == currentPlayer.getMessageBody().getClientID()) {
                                        ClientAILogger.writeToClientLog("Aufbauphase");
                                        Platform.runLater(() -> {
                                            controller.setStartingPointAI();
                                            ClientAILogger.writeToClientLog("StartingPoint wurde gewählt");

                                            SetStartingPoint setStartingPoint = new SetStartingPoint(controller.getStartPointX(), controller.getStartPointY());
                                            String serializedSetStartingPoint = Serialisierer.serialize(setStartingPoint);
                                            writer.println(serializedSetStartingPoint);
                                        });
                                    }
                                    break;
                                case 1:
                                    ClientAILogger.writeToClientLog("Upgradephase");
                                    break;
                                case 2:
                                    ClientAILogger.writeToClientLog("Programmierphase");
                                    break;
                                case 3:
                                    ClientAILogger.writeToClientLog("Aktivierungsphase");
                                    if(currentPlayer.getMessageBody().getClientID() == controller.getId()) {
                                        for (CurrentCards.ActiveCard activeCard : activeRegister) {
                                            if (activeCard.getClientID() == controller.getId()) {
                                                if(wait) {
                                                    Timer timer = new Timer();
                                                    TimerTask task = new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            ClientAILogger.writeToClientLog("run");
                                                            if (!wait) {
                                                                PlayCard playCard = new PlayCard(activeCard.getCard());
                                                                String serializedPlayCard = Serialisierer.serialize(playCard);
                                                                writer.println(serializedPlayCard);
                                                                ClientAILogger.writeToClientLog("playCard gesendet");
                                                                timer.cancel();
                                                            }
                                                        }
                                                    };
                                                    timer.scheduleAtFixedRate(task, 0, 2000);
                                                }else{
                                                    PlayCard playCard = new PlayCard(activeCard.getCard());
                                                    String serializedPlayCard = Serialisierer.serialize(playCard);
                                                    writer.println(serializedPlayCard);
                                                }

                                            }
                                        }
                                    }
                                    break;
                            }
                            break;
                        case "StartingPointTaken":
                            ClientAILogger.writeToClientLog("Starting Point Taken");
                            StartingPointTaken startingPointTaken = Deserialisierer.deserialize(serializedReceivedString, StartingPointTaken.class);

                            if(selectedMap1.equals("Death Trap")) {
                                aiRobot.setOrientation("left");
                                controller.addTakenStartingPointsDeathTrap(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            } else {
                                aiRobot.setOrientation("right");
                                controller.addTakenStartingPoints(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            }

                            int takenClientID = startingPointTaken.getMessageBody().getClientID();
                            if(takenClientID == controller.getId()){
                                aiRobot.setX(startingPointTaken.getMessageBody().getX());
                                aiRobot.setY(startingPointTaken.getMessageBody().getY());
                            }
                            //set avatarPlayer to the player that chose the starting point
                            Player avatarPlayer = new Player("", -999,-999);
                            synchronized (playerListClientAI) {
                                for (Player player : playerListClientAI) {
                                    if (player.getId() == startingPointTaken.getMessageBody().getClientID()) {
                                        avatarPlayer = player;
                                    }
                                }
                            }
                            controller.putAvatarDown(avatarPlayer, startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            ClientAILogger.writeToClientLog("Starting Point taken for ID: " + avatarPlayer.getId() + ", figure: " + avatarPlayer.getFigure());
                            break;

                        case "YourCards":
                            ClientAILogger.writeToClientLog("Your Cards");
                            YourCards yourCards = Deserialisierer.deserialize(serializedReceivedString, YourCards.class);
                            ClientAILogger.writeToClientLog(yourCards.getMessageBody().getCardsInHand());

                            ArrayList<Card> drawPile = new ArrayList<>();
                            for (String cardName : yourCards.getMessageBody().getCardsInHand()) {
                                switch (cardName) {
                                    case "Again":
                                        drawPile.add(new Again());
                                        break;
                                    case "BackUp":
                                        drawPile.add(new BackUp());
                                        break;
                                    case "TurnLeft":
                                        drawPile.add(new LeftTurn());
                                        break;
                                    case "MoveI":
                                        drawPile.add(new MoveI());
                                        break;
                                    case "MoveII":
                                        drawPile.add(new MoveII());
                                        break;
                                    case "MoveIII":
                                        drawPile.add(new MoveIII());
                                        break;
                                    case "PowerUp":
                                        drawPile.add(new PowerUp());
                                        break;
                                    case "TurnRight":
                                        drawPile.add(new RightTurn());
                                        break;
                                    case "UTurn":
                                        drawPile.add(new UTurn());
                                        break;
                                    case "Spam":
                                        drawPile.add(new Spam());
                                        break;
                                    case "Virus":
                                        drawPile.add(new Virus());
                                        break;
                                    case "Wurm":
                                        drawPile.add(new Wurm());
                                        break;
                                    case "TrojanHorse":
                                        drawPile.add(new TrojanHorse());
                                        break;
                                }
                            }
                            controller.setClientHand(drawPile);
                            controller.setHandAi(yourCards.getMessageBody().getCardsInHand());
                            smartAi.setRegister(aiRobot, controller.getHandAi());

                            break;

                        case "NotYourCards":
                            ClientAILogger.writeToClientLog("Not Your Cards");
                            NotYourCards notYourCards = Deserialisierer.deserialize(serializedReceivedString, NotYourCards.class);
                            ClientAILogger.writeToClientLog("(INFO) Player " + notYourCards.getMessageBody().getClientID() + " got " + notYourCards.getMessageBody().getCardsInHand() + " Cards");
                            break;
                        case "SelectionFinished":
                            SelectionFinished selectionFinished = Deserialisierer.deserialize(serializedReceivedString, SelectionFinished.class);
                            ClientAILogger.writeToClientLog(selectionFinished.getMessageBody().getClientID() + ": Selection Finished");
                            break;
                        case "ShuffleCoding":
                            ClientAILogger.writeToClientLog("Shuffle Coding");
                            ShuffleCoding shuffleCoding = Deserialisierer.deserialize(serializedReceivedString, ShuffleCoding.class);
                            break;
                        case "CardSelected":
                            ClientAILogger.writeToClientLog("Card Selected");
                            CardSelected cardSelected = Deserialisierer.deserialize(serializedReceivedString, CardSelected.class);
                            ClientAILogger.writeToClientLog("Player " + cardSelected.getMessageBody().getClientID() + " has set his register " + cardSelected.getMessageBody().getRegister());
                            break;
                        case "TimerStarted":
                            ClientAILogger.writeToClientLog("Timer Started");
                            TimerStarted timerStarted = Deserialisierer.deserialize(serializedReceivedString, TimerStarted.class);
                            controller.appendToChatArea(">> Timer Started \n>> (30 sec. left to fill your register)");
                            break;
                        case "TimerEnded":
                            ClientAILogger.writeToClientLog("Timer Ended");
                            TimerEnded timerEnded = Deserialisierer.deserialize(serializedReceivedString, TimerEnded.class);
                            controller.setCounter1(5);
                            break;
                        case "CardsYouGotNow":
                            ClientAILogger.writeToClientLog("Cards You Got Now");
                            CardsYouGotNow cardsYouGotNow = Deserialisierer.deserialize(serializedReceivedString, CardsYouGotNow.class);

                            ArrayList<Card> nextCards = new ArrayList<>();

                            for (String cardName : cardsYouGotNow.getMessageBody().getCards()) {
                                switch (cardName) {
                                    case "Again":
                                        nextCards.add(new Again());
                                        break;
                                    case "BackUp":
                                        nextCards.add(new BackUp());
                                        break;
                                    case "TurnLeft":
                                        nextCards.add(new LeftTurn());
                                        break;
                                    case "MoveI":
                                        nextCards.add(new MoveI());
                                        break;
                                    case "MoveII":
                                        nextCards.add(new MoveII());
                                        break;
                                    case "MoveIII":
                                        nextCards.add(new MoveIII());
                                        break;
                                    case "PowerUp":
                                        nextCards.add(new PowerUp());
                                        break;
                                    case "TurnRight":
                                        nextCards.add(new RightTurn());
                                        break;
                                    case "UTurn":
                                        nextCards.add(new UTurn());
                                        break;
                                    case "Spam":
                                        nextCards.add(new Spam());
                                        break;
                                    case "Virus":
                                        nextCards.add(new Virus());
                                        break;
                                    case "Wurm":
                                        nextCards.add(new Wurm());
                                        break;
                                    case "TrojanHorse":
                                        nextCards.add(new TrojanHorse());
                                        break;
                                }
                            }
                            controller.fillEmptyRegister(nextCards);
                            break;
                        case "CurrentCards":
                            ClientAILogger.writeToClientLog("Current Cards");
                            CurrentCards currentCards = Deserialisierer.deserialize(serializedReceivedString, CurrentCards.class);

                            activeRegister = currentCards.getMessageBody().getActiveCards();

                            controller.appendToChatArea(">> Played Register: " + registerCounter);

                            if (registerCounter == 5){
                                registerCounter = 1;
                            }else {
                                registerCounter++;
                            }

                            break;
                        case "ReplaceCard":
                            ClientAILogger.writeToClientLog("Replace Card");
                            ReplaceCard replaceCard = Deserialisierer.deserialize(serializedReceivedString, ReplaceCard.class);
                            break;
                        case "Movement":
                            ClientAILogger.writeToClientLog("Movement");
                            controller.setPlayerListClient(playerListClientAI);
                            Movement movement = Deserialisierer.deserialize(serializedReceivedString, Movement.class);

                            int clientIdToMove = movement.getMessageBody().getClientID();
                            int newX = movement.getMessageBody().getX();
                            int newY = movement.getMessageBody().getY();
                            if(clientIdToMove == controller.getId()) {
                                aiRobot.setX(newX);
                                aiRobot.setY(newY);
                            }
                            ClientAILogger.writeToClientLog(clientIdToMove + ", " + newX + ", " + newY + ", " + controller.getId());
                            controller.movementPlayed(clientIdToMove,newX, newY);

                            break;
                        case "PlayerTurning":
                            ClientAILogger.writeToClientLog("Player Turning");
                            controller.setPlayerListClient(playerListClientAI);
                            PlayerTurning playerTurning = Deserialisierer.deserialize(serializedReceivedString, PlayerTurning.class);
                            int clientIdToTurn = playerTurning.getMessageBody().getClientID();
                            String rotation = playerTurning.getMessageBody().getRotation();
                            if(clientIdToTurn == controller.getId()){
                                aiRobot.setOrientation(getResultingOrientation(rotation, aiRobot));
                            }
                            controller.playerTurn(clientIdToTurn, rotation);

                            break;
                        case "DrawDamage":
                            ClientAILogger.writeToClientLog("Draw Damage");
                            DrawDamage drawDamage = Deserialisierer.deserialize(serializedReceivedString, DrawDamage.class);

                            int damagedID = drawDamage.getMessageBody().getClientID(); // die ID die karten ziehen soll!

                            ArrayList<String> damageCardsDrawn = drawDamage.getMessageBody().getCards();

                            synchronized (playerListClientAI) {
                                for (Player player : playerListClientAI) {
                                    if (player.getId() == damagedID) {
                                        controller.appendToChatArea(player.getName() + " hat diese Karten kassiert: " + damageCardsDrawn + "!");
                                    }
                                }
                            }
                            break;
                        case "PickDamage":
                            ClientAILogger.writeToClientLog("Pick Damage");
                            PickDamage pickDamage = Deserialisierer.deserialize(serializedReceivedString, PickDamage.class);

                            ArrayList<String> availableList = pickDamage.getMessageBody().getAvailablePiles();
                            AtomicInteger numDamageCards = new AtomicInteger();
                            numDamageCards.set(pickDamage.getMessageBody().getCount());

                            ArrayList<String> selectedDamageList = new ArrayList<>();
                            int i = 0;
                            while(i < numDamageCards.get()) {
                                String damageCard;
                                damageCard = availableList.get(0);
                                selectedDamageList.add(damageCard);
                                i++;
                            }
                            SelectedDamage selectedDamage = new SelectedDamage(selectedDamageList);
                            String serializedSelectedDamage = Serialisierer.serialize(selectedDamage);
                            writer.println(serializedSelectedDamage);
                            break;
                        case "Animation":
                            ClientAILogger.writeToClientLog("Animation");
                            Animation animation = Deserialisierer.deserialize(serializedReceivedString, Animation.class);
                            break;
                        case "Reboot":
                            ClientAILogger.writeToClientLog("Reboot");
                            Reboot reboot = Deserialisierer.deserialize(serializedReceivedString, Reboot.class);
                            int rebootingClientId = reboot.getMessageBody().getClientID();

                            // for AI default direction top
                            if (controller.getId() == rebootingClientId) {
                                RebootDirection rebootDirection2 = new RebootDirection("top");
                                String serializedRebootDirection2 = Serialisierer.serialize(rebootDirection2);
                                writer.println(serializedRebootDirection2);
                            }
                            break;
                        case "Energy":
                            ClientAILogger.writeToClientLog("Energy");
                            Energy energy = Deserialisierer.deserialize(serializedReceivedString, Energy.class);
                            break;
                        case "CheckPointReached":
                            ClientAILogger.writeToClientLog("Check Point Reached");
                            CheckPointReached checkPointReached = Deserialisierer.deserialize(serializedReceivedString, CheckPointReached.class);
                            int number = checkPointReached.getMessageBody().getNumber();
                            int clientID = checkPointReached.getMessageBody().getClientID();

                            synchronized (playerListClientAI) {
                                for (Player player : playerListClientAI) {
                                    if (player.getId() == clientID && clientID == controller.getId()) {
                                        numCheckpointToken++;
                                        aiBestMove.setNumCheckpointToken(numCheckpointToken);
                                        smartAi.setNumCheckpointToken(numCheckpointToken);
                                        controller.setCheckPointImage("/boardElementsPNGs/CheckpointCounter" + number + ".png");
                                        controller.appendToChatArea(player.getName() + " has reached checkpoint " + number);

                                    }
                                }
                            }

                            break;
                        case "GameFinished":
                            ClientAILogger.writeToClientLog("GameFinished");
                            GameFinished gameFinished = Deserialisierer.deserialize(serializedReceivedString, GameFinished.class);
                            int winnerId = gameFinished.getMessageBody().getClientID();

                            synchronized (playerListClientAI) {
                                for (Player player : playerListClientAI) {
                                    if (player.getId() == winnerId) {
                                        ClientAILogger.writeToClientLog("winner id ist " + winnerId);
                                        controller.appendToChatArea(player.getName() + " has won this game!!");
                                        ClientAILogger.writeToClientLog("ausgabe hier");
                                    }
                                }
                            }
                            Thread.sleep(10000);
                            controller.shutdown();
                            break;
                        default:
                            ClientAILogger.writeToClientLog("Unhandled message received: " + messageType);
                            break;

                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * get the resulting orientation when turning the player
     * @param turningDirection clockwise or counterclockwise
     * @param robot the robot being turned
     * @return the new direction the robot is facing in now
     */
    private static String getResultingOrientation(String turningDirection, RobotAI robot) {
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
        return "-";
    }
}