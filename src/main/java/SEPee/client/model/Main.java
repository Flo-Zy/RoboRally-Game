package SEPee.client.model;

import SEPee.client.viewModel.DizzyHighwayController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Test DizzyHighway
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SEPee/client/DizzyHighway.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 650, 800);
            primaryStage.setTitle("Dizzy Highway App");
            primaryStage.setScene(scene);

            DizzyHighwayController dizzyHighwayController = loader.getController();
            primaryStage.show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
