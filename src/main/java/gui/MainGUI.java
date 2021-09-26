package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Insight");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(840);
        primaryStage.setMinWidth(1020);
        primaryStage.show();




    }

    public static void main(String[] args) {
        launch(args);
    }
}
