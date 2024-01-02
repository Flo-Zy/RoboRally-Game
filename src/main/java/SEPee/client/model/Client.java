package SEPee.client.model;

import SEPee.client.viewModel.ClientController;
import SEPee.client.viewModel.MapController.DeathTrapController;
import SEPee.client.viewModel.MapController.DizzyHighwayController;
import SEPee.client.viewModel.MapController.ExtraCrispyController;
import SEPee.client.viewModel.MapController.LostBearingsController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.serialisierung.messageType.Error;
//auslagern
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.damageCard.*;
import SEPee.server.model.card.progCard.*;
import SEPee.server.model.gameBoard.ExtraCrispy;
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
import lombok.Getter;
import lombok.Setter;

@Getter
public class Client extends Application {

    private static final String SERVER_IP = "sep21.dbs.ifi.lmu.de";
    private static final int SERVER_PORT = 52018;
    // private static final String SERVER_IP = "localhost";
    // private static final int SERVER_PORT = 8886;
    @Getter
    @Setter
    private static ArrayList<Player> playerListClient = new ArrayList<>(); // ACHTUNG wird direkt von Player importiert!
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
    private static final Object lock = new Object(); // gemeinsames Sperr-Objekt
    private int registerCounter = 1;
    @Getter
    private static ArrayList<CurrentCards.ActiveCard> activeRegister = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

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

            // Empfange HelloClient vom Server
            String serializedHelloClient = reader.readLine();
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);

            if (deserializedHelloClient.getMessageType().equals("HelloClient") && deserializedHelloClient.getMessageBody().getProtocol().equals("Version 1.0")) {
                // Send HelloServer back to the server
                HelloServer helloServer = new HelloServer("EifrigeEremiten", false, "Version 1.0");
                String serializedHelloServer = Serialisierer.serialize(helloServer);
                writer.println(serializedHelloServer);

                receivedHelloClient = true; // Update flag after receiving HelloClient and Welcome

            } else {

                //socket.close();
                controller.shutdown();
                //System.exit(0);
            }

            startServerMessageProcessing(socket, reader, controller, primaryStage, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                            //System.out.println("Alive");
                            Alive alive = new Alive();
                            String serializedAlive = Serialisierer.serialize(alive);
                            writer.println(serializedAlive);
                            break;
                        case "Welcome":
                            System.out.println("Welcome");
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedReceivedString, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            controller.setId(receivedId);
                            //Stage wird initialisiert
                            Platform.runLater(() -> {
                                primaryStage.setOnCloseRequest(event -> controller.shutdown());
                                controller.init(this, primaryStage);
                                // PlayerValues schicken
                                PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure()-1);
                                String serializedPlayerValues = Serialisierer.serialize(playerValues);
                                writer.println(serializedPlayerValues);
                                primaryStage.show();
                            });
                            break;
                        case "PlayerAdded":
                            System.out.println("PlayerAdded");
                            PlayerAdded playerAdded = Deserialisierer.deserialize(serializedReceivedString, PlayerAdded.class);
                            String name = playerAdded.getMessageBody().getName();
                            int id = playerAdded.getMessageBody().getClientID();
                            int figure = playerAdded.getMessageBody().getFigure();

                            // Create a new Player object
                            Player newPlayer = new Player(name, id, figure+1);

                            // Add the new player to the client-side playerList
                            playerListClient.add(newPlayer);
                            for(Player player : playerListClient){
                                getTakenFigures().add(player.getFigure());
                            }

                            System.out.println("Player added");
                            for (int i = 0; i < playerListClient.size(); i++) {
                                System.out.println(playerListClient.get(i).getName() + "," + playerListClient.get(i).getId());
                            }
                            break;
                        case "PlayerStatus":
                            System.out.println("PlayerStatus");
                            PlayerStatus playerStatus = Deserialisierer.deserialize(serializedReceivedString, PlayerStatus.class);
                            for (int i = 0; i < playerListClient.size(); i++) {
                                if (playerStatus.getMessageBody().getClientID() == playerListClient.get(i).getId()) {
                                    playerListClient.get(i).setReady(playerStatus.getMessageBody().isReady());
                                }
                            }
                            break;
                        case "SelectMap":
                            System.out.println("SelectMap von " + controller.getName());
                            SelectMap selectMap = Deserialisierer.deserialize(serializedReceivedString, SelectMap.class);
                            mapList = selectMap.getMessageBody().getAvailableMaps();
                            Platform.runLater(() -> {
                                selectedMap1 = controller.showSelectMapDialog();
                                System.out.println(selectedMap1);
                                MapSelected mapSelected = new MapSelected(selectedMap1);
                                String serializedMapSelected = Serialisierer.serialize(mapSelected);
                                writer.println(serializedMapSelected);
                            });
                            break;
                        case "MapSelected":
                            System.out.println("Map wurde gewählt");
                            String serializedReceivedMap = serializedReceivedString;
                            MapSelected deserializedReceivedMap = Deserialisierer.deserialize(serializedReceivedMap, MapSelected.class);

                            FXMLLoader loader;
                            System.out.println(deserializedReceivedMap.getMessageBody().getMap());
                            switch (deserializedReceivedMap.getMessageBody().getMap()) {

                                case "Dizzy Highway":
                                    selectedMap1 = "DizzyHighway";
                                    loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                                    DizzyHighwayController mapController0 = loader.getController();
                                    break;
                                case "Extra Crispy":
                                    selectedMap1 = "ExtraCrispy";
                                    loader = new FXMLLoader(getClass().getResource("/SEPee/client/ExtraCrispy.fxml"));
                                    ExtraCrispyController mapController1 = loader.getController();
                                    break;
                                case "Lost Bearings":
                                    selectedMap1 = "LostBearings";
                                    loader = new FXMLLoader(getClass().getResource("/SEPee/client/LostBearings.fxml"));
                                    LostBearingsController mapController2 = loader.getController();
                                    break;
                                case "DeathTrap":
                                    selectedMap1 = "DeathTrap";
                                    loader = new FXMLLoader(getClass().getResource("/SEPee/client/DeathTrap.fxml"));
                                    DeathTrapController mapController3 = loader.getController();
                                    break;

                                default:
                                    System.out.println("Invalid Map");
                                    break;
                            }
                            System.out.println(selectedMap1);
                            break;

                        case "GameStarted":
                            System.out.println("Game Started");
                            GameStarted gameStarted = Deserialisierer.deserialize(serializedReceivedString, GameStarted.class);
                            System.out.println(selectedMap1);
                            if(selectedMap1.equals("DizzyHighway")) {
                                controller.loadDizzyHighwayFXML(this, primaryStage);
                            } else if(selectedMap1.equals("ExtraCrispy")) {
                                controller.loadExtraCrispyFXML(this, primaryStage);
                            } else if(selectedMap1.equals("LostBearings")) {
                                controller.loadLostBearingsFXML(this, primaryStage);
                            } else if(selectedMap1.equals("DeathTrap")) {
                                controller.loadDeathTrapFXML(this, primaryStage);
                            }

                            // weitere Maps

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
                                controller.appendToChatArea(receivedMessage);
                            } else {
                                String receivedMessage = (deserializedReceivedChat.getMessageBody().getMessage());
                                controller.appendToChatArea(receivedMessage);
                            }
                            break;
                        case "Error":
                            //empfängt den Error vom Server und printet eine Fehlermeldung auf die Konsole.
                            Error deserializedError = Deserialisierer.deserialize(serializedReceivedString, Error.class);
                            System.out.println(deserializedError.getMessageBody().getError());
                            break;
                        case "ConnectionUpdate":
                            System.out.println("Connection Update");
                            ConnectionUpdate connectionUpdate = Deserialisierer.deserialize(serializedReceivedString, ConnectionUpdate.class);
                            break;
                        case "CardPlayed":
                            System.out.println("Card Played");
                            CardPlayed cardPlayed = Deserialisierer.deserialize(serializedReceivedString, CardPlayed.class);
                            controller.appendToChatArea("> Player " + cardPlayed.getMessageBody().getClientID() +
                                    " played card " + cardPlayed.getMessageBody().getCard());
                            
                            break;
                        case "ActivePhase":
                            System.out.println("Active Phase");
                            ActivePhase activePhase = Deserialisierer.deserialize(serializedReceivedString, ActivePhase.class);
                            controller.setCurrentPhase(activePhase.getMessageBody().getPhase());
                            controller.appendToChatArea(">> Active Phase: " + controller.getCurrentPhase());
                            // wenn Phase 2: SelectedCard an Server (ClientHandler) senden
                            if(controller.getCurrentPhase() == 2){
                                controller.setRegisterVisibilityFalse();
                                controller.initRegister();
                                System.out.println(" Programmierungsphase");
                            }
                            if (controller.getCurrentPhase() == 3){
                                System.out.println(" Aktivierungsphase");
                            }
                            break;
                        case "CurrentPlayer":
                            System.out.println("Current Player");
                            CurrentPlayer currentPlayer = Deserialisierer.deserialize(serializedReceivedString, CurrentPlayer.class);
                            System.out.println("Client current Player checker: " + currentPlayer.getMessageBody().getClientID());

                            switch (controller.getCurrentPhase()) {
                                case 0:
                                    if (controller.getId() == currentPlayer.getMessageBody().getClientID()) { // wenn currentPlayerID dieser ClientID hier entspricht
                                        System.out.println("Aufbauphase");
                                        Platform.runLater(() -> {
                                            controller.setStartingPoint();
                                            System.out.println("StartingPoint wurde gewählt");

                                            SetStartingPoint setStartingPoint = new SetStartingPoint(controller.getStartPointX(), controller.getStartPointY());
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
                                    if(currentPlayer.getMessageBody().getClientID() == controller.getId()) {
                                        for (CurrentCards.ActiveCard activeCard : activeRegister) {
                                            if (activeCard.getClientID() == controller.getId()) {
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
                            System.out.println("Starting Point Taken");
                            StartingPointTaken startingPointTaken = Deserialisierer.deserialize(serializedReceivedString, StartingPointTaken.class);

                            if(selectedMap1.equals("Death Trap")) {
                                controller.addTakenStartingPointsDeathTrap(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            } else {
                                controller.addTakenStartingPoints(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            }

                            int takenClientID = startingPointTaken.getMessageBody().getClientID();
                            // Setze avatarPlayer auf Spieler der gerade einen StartingPoint gewählt hat
                            Player avatarPlayer = new Player("", -999,-999);
                            for(Player player : playerListClient){
                                if(player.getId() == startingPointTaken.getMessageBody().getClientID()){
                                    avatarPlayer = player;
                                }
                            }
                            //Player avatarPlayer = playerListClient.get(takenClientID - 1); // Ids beginnen bei 1 und playerListClient bei 0
                            controller.putAvatarDown(avatarPlayer, startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            System.out.println("Starting Point taken for ID: " + avatarPlayer.getId() + ", figure: " + avatarPlayer.getFigure());
                            break;

                        case "YourCards":
                            System.out.println("Your Cards");
                            YourCards yourCards = Deserialisierer.deserialize(serializedReceivedString, YourCards.class);
                            System.out.println(yourCards.getMessageBody().getCardsInHand());
                            // Füge in ChatArea: transformCardsInHandIntoString() macht aus ArrayList<String> einen formatierten String
                            controller.appendToChatArea("Your Hand:\n" + yourCards.getMessageBody().transformCardsInHandIntoString());

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
                                // initialisiere die 9 Karten von YourCards in Hand des players
                                controller.initDrawPile();
                                controller.initRegister();

                            break;

                        case "NotYourCards":
                            System.out.println("Not Your Cards");
                            NotYourCards notYourCards = Deserialisierer.deserialize(serializedReceivedString, NotYourCards.class);
                            System.out.println("(INFO) Player " + notYourCards.getMessageBody().getClientID() + " got " + notYourCards.getMessageBody().getCardsInHand() + " Cards");
                            break;
                        case "SelectionFinished":
                            SelectionFinished selectionFinished = Deserialisierer.deserialize(serializedReceivedString, SelectionFinished.class);
                            System.out.println(selectionFinished.getMessageBody().getClientID() + ": Selection Finished");
                            break;
                        case "ShuffleCoding":
                            System.out.println("Shuffle Coding");
                            ShuffleCoding shuffleCoding = Deserialisierer.deserialize(serializedReceivedString, ShuffleCoding.class);
                            break;
                        case "CardSelected":
                            System.out.println("Card Selected");
                            CardSelected cardSelected = Deserialisierer.deserialize(serializedReceivedString, CardSelected.class);
                            System.out.println("Player " + cardSelected.getMessageBody().getClientID() + " has set his register " + cardSelected.getMessageBody().getRegister());
                            break;
                        case "TimerStarted":
                            System.out.println("Timer Started");
                            TimerStarted timerStarted = Deserialisierer.deserialize(serializedReceivedString, TimerStarted.class);
                            controller.appendToChatArea(">> Timer Started \n>> (30 sec. left to fill your register)");
                            //thread sleep 30000
                            break;
                        case "TimerEnded":
                            System.out.println("Timer Ended");
                            TimerEnded timerEnded = Deserialisierer.deserialize(serializedReceivedString, TimerEnded.class);
                            controller.appendToChatArea(">> Timer Ended \n>> (empty register fields will be filled)");
                            controller.mapController.setCounter1(5);
                            break;
                        case "CardsYouGotNow":
                            System.out.println("Cards You Got Now");
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
                            System.out.println("Current Cards");
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
                            System.out.println("Replace Card");
                            ReplaceCard replaceCard = Deserialisierer.deserialize(serializedReceivedString, ReplaceCard.class);
                            break;
                        case "Movement":
                            System.out.println("Movement");
                            Movement movement = Deserialisierer.deserialize(serializedReceivedString, Movement.class);

                            int clientIdToMove = movement.getMessageBody().getClientID();
                            int newX = movement.getMessageBody().getX();
                            int newY = movement.getMessageBody().getY();
                            controller.movementPlayed(clientIdToMove,newX, newY);

                            break;
                        case "PlayerTurning":
                            System.out.println("Player Turning");
                            PlayerTurning playerTurning = Deserialisierer.deserialize(serializedReceivedString, PlayerTurning.class);

                            int clientIdToTurn = playerTurning.getMessageBody().getClientID();
                            String rotation = playerTurning.getMessageBody().getRotation();
                            controller.playerTurn(clientIdToTurn, rotation);

                            break;
                        case "DrawDamage":
                            System.out.println("Draw Damage");
                            DrawDamage drawDamage = Deserialisierer.deserialize(serializedReceivedString, DrawDamage.class);

                            int damagedID = drawDamage.getMessageBody().getClientID(); // die ID die karten ziehen soll!

                            ArrayList<String> damageCardsDrawn = drawDamage.getMessageBody().getCards();

                            for(Player player : playerListClient) {
                                if (player.getId() == damagedID) {
                                    controller.appendToChatArea(player.getName() + " hat diese Karten kassiert: " + damageCardsDrawn + "!");
                                }
                            }




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
                            int winnerId = gameFinished.getMessageBody().getClientID();

                            for(Player player : playerListClient) {
                                if (player.getId() == winnerId) {
                                    System.out.println("winner id ist " + winnerId);
                                    controller.appendToChatArea(player.getName() + " has won this game!!");
                                    System.out.println("ausgabe hier");
                                }
                            }
                            Thread.sleep(10000);
                            controller.shutdown();
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