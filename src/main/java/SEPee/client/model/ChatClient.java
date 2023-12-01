package SEPee.client.model;

import SEPee.client.viewModel.ChatClientController;
import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.messageType.HelloServer;
import SEPee.serialisierung.messageType.HelloClient;
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

import static javafx.application.Application.launch;

public class ChatClient extends Application {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8887;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/ChatClient.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Chat Client");
            primaryStage.setScene(scene);

            ChatClientController controller = loader.getController();

            // Empfange HelloClient vom Server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serializedHelloClient = reader.readLine();
            System.out.println(serializedHelloClient);

            //HelloServer helloServer = Deserialisierer.deserialize(serializedHelloClient, HelloServer.class);
            HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);
            //test ob man Protokoll Version holen kann

            String versionProtocol = deserializedHelloClient.getMessageBody().getProtocol();
            System.out.println(versionProtocol);

            // Antworte dem Server mit Gruppeninformationen und Protokollversion
            // Hier musst du die entsprechenden Informationen einfügen
            // ...

            // Sende Antwort an den Server
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            if("Version 0.1".equals(versionProtocol)){
                writer.println("OK");

                controller.init(this, primaryStage);

                primaryStage.setOnCloseRequest(event -> controller.shutdown());

                primaryStage.show();
            }else{
                writer.println("NOT OK");
                socket.close();
            }

            // Beginne mit der Verarbeitung von Server-Nachrichten
            new Thread(() -> {
                try {
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        // Handle server messages
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Hier kannst du weitere Logik für die Client-Anwendung implementieren
            // ...

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