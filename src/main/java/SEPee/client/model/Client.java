package SEPee.client.model;

import SEPee.client.ClientLogger;
import SEPee.client.viewModel.ClientController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.Error;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.damageCard.Spam;
import SEPee.server.model.card.damageCard.TrojanHorse;
import SEPee.server.model.card.damageCard.Virus;
import SEPee.server.model.card.damageCard.Wurm;
import SEPee.server.model.card.progCard.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * reads the messages sent by the server, first establishes an connection to the server
 * and then deserializes the received messages and applies them to the GUI
 * @author Maximilian, Florian, Hasan, Felix, Franziska
 */
@Getter
public class Client extends Application {

    // private static final String SERVER_IP = "sep21.dbs.ifi.lmu.de";
    // private static final int SERVER_PORT = 52020;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8886;

    @Getter
    @Setter
    private static ArrayList<Player> playerListClient = new ArrayList<>();
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
    AtomicInteger seconds = new AtomicInteger(0);
    private final List<TakenFiguresChangeListener> takenFiguresChangeListeners = new ArrayList<>();

    public interface TakenFiguresChangeListener {
        void onTakenFiguresChanged(ArrayList<Integer> newTakenFigures);
    }

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
     * @author Maximilian, Florian, Hasan, Felix, Franziska
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/Client.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Client");
            primaryStage.setScene(scene);

            ClientController controller = loader.getController();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // receive HelloClient from server
            String serializedHelloClient = reader.readLine();
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);

            if (deserializedHelloClient.getMessageType().equals("HelloClient") && deserializedHelloClient.getMessageBody().getProtocol().equals("Version 1.0")) {
                // Send HelloServer back to the server
                HelloServer helloServer = new HelloServer("EifrigeEremiten", false, "Version 1.0");
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
     * @param controller the client's client controller
     * @param primaryStage the stage
     * @param writer the writer to print to the socket
     * @author Maximilian, Florian, Hasan, Felix, Franziska
     */
    private void startServerMessageProcessing(Socket socket, BufferedReader reader, ClientController controller, Stage primaryStage, PrintWriter writer) {
        new Thread(() -> {
            try {
                while (!receivedHelloClient) {
                    // Wait until HelloClient and Welcome are received
                    Thread.sleep(100);
                }
                // Start processing messages after receiving HelloClient and Welcome
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
                            ClientLogger.writeToClientLog("Welcome");
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedReceivedString, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            controller.setId(receivedId);

                            Platform.runLater(() -> {
                                primaryStage.setOnCloseRequest(event -> controller.shutdown());
                                controller.init(this, primaryStage);
                                if ( controller.getName() == null || controller.getFigure() == 0) {
                                    controller.shutdown();
                                } else {
                                    PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure()-1);
                                    String serializedPlayerValues = Serialisierer.serialize(playerValues);
                                    writer.println(serializedPlayerValues);
                                    controller.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");
                                    controller.updateCountdownImage(30);
                                    primaryStage.show();
                                }
                            });
                            break;
                        case "PlayerAdded":
                            ClientLogger.writeToClientLog("PlayerAdded");
                            PlayerAdded playerAdded = Deserialisierer.deserialize(serializedReceivedString, PlayerAdded.class);
                            String name = playerAdded.getMessageBody().getName();
                            int id = playerAdded.getMessageBody().getClientID();
                            int figure = playerAdded.getMessageBody().getFigure();

                            Player newPlayer = new Player(name, id, figure+1);
                            boolean exists = false;
                            synchronized (playerListClient) {
                                for (Player player : playerListClient) {
                                    if (player.getId() == id) {
                                        player.setName(name);
                                        player.setFigure(figure + 1);
                                        exists = true;
                                    }
                                }
                            }
                            if(!exists){
                                ClientLogger.writeToClientLog("Before: " + playerListClient);
                                synchronized (playerListClient) {
                                    playerListClient.add(newPlayer);
                                }
                                ClientLogger.writeToClientLog("After: " + playerListClient);
                            }

                            ClientLogger.writeToClientLog("1. " + takenFigures);
                            updateTakenFigures();
                            ClientLogger.writeToClientLog("2. " + takenFigures);
                            notifyTakenFiguresChangeListeners();

                            synchronized (playerListClient) {
                                for (int i = 0; i < playerListClient.size(); i++) {
                                    ClientLogger.writeToClientLog(playerListClient.get(i).getName() + "," + playerListClient.get(i).getId() + "," + playerListClient.get(i).getFigure());
                                }
                            }
                            break;
                        case "PlayerStatus":
                            ClientLogger.writeToClientLog("PlayerStatus");
                            PlayerStatus playerStatus = Deserialisierer.deserialize(serializedReceivedString, PlayerStatus.class);
                            if(playerStatus.getMessageBody().getClientID() == -9999){
                                wait = playerStatus.getMessageBody().isReady();
                            }
                            synchronized (playerListClient) {
                                for (int i = 0; i < playerListClient.size(); i++) {
                                    if (playerStatus.getMessageBody().getClientID() == playerListClient.get(i).getId()) {
                                        playerListClient.get(i).setReady(playerStatus.getMessageBody().isReady());
                                    }
                                }
                            }
                            controller.playEventSound("Ready");
                            break;
                        case "SelectMap":
                            ClientLogger.writeToClientLog("SelectMap of " + controller.getName());
                            SelectMap selectMap = Deserialisierer.deserialize(serializedReceivedString, SelectMap.class);
                            mapList = selectMap.getMessageBody().getAvailableMaps();
                            Platform.runLater(() -> {
                                selectedMap1 = controller.showSelectMapDialog();
                                ClientLogger.writeToClientLog(selectedMap1);
                                MapSelected mapSelected = new MapSelected(selectedMap1);
                                String serializedMapSelected = Serialisierer.serialize(mapSelected);
                                writer.println(serializedMapSelected);
                            });
                            break;
                        case "MapSelected":
                            ClientLogger.writeToClientLog("MapSelected");
                            String serializedReceivedMap = serializedReceivedString;
                            MapSelected deserializedReceivedMap = Deserialisierer.deserialize(serializedReceivedMap, MapSelected.class);

                            ClientLogger.writeToClientLog(deserializedReceivedMap.getMessageBody().getMap());
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
                                    ClientLogger.writeToClientLog("Invalid Map");
                                    break;
                            }
                            ClientLogger.writeToClientLog(selectedMap1);
                            break;

                        case "GameStarted":
                            ClientLogger.writeToClientLog("GameStarted");
                            GameStarted gameStarted = Deserialisierer.deserialize(serializedReceivedString, GameStarted.class);
                            ClientLogger.writeToClientLog(selectedMap1);
                            if(selectedMap1.equals("Dizzy Highway")) {
                                controller.loadDizzyHighwayFXML(this, primaryStage);
                            } else if(selectedMap1.equals("Extra Crispy")) {
                                controller.loadExtraCrispyFXML(this, primaryStage);
                            } else if(selectedMap1.equals("Lost Bearings")) {
                                controller.loadLostBearingsFXML(this, primaryStage);
                            } else if(selectedMap1.equals("Death Trap")) {
                                controller.loadDeathTrapFXML(this, primaryStage);
                            }
                            controller.playEventSound("GameStartAnnouncement");
                            controller.playSound("sountrack");
                            break;
                        case "ReceivedChat":
                            String serializedReceivedChat = serializedReceivedString;
                            ReceivedChat deserializedReceivedChat = Deserialisierer.deserialize(serializedReceivedChat, ReceivedChat.class);
                            if (deserializedReceivedChat.getMessageBody().isPrivate()){
                                controller.playUISound("privMessage");
                            } else {
                                controller.playUISound("messageRecieved");
                            }
                            String fromName = null;
                            synchronized (playerListClient) {
                                for (int i = 0; i < playerListClient.size(); i++) {
                                    if (deserializedReceivedChat.getMessageBody().getFrom() == playerListClient.get(i).getId()) {
                                        fromName = playerListClient.get(i).getName();
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
                            ClientLogger.writeToClientLog(deserializedError.getMessageBody().getError());
                            break;
                        case "ConnectionUpdate":
                            ClientLogger.writeToClientLog("ConnectionUpdate");
                            ConnectionUpdate connectionUpdate = Deserialisierer.deserialize(serializedReceivedString, ConnectionUpdate.class);
                            //remove player from playerList if he lost his connection
                            int clientIdToRemove = connectionUpdate.getMessageBody().getClientID();
                            synchronized (playerListClient) {
                                Iterator<Player> iterator = playerListClient.iterator();
                                while (iterator.hasNext()) {
                                    Player player = iterator.next();
                                    if (clientIdToRemove == player.getId()) {
                                        iterator.remove();
                                    }
                                }
                            }
                            break;
                        case "CardPlayed":
                            ClientLogger.writeToClientLog("CardPlayed");
                            CardPlayed cardPlayed = Deserialisierer.deserialize(serializedReceivedString, CardPlayed.class);
                            ClientLogger.writeToClientLog("Player " + cardPlayed.getMessageBody().getClientID() +
                                    " played card: " + cardPlayed.getMessageBody().getCard());
                            controller.appendToChatArea("> Player " + playerListClient.get(cardPlayed.getMessageBody().getClientID() - 1).getName() +
                                    " played card " + cardPlayed.getMessageBody().getCard());

                            break;
                        case "ActivePhase":
                            ClientLogger.writeToClientLog("ActivePhase");
                            ActivePhase activePhase = Deserialisierer.deserialize(serializedReceivedString, ActivePhase.class);
                            controller.setCurrentPhase(activePhase.getMessageBody().getPhase());
                            controller.appendToChatArea(">> Active Phase: " + controller.getCurrentPhase());
                            // wenn Phase 2: SelectedCard an Server (ClientHandler) senden
                            if(controller.getCurrentPhase() == 2){
                                controller.updateCountdownImage(30);
                                controller.setRegisterVisibilityFalse();
                                controller.initializeRegister();
                                ClientLogger.writeToClientLog(" Programming Phase");
                                controller.playEventSound("ProgrammingPhase");
                            }
                            if (controller.getCurrentPhase() == 3){
                                ClientLogger.writeToClientLog(" Activation Phase");
                                controller.playEventSound("ActivationPhase");
                            }
                            break;
                        case "CurrentPlayer":
                            CurrentPlayer currentPlayer = Deserialisierer.deserialize(serializedReceivedString, CurrentPlayer.class);
                            ClientLogger.writeToClientLog("Current Player is " + currentPlayer.getMessageBody().getClientID());
                            switch (controller.getCurrentPhase()) {
                                case 0:
                                    if (controller.getId() == currentPlayer.getMessageBody().getClientID()) {
                                        ClientLogger.writeToClientLog("Starting Phase");
                                        Platform.runLater(() -> {
                                            controller.setStartingPoint();
                                            ClientLogger.writeToClientLog("StartingPoint selected");

                                            SetStartingPoint setStartingPoint = new SetStartingPoint(controller.getStartPointX(), controller.getStartPointY());
                                            String serializedSetStartingPoint = Serialisierer.serialize(setStartingPoint);
                                            writer.println(serializedSetStartingPoint);
                                        });
                                    }
                                    break;
                                case 1:
                                    ClientLogger.writeToClientLog("Upgrade Phase");
                                    break;
                                case 2:
                                    ClientLogger.writeToClientLog("Programming Phase");
                                    break;
                                case 3:
                                    ClientLogger.writeToClientLog("Activation Phase");
                                    if(currentPlayer.getMessageBody().getClientID() == controller.getId()) {
                                        for (CurrentCards.ActiveCard activeCard : activeRegister) {
                                            if (activeCard.getClientID() == controller.getId()) {
                                                if(wait) {
                                                    Timer timer = new Timer();
                                                    TimerTask task = new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            if (!wait) {
                                                                PlayCard playCard = new PlayCard(activeCard.getCard());
                                                                String serializedPlayCard = Serialisierer.serialize(playCard);
                                                                writer.println(serializedPlayCard);
                                                                ClientLogger.writeToClientLog("PlayCard gesendet (in TimerTask)");
                                                                timer.cancel();
                                                            }
                                                        }
                                                    };
                                                    timer.scheduleAtFixedRate(task, 0, 2000);
                                                }else{
                                                    PlayCard playCard = new PlayCard(activeCard.getCard());
                                                    String serializedPlayCard = Serialisierer.serialize(playCard);
                                                    writer.println(serializedPlayCard);
                                                    ClientLogger.writeToClientLog("PlayCard gesendet");
                                                }

                                            }
                                        }
                                    }
                                    break;
                            }
                            break;
                        case "StartingPointTaken":
                            StartingPointTaken startingPointTaken = Deserialisierer.deserialize(serializedReceivedString, StartingPointTaken.class);
                            ClientLogger.writeToClientLog("StartingPointTaken of " + startingPointTaken.getMessageBody().getClientID());

                            if(selectedMap1.equals("Death Trap")) {
                                controller.addTakenStartingPointsDeathTrap(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            } else {
                                controller.addTakenStartingPoints(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            }

                            //set avatarPlayer to the player that chose the starting point
                            Player avatarPlayer = new Player("", -999,-999);
                            synchronized (playerListClient) {
                                for (Player player : playerListClient) {
                                    if (player.getId() == startingPointTaken.getMessageBody().getClientID()) {
                                        avatarPlayer = player;
                                    }
                                }
                            }
                            controller.putAvatarDown(avatarPlayer, startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            ClientLogger.writeToClientLog("StartingPoint - id: " + avatarPlayer.getId() + ", figure: " + avatarPlayer.getFigure());
                            break;
                        case "YourCards":
                            YourCards yourCards = Deserialisierer.deserialize(serializedReceivedString, YourCards.class);
                            ClientLogger.writeToClientLog("YourCards: " + yourCards.getMessageBody().getCardsInHand());

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
                                    case "Worm":
                                        drawPile.add(new Wurm());
                                        break;
                                    case "TrojanHorse":
                                        drawPile.add(new TrojanHorse());
                                        break;
                                }
                            }
                            controller.setClientHand(drawPile);
                            //initialise the 9 cards from YourCards in the player's hand
                            controller.initializeDrawPile();
                            controller.initializeRegister();
                            break;
                        case "NotYourCards":
                            NotYourCards notYourCards = Deserialisierer.deserialize(serializedReceivedString, NotYourCards.class);
                            ClientLogger.writeToClientLog(notYourCards.getMessageBody().getClientID() + " got " + notYourCards.getMessageBody().getCardsInHand() + " cards");
                            break;
                        case "SelectionFinished":
                            SelectionFinished selectionFinished = Deserialisierer.deserialize(serializedReceivedString, SelectionFinished.class);
                            ClientLogger.writeToClientLog(selectionFinished.getMessageBody().getClientID() + " finished card selection");
                            break;
                        case "ShuffleCoding":
                            ShuffleCoding shuffleCoding = Deserialisierer.deserialize(serializedReceivedString, ShuffleCoding.class);
                            ClientLogger.writeToClientLog("ShuffleCoding of " + shuffleCoding.getMessageBody().getClientID());
                            break;
                        case "CardSelected":
                            ClientLogger.writeToClientLog("CardSelected");
                            CardSelected cardSelected = Deserialisierer.deserialize(serializedReceivedString, CardSelected.class);
                            ClientLogger.writeToClientLog(cardSelected.getMessageBody().getClientID() + " has set his register " + cardSelected.getMessageBody().getRegister());
                            break;
                        case "TimerStarted":
                            ClientLogger.writeToClientLog("TimerStarted");
                            int countdownDurationSeconds = 29;
                            startCountdown(controller, countdownDurationSeconds);
                            break;
                        case "TimerEnded":
                            ClientLogger.writeToClientLog("TimerEnded");
                            controller.updateCountdownImage(0);
                            seconds.set(0);
                            controller.setCounter1(5);
                            break;
                        case "CardsYouGotNow":
                            CardsYouGotNow cardsYouGotNow = Deserialisierer.deserialize(serializedReceivedString, CardsYouGotNow.class);
                            ClientLogger.writeToClientLog("CardsYouGotNow: " + cardsYouGotNow.getMessageBody().getCards());

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
                                    case "Worm":
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
                            ClientLogger.writeToClientLog("CurrentCards");
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
                            ClientLogger.writeToClientLog("ReplaceCard");
                            ReplaceCard replaceCard = Deserialisierer.deserialize(serializedReceivedString, ReplaceCard.class);
                            break;
                        case "Movement":
                            Movement movement = Deserialisierer.deserialize(serializedReceivedString, Movement.class);
                            int clientIdToMove = movement.getMessageBody().getClientID();
                            int newX = movement.getMessageBody().getX();
                            int newY = movement.getMessageBody().getY();
                            ClientLogger.writeToClientLog(clientIdToMove + " Movement");

                            controller.setPlayerListClient(playerListClient);
                            controller.movementPlayed(clientIdToMove,newX, newY);
                            break;
                        case "PlayerTurning":
                            PlayerTurning playerTurning = Deserialisierer.deserialize(serializedReceivedString, PlayerTurning.class);
                            int clientIdToTurn = playerTurning.getMessageBody().getClientID();
                            String rotation = playerTurning.getMessageBody().getRotation();
                            ClientLogger.writeToClientLog(clientIdToTurn + " PlayerTurning");

                            controller.setPlayerListClient(playerListClient);
                            controller.playerTurn(clientIdToTurn, rotation);
                            break;
                        case "DrawDamage":
                            ClientLogger.writeToClientLog("DrawDamage");
                            DrawDamage drawDamage = Deserialisierer.deserialize(serializedReceivedString, DrawDamage.class);
                            int damagedID = drawDamage.getMessageBody().getClientID();

                            ArrayList<String> damageCardsDrawn = drawDamage.getMessageBody().getCards();
                            synchronized (playerListClient) {
                                for (Player player : playerListClient) {
                                    if (player.getId() == damagedID) {
                                        controller.appendToChatArea(player.getName() + " hat " + damageCardsDrawn + " kassiert!");
                                    }
                                }
                            }
                            break;
                        case "PickDamage":
                            ClientLogger.writeToClientLog("PickDamage");
                            PickDamage pickDamage = Deserialisierer.deserialize(serializedReceivedString, PickDamage.class);

                            ArrayList<String> avaiableList = pickDamage.getMessageBody().getAvailablePiles();
                            AtomicInteger numDamageCards = new AtomicInteger();
                            numDamageCards.set(pickDamage.getMessageBody().getCount());

                            ArrayList<String> selectedDamageList = new ArrayList<>();
                            Platform.runLater(() -> {
                                int i = 0;
                                while(i < numDamageCards.get()) {
                                    String damageCard;
                                    damageCard = controller.showSelectDamageDialog(avaiableList);
                                    selectedDamageList.add(damageCard);
                                    i++;
                                }

                                SelectedDamage selectedDamage = new SelectedDamage(selectedDamageList);
                                String serializedSelectedDamage = Serialisierer.serialize(selectedDamage);
                                writer.println(serializedSelectedDamage);
                            });
                            break;
                        case "Animation":
                            Animation animation = Deserialisierer.deserialize(serializedReceivedString, Animation.class);
                            ClientLogger.writeToClientLog("Animation type: " + animation.getMessageBody().getType());
                            String animationType = animation.getMessageBody().getType();

                            if (animationType.equals("BlueConveyorBelt") || animationType.equals("GreenConveyorBelt")){
                                controller.playUISound("Map/conveyor");

                            } else if (animationType.equals("PushPanel")) {
                                controller.playUISound("Map/pushPanel");

                            } else if (animationType.equals("Gear")){
                                // missing gear sound

                            } else if (animationType.equals("CheckPoint")){
                                //handle in checkpointreached msgt

                            } else if (animationType.equals("PlayerShooting")){
                                controller.playUISound("Map/RoboLaser");

                            } else if (animationType.equals("WallShooting")){
                                controller.playUISound("Map/BoardLaser");
                                controller.playEventSound("hitByLaser");

                            } else if (animationType.equals("EnergySpace")){
                                //handle in MsgType
                            }
                            break;
                        case "Reboot":
                            ClientLogger.writeToClientLog("Reboot");
                            Reboot reboot = Deserialisierer.deserialize(serializedReceivedString, Reboot.class);
                            int rebootingClientId = reboot.getMessageBody().getClientID();

                            controller.playEventSound("Reboot");
                            if (rebootingClientId == controller.getId()){
                                controller.playUISound("Map/reBoot");
                                controller.playEventSound("YouRebooted");
                            }

                            if (controller.getId() == rebootingClientId) {
                                Platform.runLater(() -> {
                                    String selectedRebootDirection;
                                    Stage stage = new Stage();

                                    selectedRebootDirection = controller.showSelectRebootDirectionDialog(stage);
                                    ClientLogger.writeToClientLog(selectedRebootDirection);
                                    RebootDirection rebootDirection2 = new RebootDirection(selectedRebootDirection);
                                    String serializedRebootDirection2 = Serialisierer.serialize(rebootDirection2);
                                    writer.println(serializedRebootDirection2);
                                });
                            }
                            break;
                        case "Energy":
                            ClientLogger.writeToClientLog("Energy");
                            Energy energy = Deserialisierer.deserialize(serializedReceivedString, Energy.class);
                            
                            if (controller.getId() == energy.getMessageBody().getClientID()) {
                                controller.playUISound("Map/powerUp");
                            }
                            
                            break;
                        case "CheckPointReached":
                            ClientLogger.writeToClientLog("Check Point Reached");
                            CheckPointReached checkPointReached = Deserialisierer.deserialize(serializedReceivedString, CheckPointReached.class);
                            int number = checkPointReached.getMessageBody().getNumber();
                            int clientID = checkPointReached.getMessageBody().getClientID();

                            synchronized (playerListClient) {
                                for (Player player : playerListClient) {
                                    if (player.getId() == clientID) {
                                        controller.appendToChatArea(player.getName() + " has reached checkpoint " + number);
                                        controller.playEventSound("YouGotCheckpoint");
                                        controller.playUISound("Map/checkpoint");
                                    } else if (player.getId() != clientID) {
                                        controller.playEventSound("CheckpointReached");
                                    }
                                }
                            }

                            if(clientID == controller.getId()){
                                controller.setCheckPointImage("/boardElementsPNGs/CheckpointCounter" + number + ".png");
                                controller.playUISound("Map/checkpoint");
                            }

                            break;
                        case "GameFinished":
                            ClientLogger.writeToClientLog("GameFinished");
                            GameFinished gameFinished = Deserialisierer.deserialize(serializedReceivedString, GameFinished.class);
                            int winnerId = gameFinished.getMessageBody().getClientID();

                            synchronized (playerListClient) {
                                for (Player player : playerListClient) {
                                    if (player.getId() == winnerId) {
                                        ClientLogger.writeToClientLog("WinnerId " + winnerId);
                                        controller.appendToChatArea(player.getName() + " has won this game!");
                                    }
                                }
                            }
                            if(winnerId == controller.getId()) {
                                controller.playEventSound("YouWon");
                            } else {
                                controller.playEventSound("YouLost");
                                Image looserImage = new Image(getClass().getResourceAsStream("/boardElementsPNGs/action/looser.gif"));
                                controller.getEndGIF().setImage(looserImage);
                            }
                            controller.getEndGIF().setVisible(true);
                            Thread.sleep(15000);
                            controller.shutdown();
                            break;
                        default:
                            ClientLogger.writeToClientLog("Unhandled message received: " + messageType);
                            break;

                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * change listener for taken figures
     * @param listener change listener
     * @author Felix
     */
    public void addTakenFiguresChangeListener(TakenFiguresChangeListener listener) {
        takenFiguresChangeListeners.add(listener);
    }

    /**
     * to notify the taken figures change listener
     * @author Felix
     */
    private void notifyTakenFiguresChangeListeners() {
        for (TakenFiguresChangeListener listener : takenFiguresChangeListeners) {
            listener.onTakenFiguresChanged(new ArrayList<>(takenFigures));
        }
    }

    /**
     * to update the taken figures
     * @author Felix, Maximilian
     */
    private void updateTakenFigures() {
        synchronized (playerListClient) {
            for (Player player : playerListClient) {
                if(!takenFigures.contains(player.getFigure())) {
                    takenFigures.add(player.getFigure());
                }
            }
        }
        notifyTakenFiguresChangeListeners();
    }

    /**
     * to start the 30 seconds countdown in the GUI
     * @param controller the client's controller
     * @param durationSeconds how long the timer is
     * @author Maximilian
     */
    private void startCountdown(ClientController controller, int durationSeconds) {
        Timeline timeline = new Timeline();
        seconds.set(durationSeconds);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
                    controller.updateCountdownImage(seconds.get());
                    if (seconds.get() <= 0) {
                        timeline.stop();
                    }
                    seconds.decrementAndGet();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}