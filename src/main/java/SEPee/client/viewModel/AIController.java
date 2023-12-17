package SEPee.client.viewModel;

import SEPee.client.model.AI;
import SEPee.client.model.Client;
import SEPee.client.viewModel.MapController.DizzyHighwayController;
import SEPee.client.viewModel.MapController.MapController;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.MapSelected;
import SEPee.serialisierung.messageType.SendChat;
import SEPee.serialisierung.messageType.SetStatus;
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class AIController {
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
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private int figure;
    @Setter
    @Getter
    private int id;
    public MapController mapController; // wird zB. in loadDizzyHighwayFXML() spezifiziert: mapController = dizzyHighwayController;
    private ArrayList<Integer> takenStartPoints = new ArrayList<>();
    private ArrayList<String> playerNames = new ArrayList<>();
    @Setter
    @Getter
    private ArrayList<Card> clientHand = new ArrayList<>();
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


    public void init(AI ai, Stage stage) {
        // setze namen der AI
        this.name = "AI";

        // setze Roboter der AI
        figure = showRobotSelectionDialogAI(Client.getTakenFigures());
        this.setFigure(figure);

        /*
        sendButton.setOnAction(event -> sendMessage());
        visibilityButton.setText("Alle");
        visibilityButton.setOnAction(event -> toggleVisibility());
        */
    }
    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            SendChat sendChatMessage = new SendChat(message, getSelectedRecipientId());
            System.out.println("send message selected id " + getSelectedRecipientId());

            String serializedSendChat = Serialisierer.serialize(sendChatMessage);
            AI.getWriter().println(serializedSendChat);

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
        AI.getWriter().println(serializedSetStatus);

        //Damit ClientHandler vergleicht, wie viele Spieler ready sind in der MapSelected case
        MapSelected mapSelected = new MapSelected("");
        String serializedMapSelected = Serialisierer.serialize(mapSelected);
        AI.getWriter().println(serializedMapSelected);
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

    private int showRobotSelectionDialogAI(ArrayList<Integer> takenFigures) {
        ArrayList<Integer> availableRobots = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            if (!takenFigures.contains(i)) {
                availableRobots.add(i);
            }
        }

        if (availableRobots.isEmpty()) {
            // Keine verfügbaren Roboter mehr
            return 0;
        }

        // Zufällige Auswahl eines verfügbaren Roboters für die KI
        Random random = new Random();
        return availableRobots.get(random.nextInt(availableRobots.size()));
    }

    private void initializePlayerNames() {
        playerNames.clear(); // Clear the existing names
        for (Player player : Client.getPlayerListClient()) {
            String playerName = player.getName();
            playerNames.add(playerName);
        }
    }

    // Dialog nicht notwendig, da AI automatisch DizzyHighway auswählt
    /*
    public String showSelectMapDialog() {

        String selectedMap = null;


        String selectedMap = null;

        while (selectedMap == null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(null, Client.getMapList());
            dialog.setTitle("Map auswählen");
            dialog.setHeaderText("Bitte wählen Sie eine Map:");

            // Map selection or choosing "Cancel"
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                selectedMap = result.get();
            }
        }

        return selectedMap;
    }

     */

    public void loadDizzyHighwayFXMLAI(AI ai, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                Node dizzyHighway = loader.load();

                // Get  controller
                DizzyHighwayController dizzyHighwayController = loader.getController();

                mapController = dizzyHighwayController;

                dizzyHighwayController.init(ai, primaryStage);
                dizzyHighwayController.setRootVBox(DizzyHighwayMap);

                // set loaded FXML to VBox
                DizzyHighwayMap.getChildren().setAll(dizzyHighway);
                DizzyHighwayMap.setVisible(true);
                DizzyHighwayMap.setManaged(true);

                //Hide Bereit nicht bereit button
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
        ArrayList<Integer> availableStartingPoints = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            if (!takenStartPoints.contains(i)) {
                availableStartingPoints.add(i);
            }
        }

        if (availableStartingPoints.isEmpty()) {
            // Kein verfügbarer StartingPoint mehr
            return;
        }

        // Zufällige Auswahl eines verfügbaren StartingPoints für die AI
        Random random = new Random();
        int selectedStartingPoint = availableStartingPoints.get(random.nextInt(availableStartingPoints.size()));

        // Process the selected StartingPoint
        setStartingPointXY(selectedStartingPoint);
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

    public void putAvatarDown(Player player, int x, int y){
        mapController.avatarAppear(player, x, y);

    }

    public void initDrawPile(){
        mapController.initializeDrawPile(id, clientHand); // int, ArrayList<String>
    }

    public void initRegister(){
        mapController.initializeRegister(id, clientHand);
    }

    public void setRegisterVisibilityFalse(){
        mapController.setRegisterVisibilityFalse();
    }

    public void fillEmptyRegister(ArrayList<Card> nextCards){
        mapController.fillEmptyRegister(nextCards);
    }

    public void movementPlayed(int clientIdToMove, int newX, int newY) {
        mapController.movementPlayed(clientIdToMove, newX, newY);
    }

    public void playerTurn(int clientIdToTurn, String rotation){
        mapController.playerTurn(clientIdToTurn, rotation);
    }
}