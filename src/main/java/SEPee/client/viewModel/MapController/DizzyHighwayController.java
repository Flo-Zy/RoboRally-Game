package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.PlayerTurning;
import SEPee.server.model.Player;
import SEPee.server.model.Robot;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.progCard.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.*;
public class DizzyHighwayController extends MapController {

    @Setter
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
    private ImageView field03a;
    @FXML
    private ImageView field03b;
    @FXML
    private ImageView field03c;
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

    @FXML
    private ImageView Avatar1;
    @FXML
    private ImageView Avatar2;
    @FXML
    public ImageView Avatar3;
    @FXML
    public ImageView Avatar4;
    @FXML
    public ImageView Avatar5;
    @FXML
    public ImageView Avatar6;
    @FXML
    public HBox totalHand;

    private Map<Player, Robot> playerRobotMap; //store player and robot
    private Map<Robot, ImageView> robotImageViewMap; // link robots and ImageViews
    private Map<Integer, List<Card>> playerDrawPileMap;

    public void init(Client client, Stage stage) {
        this.stage = stage;
        playerRobotMap = new HashMap<>();
        robotImageViewMap = new HashMap<>();
        playerDrawPileMap = new HashMap<>(); // init programmingPile
    }


    public void avatarAppear(Player player, int x, int y) {
        System.out.println("getFigure: " + player.getFigure());

        switch (player.getFigure()) {
            case 1:
                Robot robot1 = new Robot(x, y); // set coordinates for robot1
                playerRobotMap.put(player, robot1); //link player and robot

                ImageView avatar1ImageView = Avatar1; // store imageview
                robotImageViewMap.put(robot1, avatar1ImageView); // link imageview w robot

                updateAvatarPosition(robot1); //put on selectedStartPoint

                avatar1ImageView.setVisible(true); //make visible
                avatar1ImageView.setManaged(true);

                //rotateAvatar(player.getId(), "clockwise"); //wird rotiert hingestellt weil PlayerTurning erst mit karten geschieht

                break;
            case 2:
                Robot robot2 = new Robot(x, y);
                playerRobotMap.put(player, robot2);

                ImageView avatar2ImageView = Avatar2;
                robotImageViewMap.put(robot2, avatar2ImageView);
                updateAvatarPosition(robot2);

                avatar2ImageView.setVisible(true);
                avatar2ImageView.setManaged(true);

                //moveRobotTester(robot2);

                break;

            case 3:
                Robot robot3 = new Robot(x, y);
                playerRobotMap.put(player, robot3);

                ImageView avatar3ImageView = Avatar3;
                robotImageViewMap.put(robot3, avatar3ImageView);
                updateAvatarPosition(robot3);

                avatar3ImageView.setVisible(true);
                avatar3ImageView.setManaged(true);
                break;

            case 4:
                Robot robot4 = new Robot(x, y);
                playerRobotMap.put(player, robot4);

                ImageView avatar4ImageView = Avatar4;
                robotImageViewMap.put(robot4, avatar4ImageView);
                updateAvatarPosition(robot4);

                avatar4ImageView.setVisible(true);
                avatar4ImageView.setManaged(true);
                break;

            case 5:
                Robot robot5 = new Robot(x, y);
                playerRobotMap.put(player, robot5);

                ImageView avatar5ImageView = Avatar5;
                robotImageViewMap.put(robot5, avatar5ImageView);
                updateAvatarPosition(robot5);

                avatar5ImageView.setVisible(true);
                avatar5ImageView.setManaged(true);
                break;

            case 6:
                Robot robot6 = new Robot(x, y);
                playerRobotMap.put(player, robot6);

                ImageView avatar6ImageView = Avatar6;
                robotImageViewMap.put(robot6, avatar6ImageView);
                updateAvatarPosition(robot6);

                avatar6ImageView.setVisible(true);
                avatar6ImageView.setManaged(true);
                break;

            default:
                System.out.println("Robot not found.");
                break;
        }
    }

    private void updateAvatarPosition(Robot robot) {
        ImageView imageView = robotImageViewMap.get(robot);
        GridPane.setColumnIndex(imageView, robot.getX());
        GridPane.setRowIndex(imageView, robot.getY());

        // send messagetype move
    }

    private void rotateAvatar(int clientId, String rotateDirection) {
        PlayerTurning playerTurning = new PlayerTurning(clientId, rotateDirection);
        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
        Client.getWriter().println(serializedPlayerTurning);
        // send messageType PlayerTurning
    }

    //tester fur spätere kartenimplementierung:
    public void moveRobotTester(Robot robot) {
        if (robot != null) {
            // Move 1 field
            int currentX = robot.getX();
            int currentY = robot.getY();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // Move in the x-axis (for example)
            robot.setX(currentX + 1);
            updateAvatarPosition(robot); // Update the robot's position on the grid

            // Wait for one second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Move 2 fields
            robot.setX(currentX + 2); // Move 2 fields ahead from the previous position
            updateAvatarPosition(robot); // Update the robot's position on the grid

            // Wait for one second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //rotateAvatar(2, "clockwise");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            robot.setX(currentX + 3); // Move 2 fields ahead from the previous position
            updateAvatarPosition(robot); // Update the robot's position on the grid

            // Wait for one second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Repeat this process for the y-axis or any other required movements
        }
    }

    public void initializeDrawPile(int clientId, ArrayList<String> clientHand) {
        // Erstelle aus der erhaltenen ArrayList<String> clientHand eine ArrayList<Card> drawPile
        ArrayList<Card> drawPile = new ArrayList<>();

        for (String cardName : clientHand) {
            switch (cardName) {
                case "Again":
                    drawPile.add(new Again());
                    break; // Füge diese Unterbrechungspunkte hinzu, um sicherzustellen, dass nur eine Karte hinzugefügt wird
                case "BackUp":
                    drawPile.add(new BackUp());
                    break;
                case "LeftTurn":
                    drawPile.add(new LeftTurn());
                    break;
                case "Move1":
                    drawPile.add(new Move1());
                    break;
                case "Move2":
                    drawPile.add(new Move2());
                    break;
                case "Move3":
                    drawPile.add(new Move3());
                    break;
                case "PowerUp":
                    drawPile.add(new PowerUp());
                    break;
                case "RightTurn":
                    drawPile.add(new RightTurn());
                    break;
                case "UTurn":
                    drawPile.add(new UTurn());
                    break;
            }
        }

        // Überprüfe, ob der Spieler bereits in der playerDrawPile-Map vorhanden ist
        if (playerDrawPileMap.containsKey(clientId)) {
            playerDrawPileMap.remove(clientId);
        }

        // Erstelle eine Kopie der drawPile-Liste für diesen Client
        playerDrawPileMap.put(clientId, new ArrayList<>(drawPile));

        // Hole den Spieler-zugeordneten Kartenstapel (playerDrawPileMap)
        List<Card> drawPileClient = playerDrawPileMap.get(clientId);

        // Prüfe, ob der Kartenstapel nicht leer ist
        if (!drawPileClient.isEmpty()) {
            // Hole die HBox mit fx:id="totalHand"
            HBox totalHand = (HBox) rootVBox.lookup("#totalHand");

            // Prüfe, ob die HBox gefunden wurde
            if (totalHand != null) {

                // Durchlaufe die ersten 9 ImageView-Elemente in der HBox
                for (int i = 0; i < 9; i++) {
                    // Hole das i-te ImageView-Element
                    ImageView imageView = (ImageView) totalHand.getChildren().get(i);

                    // Prüfe, ob das ImageView-Element gefunden wurde
                    if (imageView != null) {
                        // Prüfe, ob es noch Karten im Kartenstapel gibt
                        if (!drawPileClient.isEmpty()) {
                            // Hole die oberste Karte vom Kartenstapel
                            Card topCard = drawPileClient.get(0);
                            // Entferne die oberste Karte vom Kartenstapel
                            drawPileClient.remove(0);

                            // Setze das Bild des ImageView-Elements mit dem Bild der Karte
                            Image cardImage = new Image(topCard.getImageUrl());
                            imageView.setImage(cardImage);

                            // Mache das ImageView-Element sichtbar
                            imageView.setVisible(true);
                            imageView.setManaged(true);
                        } else {
                            // Wenn der Kartenstapel leer ist, setze das Bild auf null und mache das ImageView-Element unsichtbar
                            imageView.setImage(null);
                            imageView.setVisible(false);
                            imageView.setManaged(false);
                        }
                    }
                }
            }
        }
    }

    public void initializeRegister(int clientId, ArrayList<String> clientHand){

    }
}




