package com.mellowpixels;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PasswBankGUI extends Application {

    public static Scene loginScene, mainScene;
    public static Stage window;


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;

        Parent loginRoot = FXMLLoader.load(getClass().getResource("login_layout.fxml"));
        Parent mainRoot = FXMLLoader.load(getClass().getResource("main_layout.fxml"));

        loginScene = new Scene(loginRoot, 700, 700);
        mainScene = new Scene(mainRoot, 700, 700);

        loginScene.getStylesheets().add("style.css");
        mainScene.getStylesheets().add("style.css");

        window.setResizable(false);
        window.setTitle("Ethereum Passwords Bank");
        window.setScene(loginScene);
        window.show();
//        primaryStage.
    }
}
