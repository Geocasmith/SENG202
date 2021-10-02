package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopupWindow {

    /**
     * Displays a popup box with a message and an OK button
     * @param title A title to display on the popup
     * @param message A message to display on the popup
     */
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

    /**
     * Displays a popup box with two buttons and a message, populated with the given data, returns true or false
     * depending on which button the user clicks
     * @param title A title to display on the popup
     * @param message A message to display on the popup
     * @param button1Text Text to display on the first button
     * @param button2Text Text to display on the second button
     * @param nullOnExit if true program will return null if the window is closed
     * @return A Boolean depending on which button the user presses, true for button 1, false for button 2
     */
    public static Boolean displayTwoButtonPopup(String title, String message, String button1Text, String button2Text, boolean nullOnExit) {
        Stage popupWindow = new Stage();

        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setTitle(title);
        popupWindow.setMinWidth(300);
        popupWindow.setMinHeight(100);

        Label label = new Label();
        label.setText(message);

        final Boolean[] returnValue = {null};

        if (!nullOnExit) {
            returnValue[0] = false;
        }

        Button button1 = new Button(button1Text);
        button1.setOnAction(e -> {
            popupWindow.close();
            returnValue[0] = true;
        });

        Button button2 = new Button(button2Text);
        button2.setOnAction(e -> {
            popupWindow.close();
            returnValue[0] = false;
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(label, button1, button2);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        popupWindow.setScene(scene);
        popupWindow.showAndWait();
        return returnValue[0];
    }

    /**
     * Displays a popup box with default return value as false
     * @param title A title to display on the popup
     * @param message A message to display on the popup
     * @param button1Text Text to display on the first button
     * @param button2Text Text to display on the second button
     * @return A Boolean depending on which button the user presses, true for button 1, false for button 2
     */
    public static Boolean displayTwoButtonPopup(String title, String message, String button1Text, String button2Text) {
        return displayTwoButtonPopup(title, message, button1Text, button2Text, false);
    }
}
