package SEPee.client.viewModel;

import SEPee.client.model.Client;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SetStatus;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SendChat;
import SEPee.server.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;

import static SEPee.client.model.Client.playerListClient;


public class ClientController {
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;
    @FXML
    private Button visibilityButton;

    @FXML
    private Button readyButton;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean ready = false;
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



                //try {
                    //this.socket = new Socket(chatClient.getServerIp(), chatClient.getServerPort());
                    //writer.println( "PRINT CHAT CLIENT" + chatClient);
                    //this.writer = new PrintWriter(socket.getOutputStream(), true);
                    //this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //writer.println(name + " has joined the chat.");

                    //new Thread(() -> {
                        /*try {
                            String serverMessage;
                            while ((serverMessage = reader.readLine()) != null) {
                               appendToChatArea(serverMessage);
                            }
                        } catch (IOException e) {
                            //e.printStackTrace();
                        //}*/
                    //}).start();
                //} catch (IOException e) {
                //    e.printStackTrace();
                //}
                sendButton.setOnAction(event -> sendMessage());
                visibilityButton.setText("Alle");
                visibilityButton.setOnAction(event -> toggleVisibility());
                stage.setOnCloseRequest(event -> shutdown());

                Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
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

            //DIFFERENT RECIPIENTID ???
            int recipientId = -1;

            SendChat sendChatMessage = new SendChat(message, recipientId);
            String serializedSendChat = Serialisierer.serialize(sendChatMessage);
            Client.getWriter().println(serializedSendChat);

            messageField.clear();
        }
    }

    @FXML
    private void sendReady(){
        if(!ready){
            ready = true;
        }else{
            ready = false;
        }
        System.out.println(ready);
        SetStatus setStatus = new SetStatus(getId(),ready);
        String serializedSetStatus = Serialisierer.serialize(setStatus);
        Client.getWriter().println(serializedSetStatus);
    }

    /*private void toggleVisibility() {
        if (visibilityButton.getText().equals("Alle")) {
            showPlayerListDialog();
        } else {
            // Logik für private Nachrichten
            // Hier kannst du die Logik für private Nachrichten implementieren, wenn nötig
        }
    }

    private void showPlayerListDialog() {
        // Erstellen Sie einen Dialog, um die Spielerliste anzuzeigen
        Dialog<String> playerListDialog = new Dialog<>();
        playerListDialog.setTitle("Spielerliste");
        playerListDialog.setHeaderText("Liste der Spieler im Spiel:");

        // Fügen Sie die Spielerliste als Textinhalt hinzu
        TextArea playerListText = new TextArea();
        playerListText.setEditable(false);
        playerListText.setWrapText(true);

        // Fügen Sie die Spielerliste aus der Client-Klasse hinzu
        playerListText.setText(getPlayerListAsString());

        playerListDialog.getDialogPane().setContent(playerListText);

        // Fügen Sie einen "Schließen" -Button zum Dialog hinzu
        ButtonType closeButton = new ButtonType("Schließen", ButtonBar.ButtonData.CANCEL_CLOSE);
        playerListDialog.getDialogPane().getButtonTypes().add(closeButton);

        // Zeigen Sie den Dialog an und warten Sie auf Benutzereingaben
        playerListDialog.showAndWait();
    }

    private String getPlayerListAsString() {
        StringBuilder playerListString = new StringBuilder();
        for (Player player : Client.playerListClient) {
            playerListString.append("Name: ").append(player.getName()).append("\n");
        }
        return playerListString.toString();
    }*/

    private void toggleVisibility() {
        if (visibilityButton.getText().equals("Alle")) {
            showPlayerListDialog();
        } else {
            // Logik für private Nachrichten
            // Hier kannst du die Logik für private Nachrichten implementieren, wenn nötig
        }
    }

    private void showPlayerListDialog() {
        // Erstellen Sie einen ChoiceDialog mit der Liste der Spieler
        ChoiceDialog<Player> dialog = new ChoiceDialog<>(null, playerListClient);
        dialog.setTitle("Spieler auswählen");
        dialog.setHeaderText("Bitte wählen Sie einen Spieler:");

        // Benutzer auswählen oder "Abbrechen" wählen
        Optional<Player> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Spieler ausgewählt, die Recipient-ID auf die ausgewählte Spieler-ID setzen
            Player selectedPlayer = result.get();
            int recipientId = selectedPlayer.getId();
            visibilityButton.setText("Privat");

            // Hier kannst du die Logik für private Nachrichten implementieren
            // Verwende die recipientId, um die private Nachricht zu senden
        } else {
            // "Abbrechen" wurde ausgewählt, die Recipient-ID auf -1 setzen
            int recipientId = -1;
            visibilityButton.setText("Alle");

            // Hier kannst du die Logik für Nachrichten an alle implementieren
            // Verwende die recipientId, um die Nachricht an alle zu senden
        }
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