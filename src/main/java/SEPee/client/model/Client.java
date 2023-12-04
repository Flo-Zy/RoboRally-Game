package SEPee.client.model;

import SEPee.client.viewModel.ClientController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.Player;
import SEPee.server.model.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Array;
import java.util.ArrayList;
import lombok.Getter;

@Getter
public class Client extends Application {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8887;
    public static ArrayList<Player> playerListClient = new ArrayList<>(); // ACHTUNG wird direkt von Player importiert!
    public static ArrayList<String> mapList = new ArrayList<>();


    private boolean receivedHelloClient = false;
    private static PrintWriter writer;

    public static PrintWriter getWriter(){
        return writer;
    }

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

            // Empfange HelloClient vom Server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Receive HelloClient from the server
            String serializedHelloClient = reader.readLine();
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);

            if (deserializedHelloClient.getMessageType().equals("HelloClient")) {

                //Stage wird initialisiert
                controller.init(this, primaryStage);
                primaryStage.setOnCloseRequest(event -> controller.shutdown());
                primaryStage.show();

                // Send HelloServer back to the server
                HelloServer helloServer = new HelloServer("EifrigeEremiten", false, "Version 0.1");
                String serializedHelloServer = Serialisierer.serialize(helloServer);
                writer.println(serializedHelloServer);

                receivedHelloClient = true; // Update flag after receiving HelloClient and Welcome
            } else {

                socket.close();
                //fehlermeldung
            }

            startServerMessageProcessing(socket, reader, controller, primaryStage, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void startServerMessageProcessing(Socket socket, BufferedReader reader, ClientController controller,
                                              Stage primaryStage, PrintWriter writer) {
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
                            //den empfangenen Spieler in der Client seitigen playerList speichern
                            playerListClient.add(new Player(name, id, figure));
                            System.out.println("Player added");
                            break;
                        case "PlayerStatus":
                            System.out.println("PlayerStatus");
                            PlayerStatus playerStatus = Deserialisierer.deserialize(serializedReceivedString, PlayerStatus.class);
                            for(int i = 0; i < playerListClient.size(); i++){
                                if(playerStatus.getMessageBody().getClientID() == playerListClient.get(i).getId()){
                                    playerListClient.get(i).setReady(playerStatus.getMessageBody().isReady());
                                }
                            }
                            for(int i = 0; i < playerListClient.size(); i++){
                                System.out.println(playerListClient.get(i).getName()+","+ playerListClient.get(i).isReady());
                            }
                            break;
                        case "SelectMap":
                            System.out.println("SelectMap");
                            SelectMap selectMap = Deserialisierer.deserialize(serializedReceivedString, SelectMap.class);
                            mapList = selectMap.getMessageBody().getAvailableMaps();
                            controller.init2(this, primaryStage);
                            String selectedMap = controller.getSelectedMap();
                            System.out.println(selectedMap);
                            break;
                        case "GameStarted":
                            System.out.println("Game Started");
                            break;
                        case "ReceivedChat":

                            String serializedReceivedChat = serializedReceivedString;
                            ReceivedChat deserializedReceivedChat = Deserialisierer.deserialize(serializedReceivedChat, ReceivedChat.class);

                            String fromName = null;
                            for(int i = 0; i < playerListClient.size(); i++){
                                if(deserializedReceivedChat.getMessageBody().getFrom() == playerListClient.get(i).getId()){
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
                            System.out.println("Error");
                            break;
                        case "ConnectionUpdate":
                            System.out.println("Connection Update");
                            break;
                        case "CardPlayed":
                            System.out.println("Card Played");
                            break;
                        case "CurrentPlayer":
                            System.out.println("Current Player");
                            break;
                        case "ActivePhase":
                            System.out.println("Active Phase");
                            break;
                        case "StartingPointTaken":
                            System.out.println("Starting Point Taken");
                            break;
                        case "YourCards":
                            System.out.println("Your Cards");
                            break;
                        case "NotYourCards":
                            System.out.println("Not Your Cards");
                            break;
                        case "ShuffleCoding":
                            System.out.println("Shuffle Coding");
                            break;
                        case "CardSelected":
                            System.out.println("Card Selected");
                            break;
                        case "TimerStarted":
                            System.out.println("Timer Started");
                            break;
                        case "TimerEnded":
                            System.out.println("Timer Ended");
                            break;
                        case "CardsYouGotNow":
                            System.out.println("Cards You Got Now");
                            break;
                        case "CurrentCards":
                            System.out.println("Current Cards");
                            break;
                        case "ReplaceCard":
                            System.out.println("Replace Card");
                            break;
                        case "Movement":
                            System.out.println("Movement");
                            break;
                        case "PlayerTurning":
                            System.out.println("Player Turning");
                            break;
                        case "DrawDamage":
                            System.out.println("Draw Damage");
                            break;
                        case "PickDamage":
                            System.out.println("Pick Damage");
                            break;
                        case "Animation":
                            System.out.println("Animation");
                            break;
                        case "Reboot":
                            System.out.println("Reboot");
                            break;
                        case "RebootDirection":
                            System.out.println("Reboot Direction");
                            break;
                        case "Energy":
                            System.out.println("Energy");
                            break;
                        case "CheckPointReached":
                            System.out.println("Check Point Reached");
                            break;
                        case "GameFinished":
                            System.out.println("GameFinished");
                            //hier noch berücksichtigen, dass sobald jemand gewonnen hat, nicht sofort alles schließen, sondern irgendwie anzeigen, wer gewonnen hat etc.
                            break;
                        default:
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