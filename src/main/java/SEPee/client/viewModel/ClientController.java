package SEPee.client.viewModel;

import SEPee.client.ClientLogger;
import SEPee.client.model.Client;
import SEPee.client.model.ClientAI;
import SEPee.client.viewModel.MapController.*;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.*;
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import javafx.animation.KeyFrame;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * controls all the parts of the GUI that are not in the map controllers
 * @author Maximilian, Florian, Hasan, Felix, Franzi
 */
public class ClientController {
    @FXML
    private Button sendButton;
    @Setter
    @FXML
    private HBox totalHand;
    @FXML
    public HBox totalRegister;
    private Map<Integer, List<Card>> clientHandMap;
    @FXML
    private Button visibilityButton;
    @FXML
    private Button readyButton;
    @FXML
    public ImageView checkPointImageView;
    @FXML
    public ImageView countDownImageView;
    @FXML
    private Slider uiSoundSlider;
    @FXML
    private Slider eventSoundSlider;
    @FXML
    private Slider generalSoundSlider;
    @FXML
    private Slider masterVolumeSlider;
    @Getter
    @FXML
    private ImageView endGIF;
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
    public MapController mapController;
    @Getter
    private static ArrayList<Integer> takenStartPoints = new ArrayList<>();
    private List<Integer> takenFigures = new ArrayList<>();
    private ImageView currentSelectedImageView = null;
    private ArrayList<String> playerNames = new ArrayList<>();
    @Setter
    @Getter
    private static ArrayList<Card> clientHand = new ArrayList<>();
    private static int confirmedClients = 0;
    private static Button connectButton;
    @Getter
    @Setter
    private ArrayList<String> handAi = new ArrayList<>();
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
    @Getter
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
    private ArrayList<Integer> newTakenFigures;
    private ArrayList<Zahlen> zahlen = new ArrayList<>();
    private AtomicInteger counter1 = new AtomicInteger(0);
    private Map<Integer, Integer> indexToCounterMap;
    private int selectedRecipientId = -1;

    /**
     * initializes the client controller
     * @param client the client
     * @param stage the stage
     * @author Florian, Maximilian, Hasan, Felix, Franzi
     */
    public void init(Client client, Stage stage) {
        this.clientHandMap = new HashMap<>();
        this.indexToCounterMap = new HashMap<>();
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
        Node connectButtonNode = dialog.getDialogPane().lookupButton(okButtonType);
        connectButton = (connectButtonNode instanceof Button) ? (Button) connectButtonNode : null;
        if (connectButton != null) {
            connectButton.getStyleClass().add("connect-button");
            connectButton.setDisable(true);
        }

        updateOkButtonState(client, dialog, usernameTextField, selectedRobotNumber, okButtonType);

        stage.getScene().getRoot().getStyleClass().add("dialog-background");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.add(connectButton, 1, 3, 1, 1);
        GridPane.setHalignment(connectButton, HPos.CENTER);
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
        introImage.setFitHeight(118);
        introImage.setFitWidth(645);
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
                if (imageView != currentSelectedImageView) {
                    imageView.setDisable(true);
                    imageView.setOpacity(0.4);
                } else {
                    currentSelectedImageView.setOpacity(1.0);
                    currentSelectedImageView = null;
                    updateOkButtonState(client, dialog, usernameTextField, selectedRobotNumber, okButtonType);
                }
            } else {
                final int robotNumber = i;
                imageView.setOnMouseClicked(event -> {
                    int newSelectedRobotNumber = 0;
                    if (currentSelectedImageView == imageView) {
                        // Deselect
                        currentSelectedImageView.setOpacity(1.0);
                        currentSelectedImageView = null;
                    } else {
                        // Select
                        if (currentSelectedImageView != null) {
                            if (newTakenFigures == null) {
                                currentSelectedImageView.setOpacity(1.0);
                            } else if (!newTakenFigures.contains(GridPane.getColumnIndex(currentSelectedImageView) + 1)) {
                                currentSelectedImageView.setOpacity(1.0);
                            }
                        }
                        imageView.setOpacity(0.5);
                        newSelectedRobotNumber = robotNumber;
                        currentSelectedImageView = imageView;
                    }
                    avatarImageView.setImage(image);
                    avatarImageView.setVisible(true);
                    avatarNameLabel.setText(robotNames[robotNumber-1]);
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(10.0);
                    dropShadow.setOffsetX(3.0);
                    dropShadow.setOffsetY(3.0);
                    dropShadow.setColor(Color.BLACK);

                    avatarNameLabel.setEffect(dropShadow);
                    avatarImageView.setEffect(dropShadow);
                    selectedRobotNumber[0] = newSelectedRobotNumber;
                    ClientLogger.writeToClientLog("Selected Robot Number: " + selectedRobotNumber[0]);
                    updateOkButtonState(client, dialog, usernameTextField, selectedRobotNumber, okButtonType);
                });
            }
        }


        grid.add(robotSelectionGrid, 0, 2, 2, 1);

        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateOkButtonState(client, dialog, usernameTextField, selectedRobotNumber, okButtonType);
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
                ClientController.this.newTakenFigures = newTakenFigures;

                Platform.runLater(() -> updateRobotImageViews(newTakenFigures));

                if (currentSelectedImageView != null && robotSelectionGrid.getChildren().contains(currentSelectedImageView)) {
                    if (newTakenFigures == null || newTakenFigures.contains(GridPane.getColumnIndex(currentSelectedImageView) + 1)) {
                        currentSelectedImageView = null;
                        updateOkButtonState(client, dialog, usernameTextField, selectedRobotNumber, okButtonType);
                    }
                }
            }
        });


        Optional<Pair<String, Integer>> result = dialog.showAndWait();

        result.ifPresent(usernameRobotPair -> {
            this.name = usernameRobotPair.getKey();
            this.figure = usernameRobotPair.getValue();
            stage.setTitle("Client - " + name);

            confirmedClients++;

            if (confirmedClients == 2 && connectButton != null) {
                connectButton.setDisable(false);
            }

            stage.getScene().getRoot().setStyle("-fx-background-image: url('/boardElementsPNGs/Custom/Backgrounds/Background1Edited.png');" +
                    "-fx-background-repeat: repeat;" +
                    "-fx-background-size: cover;");

            muteButton.getStyleClass().add("mute-button");
            muteButton.setOnAction(event -> SoundManager.toggleSoundMute());

            sendButton.setOnAction(event -> sendMessage());

            messageField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    event.consume();
                    sendMessage();
                }
            });

            uiSoundSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double volume = newValue.doubleValue() / 100.0;
                SoundManager.setUISoundVolume(volume);
            });

            eventSoundSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double volume = newValue.doubleValue() / 100.0;
                SoundManager.setEventSoundVolume(volume);
            });

            generalSoundSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double volume = newValue.doubleValue() / 100.0;
                SoundManager.setMusicVolume(volume);
            });

            masterVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double volume = newValue.doubleValue() / 100.0;
                SoundManager.setMasterVolume(volume);
            });
        });
    }

    /**
     * initializes the AI
     * @param clientAI the AI
     * @param stage the stage
     * @author Hasan
     */
    public void initAI(ClientAI clientAI, Stage stage) {
        figure = robotSelectionAI(ClientAI.getTakenFigures());
    }

    /**
     * updates the connect button
     * @param client the client
     * @param dialog the dialog
     * @param usernameTextField the text field for the username
     * @param selectedRobotNumber the number of the selected robot
     * @param okButtonType the connect button
     * @author Florian, Felix
     */
    private void updateOkButtonState(Client client, Dialog<Pair<String, Integer>> dialog, TextField usernameTextField, int[] selectedRobotNumber, ButtonType okButtonType) {
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);

        if (okButton != null) {
            boolean isUsernameValid = usernameTextField.getText() != null && !usernameTextField.getText().trim().isEmpty() && !Objects.equals(usernameTextField.getText(), "null");
            boolean isRobotSelected = selectedRobotNumber[0] > 0;

            boolean isRobotAvailable = false;
            if (isRobotSelected) {
                isRobotAvailable = !client.getTakenFigures().contains(selectedRobotNumber[0]);
            }

            ClientLogger.writeToClientLog("Updating OK Button State: Username Valid = " + isUsernameValid + ", Robot Selected = " + isRobotSelected + ", Robot Available = " + isRobotAvailable);

            okButton.setDisable(!(isUsernameValid && isRobotSelected && isRobotAvailable && confirmedClients < 2));
        }
    }


    /**
     * updates which robots are taken and disables the according images
     * @param newTakenFigures the figure that is now taken
     * @author Felix
     */
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

    /**
     * sends a message that is entered in the GUI
     * @author Felix, Maximilian
     */
    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            SendChat sendChatMessage = new SendChat(message, getSelectedRecipientId());
            ClientLogger.writeToClientLog("Send message to id: " + getSelectedRecipientId());

            String serializedSendChat = Serialisierer.serialize(sendChatMessage);
            Client.getWriter().println(serializedSendChat);

            messageField.clear();

            sendButton.requestFocus();
        }
    }

    /**
     * gets the selected recipient to send the message to
     * @return the selected recipient, -1 if it is a public message
     * @author Felix
     */
    private int getSelectedRecipientId() {
        if (visibilityButton.getText().equals("Privat")) {
            ClientLogger.writeToClientLog("Id selected: " + selectedRecipientId);
            return selectedRecipientId;
        } else {
            return -1;
        }
    }

    /**
     * sends ready when you press the ready button
     * @author Hasan, Franziska
     */
    @FXML
    private void sendReady() {
        if (!ready) {
            ready = true;
            readyButton.setText("NICHT BEREIT");
            readyButton.setStyle("-fx-background-color: rgba(221, 228, 0, 0.5);");

        } else {
            ready = false;
            readyButton.setText("BEREIT");
            readyButton.setStyle("-fx-background-color: #dde400;");
        }
        SetStatus setStatus = new SetStatus(ready);
        String serializedSetStatus = Serialisierer.serialize(setStatus);
        Client.getWriter().println(serializedSetStatus);

        MapSelected mapSelected = new MapSelected("");
        String serializedMapSelected = Serialisierer.serialize(mapSelected);
        Client.getWriter().println(serializedMapSelected);
    }

    /**
     * always make the AI ready
     * @author Maximilian
     */
    public void sendReadyAI() {
        ready = true;
        SetStatus setStatus = new SetStatus(ready);
        String serializedSetStatus = Serialisierer.serialize(setStatus);
        ClientAI.getWriter().println(serializedSetStatus);

        MapSelected mapSelected1 = new MapSelected("");
        String serializedMapSelected1 = Serialisierer.serialize(mapSelected1);
        ClientAI.getWriter().println(serializedMapSelected1);
    }

    /**
     * if you press the "alle" button you can choose from a list of all players who you want to send the message to
     * @author Maximilian
     */
    @FXML
    private void toggleVisibility() {
        if (visibilityButton.getText().equals("Alle")) {
            showPlayerListDialog();
        } else {
            showPlayerListDialog();
        }
    }

    /**
     * shows the player list dialog
     * @author Maximilian, Felix
     */
    private void showPlayerListDialog() {
        initializePlayerNames();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, playerNames);
        dialog.setTitle("Choose a player");

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/showPlayerListDialog.css").toExternalForm());
        dialog.getDialogPane().setGraphic(null);

        Label headerLabel = new Label("Please choose a player:");
        headerLabel.setFont(new Font("Arial", 56));
        dialog.getDialogPane().setHeader(headerLabel);
        headerLabel.getStyleClass().add("header-label");

        ButtonType sendToAllButton = new ButtonType("An Alle senden", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().add(sendToAllButton);

        Node sendToAllNode = dialog.getDialogPane().lookupButton(sendToAllButton);
        ((Button) sendToAllNode).setOnAction(event -> {
            visibilityButton.setText("Alle");
            selectedRecipientId = -1;
            dialog.close();
        });

        //choose user or cancel
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String selectedPlayerName = result.get();
            // find id by name
            int index = playerNames.indexOf(selectedPlayerName) + 1; // Index ist um 1 versetzt, weil clientIds mit 1 anfangen
            if (index != -1) {
                selectedRecipientId = index;
                visibilityButton.setText("Privat");
            }
        }
    }

    /**
     * appends something to the chat
     * @param message what to append to the chat
     * @author Maximilian Felix
     */
    public void appendToChatArea(String message) {
        Platform.runLater(() -> {
            StringBuilder formattedMessage = new StringBuilder();
            int charCount = 0;

            for (char c : message.toCharArray()) {
                formattedMessage.append(c);
                charCount++;

                if (charCount == 35) {
                    formattedMessage.append("\n");
                    charCount = 0;
                } else if (charCount >= 35 && Character.isWhitespace(c)){
                    formattedMessage.append("\n");
                    charCount = 0;
                }
            }

            chatArea.appendText(formattedMessage.toString() + "\n");
        });
    }

    /**
     * correctly shuts down the client and closes its sockets
     * @author Florian, Felix
     */
    public void shutdown() {
        try {
            if (name != null && !name.equals("null")) {
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

    /**
     * correctly shuts down the client if a game has already been started
     * @author Hasan
     */
    public void shutdown2() {
        try {
                    if (writer != null) {
                        writer.println(name + " has left the chat.");
                        writer.flush();
                        writer.close();
                    }
                    Thread.sleep(100);
                    socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * AI selects a robot
     * @param takenFigures all the figures that are already taken
     * @return the figure that the AI chooses
     * @author Maximilian, Florian
     */
    public int robotSelectionAI(ArrayList<Integer> takenFigures) {
        Random random = new Random();
        while(true){
            int robotNumber = random.nextInt(1, 7);
            //check whether player is already chosen
            if (!takenFigures.contains(robotNumber)) {
                return robotNumber;
            } else if(takenFigures.contains(1) && takenFigures.contains(2) && takenFigures.contains(3) && takenFigures.contains(4) &&
                    takenFigures.contains(5) && takenFigures.contains(6)) {
                return -1;
            }
        }
    }

    /**
     * initializes the player names
     * @author Felix, Hasan
     */
    private void initializePlayerNames() {
        playerNames.clear();
        for (Player player : Client.getPlayerListClient()) {
            String playerName = player.getName();
            playerNames.add(playerName);
        }
    }

    /**
     * shows the select map dialog and waits for user input
     * @return the selected map
     * @author Hasan, Franziska, Felix
     */
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

    /**
     * shows the select reboot direction dialog
     * @param stage the stage
     * @return the direction chosen by the user id no direction was chosen "top" gets returned as default
     * @author Felix, Maximilian, Florian, Franziska
     */
    public String showSelectRebootDirectionDialog(Stage stage) {
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        String[] selectedDirection = {null};

        double minWidth = new Text("bottom").getBoundsInLocal().getWidth() + 20;

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
        scene.getRoot().setStyle("-fx-background-image: url('/boardElementsPNGs/Custom/Backgrounds/Background1Edited.png');" +
                "-fx-background-repeat: repeat;" +
                "-fx-background-size: cover;");
        stage.setScene(scene);
        stage.setTitle("Your Reboot direction");

        Text text = new Text("Your Reboot direction");
        text.getStyleClass().add("header-label");
        double titleWidth = text.getBoundsInLocal().getWidth();
        stage.setWidth(titleWidth + 100);

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

    /**
     * shows the select damage dialog and waits for user input
     * @param availableList the options from which the user can choose the damage
     * @return the chosen damage
     * @author Hasan, Franziska
     */
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

    /**
     * loads the FXML of the map Dizzy Highway
     * @param client the client
     * @param primaryStage the client's primary stage
     * @author Felix, Florian
     */
    public void loadDizzyHighwayFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                Node dizzyHighway = loader.load();

                DizzyHighwayController dizzyHighwayController = loader.getController();
                mapController = dizzyHighwayController;

                mapController.init(client, primaryStage);
                mapController.setRootVBox(DizzyHighwayMap);

                DizzyHighwayMap.getChildren().setAll(dizzyHighway);
                DizzyHighwayMap.setVisible(true);
                DizzyHighwayMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loads the Dizzy Highway FXML for the AI
     * @param clientAI the AI
     * @param primaryStage the primary stage
     * @author Felix, Florian
     */
    public void loadDizzyHighwayFXMLAI(ClientAI clientAI, Stage primaryStage){
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
                Node dizzyHighway = loader.load();

                DizzyHighwayController dizzyHighwayController = loader.getController();

                mapController = dizzyHighwayController;

                dizzyHighwayController.initAI(clientAI, primaryStage);
                dizzyHighwayController.setRootVBox(DizzyHighwayMap);

                DizzyHighwayMap.getChildren().setAll(dizzyHighway);
                DizzyHighwayMap.setVisible(true);
                DizzyHighwayMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loads the Extra Crispy FXML
     * @param client the client
     * @param primaryStage the primary stage
     * @author Felix, Florian
     */
    public void loadExtraCrispyFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/ExtraCrispy.fxml"));
                Node extraCrispy = loader.load();

                ExtraCrispyController extraCrispyController = loader.getController();

                mapController = extraCrispyController;

                extraCrispyController.init(client, primaryStage);
                extraCrispyController.setRootVBox(ExtraCrispyMap);

                ExtraCrispyMap.getChildren().setAll(extraCrispy);
                ExtraCrispyMap.setVisible(true);
                ExtraCrispyMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loads the Extra Crispy FXML for the AI
     * @param clientAI the AI
     * @param primaryStage the primary stage
     * @author Felix, Florian
     */
    public void loadExtraCrispyFXMLAI(ClientAI clientAI, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/ExtraCrispy.fxml"));
                Node extraCrispy = loader.load();

                ExtraCrispyController extraCrispyController = loader.getController();

                mapController = extraCrispyController;

                extraCrispyController.initAI(clientAI, primaryStage);
                extraCrispyController.setRootVBox(ExtraCrispyMap);

                ExtraCrispyMap.getChildren().setAll(extraCrispy);
                ExtraCrispyMap.setVisible(true);
                ExtraCrispyMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * load the Lost Bearings FXML
     * @param client the client
     * @param primaryStage the primary stage
     * @author Florian, Felix
     */
    public void loadLostBearingsFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/LostBearings.fxml"));
                Node lostBearings = loader.load();

                LostBearingsController lostBearingsController = loader.getController();

                mapController = lostBearingsController;

                lostBearingsController.init(client, primaryStage);
                lostBearingsController.setRootVBox(LostBearingsMap);

                LostBearingsMap.getChildren().setAll(lostBearings);
                LostBearingsMap.setVisible(true);
                LostBearingsMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * load the Lost Bearings FXML for the AI
     * @param clientAI the AI
     * @param primaryStage the primary stage
     * @author Florian, Felix
     */
    public void loadLostBearingsFXMLAI(ClientAI clientAI, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/LostBearings.fxml"));
                Node lostBearings = loader.load();

                LostBearingsController lostBearingsController = loader.getController();

                mapController = lostBearingsController;

                lostBearingsController.initAI(clientAI, primaryStage);
                lostBearingsController.setRootVBox(LostBearingsMap);

                LostBearingsMap.getChildren().setAll(lostBearings);
                LostBearingsMap.setVisible(true);
                LostBearingsMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loads the Death Trap FXML
     * @param client the client
     * @param primaryStage the primary stage
     * @author Florian, Felix
     */
    public void loadDeathTrapFXML(Client client, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DeathTrap.fxml"));
                Node deathTrap = loader.load();

                DeathTrapController deathTrapController = loader.getController();

                mapController = deathTrapController;

                deathTrapController.init(client, primaryStage);
                deathTrapController.setRootVBox(DeathTrapMap);

                DeathTrapMap.getChildren().setAll(deathTrap);
                DeathTrapMap.setVisible(true);
                DeathTrapMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loads the Death Trap FXML for the AI
     * @param clientAI the AI
     * @param primaryStage the primary stage
     * @author Florian, Felix
     */
    public void loadDeathTrapFXMLAI(ClientAI clientAI, Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DeathTrap.fxml"));
                Node deathTrap = loader.load();

                DeathTrapController deathTrapController = loader.getController();

                mapController = deathTrapController;

                deathTrapController.initAI(clientAI, primaryStage);
                deathTrapController.setRootVBox(DeathTrapMap);

                DeathTrapMap.getChildren().setAll(deathTrap);
                DeathTrapMap.setVisible(true);
                DeathTrapMap.setManaged(true);

                //Hide ready not ready button
                readyButton.setVisible(false);
                readyButton.setManaged(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * adds the taken staring points to the field takenStartPoints
     * @param x the x coordinate of the taken starting point
     * @param y the y coordinate of the taken starting point
     * @author Felix, Hasan
     */
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

    /**
     * adds the taken staring points to the field takenStartPoints for Death Trap
     * @param x the x coordinate of the taken starting point
     * @param y the y coordinate of the taken starting point
     * @author Maximilian, Hasan
     */
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

    /**
     * sets the selected starting points
     * @author Felix, Maximilian, Hasan
     */
    public void setStartingPoint() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/CSSFiles/setStartingPoint.css").toExternalForm());
        dialog.setTitle("Startingpoint Selection");

        Label headerLabel = new Label("Please select a Startingpoint:");
        headerLabel.setFont(new Font("Arial", 56));
        dialog.getDialogPane().setHeader(headerLabel);
        headerLabel.getStyleClass().add("header-label");

        ButtonType button1 = new ButtonType("Start 1", ButtonBar.ButtonData.OK_DONE);
        ButtonType button2 = new ButtonType("Start 2", ButtonBar.ButtonData.OK_DONE);
        ButtonType button3 = new ButtonType("Start 3", ButtonBar.ButtonData.OK_DONE);
        ButtonType button4 = new ButtonType("Start 4", ButtonBar.ButtonData.OK_DONE);
        ButtonType button5 = new ButtonType("Start 5", ButtonBar.ButtonData.OK_DONE);
        ButtonType button6 = new ButtonType("Start 6", ButtonBar.ButtonData.OK_DONE);

        // Map um button types mit integer zu verknüpfen
        HashMap<ButtonType, Integer> buttonMap = new HashMap<>();
        buttonMap.put(button1, 1);
        buttonMap.put(button2, 2);
        buttonMap.put(button3, 3);
        buttonMap.put(button4, 4);
        buttonMap.put(button5, 5);
        buttonMap.put(button6, 6);

        dialog.getDialogPane().getButtonTypes().setAll(button1, button2, button3, button4, button5, button6);

        // disable of already selected buttons
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
        Optional<Integer> result = dialog.showAndWait();
        // take input and return: selected robot
        if (result.isPresent()) {
            int selectedStartingpoint = buttonMap.get(result.get());
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
                setStartingPointXYDeathTrap(selectedStartingpoint); //start board facing the other way
            } else {
                setStartingPointXY(selectedStartingpoint);
            }
        }
    }

    /**
     * chooses a starting point for the AI that is still available
     * @author Maximilian
     */
    public void setStartingPointAI() {
        ArrayList<Integer> availableStartingPoints = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            if (!takenStartPoints.contains(i)) {
                availableStartingPoints.add(i);
            }
        }

        if (availableStartingPoints.isEmpty()) {
            return;
        }

        //random selection of a starting point by the AI
        Random random = new Random();
        int selectedStartingPoint = availableStartingPoints.get(random.nextInt(availableStartingPoints.size()));

        if(ClientAI.getSelectedMap1().equals("Death Trap")) {
            setStartingPointXYDeathTrap(selectedStartingPoint);
        } else {
            setStartingPointXY(selectedStartingPoint);
        }
    }

    /**
     * sets the starting points x and y coordinate depending on which one was chosen
     * @param StartingPointNumber the number of the chosen starting point
     * @author Felix
     */
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

    /**
     * sets the starting points x and y coordinate depending on which one was chosen for the map Death Trap
     * @param StartingPointNumber the number of the chosen starting point
     * @author Maximilian
     */
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

    /**
     * puts the avatar onto the map
     * @param player the player
     * @param x x coordinate where to put the avatar
     * @param y y coordinate where to put the avatar
     * @author Felix
     */
    public void putAvatarDown(Player player, int x, int y){
        mapController.avatarAppear(player, x, y);
    }

    /**
     * when a movement was played
     * @param clientIdToMove the id of the client that is moving
     * @param newX the x coordinate after the movement
     * @param newY the y coordinate after the movement
     * @author Felix
     */
    public void movementPlayed(int clientIdToMove, int newX, int newY) {
        mapController.movementPlayed(clientIdToMove, newX, newY);
    }

    /**
     * when a player is turning
     * @param clientIdToTurn the id of the client that is turning
     * @param rotation which way the player is turning
     * @author Felix
     */
    public void playerTurn(int clientIdToTurn, String rotation){
        mapController.playerTurn(clientIdToTurn, rotation);
    }

    /**
     * sets the image of the checkpoint
     * @param imageUrl the image to set the checkpoint to
     * @author Maximilian
     */
    public void setCheckPointImage(String imageUrl) {
        Image image = new Image(imageUrl);
        checkPointImageView.setImage(image);
    }

    /**
     * plays UI sounds
     * @param eventName the name of the sound
     * @author Felix, Florian
     */
    public void playUISound(String eventName){
        SoundManager.playUISound(eventName);
    }

    /**
     * plays event sound
     * @param eventName the name of the sound
     * @author Felix, Florian
     */
    public void playEventSound(String eventName){
        SoundManager.playEventSound(eventName);
    }

    /**
     * plays sounds
     * @param soundName the name of the sound
     * @author Felix, Florian
     */
    public void playSound(String soundName){
        SoundManager.playMusic(soundName);
    }

    /**
     * initializes the draw pile
     * @author Maximilian
     */
    public void initializeDrawPile() {
        if (clientHandMap.containsKey(id)) {
            clientHandMap.remove(id);
        }

        clientHandMap.put(id, new ArrayList<>(clientHand));
        List<Card> drawPileClient = clientHandMap.get(id);
        if (!drawPileClient.isEmpty()) {
            if (totalHand != null) {
                for (int i = 0; i < 9; i++) {
                    ImageView imageView = (ImageView) totalHand.getChildren().get(i);

                    if (imageView != null) {
                        if (!drawPileClient.isEmpty()) {
                            Card topCard = drawPileClient.get(i);
                            javafx.scene.image.Image cardImage = new Image(topCard.getImageUrl());
                            imageView.setImage(cardImage);
                            imageView.setVisible(true);
                            imageView.setManaged(true);
                        } else {
                            imageView.setImage(null);
                            imageView.setVisible(false);
                            imageView.setManaged(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * initializes the register
     * @author Maximilian, Hasan
     */
    public void initializeRegister() {
        zahlen.clear();
        counter1.set(0);
        if (clientHandMap.containsKey(id)) {
            clientHandMap.remove(id);
        }
        clientHandMap.put(id, new ArrayList<>(clientHand));
        List<Card> drawPileClient = clientHandMap.get(id);

        if (!drawPileClient.isEmpty()) {
            if (totalHand != null && totalRegister != null) {
                for (int i = 0; i < 9; i++) {
                    ImageView handImageView = (ImageView) totalHand.getChildren().get(i);

                    if (handImageView != null) {
                        final int index = i;
                        handImageView.setOnMouseClicked(mouseEvent -> {

                            if (counter1.get() < 5) {
                                ImageView registerImageView = (ImageView) totalRegister.getChildren().get(counter1.get());
                                if(!(drawPileClient.get(index).getName().equals("Again") && counter1.get() == 0)) {
                                    SoundManager.playUISound("CardChosen");

                                    Image cardImage = new Image(drawPileClient.get(index).getImageUrl());
                                    registerImageView.setImage(cardImage);

                                    registerImageView.setVisible(true);
                                    registerImageView.setManaged(true);

                                    handImageView.setVisible(false);

                                    SelectedCard selectedCard = new SelectedCard(clientHand.get(index).getName(), counter1.get() + 1);
                                    String serializedCardSelected = Serialisierer.serialize(selectedCard);
                                    Client.getWriter().println(serializedCardSelected);

                                    zahlen.add(new ClientController.Zahlen(index, counter1.get()));
                                    indexToCounterMap.put(index, counter1.get());

                                    int smallestEmptyRegisterIndex = findSmallestEmptyRegisterIndex(totalRegister);
                                    counter1.set(smallestEmptyRegisterIndex);
                                    if(counter1.get() == 5){
                                        TimerStarted timerStarted = new TimerStarted();
                                        String serializedTimerStarted = Serialisierer.serialize(timerStarted);
                                        Client.getWriter().println(serializedTimerStarted);
                                    }
                                }
                            } else {
                                ClientLogger.writeToClientLog("Register full");

                            }
                        });
                        //}
                    }
                }
                for (int i = 0; i < 5; i++) {
                    ImageView registerImageView = (ImageView) totalRegister.getChildren().get(i);

                    if (registerImageView != null) {
                        final int registerIndex = i;

                        registerImageView.setOnMouseClicked(mouseEvent -> {
                            if (registerImageView.getImage() != null) {
                                if (counter1.get() < 5) {
                                    int indexNew = mapRegisterIndexToHandIndex(registerIndex);
                                    counter1.decrementAndGet();

                                    if (indexNew < 9) {
                                        SoundManager.playUISound("card put back");

                                        ImageView handImageView = (ImageView) totalHand.getChildren().get(indexNew);
                                        handImageView.setVisible(true);

                                        registerImageView.setImage(null);

                                        int smallestEmptyRegisterIndex = findSmallestEmptyRegisterIndex(totalRegister);
                                        counter1.set(smallestEmptyRegisterIndex);

                                        SelectedCard selectedCard = new SelectedCard(null, registerIndex+1);
                                        String serializedCardSelected = Serialisierer.serialize(selectedCard);
                                        Client.getWriter().println(serializedCardSelected);
                                    } else {
                                        ClientLogger.writeToClientLog("Hand full");
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * fill the empty register
     * @param nextCards the next available cards
     * @author Maximilian, Hasan
     */
    public void fillEmptyRegister(ArrayList<Card> nextCards) {
        int index = 0;
        int emptyIndex;
        while (index < nextCards.size()) {
            emptyIndex = findSmallestEmptyRegisterIndex(totalRegister);
            ImageView registerImageView = (ImageView) totalRegister.getChildren().get(emptyIndex);

            Image cardImage = new Image(nextCards.get(index).getImageUrl());
            registerImageView.setImage(cardImage);

            registerImageView.setVisible(true);
            registerImageView.setManaged(true);
            index++;
        }
    }

    /**
     * find the smallest empty register index
     * @param totalRegister the total register
     * @return the register's index
     * @author Maximilian, Hasan
     */
    private int findSmallestEmptyRegisterIndex(HBox totalRegister) {
        for (int i = 0; i < 5; i++) {
            ImageView registerImageView = (ImageView) totalRegister.getChildren().get(i);
            if (registerImageView.getImage() == null) {
                return i;
            }
        }
        return 5;
    }

    /**
     * maps the register index to the hand index
     * @param registerIndex the register's index
     * @return the hand index
     * @author Maximilian, Hasan
     */
    private int mapRegisterIndexToHandIndex(int registerIndex) {
        int storedInt;
        for (int i = 0; i < zahlen.size(); i++) {
            if (zahlen.get(i).register == registerIndex) {
                storedInt = zahlen.get(i).hand;
                zahlen.remove(i);
                return storedInt;
            }
        }
        return -1;
    }

    /**
     * sets the register visibility false
     * @author Maximlian, Hasan
     */
    public void setRegisterVisibilityFalse() {
        if (totalRegister != null) {
            for (int i = 0; i < 5; i++) {
                ImageView registerImageView = (ImageView) totalRegister.getChildren().get(i);
                if (registerImageView != null) {
                    registerImageView.setImage(null);
                }
            }
        }
    }

    /**
     * sets counter1
     * @param counter the counter
     * @author Hasan
     */
    public void setCounter1(int counter) {
        counter1.set(counter);
    }

    /**
     * for the relationship between the hand and the register index
     * @author Hasan
     */
    class Zahlen {
        public int hand;
        public int register;

        Zahlen(int hand, int register){
            this.hand = hand;
            this.register = register;
        }
    }

    /**
     * updates the countdown image
     * @param seconds at what second you are
     * @author Maximilian
     */
    public void updateCountdownImage(int seconds) {
        String imageName;
        if (seconds <= 0) {
            imageName = "/boardElementsPNGs/countDown/countdown_0.png";
        } else {
            imageName = "/boardElementsPNGs/countDown/countdown_" + seconds + ".png";
        }
        Image image = new Image(imageName);
        countDownImageView.setImage(image);
    }
}