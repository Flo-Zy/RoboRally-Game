package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.client.viewModel.ClientController;
import SEPee.server.model.Player;
import SEPee.server.model.Robot;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DizzyHighwayController extends MapController{

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

    public void init(Client client, Stage stage) {
        this.stage = stage;

    }


    public void avatarAppear (Player player, int x, int y) {
        System.out.println("getFigure: " + player.getFigure());

        switch (player.getFigure()) {
            case 1:
                Robot robot1 = new Robot(x, y);
                playerRobotMap.put(player, robot1); //store in hashmap
                updateAvatarPosition(robot1, Avatar1);



                break;
            case 2:
                GridPane.setColumnIndex(Avatar2, x);
                GridPane.setRowIndex(Avatar2, y);
                Avatar2.setVisible(true);
                Avatar2.setManaged(true);
                break;

            case 3:
                GridPane.setColumnIndex(Avatar3, x);
                GridPane.setRowIndex(Avatar3, y);
                Avatar3.setVisible(true);
                Avatar3.setManaged(true);
                break;

            case 4:
                GridPane.setColumnIndex(Avatar4, x);
                GridPane.setRowIndex(Avatar4, y);
                Avatar4.setVisible(true);
                Avatar4.setManaged(true);
                break;

            case 5:
                GridPane.setColumnIndex(Avatar5, x);
                GridPane.setRowIndex(Avatar5, y);
                Avatar5.setVisible(true);
                Avatar5.setManaged(true);
                break;

            case 6:
                GridPane.setColumnIndex(Avatar6, x);
                GridPane.setRowIndex(Avatar6, y);
                Avatar6.setVisible(true);
                Avatar6.setManaged(true);
                break;

            default:
                System.out.println("Robot not found.");
                break;
        }
    }




}

