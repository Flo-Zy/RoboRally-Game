//ChatClient als Terminal
/*package SEPee.client.viewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatClientController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    // Implementieren Sie die Logik für den Controller hier
    // Beachten Sie, dass Sie eine Verbindung zum Server herstellen und Nachrichten senden müssen
}*/
//ChatClient als JavaFX-Fenster
/*package SEPee.client.viewModel;

import SEPee.client.model.ChatClient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    private ChatClient chatClient;
    private Socket socket;
    private PrintWriter writer;

    public void init(ChatClient chatClient, Stage stage) {
        this.chatClient = chatClient;

        try {
            this.socket = new Socket(chatClient.getServerIp(), chatClient.getServerPort());
            this.writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        appendToChatArea(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendButton.setOnAction(event -> sendMessage());

        stage.setOnCloseRequest(event -> shutdown());
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            writer.println(chatClient.getServerIp() + ": " + message);
            messageField.clear();
        }
    }

    public void appendToChatArea(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }

    public void shutdown() {
        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
package SEPee.client.viewModel;

import SEPee.client.model.ChatClient;
import SEPee.client.model.ChatMessage;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatClientController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    private ChatClient chatClient;
    private Socket socket;
    private PrintWriter writer;
    private String username;
    private Gson gson;

    public void init(ChatClient chatClient, Stage stage) {
        this.chatClient = chatClient;
        this.gson = new Gson();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nutzername");
        dialog.setHeaderText("Wie ist dein Nutzername?");
        dialog.setContentText("Nutzername:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            this.username = result.get();
            stage.setTitle("Chat Client - " + username);

            try {
                this.socket = new Socket(chatClient.getServerIp(), chatClient.getServerPort());
                this.writer = new PrintWriter(socket.getOutputStream(), true);

                writer.println(username + " ist dem Chat beigetreten.");

                new Thread(() -> {
                    try {
                        BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String serverMessage;
                        while ((serverMessage = serverReader.readLine()) != null) {
                            processReceivedMessage(serverMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sendButton.setOnAction(event -> sendMessage());
            stage.setOnCloseRequest(event -> shutdown());
        } else {
            // Nutzer hat die Eingabe abgebrochen
            Platform.exit();
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(username, message);
            String serializedMessage = gson.toJson(chatMessage);
            writer.println(serializedMessage);
            messageField.clear();
        }
    }

    public void processReceivedMessage(String receivedJson) {
        List<ChatMessage> chatMessages = ChatClient.deserializeChatMessages(receivedJson);

        // Verarbeite die empfangenen Chat-Nachrichten weiter, z.B. zeige sie in der Chat-Ansicht an
        if (chatMessages != null) {
            for (ChatMessage message : chatMessages) {
                String displayMessage = message.getUsername() + ": " + message.getMessage();
                appendToChatArea(displayMessage);
            }
        }
    }


    public String prepareMessagesToSend(List<ChatMessage> messages) {
        return ChatClient.serializeChatMessages(messages);
    }

    public void sendMessage(List<ChatMessage> messagesToSend) {

        messagesToSend.add(new ChatMessage("SenderName", "Nachrichteninhalt"));

        String serializedMessages = prepareMessagesToSend(messagesToSend);

        sendToServer(serializedMessages);
    }

    private void sendToServer(String serializedMessages) {
        // Hier würde der Code zur Nachrichtenübertragung an den Server stehen
    }

    public void appendToChatArea(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }

    public void shutdown() {
        try {
            writer.println(username + " hat den Chat verlassen.");
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
