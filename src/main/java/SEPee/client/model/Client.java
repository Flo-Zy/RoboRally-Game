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

            startServerMessageProcessing(socket, reader, controller, primaryStage, writer);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServerMessageProcessing(Socket socket, BufferedReader reader, ClientController controller, Stage primaryStage, PrintWriter writer) {
        new Thread(() -> {
            try {
                while (true) {
                    String serializedReceivedString = reader.readLine();
                    Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedString, Message.class);
                    String messageType = deserializedReceivedString.getMessageType();

                    switch (messageType) {
                        case "Welcome":
                            String serializedWelcome = serializedReceivedString;
                            Welcome deserializedWelcome = Deserialisierer.deserialize(serializedWelcome, Welcome.class);
                            int receivedId = deserializedWelcome.getMessageBody().getClientID();
                            controller.setId(receivedId);
                            controller.init(this, primaryStage);
                            primaryStage.setOnCloseRequest(event -> controller.shutdown());
                            primaryStage.show();

                            // Beginne mit der Verarbeitung von Server-Nachrichten
                            new Thread(() -> {
                                /*try {
                                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    String serverMessage;
                                    while ((serverMessage = serverReader.readLine()) != null) {
                                        // Handle server messages
                                        System.out.println(serverMessage);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/
                            }).start();

                            //PlayerValues schicken
                            PlayerValues playerValues = new PlayerValues(controller.getName(), controller.getFigure());
                            String serializedPlayerValues = Serialisierer.serialize(playerValues);
                            writer.println(serializedPlayerValues);
                            break;
                        case "PlayerAdded":
                            // Handle PlayerAdded message
                            break;
                        case "PlayerStatus":
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
            } catch (IOException e) {
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
