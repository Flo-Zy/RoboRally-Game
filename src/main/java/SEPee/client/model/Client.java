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



            // Empfange HelloClient vom Server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serializedHelloClient = reader.readLine();
            System.out.println(serializedHelloClient);


            //prüfen, dass ein HelloClient angekommen ist, wenn es angekommne ist -> HelloServer zurückschicken
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);



            if(deserializedHelloClient.getMessageType().equals("HelloClient")){


                // Sende Antwort an den Server
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                HelloServer helloServer = new HelloServer("EifrigeEremiten", false, "Version 0.1");
                String serializedHelloServer = Serialisierer.serialize(helloServer);
                //schicken
                writer.println(serializedHelloServer);


                boolean loop = true;
                while (loop){

                    String serializedReceivedString = reader.readLine();
                    Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedString, Message.class);
                    String input = deserializedReceivedString.getMessageType();


                    switch(input){
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
                            System.out.println("PlayerAdded");
                            break;
                        case "PlayerStatus":
                            System.out.println("PlayerStatus");
                            break;
                        case "SelectMap":
                            System.out.println("SelectMap");
                            break;
                        case "ReceivedChat":
                            System.out.println("ReceivedChat");
                            break;
                        case "GameFinished":
                            System.out.println("GameFinished");
                            //hier noch berücksichtigen, dass sobald jemand gewonnen hat, nicht sofort alles schließen, sondern irgendwie anzeigen, wer gewonnen hat etc.
                            loop = false;
                            break;
                        default:
                            System.out.println("Wrong message received!");
                            break;

                    }
                }

            }else{
                //reparieren, dass es ohne Fehlermeldung schließt
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getServerIp() {
        return SERVER_IP;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}