package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ButtonTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        final StackPane root = new StackPane();
        primaryStage.setScene(new Scene(root, 200, 150));

        Button button = new Button();
        button.setText("Button");
        root.getChildren().add(button);

        primaryStage.show();

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Set the cursor to the wait cursor.
                root.setCursor(Cursor.WAIT);
                // Do some work.
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Set the cursor back to the default.
                root.setCursor(Cursor.DEFAULT);
            }

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

