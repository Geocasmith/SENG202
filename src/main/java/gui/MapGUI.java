package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;

public class MapGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {


        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Google Maps");
        primaryStage.setScene(new Scene(root, 1920, 1080, Color.web("#666970")));
        primaryStage.show();

    }

    static { // use system proxy settings when standalone application
        System.setProperty("java.net.useSystemProxies", "true");
    }

    public static void main(String[] args) { launch(args);}
}
