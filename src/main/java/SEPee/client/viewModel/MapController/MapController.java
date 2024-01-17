package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.client.viewModel.SoundManager;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SelectedCard;
import SEPee.serialisierung.messageType.TimerStarted;
import SEPee.server.model.Player;
import SEPee.server.model.Robot;
import SEPee.server.model.card.Card;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapController {
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


    public void init(Client client, Stage stage) {}

    public void avatarAppear(Player player, int x, int y) {}

    // public void initializeRegisterAI(int clientId, ArrayList<Card> clientHand){}

    public void movementPlayed(int clientId, int newX, int newY){}

    public void playerTurn(int clientIdtoTurn, String rotation){}
}
