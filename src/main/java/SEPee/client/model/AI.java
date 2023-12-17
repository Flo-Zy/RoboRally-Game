package SEPee.client.model;

import SEPee.client.viewModel.AIController;
import SEPee.client.viewModel.MapController.DizzyHighwayController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.Error;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.progCard.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

@Getter
public class AI extends Application {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8886;
    @Getter
    @Setter
    private static ArrayList<Player> playerListClient = new ArrayList<>(); // ACHTUNG wird direkt von Player importiert!
    @Getter
    @Setter
    private static ArrayList<String> mapList = new ArrayList<>();
    private String selectedMap1;
    @Getter
    @Setter
    private static ArrayList<Integer> takenFigures = Client.getTakenFigures();
    private boolean receivedHelloClient = false;
    @Getter
    private static PrintWriter writer;
    private static final Object lock = new Object(); // gemeinsames Sperr-Objekt
    private int registerCounter = 1;
    private ArrayList<CurrentCards.ActiveCard> activeRegister = Client.getActiveRegister();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/AI.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("AI");
            primaryStage.setScene(scene);

            AIController aiController = loader.getController();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Empfange HelloClient vom Server
            String serializedHelloClient = reader.readLine();
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);

            if (deserializedHelloClient.getMessageType().equals("HelloClient") && deserializedHelloClient.getMessageBody().getProtocol().equals("Version 1.0")) {
                boolean x = true;
                while (x) {
                    String serializedPlayerlist = reader.readLine();
                    Message deserializedPlayerlist = Deserialisierer.deserialize(serializedPlayerlist, Message.class);
                    if (deserializedPlayerlist.getMessageType().equals("PlayerAdded")) {
                        PlayerAdded addPlayer = Deserialisierer.deserialize(serializedPlayerlist, PlayerAdded.class);
                        if (addPlayer.getMessageBody().getClientID() < 0) {
                            x = false;
                        } else {
                            playerListClient.add(new Player(addPlayer.getMessageBody().getName(), addPlayer.getMessageBody().getClientID(), addPlayer.getMessageBody().getFigure()));
                        }
                    } else {
                        x = false;
                    }
                }

                for (int i = 0; i < playerListClient.size(); i++) {
                    System.out.println(playerListClient.get(i).getName());
                    System.out.println(playerListClient.get(i).getFigure());
                }
                //save taken figures in takenFigures
                for (Player player : playerListClient) {
                    AI.getTakenFigures().add(player.getFigure());
                }

                //Stage wird initialisiert
                primaryStage.setOnCloseRequest(event -> aiController.shutdown());
                aiController.init(this, primaryStage);

                PlayerValues playerValues = new PlayerValues(aiController.getName(), aiController.getFigure());
                String serializedPlayerValues = Serialisierer.serialize(playerValues);
                writer.println(serializedPlayerValues);

                primaryStage.show();


                // Send HelloServer back to the server
                HelloServer helloServer = new HelloServer("EifrigeEremiten", true, "Version 1.0");
                String serializedHelloServer = Serialisierer.serialize(helloServer);
                writer.println(serializedHelloServer);

                receivedHelloClient = true; // Update flag after receiving HelloClient and Welcome

            } else {

                //socket.close();
                aiController.shutdown();
                //System.exit(0);
            }

            startServerMessageProcessing(socket, reader, aiController, primaryStage, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServerMessageProcessing(Socket socket, BufferedReader reader, AIController aiController, Stage primaryStage, PrintWriter writer) {
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
                            System.out.println("AI - Alive");
                            Alive alive = new Alive();
                            String serializedAlive = Serialisierer.serialize(alive);
                            writer.println(serializedAlive);
                            break;
                        case "Welcome":
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedReceivedString, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            aiController.setId(receivedId);
                            // PlayerValues schicken
                            PlayerValues playerValues = new PlayerValues(aiController.getName(), aiController.getFigure());
                            String serializedPlayerValues = Serialisierer.serialize(playerValues);
                            writer.println(serializedPlayerValues);

                            break;
                        case "PlayerAdded":
                            PlayerAdded playerAdded = Deserialisierer.deserialize(serializedReceivedString, PlayerAdded.class);
                            String name = playerAdded.getMessageBody().getName();
                            int id = playerAdded.getMessageBody().getClientID();
                            int figure = playerAdded.getMessageBody().getFigure();

                            // Create a new Player object
                            Player newPlayer = new Player(name, id, figure);

                            // Add the new player to the client-side playerList
                            playerListClient.add(newPlayer);

                            System.out.println("AI - Player added");
                            for (int i = 0; i < playerListClient.size(); i++) {
                                System.out.println(playerListClient.get(i).getName() + "," + playerListClient.get(i).getId());
                            }
                            break;
                        case "PlayerStatus":
                            System.out.println("AI - PlayerStatus");
                            PlayerStatus playerStatus = Deserialisierer.deserialize(serializedReceivedString, PlayerStatus.class);
                            for (int i = 0; i < playerListClient.size(); i++) {
                                if (playerStatus.getMessageBody().getClientID() == playerListClient.get(i).getId()) {
                                    playerListClient.get(i).setReady(playerStatus.getMessageBody().isReady());
                                }
                            }
                            break;
                        case "SelectMap":
                            System.out.println("AI - SelectMap" + aiController.getName());
                            SelectMap selectMap = Deserialisierer.deserialize(serializedReceivedString, SelectMap.class);
                            // lade ArrayList<String> mit einem element: "DizzyHighway"
                            mapList = selectMap.getMessageBody().getAvailableMaps();

                            Platform.runLater(() -> {
                                selectedMap1 = "DizzyHighway";
                                System.out.println(selectedMap1);
                                MapSelected mapSelected = new MapSelected(selectedMap1);
                                String serializedMapSelected = Serialisierer.serialize(mapSelected);
                                writer.println(serializedMapSelected);
                            });

                            break;
                        case "MapSelected":
                            System.out.println("AI - Map wurde gewählt");
                            String serializedReceivedMap = serializedReceivedString;
                            MapSelected deserializedReceivedMap = Deserialisierer.deserialize(serializedReceivedMap, MapSelected.class);

                            DizzyHighwayController mapController = null;
                            switch (deserializedReceivedMap.getMessageBody().getMap()) {

                                case "DizzyHighway":
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                                    mapController = loader.getController();
                                    break;
                                default:
                                    System.out.println("AI - Invalid Map");
                                    break;
                            }
                            break;
                        case "GameStarted":
                            System.out.println("AI - Game Started");
                            GameStarted gameStarted = Deserialisierer.deserialize(serializedReceivedString, GameStarted.class);
                            aiController.loadDizzyHighwayFXMLAI(this, primaryStage);
                            break;
                        case "ReceivedChat":
                            String serializedReceivedChat = serializedReceivedString;
                            ReceivedChat deserializedReceivedChat = Deserialisierer.deserialize(serializedReceivedChat, ReceivedChat.class);

                            String fromName = null;
                            for (int i = 0; i < playerListClient.size(); i++) {
                                if (deserializedReceivedChat.getMessageBody().getFrom() == playerListClient.get(i).getId()) {
                                    fromName = playerListClient.get(i).getName();
                                }
                            }
                            if (fromName != null) {
                                String receivedMessage = (fromName + ": " + deserializedReceivedChat.getMessageBody().getMessage());
                                aiController.appendToChatArea(receivedMessage);
                            } else {
                                String receivedMessage = (deserializedReceivedChat.getMessageBody().getMessage());
                                aiController.appendToChatArea(receivedMessage);
                            }
                            break;
                        case "Error":
                            //empfängt den Error vom Server und printet eine Fehlermeldung auf die Konsole.
                            Error deserializedError = Deserialisierer.deserialize(serializedReceivedString, Error.class);
                            System.out.println(deserializedError.getMessageBody().getError());
                            break;
                        case "ConnectionUpdate":
                            System.out.println("AI - Connection Update");
                            ConnectionUpdate connectionUpdate = Deserialisierer.deserialize(serializedReceivedString, ConnectionUpdate.class);
                            break;
                        case "CardPlayed":
                            System.out.println("AI - Card Played");
                            CardPlayed cardPlayed = Deserialisierer.deserialize(serializedReceivedString, CardPlayed.class);
                            aiController.appendToChatArea("> Player " + cardPlayed.getMessageBody().getClientID() +
                                    " played card " + cardPlayed.getMessageBody().getCard());
                            
                            break;
                        case "ActivePhase":
                            System.out.println("AI - Active Phase");
                            ActivePhase activePhase = Deserialisierer.deserialize(serializedReceivedString, ActivePhase.class);
                            aiController.setCurrentPhase(activePhase.getMessageBody().getPhase());
                            aiController.appendToChatArea(">> Active Phase: " + aiController.getCurrentPhase());
                            // wenn Phase 2: SelectedCard an Server (ClientHandler) senden
                            if(aiController.getCurrentPhase() == 2){
                                aiController.setRegisterVisibilityFalse();
                                aiController.initRegister();
                                System.out.println(" Programmierungsphase");
                            }
                            if (aiController.getCurrentPhase() == 3){
                                System.out.println(" Aktivierungsphase");
                            }
                            break;
                        case "CurrentPlayer":
                            System.out.println("AI - Current Player");
                            CurrentPlayer currentPlayer = Deserialisierer.deserialize(serializedReceivedString, CurrentPlayer.class);
                            System.out.println("AI current Player checker: " + currentPlayer.getMessageBody().getClientID());

                            switch (aiController.getCurrentPhase()) {
                                case 0:
                                    if (aiController.getId() == currentPlayer.getMessageBody().getClientID()) { // wenn currentPlayerID dieser ClientID hier entspricht
                                        System.out.println("Aufbauphase");
                                        Platform.runLater(() -> {
                                            aiController.setStartingPoint();
                                            System.out.println("StartingPoint wurde gewählt");

                                            SetStartingPoint setStartingPoint = new SetStartingPoint(aiController.getStartPointX(), aiController.getStartPointY());
                                            String serializedSetStartingPoint = Serialisierer.serialize(setStartingPoint);
                                            writer.println(serializedSetStartingPoint);
                                        });
                                    }
                                    break;
                                case 1:
                                    System.out.println("Upgradephase");
                                    break;
                                case 2:
                                    System.out.println("Programmierphase");
                                    break;
                                case 3:
                                    System.out.println("Aktivierungsphase");
                                    if(currentPlayer.getMessageBody().getClientID() == aiController.getId()) {
                                        for (CurrentCards.ActiveCard activeCard : activeRegister) {
                                            if (activeCard.getClientID() == aiController.getId()) {
                                                PlayCard playCard = new PlayCard(activeCard.getCard());
                                                String serializedPlayCard = Serialisierer.serialize(playCard);
                                                writer.println(serializedPlayCard);
                                            }
                                        }
                                    }
                                    break;
                            }
                            break;
                        case "StartingPointTaken":
                            System.out.println("AI - Starting Point Taken");
                            StartingPointTaken startingPointTaken = Deserialisierer.deserialize(serializedReceivedString, StartingPointTaken.class);
                            aiController.addTakenStartingPoints(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());

                            int takenClientID = startingPointTaken.getMessageBody().getClientID();
                            // Setze avatarPlayer auf Spieler der gerade einen StartingPoint gewählt hat
                            Player avatarPlayer = playerListClient.get(takenClientID - 1); // Ids beginnen bei 1 und playerListClient bei 0
                            aiController.putAvatarDown(avatarPlayer, startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            System.out.println("Starting Point taken for ID: " + avatarPlayer.getId() + ", figure: " + avatarPlayer.getFigure());
                            break;

                        case "YourCards":
                            System.out.println("AI - Your Cards");
                            YourCards yourCards = Deserialisierer.deserialize(serializedReceivedString, YourCards.class);

                            // Füge in ChatArea: transformCardsInHandIntoString() macht aus ArrayList<String> einen formatierten String
                            aiController.appendToChatArea("AI Hand:\n" + yourCards.getMessageBody().transformCardsInHandIntoString());

                            // update im ClientController die clientHand
                            ArrayList<Card> drawPile = new ArrayList<>();
                            for (String cardName : yourCards.getMessageBody().getCardsInHand()) {
                                switch (cardName) {
                                    case "Again":
                                        drawPile.add(new Again());
                                        break; // Füge diese Unterbrechungspunkte hinzu, um sicherzustellen, dass nur eine Karte hinzugefügt wird
                                    case "BackUp":
                                        drawPile.add(new BackUp());
                                        break;
                                    case "LeftTurn":
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
                                    case "RightTurn":
                                        drawPile.add(new RightTurn());
                                        break;
                                    case "UTurn":
                                        drawPile.add(new UTurn());
                                        break;
                                }
                            }
                            aiController.setClientHand(drawPile);

                            // initialisiere die 9 Karten von YourCards in Hand des players
                            aiController.initDrawPile();
                            break;

                        case "NotYourCards":
                            System.out.println("AI - Not Your Cards");
                            NotYourCards notYourCards = Deserialisierer.deserialize(serializedReceivedString, NotYourCards.class);
                            System.out.println("(INFO) Player " + notYourCards.getMessageBody().getClientID() + " got " + notYourCards.getMessageBody().getCardsInHand() + " Cards");
                            break;
                        case "SelectionFinished":
                            SelectionFinished selectionFinished = Deserialisierer.deserialize(serializedReceivedString, SelectionFinished.class);
                            System.out.println("AI - " + selectionFinished.getMessageBody().getClientID() + ": Selection Finished");
                            break;
                        case "ShuffleCoding":
                            System.out.println("AI - Shuffle Coding");
                            ShuffleCoding shuffleCoding = Deserialisierer.deserialize(serializedReceivedString, ShuffleCoding.class);
                            break;
                        case "CardSelected":
                            System.out.println("AI - Card Selected");
                            CardSelected cardSelected = Deserialisierer.deserialize(serializedReceivedString, CardSelected.class);
                            System.out.println("AI - Player " + cardSelected.getMessageBody().getClientID() + " has set his register " + cardSelected.getMessageBody().getRegister());
                            break;
                        case "TimerStarted":
                            System.out.println("AI - Timer Started");
                            TimerStarted timerStarted = Deserialisierer.deserialize(serializedReceivedString, TimerStarted.class);
                            aiController.appendToChatArea(">> Timer Started \n>> (30 sec. left to fill your register)");
                            //thread sleep 30000
                            break;
                        case "TimerEnded":
                            System.out.println("AI - Timer Ended");
                            TimerEnded timerEnded = Deserialisierer.deserialize(serializedReceivedString, TimerEnded.class);
                            aiController.appendToChatArea(">> Timer Ended \n>> (empty register fields will be filled)");
                            aiController.mapController.setCounter1(5);
                            break;
                        case "CardsYouGotNow":
                            System.out.println("AI - Cards You Got Now");
                            CardsYouGotNow cardsYouGotNow = Deserialisierer.deserialize(serializedReceivedString, CardsYouGotNow.class);

                            ArrayList<Card> nextCards = new ArrayList<>();

                            for (String cardName : cardsYouGotNow.getMessageBody().getCards()) {
                                switch (cardName) {
                                    case "Again":
                                        nextCards.add(new Again());
                                        break; // Füge diese Unterbrechungspunkte hinzu, um sicherzustellen, dass nur eine Karte hinzugefügt wird
                                    case "BackUp":
                                        nextCards.add(new BackUp());
                                        break;
                                    case "LeftTurn":
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
                                    case "RightTurn":
                                        nextCards.add(new RightTurn());
                                        break;
                                    case "UTurn":
                                        nextCards.add(new UTurn());
                                        break;
                                }
                            }
                            aiController.fillEmptyRegister(nextCards);
                            break;
                        case "CurrentCards":
                            System.out.println("AI - Current Cards");
                            CurrentCards currentCards = Deserialisierer.deserialize(serializedReceivedString, CurrentCards.class);

                            activeRegister = currentCards.getMessageBody().getActiveCards();

                            aiController.appendToChatArea(">> Played Register: " + registerCounter);

                            if (registerCounter == 5){
                                registerCounter = 1;
                            }else {
                                registerCounter++;
                            }

                            break;
                        case "AI - ReplaceCard":
                            System.out.println("Replace Card");
                            ReplaceCard replaceCard = Deserialisierer.deserialize(serializedReceivedString, ReplaceCard.class);
                            break;
                        case "AI - Movement":
                            System.out.println("Movement");
                            Movement movement = Deserialisierer.deserialize(serializedReceivedString, Movement.class);

                            int clientIdToMove = movement.getMessageBody().getClientID();
                            int newX = movement.getMessageBody().getX();
                            int newY = movement.getMessageBody().getY();
                            aiController.movementPlayed(clientIdToMove,newX, newY);

                            break;
                        case "PlayerTurning":
                            System.out.println("Player Turning");
                            PlayerTurning playerTurning = Deserialisierer.deserialize(serializedReceivedString, PlayerTurning.class);

                            int clientIdToTurn = playerTurning.getMessageBody().getClientID();
                            String rotation = playerTurning.getMessageBody().getRotation();
                            aiController.playerTurn(clientIdToTurn, rotation);

                            break;
                        case "DrawDamage":
                            System.out.println("Draw Damage");
                            DrawDamage drawDamage = Deserialisierer.deserialize(serializedReceivedString, DrawDamage.class);
                            break;
                        case "PickDamage":
                            System.out.println("Pick Damage");
                            PickDamage pickDamage = Deserialisierer.deserialize(serializedReceivedString, PickDamage.class);
                            break;
                        case "Animation":
                            System.out.println("Animation");
                            Animation animation = Deserialisierer.deserialize(serializedReceivedString, Animation.class);
                            break;
                        case "Reboot":
                            System.out.println("Reboot");
                            Reboot reboot = Deserialisierer.deserialize(serializedReceivedString, Reboot.class);
                            break;
                        case "RebootDirection":
                            System.out.println("Reboot Direction");
                            RebootDirection rebootDirection = Deserialisierer.deserialize(serializedReceivedString, RebootDirection.class);
                            break;
                        case "Energy":
                            System.out.println("Energy");
                            Energy energy = Deserialisierer.deserialize(serializedReceivedString, Energy.class);
                            break;
                        case "CheckPointReached":
                            System.out.println("Check Point Reached");
                            CheckPointReached checkPointReached = Deserialisierer.deserialize(serializedReceivedString, CheckPointReached.class);
                            break;
                        case "GameFinished":
                            System.out.println("GameFinished");
                            GameFinished gameFinished = Deserialisierer.deserialize(serializedReceivedString, GameFinished.class);
                            //hier noch berücksichtigen, dass sobald jemand gewonnen hat, nicht sofort alles schließen, sondern irgendwie anzeigen, wer gewonnen hat etc.
                            break;
                        default:
                            //kann man entfernen?
                            System.out.println("Unhandled message received: " + messageType);
                            break;

                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String getServerIp() {
        return SERVER_IP;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}