package SEPee.client.viewModel;

import SEPee.client.model.Client;
import SEPee.client.model.ClientAI;
import SEPee.client.viewModel.MapController.*;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
//Später auslagern
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.progCard.*;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    @Getter
    private static ArrayList<Integer> takenStartPoints = new ArrayList<>();
    private ArrayList<String> playerNames = new ArrayList<>();
    @Setter
    @Getter
    private static ArrayList<Card> clientHand = new ArrayList<>();
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private VBox DizzyHighwayMap;
    @FXML
    private VBox ExtraCrispyMap;
    @FXML
    private VBox LostBearingsMap;
    @FXML
    private VBox DeathTrapMap;
    @Getter
    @Setter
    private static int currentPhase;
    @Getter
    private static int startPointX;
    @Getter
    private static int startPointY;



    public void init(Client Client, Stage stage) {
        boolean validUsername = false;

        while (!validUsername) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Username");
            dialog.setHeaderText("Please enter your username:");
            dialog.setContentText("Username:");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/init.css").toExternalForm());
            dialog.getDialogPane().setGraphic(null);
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
                // If Username is empty
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Username cannot be empty. Please enter a valid username.");
                alert.showAndWait();

                Platform.exit();
            }
        }
    }

    public void initAI(ClientAI clientAI, Stage stage) {
        figure = robotSelectionAI(Client.getTakenFigures());
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

    public void sendReadyAI() {
        ready = true;
        SetStatus setStatus = new SetStatus(ready);
        String serializedSetStatus = Serialisierer.serialize(setStatus);
        ClientAI.getWriter().println(serializedSetStatus);

        //Damit ClientHandler vergleicht, wie viele Spieler ready sind in der MapSelected case
        MapSelected mapSelected1 = new MapSelected("");
        String serializedMapSelected1 = Serialisierer.serialize(mapSelected1);
        ClientAI.getWriter().println(serializedMapSelected1);
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

    String[] robotNames = {"Gorbo", "LixLix", "Hasi", "Finki", "Flori", "Stinowski"};
    private int showRobotSelectionDialog(Stage stage, ArrayList<Integer> takenFigures) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/showRobotSelectionDialog.css").toExternalForm());

        dialog.setTitle("Robot Selection");

        // Custom Header
        Label headerLabel = new Label("Please select a robot:");
        //headerLabel.getStyleClass().add("custom-header-label");
        //headerLabel.setMaxWidth(Double.MAX_VALUE); // Ensures label can expand
        //headerLabel.setAlignment(Pos.CENTER); // Center alignment
        headerLabel.setFont(new Font("Arial", 56));
        // Custom styles
        //StackPane headerPane = new StackPane(headerLabel); // Wraps the label in a StackPane for better alignment
        //headerPane.getStyleClass().add("custom-header-panel"); // Transparent background
        dialog.getDialogPane().setHeader(headerLabel); // Set the custom header
        headerLabel.getStyleClass().add("header-label");

        // GridPane für die Anordnung der Bilder und Namen
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        for (int i = 1; i <= 6; i++) {
            Image image = new Image("boardElementsPNGs/Custom/Avatars/Avatar" + i + ".png");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);

            //Label nameLabel = new Label("Robot " + i);
            Label nameLabel = new Label(robotNames[i-1]);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.getStyleClass().add("grid-label");


            // Hinzufügen von ImageView und Label zum GridPane
            grid.add(imageView, i - 1, 0);
            grid.add(nameLabel, i - 1, 1);

            // Überprüfen, ob der Roboter bereits genommen wurde
            if (takenFigures.contains(i)) {
                imageView.setDisable(true); // Deaktivieren des ImageView
                imageView.setOpacity(0.1); // Reduzierung der Transparenz
            } else {
                // Event Handler für Klicks auf das ImageView
                final int robotNumber = i;
                imageView.setOnMouseClicked(event -> {
                    dialog.setResult(robotNumber);
                    dialog.close();
                });
            }
        }

        // Hinzufügen des GridPane zum Dialog
        dialog.getDialogPane().setContent(grid);

        // Anzeigen des Dialogs und Warten auf das Ergebnis
        Optional<Integer> result = dialog.showAndWait();
        return result.orElse(0); // Rückgabe der ausgewählten Roboter-Nummer oder 0
    }

    public int robotSelectionAI(ArrayList<Integer> takenFigures) {
        Random random = new Random();
        while(true){
            int robotNumber = random.nextInt(1, 7);
            // Überprüfen, ob der Roboter bereits genommen wurde
            if (!takenFigures.contains(robotNumber)) {
                return robotNumber;
            } else if(takenFigures.contains(1) && takenFigures.contains(2) && takenFigures.contains(3) && takenFigures.contains(4) && takenFigures.contains(5) && takenFigures.contains(6)) {
                return -1;
            }
        }
    }

    private void initializePlayerNames() {
        playerNames.clear(); // Clear the existing names
        for (Player player : Client.getPlayerListClient()) {
            String playerName = player.getName();
            playerNames.add(playerName);
        }
    }

    public String showSelectMapDialog() {

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


    public String showSelectRebootDirectionDialog(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        String[] directionList = {"top", "right", "bottom", "left"};
        String[] selectedDirection = {null};

        for (String direction : directionList) {
            Button directionButton = new Button(direction);
            directionButton.setOnAction(event -> {
                selectedDirection[0] = direction;
                stage.close();
            });
            root.getChildren().add(directionButton);
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Reboot direction selection");
        // stage.show();

        Duration duration = Duration.seconds(10);
        Timeline timeline = new Timeline(new KeyFrame(duration, event -> {
            if (stage.isShowing()) {
                stage.close();
                selectedDirection[0] = "top";
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();

        stage.setOnHiding(event -> timeline.stop());

        stage.showAndWait();

        return selectedDirection[0];
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

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadDizzyHighwayFXMLAI(ClientAI clientAI, Stage primaryStage){
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                Node dizzyHighway = loader.load();

                // Get  controller
                DizzyHighwayController dizzyHighwayController = loader.getController();

                mapController = dizzyHighwayController;

                dizzyHighwayController.initAI(clientAI, primaryStage);
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

    public void loadExtraCrispyFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/ExtraCrispy.fxml"));
                Node extraCrispy = loader.load();

                // Get  controller
                ExtraCrispyController extraCrispyController = loader.getController();

                mapController = extraCrispyController;

                extraCrispyController.init(client, primaryStage);
                extraCrispyController.setRootVBox(ExtraCrispyMap);

                // set loaded FXML to VBox
                ExtraCrispyMap.getChildren().setAll(extraCrispy);
                ExtraCrispyMap.setVisible(true);
                ExtraCrispyMap.setManaged(true);

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadExtraCrispyFXMLAI(ClientAI clientAI, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/ExtraCrispy.fxml"));
                Node extraCrispy = loader.load();

                // Get  controller
                ExtraCrispyController extraCrispyController = loader.getController();

                mapController = extraCrispyController;

                extraCrispyController.initAI(clientAI, primaryStage);
                extraCrispyController.setRootVBox(ExtraCrispyMap);

                // set loaded FXML to VBox
                ExtraCrispyMap.getChildren().setAll(extraCrispy);
                ExtraCrispyMap.setVisible(true);
                ExtraCrispyMap.setManaged(true);

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadLostBearingsFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/LostBearings.fxml"));
                Node lostBearings = loader.load();

                // Get  controller
                LostBearingsController lostBearingsController = loader.getController();

                mapController = lostBearingsController;

                lostBearingsController.init(client, primaryStage);
                lostBearingsController.setRootVBox(LostBearingsMap);

                // set loaded FXML to VBox
                LostBearingsMap.getChildren().setAll(lostBearings);
                LostBearingsMap.setVisible(true);
                LostBearingsMap.setManaged(true);

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadLostBearingsFXMLAI(ClientAI clientAI, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/LostBearings.fxml"));
                Node lostBearings = loader.load();

                // Get  controller
                LostBearingsController lostBearingsController = loader.getController();

                mapController = lostBearingsController;

                lostBearingsController.initAI(clientAI, primaryStage);
                lostBearingsController.setRootVBox(LostBearingsMap);

                // set loaded FXML to VBox
                LostBearingsMap.getChildren().setAll(lostBearings);
                LostBearingsMap.setVisible(true);
                LostBearingsMap.setManaged(true);

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadDeathTrapFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DeathTrap.fxml"));
                Node deathTrap = loader.load();

                // Get  controller
                DeathTrapController deathTrapController = loader.getController();

                mapController = deathTrapController;

                deathTrapController.init(client, primaryStage);
                deathTrapController.setRootVBox(DeathTrapMap);

                // set loaded FXML to VBox
                DeathTrapMap.getChildren().setAll(deathTrap);
                DeathTrapMap.setVisible(true);
                DeathTrapMap.setManaged(true);

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadDeathTrapFXMLAI(ClientAI clientAI, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DeathTrap.fxml"));
                Node deathTrap = loader.load();

                // Get  controller
                DeathTrapController deathTrapController = loader.getController();

                mapController = deathTrapController;

                deathTrapController.initAI(clientAI, primaryStage);
                deathTrapController.setRootVBox(DeathTrapMap);

                // set loaded FXML to VBox
                DeathTrapMap.getChildren().setAll(deathTrap);
                DeathTrapMap.setVisible(true);
                DeathTrapMap.setManaged(true);

                //Hide Bereit nicht bereit button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addTakenStartingPoints(int x, int y){
        int combinedValue = x * 10 + y;
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

    public void addTakenStartingPointsDeathTrap(int x, int y){
        int combinedValue = x * 10 + y;
        switch(combinedValue){
            case 111:
                takenStartPoints.add(1);
                break;
            case 123:
                takenStartPoints.add(2);
                break;
            case 114:
                takenStartPoints.add(3);
                break;
            case 115:
                takenStartPoints.add(4);
                break;
            case 126:
                takenStartPoints.add(5);
                break;
            case 118:
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

            if(Client.getSelectedMap1().equals("Death Trap")) {
                setStartingPointXYDeathTrap(selectedStartingpoint); // gespiegeltes Startboard
            } else {
                setStartingPointXY(selectedStartingpoint);
            }
        }
    }

    public void setStartingPointAI() {
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

        if(ClientAI.getSelectedMap1().equals("DeathTrap")) {
            setStartingPointXYDeathTrap(selectedStartingPoint); // gespiegeltes Startboard
        } else {
            setStartingPointXY(selectedStartingPoint);
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

    public void setStartingPointXYDeathTrap(int StartingPointNumber){
        switch(StartingPointNumber){
            case 1:
                startPointX = 11;
                startPointY = 1;
                break;
            case 2:
                startPointX = 12;
                startPointY = 3;
                break;
            case 3:
                startPointX = 11;
                startPointY = 4;
                break;
            case 4:
                startPointX = 11;
                startPointY = 5;
                break;
            case 5:
                startPointX = 12;
                startPointY = 6;
                break;
            case 6:
                startPointX = 11;
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

    public void initRegisterAI(){
        mapController.initializeRegisterAI(id, clientHand);
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