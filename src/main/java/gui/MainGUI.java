package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class creates the application stage and starts the application
 * @author
 * Date 09/10/2021
 */
public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Insight");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(840);
        primaryStage.setMinWidth(1020);
        primaryStage.show();
        MainController controller = loader.getController();
        controller.setMyPrimaryStage(primaryStage);


    }

    public static void main(String[] args) {
        launch(args);
    }
}
