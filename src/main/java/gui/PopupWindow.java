package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class PopupWindow {

    public static void displayPopup(String title, String message) {
        Stage popupWindow = new Stage();

        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setTitle(title);
        popupWindow.setMinWidth(300);
        popupWindow.setMinHeight(100);

        Label label = new Label();
        label.setText(message);

        Button exitButton = new Button("OK");
        exitButton.setOnAction(e -> popupWindow.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout);
        popupWindow.setScene(scene);
        popupWindow.showAndWait();


    }

    public static Boolean displayYesNoPopup(String title, String message) {
        Stage popupWindow = new Stage();

        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setTitle(title);
        popupWindow.setMinWidth(300);
        popupWindow.setMinHeight(100);

        Label label = new Label();
        label.setText(message);

        final Boolean[] returnValue = {false};

        Button yesButton = new Button("Yes");
        yesButton.setOnAction(e -> {
            popupWindow.close();
            returnValue[0] = true;
        });

        Button noButton = new Button("No");
        noButton.setOnAction(e -> {
            popupWindow.close();
            returnValue[0] = false;
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(label, yesButton, noButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        popupWindow.setScene(scene);
        popupWindow.showAndWait();
        return returnValue[0];
    }
}
