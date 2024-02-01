package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.server.model.Player;
import SEPee.server.model.card.Card;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * parent class of all mapControllers for each game map
 * @author Florian, Hasan
 */
public class MapController {
    @FXML
    private GridPane gridPane;
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
    @Setter
    private VBox rootVBox;
    private Stage stage;
    @Getter
    static ArrayList<Card> register;
    @Setter
    @Getter
    private Map<ImageView, Point> avatarInitialPositions = new HashMap<>();
    @Getter
    private final java.util.Queue<MoveInstruction> movementQueue = new LinkedList<>();


    /**
     * initializes the clients GUI
     * @param client the client
     * @param stage the stage
     */
    public void init(Client client, Stage stage) {}

    /**
     * sets the avatar visible
     * @param player the player whose avatar needs to be set visible
     * @param x x coordinate where the avatar appears
     * @param y y coordinate where the avatar appears
     */
    public void avatarAppear(Player player, int x, int y) {}

    /**
     * for movement animation
     * @param clientId id of the client whose avatar needs to be moved
     * @param newX the x coordinate after the movement
     * @param newY the y coordinate after the movement
     */
    public void movementPlayed(int clientId, int newX, int newY){}

    /**
     * rotates the image of the player's avatar
     * @param clientIdtoTurn id of the player whose robot needs to be rotated
     * @param rotation clockwise or counterclockwise
     */
    public void playerTurn(int clientIdtoTurn, String rotation){}
}
