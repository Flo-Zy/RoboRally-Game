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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Duration;
import javafx.util.Pair;
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
    @Setter
    @Getter
    private static ArrayList<Player> playerListClient;
    public MapController mapController; // wird zB. in loadDizzyHighwayFXML() spezifiziert: mapController = dizzyHighwayController;
    @Getter
    private static ArrayList<Integer> takenStartPoints = new ArrayList<>();
    private List<Integer> takenFigures = new ArrayList<>();

    private ImageView currentSelectedImageView = null;
    private ArrayList<String> playerNames = new ArrayList<>();
    @Setter
    @Getter
    private static ArrayList<Card> clientHand = new ArrayList<>();
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Label avatarNameLabel;
    @FXML
    private VBox DizzyHighwayMap;
    @FXML
    private VBox ExtraCrispyMap;
    @FXML
    private VBox LostBearingsMap;
    @FXML
    private VBox DeathTrapMap;
    @FXML
    private Button muteButton;
    @Getter
    @Setter
    private static int currentPhase;
    @Getter
    private static int startPointX;
    @Getter
    private static int startPointY;
    private GridPane robotSelectionGrid;
    public void init(Client client, Stage stage) {

        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        Font.loadFont(getClass().getResourceAsStream("/CSSFiles/Digital-Bold.tff"), 14);
        dialog.setTitle("Welcome to RoboRally");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/init.css").toExternalForm());
        dialog.getDialogPane().setGraphic(null);
        dialog.getDialogPane().getStyleClass().add("dialog-background");

        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Username");
        usernameTextField.getStyleClass().add("username-text-field");

        final int[] selectedRobotNumber = {0};
        String[] robotNames = {"Gorbo", "LixLix", "Hasi", "Finki", "Flori", "Stinowski"};

        ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
        Node connectButton = dialog.getDialogPane().lookupButton(okButtonType);
        connectButton.getStyleClass().add("connect-button");
        if (connectButton instanceof Button) {
            ((Button) connectButton).setDisable(true);
        }

        updateOkButtonState(dialog, usernameTextField, selectedRobotNumber, okButtonType);

        //GridPane.setHalignment(connectButton, HPos.CENTER);
        //GridPane.setValignment(connectButton, VPos.CENTER);

        stage.getScene().getRoot().getStyleClass().add("dialog-background");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.add(connectButton, 1, 3, 1, 1);
        GridPane.setHalignment(connectButton, HPos.CENTER);
        //GridPane.setHalignment(connectButton, HPos.CENTER);
        //GridPane.setValignment(connectButton, VPos.CENTER);
        //GridPane roboRally = new GridPane();
        //roboRally.setHgap(37);
        //roboRally.setVgap(37);
        //roboRally.getStyleClass().add("robo-rally-grid");
        Image RoboRallyName = new Image("boardElementsPNGs/Custom/Backgrounds/RoboRallyName.png");
        ImageView introImage = new ImageView(RoboRallyName);
        introImage.getStyleClass().add("intro-image");
        grid.add(introImage, 0,0,2,1);
        GridPane.setHalignment(introImage, HPos.CENTER);
        GridPane.setValignment(introImage, VPos.CENTER);
        //introImage(Pos.CENTER);
        grid.add(usernameTextField,0,1,3,1);
        GridPane.setHalignment(usernameTextField, HPos.CENTER);
        GridPane.setValignment(usernameTextField, VPos.CENTER);
        grid.setMinHeight(300);
        grid.setAlignment(Pos.CENTER);
        introImage.getStyleClass().add("intro-image");
        introImage.setFitHeight(118);  // Setzt die maximale Höhe
        introImage.setFitWidth(645);  // Setzt die maximale Breite
        introImage.setPreserveRatio(true);
        robotSelectionGrid = new GridPane();
        robotSelectionGrid.setHgap(10);
        robotSelectionGrid.setVgap(10);
        robotSelectionGrid.getStyleClass().add("robot-selection-grid");
        dialog.getDialogPane().setContent(grid);

        for (int i = 1; i <= 6; i++) {
            Image image = new Image("boardElementsPNGs/Custom/Avatars/Figure" + i + ".png");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);

            Label nameLabel = new Label(robotNames[i - 1]);
            nameLabel.getStyleClass().add("grid-label-robonames");
            nameLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/CSSFiles/Digital-Bold.ttf"), 36));

            GridPane.setHalignment(imageView, HPos.CENTER);
            GridPane.setValignment(imageView, VPos.CENTER);
            GridPane.setHalignment(nameLabel, HPos.CENTER);
            GridPane.setValignment(nameLabel, VPos.CENTER);

            robotSelectionGrid.add(imageView, i - 1, 0);
            robotSelectionGrid.add(nameLabel, i - 1, 1);

            if (client.getTakenFigures().contains(i)) {
                imageView.setDisable(true);
                imageView.setOpacity(0.1);
            } else {
                final int robotNumber = i;
                imageView.setOnMouseClicked(event -> {
                    int newSelectedRobotNumber = 0; // Lokale Variable
                    if (currentSelectedImageView == imageView) {
                        // Deselektieren
                        currentSelectedImageView.setOpacity(1.0);
                        currentSelectedImageView = null;
                    } else {
                        // Auswählen
                        if (currentSelectedImageView != null) {
                            currentSelectedImageView.setOpacity(1.0);
                        }
                        imageView.setOpacity(0.5);
                        newSelectedRobotNumber = robotNumber;
                        currentSelectedImageView = imageView;
                    }
                    avatarImageView.setImage(image);
                    avatarImageView.setVisible(true);
                    avatarNameLabel.setText(robotNames[robotNumber-1]);
                    avatarNameLabel.setStyle("-fx-text-fill: #dde400; " +
                            "-fx-font-size: 40px; " +
                            "-fx-font-family: 'Impact'");
                    //avatarNameLabel.setFont(customFont1);
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(10.0);
                    dropShadow.setOffsetX(3.0);
                    dropShadow.setOffsetY(3.0);
                    dropShadow.setColor(Color.BLACK);

                    avatarNameLabel.setEffect(dropShadow);
                    avatarImageView.setEffect(dropShadow);
                    selectedRobotNumber[0] = newSelectedRobotNumber;
                    System.out.println("Selected Robot Number: " + selectedRobotNumber[0]);
                    updateOkButtonState(dialog, usernameTextField, selectedRobotNumber, okButtonType);
                });
            }
        }

        grid.add(robotSelectionGrid, 0, 2, 2, 1);

        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateOkButtonState(dialog, usernameTextField, selectedRobotNumber, okButtonType);
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(usernameTextField.getText().trim(), selectedRobotNumber[0]);
            }
            return null;
        });

        client.addTakenFiguresChangeListener(new Client.TakenFiguresChangeListener() {
            @Override
            public void onTakenFiguresChanged(ArrayList<Integer> newTakenFigures) {
                Platform.runLater(() -> updateRobotImageViews(newTakenFigures));
            }
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();

        result.ifPresent(usernameRobotPair -> {
            this.name = usernameRobotPair.getKey();
            this.figure = usernameRobotPair.getValue();
            stage.setTitle("Client - " + name);
            // Hier Ihre weitere Initialisierungslogik

            stage.getScene().getRoot().setStyle("-fx-background-image: url('/boardElementsPNGs/Custom/Backgrounds/Background1Edited.png');" +
                    "-fx-background-repeat: repeat;" +
                    "-fx-background-size: cover;");

            Scene scene = stage.getScene();
            scene.getStylesheets().add(getClass().getResource("/CSSFiles/init.css").toExternalForm());
            muteButton = new Button("Mute Sounds");
            muteButton.getStyleClass().add("mute-button");
            muteButton.setOnAction(event -> SoundManager.toggleSoundMute());
            VBox root = (VBox) scene.getRoot();
            root.getChildren().add(muteButton);
            sendButton.setOnAction(event -> sendMessage());
            visibilityButton.setOnAction(event -> toggleVisibility());
        });
    }

    private void updateOkButtonState(Dialog<Pair<String, Integer>> dialog, TextField usernameTextField, int[] selectedRobotNumber, ButtonType okButtonType) {
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);

        if (okButton != null) {
            boolean isUsernameValid = usernameTextField.getText() != null && !usernameTextField.getText().trim().isEmpty();
            boolean isRobotSelected = selectedRobotNumber[0] > 0;
            System.out.println("Updating OK Button State: Username Valid = " + isUsernameValid + ", Robot Selected = " + isRobotSelected);
            okButton.setDisable(!(isUsernameValid && isRobotSelected));
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
        initializePlayerNames();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, playerNames);
        dialog.setTitle("Spieler auswählen");

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/showPlayerListDialog.css").toExternalForm());
        dialog.getDialogPane().setGraphic(null);

        Label headerLabel = new Label("Bitte wählen Sie einen Spieler:");
        headerLabel.setFont(new Font("Arial", 56));
        dialog.getDialogPane().setHeader(headerLabel);
        headerLabel.getStyleClass().add("header-label");

        // "An Alle senden" button
        ButtonType sendToAllButton = new ButtonType("An Alle senden", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().add(sendToAllButton);

        //"Send to All" button vom dialog
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
            int index = playerNames.indexOf(selectedPlayerName) + 1; // Index ist um 1 versetzt, weil clientIds mit 1 anfangen
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
            if (name != null) {
                if (socket != null && !socket.isClosed()) {
                    if (writer != null) {
                        writer.println(name + " has left the chat.");
                        writer.flush();
                        writer.close();
                    }
                    Thread.sleep(100);
                    socket.close();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public int robotSelectionAI(ArrayList<Integer> takenFigures) {
        Random random = new Random();
        while(true){
            int robotNumber = random.nextInt(1, 7);
            // Überprüfen, ob der Roboter bereits genommen wurde
            if (!takenFigures.contains(robotNumber)) {
                return robotNumber;
            } else if(takenFigures.contains(1) && takenFigures.contains(2) && takenFigures.contains(3) && takenFigures.contains(4) &&
                    takenFigures.contains(5) && takenFigures.contains(6)) {
                return -1;
            }
        }
    }

    private void initializePlayerNames() {
        playerNames.clear();
        System.out.println("initializePlayerNames (Client.getPlayerListClient()): " + Client.getPlayerListClient());
        for (Player player : Client.getPlayerListClient()) {
            String playerName = player.getName();
            playerNames.add(playerName);
        }
    }

    public String showSelectMapDialog() {
        String selectedMap = null;

        while (selectedMap == null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(null, Client.getMapList());
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/showSelectMapDialog.css").toExternalForm());
            dialog.setTitle("Map Selection");

            Label headerLabel = new Label("Please select a map:");
            headerLabel.setFont(new Font("Arial", 35));
            dialog.getDialogPane().setHeader(headerLabel);
            headerLabel.getStyleClass().add("header-label");

            // Map selection or choosing "Cancel"
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                selectedMap = result.get();
            }
        }
        return selectedMap;
    }

    public String showSelectRebootDirectionDialog(Stage stage) {
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        String[] selectedDirection = {null};

        // Berechnen der Mindestbreite für die Buttons
        double minWidth = new Text("bottom").getBoundsInLocal().getWidth() + 20; // 20 für etwas zusätzlichen Platz

        // Top Button in der ersten Reihe mittig
        Button topButton = new Button("top");
        topButton.setMinWidth(minWidth);
        topButton.setOnAction(event -> {
            selectedDirection[0] = "top";
            stage.close();
        });
        root.add(topButton, 1, 0, 1, 1);

        // Left Button in der zweiten Reihe links
        Button leftButton = new Button("left");
        leftButton.setMinWidth(minWidth);
        leftButton.setOnAction(event -> {
            selectedDirection[0] = "left";
            stage.close();
        });
        GridPane.setMargin(leftButton, new Insets(0, 0, 0, 20));
        root.add(leftButton, 0, 1, 1, 1);

        // Right Button in der zweiten Reihe rechts
        Button rightButton = new Button("right");
        rightButton.setMinWidth(minWidth);
        rightButton.setOnAction(event -> {
            selectedDirection[0] = "right";
            stage.close();
        });
        GridPane.setMargin(rightButton, new Insets(0, 20, 0, 0));
        root.add(rightButton, 2, 1, 1, 1);

        // Bottom Button in der dritten Reihe mittig
        Button bottomButton = new Button("bottom");
        bottomButton.setMinWidth(minWidth);
        bottomButton.setOnAction(event -> {
            selectedDirection[0] = "bottom";
            stage.close();
        });
        root.add(bottomButton, 1, 2, 1, 1);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/CSSFiles/showSelectRebootDirectionDialog.css");

        stage.setScene(scene);
        stage.setTitle("Reboot direction selection");

        Text text = new Text("Reboot direction selection");
        text.getStyleClass().add("header-label");
        double titleWidth = text.getBoundsInLocal().getWidth();
        stage.setWidth(titleWidth + 40);

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

    public String showSelectDamageDialog(ArrayList<String> availableList){
        String selectedDamage = null;

        while (selectedDamage == null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(null, availableList);
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/showSelectMapDialog.css").toExternalForm());
            dialog.setTitle("Damage Selection");

            Label headerLabel = new Label("Please select your damage:");
            headerLabel.setFont(new Font("Arial", 35));
            dialog.getDialogPane().setHeader(headerLabel);
            headerLabel.getStyleClass().add("header-label");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                selectedDamage = result.get();
            }
        }
        return selectedDamage;
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

                dizzyHighwayController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                dizzyHighwayController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                extraCrispyController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                extraCrispyController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                lostBearingsController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                lostBearingsController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                deathTrapController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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

                deathTrapController.setCheckPointImage("/boardElementsPNGs/CheckpointCounter0.png");

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
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/setStartingPoint.css").toExternalForm());
        dialog.setTitle("Startingpoint Selection");

        Label headerLabel = new Label("Please select a Startingpoint:");
        headerLabel.setFont(new Font("Arial", 56));
        dialog.getDialogPane().setHeader(headerLabel);
        headerLabel.getStyleClass().add("header-label");

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

    public void setCheckPointImage(String imageUrl) {
        mapController.setCheckPointImage(imageUrl);
    }

    public void playUISound(String eventName){
        SoundManager.playUISound(eventName);
    }
    public void playEventSound(String eventName){
        SoundManager.playEventSound(eventName);
    }

    public void playSound(String soundName){
        SoundManager.playSound(soundName);
    }

    private void updateRobotImageViews(ArrayList<Integer> newTakenFigures) {
        for (Node node : robotSelectionGrid.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                int robotNumber = GridPane.getColumnIndex(node);
                if (newTakenFigures.contains(robotNumber + 1)) {
                    imageView.setDisable(true);
                    imageView.setOpacity(0.1);
                } else {
                    imageView.setDisable(false);
                    imageView.setOpacity(1.0);
                }
            }
        }
    }

}