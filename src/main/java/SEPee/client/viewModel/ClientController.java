package SEPee.client.viewModel;

import SEPee.client.model.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;



public class ClientController {
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
    private String name;
    private int figure;
    private int id;

    public void init(Client chatClient, Stage stage) {
        boolean validUsername = false;

        while (!validUsername) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Username");
            dialog.setHeaderText("Please enter your username:");
            dialog.setContentText("Username:");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !result.get().trim().isEmpty()) {
                this.name = result.get().trim();
                stage.setTitle("Client - " + name);
                validUsername = true;

                figure = showRobotSelectionDialog(stage);
                setFigure(figure);



                /*try {
                    //this.socket = new Socket(chatClient.getServerIp(), chatClient.getServerPort());
                    //writer.println( "PRINT CHAT CLIENT" + chatClient);
                    //this.writer = new PrintWriter(socket.getOutputStream(), true);
                    //this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Empfange serialisierten HelloClient-String vom Server
                    String serializedHelloClient = reader.readLine();
                    HelloClient deserializedHelloClient = Deserialisierer.deserialize(serializedHelloClient, HelloClient.class);
                    String versionProtocol = deserializedHelloClient.getMessageBody().getProtocol();

                    writer.println(name + " has joined the chat.");

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

                Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));*/
            } else {
                //falls Username empty
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Username cannot be empty. Please enter a valid username.");
                alert.showAndWait();

                Platform.exit();
            }
        }
    }


    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            writer.println(name + ": " + message);
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
                    writer.println(name + " has left the chat.");
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

    private int showRobotSelectionDialog(Stage stage) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Robot Selection");
        dialog.setHeaderText("Please select a robot:");

        // Create buttons for each robot
        ButtonType button1 = new ButtonType("Robot 1", ButtonBar.ButtonData.OK_DONE);
        ButtonType button2 = new ButtonType("Robot 2", ButtonBar.ButtonData.OK_DONE);
        ButtonType button3 = new ButtonType("Robot 3", ButtonBar.ButtonData.OK_DONE);
        ButtonType button4 = new ButtonType("Robot 4", ButtonBar.ButtonData.OK_DONE);
        ButtonType button5 = new ButtonType("Robot 5", ButtonBar.ButtonData.OK_DONE);
        ButtonType button6 = new ButtonType("Robot 6", ButtonBar.ButtonData.OK_DONE);

        // Create a map to associate button types with integer values
        HashMap<ButtonType, Integer> buttonMap = new HashMap<>();
        buttonMap.put(button1, 1);
        buttonMap.put(button2, 2);
        buttonMap.put(button3, 3);
        buttonMap.put(button4, 4);
        buttonMap.put(button5, 5);
        buttonMap.put(button6, 6);

        // Add buttons to the dialog
        dialog.getDialogPane().getButtonTypes().setAll(button1, button2, button3, button4, button5, button6);

        // Show the dialog and wait for user input
        dialog.initOwner(stage);
        Optional<Integer> result = dialog.showAndWait();

        // Process user input and return the selected robot (index starting from 1)
        if (result.isPresent()) {
            return buttonMap.getOrDefault(result.get(), 0);
        }

        return 0; // Default value if no selection or unexpected button is pressed
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}