package SEPee.client.model;

import SEPee.client.viewModel.ClientController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
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

public class Client extends Application {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8887;

    private boolean receivedHelloClient = false;

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
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Receive HelloClient from the server
            String serializedHelloClient = reader.readLine();
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);

            if (deserializedHelloClient.getMessageType().equals("HelloClient")) {

                // Send HelloServer back to the server
                HelloServer helloServer = new HelloServer("EifrigeEremiten", false, "Version 0.1");
                String serializedHelloServer = Serialisierer.serialize(helloServer);
                writer.println(serializedHelloServer);

                //Stage wird initialisiert
                controller.init(this, primaryStage);
                primaryStage.setOnCloseRequest(event -> controller.shutdown());
                primaryStage.show();

                // PlayerValues schicken
                PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure());
                String serializedPlayerValues = Serialisierer.serialize(playerValues);
                writer.println(serializedPlayerValues);

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
                        case "Welcome":
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedReceivedString, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            controller.setId(receivedId);
                            // Handle PlayerAdded message
                            break;
                        case "PlayerAdded":
                            System.out.println("switch case playeradded angekommen");
                            // Handle PlayerAdded message
                            break;
                        case "PlayerStatus":
                            System.out.println("switch case playerstatus angekommen");
                            // Handle PlayerStatus message
                            break;
                        case "SelectMap":
                            // Handle SelectMap message
                            break;
                        case "ReceivedChat":
                            // Handle ReceivedChat message
                            break;
                        case "GameFinished":
                            // Handle GameFinished message
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
