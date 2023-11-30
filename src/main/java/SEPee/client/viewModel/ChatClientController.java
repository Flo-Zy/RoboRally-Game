package SEPee.client.viewModel;

import SEPee.client.model.ChatClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class ChatClientController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;
    @FXML
    private Button visibilityButton;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;

    public void init(ChatClient chatClient, Stage stage) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Please enter your username:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            this.username = result.get();
            stage.setTitle("Chat Client - " + username);

            try {
                this.socket = new Socket(chatClient.getServerIp(), chatClient.getServerPort());
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.println(username + " has joined the chat.");

                new Thread(() -> {
                    try {
                        String serverMessage;
                        while ((serverMessage = reader.readLine()) != null) {
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
            visibilityButton.setText("Alle");
            visibilityButton.setOnAction(event -> toggleVisibility());
            stage.setOnCloseRequest(event -> shutdown());

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        } else {
            Platform.exit();
        }
    }


    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            writer.println(username + ": " + message);
            messageField.clear();
        }
    }

    private void toggleVisibility() {
        if (visibilityButton.getText().equals("Alle")) {
            showPlayerListDialog();
        } else {
            // Logik für private Nachrichten
            // Hier kannst du die Logik für private Nachrichten implementieren, wenn nötig
        }
    }
    private void showPlayerListDialog() {
        // Implementiere hier die Logik für die Spielerliste und den Dialog
        // Zeige eine Liste der Benutzer und ermögliche die Auswahl eines Spielers
        // Beispiel: Verwende eine ChoiceDialog
    }

    public void appendToChatArea(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }

    public void shutdown() {
        try {
            if (socket != null && !socket.isClosed()) {
                if (writer != null) {
                    writer.println(username + " has left the chat.");
                    writer.flush();
                    writer.close();
                }
                Thread.sleep(100);
                socket.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
