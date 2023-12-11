package SEPee.client.viewModel.MapController;

import SEPee.client.model.Client;
import SEPee.client.viewModel.ClientController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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

    @Getter
    private int startPointX;
    @Getter
    private int startPointY;
    @Getter
    @Setter
    private int currentPhase;

    private ArrayList<Integer> takenStartPoints = new ArrayList<>();

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
            setStartingPointXY(selectedStartingpoint);
        }
    }

    public void addTakenStartingPoints(int x, int y){
        int combinedValue = x*10 + y;
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
}

