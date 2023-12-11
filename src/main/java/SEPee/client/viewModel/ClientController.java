package SEPee.client.viewModel;

import SEPee.client.model.Client;
import SEPee.client.viewModel.MapController.DizzyHighwayController;
import SEPee.client.viewModel.MapController.MapController;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.MapSelected;
import SEPee.serialisierung.messageType.SetStatus;
import SEPee.serialisierung.messageType.SendChat;
//Später auslagern
import SEPee.server.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientController {
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
    public MapController mapController;
    private ArrayList<Integer> takenStartPoints = new ArrayList<>();


    private ArrayList<String> playerNames = new ArrayList<>();
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;

    @FXML
    private VBox DizzyHighwayMap;

    @Getter
    @Setter
    private int currentPhase;

    @Getter
    private int startPointX;
    @Getter
    private int startPointY;



    public void init(Client Client, Stage stage) {

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

                figure = showRobotSelectionDialog(stage, Client.getTakenFigures());
                setFigure(figure);

                sendButton.setOnAction(event -> sendMessage());
                visibilityButton.setText("Alle");
                visibilityButton.setOnAction(event -> toggleVisibility());
                //stage.setOnCloseRequest(event -> shutdown());

                //Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
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
            SendChat sendChatMessage = new SendChat(message, getSelectedRecipientId());
            System.out.println("send message selected id " + getSelectedRecipientId());

            String serializedSendChat = Serialisierer.serialize(sendChatMessage);
            Client.getWriter().println(serializedSendChat);

            messageField.clear();
        }
    }

    private int giveRecipientIdToSendMessage() {
        return getSelectedRecipientId();

    }

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
    private void sendReady() {
        if (!ready) {
            ready = true;
            readyButton.setText("NICHT BEREIT");
        } else {
            ready = false;
            readyButton.setText("BEREIT");
        }
        System.out.println(ready);
        SetStatus setStatus = new SetStatus(ready);
        String serializedSetStatus = Serialisierer.serialize(setStatus);
        Client.getWriter().println(serializedSetStatus);

        //Damit ClientHandler vergleicht, wie viele Spieler ready sind in der MapSelected case
        MapSelected mapSelected = new MapSelected("");
        String serializedMapSelected = Serialisierer.serialize(mapSelected);
        Client.getWriter().println(serializedMapSelected);
    }

    private int selectedRecipientId = -1; // Initialize with a default value

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

    private int showRobotSelectionDialog(Stage stage, ArrayList<Integer> takenFigures) {
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

        //show the dialog and wait for user input
        dialog.initOwner(stage);

        //disable previously selected buttons
        for (Integer takenFigure : takenFigures) {
            ButtonType buttonType = buttonMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == takenFigure)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (buttonType != null) {
                Node buttonNode = dialog.getDialogPane().lookupButton(buttonType);
                buttonNode.setDisable(true);
            }
        }

        //show dialog, wait for input
        Optional<Integer> result = dialog.showAndWait();

        // Process user input and return the selected robot (index starting from 1)
        if (result.isPresent()) {
            int selectedRobotIndex = buttonMap.get(result.get());

            // Get the selected button
            ButtonType selectedButtonType = buttonMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == selectedRobotIndex)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (selectedButtonType != null) {
                Button selectedButton = (Button) dialog.getDialogPane().lookupButton(selectedButtonType);
                selectedButton.setDisable(true); // Disable the selected button
            }

            return selectedRobotIndex;
        }
        return 0; // Default value if no selection or unexpected button is pressed
    }

    private void initializePlayerNames() {
        playerNames.clear(); // Clear the existing names
        for (Player player : Client.getPlayerListClient()) {
            String playerName = player.getName(); // Assuming the Player class has a method getName() that returns the name
            playerNames.add(playerName);
        }
    }

    public String showSelectMapDialog() {

        //ChoiceDialog mit der Liste der Maps
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, Client.getMapList());
        dialog.setTitle("Map auswählen");
        dialog.setHeaderText("Bitte wählen Sie eine Map:");

        //Map auswählen oder "Abbrechen" wählen
        Optional<String> result = dialog.showAndWait();

        String selectedMap = null;
        if (result.isPresent()) {
            selectedMap = result.get();
        }
        return selectedMap;
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

    public void loadDizzyHighwayFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                Node dizzyHighway = loader.load();

                // Get  controller
                DizzyHighwayController dizzyHighwayController = loader.getController();

                mapController = dizzyHighwayController;

                dizzyHighwayController.init(client, primaryStage);

                dizzyHighwayController.setRootVBox(DizzyHighwayMap);

                // set loaded FXML to VBox
                DizzyHighwayMap.getChildren().setAll(dizzyHighway);

                DizzyHighwayMap.setVisible(true);
                DizzyHighwayMap.setManaged(true);

                //Hidde Bereit nicht bereit  button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void addTakenStartingPoints(int x, int y){
        int combinedValue = x*10 + y;
        switch(combinedValue){
            case 11:
                takenStartPoints.add(1);
                break;
            case 3:
                takenStartPoints.add(2);
                break;
            case 14:
                takenStartPoints.add(3);
                break;
            case 15:
                takenStartPoints.add(4);
                break;
            case 6:
                takenStartPoints.add(5);
                break;
            case 18:
                takenStartPoints.add(6);
                break;
        }
    }

    public void setStartingPoint() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Startingpoint Selection");
        dialog.setHeaderText("Please select a Startingpoint:");

        // Create buttons for each robot
        ButtonType button1 = new ButtonType("Start 1", ButtonBar.ButtonData.OK_DONE);
        ButtonType button2 = new ButtonType("Start 2", ButtonBar.ButtonData.OK_DONE);
        ButtonType button3 = new ButtonType("Start 3", ButtonBar.ButtonData.OK_DONE);
        ButtonType button4 = new ButtonType("Start 4", ButtonBar.ButtonData.OK_DONE);
        ButtonType button5 = new ButtonType("Start 5", ButtonBar.ButtonData.OK_DONE);
        ButtonType button6 = new ButtonType("Start 6", ButtonBar.ButtonData.OK_DONE);

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

        //show the dialog and wait for user input
        //dialog.initOwner(stage);

        //disable previously selected buttons
        for (Integer takenStartingPoint : takenStartPoints) {
            ButtonType buttonType = buttonMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == takenStartingPoint)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (buttonType != null) {
                Node buttonNode = dialog.getDialogPane().lookupButton(buttonType);
                buttonNode.setDisable(true);
            }
        }
        //show dialog, wait for input
        Optional<Integer> result = dialog.showAndWait();
        // Process user input and return the selected robot (index starting from 1)
        if (result.isPresent()) {
            int selectedStartingpoint = buttonMap.get(result.get());
            // Get the selected button
            ButtonType selectedButtonType = buttonMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == selectedStartingpoint)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (selectedButtonType != null) {
                Button selectedButton = (Button) dialog.getDialogPane().lookupButton(selectedButtonType);
                selectedButton.setDisable(true); // Disable the selected button
            }
            setStartingPointXY(selectedStartingpoint);
        }
    }

    public void setStartingPointXY(int StartingPointNumber){
        switch(StartingPointNumber){
            case 1:
                startPointX = 1;
                startPointY = 1;
                break;
            case 2:
                startPointX = 0;
                startPointY = 3;
                break;
            case 3:
                startPointX = 1;
                startPointY = 4;
                break;
            case 4:
                startPointX = 1;
                startPointY = 5;
                break;
            case 5:
                startPointX = 0;
                startPointY = 6;
                break;
            case 6:
                startPointX = 1;
                startPointY = 8;
                break;
        }
    }



}