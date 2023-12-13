package SEPee.client.model;

import SEPee.client.viewModel.ClientController;
import SEPee.client.viewModel.MapController.DizzyHighwayController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.serialisierung.messageType.Error;
//auslagern
import SEPee.server.model.Player;
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
    private static ArrayList<Integer> takenFigures = new ArrayList<>();
    private boolean receivedHelloClient = false;
    @Getter
    private static PrintWriter writer;
    private static final Object lock = new Object(); // gemeinsames Sperr-Objekt

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
                    Client.getTakenFigures().add(player.getFigure());
                }

                //Stage wird initialisiert
                primaryStage.setOnCloseRequest(event -> controller.shutdown());
                controller.init(this, primaryStage);

                PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure());
                String serializedPlayerValues = Serialisierer.serialize(playerValues);
                writer.println(serializedPlayerValues);

                primaryStage.show();


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
                            System.out.println("Alive");
                            break;
                        case "Welcome":
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedReceivedString, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            controller.setId(receivedId);
                            // PlayerValues schicken
                            PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure());
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
                            System.out.println("SelectMap" + controller.getName());
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

                            DizzyHighwayController mapController = null;
                            switch (deserializedReceivedMap.getMessageBody().getMap()) {

                                case "DizzyHighway":
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                                    mapController = loader.getController();
                                    break;
                                default:
                                    System.out.println("Invalid Map");
                                    break;
                            }


                            break;
                        case "GameStarted":
                            System.out.println("Game Started");
                            GameStarted gameStarted = Deserialisierer.deserialize(serializedReceivedString, GameStarted.class);
                            controller.loadDizzyHighwayFXML(this, primaryStage);
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
                            //System.out.println(fromName);
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
                            break;
                        case "ActivePhase":
                            System.out.println("Active Phase");
                            ActivePhase activePhase = Deserialisierer.deserialize(serializedReceivedString, ActivePhase.class);
                            controller.setCurrentPhase(activePhase.getMessageBody().getPhase());
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
                                        for (Player player : playerListClient) { // in Programmierphase is
                                            // Schritt 1: init drawPile in totalHand
                                            controller.initDrawPile(player.getId());
                                            System.out.println("Player: " + player.getName() + " got 9 cards.");

                                            // Schritt 2: Auswählen vom DrawPile

                                            // string an server?
                                        }
                                    break;
                                case 3:
                                    System.out.println("Aktivierungsphase");
                                    break;
                            }
                            break;
                        case "StartingPointTaken":
                            System.out.println("Starting Point Taken");
                            StartingPointTaken startingPointTaken = Deserialisierer.deserialize(serializedReceivedString, StartingPointTaken.class);
                            controller.addTakenStartingPoints(startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
/*
                            int takenClientID = startingPointTaken.getMessageBody().getClientID();
                            for (Player player : playerListClient) {
                                if (player.getId() == takenClientID) {
                                    controller.putAvatarDown(player, startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                                    System.out.println("Starting Point taken for ID: " + player.getId() + ", figure: " + player.getFigure());
                                    break;
                                }
                            }
 */
                            int takenClientID = startingPointTaken.getMessageBody().getClientID();
                            // Setze avatarPlayer auf Spieler der gerade einen StartingPoint gewählt hat
                            Player avatarPlayer = playerListClient.get(takenClientID - 1); // Ids beginnen bei 1 und playerListClient bei 0
                            controller.putAvatarDown(avatarPlayer, startingPointTaken.getMessageBody().getX(), startingPointTaken.getMessageBody().getY());
                            System.out.println("Starting Point taken for ID: " + avatarPlayer.getId() + ", figure: " + avatarPlayer.getFigure());
                            break;

                        case "YourCards":
                            System.out.println("Your Cards");
                            YourCards yourCards = Deserialisierer.deserialize(serializedReceivedString, YourCards.class);

                            // Füge in ChatArea: transformCardsInHandIntoString() macht aus ArrayList<String> einen formatierten String
                            controller.appendToChatArea(yourCards.getMessageBody().transformCardsInHandIntoString());

                            // update im ClientController die clientHand
                            controller.setClientHand(yourCards.getMessageBody().getCardsInHand());

                            // initialisiere 9 Karten in Hand des players
                            controller.initDrawPile(controller.getId());
                            break;

                        case "NotYourCards":
                            System.out.println("Not Your Cards");
                            NotYourCards notYourCards = Deserialisierer.deserialize(serializedReceivedString, NotYourCards.class);
                            break;

                        case "ShuffleCoding":
                            System.out.println("Shuffle Coding");
                            ShuffleCoding shuffleCoding = Deserialisierer.deserialize(serializedReceivedString, ShuffleCoding.class);
                            break;
                        case "CardSelected":
                            System.out.println("Card Selected");
                            CardSelected cardSelected = Deserialisierer.deserialize(serializedReceivedString, CardSelected.class);
                            break;
                        case "TimerStarted":
                            System.out.println("Timer Started");
                            TimerStarted timerStarted = Deserialisierer.deserialize(serializedReceivedString, TimerStarted.class);
                            break;
                        case "TimerEnded":
                            System.out.println("Timer Ended");
                            TimerEnded timerEnded = Deserialisierer.deserialize(serializedReceivedString, TimerEnded.class);
                            break;
                        case "CardsYouGotNow":
                            System.out.println("Cards You Got Now");
                            CardsYouGotNow cardsYouGotNow = Deserialisierer.deserialize(serializedReceivedString, CardsYouGotNow.class);
                            break;
                        case "CurrentCards":
                            System.out.println("Current Cards");
                            CurrentCards currentCards = Deserialisierer.deserialize(serializedReceivedString, CurrentCards.class);
                            break;
                        case "ReplaceCard":
                            System.out.println("Replace Card");
                            ReplaceCard replaceCard = Deserialisierer.deserialize(serializedReceivedString, ReplaceCard.class);
                            break;
                        case "Movement":
                            System.out.println("Movement");
                            Movement movement = Deserialisierer.deserialize(serializedReceivedString, Movement.class);
                            break;
                        case "PlayerTurning":
                            System.out.println("Player Turning");
                            PlayerTurning playerTurning = Deserialisierer.deserialize(serializedReceivedString, PlayerTurning.class);
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