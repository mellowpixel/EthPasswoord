package com.mellowpixels;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PasswBankGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main_layout.fxml"));
        Scene mainScene = new Scene(root, 600, 600);
        mainScene.getStylesheets().add("style.css");
        primaryStage.setTitle("Ethereum Passwords Bank");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
