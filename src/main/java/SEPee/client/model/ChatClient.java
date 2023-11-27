//ChatClient als Terminal
/*package SEPee.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Connected to server. Enter your username:");
            String username = userInputReader.readLine();
            System.out.println("Welcome to the chat, " + username + "!");

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                writer.println(username + ": " + userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
package SEPee.client.model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import SEPee.client.viewModel.ChatClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static SEPee.serialisierung.Serialisierer.gson;

public class ChatClient extends Application {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/ChatClient.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Chat Client");
            primaryStage.setScene(scene);

            ChatClientController controller = loader.getController();
            controller.init(this, primaryStage);

            primaryStage.setOnCloseRequest(event -> controller.shutdown());

            primaryStage.show();
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