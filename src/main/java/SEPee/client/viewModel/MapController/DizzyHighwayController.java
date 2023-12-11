package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.client.viewModel.ClientController;
import SEPee.server.model.Player;
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


    public void init(Client client, Stage stage) {
        this.stage = stage;

    }


    public void robotAppear(Player player, int x, int y) {
        System.out.println("getFigure: " + player.getFigure());

        switch (player.getFigure()){

            case 1:

                GridPane.setColumnIndex(Avatar1, x);
                GridPane.setRowIndex(Avatar1, y);

                System.out.println("x: " + x + " y: " + y);



                Avatar1.setVisible(true);
                Avatar1.setManaged(true);
            break;
            default:
                System.out.println("Robot not found.");
            break;
        }

    }



}

