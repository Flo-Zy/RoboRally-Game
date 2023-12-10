package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.client.viewModel.ClientController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import lombok.Getter;

public class DizzyHighwayController{
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
    @Getter
    public double x = -9;
    @Getter
    public double y = -9;

    public void init(Client Client, Stage stage) {
        this.stage = stage;

    }

    public void setRootVBox(VBox rootVBox) {
        this.rootVBox = rootVBox;
    }
    public void handleStartFieldClick() {
            field03b.setOnMouseClicked(event -> {
                int colIndex;
                if(GridPane.getColumnIndex(field03b) == null){
                    x = 0;
                }else{
                    x = GridPane.getColumnIndex(field03b);
                }
                int rowIndex;
                if(GridPane.getRowIndex(field03b) == null){
                    y = 0;
                }else{
                    y = GridPane.getRowIndex(field03b);
                }

            });
        }
    }

