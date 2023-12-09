package SEPee.client.viewModel;

import SEPee.client.model.Client;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SendChat;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


import SEPee.client.model.Client;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.MapSelected;
import SEPee.serialisierung.messageType.SetStatus;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SendChat;
import SEPee.server.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static SEPee.client.model.Client.mapList;
import static SEPee.client.model.Client.playerListClient;


public class DizzyHighwayController {

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

    private ArrayList<String> playerNames = new ArrayList<>();   @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;


    @FXML
    private VBox rootVBox;
    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView field00;
    @FXML
    private ImageView field01;
    @FXML
    private ImageView field02;
    @FXML
    private ImageView field03;
    @FXML
    private ImageView field04;
    @FXML
    private ImageView field05;
    @FXML
    private ImageView field06;
    @FXML
    private ImageView field07;
    @FXML
    private ImageView field08;
    @FXML
    private ImageView field09;
    private Stage stage;

    public void init(Client Client, Stage stage) {
        this.stage = stage;

        sendButton.setOnAction(event -> sendMessage());
        visibilityButton.setText("Alle");
        visibilityButton.setOnAction(event -> toggleVisibility());
        //stage.setOnCloseRequest(event -> shutdown());


    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            SendChat sendChatMessage = new SendChat(message, getSelectedRecipientId());
            System.out.println("send message selected id " + getSelectedRecipientId());

            String serializedSendChat = Serialisierer.serialize(sendChatMessage);
            Client.getWriter().println(serializedSendChat);

            messageField.clear();
        }
    }

    private int selectedRecipientId  = -1; // Initialize with a default value
    // Method to get the selected recipient ID
    private int getSelectedRecipientId() {
        if (visibilityButton.getText().equals("Privat")) {
            System.out.println("ID selected " + selectedRecipientId);
            return selectedRecipientId; // Return the ID of the selected player for private messages
        } else {
            return -1; // If it's a message to all, return -1
        }
    }
    @FXML
    private void toggleVisibility() {
        if (visibilityButton.getText().equals("Alle")) {
            showPlayerListDialog();
        } else {
            showPlayerListDialog();
        }
    }
    private void showPlayerListDialog() {

        // Initialize the playerNames array with player names from playerListClient
        initializePlayerNames();

        // Erstellen Sie einen ChoiceDialog mit der Liste der Spieler
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, playerNames);
        dialog.setTitle("Spieler auswählen");
        dialog.setHeaderText("Bitte wählen Sie einen Spieler:");

        // Create a "Send to All" button
        ButtonType sendToAllButton = new ButtonType("An Alle senden", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().add(sendToAllButton);

        //"Send to All" button from the dialog
        Node sendToAllNode = dialog.getDialogPane().lookupButton(sendToAllButton);
        ((Button) sendToAllNode).setOnAction(event -> {
            // Handle the action when "Send to All" is clicked
            visibilityButton.setText("Alle");
            selectedRecipientId = -1;
            dialog.close();
        });


        // Benutzer auswählen oder "Abbrechen" wählen
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String selectedPlayerName = result.get();
            // Id über Namen finden
            int index = playerNames.indexOf(selectedPlayerName) + 1; // Index ist um 1 versetzt weil clientIds mit 1 anfangen
            if (index != -1) {
                // Update selectedRecipientId based on the index
                selectedRecipientId = index;
                visibilityButton.setText("Privat");
            }
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
        System.exit(0);
    }
    private void initializePlayerNames() {
        playerNames.clear(); // Clear the existing names
        for (Player player : playerListClient) {
            String playerName = player.getName(); // Assuming the Player class has a method getName() that returns the name
            playerNames.add(playerName);
        }
    }
}
