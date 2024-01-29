package SEPee.client.viewModel.MapController;

import SEPee.client.ClientLogger;
import SEPee.client.model.Client;
import SEPee.client.model.ClientAI;
import SEPee.client.viewModel.ClientController;
import SEPee.server.model.Player;
import SEPee.server.model.Robot;
import SEPee.server.model.card.Card;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * controls all graphical changes on the map Death Trap
 * @author Maximilian, Felix
 */
public class DeathTrapController extends MapController {

    @Setter
    @FXML
    private VBox rootVBox;
    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView field00;
    @FXML
    private ImageView field02;
    @FXML
    private ImageView field04;
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
    @Getter
    static ArrayList<Card> register;

    private Map<Player, Robot> playerRobotMap;
    private Map<Robot, ImageView> robotImageViewMap;
    private AtomicInteger counter1 = new AtomicInteger(0);
    @Getter
    private final Queue<MoveInstruction> movementQueue = new LinkedList<MoveInstruction>();
    private boolean isTransitioning = false;

    /**
     * initializes the clients GUI
     * @param client the client
     * @param stage the stage
     */
    public void init(Client client, Stage stage) {
        this.stage = stage;
        playerRobotMap = new HashMap<>();
        robotImageViewMap = new HashMap<>();
    }

    /**
     * initializes the AI
     * @param clientAI the AI
     * @param stage the stage
     */
    public void initAI(ClientAI clientAI, Stage stage) {
        this.stage = stage;
        playerRobotMap = new HashMap<>();
        robotImageViewMap = new HashMap<>();
    }

    /**
     * sets the avatar visible
     * @param player the player whose avatar needs to be set visible
     * @param x x coordinate where the avatar appears
     * @param y y coordinate where the avatar appears
     */
    public void avatarAppear(Player player, int x, int y) {
        ClientLogger.writeToClientLog("Figure: " + player.getFigure());

        switch (player.getFigure()) {
            case 1:
                Robot robot1 = new Robot(x, y, "left");
                playerRobotMap.put(player, robot1);

                ImageView avatar1ImageView = Avatar1;
                robotImageViewMap.put(robot1, avatar1ImageView);

                updateAvatarPosition(robot1);

                avatar1ImageView.setVisible(true);
                avatar1ImageView.setManaged(true);
                break;
            case 2:
                Robot robot2 = new Robot(x, y, "left");
                playerRobotMap.put(player, robot2);

                ImageView avatar2ImageView = Avatar2;
                robotImageViewMap.put(robot2, avatar2ImageView);
                updateAvatarPosition(robot2);

                avatar2ImageView.setVisible(true);
                avatar2ImageView.setManaged(true);
                break;

            case 3:
                Robot robot3 = new Robot(x, y, "left");
                playerRobotMap.put(player, robot3);

                ImageView avatar3ImageView = Avatar3;
                robotImageViewMap.put(robot3, avatar3ImageView);
                updateAvatarPosition(robot3);

                avatar3ImageView.setVisible(true);
                avatar3ImageView.setManaged(true);
                break;

            case 4:
                Robot robot4 = new Robot(x, y, "left");
                playerRobotMap.put(player, robot4);

                ImageView avatar4ImageView = Avatar4;
                robotImageViewMap.put(robot4, avatar4ImageView);
                updateAvatarPosition(robot4);

                avatar4ImageView.setVisible(true);
                avatar4ImageView.setManaged(true);
                break;

            case 5:
                Robot robot5 = new Robot(x, y, "left");
                playerRobotMap.put(player, robot5);

                ImageView avatar5ImageView = Avatar5;
                robotImageViewMap.put(robot5, avatar5ImageView);
                updateAvatarPosition(robot5);

                avatar5ImageView.setVisible(true);
                avatar5ImageView.setManaged(true);
                break;

            case 6:
                Robot robot6 = new Robot(x, y, "left");
                playerRobotMap.put(player, robot6);

                ImageView avatar6ImageView = Avatar6;
                robotImageViewMap.put(robot6, avatar6ImageView);
                updateAvatarPosition(robot6);

                avatar6ImageView.setVisible(true);
                avatar6ImageView.setManaged(true);
                break;

            default:
                ClientLogger.writeToClientLog("Robot not found.");
                break;
        }
    }

    /**
     * updates the position of the player's robot
     * @param robot the robot whose avatar needs to be moved
     */
    private void updateAvatarPosition(Robot robot) {
        ImageView imageView = robotImageViewMap.get(robot);
        GridPane.setColumnIndex(imageView, robot.getX());
        GridPane.setRowIndex(imageView, robot.getY());

    }

    /**
     * rotates the image of the player's avatar
     * @param clientIdToTurn id of the player whose robot needs to be rotated
     * @param rotation clockwise or counterclockwise
     */
    public synchronized void playerTurn(int clientIdToTurn, String rotation) {
        movementQueue.offer(new MoveInstruction(clientIdToTurn, rotation));

        if (!isTransitioning && movementQueue.size() == 1) {
            processQueue();
        }
    }

    /**
     * for movement animation
     * @param clientId id of the client whose avatar needs to be moved
     * @param newX the x coordinate after the movement
     * @param newY the y coordinate after the movement
     */
    public synchronized void movementPlayed(int clientId, int newX, int newY) {
        movementQueue.offer(new MoveInstruction(clientId, newX, newY));

        if (!isTransitioning && movementQueue.size() == 1) {
            processQueue();
        }
    }

    /**
     * queue for the animations
     */
    private void processQueue() {
        if (movementQueue.isEmpty() || isTransitioning) {
            return;
        }

        isTransitioning = true;

        MoveInstruction instruction = movementQueue.peek();

        if (instruction.rotation != null) {
            processPlayerTurn(instruction);
        } else {
            processMovement(instruction);
        }
    }

    /**
     * processes the animation of a turning player
     * @param instruction what kind of movement
     */
    private void processPlayerTurn(MoveInstruction instruction) {
        Player player = getPlayerById(instruction.clientId);

        Robot robot = playerRobotMap.get(player);
        ImageView imageView = robotImageViewMap.get(robot);

        double currentRotation = imageView.getRotate();
        double rotationAmount = 90.0;

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(750), imageView);
        rotateTransition.setToAngle(instruction.rotation.equals("clockwise") ?
                currentRotation + rotationAmount : currentRotation - rotationAmount);

        rotateTransition.setOnFinished(event -> {
            movementQueue.poll();
            isTransitioning = false;
            processQueue();
        });

        rotateTransition.play();
    }

    /**
     * processes the movements
     * @param instruction what kind of movement
     */
    private void processMovement(MoveInstruction instruction) {
        Player player = getPlayerById(instruction.clientId);

        Robot robot = playerRobotMap.get(player);
        ImageView imageView = robotImageViewMap.get(robot);

        int currentX = GridPane.getColumnIndex(imageView);
        int currentY = GridPane.getRowIndex(imageView);

        double translationX = (instruction.newX - currentX) * imageView.getBoundsInParent().getWidth();
        double translationY = (instruction.newY - currentY) * imageView.getBoundsInParent().getHeight();

        TranslateTransition transition = new TranslateTransition(Duration.millis(750), imageView);
        transition.setByX(translationX);
        transition.setByY(translationY);

        transition.setOnFinished(event -> {
            GridPane.setColumnIndex(imageView, instruction.newX);
            GridPane.setRowIndex(imageView, instruction.newY);
            imageView.setTranslateX(0);
            imageView.setTranslateY(0);

            movementQueue.poll();
            isTransitioning = false;
            processQueue();
        });

        transition.play();
    }

    /**
     * returns the player that matches the id
     * @param clientId the id you want to match a player to
     * @return the player object that matches the id
     */
    private Player getPlayerById(int clientId) {
        for (Player player : ClientController.getPlayerListClient()) {
            if (player.getId() == clientId) {
                return player;
            }
        }
        return null;
    }
}